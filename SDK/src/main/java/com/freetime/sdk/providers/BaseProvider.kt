package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Base Provider.
 * Uses the 'ethereum:' URI scheme with Chain ID 8453.
 */
class BaseProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Base"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "ethereum:$recipientAddress@8453?value=${request.amount}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("base_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Base wallet found: ${e.message}"))
        }
    }
}
