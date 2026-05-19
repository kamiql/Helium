package dev.kamiql.helium.persistence.store

abstract class Store<K: Any, V: Any>(
    val id: String
) {
    abstract fun enable()
    abstract fun save()

    abstract operator fun get(key: K): V?
    abstract operator fun set(key: K, value: V)
    abstract operator fun invoke(): Map<K, V>

    abstract fun delete(key: K)
    abstract fun clear()

    fun values(): Collection<V> = invoke().values
    fun keys(): Collection<K> = invoke().keys
}