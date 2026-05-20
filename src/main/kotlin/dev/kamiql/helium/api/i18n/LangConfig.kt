package dev.kamiql.helium.api.i18n

data class LangConfig(
    val messages: MutableMap<String, List<String>> = mutableMapOf()
)