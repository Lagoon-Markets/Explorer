use serde::{Deserialize, Serialize};

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct EventSourceData {
    pub content_title: String,
    pub content_text: String,
    pub short_critical_text: String,
    pub progress: EventSourceProgressPoint,
    pub is_progress_indeterminate: bool,
    pub actions: Vec<String>,
    pub style: EventSourceProgressStyle,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize)]
pub struct EventSourceProgressPoint {
    pub point: u32,
    pub color: String,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize)]
pub struct EventSourceProgressSegment {
    pub segment: u32,
    pub color: String,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize)]
pub struct EventSourceProgressStyle {
    pub points: Vec<EventSourceProgressPoint>,
    pub segments: Vec<EventSourceProgressSegment>,
}
