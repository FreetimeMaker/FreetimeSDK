package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

/**
 * Provider class that manages game instances and game logic
 */
class GameProvider {
    private val secureRandom = SecureRandom()
    private val activeGames = mutableMapOf<String, GameInterface>()
    
    /**
     * Register a game instance
     */
    fun registerGame(gameId: String, game: GameInterface) {
        activeGames[gameId] = game
    }
    
    /**
     * Get a registered game by ID
     */
    fun getGame(gameId: String): GameInterface? {
        return activeGames[gameId]
    }
    
    /**
     * Get all registered games
     */
    fun getAllGames(): Map<String, GameInterface> {
        return activeGames.toMap()
    }
    
    /**
     * Get games by type
     */
    fun getGamesByType(gameType: GameType): List<Pair<String, GameInterface>> {
        return activeGames.filter { it.value.gameType == gameType }.toList()
    }
    
    /**
     * Get games that support a specific cryptocurrency
     */
    fun getGamesByCoin(coinType: CoinType): List<Pair<String, GameInterface>> {
        return activeGames.filter { it.value.isCoinSupported(coinType) }.toList()
    }
    
    /**
     * Generate a unique game ID
     */
    fun generateGameId(): String {
        return "game_${UUID.randomUUID().toString().substring(0, 8)}"
    }
    
    /**
     * Generate a secure random number within range
     */
    fun generateSecureRandomInt(min: Int, max: Int): Int {
        return secureRandom.nextInt(max - min + 1) + min
    }
    
    /**
     * Generate a secure random double between 0.0 and 1.0
     */
    fun generateSecureRandomDouble(): Double {
        return secureRandom.nextDouble()
    }
    
    /**
     * Calculate win probability based on RTP and volatility
     */
    fun calculateWinProbability(
        rtpPercentage: BigDecimal,
        volatilityLevel: VolatilityLevel,
        multiplier: BigDecimal
    ): Double {
        val baseProbability = rtpPercentage.toDouble() / 100.0
        val volatilityMultiplier = when (volatilityLevel) {
            VolatilityLevel.LOW -> 1.5
            VolatilityLevel.MEDIUM -> 1.0
            VolatilityLevel.HIGH -> 0.7
            VolatilityLevel.VERY_HIGH -> 0.4
        }
        
        return (baseProbability / multiplier.toDouble()) * volatilityMultiplier
    }
    
    /**
     * Create a game result
     */
    fun createGameResult(
        gameId: String,
        gameType: GameType,
        playerWon: Boolean,
        winAmount: BigDecimal,
        coinType: CoinType,
        playAmount: BigDecimal,
        multiplier: BigDecimal = BigDecimal.ONE,
        gameData: Map<String, Any> = emptyMap(),
        transactionId: String? = null
    ): GameResult {
        return GameResult(
            gameId = gameId,
            gameType = gameType,
            playerWon = playerWon,
            winAmount = winAmount,
            coinType = coinType,
            playAmount = playAmount,
            timestamp = LocalDateTime.now(),
            gameData = gameData,
            transactionId = transactionId,
            multiplier = multiplier
        )
    }
}
