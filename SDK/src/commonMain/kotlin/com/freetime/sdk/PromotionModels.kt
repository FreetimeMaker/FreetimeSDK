package com.freetime.sdk

import kotlinx.serialization.Serializable

@Serializable
data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val targetUrl: String
)

@Serializable
data class PromotionResponse(
    val version: Int,
    val promotions: List<Promotion>
)
