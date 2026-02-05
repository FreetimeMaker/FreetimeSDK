package com.freetime.sdk.payment

data class PaymentResult(
    val success: Boolean,
    val transactionId: String? = null,
    val message: String,
    val error: PaymentError? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun success(transactionId: String, message: String) = 
            PaymentResult(success = true, transactionId = transactionId, message = message)
        
        fun failure(error: PaymentError, message: String) = 
            PaymentResult(success = false, message = message, error = error)
    }
}
