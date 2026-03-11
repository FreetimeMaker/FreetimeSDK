package com.freetime.sdk.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.conversion.UsdPaymentGateway
import com.freetime.sdk.payment.gateway.PaymentGateway
import java.math.BigDecimal

/**
 * Example demonstrating direct wallet payments from app user
 * Shows how to send payments directly from user's wallet to merchant
 */
object DirectWalletPaymentExample {

    suspend fun demonstrateDirectUserToMerchantPayment() {
        val sdk = FreetimePaymentSDK()
        
        // Benutzer-Wallet-Adresse (von der die Zahlung gesendet wird)
        val userWalletAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"
        val merchantWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        
        // Erstelle Payment Gateway für direkte Zahlungen
        val paymentGateway = PaymentGateway(
            sdk = sdk,
            merchantWalletAddress = merchantWallet,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // Führe direkte Zahlung vom Benutzer an den Händler durch
        val transactionWithFees = paymentGateway.executeDirectPayment(
            userWalletAddress = userWalletAddress,
            amount = BigDecimal("0.001"), // 0.001 BTC
            customerReference = "Order-12345",
            description = "Direkte Zahlung vom Benutzer"
        )
        
        println("Direkte Zahlung erfolgreich:")
        println("Von: $userWalletAddress")
        println("An: $merchantWallet")
        println("Betrag: ${transactionWithFees.transaction.amount} BTC")
        println("Netzwerkgebühr: ${transactionWithFees.feeBreakdown.networkFee} BTC")
        println("Entwicklergebühr: ${transactionWithFees.feeBreakdown.developerFee} BTC")
        println("Empfängerbetrag: ${transactionWithFees.feeBreakdown.recipientAmount} BTC")
        println("Transaktions-ID: ${transactionWithFees.transaction.id}")
    }
    
    suspend fun demonstrateDirectPaymentWithUSDConversion() {
        val sdk = FreetimePaymentSDK()
        
        val userWalletAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"
        val merchantWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        
        // Erstelle USD Payment Gateway
        val usdPaymentGateway = sdk.createUsdPaymentGateway(
            merchantWalletAddress = merchantWallet,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // Erstelle USD-Zahlungsanfrage
        val usdPaymentRequest = usdPaymentGateway.createUsdPaymentRequest(
            usdAmount = BigDecimal("100.00"),
            customerReference = "Order-67890",
            description = "USD-Zahlung vom Benutzer",
            providedWallet = userWalletAddress // Benutzer-Wallet als Absender
        )
        
        println("USD-Zahlungsanfrage erstellt:")
        println("Zahlungs-ID: ${usdPaymentRequest.id}")
        println("Benutzer-Wallet: $userWalletAddress")
        println("Händler-Wallet: $merchantWallet")
        println("USD-Betrag: ${usdPaymentRequest.usdAmount}")
        println("BTC-Betrag: ${usdPaymentRequest.cryptoAmount}")
        println("Wechselkurs: ${usdPaymentRequest.exchangeRate}")
        
        // Führe die tatsächliche Zahlung durch
        val paymentGateway = PaymentGateway(sdk, merchantWallet, CoinType.BITCOIN)
        val transactionResult = paymentGateway.executeDirectPayment(
            userWalletAddress = userWalletAddress,
            amount = usdPaymentRequest.cryptoAmount,
            customerReference = usdPaymentRequest.customerReference,
            description = usdPaymentRequest.description
        )
        
        println("Zahlung ausgeführt:")
        println("Transaktions-ID: ${transactionResult.transaction.id}")
        println("Tatsächlich gesendet: ${transactionResult.transaction.amount} BTC")
    }
    
    suspend fun demonstrateDirectPaymentWithForwarding() {
        val sdk = FreetimePaymentSDK()
        
        // Benutzer-Wallet -> Händler-Wallet -> Weiterleitung an Endempfänger
        val userWalletAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
        val merchantWallet = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
        val finalRecipient = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
        
        val paymentGateway = PaymentGateway(
            sdk = sdk,
            merchantWalletAddress = merchantWallet,
            merchantCoinType = CoinType.ETHEREUM
        )
        
        // Direkte Zahlung mit Weiterleitung an Endempfänger
        val transactionResult = paymentGateway.executeDirectPayment(
            userWalletAddress = userWalletAddress,
            amount = BigDecimal("0.5"), // 0.5 ETH
            customerReference = "Order-11111",
            description = "Zahlung mit Weiterleitung",
            forwardToAddress = finalRecipient
        )
        
        println("Direkte Zahlung mit Weiterleitung:")
        println("Von Benutzer: $userWalletAddress")
        println("An Händler: $merchantWallet")
        println("Weiterleitung an: $finalRecipient")
        println("Betrag: ${transactionResult.transaction.amount} ETH")
        println("Transaktions-ID: ${transactionResult.transaction.id}")
    }
    
    suspend fun demonstrateMultipleUserPayments() {
        val sdk = FreetimePaymentSDK()
        
        val merchantWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        val paymentGateway = PaymentGateway(sdk, merchantWallet, CoinType.BITCOIN)
        
        // Mehrere Benutzer zahlen direkt an den Händler
        val userPayments = mapOf(
            "Benutzer-1" to "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            "Benutzer-2" to "3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy",
            "Benutzer-3" to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        )
        
        userPayments.forEach { (userId, walletAddress) ->
            try {
                val transactionResult = paymentGateway.executeDirectPayment(
                    userWalletAddress = walletAddress,
                    amount = BigDecimal("0.0005"),
                    customerReference = userId,
                    description = "Zahlung von $userId"
                )
                
                println("Zahlung von $userId erfolgreich:")
                println("  Wallet: $walletAddress")
                println("  Betrag: ${transactionResult.transaction.amount} BTC")
                println("  Transaktions-ID: ${transactionResult.transaction.id}")
                
            } catch (e: Exception) {
                println("Fehler bei Zahlung von $userId: ${e.message}")
            }
        }
    }
    
    suspend fun demonstrateDirectPaymentWithValidation() {
        val sdk = FreetimePaymentSDK()
        
        val userWalletAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"
        val merchantWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        
        // Validiere Adressen vor der Zahlung
        if (!sdk.validateAddress(userWalletAddress, CoinType.BITCOIN)) {
            println("Ungültige Benutzer-Wallet-Adresse!")
            return
        }
        
        if (!sdk.validateAddress(merchantWallet, CoinType.BITCOIN)) {
            println("Ungültige Händler-Wallet-Adresse!")
            return
        }
        
        // Prüfe Gebührenschätzung
        val feeEstimate = sdk.getFeeEstimate(
            fromAddress = userWalletAddress,
            toAddress = merchantWallet,
            amount = BigDecimal("0.001"),
            coinType = CoinType.BITCOIN
        )
        
        println("Gebührenschätzung: $feeEstimate BTC")
        
        val paymentGateway = PaymentGateway(sdk, merchantWallet, CoinType.BITCOIN)
        
        // Führe Zahlung durch
        val transactionResult = paymentGateway.executeDirectPayment(
            userWalletAddress = userWalletAddress,
            amount = BigDecimal("0.001")
        )
        
        println("Validierte Zahlung erfolgreich:")
        println("Netzwerkgebühr: ${transactionResult.feeBreakdown.networkFee} BTC")
        println("Entwicklergebühr: ${transactionResult.feeBreakdown.developerFee} BTC")
        println("Gesamtgebühren: ${transactionResult.feeBreakdown.totalFees} BTC")
    }
}
