use blocking::unblock;
use rusty_x402::DiscoveryPayload;
use x402_uri::{X402UriAction, X402UriError, X402UriScheme};

use crate::{AppStorage, NativeError, NativeResult, TokenInfo};

#[uniffi::export]
pub async fn rustffi_discover_resources(
    x402_resource_uri: String,
) -> Result<Vec<DiscoveryFfi>, NativeError> {
    DiscoveryFfi::fetch(&x402_resource_uri).await
}

#[derive(Debug, uniffi::Record)]
pub struct DiscoveryFfi {
    pub uri_scheme: X402UriSchemeFfi,
    pub uri: String,
    pub title: Option<String>,
    pub description: Option<String>,
    pub header_image: Option<String>,
    pub amount: String,
    pub asset: String,
    pub pay_to: String,
    pub maxtimeout_seconds: String,
    pub fee_payer: String,
    pub asset_info: Option<TokenInfo>,
}

impl DiscoveryFfi {
    pub async fn fetch(x402_resource_uri: &str) -> NativeResult<Vec<Self>> {
        let scheme: X402UriScheme = x402_resource_uri
            .try_into()
            .map_err(|error: X402UriError| NativeError::InvalidX402Uri(error.to_string()))?;

        match scheme {
            X402UriScheme::Https => Self::fetch_https(x402_resource_uri, scheme).await,
            _ => Err(NativeError::UnsupportedX402Scheme),
        }
    }

    pub async fn fetch_https(
        x402_resource_uri: &str,
        x402_uri_scheme: X402UriScheme,
    ) -> NativeResult<Vec<Self>> {
        let owned_uri = x402_resource_uri.to_owned();
        let response = unblock(move || minreq::get(owned_uri).send())
            .await
            .map_err(|error| NativeError::Https(error.to_string()))?;

        let parse_json = serde_json::from_str::<DiscoveryPayload>(
            response
                .as_str()
                .map_err(|error| NativeError::Https(error.to_string()))?,
        )
        .map_err(|error| NativeError::Https(error.to_string()))?;

        let mut output = Vec::<Self>::new();

        parse_json.items.iter().try_for_each(|item| {
            let accepts = item
                .accepts
                .first()
                .ok_or(NativeError::AtLeastOneAcceptsItemIsNeeded)?;

            let asset_info = AppStorage::get_store()?.get_token(accepts.asset())?;

            let info = Self {
                uri_scheme: x402_uri_scheme.into(),
                uri: x402_resource_uri.to_string(),
                title: item.title.as_ref().map(|value| value.to_string()),
                description: item.description.as_ref().map(|value| value.to_string()),
                header_image: item.header_image.as_ref().map(|value| value.to_string()),
                amount: accepts.max_amount_required().to_string(),
                asset: accepts.asset().to_string(),
                pay_to: accepts.pay_to().to_string(),
                maxtimeout_seconds: accepts.max_timeout_seconds().to_string(),
                fee_payer: accepts.extra().fee_payer().to_string(),
                asset_info,
            };

            output.push(info);

            Ok::<_, NativeError>(())
        })?;

        Ok(output)
    }
}

#[derive(Debug, Clone, Copy, Hash, PartialEq, Eq, PartialOrd, Ord, Default, uniffi::Enum)]
pub enum X402UriActionFfi {
    #[default]
    Discover,
    Subscribe,
    Unsubscribe,
    Once,
}

impl From<X402UriAction> for X402UriActionFfi {
    fn from(value: X402UriAction) -> Self {
        match value {
            X402UriAction::Discover => Self::Discover,
            X402UriAction::Subscribe => Self::Subscribe,
            X402UriAction::Unsubscribe => Self::Unsubscribe,
            X402UriAction::Once => Self::Once,
        }
    }
}

#[derive(Debug, Clone, Copy, Hash, PartialEq, Eq, PartialOrd, Ord, Default, uniffi::Enum)]
pub enum X402UriSchemeFfi {
    #[default]
    Https,
    A2a,
    Mcp,
}

impl From<X402UriScheme> for X402UriSchemeFfi {
    fn from(value: X402UriScheme) -> Self {
        match value {
            X402UriScheme::A2a => Self::A2a,
            X402UriScheme::Https => Self::Https,
            X402UriScheme::Mcp => Self::Mcp,
        }
    }
}
