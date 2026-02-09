package com.freetime.sdk.examples

import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal

/**
 * Example demonstrating how users can select which cryptocurrencies to accept
 * Shows flexible cryptocurrency selection in the app
 */
suspend fun main() {
    val sdk = FreetimePaymentSDK()
    
    println("=== Cryptocurrency Selection Example ===\n")
    
    // Example 1: Show available cryptocurrencies
    println("1. Verfügbare Kryptowährungen:")
    val availableCryptos = sdk.getAvailableCryptocurrencies()
    availableCryptos.forEachIndexed { index, coinType ->
        println("   ${index + 1}. ${coinType.coinName} (${coinType.symbol})")
    }
    println()
    
    // Example 2: User selects which cryptocurrencies to accept
    println("2. Benutzer wählt akzeptierte Kryptowährungen:")
    
    // Simulate user selection (in real app, this would come from UI)
    val userSelection = setOf(
        CoinType.BITCOIN,
        CoinType.ETHEREUM,
        CoinType.LITECOIN
    )
    
    println("   Benutzer akzeptiert:")
    userSelection.forEach { coinType ->
        println("     ✓ ${coinType.coinName}")
    }
    
    // Set accepted cryptocurrencies
    sdk.setAcceptedCryptocurrencies(userSelection)
    println("   ✓ Akzeptierte Kryptowährungen gespeichert")
    println()
    
    // Example 3: Configure wallet addresses for selected cryptocurrencies
    println("3. Wallet-Adressen für ausgewählte Kryptowährungen konfigurieren:")
    
    val walletAddresses = mapOf(
        CoinType.BITCOIN to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        CoinType.ETHEREUM to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
        CoinType.LITECOIN to "LXqRWTcQjg6FpT4b6c3u8s9WvK5hX2zY7A"
    )
    
    walletAddresses.forEach { (coinType, address) ->
        try {
            sdk.setUserWalletAddress(
                coinType = coinType,
                address = address,
                name = "Meine ${coinType.coinName} Wallet",
                isAccepted = true
            )
            println("   ✓ ${coinType.coinName}: $address")
        } catch (e: Exception) {
            println("   ✗ ${coinType.coinName}: ${e.message}")
        }
    }
    println()
    
    // Example 4: Show current configuration status
    println("4. Aktuelle Konfigurationsstatus:")
    
    val acceptedCryptos = sdk.getAcceptedCryptocurrencies()
    println("   Akzeptierte Kryptowährungen:")
    acceptedCryptos.forEach { coinType ->
        val hasWallet = sdk.hasAcceptedWallet(coinType)
        val address = sdk.getUserWalletAddress(coinType)
        println("     ${coinType.coinName}: ${if (hasWallet) "✓ Konfiguriert" else "✗ Fehlend"}")
        if (hasWallet && address != null) {
            println("       Adresse: $address")
        }
    }
    println()
    
    // Example 5: Show supported payment options
    println("5. Unterstützte Zahlungsoptionen:")
    val supportedOptions = sdk.getSupportedPaymentOptions()
    if (supportedOptions.isEmpty()) {
        println("   Keine Zahlungsoptionen verfügbar")
    } else {
        println("   Der Benutzer kann Zahlungen empfangen in:")
        supportedOptions.forEach { coinType ->
            val address = sdk.getUserWalletAddress(coinType)
            println("     ${coinType.coinName}: $address")
        }
    }
    println()
    
    // Example 6: Create payment gateways for accepted cryptocurrencies
    println("6. Payment Gateways für akzeptierte Kryptowährungen:")
    
    try {
        sdk.validateHasAcceptedWallets()
        
        val gateways = sdk.createUsdPaymentGatewaysForAccepted()
        println("   Erstellt ${gateways.size} Payment Gateways:")
        
        gateways.forEach { (coinType, gateway) ->
            try {
                val payment = gateway.createUsdPaymentRequest(
                    usdAmount = BigDecimal("100.00"),
                    customerReference = "Kunde-${coinType.symbol}",
                    description = "Produkt für ${coinType.coinName}"
                )
                
                println("     ${coinType.coinName}:")
                println("       Zahlbetrag: ${payment.cryptoAmount} ${coinType.symbol}")
                println("       USD-Äquivalent: $${payment.usdAmount}")
                println("       Ziel-Adresse: ${payment.merchantWalletAddress}")
                println()
                
            } catch (e: Exception) {
                println("     ${coinType.coinName}: Fehler - ${e.message}")
            }
        }
        
    } catch (e: Exception) {
        println("   ✗ Fehler bei Gateway-Erstellung: ${e.message}")
    }
    
    // Example 7: Dynamic cryptocurrency management
    println("7. Dynamische Kryptowährungsverwaltung:")
    
    // Add a new cryptocurrency
    println("   Füge Dogecoin hinzu:")
    try {
        sdk.setUserWalletAddress(
            coinType = CoinType.DOGECOIN,
            address = "D7xeJ8Q3vZxvKZkZJZKZKZKZKZKZKZKZK",
            name = "Meine Dogecoin Wallet",
            isAccepted = true
        )
        println("     ✓ Dogecoin hinzugefügt")
    } catch (e: Exception) {
        println("     ✗ Fehler: ${e.message}")
    }
    
    // Remove a cryptocurrency
    println("   Entferne Litecoin:")
    sdk.removeAcceptedCryptocurrency(CoinType.LITECOIN)
    println("     ✓ Litecoin entfernt")
    
    // Show updated status
    println("   Aktualisierter Status:")
    val updatedAccepted = sdk.getAcceptedCryptocurrencies()
    updatedAccepted.forEach { coinType ->
        println("     ✓ ${coinType.coinName}")
    }
    println()
    
    // Example 8: Error handling with non-accepted cryptocurrencies
    println("8. Fehlerbehandlung mit nicht akzeptierten Kryptowährungen:")
    
    // Try to create gateway for non-accepted crypto
    try {
        val gateway = sdk.createUsdPaymentGateway(
            merchantWalletAddress = "bitcoincash:qzf8zrhvphvh7h2d3e2jv6p3y8z8c8f5k",
            merchantCoinType = CoinType.BITCOIN_CASH
        )
        println("   ✗ Sollte fehlschlagen, aber hat funktioniert")
    } catch (e: Exception) {
        println("   ✓ Korrekt abgelehnt: ${e.message}")
    }
    
    // Try to configure wallet for non-accepted crypto
    try {
        sdk.setUserWalletAddress(
            coinType = CoinType.BITCOIN_CASH,
            address = "bitcoincash:qzf8zrhvphvh7h2d3e2jv6p3y8z8c8f5k",
            isAccepted = false
        )
        println("   ✓ Bitcoin Cash konfiguriert aber nicht akzeptiert")
        
        // Try to create gateway - should fail
        try {
            val gateway = sdk.createUsdPaymentGateway(
                merchantWalletAddress = "bitcoincash:qzf8zrhvphvh7h2d3e2jv6p3y8z8c8f5k",
                merchantCoinType = CoinType.BITCOIN_CASH
            )
            println("   ✗ Sollte fehlschlagen, aber hat funktioniert")
        } catch (e: Exception) {
            println("   ✓ Korrekt abgelehnt: ${e.message}")
        }
    } catch (e: Exception) {
        println("   ✗ Fehler: ${e.message}")
    }
    println()
    
    // Example 9: Practical app implementation
    println("9. Praktische App-Implementierung:")
    println("   // Kryptowährungs-Auswahl-Dialog")
    println("   fun showCryptocurrencySelectionDialog() {")
    println("       val availableCryptos = sdk.getAvailableCryptocurrencies()")
    println("       val selectedCryptos = showMultiChoiceDialog(")
    println("           title = \"Wählen Sie akzeptierte Kryptowährungen\",")
    println("           items = availableCryptos.map { it.coinName }")
    println("       )")
    println("       ")
    println("       val selectedCoinTypes = selectedCryptos.map { index ->")
    println("           availableCryptos[index]")
    println("       }.toSet()")
    println("       ")
    println("       sdk.setAcceptedCryptocurrencies(selectedCoinTypes)")
    println("       ")
    println("       // Wallet-Adressen für ausgewählte Kryptos konfigurieren")
    println("       selectedCoinTypes.forEach { coinType ->")
    println("           val address = showAddressInputDialog(coinType)")
    println("           if (address.isNotEmpty()) {")
    println("               sdk.setUserWalletAddress(coinType, address)")
    println("           }")
    println("       }")
    println("   }")
    println()
    println("   // Zahlungsoptionen anzeigen")
    println("   fun showPaymentOptions() {")
    println("       val supportedOptions = sdk.getSupportedPaymentOptions()")
    println("       paymentOptionsRecyclerView.adapter = PaymentOptionsAdapter(")
    println("           cryptocurrencies = supportedOptions")
    println("       )")
    println("   }")
    println()
    println("   // Zahlung verarbeiten")
    println("   fun processPayment(selectedCrypto: CoinType, amount: BigDecimal) {")
    println("       try {")
    println("           val address = sdk.getUserWalletAddress(selectedCrypto)")
    println("           if (address != null) {")
    println("               val gateway = sdk.createUsdPaymentGateway(address, selectedCrypto)")
    println("               val payment = gateway.createUsdPaymentRequest(amount)")
    println("               showPaymentDetails(payment)")
    println("           }")
    println("       } catch (e: Exception) {")
    println("           showError(e.message)")
    println("       }")
    println("   }")
    
    println("\n=== Cryptocurrency Selection Example Complete ===")
}

/**
 * Helper class for managing cryptocurrency selection in apps
 */
object CryptocurrencySelectionManager {
    
    /**
     * Show cryptocurrency selection UI (mock implementation)
     */
    fun showCryptocurrencySelection(sdk: FreetimePaymentSDK): Set<CoinType> {
        val available = sdk.getAvailableCryptocurrencies()
        val currentlyAccepted = sdk.getAcceptedCryptocurrencies()
        
        println("Verfügbare Kryptowährungen:")
        available.forEachIndexed { index, coinType ->
            val isAccepted = currentlyAccepted.contains(coinType)
            val status = if (isAccepted) "[✓]" else "[ ]"
            println("  ${index + 1}. $status ${coinType.coinName} (${coinType.symbol})")
        }
        
        // In real app, this would show a UI dialog
        // For demo, return a sample selection
        return setOf(CoinType.BITCOIN, CoinType.ETHEREUM)
    }
    
    /**
     * Get selection summary for display
     */
    fun getSelectionSummary(sdk: FreetimePaymentSDK): String {
        val accepted = sdk.getAcceptedCryptocurrencies()
        val configured = accepted.filter { sdk.hasAcceptedWallet(it) }
        
        return when {
            accepted.isEmpty() -> "Keine Kryptowährungen ausgewählt"
            configured.isEmpty() -> "${accepted.size} ausgewählt, aber keine konfiguriert"
            configured.size == accepted.size -> "${configured.size} Kryptowährungen bereit"
            else -> "${configured.size} von ${accepted.size} Kryptowährungen bereit"
        }
    }
    
    /**
     * Get configuration progress for selected cryptocurrencies
     */
    fun getConfigurationProgress(sdk: FreetimePaymentSDK): Float {
        val accepted = sdk.getAcceptedCryptocurrencies()
        if (accepted.isEmpty()) return 0f
        
        val configured = accepted.count { sdk.hasAcceptedWallet(it) }
        return configured.toFloat() / accepted.size * 100f
    }
    
    /**
     * Validate cryptocurrency selection
     */
    fun validateSelection(sdk: FreetimePaymentSDK): ValidationResult {
        val accepted = sdk.getAcceptedCryptocurrencies()
        val configured = accepted.filter { sdk.hasAcceptedWallet(it) }
        
        return when {
            accepted.isEmpty() -> ValidationResult(false, "Bitte wählen Sie mindestens eine Kryptowährung")
            configured.isEmpty() -> ValidationResult(false, "Bitte konfigurieren Sie Wallet-Adressen für ausgewählte Kryptowährungen")
            configured.size < accepted.size -> ValidationResult(false, "Einige ausgewählte Kryptowährungen sind nicht konfiguriert")
            else -> ValidationResult(true, "Alle ausgewählten Kryptowährungen sind bereit")
        }
    }
}

data class ValidationResult(val isValid: Boolean, val message: String)
