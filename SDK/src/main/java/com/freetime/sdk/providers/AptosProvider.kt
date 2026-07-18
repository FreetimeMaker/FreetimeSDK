package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Aptos (APT) Provider.
 * Uses the 'aptos:' URI scheme.
 * Converts amount to Octas (1 APT = 10^8 Octas).
 */
class AptosProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Aptos (APT)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val octas = (request.amount * 100_000_000).toLong()
            val uri = "aptos:$recipientAddress?amount=$octas&label=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("aptos_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Aptos wallet found: ${e.message}"))
        }
    }
}
