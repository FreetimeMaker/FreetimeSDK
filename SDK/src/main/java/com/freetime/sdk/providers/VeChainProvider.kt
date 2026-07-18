package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * VeChain (VET) Provider.
 * Uses the 'vechain:' URI scheme.
 * Converts amount to wei (1 VET = 10^18 wei).
 */
class VeChainProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "VeChain (VET)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val amountInWei = (java.math.BigDecimal(request.amount) * java.math.BigDecimal("1000000000000000000")).toBigInteger()
            val uri = "vechain:$recipientAddress@4a?value=$amountInWei"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("vechain_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No VeChain wallet found: ${e.message}"))
        }
    }
}
