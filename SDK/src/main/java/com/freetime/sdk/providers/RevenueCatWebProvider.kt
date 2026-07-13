package com.freetime.sdk.providers

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.freetime.sdk.PaymentProvider
import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.PaymentResult

/**
 * RevenueCat Provider using Web Billing.
 * F-Droid friendly.
 */
class RevenueCatWebProvider(private val baseCheckoutUrl: String) : PaymentProvider {
    override val name: String = "RevenueCat (Card/Subscription)"

    override fun processPayment(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val url = Uri.parse(baseCheckoutUrl).buildUpon()
                .appendQueryParameter("app_user_id", request.metadata["user_id"] ?: "anonymous")
                .appendQueryParameter("amount", request.amount.toString())
                .build()
                .toString()

            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(activity, Uri.parse(url))
            
            onResult(PaymentResult.Success("revenuecat_web_opened", request.amount))
        } catch (e: Exception) {
            onResult(PaymentResult.Error("Failed to open checkout: ${e.message}"))
        }
    }
}
