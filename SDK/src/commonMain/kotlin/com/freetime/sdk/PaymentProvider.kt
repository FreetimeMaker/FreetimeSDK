package com.freetime.sdk

/**
 * Interface for all payment providers.
 */
interface PaymentProvider {
    val name: String

    /**
     * Initiates the payment process.
     * @param context The platform-specific context (e.g., Activity on Android, null elsewhere).
     * @param request The payment request details.
     * @param onResult Callback for the result.
     */
    fun processPayment(
        context: Any?,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    )
}

/**
 * Platform-specific URI launcher.
 */
expect fun openPaymentUri(uri: String, context: Any?)
