package dev.kamiql.helium.serialization.adapter.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.kamiql.helium.Main
import dev.kamiql.helium.serialization.adapter.TypeAdapter
import org.bukkit.Location

class LocationAdapter(mapper: ObjectMapper) : TypeAdapter<Location>(Location::class.java, mapper) {
    override fun serialize(value: Location): JsonNode {
        val node = mapper.nodeFactory.objectNode()
        node.put("world", value.world.name)
        node.put("x", value.x)
        node.put("y", value.y)
        node.put("z", value.z)
        node.put("yaw", value.yaw)
        node.put("pitch", value.pitch)
        return node
    }

    override fun deserialize(node: JsonNode): Location {
        val world = Main.instance.server.getWorld(node.get("world").asText())
        val x = node.get("x").asDouble()
        val y = node.get("y").asDouble()
        val z = node.get("z").asDouble()
        val yaw = node.get("yaw").asDouble().toFloat()
        val pitch = node.get("pitch").asDouble().toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }
}