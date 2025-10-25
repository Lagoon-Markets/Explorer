use std::str::FromStr;

use base64ct::{Base64, Encoding};
use common::{MintInfo, RpcResponse, RpcResponseAccountInfo, RpcResponseWithContext};
use rocket::{http::Status, serde::json::Json};
use solana_pubkey::Pubkey;
use spl_token_2022::{extension::StateWithExtensions, state::Mint};

use crate::SERVER_CONFIG;

#[get("/mint-info/<address>/<chain>")]
pub fn mint_info(address: &str, chain: &str) -> Result<Json<MintInfo>, (Status, String)> {
    Pubkey::from_str(address).or(Err((
        Status::BadRequest,
        "Invalid Base58 address".to_string(),
    )))?;

    let body = jzon::object! {
      "jsonrpc": "2.0",
      "id": 1,
      "method": "getAccountInfo",
      "params": [
        address,
        {
          "commitment": "finalized",
          "encoding": "base64"
        }
      ]
    }
    .to_string();
    let url = if chain.as_bytes() == "mainnet".as_bytes() {
        SERVER_CONFIG.mainnet_endpoint()
    } else {
        SERVER_CONFIG.devnet_endpoint()
    };
    let response = minreq::post(url)
        .with_header("Content-Type", "application/json")
        .with_body(body)
        .send()
        .or(Err((
            Status::InternalServerError,
            "Unable to send the request to get mint account info to the RPC".to_string(),
        )))?;
    let parsed =
        serde_json::from_str::<RpcResponse<RpcResponseWithContext<RpcResponseAccountInfo>>>(
            response.as_str().or(Err((
                Status::InternalServerError,
                "The response body from fetchung mint account info is not a JSON string"
                    .to_string(),
            )))?,
        )
        .or(Err((Status::InternalServerError, "Unable to parse the JSON response from RPC. This probably indicates failure of a operation".to_string())))?;
    let mint_data = Base64::decode_vec(&parsed.result.value.data.0).or(Err((
        Status::InternalServerError,
        "Unable to decode the data of a mint from base64".to_string(),
    )))?;

    let mint_data = StateWithExtensions::<Mint>::unpack(&mint_data).or(Err((
        Status::InternalServerError,
        "Unable to unpack the data from the mint".to_string(),
    )))?;

    Ok(Json(MintInfo {
        program_id: parsed.result.value.owner,
        decimals: mint_data.base.decimals,
        mint_authority: mint_data
            .base
            .freeze_authority
            .map(|pubkey| pubkey.to_string())
            .into(),
        freeze_authority: mint_data
            .base
            .freeze_authority
            .map(|pubkey| pubkey.to_string())
            .into(),
    }))
}
