package dev.kamiql.helium.process

import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.Duration

abstract class ProcessManager protected constructor(val plugin: JavaPlugin) {
    abstract fun from(builder: Process.Builder): Process<*>

    fun async(): Process.Builder = Process.Builder(this).async()
    fun withDelay(delay: Duration): Process.Builder = Process.Builder(this).withDelay(delay)
    fun repeatEvery(repeat: Duration): Process.Builder = Process.Builder(this).repeatEvery(repeat)
    fun sync(): Process.Builder = Process.Builder(this).sync()
    fun run(block: (Process<*>) -> Unit): Process<*> = Process.Builder(this).run(block)
}