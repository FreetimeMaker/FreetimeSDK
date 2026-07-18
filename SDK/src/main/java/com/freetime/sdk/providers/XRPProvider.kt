package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Ripple (XRP) Provider.
 * Uses the 'ripple:' URI scheme.
 */
class XRPProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Ripple (XRP)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "ripple:$recipientAddress?amount=${request.amount}&label=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("ripple_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No XRP wallet found: ${e.message}"))
        }
    }
}
