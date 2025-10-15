use std::time::Duration;

use rocket::http;
use rocket::request::Request;
use rocket::response::{self, Responder, Response};
use rusty_x402::PaymentRequestExtras;
use rusty_x402::X_PAYMENT_HEADER_KEY;
use rusty_x402::{PaymentRequirementsBuilder, PaymentRequirementsResponse};

#[get("/todays_paper")]
pub(crate) async fn todays_paper() -> X402HttpResponse {
    X402HttpResponse
}

pub struct X402HttpResponse;

impl<'r> Responder<'r, 'static> for X402HttpResponse {
    fn respond_to(self, req: &'r Request<'_>) -> response::Result<'static> {
        let json_body;
        let status: http::Status;

        if let Some(x_payment_header) = req.headers().get_one(X_PAYMENT_HEADER_KEY) {
            json_body = jzon::object! {
                status: 200
            }
            .to_string();

            status = http::Status::Ok;
        } else {
            json_body = foo()
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

fn foo<'x>() -> PaymentRequirementsResponse<'x> {
    let extra = PaymentRequestExtras::new("TODO")
        .set_legacy_token_mint()
        .set_decimals(6);

    let mut requirement = PaymentRequirementsBuilder::new();
    requirement
        .set_amount(1 * 10u64.pow(6))
        .set_asset("")
        .set_description("TODO")
        .set_recipient("TODO")
        .set_mime_as_json()
        .set_max_timeout_seconds(Duration::from_secs(100))
        .set_resource("https://example.com")
        .set_extra(extra);

    let mut body = PaymentRequirementsResponse::new();
    body.add_payment_requirement(requirement.build().unwrap());

    body
}
