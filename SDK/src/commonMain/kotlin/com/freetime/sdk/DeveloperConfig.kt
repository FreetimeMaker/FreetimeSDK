package com.freetime.sdk

import kotlinx.serialization.Serializable

@Serializable
data class DeveloperConfig(
    val developerId: String,
    val enablePromotions: Boolean = true,
    val customPromotionUrl: String? = null,
    val hideDefaultPromotions: Boolean = false
)
