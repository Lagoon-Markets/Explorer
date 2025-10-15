pub struct AllowedAssets;

impl AllowedAssets {
    pub const USDC_DEVNET: AllowedAssetDetails =
        AllowedAssetDetails::new("Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr", 6);
}

pub struct AllowedAssetDetails {
    pub address: &'static str,
    pub decimals: u8,
}

impl AllowedAssetDetails {
    pub const fn new(address: &'static str, decimals: u8) -> Self {
        Self { address, decimals }
    }
}
