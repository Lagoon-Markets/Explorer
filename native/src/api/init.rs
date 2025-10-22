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

    AppStorage::get_store()?.load_token_list().await
}

#[derive(uniffi::Object)]
pub struct AppDetailsFfi;

#[uniffi::export]
impl AppDetailsFfi {
    #[uniffi::constructor]
    pub fn new() -> Self {
        Self
    }

    #[uniffi::method]
    pub fn sign_in_statement(&self) -> String {
        "Sign in to Lagoon.Markets Dapp".to_string()
    }

    #[uniffi::method]
    pub fn domain(&self) -> String {
        "https://lagoon.markets".to_string()
    }

    #[uniffi::method]
    pub fn identity(&self) -> String {
        "Lagoon.Markets Dapp".to_string()
    }

    #[uniffi::method]
    pub fn favicon(&self) -> String {
        "Lagoon.Markets-Favicon.png".to_string()
    }
}
