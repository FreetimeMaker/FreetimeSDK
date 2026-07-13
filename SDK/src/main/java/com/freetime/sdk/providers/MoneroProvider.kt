package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Monero (XMR) Provider.
 * Completely Open Source and F-Droid friendly.
 */
class MoneroProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Monero (XMR)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "monero:$recipientAddress?tx_amount=${request.amount}&tx_description=${Uri.encode(request.description)}"
            
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            
            onResult(PaymentResult.Success("monero_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Monero wallet found: ${e.message}"))
        }
    }
}
