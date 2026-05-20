package dev.kamiql.helium

import dev.kamiql.helium.api.config.loadConfig
import dev.kamiql.helium.api.i18n.I18n
import dev.kamiql.helium.api.process.ProcessManager
import dev.kamiql.helium.api.process.types.bukkit.BukkitProcessManager
import dev.kamiql.helium.impl.economy.VaultEconomy
import dev.kamiql.helium.impl.homes.commands.HomeCommands
import dev.kamiql.helium.impl.homes.storage.HomeStorage
import dev.kamiql.helium.lamp.CommandArgs
import org.bukkit.plugin.PluginManager
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
        lateinit var economyStorage: VaultEconomy

        lateinit var pluginManager: PluginManager
    }

    override fun onLoad() {
        instance = this
        pluginManager = server.pluginManager

        Companion.config = loadConfig("config.toml")

        i18n = I18n()
    }

    override fun onEnable() {
        processManager = BukkitProcessManager(instance)

        // Storages
        homeStorage = HomeStorage()
        economyStorage = VaultEconomy()

        // Vault
        economyStorage.initialize()

        // Listeners


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