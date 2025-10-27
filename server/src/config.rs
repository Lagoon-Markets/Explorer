use std::path::{Path, PathBuf};

use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct ServerConfig {
    payment_details: PaymentDetailsConfig,
    devnet_endpoint: String,
    mainnet_endpoint: String,
    santum_api: String,
}

impl ServerConfig {
    pub fn parse() -> Self {
        use std::{fs::File, io::Read};

        let mut path = if cfg!(debug_assertions) {
            Path::new(std::env!("CARGO_WORKSPACE_DIR")).to_path_buf()
        } else {
            PathBuf::new()
        };
        path.push("secrets.toml");

        dbg!(&path);

        let mut file = File::open(path)
            .map_err(|error| panic!("{}. Error: {}", "Unable to open `secrets.toml` file", error))
            .unwrap();
        let mut contents = String::default();

        file.read_to_string(&mut contents)
            .map_err(|error| {
                panic!(
                    "{}. Error: {}",
                    "Unable to read the contents of `secrets.toml` file", error
                )
            })
            .unwrap();

        let parsed_config = toml::from_str::<Self>(&contents).unwrap();

        if parsed_config.facilitator_address().is_none() && !parsed_config.client_is_facilitator() {
            panic!("There needs to be a facilitator. Set the `client_is_facilitator` to true or add a `facilitator` address to the config file")
        }

        parsed_config
    }

    pub fn resource_server_address(&self) -> &str {
        self.payment_details.resource_server.as_str()
    }

    pub fn facilitator_address(&self) -> Option<&String> {
        self.payment_details.facilitator.as_ref()
    }

    pub fn client_is_facilitator(&self) -> bool {
        self.payment_details.client_is_facilitator
    }

    pub fn devnet_endpoint(&self) -> &str {
        self.devnet_endpoint.as_str()
    }

    pub fn mainnet_endpoint(&self) -> &str {
        self.mainnet_endpoint.as_str()
    }

    pub fn sanctum_uri(&self) -> &str {
        self.santum_api.as_str()
    }
}

#[derive(Debug, Deserialize)]
pub struct PaymentDetailsConfig {
    resource_server: String,
    facilitator: Option<String>,
    client_is_facilitator: bool,
}
