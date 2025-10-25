use base64ct::{Base64, Encoding};
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

    pub fn shorten_base58(base58_string: &str) -> String {
        let chars = base58_string.chars().collect::<Vec<char>>();

        let mut shortened = String::default();
        chars.iter().take(5).for_each(|char| shortened.push(*char));
        shortened.push_str("...");

        chars[chars.len() - 5..]
            .iter()
            .for_each(|char| shortened.push(*char));

        shortened
    }

    pub fn to_base64(bytes: &[u8]) -> String {
        Base64::encode_string(bytes)
    }

    pub fn u64_to_float(amount: u64, decimals: u8) -> f64 {
        10_usize
            .checked_pow(decimals as u32)
            .map(|dividend| amount as f64 / dividend as f64)
            .unwrap_or_default()
    }

    pub fn format_float(amount: f64) -> String {
        let s = format!("{:.6}", amount);
        let s = s.trim_end_matches('0').trim_end_matches('.');
        if s.is_empty() {
            "0".to_string()
        } else {
            s.to_string()
        }
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
