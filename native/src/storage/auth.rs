use crate::{Byte32Array, Byte64Array, Utils};

#[derive(
    Debug, PartialEq, PartialOrd, Eq, Ord, Clone, wincode::SchemaRead, wincode::SchemaWrite,
)]
pub(crate) struct SiwsAuthResult {
    pub(crate) public_key: Byte32Array,
    pub(crate) signed_message: Vec<u8>,
    pub(crate) signature: Byte64Array,
    pub(crate) signature_type: String,
    pub(crate) auth_token: String,
}

impl SiwsAuthResult {
    pub(crate) fn address(&self) -> String {
        bs58::encode(&self.public_key).into_string()
    }

    pub(crate) fn shortened_address(&self) -> String {
        Utils::shorten_base58(&bs58::encode(&self.public_key).into_string())
    }
}
