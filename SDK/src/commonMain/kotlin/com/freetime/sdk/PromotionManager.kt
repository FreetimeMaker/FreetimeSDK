package com.freetime.sdk

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class PromotionManager(private val config: DeveloperConfig) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun fetchPromotions(): List<Promotion> {
        if (!config.enablePromotions) return emptyList()
        val url = config.customPromotionUrl ?: "https://raw.githubusercontent.com/FreetimeMaker/FreetimeSDK/master/promotions.json"
        return try {
            val response: PromotionResponse = client.get(url).body()
            response.promotions
        } catch (e: Exception) {
            emptyList()
        }
    }
}
