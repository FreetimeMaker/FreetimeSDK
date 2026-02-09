# Freetime Payment SDK

[![](https://jitpack.io/v/FreetimeMaker/FreetimeSDK.svg)](https://jitpack.io/#FreetimeMaker/FreetimeSDK)

A completely self-contained, open-source multi-cryptocurrency payment SDK for Android.

## Features

- **Multi-Coin Support**: 9 cryptocurrencies including Bitcoin (BTC), Ethereum (ETH), Litecoin (LTC), Bitcoin Cash (BCH), Dogecoin (DOGE), Solana (SOL), Polygon (MATIC), Binance Coin (BNB), and Tron (TRX)
- **Developer Fee System**: Tiered fee structure (0.05% - 0.5%) with app.ncwallet.net-compatible wallets
- **USD Payment Gateway**: Automatic USD to cryptocurrency conversion with real-time rates
- **Production-Ready**: Enhanced security, health monitoring, and statistics
- **Fully Self-Contained**: No external dependencies or API calls required
- **Local Cryptography**: All cryptographic operations performed locally
- **Wallet Management**: Create and manage multiple wallets
- **Transaction Builder**: Create and sign transactions
- **Open Source**: Fully transparent and verifiable code

## Supported Cryptocurrencies

| Cryptocurrency | Symbol | Decimal Places |
|---------------|--------|---------------|
| Bitcoin | BTC | 8 |
| Ethereum | ETH | 18 |
| Litecoin | LTC | 8 |
| Bitcoin Cash | BCH | 8 |
| Dogecoin | DOGE | 8 |
| Solana | SOL | 9 |
| Polygon | MATIC | 18 |
| Binance Coin | BNB | 18 |
| Tron | TRX | 6 |

## Installation

Add the SDK library to your Android project using JitPack.io:

### 1. Add the JitPack repository to your root `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add the dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.FreetimeMaker:FreetimeSDK:v1.0.2'
}
```

### 3. Sync your project with Gradle files

### Alternative: Local Development

For local development, you can also use:

```gradle
dependencies {
    implementation project(':SDK')
}
```

## Quick Start

### 1. Initialize SDK

```kotlin
import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.payment.CoinType

val sdk = FreetimePaymentSDK()
```

### 2. Create Wallet

```kotlin
// Create Bitcoin wallet
val bitcoinWallet = sdk.createWallet(CoinType.BITCOIN, "My Bitcoin Wallet")

// Create Ethereum wallet
val ethereumWallet = sdk.createWallet(CoinType.ETHEREUM, "My Ethereum Wallet")

// Create Litecoin wallet
val litecoinWallet = sdk.createWallet(CoinType.LITECOIN, "My Litecoin Wallet")
```

### 3. Check Balance

```kotlin
val balance = sdk.getBalance(bitcoinWallet.address)
println("Bitcoin balance: $balance BTC")
```

### 4. Send Cryptocurrency

```kotlin
import java.math.BigDecimal

val amount = BigDecimal("0.001")
val recipientAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"

val txHash = sdk.send(
    fromAddress = bitcoinWallet.address,
    toAddress = recipientAddress,
    amount = amount,
    coinType = CoinType.BITCOIN
)

println("Transaction sent: $txHash")
```

### 5. Fee Estimation

```kotlin
val fee = sdk.getFeeEstimate(
    fromAddress = bitcoinWallet.address,
    toAddress = recipientAddress,
    amount = amount,
    coinType = CoinType.BITCOIN
)

println("Estimated fee: $fee BTC")
```

### 6. Send Cryptocurrency with Developer Fees

```kotlin
// Send cryptocurrency with automatic fee calculation
val result = sdk.send(
    fromAddress = bitcoinWallet.address,
    toAddress = recipientAddress,
    amount = BigDecimal("0.1"),
    coinType = CoinType.BITCOIN
)

// Display fee breakdown
println(result.feeBreakdown.getFormattedBreakdown())
/*
Transaction Fee Breakdown (BTC):
Original Amount: 0.10000000 BTC
Network Fee: 0.00000100 BTC
Developer Fee (0.5%): 0.00050000 BTC
Total Fee: 0.00050100 BTC
Recipient Receives: 0.09949900 BTC
Developer Wallet: bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh
*/

// Broadcast the transaction
val txHash = result.broadcast()
println("Transaction sent: $txHash")
```

## Developer Fees

The SDK includes a tiered developer fee structure that automatically adjusts based on transaction amount:

### Fee Structure

| Transaction Amount | Developer Fee | Category |
|-------------------|---------------|----------|
| < $10 | 0.5% | Small Transaction |
| $10 - $100 | 0.3% | Medium Transaction |
| $100 - $1,000 | 0.2% | Large Transaction |
| $1,000 - $10,000 | 0.1% | Very Large Transaction |
| > $10,000 | 0.05% | Whale Transaction |

### Fee Management

```kotlin
val feeManager = sdk.getFeeManager()

// Get fee percentage for a specific amount
val feePercentage = feeManager.getDeveloperFeePercentage(BigDecimal("50"))
println("Fee percentage: $feePercentage%") // 0.3%

// Get fee tier information
val tier = feeManager.getFeeTier(BigDecimal("50"))
println("Transaction tier: $tier") // Medium Transaction ($10 - $100)

// Get developer wallet for specific cryptocurrency
val btcWallet = feeManager.getDeveloperWalletAddress(CoinType.BITCOIN)
println("BTC Developer Wallet: $btcWallet")

// Update developer wallet for a specific cryptocurrency
val updatedFeeManager = feeManager.updateDeveloperWallet(
    CoinType.BITCOIN, 
    "new_btc_wallet_address"
)
```

## API Reference

### FreetimePaymentSDK

The main class for interacting with the payment SDK.

#### Methods

- `createWallet(coinType: CoinType, name: String?): Wallet` - Creates a new wallet
- `getBalance(address: String): BigDecimal` - Gets the balance of an address
- `send(fromAddress: String, toAddress: String, amount: BigDecimal, coinType: CoinType): TransactionWithFees` - Sends cryptocurrency with fee calculation
- `getFeeEstimate(...): BigDecimal` - Estimates transaction fee
- `getFeeManager(): FeeManager` - Gets the fee manager for developer fee configuration
- `getAllWallets(): List<Wallet>` - Returns all wallets
- `getWalletsByCoinType(coinType: CoinType): List<Wallet>` - Returns wallets by type
- `validateAddress(address: String, coinType: CoinType): Boolean` - Validates an address

### Wallet

Represents a cryptocurrency wallet.

#### Properties

- `address: String` - The wallet address
- `coinType: CoinType` - The cryptocurrency type
- `publicKey: PublicKey` - The public key
- `privateKey: PrivateKey` - The private key (keep secure!)

#### Methods

- `getBalance(paymentProvider: PaymentInterface): BigDecimal` - Check balance
- `send(toAddress: String, amount: BigDecimal, paymentProvider: PaymentInterface): Transaction` - Send

### TransactionWithFees

Represents a cryptocurrency transaction with fee breakdown.

#### Properties

- `transaction: Transaction` - The transaction details
- `feeBreakdown: FeeBreakdown` - Detailed fee information

#### Methods

- `broadcast(): String` - Broadcasts the transaction to the network
- `getFormattedSummary(): String` - Gets formatted transaction summary

### FeeManager

Manages developer fees and wallet configuration.

#### Methods

- `getDeveloperFeePercentage(amount: BigDecimal): BigDecimal` - Gets fee percentage for amount
- `getFeeTier(amount: BigDecimal): String` - Gets transaction tier information
- `getDeveloperWalletAddress(coinType: CoinType): String` - Gets developer wallet for cryptocurrency
- `getAllDeveloperWallets(): Map<CoinType, String>` - Gets all developer wallets
- `updateDeveloperWallet(coinType: CoinType, address: String): FeeManager` - Updates developer wallet
- `updateAllDeveloperWallets(wallets: Map<CoinType, String>): FeeManager` - Updates all developer wallets

### FeeBreakdown

Contains detailed fee information for a transaction.

#### Properties

- `originalAmount: BigDecimal` - Original transaction amount
- `networkFee: BigDecimal` - Network transaction fee
- `developerFee: BigDecimal` - Developer fee amount
- `totalFee: BigDecimal` - Total fee (network + developer)
- `recipientAmount: BigDecimal` - Amount recipient receives
- `developerWalletAddress: String` - Developer wallet address
- `coinType: CoinType` - Cryptocurrency type
- `developerFeePercentage: BigDecimal` - Developer fee percentage

#### Methods

- `getFormattedBreakdown(): String` - Gets formatted fee breakdown
- `getFeeSummary(): String` - Gets fee summary string
- `getFeeTier(): String` - Gets transaction tier

## Security

- **Private Keys**: Private keys are never stored or transmitted outside the app
- **Local Processing**: All cryptographic operations happen locally on the device
- **Open Source**: The code is fully verifiable
- **No External Dependencies**: The SDK doesn't require external services or APIs

## Architecture

The SDK follows a modular architecture:

```
FreetimePaymentSDK
├── Wallet Management
├── Payment Providers
│   ├── BitcoinPaymentProvider
│   ├── EthereumPaymentProvider
│   └── LitecoinPaymentProvider
├── Crypto Utils
│   ├── BitcoinCryptoUtils
│   ├── EthereumCryptoUtils
│   └── LitecoinCryptoUtils
└── Core Interfaces
    ├── PaymentInterface
    ├── Transaction
    └── CoinType
```

## Payment Gateway - Automatic Payment Forwarding

The SDK includes a complete payment gateway for automatic processing of cryptocurrency payments.

### Features

- **Automatic Forwarding**: Payments are automatically forwarded to your merchant wallet address
- **Temporary Payment Addresses**: Create a unique temporary address for each payment
- **Status Monitoring**: Monitor payment status in real-time
- **Multi-Payment Processing**: Process multiple payments simultaneously
- **Event Listeners**: Get notifications for status changes

### Payment Gateway Setup

```kotlin
import com.freetime.sdk.payment.gateway.*

// Your merchant wallet address (fixed in code)
val merchantWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"

// Initialize Payment Gateway
val gateway = PaymentGateway(
    sdk = sdk,
    merchantWalletAddress = merchantWallet,
    merchantCoinType = CoinType.BITCOIN
)

// Start automatic processing
val processor = PaymentProcessor(gateway)
processor.addPaymentListener(LoggingPaymentListener())
processor.startProcessing()
```

### Accept Payment

```kotlin
// Option A: SDK creates a temporary wallet (default behavior)
val paymentRequest = gateway.createPaymentAddress(
    amount = BigDecimal("0.001"), // 0.001 BTC
    customerReference = "Customer-12345",
    description = "Product #ABC-123"
)

println("Pay to: ${paymentRequest.customerAddress}")
println("Amount: ${paymentRequest.amount} BTC")

// Option B: The host app can provide an existing Wallet object instead of
// letting the SDK generate a new one. This is useful when the app manages
// user wallets itself and wants incoming payments to go to a known address.
val customerWallet = sdk.createWallet(CoinType.BITCOIN, "Customer Wallet") // or app-provided
val paymentRequestWithProvidedWallet = gateway.createPaymentAddress(
    amount = BigDecimal("0.001"),
    customerReference = "Customer-12345",
    description = "Product #ABC-123",
    providedWallet = customerWallet, // optional: use app's wallet
    forwardToAddress = "bc1qexternalwalletaddress..." // optional: forward after confirmation
)

println("Pay to: ${paymentRequestWithProvidedWallet.customerAddress}")
println("Amount: ${paymentRequestWithProvidedWallet.amount} BTC")
```

### Monitor Payment Status

```kotlin
// Check payment status
val status = gateway.checkPaymentStatus(paymentRequest.id)
val details = gateway.getPaymentDetails(paymentRequest.id)

println("Status: $status")
println("Current balance: ${details?.currentBalance}")
println("Remaining: ${details?.remainingAmount}")
```

### Automatic Forwarding

Once the full payment is received at the temporary address, it is automatically forwarded to your merchant wallet address:

```kotlin
// The payment gateway automatically monitors and forwards
// Once status == CONFIRMED, the payment has been forwarded

if (status == PaymentStatus.CONFIRMED) {
    val details = gateway.getPaymentDetails(paymentRequest.id)
    println("Payment confirmed!")
    println("Forwarding hash: ${details?.forwardedTxHash}")
}
```

Hinweis: Falls `forwardToAddress` bei `createPaymentAddress` gesetzt wurde, wird an diese Adresse weitergeleitet statt an `merchantWalletAddress`.

### Merchant Configuration

```kotlin
// Pre-configured merchant settings
val config = MerchantPresets.bitcoinConfig(merchantWallet)

val gateway = PaymentGateway(
    sdk = sdk,
    merchantWalletAddress = config.walletAddress,
    merchantCoinType = config.coinType
)
```

## USD Payment Gateway - Automatic Conversion

The SDK now supports USD payments with automatic conversion to cryptocurrencies!

### Features

- **USD Input**: Specify amounts in US Dollars
- **Automatic Conversion**: Real-time exchange rates from APIs
- **Multi-API Support**: CoinGecko, CoinCap with fallback
- **Caching**: 1-minute cache for performance
- **Offline Fallback**: Mock rates when API is unavailable

### USD Payment Gateway Setup

```kotlin
import com.freetime.sdk.payment.conversion.*

// Initialize USD Payment Gateway
val usdGateway = sdk.createUsdPaymentGateway(
    merchantWalletAddress = "your_wallet_address",
    merchantCoinType = CoinType.BITCOIN
)

// Accept USD payment (automatic conversion)
val usdPayment = usdGateway.createUsdPaymentRequest(
    usdAmount = BigDecimal("100.00"), // $100 USD
    customerReference = "Customer-12345",
    description = "Product #ABC-123"
)

println("Pay ${usdPayment.cryptoAmount} ${usdPayment.coinType.symbol}")
println("Equals $${usdPayment.usdAmount} USD")
println("Exchange rate: $${usdPayment.exchangeRate}")
```

// Or: provide a Wallet and optional forwarding address when creating the USD payment
val customerEthWallet = sdk.createWallet(CoinType.ETHEREUM, "Customer ETH Wallet")
val usdPaymentWithProvidedWallet = usdGateway.createUsdPaymentRequest(
    usdAmount = BigDecimal("100.00"),
    customerReference = "Customer-12345",
    description = "Product #ABC-123",
    providedWallet = customerEthWallet, // optional
    forwardToAddress = "0xExternalForwardAddress..." // optional forwarding address
)

println("Pay ${usdPaymentWithProvidedWallet.cryptoAmount} ${usdPaymentWithProvidedWallet.coinType.symbol}")
println("Equals $${usdPaymentWithProvidedWallet.usdAmount} USD")
println("Exchange rate: $${usdPaymentWithProvidedWallet.exchangeRate}")

### Currency Conversion

```kotlin
// Currency converter for manual conversion
val converter = sdk.getCurrencyConverter()

// USD to crypto
val result = converter.convertUsdToCrypto(
    usdAmount = BigDecimal("50.00"),
    coinType = CoinType.BITCOIN
)

if (result.success) {
    println("$50.00 USD = ${result.cryptoAmount} BTC")
}

// Crypto to USD
val reverseResult = converter.convertCryptoToUsd(
    cryptoAmount = BigDecimal("0.001"),
    coinType = CoinType.BITCOIN
)

if (reverseResult.success) {
    println("0.001 BTC = $${reverseResult.usdAmount} USD")
}
```

### Exchange Rate Monitoring

```kotlin
// Get current exchange rates
val rates = converter.getAllExchangeRates()
rates.forEach { (coinType, rate) ->
    println("1 ${coinType.coinName} = $${rate}")
}
```

### API Support

**Supported APIs:**
- **CoinGecko API** (primary, free)
- **CoinCap API** (alternative)
- **Offline Fallback** with mock rates

**API Features:**
- Real-time exchange rates
- Automatic error handling
- 1-minute caching
- No API keys required (CoinGecko)

## Production Environment

For production use, the SDK provides enhanced security and reliability features:

### Production USD Payment Gateway

```kotlin
// Production gateway with configuration
val config = PaymentGatewayConfig.highVolume()
val productionGateway = sdk.createProductionUsdPaymentGateway(
    merchantWalletAddress = "your_wallet_address",
    merchantCoinType = CoinType.BITCOIN
)

// Health monitoring
val health = productionGateway.getGatewayHealthStatus()
if (!health.isHealthy) {
    // Activate fallback strategy
}

// Production payment creation
val payment = productionGateway.createUsdPaymentRequest(
    usdAmount = BigDecimal("250.00"),
    customerReference = "PROD-CUST-12345",
    metadata = mapOf(
        "product_id" to "premium-product",
        "customer_tier" to "gold"
    )
)
```

### Production Features

- **Multi-API Fallback** for failures
- **Rate Limiting** against API blocking
- **Input Validation** against invalid data
- **Health Monitoring** for system status
- **Automatic Cleanup** for expired payments
- **Statistics Tracking** for business intelligence
- **Thread Safety** for high load

### Production APIs

- **CoinGecko API** (primary)
- **CoinCap API** (fallback 1)
- **CoinBase API** (fallback 2)
- **Mock Rates** (final fallback)

## Example App

A complete example app is included in the `examples/` directory that demonstrates all SDK features:

- `ExampleApp.kt` - Basic SDK functionality
- `PaymentGatewayExample.kt` - Payment gateway with automatic forwarding
- `UsdPaymentExample.kt` - USD payments with automatic conversion
- `ProductionUsdPaymentExample.kt` - Production-ready payment processing
- `MultiCryptoExample.kt` - Multi-cryptocurrency wallet management
- `DeveloperFeeExample.kt` - Developer fee calculation and management

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please create a Pull Request or open an Issue.

## Support

For questions and support, please open an Issue in the GitHub repository.

---

**Important Note**: This SDK is designed for educational purposes and development. In production environments, you should implement additional security measures and test transactions with real blockchain nodes.
