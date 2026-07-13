package com.freetime.sdk

import android.app.Activity
import android.util.Log

/**
 * The main entry point for the Freetime SDK.
 */
class FreetimePay(
    private val config: DeveloperConfig
) {
    private val providers = mutableListOf<PaymentProvider>()

    /**
     * Registers a payment provider.
     */
    fun registerProvider(provider: PaymentProvider) {
        providers.add(provider)
    }

    /**
     * Returns the list of available providers.
     */
    fun getAvailableProviders(): List<PaymentProvider> = providers

    /**
     * Shows a UI to let the user select a provider and then processes the payment.
     */
    fun showPaymentSheet(
        activity: Activity,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        PaymentSelectionActivity.launch(activity, this, request, onResult)
    }

    /**
     * Processes a payment with the selected provider.
     */
    fun processPayment(
        activity: Activity,
        providerName: String,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        val provider = providers.find { it.name.equals(providerName, ignoreCase = true) }
        if (provider == null) {
            onResult(PaymentResult.Error("Provider not found: $providerName"))
            return
        }

        Log.d("FreetimePay", "Processing payment: ${request.amount} ${request.currency} via $providerName")

        provider.processPayment(activity, request) { result ->
            onResult(result)
        }
    }
}
