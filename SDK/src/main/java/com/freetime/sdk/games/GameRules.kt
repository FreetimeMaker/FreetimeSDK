package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal

/**
 * Represents the rules and configuration for a game
 */
data class GameRules(
    val gameType: GameType,
    val description: String,
    val minBetAmount: BigDecimal,
    val maxBetAmount: BigDecimal,
    val supportedCoins: List<CoinType>,
    val rtpPercentage: BigDecimal,
    val maxWinMultiplier: BigDecimal,
    val houseEdgePercentage: BigDecimal,
    val volatilityLevel: VolatilityLevel,
    val specialFeatures: List<String> = emptyList(),
    val customRules: Map<String, Any> = emptyMap()
)

/**
 * Volatility levels for games
 */
enum class VolatilityLevel(val displayName: String, val description: String) {
    LOW("Low", "Frequent small wins, low risk"),
    MEDIUM("Medium", "Balanced win frequency and amounts"),
    HIGH("High", "Infrequent large wins, high risk"),
    VERY_HIGH("Very High", "Rare but very large wins, very high risk")
}
