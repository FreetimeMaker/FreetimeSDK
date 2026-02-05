package com.freetime.sdk.payment

import com.freetime.sdk.payment.crypto.BitcoinPaymentProvider
import com.freetime.sdk.payment.crypto.EthereumPaymentProvider
import com.freetime.sdk.payment.crypto.LitecoinPaymentProvider
import com.freetime.sdk.payment.crypto.MoneroPaymentProvider
import com.freetime.sdk.payment.crypto.SolanaPaymentProvider
import com.freetime.sdk.payment.crypto.CardanoPaymentProvider
import com.freetime.sdk.payment.open.BankTransferProvider
import com.freetime.sdk.payment.open.LibrePaymentProvider
import com.freetime.sdk.payment.open.BitHubPaymentProvider
import com.freetime.sdk.payment.open.GitHubSponsorsProvider
import com.freetime.sdk.payment.defi.UniswapPaymentProvider
import com.freetime.sdk.payment.p2p.LightningNetworkProvider
import com.freetime.sdk.payment.multisig.MultiSigWalletProvider
import com.freetime.sdk.payment.nft.NFTPaymentProvider
import com.freetime.sdk.payment.routing.PaymentRouter
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/**
 * F-Droid compatible Payment SDK that only uses open-source payment methods.
 * Excludes proprietary services like Stripe and PayPal for F-Droid compliance.
 */
class FDroidPaymentSDK private constructor(
    private val providers: Map<PaymentMethod, List<PaymentProvider>>
) {
    
    suspend fun processPayment(request: PaymentRequest): PaymentResult {
        val methodProviders = providers[request.paymentMethod] ?: emptyList()
        
        if (methodProviders.isEmpty()) {
            return PaymentResult.failure(
                PaymentError.API_ERROR,
                "No providers available for payment method: ${request.paymentMethod}"
            )
        }
        
        for (provider in methodProviders) {
            if (provider.supportedCurrencies.contains(request.currency)) {
                return provider.processPayment(request)
            }
        }
        
        return PaymentResult.failure(
            PaymentError.INVALID_CURRENCY,
            "No providers support currency: ${request.currency}"
        )
    }
    
    suspend fun validatePayment(request: PaymentRequest): ValidationResult {
        val methodProviders = providers[request.paymentMethod] ?: emptyList()
        
        for (provider in methodProviders) {
            if (provider.supportedCurrencies.contains(request.currency)) {
                return provider.validatePayment(request)
            }
        }
        
        return ValidationResult.Failure(listOf("No providers available for this payment method and currency"))
    }
    
    suspend fun getTransactionStatus(transactionId: String, paymentMethod: PaymentMethod): TransactionStatus {
        val methodProviders = providers[paymentMethod] ?: emptyList()
        
        for (provider in methodProviders) {
            try {
                return provider.getTransactionStatus(transactionId)
            } catch (e: Exception) {
                continue
            }
        }
        
        return TransactionStatus.FAILED
    }
    
    suspend fun refundPayment(transactionId: String, paymentMethod: PaymentMethod, amount: BigDecimal? = null): PaymentResult {
        val methodProviders = providers[paymentMethod] ?: emptyList()
        
        for (provider in methodProviders) {
            try {
                return provider.refundPayment(transactionId, amount)
            } catch (e: Exception) {
                continue
            }
        }
        
        return PaymentResult.failure(
            PaymentError.TRANSACTION_FAILED,
            "No providers available for refund"
        )
    }
    
    suspend fun getBalance(address: String, paymentMethod: PaymentMethod): BigDecimal {
        val methodProviders = providers[paymentMethod] ?: emptyList()
        
        for (provider in methodProviders) {
            try {
                return provider.getBalance(address)
            } catch (e: Exception) {
                continue
            }
        }
        
        return BigDecimal.ZERO
    }
    
    suspend fun estimateFee(request: PaymentRequest): BigDecimal {
        val methodProviders = providers[request.paymentMethod] ?: emptyList()
        
        for (provider in methodProviders) {
            if (provider.supportedCurrencies.contains(request.currency)) {
                return provider.estimateFee(request)
            }
        }
        
        return BigDecimal.ZERO
    }
    
    fun getTransactionHistory(address: String? = null, paymentMethod: PaymentMethod? = null): Flow<Transaction> {
        val targetProviders = if (paymentMethod != null) {
            providers[paymentMethod] ?: emptyList()
        } else {
            providers.values.flatten()
        }
        
        return kotlinx.coroutines.flow.flow {
            targetProviders.forEach { provider ->
                try {
                    provider.getTransactionHistory(address).collect { transaction ->
                        emit(transaction)
                    }
                } catch (e: Exception) {
                    // Skip provider on error
                }
            }
        }
    }
    
    fun getSupportedMethods(): List<PaymentMethod> {
        return providers.keys.toList()
    }
    
    fun getSupportedCurrencies(paymentMethod: PaymentMethod): List<String> {
        return providers[paymentMethod]?.flatMap { it.supportedCurrencies }?.distinct() ?: emptyList()
    }
    
    class Builder {
        private val providers = mutableMapOf<PaymentMethod, MutableList<PaymentProvider>>()
        
        fun addBitcoinProvider(networkUrl: String = "https://api.blockchain.info", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(BitcoinPaymentProvider(networkUrl, testnet))
            return this
        }
        
        fun addEthereumProvider(rpcUrl: String = "https://mainnet.infura.io/v3/YOUR_PROJECT_ID", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(EthereumPaymentProvider(rpcUrl, testnet))
            return this
        }
        
        fun addBankTransferProvider(): Builder {
            providers.getOrPut(PaymentMethod.BANK_TRANSFER) { mutableListOf() }
                .add(BankTransferProvider())
            return this
        }
        
        fun addLibrePaymentProvider(endpoint: String = "https://librepay.org/api"): Builder {
            providers.getOrPut(PaymentMethod.DIGITAL_WALLET) { mutableListOf() }
                .add(LibrePaymentProvider(endpoint))
            return this
        }
        
        fun addLitecoinProvider(networkUrl: String = "https://api.blockchair.com/litecoin", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(LitecoinPaymentProvider(networkUrl, testnet))
            return this
        }
        
        fun addMoneroProvider(nodeUrl: String = "https://node.moneroworld.com:18089", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(MoneroPaymentProvider(nodeUrl, testnet))
            return this
        }
        
        fun addBitHubProvider(endpoint: String = "https://bithub.com/api"): Builder {
            providers.getOrPut(PaymentMethod.DIGITAL_WALLET) { mutableListOf() }
                .add(BitHubPaymentProvider(endpoint))
            return this
        }
        
        fun addGitHubSponsorsProvider(username: String = "freetime-sdk", accessToken: String? = null): Builder {
            providers.getOrPut(PaymentMethod.DIGITAL_WALLET) { mutableListOf() }
                .add(GitHubSponsorsProvider(username, accessToken))
            return this
        }
        
        fun addUniswapProvider(rpcUrl: String = "https://mainnet.infura.io/v3/YOUR_PROJECT_ID", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(UniswapPaymentProvider(rpcUrl, testnet))
            return this
        }
        
        fun addLightningNetworkProvider(nodeUrl: String = "https://lnd.example.com", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(LightningNetworkProvider(nodeUrl, testnet))
            return this
        }
        
        fun addSolanaProvider(rpcUrl: String = "https://api.mainnet-beta.solana.com", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(SolanaPaymentProvider(rpcUrl, testnet))
            return this
        }
        
        fun addCardanoProvider(networkUrl: String = "https://api.blockfrost.io/api/v0", testnet: Boolean = false): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(CardanoPaymentProvider(networkUrl, testnet))
            return this
        }
        
        fun addMultiSigWalletProvider(requiredSignatures: Int = 2, totalSigners: Int = 3): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(MultiSigWalletProvider(requiredSignatures = requiredSignatures, totalSigners = totalSigners))
            return this
        }
        
        fun addNFTPaymentProvider(marketplaceUrl: String = "https://nft-marketplace.com/api", blockchain: String = "ethereum"): Builder {
            providers.getOrPut(PaymentMethod.CRYPTO) { mutableListOf() }
                .add(NFTPaymentProvider(marketplaceUrl, blockchain))
            return this
        }
        
        fun addCustomProvider(paymentMethod: PaymentMethod, provider: PaymentProvider): Builder {
            providers.getOrPut(paymentMethod) { mutableListOf() }.add(provider)
            return this
        }
        
        fun build(): FDroidPaymentSDK {
            return FDroidPaymentSDK(providers.toMap())
        }
    }
}
