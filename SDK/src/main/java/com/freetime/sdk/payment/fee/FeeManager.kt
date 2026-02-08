package com.freetime.sdk.payment.fee

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Fee manager for handling developer fees and transaction costs
 */
class FeeManager(
    private val developerFeePercentage: BigDecimal = BigDecimal("1.0"), // 1% developer fee
    private val developerWallets: Map<CoinType, String> = mapOf(
        CoinType.BITCOIN to "bc1qfreetime_developer_wallet_btc",
        CoinType.ETHEREUM to "0xfreetime_developer_wallet_eth",
        CoinType.LITECOIN to "Lfreetime_developer_wallet_ltc",
        CoinType.BITCOIN_CASH to "bitcoincash:freetime_developer_wallet_bch",
        CoinType.CARDANO to "addr1freetime_developer_wallet_ada",
        CoinType.POLKADOT to "freetime_developer_wallet_dot",
        CoinType.CHAINLINK to "freetime_developer_wallet_link",
        CoinType.STELLAR to "freetime_developer_wallet_xlm",
        CoinType.DOGECOIN to "Dfreetime_developer_wallet_doge",
        CoinType.RIPPLE to "rfreetime_developer_wallet_xrp",
        CoinType.SOLANA to "freetime_developer_wallet_sol",
        CoinType.AVALANCHE to "freetime_developer_wallet_avax",
        CoinType.POLYGON to "freetime_developer_wallet_matic",
        CoinType.BINANCE_COIN to "freetime_developer_wallet_bnb",
        CoinType.TRON to "freetime_developer_wallet_trx"
    )
) {
    
    /**
     * Calculate total fees including developer fee and network fee
     */
    fun calculateTotalFees(
        amount: BigDecimal,
        networkFee: BigDecimal,
        coinType: CoinType
    ): FeeBreakdown {
        
        // Calculate developer fee (1% of transaction amount)
        val developerFee = amount.multiply(developerFeePercentage).divide(BigDecimal("100"), coinType.decimalPlaces, RoundingMode.HALF_UP)
        
        // Total fee = network fee + developer fee
        val totalFee = networkFee.add(developerFee)
        
        // Amount that will be sent to recipient
        val recipientAmount = amount.subtract(totalFee)
        
        // Get developer wallet for this specific cryptocurrency
        val developerWalletAddress = developerWallets[coinType] ?: "freetime_developer_wallet"
        
        return FeeBreakdown(
            originalAmount = amount,
            networkFee = networkFee,
            developerFee = developerFee,
            totalFee = totalFee,
            recipientAmount = recipientAmount,
            developerWalletAddress = developerWalletAddress,
            coinType = coinType
        )
    }
    
    /**
     * Get developer fee percentage
     */
    fun getDeveloperFeePercentage(): BigDecimal = developerFeePercentage
    
    /**
     * Get developer wallet address for specific cryptocurrency
     */
    fun getDeveloperWalletAddress(coinType: CoinType): String {
        return developerWallets[coinType] ?: "freetime_developer_wallet"
    }
    
    /**
     * Get all developer wallets
     */
    fun getAllDeveloperWallets(): Map<CoinType, String> = developerWallets
    
    /**
     * Update developer fee percentage
     */
    fun updateDeveloperFeePercentage(newPercentage: BigDecimal): FeeManager {
        return FeeManager(newPercentage, developerWallets)
    }
    
    /**
     * Update developer wallet for specific cryptocurrency
     */
    fun updateDeveloperWallet(coinType: CoinType, newAddress: String): FeeManager {
        val updatedWallets = developerWallets.toMutableMap()
        updatedWallets[coinType] = newAddress
        return FeeManager(developerFeePercentage, updatedWallets)
    }
    
    /**
     * Update all developer wallets
     */
    fun updateAllDeveloperWallets(newWallets: Map<CoinType, String>): FeeManager {
        return FeeManager(developerFeePercentage, newWallets)
    }
}

/**
 * Fee breakdown for a transaction
 */
data class FeeBreakdown(
    val originalAmount: BigDecimal,
    val networkFee: BigDecimal,
    val developerFee: BigDecimal,
    val totalFee: BigDecimal,
    val recipientAmount: BigDecimal,
    val developerWalletAddress: String,
    val coinType: CoinType
) {
    /**
     * Get formatted fee breakdown
     */
    fun getFormattedBreakdown(): String {
        return buildString {
            appendLine("Transaction Fee Breakdown (${coinType.symbol}):")
            appendLine("Original Amount: ${originalAmount.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol}")
            appendLine("Network Fee: ${networkFee.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol}")
            appendLine("Developer Fee (1%): ${developerFee.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol}")
            appendLine("Total Fee: ${totalFee.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol}")
            appendLine("Recipient Receives: ${recipientAmount.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol}")
            appendLine("Developer Wallet: ${developerWalletAddress}")
        }
    }
    
    /**
     * Get fee summary
     */
    fun getFeeSummary(): String {
        return "${developerFee.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol} (1% developer fee) + ${networkFee.setScale(coinType.decimalPlaces, RoundingMode.HALF_UP)} ${coinType.symbol} network fee"
    }
}
