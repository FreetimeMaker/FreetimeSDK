package com.freetime.sdk

import android.app.Activity

/**
 * Interface for all payment providers.
 */
interface PaymentProvider {
    val name: String
    
    /**
     * Initiates the payment process.
     * @param activity The current activity to launch UI if needed.
     * @param request The payment request details.
     * @param onResult Callback for the result.
     */
    fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    )
}
