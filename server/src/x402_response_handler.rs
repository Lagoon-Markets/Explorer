use std::time::Duration;

use common::CommonHeaders;
use once_cell::sync::Lazy;
use rocket::http;
use rocket::request::Request;
use rocket::response::{self, Responder, Response};
use rusty_x402::PaymentRequestExtras;
use rusty_x402::X_PAYMENT_HEADER_KEY;
use rusty_x402::{PaymentRequirementsBuilder, PaymentRequirementsResponse};

use crate::{AllowedAssets, ServerConfig};

pub(crate) static SERVER_CONFIG: Lazy<ServerConfig> = Lazy::new(|| ServerConfig::parse());

#[get("/latest_newsletter")]
pub(crate) async fn latest_newsletter() -> X402HttpResponse {
    X402HttpResponse
}

pub struct X402HttpResponse;

impl<'r> Responder<'r, 'static> for X402HttpResponse {
    fn respond_to(self, req: &'r Request<'_>) -> response::Result<'static> {
        let latest_newsletter_uri = "https://inthetrenches.cloud/latest_newsletter";
        let json_body;
        let status: http::Status;

        let x_public_key: String;

        if let Some(x_public_key_inner) = req.headers().get_one(CommonHeaders::X_PUBLIC_KEY_HEADER)
        {
            x_public_key = x_public_key_inner.to_string();
        } else {
            let inner_status = http::Status::BadRequest;
            let x_public_key_json_error = jzon::object! {
                status: inner_status.code,
                error: "Bad request. The `x-Public-Key` is missing or malformed. It requires a base58 encoded Ed25519 public address",
                resource: latest_newsletter_uri
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

            json_body = construct_response(&fee_payer)
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

fn construct_response<'x>(fee_payer: &'x str) -> PaymentRequirementsResponse<'x> {
    let asset = AllowedAssets::USDC_DEVNET;
    let extra = PaymentRequestExtras::new(fee_payer)
        .set_legacy_token_mint()
        .set_decimals(asset.decimals);

    let mut requirement = PaymentRequirementsBuilder::new();
    requirement
        .set_amount(1 * 10u64.pow(6))
        .set_asset(asset.address)
        .set_description("Read the latest on Solana developer tooling.")
        .set_recipient(SERVER_CONFIG.resource_server_address())
        .set_mime_as_json()
        .set_max_timeout_seconds(Duration::from_secs(100))
        .set_resource("https://inthetrenches.cloud")
        .set_extra(extra);

    let mut body = PaymentRequirementsResponse::new();
    body.add_payment_requirement(requirement.build().unwrap());

    body
}
