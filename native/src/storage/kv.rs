use std::borrow::Borrow;

use camino::Utf8PathBuf;
use redb::{Database, Error as RedbError, Key, ReadableDatabase, TableDefinition, Value};

pub struct AppStorage {
    store: Database,
}

type UserProfileSchema = TableDefinition<'static, &'static str, String>;
type TasksSchema = TableDefinition<'static, &'static str, String>;
type SubscriptionsSchema = TableDefinition<'static, &'static str, String>;

impl AppStorage {
    pub const APP_DIR_PATH: &str = "lagoon_markets.redb";

    const USER_PROFILE_TABLE: UserProfileSchema = UserProfileSchema::new("user_profile");
    const TASKS_TABLE: TasksSchema = TasksSchema::new("tasks");
    const SUBSCRIPTIONS_TABLE: SubscriptionsSchema = SubscriptionsSchema::new("subscriptions");

    pub async fn init(app_dir_path: &str) -> Result<Self, RedbError> {
        let mut path = Utf8PathBuf::new();
        path.push(app_dir_path);
        path.push(Self::APP_DIR_PATH);

        let store = blocking::unblock(move || Database::create(path)).await?;

        Ok(Self { store })
    }

    pub fn set<'a, K: Key, V: Value>(
        &self,
        table: TableDefinition<'_, K, V>,
        key: impl Borrow<K::SelfType<'a>>,
        value: impl Borrow<V::SelfType<'a>>,
    ) -> Result<(), RedbError> {
        let write_txn = self.store.begin_write()?;
        {
            let mut table = write_txn.open_table(table)?;
            table.insert(key, value)?;
        }
        write_txn.commit()?;

        Ok(())
    }

    pub fn get<'a, K: Key, V: Value>(
        &self,
        table: TableDefinition<'_, K, V>,
        key: impl Borrow<K::SelfType<'a>>,
    ) -> Result<Option<redb::AccessGuard<'static, V>>, redb::Error> {
        let read_txn = self.store.begin_read()?;
        let table = read_txn.open_table(table)?;

        Ok(table.get(key)?)
    }
}
