package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal

/**
 * Core interface for all games in the Freetime Games SDK
 */
interface GameInterface {
    /**
     * The type of game
     */
    val gameType: GameType
    
    /**
     * The minimum bet amount for this game
     */
    val minBetAmount: BigDecimal
    
    /**
     * The maximum bet amount for this game
     */
    val maxBetAmount: BigDecimal
    
    /**
     * The supported cryptocurrencies for this game
     */
    val supportedCoins: List<CoinType>
    
    /**
     * The return to player (RTP) percentage
     */
    val rtpPercentage: BigDecimal
    
    /**
     * Play the game with the specified amount and cryptocurrency
     */
    suspend fun play(
        amount: BigDecimal,
        coinType: CoinType,
        gameData: Map<String, Any> = emptyMap()
    ): GameResult
    
    /**
     * Validate if a bet amount is valid for this game
     */
    fun validateBetAmount(amount: BigDecimal): Boolean {
        return amount >= minBetAmount && amount <= maxBetAmount
    }
    
    /**
     * Check if a cryptocurrency is supported by this game
     */
    fun isCoinSupported(coinType: CoinType): Boolean {
        return coinType in supportedCoins
    }
    
    /**
     * Get the game configuration and rules
     */
    fun getGameRules(): GameRules
    
    /**
     * Calculate potential win amount for a given bet
     */
    fun calculatePotentialWin(amount: BigDecimal): BigDecimal {
        return amount * rtpPercentage / BigDecimal("100")
    }
}
