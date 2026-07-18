package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult
import java.util.UUID

/**
 * Sui (SUI) Provider.
 * Uses the 'sui:pay' URI scheme.
 * Converts amount to MIST (1 SUI = 10^9 MIST).
 */
class SuiProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Sui (SUI)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val mist = (request.amount * 1_000_000_000).toLong()
            val nonce = UUID.randomUUID().toString()
            val uri = "sui:pay?receiver=$recipientAddress&amount=$mist&coinType=0x2::sui::SUI&nonce=$nonce&message=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("sui_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Sui wallet found: ${e.message}"))
        }
    }
}
