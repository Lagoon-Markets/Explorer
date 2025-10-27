use base64ct::{Base64, Encoding};
use common::{SanctumBuilderResponse, SanctumRpcResponse, TxBase64Encoded};
use rocket::{http::Status, serde::json::Json};

use solana_transaction::Transaction;

use crate::SERVER_CONFIG;

#[post("/optimize-tx", format = "json", data = "<body>")]
pub async fn optimize_tx(
    body: Json<TxBase64Encoded>,
) -> Result<Json<TxBase64Encoded>, (Status, String)> {
    let decode_base64_tx = Base64::decode_vec(&body.data)
        .or(Err((Status::BadRequest, "Invalid Transaction".to_string())))?;

    // Check if the client sent a valid transaction
    bincode::deserialize::<Transaction>(&decode_base64_tx)
        .or(Err((Status::BadRequest, "Invalid transaction".to_string())))?;

    let builder_body = jzon::object! {
      "id": "1",
      "jsonrpc": "2.0",
      "method": "buildGatewayTransaction",
      "params": [
        body.data.as_str(),
        {
            deliveryMethodType: "sanctum-sender", //| "jito" | "sanctum-sender" | "helius-sender", defaults to project parameters
        }
      ]
    };

    let response = blocking::unblock(move || {
        minreq::post(SERVER_CONFIG.sanctum_uri())
            .with_header("Content-Type", "application/json")
            .with_body(builder_body.to_string())
            .send()
            .map_err(|error| (Status::InternalServerError, error.to_string()))
    })
    .await?;

    let decoded_sanctum_tx = serde_json::from_str::<SanctumRpcResponse<SanctumBuilderResponse>>(
        response.as_str().or(Err((
            Status::InternalServerError,
            "Unable to decode sanctum body as a string to parse as JSON".to_string(),
        )))?,
    )
    .or(Err((
        Status::InternalServerError,
        "Unable to parse sanctum body as JSON. Maybe you didn't follow the instructions on creating a Samctum gateway account and a delivery method".to_string(),
    )))?;

    Ok(Json(TxBase64Encoded {
        data: decoded_sanctum_tx.result.transaction,
    }))
}

#[post("/send-optimized-tx", format = "json", data = "<body>")]
pub async fn send_optimized_tx(
    body: Json<TxBase64Encoded>,
) -> Result<Json<SanctumRpcResponse<String>>, (Status, String)> {
    let decode_base64_tx = Base64::decode_vec(&body.data)
        .or(Err((Status::BadRequest, "Invalid Transaction".to_string())))?;

    // Check if the client sent a valid transaction
    bincode::deserialize::<Transaction>(&decode_base64_tx)
        .or(Err((Status::BadRequest, "Invalid transaction".to_string())))?;

    let builder_body = jzon::object! {
      "id": "1",
      "jsonrpc": "2.0",
      "method": "sendTransaction",
      "params": [
        body.data.as_str(),
        // {
        //     commitment: "confirmed",
        //     encoding: "base64"
        // }
      ]
    };

    let response = blocking::unblock(move || {minreq::post(SERVER_CONFIG.sanctum_uri())
        .with_header("Content-Type", "application/json")
        .with_body(builder_body.to_string())
        .send()
        .or(
            Err((Status::InternalServerError, "Encountered error sending transaction with `sendTransaction` method to Sanctum gateway!".to_string()))
        )}).await?;

    let decode_sent_optimized_tx =
        serde_json::from_str::<SanctumRpcResponse<String>>(response.as_str().or(
            Err((Status::InternalServerError, "The body returned by Sanctum gateway for `sendTransaction` is not a JSON string!".to_string()))
        )?).or(
            Err((Status::InternalServerError, "Unable to deserialize the sanctum response. If your Sanctum gateway config is correct then the transaction probably did not succeed!".to_string()))
        )?;

    Ok(Json(decode_sent_optimized_tx))
}
