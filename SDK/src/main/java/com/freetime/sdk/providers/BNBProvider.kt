package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * BNB Provider.
 * Uses the 'bnb:' URI scheme.
 */
class BNBProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "BNB"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "bnb:$recipientAddress?amount=${request.amount}&label=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("bnb_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No BNB wallet found: ${e.message}"))
        }
    }
}
