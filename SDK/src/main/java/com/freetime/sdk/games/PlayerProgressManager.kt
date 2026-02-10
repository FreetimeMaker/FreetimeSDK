package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages player profiles, achievements, and progress tracking
 */
class PlayerProgressManager {
    private val playerProfiles = ConcurrentHashMap<String, PlayerProfile>()
    private val allAchievements = Achievements.getAllAchievements().associateBy { it.id }
    
    /**
     * Create a new player profile
     */
    fun createPlayerProfile(playerId: String, username: String): PlayerProfile {
        val profile = PlayerProfile(
            playerId = playerId,
            username = username,
            totalWagered = CoinType.values().associateWith { BigDecimal.ZERO },
            totalWon = CoinType.values().associateWith { BigDecimal.ZERO }
        )
        playerProfiles[playerId] = profile
        return profile
    }
    
    /**
     * Get player profile by ID
     */
    fun getPlayerProfile(playerId: String): PlayerProfile? {
        return playerProfiles[playerId]
    }
    
    /**
     * Update player profile after a game
     */
    fun updatePlayerProgress(playerId: String, gameResult: GameResult): PlayerProgressUpdate {
        val profile = playerProfiles[playerId] ?: throw IllegalArgumentException("Player profile not found")
        
        // Update basic stats
        profile.totalGamesPlayed++
        profile.lastPlayedAt = LocalDateTime.now()
        
        // Update wagered amounts
        val currentWagered = profile.totalWagered[gameResult.coinType] ?: BigDecimal.ZERO
        profile.totalWagered = profile.totalWagered + (gameResult.coinType to (currentWagered + gameResult.playAmount))
        
        // Update win amounts and streak
        if (gameResult.playerWon) {
            val currentWon = profile.totalWon[gameResult.coinType] ?: BigDecimal.ZERO
            profile.totalWon = profile.totalWon + (gameResult.coinType to (currentWon + gameResult.winAmount))
            profile.winStreak++
            profile.bestWinStreak = maxOf(profile.bestWinStreak, profile.winStreak)
        } else {
            profile.winStreak = 0
        }
        
        // Update favorite game
        updateFavoriteGame(profile, gameResult.gameType)
        
        // Check for new achievements
        val newAchievements = checkAchievements(profile, gameResult)
        
        // Award experience points
        val xpGained = calculateExperiencePoints(gameResult)
        val leveledUp = profile.addExperiencePoints(xpGained)
        
        return PlayerProgressUpdate(
            profile = profile,
            newAchievements = newAchievements,
            experienceGained = xpGained,
            leveledUp = leveledUp
        )
    }
    
    /**
     * Check and unlock achievements
     */
    private fun checkAchievements(profile: PlayerProfile, gameResult: GameResult): List<Achievement> {
        val newAchievements = mutableListOf<Achievement>()
        
        allAchievements.values.forEach { achievement ->
            if (!profile.hasAchievement(achievement.id) && isAchievementUnlocked(profile, achievement, gameResult)) {
                val unlockedAchievement = achievement.copy(unlockedAt = LocalDateTime.now())
                newAchievements.add(unlockedAchievement)
                
                // Add achievement to profile
                profile.achievements = profile.achievements + unlockedAchievement
                
                // Award achievement rewards
                profile.addExperiencePoints(achievement.reward.experiencePoints)
            }
        }
        
        return newAchievements
    }
    
    /**
     * Check if an achievement is unlocked
     */
    private fun isAchievementUnlocked(profile: PlayerProfile, achievement: Achievement, gameResult: GameResult): Boolean {
        return when (achievement.requirement.type) {
            RequirementType.TOTAL_GAMES_PLAYED -> profile.totalGamesPlayed >= achievement.requirement.target
            RequirementType.TOTAL_WINS -> {
                val totalWon = profile.totalWon.values.fold(BigDecimal.ZERO) { acc, amount -> acc + amount }
                val totalWagered = profile.getTotalWagered()
                totalWon > BigDecimal.ZERO && 
                ((totalWon / totalWagered) * BigDecimal("100")).toInt() >= achievement.requirement.target
            }
            RequirementType.WIN_STREAK -> profile.winStreak >= achievement.requirement.target
            RequirementType.WAGER_AMOUNT -> profile.getTotalWagered() >= BigDecimal(achievement.requirement.target)
            RequirementType.GAME_TYPE_PLAYS -> gameResult.gameType == achievement.requirement.gameType
            RequirementType.CRYPTOCURRENCY_WAGER -> {
                val coinWagered = profile.totalWagered[achievement.requirement.coinType] ?: BigDecimal.ZERO
                coinWagered >= BigDecimal(achievement.requirement.target)
            }
            RequirementType.CONSECUTIVE_DAYS -> {
                // Implementation would track daily play sessions
                false // Simplified for now
            }
            RequirementType.BIG_WIN -> gameResult.multiplier >= BigDecimal(achievement.requirement.target)
        }
    }
    
    /**
     * Calculate experience points for a game
     */
    private fun calculateExperiencePoints(gameResult: GameResult): Int {
        val baseXP = 10
        val winBonus = if (gameResult.playerWon) 20 else 0
        val multiplierBonus = (gameResult.multiplier.toInt() - 1) * 5
        return baseXP + winBonus + multiplierBonus
    }
    
    /**
     * Update player's favorite game
     */
    private fun updateFavoriteGame(profile: PlayerProfile, gameType: GameType) {
        // Simple implementation - could be enhanced with actual play tracking
        if (profile.favoriteGame == null) {
            profile.favoriteGame = gameType
        }
    }
    
    /**
     * Get player statistics
     */
    fun getPlayerStatistics(playerId: String): PlayerStatistics? {
        val profile = playerProfiles[playerId] ?: return null
        
        return PlayerStatistics(
            playerId = profile.playerId,
            username = profile.username,
            level = profile.currentLevel,
            experiencePoints = profile.experiencePoints,
            totalGamesPlayed = profile.totalGamesPlayed,
            totalWagered = profile.getTotalWagered(),
            totalWon = profile.getTotalWon(),
            netProfit = profile.getNetProfit(),
            winRate = profile.getWinRate(),
            currentWinStreak = profile.winStreak,
            bestWinStreak = profile.bestWinStreak,
            achievementsUnlocked = profile.achievements.size,
            totalAchievements = allAchievements.size,
            favoriteGame = profile.favoriteGame,
            joinedAt = profile.createdAt,
            lastPlayedAt = profile.lastPlayedAt
        )
    }
    
    /**
     * Get all achievements for a player
     */
    fun getPlayerAchievements(playerId: String): Set<Achievement> {
        return playerProfiles[playerId]?.achievements ?: emptySet()
    }
    
    /**
     * Get leaderboard
     */
    fun getLeaderboard(limit: Int = 10): List<PlayerStatistics> {
        return playerProfiles.values
            .map { getPlayerStatistics(it.playerId)!! }
            .sortedByDescending { it.experiencePoints }
            .take(limit)
    }
}

/**
 * Result of player progress update
 */
data class PlayerProgressUpdate(
    val profile: PlayerProfile,
    val newAchievements: List<Achievement>,
    val experienceGained: Int,
    val leveledUp: Boolean
)

/**
 * Player statistics for display
 */
data class PlayerStatistics(
    val playerId: String,
    val username: String,
    val level: Int,
    val experiencePoints: Int,
    val totalGamesPlayed: Int,
    val totalWagered: BigDecimal,
    val totalWon: BigDecimal,
    val netProfit: BigDecimal,
    val winRate: BigDecimal,
    val currentWinStreak: Int,
    val bestWinStreak: Int,
    val achievementsUnlocked: Int,
    val totalAchievements: Int,
    val favoriteGame: GameType?,
    val joinedAt: LocalDateTime,
    val lastPlayedAt: LocalDateTime?
)
