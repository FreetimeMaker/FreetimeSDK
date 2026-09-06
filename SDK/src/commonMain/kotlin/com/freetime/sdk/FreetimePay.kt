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
            "BTC" to ("Bitcoin (BTC)" to "bitcoin"),
            "ETH" to ("Ethereum (ETH)" to "ethereum"),
            "DOGE" to ("Dogecoin (DOGE)" to "doge"),
            "LTC" to ("Litecoin (LTC)" to "litecoin"),
            "BCH" to ("Bitcoin Cash (BCH)" to "bitcoincash"),
            "DASH" to ("Dash (DASH)" to "dash"),
            "XRP" to ("XRP (XRP)" to "xrp"),
            "SOL" to ("Solana (SOL)" to "solana"),
            "MATIC" to ("Polygon (MATIC)" to "polygon")
        )

        for ((symbol, config) in cryptoMap) {
            val address = addresses[symbol]
            if (address != null) {
                registerProvider(CryptoProvider(config.first, config.second, address))
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
        val provider = providers.find { it.name.equals(providerName, ignoreCase = true) }
        if (provider == null) {
            onResult(PaymentResult.Error("Provider not found: $providerName"))
            return
        }

        provider.processPayment(context, request) { result ->
            onResult(result)
        }
    }
}
