package com.freetime.sdk.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.conversion.*
import java.math.BigDecimal

/**
 * Beispiel für Produktions-USD-Zahlungen mit erweiterten Sicherheitsfeatures
 */
class ProductionUsdPaymentExample {
    
    private val sdk = FreetimePaymentSDK()
    
    // Händler Wallet-Adresse (im Code fest definiert)
    private val merchantBitcoinWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
    private val merchantEthereumWallet = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
    
    suspend fun demonstrateProductionUsdPayments() {
        println("=== Production USD Payment Gateway Demo ===\n")
        
        // 1. Production USD Payment Gateway mit Konfiguration initialisieren
        println("1. Initialisiere Production USD Payment Gateway...")
        val config = PaymentGatewayConfig.highVolume()
        val productionGateway = sdk.createProductionUsdPaymentGateway(
            merchantWalletAddress = merchantBitcoinWallet,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // 2. Gateway-Health-Status überprüfen
        println("2. Gateway Health Status:")
        val healthStatus = productionGateway.getGatewayHealthStatus()
        println("- Gateway Healthy: ${healthStatus.isHealthy}")
        println("- Converter Healthy: ${healthStatus.converterHealth.isHealthy}")
        println("- Pending Payments: ${healthStatus.pendingPaymentsCount}")
        println("- Confirmed Payments: ${healthStatus.confirmedPaymentsCount}")
        println("- Last Update: ${healthStatus.converterHealth.lastSuccessfulUpdate}")
        println()
        
        // 3. Produktions-USD Zahlungsanfrage erstellen
        println("3. Erstelle Produktions-USD Zahlungsanfrage...")
        try {
            val usdPayment = productionGateway.createUsdPaymentRequest(
                usdAmount = BigDecimal("250.00"), // $250 USD
                customerReference = "PROD-CUST-12345",
                description = "Premium Produkt #PROD-ABC-123",
                metadata = mapOf(
                    "product_id" to "PROD-ABC-123",
                    "customer_tier" to "premium",
                    "source" to "mobile_app"
                )
            )
            
            println("Produktions-USD Zahlungsanfrage erstellt:")
            println("- ID: ${usdPayment.id}")
            println("- USD Betrag: $${usdPayment.usdAmount}")
            println("- Gebühr: $${usdPayment.feeAmount}")
            println("- Gesamt: $${usdPayment.totalUsdAmount}")
            println("- Crypto Betrag: ${usdPayment.cryptoAmount} ${usdPayment.coinType.symbol}")
            println("- Wechselkurs: $${usdPayment.exchangeRate}")
            println("- Kundenadresse: ${usdPayment.customerAddress}")
            println("- Händleradresse: ${usdPayment.merchantAddress}")
            println("- Metadata: ${usdPayment.metadata}")
            println("- Info: ${usdPayment.getFormattedInfo()}")
            println()
            
            // 4. Zahlungsstatus überwachen mit erweitertem Monitoring
            println("4. Überwache Produktions-Zahlungsstatus...")
            monitorProductionPayment(productionGateway, usdPayment.id)
            
        } catch (e: Exception) {
            println("Fehler bei Zahlungserstellung: ${e.message}")
        }
        
        // 5. Statistiken und Health-Checks
        println("\n5. Produktions-Statistiken:")
        val stats = productionGateway.getPaymentStatistics()
        println("- Gesamtzahlungen: ${stats.totalPayments}")
        println("- Ausstehende Zahlungen: ${stats.pendingPayments}")
        println("- Gesamt USD Volumen: $${stats.totalUsdVolume}")
        println("- Gesammelte Gebühren: $${stats.totalFeesCollected}")
        println("- Durchschnittlicher Betrag: $${stats.averagePaymentAmount}")
        println("- Zahlungen (24h): ${stats.recentPayments24h}")
        println("- Volumen (24h): $${stats.recentVolume24h}")
        
        // 6. Aufräum-Operationen
        println("\n6. Aufräum-Operationen:")
        val cleanedUp = productionGateway.cleanupExpiredPayments()
        println("Aufgeräumte abgelaufene Zahlungen: $cleanedUp")
        
        println("\n=== Production USD Payment Gateway Demo abgeschlossen ===")
    }
    
    private suspend fun monitorProductionPayment(
        gateway: ProductionUsdPaymentGateway,
        paymentId: String
    ) {
        var checkCount = 0
        val maxChecks = 5
        
        while (checkCount < maxChecks) {
            val status = gateway.checkUsdPaymentStatus(paymentId)
            val details = gateway.getUsdPaymentDetails(paymentId)
            
            println("Check ${checkCount + 1}: Status = $status")
            details?.let {
                println("  Aktuelles Crypto-Guthaben: ${it.currentCryptoBalance} ${it.usdPaymentRequest.coinType.symbol}")
                println("  Aktueller USD-Wert: $${it.currentUsdValue.setScale(2, BigDecimal.ROUND_HALF_UP)}")
                println("  Verbleibender USD-Wert: $${it.remainingUsdValue.setScale(2, BigDecimal.ROUND_HALF_UP)}")
                println("  Gebühr: $${it.processingFee}")
            }
            
            when (status) {
                PaymentStatus.CONFIRMED -> {
                    println("✅ Produktions-Zahlung erfolgreich bestätigt und weitergeleitet!")
                    break
                }
                PaymentStatus.EXPIRED -> {
                    println("⏰ Produktions-Zahlung abgelaufen")
                    break
                }
                PaymentStatus.FORWARDING_FAILED -> {
                    println("❌ Weiterleitung fehlgeschlagen")
                    break
                }
                PaymentStatus.PENDING -> {
                    println("⏳ Warte auf Produktions-Zahlung...")
                }
                else -> {
                    println("❓ Unbekannter Status")
                }
            }
            
            checkCount++
            kotlinx.coroutines.delay(2000) // 2 Sekunden warten
        }
    }
    
    suspend fun demonstrateProductionCurrencyConverter() {
        println("\n=== Production Currency Converter Demo ===\n")
        
        val converter = sdk.getProductionCurrencyConverter()
        
        // 1. Health-Status des Converters
        println("1. Converter Health Status:")
        val health = converter.getHealthStatus()
        println("- Healthy: ${health.isHealthy}")
        println("- Last Successful Update: ${health.lastSuccessfulUpdate}")
        println("- Time Since Last Update: ${health.timeSinceLastUpdate}ms")
        println("- Cached Rates: ${health.cachedRatesCount}")
        println()
        
        // 2. Konfigurationen testen
        println("2. Verschiedene Konfigurationen:")
        
        // Default Konfiguration
        println("Default Konfiguration:")
        testConversion(converter, BigDecimal("100.00"), CoinType.BITCOIN)
        
        // High Frequency Konfiguration
        val highFreqConverter = ProductionCurrencyConverter(ConversionConfig.highFrequency())
        println("High Frequency Konfiguration:")
        testConversion(highFreqConverter, BigDecimal("50.00"), CoinType.ETHEREUM)
        
        // Low Frequency Konfiguration
        val lowFreqConverter = ProductionCurrencyConverter(ConversionConfig.lowFrequency())
        println("Low Frequency Konfiguration:")
        testConversion(lowFreqConverter, BigDecimal("75.00"), CoinType.LITECOIN)
        
        // 3. Fallback-Test
        println("\n3. Fallback-Test mit verschiedenen Beträgen:")
        val amounts = listOf(
            BigDecimal("1.00"),
            BigDecimal("10.00"),
            BigDecimal("100.00"),
            BigDecimal("1000.00")
        )
        
        amounts.forEach { amount ->
            val result = converter.convertUsdToCrypto(amount, CoinType.BITCOIN)
            if (result.success) {
                println("$${amount} → ${result.cryptoAmount} BTC (Rate: $${result.exchangeRate})")
            } else {
                println("$${amount} → Fehler: ${result.error}")
            }
        }
        
        println("\n=== Production Currency Converter Demo abgeschlossen ===")
    }
    
    private suspend fun testConversion(
        converter: ProductionCurrencyConverter,
        usdAmount: BigDecimal,
        coinType: CoinType
    ) {
        val result = converter.convertUsdToCrypto(usdAmount, coinType)
        if (result.success) {
            println("  $${usdAmount} → ${result.cryptoAmount} ${coinType.symbol}")
            println("  Rate: $${result.exchangeRate}")
            println("  Timestamp: ${result.timestamp}")
        } else {
            println("  Fehler: ${result.error}")
        }
        println()
    }
    
    suspend fun demonstrateProductionErrorHandling() {
        println("\n=== Production Error Handling Demo ===\n")
        
        val gateway = sdk.createProductionUsdPaymentGateway(
            merchantBitcoinWallet, CoinType.BITCOIN
        )
        
        // 1. Ungültige Beträge testen
        println("1. Ungültige Beträge testen:")
        
        val invalidAmounts = listOf(
            BigDecimal("0.00"),
            BigDecimal("-10.00"),
            BigDecimal("0.50") // Unter Minimum
        )
        
        invalidAmounts.forEach { amount ->
            try {
                gateway.createUsdPaymentRequest(
                    usdAmount = amount,
                    customerReference = "INVALID-TEST"
                )
                println("  $${amount}: Unerwartet erfolgreich")
            } catch (e: Exception) {
                println("  $${amount}: Erwarteter Fehler - ${e.message}")
            }
        }
        
        // 2. Zahlung stornieren
        println("\n2. Zahlung stornieren:")
        try {
            val payment = gateway.createUsdPaymentRequest(
                usdAmount = BigDecimal("25.00"),
                customerReference = "CANCEL-TEST"
            )
            
            println("  Zahlung erstellt: ${payment.id}")
            
            val cancelResult = gateway.cancelUsdPayment(payment.id)
            println("  Stornierung: ${if (cancelResult.success) "Erfolg" else "Fehlgeschlagen"}")
            
            if (!cancelResult.success) {
                println("  Fehler: ${cancelResult.error}")
            }
            
        } catch (e: Exception) {
            println("  Fehler bei Stornierung: ${e.message}")
        }
        
        // 3. Nicht existierende Zahlung stornieren
        println("\n3. Nicht existierende Zahlung stornieren:")
        val cancelResult = gateway.cancelUsdPayment("non-existent-payment")
        println("  Stornierung: ${if (cancelResult.success) "Unerwartet erfolgreich" else "Erwartet fehlgeschlagen"}")
        if (!cancelResult.success) {
            println("  Fehler: ${cancelResult.error}")
        }
        
        println("\n=== Production Error Handling Demo abgeschlossen ===")
    }
}

/**
 * Main Funktion für Production Demo
 */
suspend fun main() {
    val example = ProductionUsdPaymentExample()
    
    example.demonstrateProductionUsdPayments()
    example.demonstrateProductionCurrencyConverter()
    example.demonstrateProductionErrorHandling()
}
