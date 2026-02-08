# Freetime Payment SDK

[![](https://jitpack.io/v/FreetimeMaker/FreetimeSDK.svg)](https://jitpack.io/#FreetimeMaker/FreetimeSDK)

A completely self-contained, open-source multi-cryptocurrency payment SDK for Android.

## Features

- **Multi-Coin Support**: Bitcoin (BTC), Ethereum (ETH), Litecoin (LTC)
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

## Installation

Add the SDK library to your Android project:

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

## API Reference

### FreetimePaymentSDK

The main class for interacting with the payment SDK.

#### Methods

- `createWallet(coinType: CoinType, name: String?): Wallet` - Creates a new wallet
- `getBalance(address: String): BigDecimal` - Gets the balance of an address
- `send(fromAddress: String, toAddress: String, amount: BigDecimal, coinType: CoinType): String` - Sends cryptocurrency
- `getFeeEstimate(...): BigDecimal` - Estimates transaction fee
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

### Transaction

Represents a cryptocurrency transaction.

#### Properties

- `id: String` - Transaction ID
- `fromAddress: String` - Sender address
- `toAddress: String` - Recipient address
- `amount: BigDecimal` - Amount
- `fee: BigDecimal` - Fee
- `coinType: CoinType` - Cryptocurrency type
- `status: TransactionStatus` - Transaction status

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
// Create a temporary payment address for the customer
val paymentRequest = gateway.createPaymentAddress(
    amount = BigDecimal("0.001"), // 0.001 BTC
    customerReference = "Customer-12345",
    description = "Product #ABC-123"
)

println("Pay to: ${paymentRequest.customerAddress}")
println("Amount: ${paymentRequest.amount} BTC")
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

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please create a Pull Request or open an Issue.

## Support

For questions and support, please open an Issue in the GitHub repository.

---

**Important Note**: This SDK is designed for educational purposes and development. In production environments, you should implement additional security measures and test transactions with real blockchain nodes.
