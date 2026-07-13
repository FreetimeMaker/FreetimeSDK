package com.freetime.sdk.providers

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult
import java.util.UUID

/**
 * A simulation provider for development and testing.
 */
class MockProvider : PaymentProvider {
    override val name: String = "Test Payment (Mock)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            onResult(PaymentResult.Success("MOCK-${UUID.randomUUID()}", request.amount))
        }, 1500)
    }
}
