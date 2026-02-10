package com.freetime.sdk.examples

import com.freetime.sdk.payment.CoinType
import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.games.*
import java.math.BigDecimal

/**
 * Example for game developers on how to integrate the Freetime Games SDK
 */
object GameDeveloperExample {
    
    /**
     * Example 1: Creating a custom game
     */
    class NumberGuessGame : GameInterface {
        override val gameType = GameType.CUSTOM_GAME
        override val minBetAmount = BigDecimal("0.0001")
        override val maxBetAmount = BigDecimal("1.0")
        override val supportedCoins = listOf(CoinType.BITCOIN, CoinType.ETHEREUM)
        override val rtpPercentage = BigDecimal("96.0")
        
        private val provider = GameProvider()
        
        override suspend fun play(
            amount: BigDecimal,
            coinType: CoinType,
            gameData: Map<String, Any>
        ): GameResult {
            // Get player's guess from game data
            val playerGuess = gameData["guess"] as? Int ?: 50
            val range = gameData["range"] as? Int ?: 100
            
            // Generate random number
            val randomNumber = provider.generateSecureRandomInt(1, range)
            
            // Calculate win condition (guess within 10 of the number)
            val difference = kotlin.math.abs(playerGuess - randomNumber)
            val playerWon = difference <= 10
            
            // Calculate multiplier based on how close the guess was
            val multiplier = when {
                difference == 0 -> BigDecimal("10") // Exact guess
                difference <= 3 -> BigDecimal("5")  // Very close
                difference <= 7 -> BigDecimal("3")  // Close
                difference <= 10 -> BigDecimal("2") // Within range
                else -> BigDecimal.ZERO
            }
            
            val winAmount = if (playerWon) amount * multiplier else BigDecimal.ZERO
            
            val resultData = mapOf(
                "playerGuess" to playerGuess,
                "randomNumber" to randomNumber,
                "difference" to difference,
                "multiplier" to multiplier
            )
            
            return provider.createGameResult(
                gameId = provider.generateGameId(),
                gameType = gameType,
                playerWon = playerWon,
                winAmount = winAmount,
                coinType = coinType,
                playAmount = amount,
                multiplier = if (playerWon) multiplier else BigDecimal.ZERO,
                gameData = resultData
            )
        }
        
        override fun getGameRules(): GameRules {
            return GameRules(
                gameType = gameType,
                description = "Guess a number between 1-100. Win up to 10x your bet!",
                minBetAmount = minBetAmount,
                maxBetAmount = maxBetAmount,
                supportedCoins = supportedCoins,
                rtpPercentage = rtpPercentage,
                maxWinMultiplier = BigDecimal("10"),
                houseEdgePercentage = BigDecimal("4"),
                volatilityLevel = VolatilityLevel.HIGH,
                specialFeatures = listOf("Number guessing", "Variable multipliers", "Skill-based"),
                customRules = mapOf(
                    "range" to 100,
                    "winRange" to 10,
                    "exactMultiplier" to 10,
                    "closeMultiplier" to 5,
                    "nearMultiplier" to 3,
                    "withinRangeMultiplier" to 2
                )
            )
        }
    }
    
    /**
     * Example 2: Racing game with betting
     */
    class RacingGame : GameInterface {
        override val gameType = GameType.CUSTOM_GAME
        override val minBetAmount = BigDecimal("0.001")
        override val maxBetAmount = BigDecimal("5.0")
        override val supportedCoins = CoinType.values().toList()
        override val rtpPercentage = BigDecimal("94.0")
        
        private val provider = GameProvider()
        
        override suspend fun play(
            amount: BigDecimal,
            coinType: CoinType,
            gameData: Map<String, Any>
        ): GameResult {
            // Get player's chosen car
            val playerCar = gameData["car"] as? Int ?: 1
            val numCars = gameData["numCars"] as? Int ?: 6
            
            // Simulate race with random results
            val raceResults = (1..numCars).shuffled()
            val playerPosition = raceResults.indexOf(playerCar) + 1
            
            // Calculate win based on position
            val (playerWon, multiplier) = when (playerPosition) {
                1 -> Pair(true, BigDecimal("6"))  // 1st place
                2 -> Pair(true, BigDecimal("3"))  // 2nd place
                3 -> Pair(true, BigDecimal("2"))  // 3rd place
                else -> Pair(false, BigDecimal.ZERO)
            }
            
            val winAmount = if (playerWon) amount * multiplier else BigDecimal.ZERO
            
            val resultData = mapOf(
                "playerCar" to playerCar,
                "playerPosition" to playerPosition,
                "raceResults" to raceResults,
                "multiplier" to multiplier
            )
            
            return provider.createGameResult(
                gameId = provider.generateGameId(),
                gameType = gameType,
                playerWon = playerWon,
                winAmount = winAmount,
                coinType = coinType,
                playAmount = amount,
                multiplier = if (playerWon) multiplier else BigDecimal.ZERO,
                gameData = resultData
            )
        }
        
        override fun getGameRules(): GameRules {
            return GameRules(
                gameType = gameType,
                description = "Choose your car and race! Top 3 positions win with different multipliers.",
                minBetAmount = minBetAmount,
                maxBetAmount = maxBetAmount,
                supportedCoins = supportedCoins,
                rtpPercentage = rtpPercentage,
                maxWinMultiplier = BigDecimal("6"),
                houseEdgePercentage = BigDecimal("6"),
                volatilityLevel = VolatilityLevel.MEDIUM,
                specialFeatures = listOf("Racing simulation", "Multiple positions", "Variable payouts"),
                customRules = mapOf(
                    "numCars" to 6,
                    "firstPlaceMultiplier" to 6,
                    "secondPlaceMultiplier" to 3,
                    "thirdPlaceMultiplier" to 2
                )
            )
        }
    }
    
    /**
     * Main example showing how to integrate games with payments
     */
    suspend fun main() {
        // Initialize the payment SDK
        val paymentSDK = FreetimePaymentSDK()
        
        // Configure developer wallet (this is where you receive payments)
        paymentSDK.setUserWalletAddress(
            coinType = CoinType.BITCOIN,
            address = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            name = "Game Revenue Wallet"
        )
        
        paymentSDK.setUserWalletAddress(
            coinType = CoinType.ETHEREUM,
            address = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
            name = "Game Revenue Wallet ETH"
        )
        
        // Get the games SDK
        val gamesSDK = paymentSDK.getGamesSDK()
        
        // Register custom games (no built-in games)
        gamesSDK.registerGame("number_guess", NumberGuessGame())
        gamesSDK.registerGame("racing", RacingGame())
        
        // Show available games
        println("=== Available Games ===")
        gamesSDK.getAvailableGames().forEach { (gameId, game) ->
            println("$gameId: ${game.gameType.displayName}")
            println("  Min bet: ${game.minBetAmount} BTC")
            println("  Max bet: ${game.maxBetAmount} BTC")
            println("  RTP: ${game.rtpPercentage}%")
            println()
        }
        
        // Simulate players playing games
        simulatePlayerGameplay(gamesSDK)
        
        // Show developer analytics
        showDeveloperAnalytics(gamesSDK)
    }
    
    private suspend fun simulatePlayerGameplay(gamesSDK: FreetimeGamesSDK) {
        println("=== Simulating Player Gameplay ===")
        
        // Player 1 plays number guessing game
        val player1Result = gamesSDK.playGameWithPayment(
            playerId = "player_001",
            username = "Alice",
            gameId = "number_guess",
            amount = BigDecimal("0.01"),
            coinType = CoinType.BITCOIN,
            gameData = mapOf("guess" to 42, "range" to 100)
        )
        
        println("Player 1 (Alice): ${player1Result.gameResult.getFormattedSummary()}")
        println("Level: ${player1Result.playerProfile.currentLevel}")
        println("XP: ${player1Result.playerProfile.experiencePoints}")
        println()
        
        // Player 2 plays racing game
        val player2Result = gamesSDK.playGameWithPayment(
            playerId = "player_002", 
            username = "Bob",
            gameId = "racing",
            amount = BigDecimal("0.005"),
            coinType = CoinType.ETHEREUM,
            gameData = mapOf("car" to 3, "numCars" to 6)
        )
        
        println("Player 2 (Bob): ${player2Result.gameResult.getFormattedSummary()}")
        println("Level: ${player2Result.playerProfile.currentLevel}")
        println("New achievements: ${player2Result.playerProgress.newAchievements.size}")
        println()
        
        // Player 1 plays multiple games to show progress
        repeat(5) { i ->
            gamesSDK.playGameWithPayment(
                playerId = "player_001",
                username = "Alice", 
                gameId = "coin_flip",
                amount = BigDecimal("0.001"),
                coinType = CoinType.BITCOIN,
                gameData = mapOf("choice" to if (i % 2 == 0) "heads" else "tails")
            )
        }
        
        // Show updated player stats
        val player1Stats = gamesSDK.getPlayerStatistics("player_001")
        println("Alice's Updated Stats:")
        println("  Total games: ${player1Stats.totalGamesPlayed}")
        println("  Level: ${player1Stats.level}")
        println("  Win rate: ${player1Stats.winRate}%")
        println("  Achievements: ${player1Stats.achievementsUnlocked}/${player1Stats.totalAchievements}")
        println()
    }
    
    private fun showDeveloperAnalytics(gamesSDK: FreetimeGamesSDK) {
        println("=== Developer Analytics ===")
        
        // Game history
        val gameHistory = gamesSDK.getGameHistory()
        println("Total games played: ${gameHistory.size}")
        
        // Revenue analytics
        val totalRevenue = gameHistory.sumOf { it.playAmount }
        val totalPayouts = gameHistory.filter { it.playerWon }.sumOf { it.winAmount }
        val netRevenue = totalRevenue - totalPayouts
        
        println("Total revenue: ${totalRevenue.toPlainString()} BTC")
        println("Total payouts: ${totalPayouts.toPlainString()} BTC") 
        println("Net revenue: ${netRevenue.toPlainString()} BTC")
        
        // Game popularity
        val gamePopularity = gameHistory.groupBy { it.gameType }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
        
        println("\nGame Popularity:")
        gamePopularity.forEach { (gameType, count) ->
            println("  ${gameType.displayName}: $count plays")
        }
        
        // Leaderboard
        println("\nTop Players:")
        val leaderboard = gamesSDK.getLeaderboard(5)
        leaderboard.forEachIndexed { index, player ->
            println("  ${index + 1}. ${player.username} - Level ${player.level} (${player.totalGamesPlayed} games)")
        }
    }
    
    /**
     * Example of creating custom achievements
     */
    fun createCustomAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "number_guess_expert",
                name = "Number Guess Expert",
                description = "Win 5 number guessing games in a row",
                category = AchievementCategory.SPECIFIC_GAMES,
                requirement = AchievementRequirement(
                    type = RequirementType.WIN_STREAK,
                    target = 5,
                    gameType = GameType.CUSTOM_GAME
                ),
                reward = AchievementReward(
                    experiencePoints = 300,
                    title = "Expert Guesser",
                    badge = "number_expert"
                )
            ),
            Achievement(
                id = "racing_champion",
                name = "Racing Champion", 
                description = "Win 10 racing games",
                category = AchievementCategory.SPECIFIC_GAMES,
                requirement = AchievementRequirement(
                    type = RequirementType.TOTAL_WINS,
                    target = 10,
                    gameType = GameType.CUSTOM_GAME
                ),
                reward = AchievementReward(
                    experiencePoints = 500,
                    title = "Champion",
                    badge = "racing_champion"
                )
            )
        )
    }
}
