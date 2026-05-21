package dev.kamiql.helium.impl.tpa.commands

import dev.kamiql.helium.Main
import dev.kamiql.helium.api.teleportWithDelay
import dev.kamiql.helium.api.translatable
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import kotlin.time.Duration.Companion.minutes

class TpaCommands {
    private val requests = mutableListOf<TpaRequest>()

    @Command("tpa")
    fun tpa(sender: Player, target: Player) {
        sendRequest(sender, target, TpaType.TPA)
    }

    @Command("tpahere")
    fun tpahere(sender: Player, target: Player) {
        sendRequest(sender, target, TpaType.TPA_HERE)
    }

    @Command("tpaaccept")
    fun tpaAccept(sender: Player, target: Player) {
        val request = requests.find {
            it.sender == target && it.target == sender
        }

        if (request == null) {
            sender.translatable(
                "tpa.request.invalid",
                mapOf("target" to target.name),
                "Du hast keine TPA Anfrage von {target} erhalten"
            )
            return
        }

        requests.remove(request)

        when (request.type) {
            TpaType.TPA -> {
                request.sender.teleportWithDelay {
                    request.sender.teleportAsync(request.target.location)
                }
            }

            TpaType.TPA_HERE -> {
                request.target.teleportWithDelay {
                    request.target.teleportAsync(request.sender.location)
                }
            }
        }

        sender.translatable(
            "tpa.request.accepted.self",
            mapOf("target" to target.name),
            "Du hast die TPA Anfrage von {target} akzeptiert"
        )

        target.translatable(
            "tpa.request.accepted.target",
            mapOf("sender" to sender.name),
            "{sender} hat deine TPA Anfrage akzeptiert"
        )
    }

    @Command("tpadeny")
    fun tpaDeny(sender: Player, target: Player) {
        val request = requests.find {
            it.sender == target && it.target == sender
        }

        if (request == null) {
            sender.translatable(
                "tpa.request.invalid",
                mapOf("target" to target.name),
                "Du hast keine TPA Anfrage von {target} erhalten"
            )
            return
        }

        requests.remove(request)

        sender.translatable(
            "tpa.request.denied.self",
            mapOf("target" to target.name),
            "Du hast die TPA Anfrage von {target} abgelehnt"
        )

        target.translatable(
            "tpa.request.denied.target",
            mapOf("sender" to sender.name),
            "{sender} hat deine TPA Anfrage abgelehnt"
        )
    }

    private fun sendRequest(sender: Player, target: Player, type: TpaType) {
        if (sender == target) {
            sender.translatable(
                "tpa.request.self",
                emptyMap(),
                "Du kannst keine TPA Anfrage an dich selbst senden"
            )
            return
        }

        val existing = requests.find { it.sender == sender }

        if (existing != null) {
            sender.translatable(
                "tpa.request.already_sent",
                mapOf("target" to existing.target.name),
                "Du hast bereits eine TPA Anfrage an {target} gesendet"
            )
            return
        }

        val request = TpaRequest(
            sender = sender,
            target = target,
            type = type
        )

        requests += request

        Main.processManager.withDelay(5.minutes).run {
            if (!requests.remove(request)) return@run

            sender.translatable(
                "tpa.request.expired",
                mapOf("target" to target.name),
                "Deine TPA Anfrage an {target} ist abgelaufen"
            )
        }

        sender.translatable(
            "tpa.request.sent",
            mapOf("target" to target.name),
            "Du hast eine TPA Anfrage an {target} gesendet"
        )

        target.translatable(
            "tpa.request.received",
            mapOf(
                "sender" to sender.name,
                "type" to when (type) {
                    TpaType.TPA -> "/tpa"
                    TpaType.TPA_HERE -> "/tpahere"
                }
            ),
            "Du hast eine {type} Anfrage von {sender} erhalten"
        )
    }

    data class TpaRequest(
        val sender: Player,
        val target: Player,
        val type: TpaType
    )

    enum class TpaType {
        TPA,
        TPA_HERE
    }
}