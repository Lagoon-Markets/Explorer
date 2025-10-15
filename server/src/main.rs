#[macro_use]
extern crate rocket;

mod config;
pub use config::*;

mod x402_response_handler;
pub use x402_response_handler::*;

mod index_handler;
pub use index_handler::*;

mod allowed_assets;
pub use allowed_assets::*;

#[allow(clippy::redundant_closure)]
pub(crate) static SERVER_CONFIG: once_cell::sync::Lazy<ServerConfig> =
    once_cell::sync::Lazy::new(|| ServerConfig::parse());

#[launch]
fn rocket() -> _ {
    rocket::build().mount("/", routes![index, latest_newsletter])
}
