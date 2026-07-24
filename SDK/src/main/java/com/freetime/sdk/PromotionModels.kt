package com.freetime.sdk

data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val targetUrl: String
)

data class PromotionResponse(
    val version: Int,
    val promotions: List<Promotion>
)
