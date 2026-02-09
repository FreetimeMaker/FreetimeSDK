package com.freetime.sdk.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.gateway.*
import java.math.BigDecimal

/**
 * Beispiel für die Verwendung des Payment Gateway zur automatischen Zahlungsweiterleitung
 */
class PaymentGatewayExample {
    
    private val sdk = FreetimePaymentSDK()
    
    // Händler Wallet-Adresse (im Code fest definiert)
    private val merchantBitcoinWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
    private val merchantEthereumWallet = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
    
    suspend fun demonstratePaymentGateway() {
        println("=== Payment Gateway Demo ===\n")
        
        // 1. Payment Gateway für Bitcoin initialisieren
        println("1. Initialisiere Payment Gateway...")
        val bitcoinGateway = PaymentGateway(
            sdk = sdk,
            merchantWalletAddress = merchantBitcoinWallet,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // 2. Payment Processor für automatische Verarbeitung starten
        println("2. Starte Payment Processor...")
        val processor = PaymentProcessor(bitcoinGateway)
        processor.addPaymentListener(LoggingPaymentListener())
        processor.startProcessing()
        
        // 3. Zahlungsanfrage für Kunden erstellen (Beispiel: App stellt eigenes Wallet bereit)
        println("3. Erstelle Zahlungsanfrage...")
        // App/Benutzer könnte ein bestehendes Wallet bereitstellen; hier demonstrativ per SDK erzeugt
        val customerProvidedWallet = sdk.createWallet(CoinType.BITCOIN, "Kunden-Wallet-1")
        val paymentRequest = bitcoinGateway.createPaymentAddress(
            amount = BigDecimal("0.001"),
            customerReference = "Kunde-12345",
            description = "Produkt #ABC-123",
            providedWallet = customerProvidedWallet
        )
        
        println("Zahlungsanfrage erstellt:")
        println("- ID: ${paymentRequest.id}")
        println("- Kundenadresse: ${paymentRequest.customerAddress}")
        println("- Betrag: ${paymentRequest.amount} ${paymentRequest.coinType.symbol}")
        println("- Händleradresse: ${paymentRequest.merchantAddress}")
        println("- Gültig bis: ${paymentRequest.expiresAt}")
        println()
        
        // 4. Zahlungsstatus überwachen
        println("4. Überwache Zahlungsstatus...")
        var checkCount = 0
        val maxChecks = 10
        
        while (checkCount < maxChecks) {
            val status = bitcoinGateway.checkPaymentStatus(paymentRequest.id)
            val details = bitcoinGateway.getPaymentDetails(paymentRequest.id)
            
            println("Check ${checkCount + 1}: Status = $status")
            details?.let {
                println("  Aktuelles Guthaben: ${it.currentBalance}")
                println("  Verbleibender Betrag: ${it.remainingAmount}")
            }
            
            when (status) {
                PaymentStatus.CONFIRMED -> {
                    println("✅ Zahlung erfolgreich bestätigt und weitergeleitet!")
                    break
                }
                PaymentStatus.EXPIRED -> {
                    println("⏰ Zahlung abgelaufen")
                    break
                }
                PaymentStatus.FORWARDING_FAILED -> {
                    println("❌ Weiterleitung fehlgeschlagen")
                    break
                }
                PaymentStatus.PENDING -> {
                    println("⏳ Warte auf Zahlung...")
                }
                else -> {
                    println("❓ Unbekannter Status")
                }
            }
            
            checkCount++
            kotlinx.coroutines.delay(2000) // 2 Sekunden warten
        }
        
        // 5. Processor stoppen
        processor.stopProcessing()
        
        // 6. Zusammenfassung anzeigen
        println("\n5. Zusammenfassung:")
        val finalDetails = bitcoinGateway.getPaymentDetails(paymentRequest.id)
        finalDetails?.let {
            println("Zahlungsdetails:")
            println("- Status: ${it.paymentRequest.status}")
            println("- Empfangen: ${it.currentBalance}")
            println("- Weiterleitungs-Hash: ${it.forwardedTxHash ?: "N/A"}")
        }
        
        println("\n=== Payment Gateway Demo abgeschlossen ===")
    }
    
    suspend fun demonstrateMultiplePayments() {
        println("\n=== Multi-Zahlungs Demo ===\n")
        
        val gateway = PaymentGateway(
            sdk = sdk,
            merchantWalletAddress = merchantBitcoinWallet,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // Erstelle mehrere Zahlungsanfragen
        val payments = mutableListOf<PaymentRequest>()
        
        repeat(3) { index ->
            val payment = gateway.createPaymentAddress(
                amount = BigDecimal("0.00${index + 1}"),
                customerReference = "Kunde-${1000 + index}",
                description = "Produkt #PROD-${index + 1}"
            )
            payments.add(payment)
            
            println("Zahlung ${index + 1}: ${payment.id}")
            println("  Adresse: ${payment.customerAddress}")
            println("  Betrag: ${payment.amount} BTC")
        }
        
        println("\nÜberwache alle Zahlungen...")
        
        // Überwache alle Zahlungen
        var allProcessed = false
        val maxIterations = 20
        var iteration = 0
        
        while (!allProcessed && iteration < maxIterations) {
            allProcessed = true
            
            for (payment in payments) {
                val status = gateway.checkPaymentStatus(payment.id)
                val details = gateway.getPaymentDetails(payment.id)
                
                print("${payment.id}: $status")
                details?.let {
                    print(" (${it.currentBalance}/${it.amount})")
                }
                print(" | ")
                
                if (status == PaymentStatus.PENDING) {
                    allProcessed = false
                }
            }
            
            println()
            iteration++
            kotlinx.coroutines.delay(3000) // 3 Sekunden warten
        }
        
        // Zeige finale Ergebnisse
        println("\nFinale Ergebnisse:")
        for (payment in payments) {
            val details = gateway.getPaymentDetails(payment.id)
            details?.let {
                println("${payment.id}: ${it.paymentRequest.status}")
                if (it.forwardedTxHash != null) {
                    println("  Weitergeleitet: ${it.forwardedTxHash}")
                }
            }
        }
        
        println("\n=== Multi-Zahlungs Demo abgeschlossen ===")
    }
    
    suspend fun demonstrateMerchantConfig() {
        println("\n=== Merchant Konfiguration Demo ===\n")
        
        // Verschiedene Merchant-Konfigurationen
        val bitcoinConfig = MerchantPresets.bitcoinConfig(merchantBitcoinWallet)
        val ethereumConfig = MerchantPresets.ethereumConfig(merchantEthereumWallet)
        
        println("Bitcoin Merchant Config:")
        println("- Wallet: ${bitcoinConfig.walletAddress}")
        println("- Coin: ${bitcoinConfig.coinType.symbol}")
        println("- Auto-Forward Threshold: ${bitcoinConfig.autoForwardThreshold} BTC")
        println("- Timeout: ${bitcoinConfig.maxPaymentTimeout / 60000} Minuten")
        
        println("\nEthereum Merchant Config:")
        println("- Wallet: ${ethereumConfig.walletAddress}")
        println("- Coin: ${ethereumConfig.coinType.symbol}")
        println("- Auto-Forward Threshold: ${ethereumConfig.autoForwardThreshold} ETH")
        println("- Timeout: ${ethereumConfig.maxPaymentTimeout / 60000} Minuten")
        
        // Erstelle Gateways mit verschiedenen Konfigurationen
        val btcGateway = PaymentGateway(
            sdk = sdk,
            merchantWalletAddress = bitcoinConfig.walletAddress,
            merchantCoinType = bitcoinConfig.coinType
        )
        
        val ethGateway = PaymentGateway(
            sdk = sdk,
            merchantWalletAddress = ethereumConfig.walletAddress,
            merchantCoinType = ethereumConfig.coinType
        )
        
        // Erstelle Testzahlungen
        val btcPayment = btcGateway.createPaymentAddress(
            amount = BigDecimal("0.0005"),
            customerReference = "BTC-Kunde"
        )
        
        val ethPayment = ethGateway.createPaymentAddress(
            amount = BigDecimal("0.01"),
            customerReference = "ETH-Kunde"
        )
        
        println("\nTestzahlungen erstellt:")
        println("BTC: ${btcPayment.customerAddress} für ${btcPayment.amount} BTC")
        println("ETH: ${ethPayment.customerAddress} für ${ethPayment.amount} ETH")
        
        println("\n=== Merchant Konfiguration Demo abgeschlossen ===")
    }
}

/**
 * Main Funktion für Payment Gateway Demo
 */
suspend fun main() {
    val example = PaymentGatewayExample()
    
    example.demonstratePaymentGateway()
    example.demonstrateMultiplePayments()
    example.demonstrateMerchantConfig()
}
