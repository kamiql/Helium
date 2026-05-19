package dev.kamiql.helium.persistence

import dev.kamiql.helium.persistence.store.Store

/**
 * DataStorage
 */
abstract class DataStorage<K: Any, V: Any>(private val store: Store<K, V>) {
    init { store.enable() }

    operator fun get(key: K): V? = store[key]
    operator fun set(key: K, value: V) = store.set(key, value)
    operator fun invoke(): Map<K, V> = store.invoke()

    fun delete(key: K) = store.delete(key)
    fun clear() = store.clear()

    fun values(): Collection<V> = invoke().values
    fun keys(): Collection<K> = invoke().keys

    fun shutdown() {
        store.save()
    }
}