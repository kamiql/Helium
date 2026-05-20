package dev.kamiql.helium.impl.economy

import dev.kamiql.helium.Main
import dev.kamiql.helium.api.Utility
import dev.kamiql.helium.persistence.DataStorage
import dev.kamiql.helium.persistence.store.impl.SQLiteStore
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.ServicePriority
import java.util.UUID

class VaultEconomy : Economy, DataStorage<UUID, Double>(SQLiteStore(
    "economy",
    UUID::class.java,
    Double::class.javaObjectType
)) {
    override fun isEnabled(): Boolean = Main.instance.isEnabled

    override fun getName(): String = "Helium"

    override fun hasBankSupport(): Boolean = false

    override fun fractionalDigits(): Int = 2
    override fun format(p0: Double): String = "$${Utility.formatNumber(p0)}"

    override fun currencyNamePlural(): String = "Coins"
    override fun currencyNameSingular(): String = "Coin"

    override fun hasAccount(p0: String?): Boolean = Utility.resolvePlayer(p0) != null
    override fun hasAccount(p0: OfflinePlayer?): Boolean = p0 != null
    override fun hasAccount(p0: String?, p1: String?): Boolean = Utility.resolvePlayer(p0) != null
    override fun hasAccount(p0: OfflinePlayer?, p1: String?): Boolean = p0 != null

    override fun getBalance(p0: String?): Double = Utility.resolvePlayer(p0)?.let { this[it.uniqueId] } ?: 0.0
    override fun getBalance(p0: OfflinePlayer?): Double = p0?.let { this[it.uniqueId] } ?: 0.0
    override fun getBalance(p0: String?, p1: String?): Double = getBalance(Utility.resolvePlayer(p0))
    override fun getBalance(p0: OfflinePlayer?, p1: String?): Double = getBalance(p0)

    override fun has(p0: String?, p1: Double): Boolean = p1 >= 0.0 && getBalance(p0) >= p1
    override fun has(p0: OfflinePlayer?, p1: Double): Boolean = p1 >= 0.0 && getBalance(p0) >= p1
    override fun has(p0: String?, p1: String?, p2: Double): Boolean = has(p0, p2)
    override fun has(p0: OfflinePlayer?, p1: String?, p2: Double): Boolean = has(p0, p2)

    override fun withdrawPlayer(p0: String?, p1: Double): EconomyResponse = withdrawPlayer(Utility.resolvePlayer(p0), p1)
    override fun withdrawPlayer(
        p0: OfflinePlayer?,
        p1: Double
    ): EconomyResponse {
        p0 ?: return missingAccountResponse()
        if (p1 < 0.0) return invalidAmountResponse(p1, p0)

        val balance = getBalance(p0)
        if (balance < p1) {
            return EconomyResponse(p1, balance, ResponseType.FAILURE, "Insufficient funds")
        }

        this[p0.uniqueId] = balance - p1
        return EconomyResponse(p1, getBalance(p0), ResponseType.SUCCESS, "Withdrawal successful")
    }
    override fun withdrawPlayer(
        p0: String?,
        p1: String?,
        p2: Double
    ): EconomyResponse = withdrawPlayer(Utility.resolvePlayer(p0), p2)
    override fun withdrawPlayer(
        p0: OfflinePlayer?,
        p1: String?,
        p2: Double
    ): EconomyResponse = withdrawPlayer(p0, p2)


    override fun depositPlayer(p0: String?, p1: Double): EconomyResponse = depositPlayer(Utility.resolvePlayer(p0), p1)
    override fun depositPlayer(
        p0: OfflinePlayer?,
        p1: Double
    ): EconomyResponse {
        p0 ?: return missingAccountResponse()
        if (p1 < 0.0) return invalidAmountResponse(p1, p0)

        val balance = getBalance(p0)
        this[p0.uniqueId] = balance + p1
        return EconomyResponse(p1, getBalance(p0), ResponseType.SUCCESS, "Deposit successful")
    }
    override fun depositPlayer(
        p0: String?,
        p1: String?,
        p2: Double
    ): EconomyResponse = depositPlayer(Utility.resolvePlayer(p0), p2)
    override fun depositPlayer(
        p0: OfflinePlayer?,
        p1: String?,
        p2: Double
    ): EconomyResponse = depositPlayer(p0, p2)

    override fun createBank(p0: String?, p1: String?): EconomyResponse = bankNotSupported()
    override fun createBank(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse = bankNotSupported()
    override fun deleteBank(p0: String?): EconomyResponse = bankNotSupported()
    override fun bankBalance(p0: String?): EconomyResponse = bankNotSupported()
    override fun bankHas(p0: String?, p1: Double): EconomyResponse = bankNotSupported()
    override fun bankWithdraw(p0: String?, p1: Double): EconomyResponse = bankNotSupported()
    override fun bankDeposit(p0: String?, p1: Double): EconomyResponse = bankNotSupported()
    override fun isBankOwner(p0: String?, p1: String?): EconomyResponse = bankNotSupported()
    override fun isBankOwner(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse = bankNotSupported()
    override fun isBankMember(p0: String?, p1: String?): EconomyResponse = bankNotSupported()
    override fun isBankMember(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse = bankNotSupported()
    override fun getBanks(): List<String?> = emptyList()

    override fun createPlayerAccount(p0: String?): Boolean = createPlayerAccount(Utility.resolvePlayer(p0))
    override fun createPlayerAccount(p0: OfflinePlayer?): Boolean {
        p0 ?: return false
        if (!this().contains(p0.uniqueId)) {
            this[p0.uniqueId] = 0.0
        }
        return true
    }
    override fun createPlayerAccount(p0: String?, p1: String?): Boolean = createPlayerAccount(Utility.resolvePlayer(p0))
    override fun createPlayerAccount(p0: OfflinePlayer?, p1: String?): Boolean = createPlayerAccount(p0, p1)

    private fun invalidAmountResponse(amount: Double, player: OfflinePlayer): EconomyResponse {
        return EconomyResponse(amount, getBalance(player), ResponseType.FAILURE, "Amount must be positive")
    }

    private fun missingAccountResponse(): EconomyResponse {
        return EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player account not found")
    }

    private fun bankNotSupported(): EconomyResponse {
        return EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported")
    }

    fun initialize() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return

        val servicesManager = Bukkit.getServicesManager()
        val currentProvider = servicesManager.getRegistration(Economy::class.java)?.provider
        if (currentProvider is VaultEconomy) return

        servicesManager.register(Economy::class.java, this, Main.instance, ServicePriority.Normal)
        Main.instance.logger.info("Hooked into Vault!")
    }
}