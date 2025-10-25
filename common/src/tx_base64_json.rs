use std::collections::HashMap;

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Eq, PartialOrd, Ord, Serialize, Deserialize)]

pub struct TxBase64Encoded {
    pub data: String,
}

#[derive(Debug, Clone, PartialEq, Eq, PartialOrd, Ord, Serialize, Deserialize)]
pub struct SanctumRpcResponse<T> {
    pub id: String,
    pub jsonrpc: String,
    pub result: T,
}

#[derive(Debug, Clone, PartialEq, Eq, PartialOrd, Ord, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SanctumBuilderResponse {
    pub transaction: String,
    pub latest_blockhash: LatestBlockHashResponse,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct LatestBlockHashResponse {
    pub blockhash: String,
    pub last_valid_block_height: String,
}

#[derive(Debug, PartialEq, Eq, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct TipResponse {
    pub accounts: Vec<TipAccountInfo>,
    pub program_address: String,
    pub data: HashMap<String, u8>,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Deserialize, Clone)]
pub struct TipAccountInfo {
    pub address: String,
    pub role: u8,
    pub signer: Option<TipSignerInfo>,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Deserialize, Clone)]
pub struct TipSignerInfo {
    pub address: String,
}

#[derive(Debug, PartialEq, Eq, Deserialize, Clone)]
pub struct JsonRpcResponse<T> {
    pub id: u8,
    pub result: T,
}

#[derive(Debug, PartialEq, Eq, Deserialize, Clone)]
pub struct ResultWithContext<U> {
    pub context: ResponseContext,
    pub value: U,
}

#[derive(Debug, PartialEq, Eq, Deserialize, Clone)]
pub struct ResponseContext {
    pub slot: u64,
}

#[derive(Debug, PartialEq, Eq, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct ResponseBlockHash {
    pub blockhash: String,
    pub last_valid_block_height: u64,
}

fn tip() {

    // ////////// GET the TIP

    // let tip_builder_body = jzon::object! {
    // "id": "Sanctum Sender",
    // "jsonrpc": "2.0",
    // "method": "getTipInstructions",
    // "params": [
    //   {
    //     feePayer: alice.pubkey().to_string(),
    //       jitoTipRange: "medium", // "low" | "medium" | "high" | "max", defaults to project parameters
    //       deliveryMethodType: "sanctum-sender" //| "jito" | "sanctum-sender" | "helius-sender", defaults to project parameters
    //   }
    // ]};

    // let tip_response = minreq::post(sanctum_uri)
    //     .with_header("Content-Type", "application/json")
    //     .with_body(tip_builder_body.to_string())
    //     .send()
    //     .unwrap();
    // dbg!(tip_response.clone().as_str());
    // let decode_tip = serde_json::from_str::<SanctumRpcResponse<Vec<TipResponse>>>(
    //     tip_response.as_str().unwrap(),
    // )
    // .unwrap();
    // dbg!(&decode_tip);

    // let mut tip_ixs = Vec::<Instruction>::default();

    // decode_tip.result.into_iter().for_each(|tip| {
    //     let program_id = Pubkey::from_str_const(&tip.program_address);
    //     let data = tip.data.values().copied().collect::<Vec<u8>>();
    //     let mut accounts = Vec::<AccountMeta>::default();

    //     tip.accounts.into_iter().for_each(|tip_account| {
    //         if let Some(account) = tip_account.signer.as_ref() {
    //             let public_key = Pubkey::from_str_const(&account.address);
    //             let account_inner = AccountMeta::new(public_key, true);

    //             accounts.push(account_inner);
    //         } else {
    //             let public_key = Pubkey::from_str_const(&tip_account.address);
    //             let account_inner = AccountMeta::new(public_key, false);

    //             accounts.push(account_inner);
    //         }
    //     });

    //     tip_ixs.push(Instruction {
    //         program_id,
    //         accounts,
    //         data,
    //     })
    // });

    // dbg!(&tip_ixs);

    // //-----
}
