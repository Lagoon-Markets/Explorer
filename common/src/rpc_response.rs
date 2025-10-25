use serde::{Deserialize, Serialize};

#[derive(Debug, PartialEq, Eq, Deserialize)]
pub struct RpcResponse<T> {
    pub id: u8,
    pub jsonrpc: String,
    pub result: T,
}

#[derive(Debug, PartialEq, Eq, Deserialize)]
pub struct RpcResponseWithContext<U> {
    pub context: RpcContext,
    pub value: U,
}

#[derive(Debug, PartialEq, Eq, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct RpcContext {
    pub api_version: String,
    pub slot: u64,
}

#[derive(Debug, PartialEq, Eq, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct RpcResponseAccountInfo {
    pub data: (String, String),
    pub executable: bool,
    pub lamports: u64,
    pub owner: String,
    pub rent_epoch: Option<u64>,
    pub space: u64,
}

#[derive(Debug, PartialEq, Eq, Clone, Serialize, Deserialize)]
pub struct MintInfo {
    pub program_id: String,
    pub decimals: u8,
    pub mint_authority: Option<String>,
    pub freeze_authority: Option<String>,
}
