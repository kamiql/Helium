package dev.kamiql.helium.persistence.store.impl

import dev.kamiql.helium.Main
import dev.kamiql.helium.persistence.store.Store
import dev.kamiql.helium.process.Process
import dev.kamiql.helium.serialization.Serializer
import java.lang.reflect.Type
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

class SQLiteStore<K : Any, V : Any>(
    id: String,
    keyType: Type,
    valueType: Type
) : Store<K, V>(
    id
) {
    val serializer: Serializer<Map<K, V>> = Serializer.mapSerializer(Serializer.json, keyType, valueType)

    private val file = Main.instance.dataFolder.resolve("storage/$id.db")

    private val data = ConcurrentHashMap<K, V>()

    private val dirty = ConcurrentHashMap.newKeySet<K>()
    private val deleted = ConcurrentHashMap.newKeySet<K>()

    private var connection: Connection? = null
    private var process: Process<*>? = null

    override fun enable() {
        file.parentFile.mkdirs()

        connection = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}").apply {
            createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS store_state (
                        id INTEGER PRIMARY KEY CHECK (id = 1),
                        payload BLOB NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        connection!!.prepareStatement("SELECT payload FROM store_state WHERE id = 1").use { statement ->
            statement.executeQuery().use { result ->
                if (result.next()) {
                    data.putAll(serializer.decode(result.getBytes("payload")))
                }
            }
        }

        process = Main.processManager.async().repeatEvery(5.minutes).run {
            if (dirty.isNotEmpty() || deleted.isNotEmpty()) {
                save()
            }
        }
    }

    override fun save() {
        val current = data.toMutableMap()

        deleted.forEach { current.remove(it) }

        val encoded = serializer.encode(current)

        connection?.prepareStatement(
            """
            INSERT INTO store_state(id, payload)
            VALUES (1, ?)
            ON CONFLICT(id) DO UPDATE SET payload = excluded.payload
            """.trimIndent()
        )?.use { statement ->
            statement.setBytes(1, encoded)
            statement.executeUpdate()
        }

        dirty.clear()
        deleted.clear()
    }

    override fun get(key: K): V? = data[key]

    override fun set(key: K, value: V) {
        data[key] = value
        dirty.add(key)
        deleted.remove(key)
    }

    override fun invoke(): Map<K, V> = data.toMap()

    override fun delete(key: K) {
        data.remove(key)
        dirty.remove(key)
        deleted.add(key)
    }

    override fun clear() {
        data.clear()
        dirty.clear()
        deleted.clear()
    }
}
