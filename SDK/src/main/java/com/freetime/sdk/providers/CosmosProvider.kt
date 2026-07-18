package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Cosmos Hub (ATOM) Provider.
 * Uses the 'cosmos:' URI scheme.
 * Converts amount to uatom (1 ATOM = 1,000,000 uatom).
 */
class CosmosProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Cosmos Hub (ATOM)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uatom = (request.amount * 1_000_000).toLong()
            val uri = "cosmos:$recipientAddress?amount=$uatom&denom=uatom&memo=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("cosmos_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Cosmos wallet found: ${e.message}"))
        }
    }
}
