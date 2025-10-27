use std::collections::VecDeque;
use std::time::Duration;

use common::{
    CommonHeaders, CommonUtils, EventSourceData, EventSourceProgressPoint,
    EventSourceProgressSegment, EventSourceProgressStyle, SolanaChain, X402400BadRequest,
};
use rocket::http::{self, Status};
use rocket::request::{Outcome, Request};
use rocket::response::stream::{Event, EventStream};
use rocket::response::{self, Responder, Response};
use rocket::tokio::time;
use rusty_x402::PaymentRequestExtras;
use rusty_x402::X_PAYMENT_HEADER_KEY;
use rusty_x402::{PaymentRequirementsBuilder, PaymentRequirementsResponse};

use crate::{AllowedAssets, SERVER_CONFIG};

pub const NEWSLETTER_URI: &str = "https://lagoon.markets/latest_newsletter";
pub const VOTING: &str = "https://lagoon.markets/x402/voting";

#[get("/voting")]
pub fn voting_handler() -> Result<rocket::response::stream::EventStream![Event], (Status, String)> {
    let mut events = create_test_events()
        .into_iter()
        .map(|value| serde_json::to_string(&value))
        .collect::<Result<VecDeque<String>, serde_json::Error>>()
        .or(Err((
            Status::InternalServerError,
            "Unable to serialize eventsource data".to_string(),
        )))?;

    Ok(EventStream! {
     let mut interval = time::interval(Duration::from_secs(3));
         interval.tick().await; // wait before sending the first event
          loop {

            if let Some(value) = events.pop_front() {
                yield Event::data(value).id("streaming");
            }else {
                yield Event::data("").id("done");
                return;
            }

            interval.tick().await;
        }
    })
}

#[get("/latest_newsletter")]
pub(crate) async fn latest_newsletter() -> X402HttpResponse {
    X402HttpResponse
}

pub struct X402HttpResponse;

impl<'r> Responder<'r, 'static> for X402HttpResponse {
    fn respond_to(self, req: &'r Request<'_>) -> response::Result<'static> {
        let latest_newsletter_uri = NEWSLETTER_URI;
        let json_body;
        let status: http::Status;

        let x_public_key: String;
        if let Some(x_public_key_inner) = req.headers().get_one(CommonHeaders::X402_ADDRESS_HEADER)
        {
            x_public_key = x_public_key_inner.to_string();
        } else {
            let inner_status = http::Status::BadRequest;
            let x_public_key_json_error = jzon::object! {
                status: inner_status.code,
                error: format!("Bad request. The `{}` header is missing or malformed. It requires a base58 encoded Ed25519 public address", CommonHeaders::X402_ADDRESS_HEADER),
                resource: latest_newsletter_uri,
                header: CommonHeaders::X402_CHAIN_HEADER
            }.to_string();

            return Response::build()
                .sized_body(
                    x_public_key_json_error.len(),
                    std::io::Cursor::new(x_public_key_json_error),
                )
                .header(rocket::http::ContentType::JSON)
                .status(inner_status)
                .ok();
        }

        let chain: String;
        if let Some(chain_inner) = req.headers().get_one(CommonHeaders::X402_CHAIN_HEADER) {
            chain = chain_inner.to_string();
        } else {
            let inner_status = http::Status::BadRequest;
            let x_chain_json_error = X402400BadRequest{
                status: inner_status.code,
                error: format!("Bad request. The `{}` header is missing or malformed. It requires the chain identification in x402 format", CommonHeaders::X402_CHAIN_HEADER),
                resource: latest_newsletter_uri.to_string(),
                header: CommonHeaders::X402_CHAIN_HEADER.to_string()
            }.to_json().to_string();

            return Response::build()
                .sized_body(
                    x_chain_json_error.len(),
                    std::io::Cursor::new(x_chain_json_error),
                )
                .header(rocket::http::ContentType::JSON)
                .status(inner_status)
                .ok();
        }

        if let Some(x_payment_header) = req.headers().get_one(X_PAYMENT_HEADER_KEY) {
            json_body = jzon::object! {
                status: 200
            }
            .to_string();

            status = http::Status::Ok;
        } else {
            let fee_payer = if SERVER_CONFIG.client_is_facilitator() {
                x_public_key
            } else {
                SERVER_CONFIG.facilitator_address().unwrap().to_owned()
            };

            json_body = construct_response(&fee_payer, &chain)
                .or(Err(http::Status::InternalServerError))?
                .to_json()
                .or(Err(http::Status::InternalServerError))?
                .to_string();

            status = http::Status::PaymentRequired;
        }

        Response::build()
            .sized_body(json_body.len(), std::io::Cursor::new(json_body))
            .header(rocket::http::ContentType::JSON)
            .status(status)
            .ok()
    }
}

fn construct_response<'x>(
    fee_payer: &'x str,
    chain: &str,
) -> Result<PaymentRequirementsResponse<'x>, String> {
    let asset = if chain.to_lowercase().as_bytes() == SolanaChain::DEVNET_X402_ID.as_bytes() {
        AllowedAssets::USDC_DEVNET
    } else {
        return Err("Unsupported network".to_string());
    };

    let extra = PaymentRequestExtras::new(fee_payer)
        .set_legacy_token_mint()
        .set_decimals(asset.decimals);

    let mut requirement = PaymentRequirementsBuilder::new();
    requirement
        .set_amount((0.10 * 10f64.powi(6)) as u64)
        .set_asset(asset.address)
        .set_description("Read the latest on Solana developer tooling.")
        .set_recipient(SERVER_CONFIG.resource_server_address())
        .set_mime_as_json()
        .set_max_timeout_seconds(Duration::from_secs(100))
        .set_resource(NEWSLETTER_URI)
        .set_extra(extra);

    let mut body = PaymentRequirementsResponse::new();
    body.add_payment_requirement(requirement.build().unwrap());

    Ok(body)
}

fn create_test_events() -> Vec<EventSourceData> {
    let point_color = "#FF00FFFF";
    let segment_color = "#FFFFFFFF";

    let progress_style_input = EventSourceProgressStyle {
        points: vec![
            EventSourceProgressPoint {
                point: 0,
                color: point_color.into(),
            },
            EventSourceProgressPoint {
                point: 25,
                color: point_color.into(),
            },
            EventSourceProgressPoint {
                point: 50,
                color: point_color.into(),
            },
            EventSourceProgressPoint {
                point: 75,
                color: point_color.into(),
            },
            EventSourceProgressPoint {
                point: 100,
                color: point_color.into(),
            },
        ],
        segments: vec![
            EventSourceProgressSegment {
                segment: 25,
                color: segment_color.to_string(),
            },
            EventSourceProgressSegment {
                segment: 25,
                color: segment_color.to_string(),
            },
            EventSourceProgressSegment {
                segment: 25,
                color: segment_color.to_string(),
            },
            EventSourceProgressSegment {
                segment: 25,
                color: segment_color.to_string(),
            },
        ],
    };

    let init = EventSourceData {
        content_title: "Vote open".to_string(),
        content_text: "AI agent preparing to cast their vote...".to_string(),
        short_critical_text: "Placing".to_string(),

        progress: EventSourceProgressPoint {
            point: 0,
            color: point_color.to_string(),
        },
        is_progress_indeterminate: true,
        actions: vec![],
        style: progress_style_input.clone(),
    };

    let preparing = EventSourceData {
        content_title: "Vote cast in favour".to_string(),
        content_text: "The AI agent decided in favour using HTTP/3 on AI agents DAO peer-to-peer network on double zero".to_string(),
        short_critical_text: "Vote Cast".to_string(),

        progress: EventSourceProgressPoint {
            point: 25,
            color: point_color.to_string(),
        },
        actions: vec![],
        is_progress_indeterminate: false,
        style: progress_style_input.clone(),
    };

    let en_route = EventSourceData {
        content_title: "Vote still ongoing".to_string(),
        content_text: "Other AI agents are still voting. Current vote rate at 67%.".to_string(),
        short_critical_text: "Vote ongoing".to_string(),

        progress: EventSourceProgressPoint {
            point: 50,
            color: point_color.to_string(),
        },
        actions: vec![],
        style: progress_style_input.clone(),
        is_progress_indeterminate: false,
    };

    let arriving = EventSourceData {
        content_title: "All agent votes cast".to_string(),
        content_text: "Other AI agents have finished casting their votes. Tallying...".to_string(),
        short_critical_text: "On route".to_string(),

        progress: EventSourceProgressPoint {
            point: 75,
            color: point_color.to_string(),
        },
        actions: vec!["Tip Agent".to_string(), "Got it".to_string()],

        style: progress_style_input.clone(),
        is_progress_indeterminate: false,
    };

    let delivered = EventSourceData {
        content_title: "Voting closed".to_string(),
        content_text:
            "The outcome was 98% AI agents in favour of HTTP/3 upgrade. You won this election :)"
                .to_string(),
        short_critical_text: "You won (98%)".to_string(),

        progress: EventSourceProgressPoint {
            point: 100,
            color: point_color.to_string(),
        },
        actions: vec!["View votes".to_string()],
        style: progress_style_input.clone(),
        is_progress_indeterminate: false,
    };

    vec![init, preparing, en_route, arriving, delivered]
}
