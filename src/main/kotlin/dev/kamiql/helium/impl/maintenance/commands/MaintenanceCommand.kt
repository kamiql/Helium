package dev.kamiql.helium.impl.maintenance.commands

import dev.kamiql.helium.Main
import dev.kamiql.helium.api.c
import dev.kamiql.helium.api.config.saveConfig
import dev.kamiql.helium.api.sendC
import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerKickEvent
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("maintenance")
@CommandPermission("helium.command.maintenance")
class MaintenanceCommand {
    @Subcommand("on")
    fun on(sender: CommandSender) {
        Main.config.maintenance.enabled = true
        Main.instance.saveConfig("config.toml", Main.config)

        sender.sendC("<red>Maintenance</red> enabled")

        Main.instance.server.onlinePlayers.forEach { player ->
            if (player.hasPermission("helium.maintenance.bypass")) return@forEach

            player.kick(Main.config.maintenance.motd.c(), PlayerKickEvent.Cause.PLUGIN)
        }
    }

    @Subcommand("off")
    fun off(sender: CommandSender) {
        Main.config.maintenance.enabled = false
        Main.instance.saveConfig("config.toml", Main.config)

        sender.sendC("<red>Maintenance</red> off")
    }
}