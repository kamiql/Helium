package dev.kamiql.helium.serialization.adapter.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BinaryNode
import dev.kamiql.helium.serialization.adapter.TypeAdapter
import org.bukkit.inventory.ItemStack

class ItemStackAdapter(mapper: ObjectMapper) : TypeAdapter<ItemStack>(ItemStack::class.java, mapper) {
    override fun serialize(value: ItemStack): JsonNode {
        return BinaryNode.valueOf(value.serializeAsBytes())
    }

    override fun deserialize(node: JsonNode): ItemStack {
        return ItemStack.deserializeBytes(node.binaryValue())
    }
}