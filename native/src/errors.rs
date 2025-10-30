pub type NativeResult<T> = Result<T, NativeError>;

#[derive(thiserror::Error, uniffi::Error, Debug, PartialEq, Eq)]
pub enum NativeError {
    #[error("Expected the user's profile to be initialized in order to get the user's address to use in transactions. Sign In With Solana first to achieve this.")]
    MissingUserAddress,
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
    #[error("X402Uri error: `{0}`")]
    X402Uri(String),
    #[error("The scheme for x402://.../<scheme>:// is not supported")]
    UnsupportedX402Scheme,
    #[error("Invalid X402Uri. Error: `{0}`.")]
    InvalidX402Uri(String),
    #[error("Encountered HTTPS error: `{0}`")]
    Https(String),
    #[error("The x402 resource needs at least one payment method in `accepts` field")]
    AtLeastOneAcceptsItemIsNeeded,
    #[error("Unable to deserialize `solana.tokenlist.json` ")]
    UnableToDeserializeTokenList,
    #[error("Unable to serialize `{0}` Token value from `solana.tokenlist.json`")]
    UnableToSerializeTokenValue(String),
    #[error("The entry `{0}` from the token list table is corrupted. Unable to deserialize into TokenInfo")]
    CorruptedTokenInfoEntry(String),
    #[error("The amount could not be converted into an unsigned 64 bit integer number `2^64-1`")]
    AmountNotU64,
    #[error("Unable to serialize the Transaction into bytes")]
    UnableToSerializeTransaction,
    #[error("Creating the transfer checked instruction resulted in an error. Error `{0}`")]
    InvalidTransferCheckedData(String),
    #[error(
        "Unabe to convert the base64 transaction to optimize into JSON for the `optimize-tx` route"
    )]
    UnableToEncodeBase64TxToJson,
    #[error("Unable to decode the result of parsing the JSON from `optimize-tx` route. Is the data Base64?")]
    UnableToDecodeOptimizedTx,
    #[error("The `largeIcon` parameter for live updates is not a valid base64 string")]
    InvalidLiveUpdatesLargeIconBase64,
    #[error("The `progressTrackerIcon` parameter for live updates is not a valid base64 string")]
    InvalidLiveUpdatesProgressTrackerIconBase64,
    #[error("Unable to serialize a X402Resource to DB")]
    SerializeX402DataToBytes,
    #[error("Unable to deserialize a X402Resource from DB")]
    DeserializeX402Resource,
}

impl From<redb::Error> for NativeError {
    fn from(error: redb::Error) -> Self {
        Self::StorageError(error.to_string())
    }
}
