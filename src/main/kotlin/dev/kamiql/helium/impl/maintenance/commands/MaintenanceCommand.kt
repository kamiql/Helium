package dev.kamiql.helium.impl.maintenance.commands

import dev.kamiql.helium.Main
import dev.kamiql.helium.api.config.saveConfig
import dev.kamiql.helium.api.sendC
import org.bukkit.command.CommandSender
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand

@Command("maintenance")
class MaintenanceCommand {
    @Subcommand("on")
    fun on(sender: CommandSender) {
        Main.config.maintenance.enabled = true
        Main.instance.saveConfig("config.toml", Main.config)

        sender.sendC("<red>Maintenance</red> enabled")
    }

    @Subcommand("off")
    fun off(sender: CommandSender) {
        Main.config.maintenance.enabled = false
        Main.instance.saveConfig("config.toml", Main.config)

        sender.sendC("<red>Maintenance</red> off")
    }
}