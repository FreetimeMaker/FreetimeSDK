package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * MultiversX (EGLD) Provider.
 * Uses the 'multiversx:' URI scheme.
 * Converts amount to atto-EGLD (1 EGLD = 10^18 atto-EGLD).
 */
class MultiversXProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "MultiversX (EGLD)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            // Using BigInteger for atto-EGLD precision
            val amountInAtto = (java.math.BigDecimal(request.amount) * java.math.BigDecimal("1000000000000000000")).toBigInteger()
            val uri = "multiversx:$recipientAddress?amount=$amountInAtto&data=${Uri.encode(request.description)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("multiversx_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No MultiversX wallet found: ${e.message}"))
        }
    }
}
