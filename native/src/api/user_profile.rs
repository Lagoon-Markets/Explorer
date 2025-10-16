use crate::{NativeError, UserProfile};

#[uniffi::export]
pub async fn rustffi_get_profile() -> Result<Option<UserProfile>, NativeError> {
    UserProfile::get_profile().await
}
