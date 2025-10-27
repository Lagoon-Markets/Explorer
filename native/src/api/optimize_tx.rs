use std::str::FromStr;

use base64ct::{Base64, Encoding};
use common::{SanctumBuilderResponse, SanctumRpcResponse, TxBase64Encoded};
use solana_pubkey::Pubkey;

use crate::{
    api::{
        construct_tx::{transfer_sol, transfer_token},
        discovery::DiscoveryFfi,
        utils::log_to_logcat,
    },
    AppStorage, NativeError, NativeResult,
};

#[uniffi::export]
pub async fn rustffi_optimize_transaction(
    x402_resource: DiscoveryFfi,
    mainnet: bool,
) -> NativeResult<Vec<u8>> {
    let asset_pubkey =
        Pubkey::from_str(&x402_resource.asset).or(Err(NativeError::InvalidBase58String))?;

    let auth_details = AppStorage::get_store()?
        .get_auth()?
        .ok_or(NativeError::MissingUserAddress)?;
    let user_pubkey = Pubkey::new_from_array(auth_details.public_key);
    let fee_payer = if x402_resource.fee_payer.is_empty() {
        user_pubkey
    } else {
        Pubkey::from_str(&x402_resource.fee_payer).or(Err(NativeError::InvalidBase58String))?
    };
    let pay_to = Pubkey::from_str(x402_resource.pay_to.as_str())
        .or(Err(NativeError::InvalidBase58String))?;
    let amount = x402_resource
        .amount
        .parse::<u64>()
        .or(Err(NativeError::AmountNotU64))?;

    let encoded_tx = if asset_pubkey == solana_system_interface::program::ID {
        Base64::encode_string(&transfer_sol(user_pubkey, fee_payer, pay_to, amount)?)
    } else {
        Base64::encode_string(
            &transfer_token(
                user_pubkey,
                fee_payer,
                pay_to,
                amount,
                asset_pubkey,
                mainnet,
            )
            .await?,
        )
    };

    let optimize_tx_body = serde_json::to_string(&TxBase64Encoded { data: encoded_tx })
        .or(Err(NativeError::UnableToEncodeBase64TxToJson))?;

    let optimized_tx_response = blocking::unblock(move || {
        minreq::post("https://lagoon.markets/x402/optimize-tx")
            .with_header("Content-Type", "application/json")
            .with_body(optimize_tx_body)
            .send()
    })
    .await
    .map_err(|error| NativeError::Https(error.to_string()))?;
    let optimized_tx_decoded =
        serde_json::from_str::<TxBase64Encoded>(optimized_tx_response.as_str().or(Err(
            NativeError::Https("The response from `optimize-tx` route was not JSON".to_string()),
        ))?)
        .or(Err(NativeError::Https(
            "Unable to deserialize optimized transaction response".to_string(),
        )))?;

    Base64::decode_vec(&optimized_tx_decoded.data).or(Err(NativeError::UnableToDecodeOptimizedTx))
}

#[uniffi::export]
pub async fn rustffi_send_optimized_transaction(tx: Vec<u8>) -> NativeResult<String> {
    let encoded_tx = serde_json::to_string(&TxBase64Encoded {
        data: Base64::encode_string(&tx),
    })
    .or(Err(NativeError::UnableToSerializeTransaction))?;

    log_to_logcat(&(String::from("SEND OPTIMIZED tx input") + encoded_tx.as_str()));

    // let optimized_tx_response = blocking::unblock(move || {
    //     minreq::post("https://lagoon.markets/x402/send-optimized-tx")
    //         .with_header("Content-Type", "application/json")
    //         .with_body(encoded_tx)
    //         .send()
    // })
    // .await
    // .map_err(|error| {
    //     NativeError::Https(String::from("Send Optimized : ") + error.to_string().as_str())
    // })?;

    let optimized_tx_response: String = ureq::post("https://lagoon.markets/x402/send-optimized-tx")
        .header("Content-Type", "application/json")
        .send(encoded_tx)
        .map_err(|error| {
            NativeError::Https(String::from("Send Optimized : ") + error.to_string().as_str())
        })?
        .body_mut()
        .read_to_string()
        .or(Err(NativeError::Https(
            "The response from `send-optimized-tx` route was not JSON".to_string(),
        )))?;

    log_to_logcat(&(String::from("SEND OPTIMIZED tx response") + optimized_tx_response.as_str()));

    let optimized_tx_decoded = serde_json::from_str::<SanctumRpcResponse<String>>(
        optimized_tx_response.as_str(),
    )
    .or(Err(NativeError::Https(
        "Unable to deserialize optimize code response".to_string(),
    )))?;

    Ok(optimized_tx_decoded.result)
}
