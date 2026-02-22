# Freetime Payment SDK

[![](https://jitpack.io/v/FreetimeMaker/FreetimeSDK.svg)](https://jitpack.io/#FreetimeMaker/FreetimeSDK)
![Daily download statistics](https://jitpack.io/v/FreetimeMaker/FreetimeSDK/day.svg)
![Weekly download statistics](https://jitpack.io/v/FreetimeMaker/FreetimeSDK/week.svg)
![Monthly download statistics](https://jitpack.io/v/FreetimeMaker/FreetimeSDK/month.svg)
![Total download statistics](https://jitpack.io/v/FreetimeMaker/FreetimeSDK/total.svg)

A completely self-contained, open-source multi-cryptocurrency payment SDK for Android with integrated gaming functionality.

## Features

### Payment Features
- **Multi-Coin Support**: 9 cryptocurrencies including Bitcoin (BTC), Ethereum (ETH), Litecoin (LTC), Bitcoin Cash (BCH), Dogecoin (DOGE), Solana (SOL), Polygon (MATIC), Binance Coin (BNB), and Tron (TRX)
- **User Wallet Configuration**: Users can configure their own wallet addresses in app
- **Cryptocurrency Selection**: Users can choose which cryptocurrencies to accept
- **Address-Based Payments**: Support for user-provided wallet addresses only
- **Donation System**: Complete donation functionality with predefined amounts and custom options
- **External Wallet Integration**: Integration with popular wallet apps (Trust Wallet, MetaMask, Coinbase, etc.)
- **Deep Link Support**: Automatic deep link generation for external wallet apps
- **Developer Fee System**: Tiered fee structure (0.05% - 0.5%)
- **USD Payment Gateway**: Automatic USD to cryptocurrency conversion with real-time rates

### Gaming Features
- **Game Integration**: Complete SDK for integrating cryptocurrency payments into games
- **Player Progress Tracking**: Track player achievements, levels, and statistics
- **Achievement System**: Built-in achievement system with XP and rewards
- **Revenue Generation**: Built-in monetization with automatic fee collection
- **Custom Game Support**: Easy integration framework for custom games
- **Leaderboards**: Global and game-specific leaderboards
- **Player Profiles**: Comprehensive player statistics and progress tracking

### Technical Features
- **Production-Ready**: Enhanced security, health monitoring, and statistics
- **Fully Self-Contained**: No external dependencies or API calls required
- **Local Cryptography**: All cryptographic operations performed locally
- **Address-Based Payments**: Configure and use external wallet addresses
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

### 2. Configure User Wallet Addresses

```kotlin
// Set user's Bitcoin wallet address
sdk.setUserWalletAddress(
    coinType = CoinType.BITCOIN,
    address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    name = "My Bitcoin Wallet"
)

// Set user's Ethereum wallet address
sdk.setUserWalletAddress(
    coinType = CoinType.ETHEREUM,
    address = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
    name = "My Ethereum Wallet"
)
```

### 3. Select Which Cryptocurrencies to Accept

```kotlin
// User selects which cryptocurrencies to accept
val acceptedCryptocurrencies = setOf(
    CoinType.BITCOIN,
    CoinType.ETHEREUM,
    CoinType.LITECOIN
)

sdk.setAcceptedCryptocurrencies(acceptedCryptocurrencies)
```

### 4. Send Cryptocurrency

```kotlin
import java.math.BigDecimal

val amount = BigDecimal("0.001")
val recipientAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
val fromAddress = "your_user_wallet_address" // User's configured wallet address

val txHash = sdk.send(
    fromAddress = fromAddress,
    toAddress = recipientAddress,
    amount = amount,
    coinType = CoinType.BITCOIN
)

println("Transaction sent: $txHash")
```

### 5. Fee Estimation

```kotlin
val fromAddress = "your_user_wallet_address"
val fee = sdk.getFeeEstimate(
    fromAddress = fromAddress,
    toAddress = recipientAddress,
    amount = amount,
    coinType = CoinType.BITCOIN
)

println("Estimated fee: $fee BTC")
```

### 6. Send Cryptocurrency with Developer Fees

```kotlin
// Send cryptocurrency with automatic fee calculation
val fromAddress = "your_user_wallet_address"
val result = sdk.send(
    fromAddress = fromAddress,
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

## Donations

The SDK includes a complete donation system that allows users to make cryptocurrency donations with predefined or custom amounts.

### Basic Donation

```kotlin
// Create a simple donation with fees
val donation = sdk.donate(
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    amount = BigDecimal("0.1"),
    coinType = CoinType.BITCOIN,
    donorName = "John Donor",
    donationMessage = "Supporting your great work!"
)

println("Donation Amount: ${donation.donation.amount} BTC")
println("Network Fee: ${donation.networkFee} BTC")
println("Developer Fee: ${donation.developerFee} BTC")
println("Total Cost: ${donation.totalAmount} BTC")

// Broadcast the donation
val txId = sdk.broadcastDonation(donation.donation)
println("Donation sent: $txId")
```

### Donation without Fees

```kotlin
// Create a donation where the full amount goes to recipient
val donation = sdk.donateWithoutFees(
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    amount = BigDecimal("0.1"),
    coinType = CoinType.BITCOIN,
    donorName = "Jane Donor"
)

val txId = sdk.broadcastDonation(donation)
println("Full amount reaches recipient!")
```

### Donation Amount Options

```kotlin
// Get predefined donation amounts for a cryptocurrency
val amountSelector = sdk.getDonationAmountSelector()
val donationOptions = amountSelector.getDonationOptions(
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    coinType = CoinType.BITCOIN,
    sdk = sdk
)

// Display donation options to user
println("=== Donation Options ===")
donationOptions.forEachIndexed { index, option ->
    println("${index + 1}. ${option.label}")
    println("   Fees: ${option.networkFee + option.developerFee} BTC")
    println("   Total: ${option.totalCost} BTC")
}

// User selects an option
val selectedOption = donationOptions[0]
val donation = sdk.donate(
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    amount = selectedOption.amount,
    coinType = CoinType.BITCOIN
)
```

### Custom Donation Amounts

```kotlin
// User enters custom amount
val customAmount = BigDecimal("0.05")

// Validate the amount
if (sdk.validateDonationAmount(customAmount, CoinType.BITCOIN)) {
    val donation = sdk.donate(
        toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
        amount = customAmount,
        coinType = CoinType.BITCOIN,
        donorName = "Custom Donor"
    )
    
    println("Donation created: ${donation.donation.amount} BTC")
} else {
    println("Amount too small or invalid")
}
```

### Donation Options with Custom Labels

```kotlin
// Define custom labels for predefined amounts
val customLabels = mapOf(
    BigDecimal("0.1") to "Small Donation",
    BigDecimal("0.5") to "Medium Donation",
    BigDecimal("1.0") to "Large Donation",
    BigDecimal("5.0") to "Generous Support",
    BigDecimal("10.0") to "VIP Supporter"
)

val options = amountSelector.getDonationOptionsWithLabels(
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    coinType = CoinType.ETHEREUM,
    sdk = sdk,
    labels = customLabels
)
```

### Fee Estimation for Donations

```kotlin
// Get fee estimate before creating donation
val feeEstimate = sdk.getDonationFeeEstimate(
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    amount = BigDecimal("0.1"),
    coinType = CoinType.BITCOIN
)

println("Estimated fee: $feeEstimate BTC")

// Get detailed fee breakdown
val feeBreakdown = sdk.getDonationFeeBreakdown(
    amount = BigDecimal("0.1"),
    networkFee = feeEstimate,
    coinType = CoinType.BITCOIN
)

println("Network Fee: ${feeBreakdown.networkFee} BTC")
println("Developer Fee: ${feeBreakdown.developerFee} BTC")
println("Recipient Receives: ${feeBreakdown.recipientAmount} BTC")
```

### Supported Donation Amounts

The SDK provides predefined donation amounts optimized for each cryptocurrency:

| Cryptocurrency | Predefined Amounts |
|---|---|
| Bitcoin (BTC) | 0.001, 0.005, 0.01, 0.05, 0.1 |
| Ethereum (ETH) | 0.1, 0.5, 1, 5, 10 |
| Litecoin (LTC) | 0.5, 1, 5, 10, 50 |
| Bitcoin Cash (BCH) | 0.01, 0.05, 0.1, 0.5, 1 |
| Dogecoin (DOGE) | 10, 50, 100, 500, 1000 |
| Solana (SOL) | 0.1, 0.5, 1, 5, 10 |

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

- `validateAddress(address: String, coinType: CoinType): Boolean` - Validates an address
- `send(fromAddress: String, toAddress: String, amount: BigDecimal, coinType: CoinType): TransactionWithFees` - Sends cryptocurrency with fee calculation
- `getFeeEstimate(...): BigDecimal` - Estimates transaction fee
- `getFeeManager(): FeeManager` - Gets fee manager for developer fee configuration
- `hasUserWallet(coinType: CoinType): Boolean` - Checks if user has configured wallet for cryptocurrency
- `setAcceptedCryptocurrencies(cryptocurrencies: Set<CoinType>): Unit` - Sets which cryptocurrencies user wants to accept
- `getAcceptedCryptocurrencies(): Set<CoinType>` - Gets all accepted cryptocurrencies
- `isCryptocurrencyAccepted(coinType: CoinType): Boolean` - Checks if user accepts specific cryptocurrency
- `addAcceptedCryptocurrency(coinType: CoinType): Unit` - Adds cryptocurrency to accepted list
- `removeAcceptedCryptocurrency(coinType: CoinType): Unit` - Removes cryptocurrency from accepted list
- `getSupportedPaymentOptions(): List<CoinType>` - Gets cryptocurrencies user can receive payments in
- `getAvailableCryptocurrencies(): List<CoinType>` - Gets all available cryptocurrencies for selection
- `hasAnyAcceptedWallet(): Boolean` - Checks if user has any accepted cryptocurrency configured
- `validateHasAcceptedWallets(): Boolean` - Validates that user has at least one accepted cryptocurrency

#### External Wallet Integration Methods

- `createUsdPaymentGatewayWithWalletSupport(merchantWalletAddress: String, merchantCoinType: CoinType): UsdPaymentGateway` - Creates USD payment gateway with external wallet support
- `getExternalWalletManager(): ExternalWalletManager` - Gets external wallet manager for wallet app integration
- `getAvailableWalletApps(coinType: CoinType): List<ExternalWalletApp>` - Gets available wallet apps for specific cryptocurrency
- `generatePaymentDeepLink(walletApp: ExternalWalletApp, address: String, amount: BigDecimal, coinType: CoinType): String` - Generates deep link for external wallet

#### Donation Methods

- `suspend fun donate(toAddress: String, amount: BigDecimal, coinType: CoinType, donorName: String?, donationMessage: String?): DonationWithFees` - Creates a donation with fee calculation
- `suspend fun donateWithoutFees(toAddress: String, amount: BigDecimal, coinType: CoinType, donorName: String?, donationMessage: String?): Donation` - Creates a donation without fees
- `suspend fun broadcastDonation(donation: Donation): String` - Broadcasts a donation to the blockchain
- `suspend fun getDonationFeeEstimate(toAddress: String, amount: BigDecimal, coinType: CoinType): BigDecimal` - Gets donation fee estimate
- `fun getDonationFeeBreakdown(amount: BigDecimal, networkFee: BigDecimal, coinType: CoinType): FeeBreakdown` - Gets detailed fee breakdown for donation
- `fun validateDonationAmount(amount: BigDecimal, coinType: CoinType): Boolean` - Validates if donation amount is valid for cryptocurrency
- `fun getDonationProvider(): DonationInterface` - Gets the donation provider
- `fun getDonationAmountSelector(): DonationAmountSelector` - Gets the donation amount selector for predefined amounts

### UsdPaymentGateway (Enhanced)

#### Additional Methods for External Wallet Integration

- `createUsdPaymentWithWalletSelection(...): UsdPaymentRequestWithWalletSelection` - Creates USD payment request with wallet selection
- `getAvailableWalletApps(): List<ExternalWalletApp>` - Gets available wallet apps for merchant's cryptocurrency
- `generatePaymentDeepLink(walletApp: ExternalWalletApp, usdPaymentRequest: UsdPaymentRequest): String` - Generates deep link for specific wallet
- `isWalletSupported(walletApp: ExternalWalletApp): Boolean` - Checks if wallet app supports merchant's cryptocurrency
- `getAllSupportedWalletApps(): List<ExternalWalletApp>` - Gets all supported external wallet apps

### ExternalWalletManager

#### Methods

- `createPaymentWithWalletSelection(usdPaymentRequest: UsdPaymentRequest, coinType: CoinType): UsdPaymentRequestWithWalletSelection` - Creates payment with wallet selection
- `getAllSupportedWallets(): List<ExternalWalletApp>` - Gets all supported wallet apps
- `getWalletsForCryptocurrency(coinType: CoinType): List<ExternalWalletApp>` - Gets wallet apps for specific cryptocurrency
- `generatePaymentDeepLink(walletApp: ExternalWalletApp, address: String, amount: BigDecimal, coinType: CoinType): String` - Generates payment deep link
- `isCoinSupported(walletApp: ExternalWalletApp, coinType: CoinType): Boolean` - Checks if wallet supports cryptocurrency
- `getWalletByPackageName(packageName: String): ExternalWalletApp?` - Gets wallet app by package name

### ExternalWalletApp

#### Properties

- `name: String` - Display name of the wallet app
- `packageName: String` - Android package name
- `supportedCoins: List<CoinType>` - List of supported cryptocurrencies
- `deepLinkScheme: String` - Deep link scheme for the wallet
- `iconUrl: String?` - Optional URL for wallet icon

#### Methods

- `generatePaymentDeepLink(address: String, amount: BigDecimal, coinType: CoinType): String` - Generates deep link for payment
- `isInstalled(): Boolean` - Checks if wallet app is installed

#### Predefined Wallet Apps

- `ExternalWalletApp.TRUST_WALLET` - Trust Wallet
- `ExternalWalletApp.META_MASK` - MetaMask
- `ExternalWalletApp.COINBASE_WALLET` - Coinbase Wallet
- `ExternalWalletApp.BINANCE_WALLET` - Binance Wallet
- `ExternalWalletApp.EXODUS` - Exodus
- `ExternalWalletApp.ATOMIC_WALLET` - Atomic Wallet
- `ExternalWalletApp.LEDGER_LIVE` - Ledger Live
- `ExternalWalletApp.TREZOR_SUITE` - Trezor Suite
- `ExternalWalletApp.MYCELIUM` - Mycelium
- `ExternalWalletApp.ELECTRUM` - Electrum
- `ExternalWalletApp.BRAVE_WALLET` - Brave Wallet
- `ExternalWalletApp.RAINBOW_WALLET` - Rainbow Wallet
- `ExternalWalletApp.WALLET_CONNECT` - WalletConnect
- `ExternalWalletApp.PHANTOM_WALLET` - Phantom Wallet
- `ExternalWalletApp.SOLFLARE_WALLET` - Solflare Wallet
- `ExternalWalletApp.YOROI_WALLET` - Yoroi Wallet
- `ExternalWalletApp.ADALITE_WALLET` - AdaLite Wallet
- `ExternalWalletApp.TRON_WALLET` - TronWallet
- `ExternalWalletApp.KLEVER_WALLET` - Klever Wallet
- `ExternalWalletApp.BITKEEP_WALLET` - BitKeep Wallet
- `ExternalWalletApp.SAFE_WALLET` - Safe Wallet
- `ExternalWalletApp.ARGENT_WALLET` - Argent Wallet
- `ExternalWalletApp.ZERION_WALLET` - Zerion Wallet
- `ExternalWalletApp.IM_TOKEN_WALLET` - imToken Wallet
- `ExternalWalletApp.MATH_WALLET` - MathWallet
- `ExternalWalletApp.TOKEN_POCKET` - TokenPocket

#### Companion Methods

- `getAllWalletApps(): List<ExternalWalletApp>` - Gets all predefined wallet apps
- `getWalletsForCoin(coinType: CoinType): List<ExternalWalletApp>` - Gets wallets supporting specific coin

### UsdPaymentRequestWithWalletSelection

#### Properties

- `usdPaymentRequest: UsdPaymentRequest` - Base USD payment request
- `availableWalletApps: List<ExternalWalletApp>` - Available wallet apps
- `selectedWalletApp: ExternalWalletApp?` - User-selected wallet app
- `walletSelectionRequired: Boolean` - Whether wallet selection is required

#### Methods

- `getPaymentDeepLink(): String?` - Gets payment deep link for selected wallet
- `selectWallet(walletApp: ExternalWalletApp): UsdPaymentRequestWithWalletSelection` - Selects wallet app
- `getFormattedInfo(): String` - Gets formatted payment info with wallet selection

### UserWalletConfig

Represents user's wallet configuration for external addresses.

#### Properties

- `coinType: CoinType` - The cryptocurrency type
- `address: String` - The wallet address
- `name: String?` - Optional display name
- `isActive: Boolean` - Whether the wallet is active
- `isAccepted: Boolean` - Whether user accepts this cryptocurrency

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

## User Wallet Configuration

The SDK allows users to configure their own wallet addresses and select which cryptocurrencies they want to accept.

### Features

- **User Wallet Addresses**: Users can input their own wallet addresses in the app
- **Cryptocurrency Selection**: Users can choose which cryptocurrencies to accept payments in
- **Flexible Configuration**: Support for both individual and bulk wallet configuration
- **Address Validation**: Automatic validation of wallet address formats
- **Dynamic Management**: Add/remove cryptocurrencies at runtime

### Basic Setup

```kotlin
// Configure user wallet addresses
sdk.setUserWalletAddress(
    coinType = CoinType.BITCOIN,
    address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    name = "My Bitcoin Wallet"
)

sdk.setUserWalletAddress(
    coinType = CoinType.ETHEREUM,
    address = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
    name = "My Ethereum Wallet"
)
```

### Cryptocurrency Selection

```kotlin
// User selects which cryptocurrencies to accept
val acceptedCryptocurrencies = setOf(
    CoinType.BITCOIN,
    CoinType.ETHEREUM,
    CoinType.LITECOIN
)

sdk.setAcceptedCryptocurrencies(acceptedCryptocurrencies)

// Check which cryptocurrencies are accepted
val isBitcoinAccepted = sdk.isCryptocurrencyAccepted(CoinType.BITCOIN)
val acceptedCryptos = sdk.getAcceptedCryptocurrencies()

// Get supported payment options (configured + accepted)
val paymentOptions = sdk.getSupportedPaymentOptions()
```

### Advanced Configuration

```kotlin
// Add/remove cryptocurrencies dynamically
sdk.addAcceptedCryptocurrency(CoinType.DOGECOIN)
sdk.removeAcceptedCryptocurrency(CoinType.LITECOIN)

// Check configuration status
val hasBitcoinWallet = sdk.hasUserWallet(CoinType.BITCOIN)
val hasAnyAcceptedWallet = sdk.hasAnyAcceptedWallet()

// Validate configuration before payments
try {
    sdk.validateHasAcceptedWallets()
    // Proceed with payment processing
} catch (e: Exception) {
    // Show configuration UI to user
}
```

### Payment Gateway with User Wallets

```kotlin
// Create USD payment gateway with user's configured wallet
val userBitcoinAddress = sdk.getUserWalletAddress(CoinType.BITCOIN)
if (userBitcoinAddress != null && sdk.isCryptocurrencyAccepted(CoinType.BITCOIN)) {
    val gateway = sdk.createUsdPaymentGateway(
        merchantWalletAddress = userBitcoinAddress,
        merchantCoinType = CoinType.BITCOIN
    )
    
    val payment = gateway.createUsdPaymentRequest(
        usdAmount = BigDecimal("100.00"),
        customerReference = "Customer-12345",
        description = "Product Purchase"
    )
    
    println("Pay ${payment.cryptoAmount} BTC to $userBitcoinAddress")
}

// Create gateways for all accepted cryptocurrencies
val allGateways = sdk.createUsdPaymentGatewaysForAccepted()
allGateways.forEach { (coinType, gateway) ->
    println("Gateway ready for ${coinType.coinName}")
}
```


## External Wallet Integration

The SDK supports integration with popular external cryptocurrency wallet apps, allowing users to pay using their preferred wallet application.

### Features

- **Multi-Wallet Support**: Integration with Trust Wallet, MetaMask, Coinbase, Binance, Exodus, and Atomic Wallet
- **Automatic Detection**: Automatically shows only wallets that support the selected cryptocurrency
- **Deep Link Integration**: Generates deep links to open wallet apps directly with payment details
- **Installation Handling**: Redirects users to app store if wallet is not installed
- **Customizable**: Support for custom wallet apps and deep link schemes

### Supported Wallet Apps

| Wallet App | Package Name | Supported Coins | Deep Link Scheme |
|-------------|---------------|------------------|-------------------|
| Trust Wallet | com.wallet.crypto.trustapp | BTC, ETH, LTC, BCH, DOGE, MATIC, BNB | `trust://` |
| MetaMask | io.metamask | ETH, MATIC, BNB | `metamask://` |
| Coinbase Wallet | com.coinbase.android | BTC, ETH, LTC, BCH, DOGE | `cbwallet://` |
| Binance Wallet | com.binance.dev | All supported coins | `binance://` |
| Exodus | exodusmovement.exodus | BTC, ETH, LTC, BCH, DOGE, SOL | `exodus://` |
| Atomic Wallet | co.atomicwallet | All supported coins | `atomic://` |
| Ledger Live | com.ledger.live | BTC, ETH, LTC, BCH, DOGE, MATIC, BNB, SOL, TRX | `ledgerlive://` |
| Trezor Suite | satoshilabs.trezor.trezor-suite | BTC, ETH, LTC, BCH, DOGE, MATIC, BNB | `trezor://` |
| Mycelium | com.mycelium.wallet | BTC, LTC, ETH | `mycelium://` |
| Electrum | org.electrum.electrum | BTC, LTC, BCH | `electrum://` |
| Brave Wallet | com.brave.browser | ETH, MATIC, BNB | `brave://` |
| Rainbow Wallet | me.rainbow | ETH, MATIC, BNB | `rainbow://` |
| WalletConnect | com.walletconnect | ETH, MATIC, BNB, SOL, TRX | `wc://` |
| Phantom Wallet | app.phantom | SOL, ETH, MATIC | `phantom://` |
| Solflare Wallet | com.solflare.mobile | SOL, ETH | `solflare://` |
| Yoroi Wallet | io.emurgo.yoroi | ADA | `yoroi://` |
| AdaLite Wallet | com.adalite.wallet | ADA | `adalite://` |
| TronWallet | com.tronlinkpro.wallet | TRX, BTC, ETH | `tronlink://` |
| Klever Wallet | com.klever.wallet | TRX, BTC, ETH, LTC | `klever://` |
| BitKeep Wallet | com.bitkeep.wallet | All supported coins | `bitkeep://` |
| Safe Wallet | io.gnosis.safe | ETH, MATIC, BNB | `safe://` |
| Argent Wallet | io.argent.wallet | ETH, MATIC, BNB | `argent://` |
| Zerion Wallet | io.zerion.wallet | ETH, MATIC, BNB, SOL | `zerion://` |
| imToken Wallet | im.token.im | BTC, ETH, LTC, BCH, DOGE, BNB, MATIC, SOL, TRX | `imtokenv2://` |
| MathWallet | com.mathwallet.android | All supported coins | `mathwallet://` |
| TokenPocket | com.tokenpocket.pocket | BTC, ETH, LTC, BCH, DOGE, BNB, MATIC, SOL, TRX | `tpoutside://` |

### Basic Usage

```kotlin
import com.freetime.sdk.payment.conversion.*

// Create USD payment gateway with wallet support
val gateway = sdk.createUsdPaymentGatewayWithWalletSupport(
    merchantWalletAddress = "your_wallet_address",
    merchantCoinType = CoinType.BITCOIN
)

// Create payment with wallet selection
val paymentWithWalletSelection = gateway.createUsdPaymentWithWalletSelection(
    usdAmount = BigDecimal("100.00"),
    customerReference = "Customer-12345",
    description = "Product Purchase"
)

// Get available wallet apps for Bitcoin
val availableWallets = gateway.getAvailableWalletApps()
```

### Wallet Selection Dialog

```kotlin
// Show wallet selection dialog
fun showWalletSelectionDialog(paymentRequest: UsdPaymentRequestWithWalletSelection) {
    val dialog = WalletSelectionDialog(
        title = "Wählen Sie Ihre Wallet-App",
        wallets = paymentRequest.availableWalletApps,
        onWalletSelected = { selectedWallet ->
            val updatedPayment = paymentRequest.selectWallet(selectedWallet)
            val deepLink = updatedPayment.getPaymentDeepLink()
            
            // Open wallet app with deep link
            openDeepLink(deepLink)
            
            // Start payment status monitoring
            startPaymentMonitoring(updatedPayment.usdPaymentRequest.id)
        },
        onInstallWallet = { wallet ->
            // Redirect to app store
            openAppStore(wallet.packageName)
        }
    )
    dialog.show()
}
```

### Deep Link Generation

```kotlin
// Generate deep link for specific wallet
val trustWallet = ExternalWalletApp.TRUST_WALLET
val deepLink = gateway.generatePaymentDeepLink(
    walletApp = trustWallet,
    usdPaymentRequest = paymentRequest
)

// Example deep link: trust://send?address=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa&amount=0.001&asset=btc
```

### Custom Wallet Integration

```kotlin
// Add custom wallet app
val customWallet = ExternalWalletApp(
    name = "My Custom Wallet",
    packageName = "com.mycompany.wallet",
    supportedCoins = listOf(CoinType.BITCOIN, CoinType.ETHEREUM),
    deepLinkScheme = "mywallet"
)

// Check if wallet supports cryptocurrency
val isSupported = gateway.isWalletSupported(customWallet)

// Generate custom deep link
val customDeepLink = customWallet.generatePaymentDeepLink(
    address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    amount = BigDecimal("0.001"),
    coinType = CoinType.BITCOIN
)
```

### Error Handling

```kotlin
fun handleWalletPaymentErrors(paymentRequest: UsdPaymentRequestWithWalletSelection) {
    try {
        // Check if wallets are available
        if (paymentRequest.availableWalletApps.isEmpty()) {
            showErrorMessage("Keine unterstützten Wallet-Apps gefunden")
            return
        }
        
        // Check if selected wallet is installed
        paymentRequest.selectedWalletApp?.let { selectedWallet ->
            if (!selectedWallet.isInstalled()) {
                showInstallWalletDialog(selectedWallet)
                return
            }
        }
        
        // Generate deep link
        val deepLink = paymentRequest.getPaymentDeepLink()
        openDeepLink(deepLink)
        
    } catch (e: SecurityException) {
        showErrorMessage("Berechtigungen fehlen für Deep Link Öffnung")
    } catch (e: ActivityNotFoundException) {
        showErrorMessage("Keine App gefunden für diesen Deep Link")
    }
}
```

### Payment Status Monitoring

```kotlin
// Monitor payment status after wallet opens
fun startPaymentMonitoring(paymentId: String) {
    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            val status = gateway.checkUsdPaymentStatus(paymentId)
            updatePaymentUI(status)
            
            if (status == PaymentStatus.CONFIRMED) {
                showPaymentSuccess()
                break
            } else if (status == PaymentStatus.EXPIRED) {
                showPaymentExpired()
                break
            }
            
            delay(5000) // Check every 5 seconds
        }
    }
}
```

## Games Integration

The Freetime SDK includes a complete gaming module that allows developers to easily integrate cryptocurrency payments and player achievement tracking into their games.

### Quick Start for Games

```kotlin
import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.games.*

// Initialize the payment SDK
val paymentSDK = FreetimePaymentSDK()

// Configure your wallet to receive payments
paymentSDK.setUserWalletAddress(
    coinType = CoinType.BITCOIN,
    address = "your_bitcoin_address_here",
    name = "Game Revenue Wallet"
)

// Get the games SDK
val gamesSDK = paymentSDK.getGamesSDK()
```

### Creating a Custom Game

```kotlin
class MyCustomGame : GameInterface {
    override val gameType = GameType.CUSTOM_GAME
    override val minBetAmount = BigDecimal("0.001")
    override val maxBetAmount = BigDecimal("1.0")
    override val supportedCoins = listOf(CoinType.BITCOIN, CoinType.ETHEREUM)
    override val rtpPercentage = BigDecimal("95.0")
    
    override suspend fun play(
        amount: BigDecimal,
        coinType: CoinType,
        gameData: Map<String, Any>
    ): GameResult {
        // Your game logic here
        val playerWon = /* your win condition */ true
        val winAmount = if (playerWon) amount * BigDecimal("2") else BigDecimal.ZERO
        
        return GameResult(
            gameId = "my_game_${UUID.randomUUID()}",
            gameType = gameType,
            playerWon = playerWon,
            winAmount = winAmount,
            coinType = coinType,
            playAmount = amount,
            timestamp = LocalDateTime.now(),
            multiplier = if (playerWon) BigDecimal("2") else BigDecimal.ZERO
        )
    }
    
    override fun getGameRules(): GameRules {
        return GameRules(
            gameType = gameType,
            description = "My custom game description",
            minBetAmount = minBetAmount,
            maxBetAmount = maxBetAmount,
            supportedCoins = supportedCoins,
            rtpPercentage = rtpPercentage,
            maxWinMultiplier = BigDecimal("2"),
            houseEdgePercentage = BigDecimal("5"),
            volatilityLevel = VolatilityLevel.MEDIUM
        )
    }
}
```

### Playing Games with Payments

```kotlin
// Register your game
gamesSDK.registerGame("my_custom_game", MyCustomGame())

// Play the game with real payments
val result = gamesSDK.playGameWithPayment(
    playerId = "player_123",
    username = "PlayerName",
    gameId = "my_custom_game",
    amount = BigDecimal("0.01"),
    coinType = CoinType.BITCOIN,
    gameData = mapOf("difficulty" to "hard")
)

println("Game result: ${result.gameResult.getFormattedSummary()}")
println("Player level: ${result.playerProfile.currentLevel}")
println("New achievements: ${result.playerProgress.newAchievements}")
```

### Game Integration Framework

The Freetime SDK provides a complete framework for developers to integrate cryptocurrency payments and player achievement tracking into their custom games.

### Player Progress Tracking

```kotlin
// Get player profile
val profile = gamesSDK.getPlayerProfile("player_123")

// Get player statistics
val stats = gamesSDK.getPlayerStatistics("player_123")
println("Total games played: ${stats.totalGamesPlayed}")
println("Win rate: ${stats.winRate}%")
println("Current level: ${stats.level}")

// Get player achievements
val achievements = gamesSDK.getPlayerAchievements("player_123")
achievements.forEach { achievement ->
    println("Achievement: ${achievement.name} - ${achievement.description}")
}

// Get leaderboard
val leaderboard = gamesSDK.getLeaderboard(10)
leaderboard.forEachIndexed { index, player ->
    println("${index + 1}. ${player.username} - Level ${player.level}")
}
```

### Game Developer Revenue

The SDK automatically handles developer fees with a tiered structure:

| Transaction Amount | Developer Fee |
|-------------------|---------------|
| < $10 | 0.5% |
| $10 - $100 | 0.3% |
| $100 - $1,000 | 0.2% |
| $1,000 - $10,000 | 0.1% |
| > $10,000 | 0.05% |

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
- `UserWalletConfigExample.kt` - User wallet configuration and management
- `CryptocurrencySelectionExample.kt` - Cryptocurrency selection and payment options
- `WalletImportExample.kt` - Import existing wallets by address or private key
- `RequiredWalletConfigExample.kt` - Required wallet configuration for all cryptocurrencies
- `ExternalWalletIntegrationExample.kt` - External wallet app integration and deep link generation
- `DonationExample.kt` - Donation functionality with predefined amounts and custom options

## License

This project is licensed under the Apache-2.0 License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please create a Pull Request or open an Issue.

## Support

For questions and support, please open an Issue in the GitHub repository.
