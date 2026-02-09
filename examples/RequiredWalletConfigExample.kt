package com.freetime.sdk.examples

import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal

/**
 * Example demonstrating required wallet configuration for ALL cryptocurrencies
 * Shows how users must configure addresses for every supported cryptocurrency
 */
suspend fun main() {
    val sdk = FreetimePaymentSDK()
    
    println("=== Required Wallet Configuration for ALL Cryptocurrencies ===\n")
    
    // Example 1: Check initial state
    println("1. Initial wallet configuration status:")
    val allConfigured = sdk.areAllRequiredWalletsConfigured()
    println("   Alle Wallets konfiguriert: $allConfigured")
    
    val missingWallets = sdk.getMissingWalletConfigurations()
    if (missingWallets.isNotEmpty()) {
        println("   Fehlende Wallets:")
        missingWallets.forEach { coinType ->
            println("     - ${coinType.coinName}")
        }
    }
    println()
    
    // Example 2: Configure all required wallets at once
    println("2. Alle erforderlichen Wallet-Adressen konfigurieren:")
    
    val allWalletAddresses = mapOf(
        CoinType.BITCOIN to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        CoinType.ETHEREUM to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
        CoinType.LITECOIN to "LXqRWTcQjg6FpT4b6c3u8s9WvK5hX2zY7A",
        CoinType.BITCOIN_CASH to "bitcoincash:qzf8zrhvphvh7h2d3e2jv6p3y8z8c8f5k",
        CoinType.DOGECOIN to "D7xeJ8Q3vZxvKZkZJZKZKZKZKZKZKZKZK",
        CoinType.SOLANA to "11111111111111111111111111111112",
        CoinType.POLYGON to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db46",
        CoinType.BINANCE_COIN to "bnb1grpf0955h0ykzq3ar5nmum7y6gdfl6lxfn46h2",
        CoinType.TRON to "TLa2f6VPqDgH67qQr3iMDx41MDQ6NfhBdo"
    )
    
    try {
        sdk.setAllRequiredWalletAddresses(allWalletAddresses)
        println("   ✓ Alle Wallet-Adressen erfolgreich konfiguriert!")
        
        allWalletAddresses.forEach { (coinType, address) ->
            println("     ${coinType.coinName}: $address")
        }
    } catch (e: Exception) {
        println("   ✗ Fehler bei der Konfiguration: ${e.message}")
    }
    println()
    
    // Example 3: Verify all wallets are configured
    println("3. Überprüfung der Konfiguration:")
    val nowConfigured = sdk.areAllRequiredWalletsConfigured()
    println("   Alle Wallets konfiguriert: $nowConfigured")
    
    val nowMissing = sdk.getMissingWalletConfigurations()
    if (nowMissing.isEmpty()) {
        println("   ✓ Keine fehlenden Wallets")
    } else {
        println("   Immer noch fehlend:")
        nowMissing.forEach { coinType ->
            println("     - ${coinType.coinName}")
        }
    }
    println()
    
    // Example 4: Show all configured wallets
    println("4. Alle konfigurierten Wallets:")
    val allWallets = sdk.getAllUserWallets()
    allWallets.forEach { (coinType, config) ->
        println("   ${coinType.coinName}:")
        println("     Adresse: ${config.address}")
        println("     Name: ${config.name}")
        println("     Aktiv: ${config.isActive}")
    }
    println()
    
    // Example 5: Use configured wallets for payments
    println("5. Zahlungen mit konfigurierten Wallets:")
    
    // Validate all wallets before proceeding
    try {
        sdk.validateAllRequiredWallets()
        println("   ✓ Alle Wallets validiert - Zahlungen können empfangen werden")
        
        // Create payment gateways for different cryptocurrencies
        val supportedCoins = listOf(
            CoinType.BITCOIN,
            CoinType.ETHEREUM,
            CoinType.LITECOIN,
            CoinType.DOGECOIN
        )
        
        supportedCoins.forEach { coinType ->
            val userAddress = sdk.getUserWalletAddress(coinType)
            if (userAddress != null) {
                try {
                    val gateway = sdk.createUsdPaymentGateway(
                        merchantWalletAddress = userAddress,
                        merchantCoinType = coinType
                    )
                    
                    val payment = gateway.createUsdPaymentRequest(
                        usdAmount = BigDecimal("100.00"),
                        customerReference = "Kunde-${coinType.symbol}",
                        description = "Produkt für ${coinType.coinName}"
                    )
                    
                    println("     ${coinType.coinName}:")
                    println("       Zahlbetrag: ${payment.cryptoAmount} ${coinType.symbol}")
                    println("       USD-Äquivalent: $${payment.usdAmount}")
                    println("       Ziel-Adresse: ${userAddress}")
                    println()
                    
                } catch (e: Exception) {
                    println("     ${coinType.coinName}: Fehler bei Gateway-Erstellung - ${e.message}")
                }
            }
        }
        
    } catch (e: Exception) {
        println("   ✗ Validierung fehlgeschlagen: ${e.message}")
    }
    
    // Example 6: Error handling with incomplete configuration
    println("6. Fehlerbehandlung bei unvollständiger Konfiguration:")
    
    // Create a new SDK instance to demonstrate error
    val testSdk = FreetimePaymentSDK()
    
    // Configure only some wallets
    val incompleteWallets = mapOf(
        CoinType.BITCOIN to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        CoinType.ETHEREUM to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
        // Missing all other required wallets
    )
    
    try {
        testSdk.setAllRequiredWalletAddresses(incompleteWallets)
        println("   ✗ Sollte fehlschlagen, aber hat funktioniert")
    } catch (e: Exception) {
        println("   ✓ Korrekt abgelehnt: ${e.message}")
    }
    
    // Try to validate incomplete configuration
    try {
        testSdk.validateAllRequiredWallets()
        println("   ✗ Validierung sollte fehlschlagen")
    } catch (e: Exception) {
        println("   ✓ Validierung korrekt abgelehnt: ${e.message}")
        
        val missing = testSdk.getMissingWalletConfigurations()
        println("   Fehlende Wallets (${missing.size}):")
        missing.forEach { coinType ->
            println("     - ${coinType.coinName}")
        }
    }
    println()
    
    // Example 7: Practical app implementation
    println("7. Praktische App-Implementierung:")
    println("   // Benutzer-Interface für Wallet-Konfiguration")
    println("   fun showWalletConfigurationDialog() {")
    println("       val walletAddresses = mutableMapOf<CoinType, String>()")
    println("       ")
    println("       // Für jede unterstützte Kryptowährung:")
    println("       CoinType.values().forEach { coinType ->")
    println("           val address = showAddressInputDialog(coinType)")
    println("           if (address.isNotEmpty()) {")
    println("               walletAddresses[coinType] = address")
    println("           }")
    println("       }")
    println("       ")
    println("       // Alle Adressen auf einmal konfigurieren")
    println("       try {")
    println("           sdk.setAllRequiredWalletAddresses(walletAddresses)")
    println("           // Erfolg - App kann Zahlungen empfangen")
    println("           enablePaymentFeatures()")
    println("       } catch (e: Exception) {")
    println("           // Fehler - Benutzer muss fehlende Adressen eingeben")
    println("           showError(e.message)")
    println("       }")
    println("   }")
    println()
    println("   // Vor Zahlungen immer validieren")
    println("   fun processPayment(amount: BigDecimal) {")
    println("       try {")
    println("           sdk.validateAllRequiredWallets()")
    println("           // Fortfahren mit Zahlungsverarbeitung")
    println("           val gateway = createPaymentGateway()")
    println("           val payment = gateway.createUsdPaymentRequest(amount)")
    println("           // ...")
    println("       } catch (e: Exception) {")
    println("           // Wallet-Konfiguration unvollständig")
    println("           showWalletConfigurationDialog()")
    println("       }")
    println("   }")
    
    println("\n=== Required Wallet Configuration Example Complete ===")
}

/**
 * Helper class for managing required wallet configurations
 */
object RequiredWalletManager {
    
    /**
     * Check if app is ready for payments
     */
    fun isAppReadyForPayments(sdk: FreetimePaymentSDK): Boolean {
        return try {
            sdk.validateAllRequiredWallets()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get configuration status for all coins
     */
    fun getConfigurationStatus(sdk: FreetimePaymentSDK): Map<CoinType, Boolean> {
        val status = mutableMapOf<CoinType, Boolean>()
        val supportedCoins = listOf(
            CoinType.BITCOIN,
            CoinType.ETHEREUM,
            CoinType.LITECOIN,
            CoinType.BITCOIN_CASH,
            CoinType.DOGECOIN,
            CoinType.SOLANA,
            CoinType.POLYGON,
            CoinType.BINANCE_COIN,
            CoinType.TRON
        )
        
        supportedCoins.forEach { coinType ->
            status[coinType] = sdk.hasUserWallet(coinType)
        }
        
        return status
    }
    
    /**
     * Get completion percentage
     */
    fun getConfigurationProgress(sdk: FreetimePaymentSDK): Float {
        val supportedCoins = listOf(
            CoinType.BITCOIN,
            CoinType.ETHEREUM,
            CoinType.LITECOIN,
            CoinType.BITCOIN_CASH,
            CoinType.DOGECOIN,
            CoinType.SOLANA,
            CoinType.POLYGON,
            CoinType.BINANCE_COIN,
            CoinType.TRON
        )
        
        val configured = supportedCoins.count { sdk.hasUserWallet(it) }
        return configured.toFloat() / supportedCoins.size * 100f
    }
    
    /**
     * Generate configuration summary
     */
    fun getConfigurationSummary(sdk: FreetimePaymentSDK): String {
        val progress = getConfigurationProgress(sdk)
        val missing = sdk.getMissingWalletConfigurations()
        
        return if (missing.isEmpty()) {
            "Alle Wallets konfiguriert ($progress%)"
        } else {
            "Fortschritt: ${progress.toInt()}% - Fehlend: ${missing.joinToString { it.coinName }}"
        }
    }
}
