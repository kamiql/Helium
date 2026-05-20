package dev.kamiql.helium.api.config

import dev.kamiql.helium.Main
import dev.kamiql.helium.serialization.Serializer
import org.bukkit.plugin.java.JavaPlugin

inline fun <reified C : Any> JavaPlugin.loadConfig(path: String): C {
    val file = Main.instance.dataFolder.resolve(path)

    if (!file.exists()) {
        file.parentFile.mkdirs()
        this::class.java.classLoader.getResourceAsStream(path)
            ?.use { file.outputStream().use(it::copyTo) }
            ?: file.createNewFile()
    }

    val mapper = when (file.extension) {
        "json" -> Serializer.json
        "yaml", "yml" -> Serializer.yaml
        "toml" -> Serializer.toml
        else -> error("Unsupported file extension ${file.extension}")
    }

    val serializer = Serializer<C>(
        mapper,
        C::class.java,
    )

    return serializer.decode(file.readBytes())
}

fun <C : Any> JavaPlugin.saveConfig(path: String, config: C) {
    val file = Main.instance.dataFolder.resolve(path)

    if (!file.exists()) {
        file.parentFile.mkdirs()
        file.createNewFile()
    }

    val mapper = when (file.extension) {
        "json" -> Serializer.json
        "yaml", "yml" -> Serializer.yaml
        "toml" -> Serializer.toml
        else -> error("Unsupported file extension ${file.extension}")
    }

    val serializer = Serializer<C>(
        mapper,
        config.javaClass
    )

    file.writeBytes(serializer.encode(config))
}