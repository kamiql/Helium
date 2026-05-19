package dev.kamiql.helium.i18n

import dev.kamiql.helium.Main
import dev.kamiql.helium.config.loadConfig
import dev.kamiql.helium.config.saveConfig
import org.bukkit.entity.Player

class I18n {
    var configs: Map<String, LangConfig> = Main.config.lang.locales
        .associateWith { locale -> Main.instance.loadConfig<LangConfig>("lang/$locale.yml") }

    fun message(
        player: Player,
        key: String,
        replace: Map<String, String>,
        default: List<String>
    ): List<String> {
        val localeKey = player.locale().toLanguageTag()

        val fallbackKey = Main.config.lang.fallback

        val fallback = configs[fallbackKey] ?: LangConfig()
        val locale = configs[localeKey] ?: fallback

        val source = when {
            locale.messages.containsKey(key) -> locale.messages
            fallback.messages.containsKey(key) -> fallback.messages
            else -> {
                fallback.messages[key] = default
                Main.instance.saveConfig("lang/$fallbackKey.yml", fallback)
                fallback.messages
            }
        }

        return (source[key] ?: default).map { line ->
            replace.entries.fold(line) { acc, (k, v) ->
                acc.replace("{$k}", v)
            }
        }
    }
}