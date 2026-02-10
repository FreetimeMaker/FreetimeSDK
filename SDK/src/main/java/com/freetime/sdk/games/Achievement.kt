package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Achievement system for player accomplishments
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val requirement: AchievementRequirement,
    val reward: AchievementReward,
    val icon: String? = null,
    val isHidden: Boolean = false,
    val unlockedAt: LocalDateTime? = null
)

/**
 * Achievement categories
 */
enum class AchievementCategory(val displayName: String) {
    GAMES_PLAYED("Games Played"),
    WINS("Wins"),
    WAGERING("Wagering"),
    STREAKS("Win Streaks"),
    SPECIFIC_GAMES("Specific Games"),
    CRYPTOCURRENCY("Cryptocurrency"),
    SPECIAL("Special")
}

/**
 * Achievement requirements
 */
data class AchievementRequirement(
    val type: RequirementType,
    val target: Int,
    val gameType: GameType? = null,
    val coinType: CoinType? = null
)

enum class RequirementType {
    TOTAL_GAMES_PLAYED,
    TOTAL_WINS,
    WIN_STREAK,
    WAGER_AMOUNT,
    GAME_TYPE_PLAYS,
    CRYPTOCURRENCY_WAGER,
    CONSECUTIVE_DAYS,
    BIG_WIN
}

/**
 * Achievement rewards
 */
data class AchievementReward(
    val experiencePoints: Int = 0,
    val bonusMultiplier: BigDecimal = BigDecimal.ONE,
    val title: String? = null,
    val badge: String? = null
)

/**
 * Predefined achievements
 */
object Achievements {
    val FIRST_GAME = Achievement(
        id = "first_game",
        name = "First Steps",
        description = "Play your first game",
        category = AchievementCategory.GAMES_PLAYED,
        requirement = AchievementRequirement(RequirementType.TOTAL_GAMES_PLAYED, 1),
        reward = AchievementReward(experiencePoints = 50)
    )
    
    val FIRST_WIN = Achievement(
        id = "first_win",
        name = "Winner!",
        description = "Win your first game",
        category = AchievementCategory.WINS,
        requirement = AchievementRequirement(RequirementType.TOTAL_WINS, 1),
        reward = AchievementReward(experiencePoints = 100)
    )
    
    val GAME_VETERAN = Achievement(
        id = "game_veteran",
        name = "Game Veteran",
        description = "Play 100 games",
        category = AchievementCategory.GAMES_PLAYED,
        requirement = AchievementRequirement(RequirementType.TOTAL_GAMES_PLAYED, 100),
        reward = AchievementReward(experiencePoints = 500, title = "Veteran")
    )
    
    val WIN_STREAK_5 = Achievement(
        id = "win_streak_5",
        name = "On Fire!",
        description = "Win 5 games in a row",
        category = AchievementCategory.STREAKS,
        requirement = AchievementRequirement(RequirementType.WIN_STREAK, 5),
        reward = AchievementReward(experiencePoints = 200)
    )
    
    val HIGH_ROLLER = Achievement(
        id = "high_roller",
        name = "High Roller",
        description = "Wager total of 1 BTC",
        category = AchievementCategory.WAGERING,
        requirement = AchievementRequirement(RequirementType.WAGER_AMOUNT, 100000000), // 1 BTC in satoshis
        reward = AchievementReward(experiencePoints = 1000, title = "High Roller")
    )
    
    val COIN_FLIP_MASTER = Achievement(
        id = "coin_flip_master",
        name = "Coin Flip Master",
        description = "Play 50 coin flip games",
        category = AchievementCategory.SPECIFIC_GAMES,
        requirement = AchievementRequirement(RequirementType.GAME_TYPE_PLAYS, 50, GameType.COIN_FLIP),
        reward = AchievementReward(experiencePoints = 300)
    )
    
    val BITCOIN_BELIEVER = Achievement(
        id = "bitcoin_believer",
        name = "Bitcoin Believer",
        description = "Wager 0.5 BTC in total",
        category = AchievementCategory.CRYPTOCURRENCY,
        requirement = AchievementRequirement(RequirementType.CRYPTOCURRENCY_WAGER, 50000000, coinType = com.freetime.sdk.payment.CoinType.BITCOIN),
        reward = AchievementReward(experiencePoints = 750)
    )
    
    fun getAllAchievements(): List<Achievement> {
        return listOf(
            FIRST_GAME,
            FIRST_WIN,
            GAME_VETERAN,
            WIN_STREAK_5,
            HIGH_ROLLER,
            COIN_FLIP_MASTER,
            BITCOIN_BELIEVER
        )
    }
}
