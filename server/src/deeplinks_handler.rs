use rocket::{fs::FileServer, response::content::RawHtml};

#[get("/deeplink/<base58>")]
fn deeplink(base58: &str) -> RawHtml<String> {
    use minijinja::{context, Environment};

    let mut env = Environment::new();
    env.add_template("template.html", include_str!("../static/template.html"))
        .unwrap();

    let rendered = env
        .get_template("template.html")
        .unwrap()
        .render(context! { base58 => base58 })
        .unwrap();

    RawHtml(rendered)
}
