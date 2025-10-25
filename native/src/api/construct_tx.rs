use std::str::FromStr;

use common::MintInfo;
use solana_message::Message;
use solana_pubkey::Pubkey;
use solana_transaction::Transaction;
use spl_associated_token_account::get_associated_token_address_with_program_id;

use crate::{api::discovery::DiscoveryFfi, AppStorage, NativeError, NativeResult};

#[uniffi::export]
pub async fn rustffi_construct_tx(
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

    if asset_pubkey == solana_system_interface::program::ID {
        transfer_sol(user_pubkey, fee_payer, pay_to, amount)
    } else {
        transfer_token(
            user_pubkey,
            fee_payer,
            pay_to,
            amount,
            asset_pubkey,
            mainnet,
        )
        .await
    }
}

pub async fn transfer_token(
    user_pubkey: Pubkey,
    fee_payer: Pubkey,
    pay_to: Pubkey,
    amount: u64,
    asset: Pubkey,
    mainnet: bool,
) -> NativeResult<Vec<u8>> {
    let mut url = "https://lagoon.markets/mint-info/".to_string();
    url.push_str(asset.to_string().as_str());
    url.push_str(if mainnet { "/mainnet" } else { "/devnet" });

    let response = blocking::unblock(move || {
        minreq::post(url)
            .with_header("Content-Type", "application/json")
            .send()
    })
    .await
    .map_err(|error| NativeError::Https(error.to_string()))?;
    let mint_data = serde_json::from_str::<MintInfo>(
        response
            .as_str()
            .map_err(|error| NativeError::Https(error.to_string()))?,
    )
    .or(Err(NativeError::Https(
        "Unable to parse the mint info response type from JSON".to_string(),
    )))?;

    let decimals = mint_data.decimals;
    let token_program =
        Pubkey::from_str(&mint_data.program_id).or(Err(NativeError::InvalidBase58String))?;

    let user_ata =
        get_associated_token_address_with_program_id(&user_pubkey, &asset, &token_program);

    let resource_recipient_ata =
        get_associated_token_address_with_program_id(&pay_to, &asset, &token_program);

    let transfer_ix = spl_token_2022::instruction::transfer_checked(
        &token_program,
        &user_ata,
        &asset,
        &resource_recipient_ata,
        &fee_payer,
        &[&user_pubkey, &fee_payer],
        amount,
        decimals,
    )
    .map_err(|error| NativeError::InvalidTransferCheckedData(error.to_string()))?;

    let message = Message::new(&[transfer_ix], Some(&fee_payer));
    let transfer_tx = Transaction::new_unsigned(message);

    bincode::serialize(&transfer_tx).or(Err(NativeError::UnableToSerializeTransaction))
}

pub fn transfer_sol(
    user_pubkey: Pubkey,
    fee_payer: Pubkey,
    pay_to: Pubkey,
    amount: u64,
) -> NativeResult<Vec<u8>> {
    let transfer_ix = solana_system_interface::instruction::transfer(&user_pubkey, &pay_to, amount);

    let message = Message::new(&[transfer_ix], Some(&fee_payer));
    let transfer_tx = Transaction::new_unsigned(message);

    bincode::serialize(&transfer_tx).or(Err(NativeError::UnableToSerializeTransaction))
}
