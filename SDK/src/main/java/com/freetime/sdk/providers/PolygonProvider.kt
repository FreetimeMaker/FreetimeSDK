package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Polygon (MATIC) Provider.
 * Uses the 'ethereum:' URI scheme with Chain ID 137.
 */
class PolygonProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Polygon (MATIC)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "ethereum:$recipientAddress@137?value=${request.amount}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("polygon_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Polygon wallet found: ${e.message}"))
        }
    }
}
