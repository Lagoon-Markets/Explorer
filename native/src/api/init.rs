use crate::{AppStorage, NativeError, APP_STORAGE, PKG_VERSION};

#[uniffi::export]
pub fn rustffi_ffi_version() -> String {
    PKG_VERSION.to_string()
}

#[uniffi::export]
pub async fn rustffi_init_db(app_dir_path: &str) -> Result<(), NativeError> {
    let store = AppStorage::init(app_dir_path)
        .await
        .map_err(|error| NativeError::InitKv(error.to_string()))?;

    APP_STORAGE.set(store).err();
    // .or(Err(NativeError::UnableToSetGlobalStorageObject))?;

    Ok(())
}
