package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Algorand (ALGO) Provider.
 * Uses the 'algorand:' URI scheme.
 * Converts amount to microAlgos (1 ALGO = 1,000,000 microAlgos).
 */
class AlgorandProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Algorand (ALGO)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val microAlgos = (request.amount * 1_000_000).toLong()
            val uri = "algorand://$recipientAddress?amount=$microAlgos&note=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("algorand_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Algorand wallet found: ${e.message}"))
        }
    }
}
