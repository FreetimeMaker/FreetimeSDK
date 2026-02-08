package com.freetime.sdk.payment

import com.freetime.sdk.payment.providers.BitcoinPaymentProvider
import com.freetime.sdk.payment.providers.EthereumPaymentProvider
import com.freetime.sdk.payment.providers.LitecoinPaymentProvider
import com.freetime.sdk.payment.providers.BitcoinCashPaymentProvider
import com.freetime.sdk.payment.providers.CardanoPaymentProvider
import com.freetime.sdk.payment.providers.DogecoinPaymentProvider
import com.freetime.sdk.payment.providers.SolanaPaymentProvider
import com.freetime.sdk.payment.crypto.BitcoinCryptoUtils
import com.freetime.sdk.payment.crypto.EthereumCryptoUtils
import com.freetime.sdk.payment.crypto.LitecoinCryptoUtils
import com.freetime.sdk.payment.crypto.BitcoinCashCryptoUtils
import com.freetime.sdk.payment.crypto.CardanoCryptoUtils
import com.freetime.sdk.payment.crypto.DogecoinCryptoUtils
import com.freetime.sdk.payment.crypto.SolanaCryptoUtils
import com.freetime.sdk.payment.conversion.CurrencyConverter
import com.freetime.sdk.payment.conversion.UsdPaymentGateway
import com.freetime.sdk.payment.conversion.ProductionCurrencyConverter
import com.freetime.sdk.payment.conversion.ProductionUsdPaymentGateway
import com.freetime.sdk.payment.fee.FeeManager
import com.freetime.sdk.payment.fee.FeeBreakdown
import java.math.BigDecimal

/**
 * Main SDK class for multi-cryptocurrency payment processing
 * 
 * This is a completely self-contained, open-source SDK that doesn't depend on any external services.
 * All cryptographic operations are performed locally.
 */
class FreetimePaymentSDK {
    
    private val walletManager = WalletManager()
    private val paymentProviders = mutableMapOf<CoinType, PaymentInterface>()
    private val feeManager = FeeManager()
    
    init {
        // Initialize payment providers for each supported coin
        initializePaymentProviders()
    }
    
    /**
     * Initialize payment providers for all supported cryptocurrencies
     */
    private fun initializePaymentProviders() {
        paymentProviders[CoinType.BITCOIN] = BitcoinPaymentProvider()
        paymentProviders[CoinType.ETHEREUM] = EthereumPaymentProvider()
        paymentProviders[CoinType.LITECOIN] = LitecoinPaymentProvider()
        paymentProviders[CoinType.BITCOIN_CASH] = BitcoinCashPaymentProvider()
        paymentProviders[CoinType.CARDANO] = CardanoPaymentProvider()
        paymentProviders[CoinType.DOGECOIN] = DogecoinPaymentProvider()
        paymentProviders[CoinType.SOLANA] = SolanaPaymentProvider()
    }
    
    /**
     * Create a new wallet for the specified cryptocurrency
     */
    suspend fun createWallet(coinType: CoinType, name: String? = null): Wallet {
        val provider = paymentProviders[coinType] 
            ?: throw UnsupportedOperationException("Unsupported coin type: $coinType")
        
        val address = provider.generateAddress(coinType)
        val keyPair = generateKeyPair(coinType)
        
        val wallet = Wallet(
            address = address,
            coinType = coinType,
            keyPair = keyPair,
            name = name ?: "${coinType.coinName} Wallet"
        )
        
        walletManager.addWallet(wallet)
        return wallet
    }
    
    /**
     * Get balance of a wallet address
     */
    suspend fun getBalance(address: String): BigDecimal {
        val wallet = walletManager.getWallet(address) 
            ?: throw IllegalArgumentException("Wallet not found: $address")
        
        val provider = paymentProviders[wallet.coinType]
            ?: throw UnsupportedOperationException("No provider for ${wallet.coinType}")
        
        return provider.getBalance(address, wallet.coinType)
    }
    
    /**
     * Send cryptocurrency with automatic fee calculation
     */
    suspend fun send(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal,
        coinType: CoinType
    ): TransactionWithFees {
        
        val provider = paymentProviders[coinType] 
            ?: throw UnsupportedOperationException("Unsupported coin type: $coinType")
        
        // Get network fee estimate
        val networkFee = provider.getFeeEstimate(fromAddress, toAddress, amount, coinType)
        
        // Calculate total fees including developer fee
        val feeBreakdown = feeManager.calculateTotalFees(amount, networkFee, coinType)
        
        // Create transaction with recipient amount (original amount minus fees)
        val transaction = provider.createTransaction(
            fromAddress = fromAddress,
            toAddress = toAddress,
            amount = feeBreakdown.recipientAmount,
            coinType = coinType
        )
        
        return TransactionWithFees(
            transaction = transaction,
            feeBreakdown = feeBreakdown
        )
    }
    
    /**
     * Get fee breakdown for a transaction
     */
    fun getFeeBreakdown(
        amount: BigDecimal,
        networkFee: BigDecimal,
        coinType: CoinType
    ): FeeBreakdown {
        return feeManager.calculateTotalFees(amount, networkFee, coinType)
    }
    
    /**
     * Get fee manager
     */
    fun getFeeManager(): FeeManager = feeManager
    
    /**
     * Get fee estimate
     */
    suspend fun getFeeEstimate(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal,
        coinType: CoinType
    ): BigDecimal {
        val provider = paymentProviders[coinType]
            ?: throw UnsupportedOperationException("Unsupported coin type: $coinType")
        
        return provider.getFeeEstimate(fromAddress, toAddress, amount, coinType)
    }
    
    /**
     * Get all wallets
     */
    fun getAllWallets(): List<Wallet> {
        return walletManager.getAllWallets()
    }
    
    /**
     * Get wallets by coin type
     */
    fun getWalletsByCoinType(coinType: CoinType): List<Wallet> {
        return walletManager.getWalletsByCoinType(coinType)
    }
    
    /**
     * Validate address format
     */
    fun validateAddress(address: String, coinType: CoinType): Boolean {
        val provider = paymentProviders[coinType]
            ?: return false
        
        return provider.validateAddress(address, coinType)
    }
    
    /**
     * Generate key pair for the specified cryptocurrency
     */
    private fun generateKeyPair(coinType: CoinType): java.security.KeyPair {
        return when (coinType) {
            CoinType.BITCOIN -> BitcoinCryptoUtils.generateKeyPair()
            CoinType.ETHEREUM -> EthereumCryptoUtils.generateKeyPair()
            CoinType.LITECOIN -> LitecoinCryptoUtils.generateKeyPair()
            CoinType.BITCOIN_CASH -> BitcoinCashCryptoUtils.generateKeyPair()
            CoinType.CARDANO -> CardanoCryptoUtils.generateKeyPair()
            CoinType.DOGECOIN -> DogecoinCryptoUtils.generateKeyPair()
            CoinType.SOLANA -> SolanaCryptoUtils.generateKeyPair()
            else -> throw UnsupportedOperationException("Unsupported coin type: $coinType")
        }
    }
    
    /**
     * Create Production USD Payment Gateway with enhanced security and reliability
     */
    fun createProductionUsdPaymentGateway(
        merchantWalletAddress: String,
        merchantCoinType: CoinType
    ): ProductionUsdPaymentGateway {
        return ProductionUsdPaymentGateway(this, merchantWalletAddress, merchantCoinType)
    }
    
    /**
     * Create USD Payment Gateway with automatic crypto conversion
     */
    fun createUsdPaymentGateway(
        merchantWalletAddress: String,
        merchantCoinType: CoinType
    ): UsdPaymentGateway {
        return UsdPaymentGateway(this, merchantWalletAddress, merchantCoinType)
    }
    
    /**
     * Get currency converter for USD/crypto conversions
     */
    fun getCurrencyConverter(): CurrencyConverter {
        return CurrencyConverter()
    }
    
    /**
     * Get production currency converter with real-time rates
     */
    fun getProductionCurrencyConverter(): ProductionCurrencyConverter {
        return ProductionCurrencyConverter()
    }
}
