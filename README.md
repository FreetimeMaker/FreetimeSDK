# Freetime Multi-Provider Payment SDK (F-Droid Friendly)

This SDK enables the integration of real payment providers into Android applications without relying on proprietary binary blobs. It is fully open-source, serverless, and ideal for F-Droid.

## Features

- **F-Droid Friendly**: No proprietary SDKs. Uses Web Flows and native Intents.
- **Serverless**: Designed to work without any backend infrastructure.
- **Real Providers**: Supports RevenueCat (Web Billing) and a wide range of Cryptocurrencies.
- **Crypto-Ready**: Native support for BTC, ETH, Base, XMR, LTC, and SOL via Wallet Intents.

## Supported Providers

1.  **RevenueCat (Web Billing)**: Secure fiat payments and subscriptions.
2.  **Bitcoin (BTC)**: Standard BIP21 payments.
3.  **Ethereum (ETH)**: ERC-681 payments on Ethereum Mainnet.
4.  **Base Pay (L2)**: Low-fee crypto payments on the Base network.
5.  **Monero (XMR)**: Privacy-focused direct P2P payments.
6.  **Litecoin (LTC)**: Fast and low-fee payments.
7.  **Solana (SOL)**: High-speed crypto payments.

## Installation

```kotlin
dependencies {
    implementation(project(":SDK"))
}
```

## Quick Start

### 1. Configuration
```kotlin
val config = DeveloperConfig(developerId = "your_id")
val sdk = FreetimePay(config)
```

### 2. Register Providers
```kotlin
// RevenueCat via Web Billing
sdk.registerProvider(RevenueCatWebProvider("https://checkout.revenuecat.com/your_link"))

// Crypto Providers
sdk.registerProvider(BitcoinProvider("BTC_ADDRESS"))
sdk.registerProvider(EthereumProvider("ETH_ADDRESS"))
sdk.registerProvider(BaseDirectProvider("BASE_ADDRESS"))
sdk.registerProvider(MoneroProvider("XMR_ADDRESS"))
sdk.registerProvider(LitecoinProvider("LTC_ADDRESS"))
sdk.registerProvider(SolanaProvider("SOL_ADDRESS"))
```

### 3. Start Payment
```kotlin
val request = PaymentRequest(
    amount = 5.0,
    currency = "USD",
    description = "Premium Support"
)

sdk.showPaymentSheet(this, request) { result ->
    when (result) {
        is PaymentResult.Success -> println("Success! Transaction ID: ${result.transactionId}")
        is PaymentResult.Error -> println("Error: ${result.message}")
    }
}
```

## License
Apache-2.0
