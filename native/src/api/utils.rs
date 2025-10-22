use crate::Utils;

#[uniffi::export]
pub fn rustffi_shorten_base58(base58_string: String) -> String {
    Utils::shorten_base58(&base58_string)
}
