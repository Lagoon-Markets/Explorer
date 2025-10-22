use std::borrow::Borrow;

use camino::Utf8PathBuf;
use redb::{Database, Error as RedbError, Key, ReadableDatabase, TableDefinition, Value};
use serde::Deserialize;

use crate::{NativeError, NativeResult, SiwsAuthResult, APP_STORAGE};

pub type RedbResult<T> = Result<T, RedbError>;

pub struct AppStorage {
    store: Database,
    path: Utf8PathBuf,
}

type UserProfileSchema = TableDefinition<'static, &'static str, Vec<u8>>;
type AuthSchema = TableDefinition<'static, &'static str, Vec<u8>>;
type TasksSchema = TableDefinition<'static, &'static str, String>;
type SubscriptionsSchema = TableDefinition<'static, &'static str, String>;

impl AppStorage {
    pub const APP_DIR_PATH: &str = "lagoon_markets.redb";

    const TASKS_TABLE: TasksSchema = TasksSchema::new("tasks");
    const SUBSCRIPTIONS_TABLE: SubscriptionsSchema = SubscriptionsSchema::new("subscriptions");

    pub fn get_store<'a>() -> NativeResult<&'a AppStorage> {
        APP_STORAGE.get().ok_or(NativeError::StoreIsNotInitialized)
    }

    pub async fn init(app_dir_path: &str) -> Result<Self, RedbError> {
        let mut path = Utf8PathBuf::new();
        path.push(app_dir_path);
        path.push(Self::APP_DIR_PATH);

        let path_cloned = path.clone();
        let store = blocking::unblock(move || Database::create(path_cloned)).await?;

        Ok(Self { store, path })
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

    fn table_data_exists<'a, K: Key, V: Value>(
        &self,
        table: TableDefinition<'_, K, V>,
    ) -> Result<TableStatus, RedbError> {
        if let Some(error) = self.store.begin_read()?.open_table(table).err() {
            Ok(match error {
                redb::TableError::TableDoesNotExist(_) => TableStatus::NotFound,
                redb::TableError::TableExists(_) => TableStatus::Exists,
                _ => return Err(error.into()),
            })
        } else {
            Ok(TableStatus::Ok)
        }
    }
}

#[derive(Debug, PartialEq, Eq)]
enum TableStatus {
    Ok,
    Exists,
    NotFound,
}

impl AppStorage {
    const USER_PROFILE_KEY: &str = "profile";
    const USER_PROFILE_TABLE: UserProfileSchema = UserProfileSchema::new("user_profile");

    pub async fn get_profile(&'static self) -> RedbResult<Option<Vec<u8>>> {
        blocking::unblock(move || {
            self.get(Self::USER_PROFILE_TABLE, Self::USER_PROFILE_KEY)
                .map(|data| data.map(|inner| inner.value()))
        })
        .await
    }
}

impl AppStorage {
    const AUTH_KEY: &str = "auth";
    const AUTH_TABLE: AuthSchema = AuthSchema::new("user_auth");

    pub fn set_auth(&self, auth: SiwsAuthResult) -> NativeResult<()> {
        let auth_bytes =
            wincode::serialize(&auth).or(Err(NativeError::SerializeSiwsAuthResultToBytes))?;

        Ok(self.set(Self::AUTH_TABLE, Self::AUTH_KEY, auth_bytes)?)
    }

    pub fn get_auth(&self) -> NativeResult<Option<SiwsAuthResult>> {
        match self.get(Self::AUTH_TABLE, Self::AUTH_KEY) {
            Err(error) => match error {
                redb::Error::TableDoesNotExist(_) => Ok(Option::None),
                _ => Err(error.into()),
            },
            Ok(auth_bytes) => auth_bytes
                .map(|auth_bytes_inner| {
                    wincode::deserialize(&auth_bytes_inner.value())
                        .or(Err(NativeError::DeserializeSiwsAuthResultToBytes))
                })
                .transpose(),
        }
    }
}

impl AppStorage {
    const TOKEN_LIST_TABLE: AuthSchema = AuthSchema::new("token_list_table");

    pub async fn load_token_list(&self) -> NativeResult<()> {
        if self.table_data_exists(Self::TOKEN_LIST_TABLE)? == TableStatus::NotFound {
            let token_list = include_str!(concat!(
                std::env!("CARGO_WORKSPACE_DIR"),
                "solana.tokenlist.json"
            ));

            let parsed_list = serde_json::from_str::<SolanaTokenListMetadata>(token_list)
                .or(Err(NativeError::UnableToDeserializeTokenList))?;

            parsed_list.tokens.iter().try_for_each(|token| {
                let value_bytes = wincode::serialize(&token).or(Err(
                    NativeError::UnableToSerializeTokenValue(format!("{:?}", token)),
                ))?;
                self.set(Self::TOKEN_LIST_TABLE, token.address.as_str(), value_bytes)?;

                Ok::<_, NativeError>(())
            })
        } else {
            Ok(())
        }
    }

    pub fn get_token(&self, key: &str) -> NativeResult<Option<TokenInfo>> {
        self.get(Self::TOKEN_LIST_TABLE, key)?
            .map(|value| {
                let bytes = value.value();
                let deser = wincode::deserialize::<TokenInfo>(&bytes)
                    .or(Err(NativeError::CorruptedTokenInfoEntry(key.to_string())))?;

                Ok::<_, NativeError>(deser)
            })
            .transpose()
    }
}

#[derive(Debug, Deserialize, Clone)]
pub struct SolanaTokenListMetadata {
    pub tokens: Vec<TokenInfo>,
}

#[derive(Debug, Clone, Deserialize, wincode::SchemaRead, wincode::SchemaWrite, uniffi::Record)]
#[allow(non_snake_case)]
pub struct TokenInfo {
    pub chainId: u8,
    pub address: String,
    pub symbol: String,
    pub name: String,
    pub decimals: u8,
    pub logoURI: String,
}
