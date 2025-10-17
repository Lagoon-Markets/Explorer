use crate::{AppStorage, NativeError, NativeResult, SiwsAuthResult, Utils};

#[uniffi::export]
pub fn rustffi_siws(auth_data: SiwsFfiAuthResult) -> NativeResult<String> {
    let parsed_auth_data: SiwsAuthResult = auth_data.try_into()?;
    let shortened_address = parsed_auth_data.shortened_address();

    AppStorage::get_store()?.set_auth(parsed_auth_data)?;

    Ok(shortened_address)
}

#[uniffi::export]
pub fn rustffi_get_auth() -> NativeResult<Option<String>> {
    Ok(AppStorage::get_store()?
        .get_auth()?
        .map(|value| value.shortened_address()))
}

#[derive(uniffi::Record, Debug)]
pub struct SiwsFfiAuthResult {
    pub public_key: Vec<i8>,
    pub signed_message: Vec<i8>,
    pub signature: Vec<i8>,
    pub signature_type: String,
    pub auth_token: String,
}

impl TryFrom<SiwsFfiAuthResult> for SiwsAuthResult {
    type Error = NativeError;

    fn try_from(ffi_auth: SiwsFfiAuthResult) -> Result<Self, Self::Error> {
        let public_key = Utils::i8_vec_to_u8_32byte_array(&ffi_auth.public_key)?;
        let signed_message = Utils::i8_vec_to_vector_of_bytes(&ffi_auth.signed_message);
        let signature = Utils::i8_vec_to_u8_64byte_array(&ffi_auth.signature)?;

        Ok(Self {
            public_key,
            signed_message,
            signature,
            signature_type: ffi_auth.signature_type,
            auth_token: ffi_auth.auth_token,
        })
    }
}
