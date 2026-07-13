package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Ethereum (ETH) Provider.
 * Uses the 'ethereum:' URI scheme (ERC-681).
 */
class EthereumProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Ethereum (ETH)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            // Mainnet is chain ID 1
            val uri = "ethereum:$recipientAddress@1?value=${request.amount}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            onResult(PaymentResult.Success("ethereum_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No Ethereum wallet found: ${e.message}"))
        }
    }
}
