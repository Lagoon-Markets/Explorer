use wincode::{SchemaRead, SchemaWrite};

use crate::{AppStorage, Base58String, NativeError, NativeResult};

#[derive(Debug, PartialEq, PartialOrd, Clone, Default, SchemaRead, SchemaWrite, uniffi::Record)]
pub struct UserProfile {
    pub name: String,
    pub public_key: Base58String,
}

impl UserProfile {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn pack(&self) -> NativeResult<Vec<u8>> {
        wincode::serialize(&self).or(Err(NativeError::PackingUserProfile))
    }

    pub fn unpack(profile_bytes: &[u8]) -> NativeResult<Self> {
        wincode::deserialize(profile_bytes).or(Err(NativeError::UnpackingUserProfile))
    }

    pub async fn get_profile() -> NativeResult<Option<Self>> {
        let user_profile_bytes = AppStorage::get_store()?.get_profile().await?;

        user_profile_bytes
            .map(|data_bytes| Self::unpack(&data_bytes))
            .transpose()
    }
}
