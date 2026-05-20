package dev.kamiql.helium.api

import dev.kamiql.helium.Main
import dev.kamiql.helium.Main.Companion.economyStorage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachmentInfo
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val cleanCharacters = mapOf(
    "a" to 'ᴀ',
    "b" to 'ʙ',
    "c" to 'ᴄ',
    "d" to 'ᴅ',
    "e" to 'ᴇ',
    "f" to 'ꜰ',
    "g" to 'ɢ',
    "h" to 'ʜ',
    "i" to 'ɪ',
    "j" to 'ᴊ',
    "k" to 'ᴋ',
    "l" to 'ʟ',
    "m" to 'ᴍ',
    "n" to 'ɴ',
    "o" to 'ᴏ',
    "p" to 'ᴘ',
    "q" to 'q',
    "r" to 'ʀ',
    "s" to 'ꜱ',
    "t" to 'ᴛ',
    "u" to 'ᴜ',
    "v" to 'v',
    "w" to 'ᴡ',
    "x" to 'x',
    "y" to 'ʏ',
    "z" to 'ᴢ'
)

fun String.c(): Component {
    return MiniMessage.miniMessage().deserialize(this)
}

fun String.cc(): String {
    val regex = """(<[^>]+>)|([^<]+)""".toRegex()
    val processedString = buildString {
        regex.findAll(this@cc).forEach { match ->
            val tagPart = match.groups[1]?.value
            val textPart = match.groups[2]?.value
            if (tagPart != null) {
                append(tagPart)
            } else textPart?.forEach { char ->
                val key = char.lowercaseChar().toString()
                append(cleanCharacters[key] ?: char)
            }
        }
    }

    return processedString
}

fun String.ccc(): Component {
    return this.cc().c()
}

fun Duration.toBukkitTicks(): Long {
    return this.inWholeMilliseconds / 50
}

fun Audience.sendCCC(
    text: String
) {
    this.sendMessage(text.ccc())
}

fun Audience.sendC(
    text: String
) {
    this.sendMessage(text.c())
}

fun Audience.actionCCC(
    text: String
) {
    this.sendActionBar(
        "<aqua>» <gray>$text</gray> «</aqua>".ccc()
    )
}

fun Audience.sendPrefixedCCC(
    prefix: String, symbol: String, vararg messages: String
) {
    this.sendC("")
    this.sendCCC("$symbol $prefix")
    messages.forEach { message ->
        this.sendC("<gray>$message")
    }
    this.sendC("")
}

fun Player.translatable(id: String, replace: Map<String, String>, vararg default: String) {
    this.sendPrefixedCCC(
        Main.Companion.config.prefix,
        "✉",
        *Main.Companion.i18n.message(this, id, replace, default.toList()).toTypedArray()
    )
}

fun Player.error(id: String, replace: Map<String, String>, vararg default: String) {
    this.sendPrefixedCCC(
        "<gradient:red:white>Error</gradient>",
        "✖",
        *Main.Companion.i18n.message(this, id, replace, default.toList()).toTypedArray()
    )
}

fun Player.teleportWithDelay(seconds: Int = 5, callback: () -> Unit) {
    var current = 0
    var lastLocation = this.location
    Main.Companion.processManager.repeatEvery(1.seconds).run { process ->
        if (current >= seconds) {
            callback()
            process.cancel()
        } else {
            this.actionCCC("${seconds - current}s")
            sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING)

            if (location.x == lastLocation.x && location.y == lastLocation.y) {
                current++
            } else {
                this.actionCCC("Teleportation cancelled")
                sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS)
                process.cancel()
            }
        }
    }
}

fun Audience.sound(sound: org.bukkit.Sound) {
    this.playSound(
        Sound.sound(sound, Sound.Source.PLAYER, 1f, 1f)
    )
}

@Suppress("UNCHECKED_CAST")
fun <T> Player.extract(
    prefix: String,
    default: T
): T = (this.effectivePermissions
    .map(PermissionAttachmentInfo::getPermission)
    .asSequence()
    .filter { it.startsWith("$prefix.") }
    .map { it.substringAfter("$prefix.") }
    .firstOrNull() ?: default
) as T

fun OfflinePlayer.balance(): Double = economyStorage.getBalance(this)
fun OfflinePlayer.has(amount: Double): Boolean = economyStorage.has(this, amount)
fun OfflinePlayer.take(amount: Double) = economyStorage.withdrawPlayer(this, amount)
fun OfflinePlayer.give(amount: Double) = economyStorage.depositPlayer(this, amount)