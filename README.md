# Freetime Payment SDK for F-Droid

A fully open-source payment SDK designed specifically for F-Droid applications, providing cryptocurrency and traditional payment methods without any proprietary dependencies.

## 🌟 F-Droid Features

- **100% FOSS Compliant**: No proprietary services or dependencies
- **No Tracking**: Built with privacy in mind
- **Open Source**: Full source code transparency
- **F-Droid Ready**: Includes metadata and build configuration

## 🚀 Supported Payment Methods

### Cryptocurrency (Open Source)
- **Bitcoin (BTC)**: Direct blockchain transactions via open APIs
- **Ethereum (ETH)**: Including ERC-20 tokens via public nodes
- **Litecoin (LTC)**: Fast, low-fee blockchain payments
- **Monero (XMR)**: Privacy-focused cryptocurrency transactions
- **Lightning Network**: Instant, low-fee Bitcoin payments
- **Solana (SOL)**: High-performance blockchain with low fees
- **Cardano (ADA)**: Sustainable blockchain with staking support

### Traditional Payments (Open Source)
- **Bank Transfer**: Automatic IBAN/SWIFT generation and reference tracking
- **LibrePay**: Open-source donation platform integration
- **BitHub**: Community-driven payment processing
- **GitHub Sponsors**: Direct developer sponsorship platform

### Decentralized Finance (DeFi)
- **Uniswap**: Token swaps and liquidity pool interactions
- **DeFi Protocols**: Open-source decentralized finance integrations

### Advanced Features
- **Multi-Signature Wallets**: Secure multi-party transaction signing
- **NFT Payments**: Buy, sell, and trade NFTs with royalty support
- **Payment Routing**: Intelligent payment optimization and splitting
- **Cross-Chain Support**: Bridge between different blockchain networks

## 📦 F-Droid Installation

Add the SDK to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":payment-sdk"))
}
```

## 🔧 Quick Start for F-Droid

### 1. Initialize the F-Droid SDK

```kotlin
val sdk = FDroidPaymentSDK.Builder()
    .addBitcoinProvider(testnet = true)
    .addLitecoinProvider(testnet = true)
    .addMoneroProvider(testnet = true)
    .addEthereumProvider(testnet = true)
    .addBankTransferProvider()
    .addLibrePaymentProvider()
    .addBitHubProvider()
    .addGitHubSponsorsProvider("freetime-sdk")
    .addUniswapProvider(testnet = true)
    .addLightningNetworkProvider(testnet = true)
    .build()
```

### 2. Process a Bitcoin Payment

```kotlin
val request = PaymentRequest(
    amount = BigDecimal("0.001"),
    currency = "BTC",
    paymentMethod = PaymentMethod.CRYPTO,
    description = "F-Droid app donation",
    recipientAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
    senderAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
    privateKey = "your_private_key_here"
)

val result = sdk.processPayment(request)
```

### 3. Bank Transfer with Automatic Instructions

```kotlin
val request = PaymentRequest(
    amount = BigDecimal("25.00"),
    currency = "EUR",
    paymentMethod = PaymentMethod.BANK_TRANSFER,
    description = "F-Droid app purchase"
)

val result = sdk.processPayment(request)
if (result.isSuccess) {
    val bankProvider = BankTransferProvider()
    val instructions = bankProvider.generateBankTransferInstructions(request, result.transactionId!!)
    println(instructions.getFormattedInstructions())
}
```

### 4. LibrePay Integration

```kotlin
val request = PaymentRequest(
    amount = BigDecimal("10.00"),
    currency = "EUR",
    paymentMethod = PaymentMethod.DIGITAL_WALLET,
    description = "Support open-source development",
    returnUrl = "https://yourapp.com/success"
)

val result = sdk.processPayment(request)
if (result.isSuccess) {
    val libreProvider = LibrePaymentProvider()
    val paymentUrl = libreProvider.generatePaymentUrl(request, result.transactionId!!)
    // Open paymentUrl in browser or webview
}
```

## 🛡️ F-Droid Compliance

### What's Included (FOSS Compliant)
- ✅ Bitcoin blockchain integration
- ✅ Ethereum blockchain integration
- ✅ Bank transfer generation
- ✅ LibrePay open-source payments
- ✅ All dependencies are FOSS licensed

### What's Excluded (Proprietary)
- ❌ Stripe (proprietary service)
- ❌ PayPal (proprietary service)
- ❌ Google Pay (proprietary service)
- ❌ Apple Pay (proprietary service)
- ❌ Any closed-source payment SDKs

## 🏗️ Build Configuration

The SDK includes F-Droid compatible build configuration:

```gradle
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // No proprietary dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

## 📋 F-Droid Metadata

The SDK includes complete F-Droid metadata in `metadata/com.freetime.sdk.payment.txt`:

- **Categories**: Development
- **License**: MIT
- **Web Site**: Source code repository
- **Build Configuration**: Gradle-based, no proprietary dependencies

## 🔒 Security & Privacy

- **No Analytics**: No tracking or analytics built-in
- **Local Processing**: All validation happens locally
- **Open Source**: Full code transparency
- **Minimal Permissions**: Only network access for blockchain APIs
- **No Data Collection**: SDK doesn't collect user data

## 🌍 International Support

### Supported Currencies
- **Crypto**: BTC, ETH, and major ERC-20 tokens
- **Traditional**: EUR, USD, GBP, CHF, CAD, AUD
- **Bank Transfer**: Full IBAN/SWIFT support for EU/UK/US

### Localization
- Error messages and instructions available in multiple languages
- Currency formatting based on locale
- Bank transfer instructions localized by region

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run F-Droid specific tests
./gradlew testDebugUnitTest --tests "*FDroid*"
```

## 📚 Examples

See `FDroidPaymentExamples.kt` for complete usage examples:

- Bitcoin payments
- Bank transfers with IBAN generation
- LibrePay donations
- Multi-provider scenarios
- Fee comparisons
- Validation examples

## 🤝 Contributing

This is an open-source project designed for the F-Droid ecosystem. Contributions welcome!

### Development Guidelines
- All code must be FOSS licensed
- No proprietary dependencies
- Maintain F-Droid compatibility
- Include comprehensive tests
- Follow Kotlin coding conventions

## 📄 License

MIT License - Full F-Droid compatibility

## 🆘 Support

- **Issues**: GitHub Issues (no proprietary trackers)
- **Discussions**: GitHub Discussions
- **Documentation**: Inline code documentation
- **Community**: F-Droid forums and matrix rooms

---

*Built for the open-source community, by the open-source community.*
