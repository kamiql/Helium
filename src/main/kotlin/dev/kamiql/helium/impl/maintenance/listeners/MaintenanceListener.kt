package dev.kamiql.helium.impl.maintenance.listeners

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import dev.kamiql.helium.Main
import dev.kamiql.helium.api.c
import io.papermc.paper.connection.PlayerLoginConnection
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent

object MaintenanceListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPing(event: PaperServerListPingEvent) {
        if (!Main.config.maintenance.enabled) return

        event.version = LegacyComponentSerializer.legacySection().serialize(
            Main.config.maintenance.info.c()
        )
        event.motd(Main.config.maintenance.motd.c())
        event.protocolVersion = -1
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerLogin(event: PlayerJoinEvent) {
        if (!Main.config.maintenance.enabled) return
        if (event.player.hasPermission("helium.maintenance.bypass")) return

        event.player.kick(Main.config.maintenance.motd.c(), PlayerKickEvent.Cause.PLUGIN)
    }
}