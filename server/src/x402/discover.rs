use std::{borrow::Cow, time::Duration};

use rocket::serde::json::Json;
use rusty_x402::{
    DiscoveryPayload, PayloadPagination, PaymentRequestExtras, PaymentRequirementsBuilder,
    ResourceInfo, X402Version,
};

use crate::AllowedAssets;

#[get("/discover")]
pub fn x402_discover<'x>() -> Result<Json<DiscoveryPayload<'x>>, String> {
    let mut items = Vec::<ResourceInfo>::default();

    let resource1_url = "https://lagoon.markets/latest_newsletter";
    let extras = PaymentRequestExtras::new("");
    let mut resource1_requirements = PaymentRequirementsBuilder::new();
    resource1_requirements
        .set_amount(10)
        .set_asset(AllowedAssets::USDC_DEVNET.address)
        .set_description("Pay using USDC to access latest newsletter")
        .set_max_timeout_seconds(Duration::from_secs(60 * 5))
        .set_recipient("67JmfPZkcYZm5wkzwF7csDCrNpNwWD8AiyWcLZYWmpyP")
        .set_resource(resource1_url)
        .set_extra(extras)
        .set_mime_as_json();
    let mut resource1 = ResourceInfo {
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
    items.push(resource1.clone());
    resource1.r#type.replace("a2a");
    items.push(resource1);

    let payload = DiscoveryPayload {
        x402_version: X402Version::V1 as u8,
        items: Cow::Owned(items),
        pagination: PayloadPagination::default(),
    };

    Ok(Json(payload))
}
