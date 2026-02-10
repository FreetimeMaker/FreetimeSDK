package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Player profile for tracking game progress and achievements
 */
data class PlayerProfile(
    val playerId: String,
    val username: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var totalGamesPlayed: Int = 0,
    var totalWagered: Map<CoinType, BigDecimal> = emptyMap(),
    var totalWon: Map<CoinType, BigDecimal> = emptyMap(),
    var achievements: Set<Achievement> = emptySet(),
    var currentLevel: Int = 1,
    var experiencePoints: Int = 0,
    var favoriteGame: GameType? = null,
    var winStreak: Int = 0,
    var bestWinStreak: Int = 0,
    var lastPlayedAt: LocalDateTime? = null
) {
    /**
     * Get total wagered across all cryptocurrencies
     */
    fun getTotalWagered(): BigDecimal {
        return totalWagered.values.fold(BigDecimal.ZERO) { acc, amount -> acc + amount }
    }
    
    /**
     * Get total won across all cryptocurrencies
     */
    fun getTotalWon(): BigDecimal {
        return totalWon.values.fold(BigDecimal.ZERO) { acc, amount -> acc + amount }
    }
    
    /**
     * Get net profit/loss
     */
    fun getNetProfit(): BigDecimal {
        return getTotalWon() - getTotalWagered()
    }
    
    /**
     * Get overall win rate
     */
    fun getWinRate(): BigDecimal {
        return if (totalGamesPlayed > 0) {
            (totalWon.values.sumOf { it } / getTotalWagered()) * BigDecimal("100")
        } else BigDecimal.ZERO
    }
    
    /**
     * Check if player has a specific achievement
     */
    fun hasAchievement(achievementId: String): Boolean {
        return achievements.any { it.id == achievementId }
    }
    
    /**
     * Add experience points and check for level up
     */
    fun addExperiencePoints(points: Int): Boolean {
        experiencePoints += points
        val newLevel = calculateLevel(experiencePoints)
        val leveledUp = newLevel > currentLevel
        currentLevel = newLevel
        return leveledUp
    }
    
    /**
     * Calculate level based on experience points
     */
    private fun calculateLevel(xp: Int): Int {
        return (xp / 1000) + 1 // 1000 XP per level
    }
    
    /**
     * Get experience needed for next level
     */
    fun getExperienceToNextLevel(): Int {
        val nextLevelXp = currentLevel * 1000
        return nextLevelXp - experiencePoints
    }
}
