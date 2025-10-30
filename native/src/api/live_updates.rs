use base64ct::{Base64, Encoding};
use common::{
    EventSourceData, EventSourceProgressPoint, EventSourceProgressSegment, EventSourceProgressStyle,
};
use serde::{Deserialize, Serialize};
use sse_client::EventSource;
use std::sync::{Arc, Mutex};

use crate::{api::utils::log_to_logcat, NativeError, NativeResult};

#[uniffi::export(with_foreign)]
pub trait EventListenerFfi: Send + Sync {
    fn on_event(&self, event: EventSourceDataFfi) -> Result<(), NativeError>;
}

#[derive(uniffi::Object)]
pub struct EventEmitterFfi {
    listener: Arc<Mutex<Option<Arc<dyn EventListenerFfi>>>>,
}

#[uniffi::export]
impl EventEmitterFfi {
    #[uniffi::constructor]
    pub fn new() -> Arc<Self> {
        Arc::new(Self {
            listener: Arc::new(Mutex::new(None)),
        })
    }

    pub fn set_listener(&self, listener: Arc<dyn EventListenerFfi>) {
        if let Ok(mut value) = self.listener.lock() {
            *value = Some(listener)
        }
    }

    pub fn start(&self, eventsource_uri: String) {
        // let listener = self.listener.clone();

        let listener = self.listener.clone();
        std::thread::spawn(move || {
            let event_source = match EventSource::new(&eventsource_uri) {
                Ok(value) => value,
                Err(_) => return,
            };

            for event in event_source.receiver().iter() {
                log_to_logcat(&event.id);

                if event.id.as_bytes() == "done".as_bytes() {
                    log_to_logcat("DONE");

                    event_source.close();
                    break;
                } else {
                    log_to_logcat(&event.id);
                    log_to_logcat(&event.data);

                    let decoded = match serde_json::from_str::<EventSourceData>(&event.data) {
                        Ok(value) => value,
                        Err(error) => {
                            log_to_logcat(&error.to_string());

                            EventSourceData {
                                content_title: "ERROR".to_string(),
                                content_text: error.to_string(),
                                short_critical_text: "error".to_string(),
                                // large_icon: Some(INTIAL_ICON.to_string()),
                                progress: EventSourceProgressPoint {
                                    point: 0,
                                    color: "#FFFF0000".to_string(),
                                },
                                is_progress_indeterminate: false,
                                actions: vec![],
                                style: EventSourceProgressStyle {
                                    points: vec![],
                                    segments: vec![],
                                },
                            }
                        }
                    };

                    if let Some(listener) = &*listener.lock().unwrap() {
                        let _ = listener.on_event(decoded.into());
                    }
                }
            }
        });
    }
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct EventSourceDataFfi {
    pub content_title: String,
    pub content_text: String,
    pub short_critical_text: String,
    pub progress: EventSourceProgressPointFfi,
    pub is_progress_indeterminate: bool,
    pub actions: Vec<String>,
    pub style: EventSourceProgressStyleFfi,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct EventSourceProgressStyleFfi {
    pub points: Vec<EventSourceProgressPointFfi>,
    pub segments: Vec<EventSourceProgressSegmentFfi>,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct EventSourceProgressPointFfi {
    pub point: i32,
    pub color: String,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct EventSourceProgressSegmentFfi {
    pub segment: i32,
    pub color: String,
}

impl From<EventSourceData> for EventSourceDataFfi {
    fn from(value: EventSourceData) -> Self {
        // let progress_tracker_icon = Base64::decode_vec(&value.progress_tracker_icon)
        //     .unwrap_or(Base64::decode_vec(INTIAL_ICON).unwrap_or_default());
        // let large_icon = value
        //     .large_icon
        //     .map(|value| {
        //         Base64::decode_vec(&value).or(Err(NativeError::InvalidLiveUpdatesLargeIconBase64))
        //     })
        //     .transpose()
        //     .unwrap_or_default();

        Self {
            // progress_tracker_icon,
            content_title: value.content_title,
            content_text: value.content_text,
            short_critical_text: value.short_critical_text,
            // large_icon,
            progress: value.progress.into(),
            is_progress_indeterminate: value.is_progress_indeterminate,
            actions: value.actions,
            style: value.style.into(),
        }
    }
}

impl From<EventSourceProgressStyle> for EventSourceProgressStyleFfi {
    fn from(value: EventSourceProgressStyle) -> Self {
        Self {
            points: value.points.into_iter().map(|value| value.into()).collect(),
            segments: value
                .segments
                .into_iter()
                .map(|value| value.into())
                .collect(),
        }
    }
}

impl From<EventSourceProgressPoint> for EventSourceProgressPointFfi {
    fn from(value: EventSourceProgressPoint) -> Self {
        Self {
            point: value.point as i32,
            color: value.color,
        }
    }
}

impl From<EventSourceProgressSegment> for EventSourceProgressSegmentFfi {
    fn from(value: EventSourceProgressSegment) -> Self {
        Self {
            segment: value.segment as i32,
            color: value.color,
        }
    }
}
