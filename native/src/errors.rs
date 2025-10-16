pub type NativeResult<T> = Result<T, NativeError>;

#[derive(thiserror::Error, uniffi::Error, Debug, PartialEq, Eq)]
pub enum NativeError {
    #[error("Unable to initalize the apps storage. Error: {0}")]
    InitKv(String),
}
