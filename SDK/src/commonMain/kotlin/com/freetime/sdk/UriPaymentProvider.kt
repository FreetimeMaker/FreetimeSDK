package com.freetime.sdk

abstract class UriPaymentProvider(
    override val name: String,
    val recipientAddress: String
) : PaymentProvider {
    
    abstract fun buildUri(request: PaymentRequest): String

    override fun processPayment(
        context: Any?,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = buildUri(request)
            openPaymentUri(uri, context)
            onResult(PaymentResult.Success("uri_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("Failed to launch payment: ${e.message}"))
        }
    }
}

fun urlEncode(s: String): String {
    val allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.~"
    return buildString {
        for (char in s) {
            if (char in allowed) {
                append(char)
            } else {
                // Simplistic encoding for non-allowed characters
                append("%${char.code.toString(16).uppercase()}")
            }
        }
    }
}
