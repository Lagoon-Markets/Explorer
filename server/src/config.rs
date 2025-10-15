use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct ServerConfig {
    payment_details: PaymentDetailsConfig,
}

impl ServerConfig {
    pub fn parse() -> Self {
        use std::{fs::File, io::Read};

        let mut file = File::open("secrets.toml")
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

        toml::from_str::<Self>(&contents).unwrap()
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
}

#[derive(Debug, Deserialize)]
pub struct PaymentDetailsConfig {
    resource_server: String,
    facilitator: Option<String>,
    client_is_facilitator: bool,
}
