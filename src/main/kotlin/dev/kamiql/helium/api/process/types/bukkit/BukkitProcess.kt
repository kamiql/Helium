package dev.kamiql.helium.api.process.types.bukkit

import dev.kamiql.helium.api.process.Process
import org.bukkit.scheduler.BukkitTask

class BukkitProcess: Process<BukkitTask>() {
    override fun cancel() {
        task.cancel()
    }
    override fun isCancelled(): Boolean = task.isCancelled
}