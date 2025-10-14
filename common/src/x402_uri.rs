use serde::{Deserialize, Serialize};

#[derive(
    Debug, PartialEq, PartialOrd, Hash, Clone, Copy, Default, Eq, Ord, Serialize, Deserialize,
)]
pub enum X402Uri {
    #[default]
    Discover,
    Subscribe,
    Unsubscribe,
}
