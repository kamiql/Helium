package dev.kamiql.helium

data class Config(
    val maintenance: Maintenance = Maintenance(),
    val prefix: String = "<b><gradient:#2F34E2:#0098FF>HELIUM</gradient></b>",
    val maxHomes: Int = 3,
    val lang: I18n = I18n()
) {
    data class Maintenance(
        var enabled: Boolean = true,
        val motd: String = "<red>Currently under maintenance!",
        val info: String = "<dark_red>Maintenance"
    )

    data class I18n(
        val fallback: String = "de-DE",
        val locales: List<String> = listOf("en-US", "de-DE"),
    )
}