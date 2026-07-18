package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Hedera (HBAR) Provider.
 * Uses the 'hedera:' URI scheme (HIP-110).
 */
class HederaProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Hedera (HBAR)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val uri = "hedera:$recipientAddress?amount=${request.amount}&memo=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("hedera_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Hedera wallet found: ${e.message}"))
        }
    }
}
