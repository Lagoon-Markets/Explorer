use wincode::{SchemaRead, SchemaWrite};

use crate::{NativeError, NativeResult};

pub const PKG_VERSION: &str = env!("CARGO_PKG_VERSION");

pub struct Utils;

impl Utils {
    pub fn i8_vec_to_u8_32byte_array(i8_vec: &[i8]) -> NativeResult<[u8; 32]> {
        let i8_array: [i8; 32] = i8_vec
            .try_into()
            .or(Err(NativeError::VectorNot32BytesLong))?;

        Ok(i8_array.map(|b| b as u8))
    }

    pub fn i8_vec_to_u8_64byte_array(i8_vec: &[i8]) -> NativeResult<[u8; 64]> {
        let i8_array: [i8; 64] = i8_vec
            .try_into()
            .or(Err(NativeError::VectorNot64BytesLong))?;

        Ok(i8_array.map(|b| b as u8))
    }

    pub fn i8_vec_to_vector_of_bytes(i8_vec: &[i8]) -> Vec<u8> {
        i8_vec.iter().map(|b| *b as u8).collect::<Vec<u8>>()
    }
}

#[derive(
    Debug, PartialEq, Default, Eq, PartialOrd, Ord, Clone, SchemaRead, SchemaWrite, uniffi::Record,
)]
pub struct Base58String {
    address: String,
}

impl Base58String {
    pub fn new(public_key_bytes: &[u8]) -> Self {
        Self {
            address: bs58::encode(public_key_bytes).into_string(),
        }
    }

    fn parse(base58_str: &str) -> NativeResult<Vec<u8>> {
        bs58::decode(base58_str)
            .into_vec()
            .or(Err(NativeError::InvalidBase58String))
    }

    pub fn new_from_str(base58_str: &str) -> NativeResult<Self> {
        Self::parse(base58_str)?;

        Ok(Self {
            address: base58_str.to_string(),
        })
    }

    pub fn new_from_string(base58_str: String) -> NativeResult<Self> {
        Self::parse(base58_str.as_str())?;

        Ok(Self {
            address: base58_str,
        })
    }

    pub fn decode(&self) -> NativeResult<[u8; 32]> {
        bs58::decode(self.address.as_str())
            .into_vec()
            .or(Err(NativeError::InvalidBase58String))?
            .try_into()
            .or(Err(NativeError::InvalidBase58StringIsNot32BytesLength))
    }

    pub fn address(&self) -> &str {
        self.address.as_str()
    }
}
