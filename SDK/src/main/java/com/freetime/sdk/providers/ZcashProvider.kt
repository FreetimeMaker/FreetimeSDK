package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Zcash (ZEC) Provider.
 * Uses the 'zcash:' URI scheme.
 */
class ZcashProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Zcash (ZEC)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "zcash:$recipientAddress?amount=${request.amount}&label=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("zcash_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Zcash wallet found: ${e.message}"))
        }
    }
}
