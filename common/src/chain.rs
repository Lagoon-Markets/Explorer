#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Copy, Default)]
pub enum SolanaChain {
    MainnetBeta,
    Testnet,
    #[default]
    Devnet,
    Localnet,
}

impl SolanaChain {
    pub const MAINNET_X402_ID: &str = "solana-mainnet";
    pub const TESTNET_X402_ID: &str = "solana-testnet";
    pub const DEVNET_X402_ID: &str = "solana-devnet";
    pub const LOCALNET_X402_ID: &str = "solana-localnet";
}
