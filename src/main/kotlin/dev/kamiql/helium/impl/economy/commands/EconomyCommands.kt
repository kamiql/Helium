package dev.kamiql.helium.impl.economy.commands

import dev.kamiql.helium.api.balance
import dev.kamiql.helium.api.give
import dev.kamiql.helium.api.has
import dev.kamiql.helium.api.take
import dev.kamiql.helium.api.translatable
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("economy", "eco")
class EconomyCommands {
    @Subcommand("balance", "bal")
    fun balance(sender: Player, @Optional target: OfflinePlayer?) {
        val p = target ?: sender

        val balance = p.balance()

        sender.translatable(
            "eco.balance",
            mapOf("target" to (p.name ?: "Unknown"), "balance" to balance.toString()),
            "{target} hat {balance} Geld."
        )
    }

    @Subcommand("pay")
    fun pay(sender: Player, target: OfflinePlayer, amount: Double) {
        if (!sender.has(amount)) {
            sender.translatable("eco.insufficient.funds", mapOf(), "Du hast nicht genug Geld.")
            return
        }

        target.give(amount)
        sender.take(amount)

        sender.translatable(
            "eco.pay.success.self",
            mapOf("amount" to amount.toString(), "target" to (target.name ?: "Unknown")),
            "Du hast {amount} an {target} bezahlt."
        )
        target.player?.translatable(
            "eco.pay.success.target",
            mapOf("amount" to amount.toString(), "sender" to sender.name),
            "Du hast {amount} von {sender} erhalten."
        )
    }

    @Subcommand("give")
    @CommandPermission("helium.economy.give")
    fun give(sender: Player, target: OfflinePlayer, amount: Double) {
        target.give(amount)

        sender.translatable(
            "eco.give.success.self",
            mapOf("amount" to amount.toString(), "sender" to (target.name ?: "Unknown")),
            "Du hast {amount} an {sender} gegeben."
        )
    }

    @Subcommand("take")
    @CommandPermission("helium.economy.take")
    fun take(sender: Player, target: OfflinePlayer, amount: Double) {
        when (target.take(amount).type) {
            EconomyResponse.ResponseType.FAILURE -> {
                sender.translatable(
                    "eco.take.failure",
                    mapOf("target" to (target.name ?: "Unknown"), "amount" to amount.toString()),
                    "{target} hat keine {amount}."
                )
                return
            }
            EconomyResponse.ResponseType.SUCCESS -> {
                sender.translatable(
                    "eco.take.success.self",
                    mapOf("amount" to amount.toString(), "sender" to (target.name ?: "Unknown")),
                    "Du hast {sender} {amount} weggenommen."
                )
                return
            }
            else -> {
                sender.translatable(
                    "eco.take.error",
                    mapOf(),
                    "Es ist ein Fehler aufgetreten."
                )
                return
            }
        }
    }
}