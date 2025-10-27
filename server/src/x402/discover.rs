use std::{borrow::Cow, time::Duration};

use rocket::serde::json::Json;
use rusty_x402::{
    DiscoveryPayload, PayloadPagination, PaymentRequestExtras, PaymentRequirementsBuilder,
    ResourceInfo, X402Version,
};

use crate::{AllowedAssets, SERVER_CONFIG};

#[get("/discover")]
pub fn x402_discover<'x>() -> Result<Json<DiscoveryPayload<'x>>, String> {
    let mut items = Vec::<ResourceInfo>::default();

    let resource1_url = crate::NEWSLETTER_URI;
    let extras = PaymentRequestExtras::new("");
    let mut resource1_requirements = PaymentRequirementsBuilder::new();
    resource1_requirements
        .set_amount(500_000)
        .set_asset(AllowedAssets::SOL.address)
        .set_description("Pay using USDC to access latest newsletter")
        .set_max_timeout_seconds(Duration::from_secs(60 * 5))
        .set_recipient(SERVER_CONFIG.resource_server_address())
        .set_resource(resource1_url)
        .set_extra(extras)
        .set_mime_as_json();

    let resource1 = ResourceInfo {
        resource: resource1_url,
        r#type: Option::Some("http"),
        x402_version: X402Version::V1 as u8,
        accepts: Cow::Owned(vec![resource1_requirements.build().map_err(|error| {
            String::from("Error buildng resource") + error.to_string().as_str()
        })?]),
        header_image: Some("https://lagoon.markets/typewriter.jpg".into()),
        title: Some("Latest Newsletter".into()),
        description: Some(
            "Get latest insights on onchain activity and developer productivity".into(),
        ),
        last_updated: u64::default(),
        metadata: Option::default(),
    };

    let resource2_url = crate::VOTING;
    let extras2 = PaymentRequestExtras::new("");
    let mut resource2_requirements = PaymentRequirementsBuilder::new();
    resource2_requirements
        .set_amount(500_000)
        .set_asset(AllowedAssets::SOL.address)
        .set_description("View voting live updates")
        .set_max_timeout_seconds(Duration::from_secs(60 * 5))
        .set_recipient(SERVER_CONFIG.resource_server_address())
        .set_resource(resource2_url)
        .set_extra(extras2)
        .set_mime_as_json();

    let resource2 = ResourceInfo {
        resource: resource2_url,
        r#type: Option::Some("a2a"),
        x402_version: X402Version::V1 as u8,
        accepts: Cow::Owned(vec![resource2_requirements.build().map_err(|error| {
            String::from("Error buildng resource") + error.to_string().as_str()
        })?]),
        header_image: Some("https://lagoon.markets/typewriter.jpg".into()),
        title: Some("Voting Live Updates".into()),
        description: Some("View the current voting live updates from our AI agent".into()),
        last_updated: u64::default(),
        metadata: Option::default(),
    };

    items.push(resource1);
    items.push(resource2);

    let payload = DiscoveryPayload {
        x402_version: X402Version::V1 as u8,
        items: Cow::Owned(items),
        pagination: PayloadPagination::default(),
    };

    Ok(Json(payload))
}
