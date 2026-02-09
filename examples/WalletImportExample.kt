package com.freetime.sdk.examples

import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal

/**
 * Example demonstrating wallet import functionality
 * Shows how users can import their existing wallet addresses into the app
 */
suspend fun main() {
    val sdk = FreetimePaymentSDK()
    
    println("=== Wallet Import Example ===\n")
    
    // Example 1: Import Bitcoin wallet by address (watch-only)
    println("1. Import Bitcoin wallet by address (watch-only):")
    try {
        val bitcoinAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        val importedBitcoinWallet = sdk.importWalletByAddress(
            address = bitcoinAddress,
            coinType = CoinType.BITCOIN,
            name = "My Imported Bitcoin Wallet"
        )
        
        println("   ✓ Imported Bitcoin wallet:")
        println("     Address: ${importedBitcoinWallet.address}")
        println("     Name: ${importedBitcoinWallet.name}")
        println("     Type: ${importedBitcoinWallet.coinType.coinName}")
        println("     Note: This is a watch-only wallet (can receive and check balance)")
        
        // Check balance
        val balance = sdk.getBalance(importedBitcoinWallet.address)
        println("     Balance: $balance BTC\n")
        
    } catch (e: Exception) {
        println("   ✗ Error importing Bitcoin wallet: ${e.message}\n")
    }
    
    // Example 2: Import Ethereum wallet by address (watch-only)
    println("2. Import Ethereum wallet by address (watch-only):")
    try {
        val ethereumAddress = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
        val importedEthereumWallet = sdk.importWalletByAddress(
            address = ethereumAddress,
            coinType = CoinType.ETHEREUM,
            name = "My Imported Ethereum Wallet"
        )
        
        println("   ✓ Imported Ethereum wallet:")
        println("     Address: ${importedEthereumWallet.address}")
        println("     Name: ${importedEthereumWallet.name}")
        println("     Type: ${importedEthereumWallet.coinType.coinName}")
        println("     Note: This is a watch-only wallet (can receive and check balance)")
        
        // Check balance
        val balance = sdk.getBalance(importedEthereumWallet.address)
        println("     Balance: $balance ETH\n")
        
    } catch (e: Exception) {
        println("   ✗ Error importing Ethereum wallet: ${e.message}\n")
    }
    
    // Example 3: Import wallet with private key (full control)
    println("3. Import Bitcoin wallet with private key (full control):")
    try {
        val bitcoinAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        val privateKey = "L1aW4aubDFB7yfras2S1mN3bHEg1vZgUf8YYBvUj6XwJXw2v7M6K" // Example private key
        
        val importedBitcoinWalletWithKey = sdk.importWalletWithPrivateKey(
            address = bitcoinAddress,
            privateKey = privateKey,
            coinType = CoinType.BITCOIN,
            name = "My Full Bitcoin Wallet"
        )
        
        println("   ✓ Imported Bitcoin wallet with private key:")
        println("     Address: ${importedBitcoinWalletWithKey.address}")
        println("     Name: ${importedBitcoinWalletWithKey.name}")
        println("     Type: ${importedBitcoinWalletWithKey.coinType.coinName}")
        println("     Note: This wallet has full control (can send transactions)")
        
        // Check balance
        val balance = sdk.getBalance(importedBitcoinWalletWithKey.address)
        println("     Balance: $balance BTC")
        
        // Example of sending transaction (if balance > 0)
        if (balance > BigDecimal.ZERO) {
            println("   → This wallet can send transactions")
            // Uncomment to test sending (requires actual balance)
            /*
            val recipientAddress = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"
            val amount = BigDecimal("0.001")
            
            val txResult = sdk.send(
                fromAddress = importedBitcoinWalletWithKey.address,
                toAddress = recipientAddress,
                amount = amount,
                coinType = CoinType.BITCOIN
            )
            
            println("   ✓ Transaction created: ${txResult.transaction.id}")
            println("   ✓ Fee breakdown: ${txResult.feeBreakdown.getFormattedBreakdown()}")
            */
        } else {
            println("   → No balance available for sending transactions")
        }
        println()
        
    } catch (e: Exception) {
        println("   ✗ Error importing Bitcoin wallet with private key: ${e.message}\n")
    }
    
    // Example 4: Import Ethereum wallet with private key (full control)
    println("4. Import Ethereum wallet with private key (full control):")
    try {
        val ethereumAddress = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
        val privateKey = "0x0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef" // Example private key
        
        val importedEthereumWalletWithKey = sdk.importWalletWithPrivateKey(
            address = ethereumAddress,
            privateKey = privateKey,
            coinType = CoinType.ETHEREUM,
            name = "My Full Ethereum Wallet"
        )
        
        println("   ✓ Imported Ethereum wallet with private key:")
        println("     Address: ${importedEthereumWalletWithKey.address}")
        println("     Name: ${importedEthereumWalletWithKey.name}")
        println("     Type: ${importedEthereumWalletWithKey.coinType.coinName}")
        println("     Note: This wallet has full control (can send transactions)")
        
        // Check balance
        val balance = sdk.getBalance(importedEthereumWalletWithKey.address)
        println("     Balance: $balance ETH")
        
        // Example of sending transaction (if balance > 0)
        if (balance > BigDecimal.ZERO) {
            println("   → This wallet can send transactions")
            // Uncomment to test sending (requires actual balance)
            /*
            val recipientAddress = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db46"
            val amount = BigDecimal("0.01")
            
            val txResult = sdk.send(
                fromAddress = importedEthereumWalletWithKey.address,
                toAddress = recipientAddress,
                amount = amount,
                coinType = CoinType.ETHEREUM
            )
            
            println("   ✓ Transaction created: ${txResult.transaction.id}")
            println("   ✓ Fee breakdown: ${txResult.feeBreakdown.getFormattedBreakdown()}")
            */
        } else {
            println("   → No balance available for sending transactions")
        }
        println()
        
    } catch (e: Exception) {
        println("   ✗ Error importing Ethereum wallet with private key: ${e.message}\n")
    }
    
    // Example 5: Address validation
    println("5. Address validation examples:")
    
    val testAddresses = mapOf(
        "Bitcoin (valid)" to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        "Bitcoin (invalid)" to "1InvalidBitcoinAddress",
        "Ethereum (valid)" to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
        "Ethereum (invalid)" to "0xInvalidEthAddress",
        "Ethereum (wrong length)" to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4D"
    )
    
    testAddresses.forEach { (description, address) ->
        val coinType = if (description.contains("Bitcoin")) CoinType.BITCOIN else CoinType.ETHEREUM
        val isValid = sdk.validateAddress(address, coinType)
        println("   $description: $address → ${if (isValid) "✓ Valid" else "✗ Invalid"}")
    }
    
    println()
    
    // Example 6: List all imported wallets
    println("6. All imported wallets:")
    val allWallets = sdk.getAllWallets()
    if (allWallets.isEmpty()) {
        println("   No wallets imported yet")
    } else {
        allWallets.forEachIndexed { index, wallet ->
            println("   ${index + 1}. ${wallet.name}")
            println("      Address: ${wallet.address}")
            println("      Type: ${wallet.coinType.coinName}")
            println("      Has Private Key: ${wallet.privateKey != null}")
        }
    }
    
    println("\n=== Wallet Import Example Complete ===")
}

/**
 * Helper function to demonstrate user input for wallet import
 * In a real app, this would be called from UI input fields
 */
suspend fun importWalletFromUserInput(
    sdk: FreetimePaymentSDK,
    address: String,
    coinType: CoinType,
    privateKey: String? = null,
    walletName: String? = null
): Boolean {
    return try {
        // Validate address first
        if (!sdk.validateAddress(address, coinType)) {
            println("Invalid address format for $coinType")
            return false
        }
        
        // Import wallet
        val wallet = if (privateKey != null) {
            sdk.importWalletWithPrivateKey(address, privateKey, coinType, walletName)
        } else {
            sdk.importWalletByAddress(address, coinType, walletName)
        }
        
        println("Successfully imported wallet: ${wallet.address}")
        true
    } catch (e: Exception) {
        println("Failed to import wallet: ${e.message}")
        false
    }
}
