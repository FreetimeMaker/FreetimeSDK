package com.freetime.sdk

/**
 * Configuration for the SDK.
 */
data class DeveloperConfig(
    val developerId: String,
    val enablePromotions: Boolean = true,
    val customPromotionUrl: String? = null,
    val hideDefaultPromotions: Boolean = false
)
