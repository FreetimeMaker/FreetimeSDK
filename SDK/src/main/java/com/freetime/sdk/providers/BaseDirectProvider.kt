package com.freetime.sdk.providers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * Base Pay Provider (Coinbase L2 Network).
 * Serverless & F-Droid friendly: Uses native Wallet Intents (ERC-681).
 */
class BaseDirectProvider(private val recipientAddress: String) : PaymentProvider {
    override val name: String = "Base Pay (USDC/ETH)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            // Ethereum standard URI for Base L2 (Chain ID 8453)
            val uri = "ethereum:$recipientAddress@8453?value=${request.amount}"
            
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            activity.startActivity(intent)
            
            onResult(PaymentResult.Success("base_intent_launched", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("No compatible wallet found: ${e.message}"))
        }
    }
}
