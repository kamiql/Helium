package dev.kamiql.helium.persistence.store.impl

import com.fasterxml.jackson.databind.ObjectMapper
import dev.kamiql.helium.Main
import dev.kamiql.helium.persistence.store.Store
import dev.kamiql.helium.serialization.Serializer
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class FileStore<K : Any, V : Any>(
    id: String,
    keyType: Type,
    valueType: Type,
    type: ConfigType = ConfigType.TOML,
) : Store<K, V>(
    id
) {
    val serializer: Serializer<Map<K, V>> = Serializer.mapSerializer(type.serializer, keyType, valueType)

    private val file: File = Main.instance.dataFolder.resolve("config/$id.${type.extension}")

    private val data: ConcurrentMap<K, V> = ConcurrentHashMap()

    override fun enable() {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            return
        }

        data.putAll(serializer.decode(file.readBytes()))
    }

    override fun save() {
        file.writeBytes(serializer.encode(data.toMap()))
    }

    override fun get(key: K): V? {
        return data[key]
    }

    override fun set(key: K, value: V) {
        data[key] = value
    }

    override fun delete(key: K) {
        data.remove(key)
    }

    override fun invoke(): Map<K, V> {
        return data.toMap()
    }

    override fun clear() {
        data.clear()
    }

    enum class ConfigType(val extension: String, val serializer: ObjectMapper) {
        TOML("toml", Serializer.toml), YAML("yml", Serializer.yaml), JSON("json", Serializer.json);
    }
}
