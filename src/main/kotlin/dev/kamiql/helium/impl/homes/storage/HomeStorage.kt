package dev.kamiql.helium.impl.homes.storage

import com.fasterxml.jackson.core.type.TypeReference
import dev.kamiql.helium.impl.homes.model.Home
import dev.kamiql.helium.persistence.DataStorage
import dev.kamiql.helium.persistence.store.impl.FileStore
import dev.kamiql.helium.persistence.store.impl.SQLiteStore
import java.util.*

class HomeStorage : DataStorage<UUID, MutableList<Home>>(
    SQLiteStore(
        "homes",
        object : TypeReference<UUID>() {}.type,
        object : TypeReference<MutableList<Home>>() {}.type
    )
)
