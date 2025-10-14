#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub struct X402Uri<'x> {
    uri_scheme: X402UriScheme,
    action: X402UriAction,
    uri: &'x str,
}

impl<'x> X402Uri<'x> {
    pub fn new(x402_uri: &'x str) -> X402UriResult<Self> {
        let scheme = "x402://";

        if !x402_uri.starts_with(scheme) {
            return Err(X402UriError::UriSchemeStartsWithX402Scheme);
        }

        let trim_scheme = x402_uri.trim_start_matches(scheme);

        if let Some((action_raw, rest)) = trim_scheme.split_once('/') {
            let action: X402UriAction = action_raw.try_into()?;

            let mut outcome = Self {
                uri_scheme: X402UriScheme::Https,
                action,
                uri: rest,
            };

            if rest.starts_with(X402UriScheme::HTTPS_SCHEME) {
                outcome.uri_scheme = X402UriScheme::Https;
            } else if rest.starts_with(X402UriScheme::A2A_SCHEME) {
                outcome.uri_scheme = X402UriScheme::A2a;
            } else if rest.starts_with(X402UriScheme::MCP_SCHEME) {
                outcome.uri_scheme = X402UriScheme::Mcp;
            } else {
                return Err(X402UriError::UnsupportedUriScheme);
            }

            Ok(outcome)
        } else {
            Err(X402UriError::ExpectedAnActionInUri)
        }
    }

    pub fn uri_scheme(&self) -> X402UriScheme {
        self.uri_scheme
    }

    pub fn action(&self) -> X402UriAction {
        self.action
    }

    pub fn uri(&self) -> &str {
        self.uri
    }
}

pub type X402UriResult<T> = Result<T, X402UriError>;

#[derive(Debug, PartialEq, Eq, thiserror::Error)]
pub enum X402UriError {
    #[error("A x402Uri must start with `x402://` ")]
    UriSchemeStartsWithX402Scheme,
    #[error("A x402Uri must have an action after `x402://`")]
    ExpectedAnActionInUri,
    /// The scheme in the URI is not part of the X402Uri specification
    #[error("The scheme in the URI is not part of the X402Uri specification")]
    InvalidX402UriScheme,
    /// The action in the URI is not part of the X402Uri specification
    #[error("The action in the URI is not part of the X402Uri specification")]
    InvalidX402UriAction,
    #[error("The URI after the action `x402://<action>/<this URI>` is not supported. Only `https://`, `a2a://` and `mcp://` are supported.")]
    UnsupportedUriScheme,
}

#[derive(Debug, Clone, Copy, Hash, PartialEq, Eq, PartialOrd, Ord, Default)]
pub enum X402UriAction {
    #[default]
    Discover,
    Subscribe,
    Unsubscribe,
    Once,
}

impl X402UriAction {
    pub const DISCOVER: &str = "discover";
    pub const SUBSCRIBE: &str = "subscribe";
    pub const UNSUBSCRIBE: &str = "unsubscribe";
    pub const ONCE: &str = "once";
}

impl TryFrom<&str> for X402UriAction {
    type Error = X402UriError;

    fn try_from(value: &str) -> Result<Self, Self::Error> {
        let parsed = match value {
            Self::DISCOVER => Self::Discover,
            Self::SUBSCRIBE => Self::Subscribe,
            Self::UNSUBSCRIBE => Self::Unsubscribe,
            Self::ONCE => Self::Once,
            _ => return Err(X402UriError::InvalidX402UriAction),
        };

        Ok(parsed)
    }
}

#[derive(Debug, Clone, Copy, Hash, PartialEq, Eq, PartialOrd, Ord, Default)]
pub enum X402UriScheme {
    #[default]
    Https,
    A2a,
    Mcp,
}

impl X402UriScheme {
    pub const HTTPS_SCHEME: &str = "https://";
    pub const A2A_SCHEME: &str = "a2a://";
    pub const MCP_SCHEME: &str = "mcp://";
}

impl TryFrom<&str> for X402UriScheme {
    type Error = X402UriError;

    fn try_from(value: &str) -> Result<Self, Self::Error> {
        let parsed = match value {
            Self::HTTPS_SCHEME => Self::Https,
            Self::A2A_SCHEME => Self::A2a,
            Self::MCP_SCHEME => Self::Mcp,
            _ => return Err(X402UriError::InvalidX402UriScheme),
        };

        Ok(parsed)
    }
}

#[cfg(test)]
mod x402_uri_sanity {
    use super::*;

    #[test]
    fn test_https() {
        let x402_uri =
            "x402://discover/https://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
        let x402_uri = "x402://subscribe/https://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
        let x402_uri = "x402://unsubscribe/https://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
    }

    #[test]
    fn test_a2a() {
        let x402_uri =
            "x402://discover/a2a://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
        let x402_uri =
            "x402://subscribe/a2a://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
        let x402_uri = "x402://unsubscribe/a2a://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
    }

    #[test]
    fn test_mcp() {
        let x402_uri =
            "x402://discover/mcp://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
        let x402_uri =
            "x402://subscribe/mcp://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
        let x402_uri = "x402://unsubscribe/mcp://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_ok());
    }

    #[test]
    fn test_error() {
        let x402_uri =
            "x402://discover/foo://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_err());
        let x402_uri =
            "x402://subscribe/foo://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_err());
        let x402_uri = "x402://unsubscribe/foo://example.com/apiv1/So11111111111111111111111111111111111111112";
        assert!(X402Uri::new(x402_uri).is_err());
    }
}
