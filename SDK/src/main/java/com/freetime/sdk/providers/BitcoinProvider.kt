package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Bitcoin (BTC) Provider.
 * Uses the 'bitcoin:' URI scheme (BIP21).
 */
class BitcoinProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Bitcoin (BTC)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "bitcoin:$recipientAddress?amount=${request.amount}&label=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("bitcoin_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Bitcoin wallet found: ${e.message}"))
        }
    }
}
