package com.freetime.sdk

/**
 * Represents a payment request.
 * @param amount The amount to be paid.
 * @param currency The currency code (e.g., "USD", "EUR").
 * @param description Description of the purchase.
 */
data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val description: String,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Result of a payment operation.
 */
sealed class PaymentResult {
    data class Success(val transactionId: String, val amount: Double) : PaymentResult()
    data class Error(val message: String, val code: String? = null) : PaymentResult()
    object Cancelled : PaymentResult()
}
