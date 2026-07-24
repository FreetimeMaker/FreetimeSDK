# <img src="media/sdk_logo.png" width="48" height="48" valign="middle"> Freetime Multi-Provider Payment SDK (F-Droid Friendly)

This SDK enables the integration of real payment providers into Android applications without relying on proprietary binary blobs. It is fully open-source, serverless, and ideal for F-Droid.

## Features

- **F-Droid Friendly**: No proprietary SDKs. Uses Web Flows and native Intents.
- **Serverless**: Designed to work without any backend infrastructure.
- **Promotion System**: Display "Featured Projects" anywhere in your app. Privacy-friendly and fully configurable.
- **Real Providers**: Supports RevenueCat (Web Billing) and a wide range of Cryptocurrencies.
- **Crypto-Ready**: Native support for 30+ major cryptocurrencies and Layer 2s (BTC, ETH, SOL, OP, ARB, BASE, etc.).
- **Modern Infrastructure**: Built with Kotlin 2.4 and target SDK 37.

## Promotion System

The SDK includes a flexible promotion system to monetize your app ethically.

### 1. Default Placement
By default, a promotion is shown at the bottom of the payment selection sheet.

### 2. Manual Placement
Use the `PromotionView` component to show featured projects anywhere in your app:

```xml
<com.freetime.sdk.PromotionView
    android:id="@+id/promotion"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
val promoView = findViewById<PromotionView>(R.id.promotion)
promoView.loadPromotion(sdk.config)
```

### 3. Custom Content
Developers can provide their own JSON list of promotions via `DeveloperConfig(customPromotionUrl = "...")` or opt-out entirely for free.

## Supported Providers

1.  **RevenueCat (Web Billing)**: Secure fiat payments and subscriptions.
2.  **Cryptocurrencies**: Comprehensive support for 32 major assets and networks:
    - **Legacy/Major**: BTC, ETH, DOGE, LTC, BCH, TRX, XLM, DASH, ZEC, XMR, XRP.
    - **High Performance L1s**: SOL, ADA, DOT, ALGO, ATOM, NEAR, EGLD, HBAR, APT, SUI, VET, XTZ.
    - **Layer 2s & EVMs**: OP, ARB, BASE, CELO, AVAX, MATIC, FTM.
    - **Exchange/Native**: BNB, XNO.

## Installation

```kotlin
dependencies {
    implementation("com.freetime:sdk:1.3.0")
}
```

## Quick Start

### 1. Configuration
```kotlin
val config = DeveloperConfig(developerId = "your_id")
val sdk = FreetimePay(config)
```

### 2. Register Providers

#### Batch Registration for Crypto
The easiest way to register all supported cryptocurrencies is using a map of your addresses:

```kotlin
val addresses = mapOf(
    "BTC" to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "ETH" to "0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe",
    "SOL" to "7p2...",
    "XMR" to "44AFFq...",
    // ... add any other addresses for the tokens you wish to support
)

sdk.registerDefaultCryptoProviders(addresses)
```

#### Manual Registration
You can still register providers individually if needed:

```kotlin
sdk.registerProvider(RevenueCatWebProvider("https://checkout.revenuecat.com/your_link"))
sdk.registerProvider(BitcoinProvider("BTC_ADDRESS"))
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
        is PaymentResult.Cancelled -> println("User cancelled")
    }
}
```

## License
Apache-2.0
