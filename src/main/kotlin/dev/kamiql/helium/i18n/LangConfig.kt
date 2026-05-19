package dev.kamiql.helium.i18n

data class LangConfig(
    val messages: MutableMap<String, List<String>> = mutableMapOf()
)