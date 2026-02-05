package com.freetime.sdk.payment.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.crypto.LitecoinPaymentProvider
import com.freetime.sdk.payment.crypto.MoneroPaymentProvider
import com.freetime.sdk.payment.open.BitHubPaymentProvider
import com.freetime.sdk.payment.open.GitHubSponsorsProvider
import com.freetime.sdk.payment.defi.UniswapPaymentProvider
import com.freetime.sdk.payment.p2p.LightningNetworkProvider
import java.math.BigDecimal

object AdvancedPaymentExamples {
    
    suspend fun litecoinPaymentExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addLitecoinProvider(testnet = true)
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("0.1"),
            currency = "LTC",
            paymentMethod = PaymentMethod.CRYPTO,
            description = "Litecoin donation",
            recipientAddress = "ltc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            senderAddress = "ltc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            privateKey = "your_litecoin_private_key"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("Litecoin payment successful: ${result.transactionId}")
            }
            else -> {
                println("Litecoin payment failed: ${result.message}")
            }
        }
    }
    
    suspend fun moneroPaymentExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addMoneroProvider(testnet = true)
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("0.05"),
            currency = "XMR",
            paymentMethod = PaymentMethod.CRYPTO,
            description = "Private Monero donation",
            recipientAddress = "4A7F8B9C2D5E6F1A3B8C9D2E5F6A1B3C8D9E2F5A6B1C3D8E9F2A5B6C1D3E8F9",
            senderAddress = "8B9C2D5E6F1A3B8C9D2E5F6A1B3C8D9E2F5A6B1C3D8E9F2A5B6C1D3E8F9A2B",
            privateKey = "your_monero_private_key"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("Monero payment successful: ${result.transactionId}")
            }
            else -> {
                println("Monero payment failed: ${result.message}")
            }
        }
    }
    
    suspend fun bitHubPaymentExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addBitHubProvider()
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("50.00"),
            currency = "BTC",
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            description = "BitHub payment",
            returnUrl = "https://yourapp.com/success"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("BitHub payment initiated: ${result.transactionId}")
                
                val bitHubProvider = BitHubPaymentProvider()
                val invoice = bitHubProvider.createInvoice(request)
                println(invoice.getFormattedInvoice())
                println("Payment URL: ${invoice.paymentUrl}")
            }
            else -> {
                println("BitHub payment failed: ${result.message}")
            }
        }
    }
    
    suspend fun gitHubSponsorsExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addGitHubSponsorsProvider("freetime-sdk")
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("10.00"),
            currency = "USD",
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            description = "GitHub Sponsors support"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("GitHub Sponsors sponsorship created: ${result.transactionId}")
                
                val gitHubProvider = GitHubSponsorsProvider("freetime-sdk")
                val sponsorship = gitHubProvider.createSponsorshipTier(request)
                println(sponsorship.getFormattedTier())
                
                // Show available tiers
                val tiers = gitHubProvider.getSponsorshipTiers()
                tiers.forEach { tier ->
                    println(tier.getFormattedTier())
                }
            }
            else -> {
                println("GitHub Sponsors failed: ${result.message}")
            }
        }
    }
    
    suspend fun uniswapSwapExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addUniswapProvider(testnet = true)
            .build()
        
        val request = PaymentRequest(
            amount = BigDecimal("0.5"),
            currency = "ETH",
            paymentMethod = PaymentMethod.CRYPTO,
            description = "Uniswap ETH to USDT swap",
            recipientAddress = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("Uniswap swap executed: ${result.transactionId}")
            }
            else -> {
                println("Uniswap swap failed: ${result.message}")
            }
        }
        
        // Get swap quote
        val uniswapProvider = UniswapPaymentProvider(testnet = true)
        val quote = uniswapProvider.getSwapQuote("ETH", "USDT", BigDecimal("0.5"))
        println(quote.getFormattedQuote())
        
        // Show liquidity pools
        val pools = uniswapProvider.getLiquidityPools()
        pools.forEach { pool ->
            println(pool.getFormattedPool())
        }
    }
    
    suspend fun lightningNetworkExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addLightningNetworkProvider(testnet = true)
            .build()
        
        // Create Lightning invoice first
        val lightningProvider = LightningNetworkProvider(testnet = true)
        val invoice = lightningProvider.createLightningInvoice(
            BigDecimal("0.0001"),
            "Lightning payment test"
        )
        println(invoice.getFormattedInvoice())
        
        val request = PaymentRequest(
            amount = invoice.amount,
            currency = "BTC",
            paymentMethod = PaymentMethod.CRYPTO,
            description = "Lightning Network payment",
            recipientAddress = invoice.invoice
        )
        
        val result = sdk.processPayment(request)
        when {
            result.isSuccess -> {
                println("Lightning payment sent: ${result.transactionId}")
            }
            else -> {
                println("Lightning payment failed: ${result.message}")
            }
        }
        
        // Show channel information
        val channels = lightningProvider.getChannelInfo()
        channels.forEach { channel ->
            println(channel.getFormattedChannel())
        }
    }
    
    suspend fun comprehensiveMultiProviderExample() {
        val sdk = FDroidPaymentSDK.Builder()
            .addBitcoinProvider(testnet = true)
            .addLitecoinProvider(testnet = true)
            .addMoneroProvider(testnet = true)
            .addEthereumProvider(testnet = true)
            .addBankTransferProvider()
            .addLibrePaymentProvider()
            .addBitHubProvider()
            .addGitHubSponsorsProvider("freetime-sdk")
            .addUniswapProvider(testnet = true)
            .addLightningNetworkProvider(testnet = true)
            .build()
        
        println("🚀 Comprehensive F-Droid Payment SDK")
        println("=====================================")
        println("Supported Methods: ${sdk.getSupportedMethods()}")
        println()
        
        // Show all supported currencies by method
        sdk.getSupportedMethods().forEach { method ->
            val currencies = sdk.getSupportedCurrencies(method)
            println("$method: $currencies")
        }
        
        println()
        println("📊 Fee Comparison for 100 EUR equivalent:")
        println("===========================================")
        
        // Compare fees across different methods
        val baseAmount = BigDecimal("100.00")
        
        val cryptoAmount = BigDecimal("0.005") // ~100 EUR in BTC
        val btcRequest = PaymentRequest(
            amount = cryptoAmount,
            currency = "BTC",
            paymentMethod = PaymentMethod.CRYPTO,
            recipientAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            senderAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            privateKey = "test_key"
        )
        val btcFee = sdk.estimateFee(btcRequest)
        println("Bitcoin: $btcFee BTC")
        
        val ltcRequest = PaymentRequest(
            amount = BigDecimal("2.0"),
            currency = "LTC",
            paymentMethod = PaymentMethod.CRYPTO,
            recipientAddress = "ltc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            senderAddress = "ltc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            privateKey = "test_key"
        )
        val ltcFee = sdk.estimateFee(ltcRequest)
        println("Litecoin: $ltcFee LTC")
        
        val xmrRequest = PaymentRequest(
            amount = BigDecimal("0.005"),
            currency = "XMR",
            paymentMethod = PaymentMethod.CRYPTO,
            recipientAddress = "4A7F8B9C2D5E6F1A3B8C9D2E5F6A1B3C8D9E2F5A6B1C3D8E9F2A5B6C1D3E8F9",
            senderAddress = "8B9C2D5E6F1A3B8C9D2E5F6A1B3C8D9E2F5A6B1C3D8E9F2A5B6C1D3E8F9A2B",
            privateKey = "test_key"
        )
        val xmrFee = sdk.estimateFee(xmrRequest)
        println("Monero: $xmrFee XMR")
        
        val bankRequest = PaymentRequest(
            amount = baseAmount,
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER
        )
        val bankFee = sdk.estimateFee(bankRequest)
        println("Bank Transfer: €$bankFee")
        
        val libreRequest = PaymentRequest(
            amount = baseAmount,
            currency = "EUR",
            paymentMethod = PaymentMethod.DIGITAL_WALLET
        )
        val libreFee = sdk.estimateFee(libreRequest)
        println("LibrePay: €$libreFee")
        
        val lightningRequest = PaymentRequest(
            amount = BigDecimal("0.0001"),
            currency = "BTC",
            paymentMethod = PaymentMethod.CRYPTO,
            recipientAddress = "lnbc1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypq"
        )
        val lightningFee = sdk.estimateFee(lightningRequest)
        println("Lightning: $lightningFee BTC")
        
        println()
        println("🔍 Transaction History:")
        println("======================")
        
        // Show transaction history
        sdk.getTransactionHistory().collect { transaction ->
            println("${transaction.id}: ${transaction.amount} ${transaction.currency} - ${transaction.status}")
        }
    }
}
