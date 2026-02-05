package com.freetime.sdk.payment.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.open.BankTransferProvider
import java.math.BigDecimal

object FDroidPaymentExamples {
    
    suspend fun fDroidBitcoinPaymentExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addBitcoinProvider(testnet = true)
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("0.001"),
            currency = "BTC",
            paymentMethod = PaymentMethod.CRYPTO,
            description = "F-Droid app donation",
            recipientAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            senderAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            privateKey = "your_private_key_here"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("Bitcoin payment successful: ${result.transactionId}")
            }
            else -> {
                println("Bitcoin payment failed: ${result.message}")
            }
        }
    }
    
    suspend fun fDroidBankTransferExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addBankTransferProvider()
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("25.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            description = "F-Droid app purchase"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("Bank transfer initiated: ${result.transactionId}")
                
                val bankProvider = BankTransferProvider()
                val instructions = bankProvider.generateBankTransferInstructions(request, result.transactionId!!)
                println(instructions.getFormattedInstructions())
            }
            else -> {
                println("Bank transfer failed: ${result.message}")
            }
        }
    }
    
    suspend fun fDroidLibrePayExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addLibrePaymentProvider()
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("10.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            description = "F-Droid app support",
            returnUrl = "https://yourapp.com/success"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("LibrePay payment initiated: ${result.transactionId}")
                
                val libreProvider = com.freetime.sdk.payment.open.LibrePaymentProvider()
                val paymentUrl = libreProvider.generatePaymentUrl(request, result.transactionId!!)
                println("Payment URL: $paymentUrl")
            }
            else -> {
                println("LibrePay payment failed: ${result.message}")
            }
        }
    }
    
    suspend fun fDroidMultiProviderExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addBitcoinProvider(testnet = true)
            .addEthereumProvider(testnet = true)
            .addBankTransferProvider()
            .addLibrePaymentProvider()
            .build()
        
        println("F-Droid SDK Supported Methods: ${sdk.getSupportedMethods()}")
        println("Crypto currencies: ${sdk.getSupportedCurrencies(PaymentMethod.CRYPTO)}")
        println("Bank transfer currencies: ${sdk.getSupportedCurrencies(PaymentMethod.BANK_TRANSFER)}")
        println("Digital wallet currencies: ${sdk.getSupportedCurrencies(PaymentMethod.DIGITAL_WALLET)}")
        
        sdk.getTransactionHistory().collect { transaction ->
            println("Transaction: ${transaction.id} - ${transaction.amount} ${transaction.currency} (${transaction.status})")
        }
    }
    
    suspend fun fDroidFeeComparisonExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addBitcoinProvider(testnet = true)
            .addBankTransferProvider()
            .addLibrePaymentProvider()
            .build()
        
        val amount = BigDecimal("100.00")
        
        val btcRequest = PaymentRequest(
            amount = BigDecimal("0.001"),
            currency = "BTC",
            paymentMethod = PaymentMethod.CRYPTO,
            recipientAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            senderAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            privateKey = "private_key"
        )
        val btcFee = sdk.estimateFee(btcRequest)
        println("Bitcoin fee: $btcFee BTC")
        
        val bankRequest = PaymentRequest(
            amount = amount,
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER
        )
        val bankFee = sdk.estimateFee(bankRequest)
        println("Bank transfer fee: €$bankFee")
        
        val libreRequest = PaymentRequest(
            amount = amount,
            currency = "EUR",
            paymentMethod = PaymentMethod.DIGITAL_WALLET
        )
        val libreFee = sdk.estimateFee(libreRequest)
        println("LibrePay fee: €$libreFee")
    }
}
