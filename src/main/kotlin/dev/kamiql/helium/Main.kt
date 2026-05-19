package dev.kamiql.helium

import dev.kamiql.helium.config.loadConfig
import dev.kamiql.helium.i18n.I18n
import dev.kamiql.helium.impl.homes.commands.HomeCommands
import dev.kamiql.helium.impl.homes.storage.HomeStorage
import dev.kamiql.helium.lamp.CommandArgs
import dev.kamiql.helium.process.ProcessManager
import dev.kamiql.helium.process.types.bukkit.BukkitProcessManager
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: Main

        lateinit var processManager: ProcessManager
        lateinit var lamp: Lamp<BukkitCommandActor>
        lateinit var i18n: I18n

        lateinit var config: Config

        lateinit var homeStorage: HomeStorage
    }

    override fun onLoad() {
        instance = this

        Companion.config = loadConfig("config.toml")

        i18n = I18n()
    }

    override fun onEnable() {
        processManager = BukkitProcessManager(instance)

        homeStorage = HomeStorage()

        // Very low priority
        lamp = BukkitLamp.builder(this).accept(CommandArgs).build()
        lamp.register(
            HomeCommands()
        )
    }

    override fun onDisable() {
        homeStorage.shutdown()
    }
}