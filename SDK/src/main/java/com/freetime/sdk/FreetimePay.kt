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
     * Registers all default major cryptocurrency providers.
     * @param addresses A map of currency codes ("BTC", "ETH", "DOGE", etc.) to recipient addresses.
     */
    fun registerDefaultCryptoProviders(addresses: Map<String, String>) {
        addresses["BTC"]?.let { registerProvider(com.freetime.sdk.providers.BitcoinProvider(it)) }
        addresses["ETH"]?.let { registerProvider(com.freetime.sdk.providers.EthereumProvider(it)) }
        addresses["DOGE"]?.let { registerProvider(com.freetime.sdk.providers.DogecoinProvider(it)) }
        addresses["BCH"]?.let { registerProvider(com.freetime.sdk.providers.BitcoinCashProvider(it)) }
        addresses["DASH"]?.let { registerProvider(com.freetime.sdk.providers.DashProvider(it)) }
        addresses["ZEC"]?.let { registerProvider(com.freetime.sdk.providers.ZcashProvider(it)) }
        addresses["XRP"]?.let { registerProvider(com.freetime.sdk.providers.XRPProvider(it)) }
        addresses["ADA"]?.let { registerProvider(com.freetime.sdk.providers.CardanoProvider(it)) }
        addresses["DOT"]?.let { registerProvider(com.freetime.sdk.providers.PolkadotProvider(it)) }
        addresses["BNB"]?.let { registerProvider(com.freetime.sdk.providers.BNBProvider(it)) }
        addresses["XNO"]?.let { registerProvider(com.freetime.sdk.providers.NanoProvider(it)) }
        addresses["XMR"]?.let { registerProvider(com.freetime.sdk.providers.MoneroProvider(it)) }
        addresses["LTC"]?.let { registerProvider(com.freetime.sdk.providers.LitecoinProvider(it)) }
        addresses["SOL"]?.let { registerProvider(com.freetime.sdk.providers.SolanaProvider(it)) }
        addresses["XLM"]?.let { registerProvider(com.freetime.sdk.providers.StellarProvider(it)) }
        addresses["TRX"]?.let { registerProvider(com.freetime.sdk.providers.TronProvider(it)) }
        addresses["ALGO"]?.let { registerProvider(com.freetime.sdk.providers.AlgorandProvider(it)) }
        addresses["ATOM"]?.let { registerProvider(com.freetime.sdk.providers.CosmosProvider(it)) }
        addresses["XTZ"]?.let { registerProvider(com.freetime.sdk.providers.TezosProvider(it)) }
        addresses["AVAX"]?.let { registerProvider(com.freetime.sdk.providers.AvalancheProvider(it)) }
        addresses["MATIC"]?.let { registerProvider(com.freetime.sdk.providers.PolygonProvider(it)) }
        addresses["FTM"]?.let { registerProvider(com.freetime.sdk.providers.FantomProvider(it)) }
        addresses["NEAR"]?.let { registerProvider(com.freetime.sdk.providers.NearProvider(it)) }
        addresses["OP"]?.let { registerProvider(com.freetime.sdk.providers.OptimismProvider(it)) }
        addresses["ARB"]?.let { registerProvider(com.freetime.sdk.providers.ArbitrumProvider(it)) }
        addresses["BASE"]?.let { registerProvider(com.freetime.sdk.providers.BaseProvider(it)) }
        addresses["CELO"]?.let { registerProvider(com.freetime.sdk.providers.CeloProvider(it)) }
        addresses["EGLD"]?.let { registerProvider(com.freetime.sdk.providers.MultiversXProvider(it)) }
        addresses["HBAR"]?.let { registerProvider(com.freetime.sdk.providers.HederaProvider(it)) }
        addresses["APT"]?.let { registerProvider(com.freetime.sdk.providers.AptosProvider(it)) }
        addresses["SUI"]?.let { registerProvider(com.freetime.sdk.providers.SuiProvider(it)) }
        addresses["VET"]?.let { registerProvider(com.freetime.sdk.providers.VeChainProvider(it)) }
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
