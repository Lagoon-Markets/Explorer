use crate::Utils;

#[uniffi::export]
pub fn rustffi_shorten_base58(base58_string: String) -> String {
    Utils::shorten_base58(&base58_string)
}

#[uniffi::export]
pub fn rustffi_to_base64(bytes: Vec<u8>) -> String {
    Utils::to_base64(&bytes)
}

#[uniffi::export]
pub fn rustffi_format_amount(amount: String, decimals: Option<u8>) -> String {
    let amount = amount.parse::<u64>().unwrap_or_default();
    Utils::format_float(Utils::u64_to_float(amount, decimals.unwrap_or_default()))
}

pub fn log_to_logcat(message: &str) {
    unsafe {
        android_log_sys::__android_log_print(
            android_log_sys::LogPriority::DEBUG as i32,
            std::ffi::CString::new("LAGOON.MARKETS> ")
                .unwrap_or_default()
                .as_ptr(),
            std::ffi::CString::new(message).unwrap_or_default().as_ptr(),
        );
    }
}
