package dev.kamiql.helium.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.kamiql.helium.serialization.adapter.TypeAdapter
import dev.kamiql.helium.serialization.adapter.impl.ItemStackAdapter
import dev.kamiql.helium.serialization.adapter.impl.LocationAdapter
import java.lang.reflect.Type

class Serializer<T : Any>(
    private val mapper: ObjectMapper,
    val type: Type
) {
    companion object {
        private val kotlinModule = KotlinModule.Builder()
            .enable(KotlinFeature.UseJavaDurationConversion)
            .enable(KotlinFeature.NullIsSameAsDefault)
            .build()

        private fun ObjectMapper.typeAdapters(): ObjectMapper {
            registerModule(kotlinModule)
            listOf<TypeAdapter<*>>(
                ItemStackAdapter(this),
                LocationAdapter(this)
            ).forEach { registerModule(it.module()) }
            return this
        }

        val json: ObjectMapper = ObjectMapper()
            .typeAdapters()

        val yaml: ObjectMapper = ObjectMapper(YAMLFactory())
            .typeAdapters()

        val toml: ObjectMapper = TomlMapper()
            .typeAdapters()

        fun <K : Any, V : Any> mapSerializer(
            mapper: ObjectMapper,
            keyType: Type,
            valueType: Type
        ): Serializer<Map<K, V>> {
            val typeFactory = mapper.typeFactory
            val mapType = typeFactory.constructMapType(
                Map::class.java,
                typeFactory.constructType(keyType),
                typeFactory.constructType(valueType)
            )
            return Serializer(mapper, mapType)
        }
    }

    fun encode(value: T): ByteArray = mapper.writeValueAsBytes(value)
    fun decode(data: ByteArray): T {
        val tree = if (data.isEmpty()) mapper.createObjectNode() else mapper.readTree(data)
        return mapper.treeToValue(tree, mapper.typeFactory.constructType(type))
    }
}
