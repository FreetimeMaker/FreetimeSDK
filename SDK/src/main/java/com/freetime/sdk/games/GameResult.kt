package com.freetime.sdk.games

import com.freetime.sdk.payment.CoinType
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Represents the result of a game play
 */
data class GameResult(
    val gameId: String,
    val gameType: GameType,
    val playerWon: Boolean,
    val winAmount: BigDecimal,
    val coinType: CoinType,
    val playAmount: BigDecimal,
    val timestamp: LocalDateTime,
    val gameData: Map<String, Any> = emptyMap(),
    val transactionId: String? = null,
    val multiplier: BigDecimal = BigDecimal.ONE
) {
    /**
     * Returns the net profit/loss for the player
     */
    fun getNetProfit(): BigDecimal {
        return if (playerWon) winAmount - playAmount else -playAmount
    }
    
    /**
     * Returns the return to player (RTP) percentage
     */
    fun getRTP(): BigDecimal {
        return if (playAmount > BigDecimal.ZERO) {
            (winAmount / playAmount) * BigDecimal("100")
        } else BigDecimal.ZERO
    }
    
    /**
     * Checks if this is a winning result
     */
    fun isWinning(): Boolean = playerWon && winAmount > BigDecimal.ZERO
    
    /**
     * Gets formatted result summary
     */
    fun getFormattedSummary(): String {
        return if (playerWon) {
            "WIN: ${winAmount.toPlainString()} ${coinType.coinName} (Multiplier: ${multiplier}x)"
        } else {
            "LOSS: ${playAmount.toPlainString()} ${coinType.coinName}"
        }
    }
}
