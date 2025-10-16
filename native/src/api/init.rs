use crate::{AppStorage, NativeError, PKG_VERSION};

#[uniffi::export]
pub fn rustffi_ffi_version() -> String {
    PKG_VERSION.to_string()
}

#[uniffi::export]
pub async fn rustffi_init_db(app_dir_path: &str) -> Result<(), NativeError> {
    AppStorage::init(app_dir_path)
        .await
        .map_err(|error| NativeError::InitKv(error.to_string()))?;

    Ok(())
}
