package com.freetime.sdk.examples

import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal

/**
 * Example demonstrating how users can configure their wallet addresses in the app
 * This allows users to enter their own wallet addresses for receiving payments
 */
suspend fun main() {
    val sdk = FreetimePaymentSDK()
    
    println("=== User Wallet Configuration Example ===\n")
    
    // Example 1: Simple wallet configuration
    println("1. Configure user wallet addresses:")
    
    // User sets their Bitcoin wallet address
    try {
        sdk.setUserWalletAddress(
            coinType = CoinType.BITCOIN,
            address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
            name = "Meine Bitcoin Wallet"
        )
        println("   ✓ Bitcoin wallet configured: 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa")
    } catch (e: Exception) {
        println("   ✗ Error configuring Bitcoin wallet: ${e.message}")
    }
    
    // User sets their Ethereum wallet address
    try {
        sdk.setUserWalletAddress(
            coinType = CoinType.ETHEREUM,
            address = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
            name = "Meine Ethereum Wallet"
        )
        println("   ✓ Ethereum wallet configured: 0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45")
    } catch (e: Exception) {
        println("   ✗ Error configuring Ethereum wallet: ${e.message}")
    }
    
    // User sets their Litecoin wallet address
    try {
        sdk.setUserWalletAddress(
            coinType = CoinType.LITECOIN,
            address = "LXqRWTcQjg6FpT4b6c3u8s9WvK5hX2zY7A",
            name = "Meine Litecoin Wallet"
        )
        println("   ✓ Litecoin wallet configured: LXqRWTcQjg6FpT4b6c3u8s9WvK5hX2zY7A")
    } catch (e: Exception) {
        println("   ✗ Error configuring Litecoin wallet: ${e.message}")
    }
    
    println()
    
    // Example 2: Check configured wallets
    println("2. Check configured wallets:")
    
    val supportedCoins = listOf(
        CoinType.BITCOIN,
        CoinType.ETHEREUM,
        CoinType.LITECOIN,
        CoinType.DOGECOIN,
        CoinType.BITCOIN_CASH
    )
    
    supportedCoins.forEach { coinType ->
        val hasWallet = sdk.hasUserWallet(coinType)
        val address = sdk.getUserWalletAddress(coinType)
        
        if (hasWallet && address != null) {
            println("   ✓ ${coinType.coinName}: $address")
        } else {
            println("   - ${coinType.coinName}: Nicht konfiguriert")
        }
    }
    
    println()
    
    // Example 3: Using configured wallets for payments
    println("3. Using configured wallets for payments:")
    
    // Create payment gateway using user's configured Bitcoin wallet
    val userBitcoinAddress = sdk.getUserWalletAddress(CoinType.BITCOIN)
    if (userBitcoinAddress != null) {
        println("   Bitcoin-Zahlungen empfangen auf: $userBitcoinAddress")
        
        // Example: Create USD payment gateway with user's wallet
        val usdGateway = sdk.createUsdPaymentGateway(
            merchantWalletAddress = userBitcoinAddress,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // Create a payment request
        val paymentRequest = usdGateway.createUsdPaymentRequest(
            usdAmount = BigDecimal("100.00"),
            customerReference = "Kunde-12345",
            description = "Produkt #ABC-123"
        )
        
        println("   ✓ USD-Zahlungsanfrage erstellt:")
        println("     Zahlbetrag: ${paymentRequest.cryptoAmount} ${paymentRequest.coinType.symbol}")
        println("     USD-Äquivalent: $${paymentRequest.usdAmount}")
        println("     Ziel-Adresse: ${userBitcoinAddress}")
    }
    
    // Example with Ethereum
    val userEthereumAddress = sdk.getUserWalletAddress(CoinType.ETHEREUM)
    if (userEthereumAddress != null) {
        println("   Ethereum-Zahlungen empfangen auf: $userEthereumAddress")
        
        // Create payment gateway with user's Ethereum wallet
        val ethGateway = sdk.createUsdPaymentGateway(
            merchantWalletAddress = userEthereumAddress,
            merchantCoinType = CoinType.ETHEREUM
        )
        
        val ethPayment = ethGateway.createUsdPaymentRequest(
            usdAmount = BigDecimal("250.00"),
            customerReference = "Kunde-67890",
            description = "Premium-Produkt"
        )
        
        println("   ✓ ETH-Zahlungsanfrage erstellt:")
        println("     Zahlbetrag: ${ethPayment.cryptoAmount} ${ethPayment.coinType.symbol}")
        println("     USD-Äquivalent: $${ethPayment.usdAmount}")
        println("     Ziel-Adresse: ${userEthereumAddress}")
    }
    
    println()
    
    // Example 4: Wallet configuration management
    println("4. Wallet configuration management:")
    
    val configManager = sdk.getUserWalletConfigManager()
    
    // Show all configured wallets
    println("   Alle konfigurierten Wallets:")
    configManager.getAllUserWallets().forEach { (coinType, config) ->
        println("     ${coinType.coinName}:")
        println("       Adresse: ${config.address}")
        println("       Name: ${config.name}")
        println("       Aktiv: ${config.isActive}")
    }
    
    println()
    
    // Example 5: Update wallet configuration
    println("5. Update wallet configuration:")
    
    // Update Bitcoin wallet name
    configManager.setUserWalletAddress(
        coinType = CoinType.BITCOIN,
        address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        name = "Meine Haupt-Bitcoin Wallet"
    )
    println("   ✓ Bitcoin Wallet Name aktualisiert")
    
    // Deactivate Litecoin wallet
    configManager.setUserWalletActive(CoinType.LITECOIN, false)
    println("   ✓ Litecoin Wallet deaktiviert")
    
    // Check active wallets
    println("   Aktive Wallets:")
    configManager.getActiveUserWallets().forEach { (coinType, config) ->
        println("     ✓ ${coinType.coinName}: ${config.address}")
    }
    
    println()
    
    // Example 6: Error handling with invalid addresses
    println("6. Error handling with invalid addresses:")
    
    val invalidAddresses = mapOf(
        CoinType.BITCOIN to "invalid-bitcoin-address",
        CoinType.ETHEREUM to "0xinvalid",
        CoinType.LITECOIN to "too-short"
    )
    
    invalidAddresses.forEach { (coinType, address) ->
        try {
            sdk.setUserWalletAddress(coinType, address, "Test Wallet")
            println("   ✗ $coinType: Sollte fehlschlagen, aber hat funktioniert")
        } catch (e: Exception) {
            println("   ✓ $coinType: Korrekt abgelehnt - ${e.message}")
        }
    }
    
    println()
    
    // Example 7: Practical usage in an app
    println("7. Praktische Anwendung in einer App:")
    println("   // Benutzer gibt seine Wallet-Adresse in der App ein:")
    println("   val userAddress = editText.text.toString()")
    println("   val selectedCoin = spinner.selectedItem as CoinType")
    println("   ")
    println("   // SDK konfigurieren:")
    println("   sdk.setUserWalletAddress(selectedCoin, userAddress)")
    println("   ")
    println("   // Zahlungen empfangen:")
    println("   val gateway = sdk.createUsdPaymentGateway(")
    println("       merchantWalletAddress = userAddress,")
    println("       merchantCoinType = selectedCoin")
    println("   )")
    println("   ")
    println("   // Zahlungsanfrage erstellen:")
    println("   val payment = gateway.createUsdPaymentRequest(")
    println("       usdAmount = BigDecimal(\"100.00\"),")
    println("       customerReference = \"customer-id\"")
    println("   )")
    
    println("\n=== User Wallet Configuration Example Complete ===")
}

/**
 * Helper function for app integration
 * Shows how to integrate user wallet configuration in a real app
 */
object UserWalletHelper {
    
    /**
     * Configure user wallet from UI input
     */
    suspend fun configureUserWalletFromInput(
        sdk: FreetimePaymentSDK,
        coinType: CoinType,
        addressInput: String,
        nameInput: String? = null
    ): Boolean {
        return try {
            // Trim and validate input
            val address = addressInput.trim()
            if (address.isEmpty()) {
                return false
            }
            
            // Configure wallet
            sdk.setUserWalletAddress(
                coinType = coinType,
                address = address,
                name = nameInput
            )
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get user-friendly wallet status
     */
    fun getWalletStatusText(sdk: FreetimePaymentSDK, coinType: CoinType): String {
        return if (sdk.hasUserWallet(coinType)) {
            val address = sdk.getUserWalletAddress(coinType)
            "Konfiguriert: ${address?.take(8)}...${address?.takeLast(8)}"
        } else {
            "Nicht konfiguriert"
        }
    }
    
    /**
     * Get all configured wallet addresses for display
     */
    fun getConfiguredWalletsForDisplay(sdk: FreetimePaymentSDK): List<String> {
        val configManager = sdk.getUserWalletConfigManager()
        return configManager.getActiveUserWallets().map { (coinType, config) ->
            "${coinType.coinName}: ${config.address}"
        }
    }
}
