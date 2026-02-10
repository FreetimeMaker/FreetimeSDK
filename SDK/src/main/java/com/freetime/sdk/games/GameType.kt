package com.freetime.sdk.games

/**
 * Enumeration of supported game types in the Freetime Games SDK
 */
enum class GameType(val displayName: String, val description: String) {
    LOTTERY("Lottery", "Traditional lottery game with number selection"),
    SCRATCH_CARD("Scratch Card", "Instant win scratch card game"),
    SLOT_MACHINE("Slot Machine", "Classic slot machine game"),
    DICE_ROLL("Dice Roll", "Simple dice rolling game"),
    COIN_FLIP("Coin Flip", "Heads or tails coin flip game"),
    WHEEL_OF_FORTUNE("Wheel of Fortune", "Spin the wheel for prizes"),
    BINGO("Bingo", "Classic bingo game"),
    BLACKJACK("Blackjack", "Card game against the dealer"),
    ROULETTE("Roulette", "Casino roulette game"),
    POKER("Poker", "Texas Hold'em poker game")
}
