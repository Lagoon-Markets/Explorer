#[macro_use]
extern crate rocket;

use rocket::fs::FileServer;

mod config;
pub use config::*;

mod newsletter;
pub use newsletter::*;

mod index_handler;
pub use index_handler::*;

mod allowed_assets;
pub use allowed_assets::*;

mod x402;
pub use x402::*;

mod deeplinks_handler;
pub use deeplinks_handler::*;

#[allow(clippy::redundant_closure)]
pub(crate) static SERVER_CONFIG: once_cell::sync::Lazy<ServerConfig> =
    once_cell::sync::Lazy::new(|| ServerConfig::parse());

#[rocket::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    rocket::build()
        .mount("/", FileServer::from("static"))
        .mount("/", routes![latest_newsletter])
        .mount("/x402", routes![x402_discover])
        .launch()
        .await?;

    Ok(())
}
