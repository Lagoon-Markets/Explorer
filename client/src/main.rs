use core::fmt;
use std::str::FromStr;

use base64ct::{Base64, Encoding};
use common::{CommonHeaders, SolanaChain, X402400BadRequest};
use rusty_x402::{PaymentRequirementsResponse, XPaymentPayload, X_PAYMENT_HEADER_KEY};
use serde::{Deserialize, Deserializer};
use solana_instruction::Instruction;
use solana_keypair::Keypair;
use solana_message::Message;
use solana_pubkey::Pubkey;
use solana_signer::Signer;
use solana_transaction::Transaction;
use spl_associated_token_account::{
    get_associated_token_address, get_associated_token_address_with_program_id,
};
use spl_token::ID as LEGACY_TOKEN_PROGRAM;
use spl_token_2022::ID as TOKEN_EXT_PROGRAM;

fn main() {
    let resource_uri = "http://localhost:8000/latest_newsletter";
    let solana_devnet = "https://api.devnet.solana.com";

    let file_contents = std::fs::read_to_string("/workspaces/client-keypair.json").unwrap();
    let buffer = file_contents
        .trim()
        .trim_matches(|c| c == '[' || c == ']')
        .split(',')
        .map(|s| {
            s.trim()
                .parse::<u8>()
                .expect("Invalid char is not a number")
        })
        .collect::<Vec<u8>>();
    let payer_keypair: [u8; 64] = buffer.try_into().expect("Invalid keypair bytes");
    let payer_keypair = Keypair::new_from_array(payer_keypair[0..32].try_into().unwrap());

    let response = minreq::get(resource_uri)
        .with_header(
            CommonHeaders::X402_ADDRESS_HEADER,
            payer_keypair.pubkey().to_string(),
        )
        .with_header(
            CommonHeaders::X402_CHAIN_HEADER,
            SolanaChain::DEVNET_X402_ID,
        )
        .send()
        .unwrap();

    if response.status_code == 402 {
        println!("Payment required");
        send_authorization(&payer_keypair, response, resource_uri);
    } else if response.status_code == 200 {
        println!("User is authorized");
    } else {
        println!("Server error: {:?}", response.as_str());
    }
}

#[derive(Debug, Deserialize)]
pub struct RpcResponse<T: fmt::Debug> {
    pub jsonrpc: String,
    pub result: T,
    pub id: u8,
}

#[derive(Debug, Deserialize)]
pub struct ResponseWithContext {
    pub context: ResponseContext,
    pub value: LatestBlockhash,
}

#[derive(Debug, Deserialize)]
pub struct ResponseContext {
    pub slot: u64,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct LatestBlockhash {
    #[serde(deserialize_with = "deserialize_blockhash")]
    pub blockhash: solana_hash::Hash,
    pub last_valid_block_height: u64,
}

pub fn deserialize_blockhash<'de, D>(deserializer: D) -> Result<solana_hash::Hash, D::Error>
where
    D: Deserializer<'de>,
{
    let hash_str = String::deserialize(deserializer)?;
    solana_hash::Hash::from_str(&hash_str).map_err(serde::de::Error::custom)
}

fn send_authorization(party_paying: &Keypair, response: minreq::Response, resource_uri: &str) {
    let response_body =
        serde_json::from_str::<PaymentRequirementsResponse>(response.as_str().unwrap()).unwrap();

    println!("USER PUBKEY: {}", party_paying.pubkey());

    let mut ixs = Vec::<Instruction>::new();
    let mut fee_payer = Pubkey::default();

    response_body.accepts().iter().for_each(|requirements| {
        let resource_recipient = Pubkey::from_str_const(requirements.pay_to());
        fee_payer = Pubkey::from_str_const(requirements.extra().fee_payer());
        let amount = requirements.max_amount_required();
        let asset = Pubkey::from_str_const(requirements.asset());
        let token_extensions_mint = requirements.extra().token_extensions_mint();
        dbg!(token_extensions_mint);
        let decimals = requirements.extra().decimals();

        let ix: Instruction;

        if solana_sdk_ids::system_program::check_id(&asset) {
            ix = solana_system_interface::instruction::transfer(
                &party_paying.pubkey(),
                &resource_recipient,
                amount,
            );
        } else if token_extensions_mint {
            let ata = get_associated_token_address_with_program_id(
                &party_paying.pubkey(),
                &asset,
                &TOKEN_EXT_PROGRAM,
            );
            println!("ATA: {ata}");
            let resource_recipient_ata = get_associated_token_address_with_program_id(
                &resource_recipient,
                &asset,
                &TOKEN_EXT_PROGRAM,
            );
            println!("PAY TO ATA: {resource_recipient_ata}");

            ix = spl_token_2022::instruction::transfer_checked(
                &TOKEN_EXT_PROGRAM,
                &ata,
                &asset,
                &resource_recipient_ata,
                &party_paying.pubkey(),
                &[&party_paying.pubkey(), &fee_payer],
                amount,
                decimals,
            )
            .unwrap();
        } else {
            let ata = get_associated_token_address(&party_paying.pubkey(), &asset);
            println!("LEGACY ATA: {ata}");
            let resource_recipient_ata = get_associated_token_address(&resource_recipient, &asset);
            println!("LEGACY PAY TO ATA: {resource_recipient_ata}");

            ix = spl_token::instruction::transfer_checked(
                &LEGACY_TOKEN_PROGRAM,
                &ata,
                &asset,
                &resource_recipient_ata,
                &party_paying.pubkey(),
                &[&party_paying.pubkey(), &fee_payer],
                amount,
                decimals,
            )
            .unwrap();
        }

        ixs.push(ix);
    });

    let message = Message::new(ixs.as_slice(), Some(&fee_payer));
    let mut tx = Transaction::new_unsigned(message);

    tx.partial_sign(&[&party_paying], solana_hash::Hash::new_unique());

    let tx_bytes = bincode::serialize(&tx).unwrap();
    let x_payment_header = XPaymentPayload::new(tx_bytes);
    let encoded_x_payment_header = serde_json::to_string(&x_payment_header).unwrap();
    let base64_encoded_x_payment_header =
        Base64::encode_string(encoded_x_payment_header.as_bytes());

    let response = minreq::get(resource_uri)
        .with_header(X_PAYMENT_HEADER_KEY, base64_encoded_x_payment_header)
        .with_header(
            CommonHeaders::X402_CHAIN_HEADER,
            SolanaChain::DEVNET_X402_ID,
        )
        .with_header(
            CommonHeaders::X402_ADDRESS_HEADER,
            party_paying.pubkey().to_string(),
        )
        .send()
        .unwrap();

    if response.status_code == 200 {
        println!("Transaction was successful")
    } else if response.status_code == 400 {
        let deser = serde_json::from_str::<X402400BadRequest>(response.as_str().unwrap());
        dbg!(deser.unwrap());
    }
}

// let recent_blockhash_response = minreq::post(solana_devnet)
//     .with_header("Content-Type", "application/json")
//     .with_body(
//         jzon::object! {
//           "jsonrpc": "2.0",
//           "id": 1,
//           "method": "getLatestBlockhash",
//           "params": [{"commitment": "finalized"}]
//         }
//         .to_string(),
//     )
//     .send()
//     .unwrap();
// let recent_blockhash = serde_json::from_str::<RpcResponse<ResponseWithContext>>(
//     recent_blockhash_response.as_str().unwrap(),
// )
// .unwrap()
// .result
// .value
// .blockhash;
// tx.sign(&[&party_paying], recent_blockhash);

// let send_tx_response = minreq::post(solana_devnet)
//     .with_header("Content-Type", "application/json")
//     .with_body(
//         jzon::object! {
//             "jsonrpc": "2.0",
//         "id": 1,
//         "method": "sendTransaction",
//         "params": [
//             encoded_base64_tx,
//             {
//                 encoding: "base64"
//             }
//         ]
//         }
//         .to_string(),
//     )
//     .send()
//     .unwrap();
// dbg!(&send_tx_response.clone().as_str());

// let response_signature =
//     serde_json::from_str::<RpcResponse<String>>(send_tx_response.as_str().unwrap()).unwrap();
// dbg!(&response_signature);
