package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Optimism (OP) Provider.
 * Uses the 'ethereum:' URI scheme with Chain ID 10.
 */
class OptimismProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Optimism (OP)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "ethereum:$recipientAddress@10?value=${request.amount}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("optimism_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Optimism wallet found: ${e.message}"))
        }
    }
}
