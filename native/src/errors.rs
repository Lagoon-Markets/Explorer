pub type NativeResult<T> = Result<T, NativeError>;

#[derive(thiserror::Error, uniffi::Error, Debug, PartialEq, Eq)]
pub enum NativeError {
    #[error("Unable to initalize the apps storage. Error: {0}")]
    InitKv(String),
    #[error("The vector of `i8` from FFI should be at least 32 bytes long")]
    VectorNot32BytesLong,
    #[error("The vector of `i8` from FFI should be at least 64 bytes long")]
    VectorNot64BytesLong,
    #[error("Unable to serialize `UserProfile` to bytes")]
    PackingUserProfile,
    #[error("Unable to deserialize `UserProfile` from bytes")]
    UnpackingUserProfile,
    #[error("Unable to set the storage object globally")]
    UnableToSetGlobalStorageObject,
    #[error("Attempted to get the `AppStorage` but it is not initialized yet!")]
    StoreIsNotInitialized,
    #[error("AppStorage error. Error: `{0}`")]
    StorageError(String),
    #[error("The base58 was invalid")]
    InvalidBase58String,
    #[error("The base58 was valid but it does not decode to a length of 32 bytes")]
    InvalidBase58StringIsNot32BytesLength,
    #[error("Unable to serialize `SiwsAuthResult` to bytes")]
    SerializeSiwsAuthResultToBytes,
    #[error("Unable to deserialize `SiwsAuthResult` from bytes")]
    DeserializeSiwsAuthResultToBytes,
}

impl From<redb::Error> for NativeError {
    fn from(error: redb::Error) -> Self {
        Self::StorageError(error.to_string())
    }
}
