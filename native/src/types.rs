use once_cell::sync::OnceCell;

use crate::AppStorage;

pub static APP_STORAGE: OnceCell<AppStorage> = OnceCell::new();

pub type Byte32Array = [u8; 32];
pub type Byte64Array = [u8; 64];
