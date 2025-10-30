use crate::{AppStorage, NativeError, X402Data};

#[uniffi::export]
pub fn rustffi_store_x402(data: X402Data) -> Result<(), NativeError> {
    AppStorage::get_store()?.set_x402(data)
}

#[uniffi::export]
pub fn rustffi_get_x402(uri: String) -> Result<Option<X402Data>, NativeError> {
    Ok(AppStorage::get_store()?
        .get_x402(&uri)?
        .map(|value| value.data))
}

#[uniffi::export]
pub fn rustffi_get_x402_resources() -> Result<Vec<X402Data>, NativeError> {
    AppStorage::get_store()?.get_all()
}
