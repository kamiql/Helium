package dev.kamiql.helium.impl.homes.commands

import dev.kamiql.helium.Main
import dev.kamiql.helium.impl.homes.model.Home
import dev.kamiql.helium.api.teleportWithDelay
import dev.kamiql.helium.api.translatable
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command

class HomeCommands {
    @Command("sethome")
    fun setHome(player: Player, name: String) {
        if (Main.homeStorage[player.uniqueId]?.any { it.name.equals(name, true) } == true) {
            player.translatable(
                "homes.exists.already",
                mapOf(),
                "Dieses home gibt es bereits!"
            )
            return
        }

        Main.homeStorage[player.uniqueId] = (Main.homeStorage[player.uniqueId] ?: mutableListOf()).apply {
            add(Home(name,player.location))
        }

        player.translatable(
            "homes.set",
            mapOf(),
            "Home $name gesetzt!"
        )
    }

    @Command("delhome")
    fun delHome(player: Player, home: Home) {
        if (Main.homeStorage[player.uniqueId]?.contains(home) == false) {
            player.translatable(
                "homes.exists.not",
                mapOf(),
                "Dieses home existiert nicht!"
            )
            return
        }

        Main.homeStorage[player.uniqueId] = (Main.homeStorage[player.uniqueId] ?: mutableListOf()).apply {
            remove(home)
        }

        player.translatable(
            "homes.deleted",
            mapOf(
                "home" to home.name
            ),
            "Home {home} gelöscht!"
        )
    }

    @Command("home")
    fun home(player: Player, home: Home) {
        player.teleportWithDelay {
            player.teleportAsync(home.location)
        }
    }
}