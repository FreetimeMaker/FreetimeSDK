package com.freetime.sdk

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val description: String,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
sealed class PaymentResult {
    @Serializable
    data class Success(val transactionId: String, val amount: Double) : PaymentResult()
    @Serializable
    data class Error(val message: String, val code: String? = null) : PaymentResult()
    @Serializable
    object Cancelled : PaymentResult()
}
