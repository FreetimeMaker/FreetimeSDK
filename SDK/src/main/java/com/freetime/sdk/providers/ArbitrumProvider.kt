package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Arbitrum One (ARB) Provider.
 * Uses the 'ethereum:' URI scheme with Chain ID 42161.
 */
class ArbitrumProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Arbitrum One (ARB)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "ethereum:$recipientAddress@42161?value=${request.amount}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("arbitrum_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Arbitrum wallet found: ${e.message}"))
        }
    }
}
