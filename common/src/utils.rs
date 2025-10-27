use percent_encoding::{percent_decode_str, percent_encode, NON_ALPHANUMERIC};

pub struct CommonUtils;

impl CommonUtils {
    pub fn percent_encode(value: &str) -> String {
        percent_encode(value.as_bytes(), NON_ALPHANUMERIC).to_string()
    }

    pub fn percent_decode(value: &str) -> Result<String, String> {
        Ok(percent_decode_str(value)
            .decode_utf8()
            .map_err(|error| error.to_string())?
            .to_string())
    }
}
