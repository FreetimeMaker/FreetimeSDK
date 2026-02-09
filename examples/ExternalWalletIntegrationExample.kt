package com.freetime.sdk.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.conversion.*
import java.math.BigDecimal

/**
 * Example demonstrating external wallet app integration for crypto payments
 * Shows how to let users choose their preferred wallet app
 */
suspend fun main() {
    val sdk = FreetimePaymentSDK()
    
    println("=== External Wallet Integration Example ===\n")
    
    // Example 1: Create USD payment with wallet selection
    println("1. USD-Zahlung mit Wallet-Auswahl erstellen:")
    
    val gateway = sdk.createUsdPaymentGateway(
        merchantWalletAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        merchantCoinType = CoinType.BITCOIN
    )
    
    val paymentWithWalletSelection = gateway.createUsdPaymentWithWalletSelection(
        usdAmount = BigDecimal("100.00"),
        customerReference = "Customer-12345",
        description = "Premium Product Purchase"
    )
    
    println("   Zahlungsdetails:")
    println("     ${paymentWithWalletSelection.getFormattedInfo()}")
    println()
    
    // Example 2: Show available wallet apps
    println("2. Verfügbare Wallet-Apps für Bitcoin:")
    val availableWallets = gateway.getAvailableWalletApps()
    
    availableWallets.forEachIndexed { index, wallet ->
        println("   ${index + 1}. ${wallet.name}")
        println("      Package: ${wallet.packageName}")
        println("      Unterstützte Coins: ${wallet.supportedCoins.map { it.symbol }.joinToString(", ")}")
        println("      Deep Link: ${wallet.deepLinkScheme}://")
        println()
    }
    
    // Example 3: Simulate wallet selection
    println("3. Wallet-Auswahl simulieren:")
    if (availableWallets.isNotEmpty()) {
        val selectedWallet = availableWallets.first() // In real app, user would choose
        val paymentWithSelectedWallet = paymentWithWalletSelection.selectWallet(selectedWallet)
        
        println("   Ausgewählte Wallet: ${selectedWallet.name}")
        println("   ${paymentWithSelectedWallet.getFormattedInfo()}")
        
        // Generate deep link
        val deepLink = paymentWithSelectedWallet.getPaymentDeepLink()
        println("   Deep Link: $deepLink")
        println()
    }
    
    // Example 4: Show wallet selection UI (mock)
    println("4. Wallet-Auswahl UI (Mock):")
    showWalletSelectionUI(paymentWithWalletSelection)
    
    // Example 5: Different cryptocurrencies
    println("5. Wallet-Apps für verschiedene Kryptowährungen:")
    
    val cryptocurrencies = listOf(
        CoinType.BITCOIN,
        CoinType.ETHEREUM,
        CoinType.LITECOIN,
        CoinType.DOGECOIN
    )
    
    cryptocurrencies.forEach { coinType ->
        println("   ${coinType.coinName} (${coinType.symbol}):")
        val wallets = ExternalWalletApp.getWalletsForCoin(coinType)
        wallets.forEach { wallet ->
            println("     ✓ ${wallet.name}")
        }
        if (wallets.isEmpty()) {
            println("     ✗ Keine Wallet-Apps verfügbar")
        }
        println()
    }
    
    // Example 6: Deep link generation for different wallets
    println("6. Deep Links für verschiedene Wallets:")
    
    val testAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
    val testAmount = BigDecimal("0.001")
    
    listOf(
        ExternalWalletApp.TRUST_WALLET,
        ExternalWalletApp.META_MASK,
        ExternalWalletApp.COINBASE_WALLET,
        ExternalWalletApp.BINANCE_WALLET
    ).forEach { wallet ->
        if (gateway.isWalletSupported(wallet)) {
            val deepLink = wallet.generatePaymentDeepLink(testAddress, testAmount, CoinType.BITCOIN)
            println("   ${wallet.name}:")
            println("     $deepLink")
        }
    }
    println()
    
    // Example 7: Practical app implementation
    println("7. Praktische App-Implementierung:")
    println("""
   // Wallet-Auswahl Dialog
   fun showWalletSelectionDialog(paymentRequest: UsdPaymentRequestWithWalletSelection) {
       val availableWallets = paymentRequest.availableWalletApps
       
       if (availableWallets.isEmpty()) {
           showNoWalletsAvailable()
           return
       }
       
       val dialog = WalletSelectionDialog(
           title = "Wählen Sie Ihre Wallet-App",
           wallets = availableWallets,
           onWalletSelected = { selectedWallet ->
               val updatedPayment = paymentRequest.selectWallet(selectedWallet)
               val deepLink = updatedPayment.getPaymentDeepLink()
               
               // Öffne Wallet-App mit Deep Link
               openDeepLink(deepLink)
               
               // Starte Zahlungs-Status-Überwachung
               startPaymentMonitoring(updatedPayment.usdPaymentRequest.id)
           },
           onInstallWallet = { wallet ->
               // Leite Nutzer zum App Store weiter
               openAppStore(wallet.packageName)
           }
       )
       
       dialog.show()
   }
   
   // Deep Link öffnen
   fun openDeepLink(deepLink: String) {
       try {
           val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
           startActivity(intent)
       } catch (e: Exception) {
           showError("Konnte Wallet-App nicht öffnen: ${e.message}")
       }
   }
   
   // App Store öffnen
   fun openAppStore(packageName: String) {
       try {
           val intent = Intent(Intent.ACTION_VIEW).apply {
               data = Uri.parse("market://details?id=$packageName")
           }
           startActivity(intent)
       } catch (e: Exception) {
           // Fallback zu Browser
           val intent = Intent(Intent.ACTION_VIEW).apply {
               data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
           }
           startActivity(intent)
       }
   }
   
   // Zahlungs-Status überwachen
   fun startPaymentMonitoring(paymentId: String) {
       // Starte Timer oder Coroutine zur Status-Überprüfung
       CoroutineScope(Dispatchers.IO).launch {
           while (true) {
               val status = gateway.checkUsdPaymentStatus(paymentId)
               updatePaymentUI(status)
               
               if (status == PaymentStatus.CONFIRMED) {
                   showPaymentSuccess()
                   break
               } else if (status == PaymentStatus.EXPIRED) {
                   showPaymentExpired()
                   break
               }
               
               delay(5000) // Alle 5 Sekunden prüfen
           }
       }
   }
    """.trimIndent())
    
    // Example 8: Error handling
    println("8. Fehlerbehandlung:")
    println("""
   // Fehlerbehandlung bei Wallet-Integration
   fun handleWalletPaymentErrors(paymentRequest: UsdPaymentRequestWithWalletSelection) {
       try {
           // Prüfe ob Wallets verfügbar
           if (paymentRequest.availableWalletApps.isEmpty()) {
               showErrorMessage("Keine unterstützten Wallet-Apps gefunden")
               return
           }
           
           // Prüfe ob gewählte Wallet installiert ist
           paymentRequest.selectedWalletApp?.let { selectedWallet ->
               if (!selectedWallet.isInstalled()) {
                   showInstallWalletDialog(selectedWallet)
                   return
               }
           }
           
           // Generiere Deep Link
           val deepLink = paymentRequest.getPaymentDeepLink()
           if (deepLink == null) {
               showErrorMessage("Deep Link konnte nicht generiert werden")
               return
           }
           
           // Öffne Wallet
           openDeepLink(deepLink)
           
       } catch (e: SecurityException) {
           showErrorMessage("Berechtigungen fehlen für Deep Link Öffnung")
       } catch (e: ActivityNotFoundException) {
           showErrorMessage("Keine App gefunden für diesen Deep Link")
       } catch (e: Exception) {
           showErrorMessage("Unerwarteter Fehler: ${e.message}")
       }
   }
    """.trimIndent())
    
    println("\n=== External Wallet Integration Example Complete ===")
}

/**
 * Mock wallet selection UI
 */
fun showWalletSelectionUI(paymentWithWalletSelection: UsdPaymentRequestWithWalletSelection) {
    println("   Wallet-Auswahl Dialog:")
    println("   ┌─────────────────────────────────────────┐")
    println("   │  Wählen Sie Ihre Wallet-App         │")
    println("   ├─────────────────────────────────────────┤")
    
    paymentWithWalletSelection.availableWalletApps.forEachIndexed { index, wallet ->
        val status = if (wallet.isInstalled()) "✓" else "✗"
        println("   │  ${index + 1}. $status ${wallet.name.padEnd(20)} │")
    }
    
    println("   ├─────────────────────────────────────────┤")
    println("   │  0. Abbrechen                        │")
    println("   └─────────────────────────────────────────┘")
    println()
    println("   Hinweis: ✗ = Nicht installiert, wird zum Download weitergeleitet")
    println()
}
