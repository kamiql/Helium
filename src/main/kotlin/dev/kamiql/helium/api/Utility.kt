package dev.kamiql.helium.api

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.text.NumberFormat
import java.util.Locale

object Utility {
    fun formatNumber(amount: Double): String {
        val nf = NumberFormat.getNumberInstance(Locale.GERMANY)
        nf.maximumFractionDigits = 1
        return nf.format(amount)
    }

    fun resolvePlayer(name: String?): OfflinePlayer? {
        return Bukkit.getOfflinePlayerIfCached(name ?: "")
    }
}