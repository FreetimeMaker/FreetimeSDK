package com.freetime.sdk

import com.freetime.sdk.providers.CryptoProvider

/**
 * The main entry point for the Freetime SDK.
 */
class FreetimePay(
    val config: DeveloperConfig
) {
    private val providers = mutableListOf<PaymentProvider>()

    /**
     * Registers a payment provider.
     */
    fun registerProvider(provider: PaymentProvider) {
        providers.add(provider)
    }

    /**
     * Registers all default major cryptocurrency providers.
     * @param addresses A map of currency codes ("BTC", "ETH", "DOGE", etc.) to recipient addresses.
     */
    fun registerDefaultCryptoProviders(addresses: Map<String, String>) {
        val cryptoMap = mapOf(
            "BTC" to Triple("Bitcoin (BTC)", "bitcoin", "amount"),
            "ETH" to Triple("Ethereum (ETH)", "ethereum", "value"),
            "DOGE" to Triple("Dogecoin (DOGE)", "doge", "amount"),
            "LTC" to Triple("Litecoin (LTC)", "litecoin", "amount"),
            "BCH" to Triple("Bitcoin Cash (BCH)", "bitcoincash", "amount"),
            "DASH" to Triple("Dash (DASH)", "dash", "amount"),
            "XRP" to Triple("XRP (XRP)", "xrp", "amount"),
            "SOL" to Triple("Solana (SOL)", "solana", "amount"),
            "MATIC" to Triple("Polygon (MATIC)", "polygon", "amount")
        )

        for ((symbol, config) in cryptoMap) {
            val address = addresses[symbol]
            if (address != null) {
                registerProvider(CryptoProvider(config.first, config.second, address, config.third))
            }
        }
    }

    /**
     * Returns the list of available providers.
     */
    fun getAvailableProviders(): List<PaymentProvider> = providers

    /**
     * Processes a payment with the selected provider.
     * @param context Platform-specific context.
     */
    fun processPayment(
        context: Any?,
        providerName: String,
        request: PaymentRequest,
        onResult: (PaymentResult) -> Unit
    ) {
        try {
            val provider = providers.find { it.name.equals(providerName, ignoreCase = true) }
            if (provider == null) {
                onResult(PaymentResult.Error("Provider not found: $providerName"))
                return
            }

            provider.processPayment(context, request) { result ->
                onResult(result)
            }
        } catch (e: Exception) {
            onResult(PaymentResult.Error("FreetimeSDK internal error: ${e.message}"))
        }
    }
}
