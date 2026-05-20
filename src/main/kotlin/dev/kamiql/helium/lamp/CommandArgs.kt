package dev.kamiql.helium.lamp

import dev.kamiql.helium.Main
import dev.kamiql.helium.impl.homes.model.Home
import revxrsal.commands.Lamp
import revxrsal.commands.LampBuilderVisitor
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.exception.CommandErrorException

object CommandArgs : LampBuilderVisitor<BukkitCommandActor> {
    override fun visit(builder: Lamp.Builder<BukkitCommandActor?>) {
        builder.parameterTypes { params ->
            params.addParameterType(Home::class.java) { input, ctx ->
                val input = input.readString()
                Main.homeStorage[ctx.actor().uniqueId()]?.firstOrNull { it.name.equals(input, true) } ?: throw CommandErrorException("Home not found: $input")
            }
        }

        builder.suggestionProviders { providers ->
            providers.addProvider(Home::class.java) { ctx ->
                Main.homeStorage[ctx.actor().uniqueId()]?.map { it.name } ?: emptyList()
            }
        }
    }
}