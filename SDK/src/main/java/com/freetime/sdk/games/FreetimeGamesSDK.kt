package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import com.freetime.sdk.payment.FreetimePaymentSDK
import java.math.BigDecimal

/**
 * Main SDK class for Freetime Games - integrates games with payment functionality
 */
class FreetimeGamesSDK(private val paymentSDK: FreetimePaymentSDK) {
    private val gameProvider = GameProvider()
    private val gameHistory = mutableListOf<GameResult>()
    private val progressManager = PlayerProgressManager()
    
    init {
        // Register default games
        registerDefaultGames()
    }
    
    /**
     * Register a custom game
     */
    fun registerGame(gameId: String, game: GameInterface) {
        gameProvider.registerGame(gameId, game)
    }
    
    /**
     * Get available games
     */
    fun getAvailableGames(): Map<String, GameInterface> {
        return gameProvider.getAllGames()
    }
    
    /**
     * Get games that support a specific cryptocurrency
     */
    fun getGamesForCoin(coinType: CoinType): List<Pair<String, GameInterface>> {
        return gameProvider.getGamesByCoin(coinType)
    }
    
    /**
     * Create or get player profile
     */
    fun getOrCreatePlayerProfile(playerId: String, username: String): PlayerProfile {
        return progressManager.getPlayerProfile(playerId) 
            ?: progressManager.createPlayerProfile(playerId, username)
    }
    
    /**
     * Play a game with automatic payment processing and progress tracking
     */
    suspend fun playGameWithPayment(
        playerId: String,
        username: String,
        gameId: String,
        amount: BigDecimal,
        coinType: CoinType,
        gameData: Map<String, Any> = emptyMap()
    ): GameSessionResult {
        val game = gameProvider.getGame(gameId)
            ?: throw IllegalArgumentException("Game with ID $gameId not found")
        
        // Validate game supports the cryptocurrency
        if (!game.isCoinSupported(coinType)) {
            throw IllegalArgumentException("Game $gameId does not support $coinType")
        }
        
        // Validate bet amount
        if (!game.validateBetAmount(amount)) {
            throw IllegalArgumentException("Bet amount $amount is not valid for game $gameId")
        }
        
        // Process payment (deduct from user's wallet)
        val userWallet = paymentSDK.getUserWalletAddress(coinType)
            ?: throw IllegalStateException("No wallet configured for $coinType")
        
        // For demo purposes, we'll simulate the payment
        // In a real implementation, you would process the actual payment here
        
        // Play the game
        val result = game.play(amount, coinType, gameData)
        
        // If player won, process winnings
        if (result.playerWon && result.winAmount > BigDecimal.ZERO) {
            // In a real implementation, you would credit winnings to user's wallet
            println("Processing winnings: ${result.winAmount} ${coinType.coinName}")
        }
        
        // Store result in history
        gameHistory.add(result)
        
        // Update player progress
        val profile = getOrCreatePlayerProfile(playerId, username)
        val progressUpdate = progressManager.updatePlayerProgress(playerId, result)
        
        return GameSessionResult(
            gameResult = result,
            playerProgress = progressUpdate,
            playerProfile = progressUpdate.profile
        )
    }
    
    /**
     * Play a game without payment processing (for testing/fun mode)
     */
    suspend fun playGameForFun(
        playerId: String,
        username: String,
        gameId: String,
        amount: BigDecimal,
        coinType: CoinType,
        gameData: Map<String, Any> = emptyMap()
    ): GameSessionResult {
        val game = gameProvider.getGame(gameId)
            ?: throw IllegalArgumentException("Game with ID $gameId not found")
        
        val result = game.play(amount, coinType, gameData)
        gameHistory.add(result)
        
        val profile = getOrCreatePlayerProfile(playerId, username)
        val progressUpdate = progressManager.updatePlayerProgress(playerId, result)
        
        return GameSessionResult(
            gameResult = result,
            playerProgress = progressUpdate,
            playerProfile = progressUpdate.profile
        )
    }
    
    /**
     * Get game history
     */
    fun getGameHistory(): List<GameResult> {
        return gameHistory.toList()
    }
    
    /**
     * Get player profile
     */
    fun getPlayerProfile(playerId: String): PlayerProfile? {
        return progressManager.getPlayerProfile(playerId)
    }
    
    /**
     * Get player statistics
     */
    fun getPlayerStatistics(playerId: String): PlayerStatistics? {
        return progressManager.getPlayerStatistics(playerId)
    }
    
    /**
     * Get player achievements
     */
    fun getPlayerAchievements(playerId: String): Set<Achievement> {
        return progressManager.getPlayerAchievements(playerId)
    }
    
    /**
     * Get leaderboard
     */
    fun getLeaderboard(limit: Int = 10): List<PlayerStatistics> {
        return progressManager.getLeaderboard(limit)
    }
    
    /**
     * Get game by ID
     */
    fun getGame(gameId: String): GameInterface? {
        return gameProvider.getGame(gameId)
    }
    
    /**
     * Register default games
     */
    private fun registerDefaultGames() {
        // No built-in games - developers create their own custom games
        // Example: registerGame("my_custom_game", MyCustomGame())
    }
}

/**
 * Result of a game session including player progress
 */
data class GameSessionResult(
    val gameResult: GameResult,
    val playerProgress: PlayerProgressUpdate,
    val playerProfile: PlayerProfile
)
