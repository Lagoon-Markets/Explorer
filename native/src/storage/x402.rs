use wincode::{SchemaRead, SchemaWrite};

use crate::api::DiscoveryFfi;

#[derive(Debug, PartialEq, PartialOrd, Eq, Ord, Clone, SchemaRead, SchemaWrite)]
pub struct X402Resource {
    pub id: [u8; 32],
    pub data: X402Data,
    pub resource_data: Vec<u8>,
}

#[derive(Debug, PartialEq, PartialOrd, Eq, Ord, Clone, SchemaRead, SchemaWrite, uniffi::Record)]
pub struct X402Data {
    pub uri: String,
    pub tx: Option<String>,
    pub resource_data: DiscoveryFfi,
}
