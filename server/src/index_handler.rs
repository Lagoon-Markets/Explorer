use rocket::http::Status;
#[get("/")]

pub(crate) fn index() -> (Status, &'static str) {
    (Status::NotFound, "Hey, there's no index!")
}
