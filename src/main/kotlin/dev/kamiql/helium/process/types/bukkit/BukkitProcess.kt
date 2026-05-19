package dev.kamiql.helium.process.types.bukkit

import dev.kamiql.helium.process.Process
import org.bukkit.scheduler.BukkitTask

class BukkitProcess: Process<BukkitTask>() {
    override fun cancel() {
        task.cancel()
    }
    override fun isCancelled(): Boolean = task.isCancelled
}