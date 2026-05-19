package dev.kamiql.helium.persistence.store.impl

import dev.kamiql.helium.persistence.store.Store
import java.util.concurrent.ConcurrentHashMap

class MemoryStore<K : Any, V : Any>(id: String) : Store<K, V>(id) {
    val data: ConcurrentHashMap<K, V> = ConcurrentHashMap()

    override fun enable() {}

    override fun save() {}

    override fun get(key: K): V? {
        return data[key]
    }

    override fun set(key: K, value: V) {
        data[key] = value
    }

    override fun invoke(): Map<K, V> {
        return data
    }

    override fun delete(key: K) {
        data.remove(key)
    }

    override fun clear() {
        data.clear()
    }
}