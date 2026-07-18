package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Stellar (XLM) Provider.
 * Uses the 'web+stellar:pay' URI scheme (SEP-7).
 */
class StellarProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Stellar (XLM)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "web+stellar:pay?destination=$recipientAddress&amount=${request.amount}&memo=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("stellar_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Stellar wallet found: ${e.message}"))
        }
    }
}
