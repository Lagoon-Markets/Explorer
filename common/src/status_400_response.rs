use serde::{Deserialize, Serialize};

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize)]
pub struct X402400BadRequest {
    pub error: String,
    pub status: u16,
    pub resource: String,
    pub header: String,
}

impl X402400BadRequest {
    pub fn to_json(&self) -> jzon::JsonValue {
        jzon::object! {
            error: self.error.as_str(),
            status:self.status,
            header: self.header.as_str(),
            resource:self.resource.as_str()
        }
    }
}
