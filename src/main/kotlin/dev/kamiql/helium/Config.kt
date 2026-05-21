package dev.kamiql.helium

data class Config(
    val prefix: String = "<b><gradient:#2F34E2:#0098FF>HELIUM</gradient></b>",
    val maxHomes: Int = 3,
    val lang: I18n = I18n()
) {
    data class I18n(
        val fallback: String = "de-DE",
        val locales: List<String> = listOf("en-US", "de-DE"),
    )
}