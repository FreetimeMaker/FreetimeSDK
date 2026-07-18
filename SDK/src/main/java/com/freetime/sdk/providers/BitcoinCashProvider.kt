package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Bitcoin Cash (BCH) Provider.
 * Uses the 'bitcoincash:' URI scheme.
 */
class BitcoinCashProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Bitcoin Cash (BCH)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "bitcoincash:$recipientAddress?amount=${request.amount}&label=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("bitcoincash_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Bitcoin Cash wallet found: ${e.message}"))
        }
    }
}
