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

#[launch]
fn rocket() -> _ {
    rocket::build().mount("/", routes![index, latest_newsletter])
}
