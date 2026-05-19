package dev.kamiql.helium.serialization.adapter

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule

abstract class TypeAdapter<T : Any>(
    val type: Class<T>,
    val mapper: ObjectMapper
) {
    abstract fun serialize(value: T): JsonNode
    abstract fun deserialize(node: JsonNode): T

    private val serializer = object : JsonSerializer<T>() {
        override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeTree(this@TypeAdapter.serialize(value))
        }
    }

    private val deserializer = object : JsonDeserializer<T>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
            val node = p.codec.readTree<JsonNode>(p)
            return this@TypeAdapter.deserialize(node)
        }
    }

    fun module(): Module = SimpleModule()
        .addSerializer(type, serializer)
        .addDeserializer(type, deserializer)
}