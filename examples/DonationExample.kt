import com.freetime.sdk.payment.*
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking

/**
 * Example demonstrating donations in the Freetime SDK
 */
class DonationExample {
    
    fun exampleSelectAndDonate() = runBlocking {
        // Initialize SDK and selector
        val sdk = FreetimePaymentSDK()
        val amountSelector = DonationAmountSelector()
        
        val recipientAddress = "1A1z7agoat2JLLSQwowL5fTDnwFLzhCe4Y"
        val coinType = CoinType.BITCOIN
        
        // Get predefined donation options
        val options = amountSelector.getDonationOptions(recipientAddress, coinType, sdk)
        
        println("=== Spendenoptionen ===")
        options.forEachIndexed { index, option ->
            println("${index + 1}. ${option.label}")
            println("   Gebühren: ${option.networkFee + option.developerFee}")
            println("   Gesamtkosten: ${option.totalCost}")
        }
        
        // User selects option (e.g., option 3)
        val selectedOption = options[2]
        println("\nAusgewählte Option: ${selectedOption.label}")
        
        // Create donation with selected amount
        val donation = sdk.donate(
            toAddress = recipientAddress,
            amount = selectedOption.amount,
            coinType = coinType,
            donorName = "Max Mustermann",
            donationMessage = "Für euer tolles Projekt!"
        )
        
        println("Spende erstellt!")
        println("Betrag: ${donation.donation.amount} ${coinType.coinName}")
        println("Gebühren: ${donation.feeBreakdown.totalFee} ${coinType.coinName}")
        println("Gesamtkosten: ${donation.totalAmount} ${coinType.coinName}")
        
        // Broadcast donation
        val txId = sdk.broadcastDonation(donation.donation)
        println("Im Netzwerk übertragen: $txId")
    }
    
    fun exampleCustomAmount() = runBlocking {
        val sdk = FreetimePaymentSDK()
        
        val recipientAddress = "1A1z7agoat2JLLSQwowL5fTDnwFLzhCe4Y"
        val customAmount = BigDecimal("0.05")  // Spender wählt benutzerdefinierten Betrag
        
        // Validate and create donation
        if (sdk.validateDonationAmount(customAmount, CoinType.BITCOIN)) {
            val donation = sdk.donate(
                toAddress = recipientAddress,
                amount = customAmount,
                coinType = CoinType.BITCOIN,
                donorName = "Anna Schmidt"
            )
            
            println("Spende mit ${donation.donation.amount} BTC erstellt")
            println("Gesamtkosten: ${donation.totalAmount} BTC")
        } else {
            println("Betrag ist zu klein!")
        }
    }
    
    fun exampleWithCustomLabels() = runBlocking {
        val sdk = FreetimePaymentSDK()
        val amountSelector = DonationAmountSelector()
        
        val recipientAddress = "0x0987654321098765432109876543210987654321"
        
        // Define custom labels for amounts
        val customLabels = mapOf(
            BigDecimal("0.1") to "Kleine Spende (0.1 ETH)",
            BigDecimal("0.5") to "Mittlere Spende (0.5 ETH)",
            BigDecimal("1") to "Große Spende (1 ETH)",
            BigDecimal("5") to "Sehr großzügig (5 ETH)",
            BigDecimal("10") to "Mega-Unterstützung (10 ETH)"
        )
        
        // Get options with custom labels
        val options = amountSelector.getDonationOptionsWithLabels(
            recipientAddress,
            CoinType.ETHEREUM,
            sdk,
            customLabels
        )
        
        println("=== Ethereum Spendenoptionen ===")
        options.forEach { option ->
            println("${option.label}")
            println("  Gebühren: ${option.networkFee + option.developerFee} ETH")
            println("  Gesamtkosten: ${option.totalCost} ETH")
            println()
        }
    }
    
    fun exampleDonateWithFees() = runBlocking {
        // Initialize SDK
        val sdk = FreetimePaymentSDK()
        
        // Create a donation with fees
        val recipientAddress = "1A1z7agoat2JLLSQwowL5fTDnwFLzhCe4Y"  // Charity wallet
        val donationAmount = BigDecimal("0.1")  // 0.1 BTC
        
        try {
            val donationWithFees = sdk.donate(
                toAddress = recipientAddress,
                amount = donationAmount,
                coinType = CoinType.BITCOIN,
                donorName = "John Donor",
                donationMessage = "Supporting your great cause!"
            )
            
            println("Donation created successfully!")
            println("Donation ID: ${donationWithFees.donation.id}")
            println("Amount: ${donationWithFees.donation.amount} BTC")
            println("Network Fee: ${donationWithFees.networkFee} BTC")
            println("Developer Fee: ${donationWithFees.developerFee} BTC")
            println("Total Cost: ${donationWithFees.totalAmount} BTC")
            println("Donor: ${donationWithFees.donation.donorName}")
            println("Message: ${donationWithFees.donation.donationMessage}")
            
            // Broadcast donation to blockchain
            val txId = sdk.broadcastDonation(donationWithFees.donation)
            println("Broadcasted with transaction ID: $txId")
            
        } catch (e: Exception) {
            println("Error creating donation: ${e.message}")
        }
    }
    
    fun exampleDonateWithoutFees() = runBlocking {
        val sdk = FreetimePaymentSDK()
        
        // Create a donation without fees (full amount goes to recipient)
        val recipientAddress = "0x0987654321098765432109876543210987654321"
        val donationAmount = BigDecimal("1.0")  // 1 ETH
        
        try {
            val donation = sdk.donateWithoutFees(
                toAddress = recipientAddress,
                amount = donationAmount,
                coinType = CoinType.ETHEREUM,
                donorName = "Jane Donor"
            )
            
            println("Donation created without fees!")
            println("Full amount ${donation.amount} ETH will go to recipient")
            
            // Broadcast the donation
            val txId = sdk.broadcastDonation(donation)
            println("Broadcasted with transaction ID: $txId")
            
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
    
    fun exampleValidateDonationAmount() {
        val sdk = FreetimePaymentSDK()
        
        // Bitcoin minimum is 0.0001
        val isValidBTC1 = sdk.validateDonationAmount(BigDecimal("0.0001"), CoinType.BITCOIN)
        val isValidBTC2 = sdk.validateDonationAmount(BigDecimal("0.00001"), CoinType.BITCOIN)
        
        println("0.0001 BTC is valid: $isValidBTC1")  // true
        println("0.00001 BTC is valid: $isValidBTC2") // false
        
        // Ethereum minimum is 0.01
        val isValidETH1 = sdk.validateDonationAmount(BigDecimal("0.01"), CoinType.ETHEREUM)
        val isValidETH2 = sdk.validateDonationAmount(BigDecimal("0.001"), CoinType.ETHEREUM)
        
        println("0.01 ETH is valid: $isValidETH1")    // true
        println("0.001 ETH is valid: $isValidETH2")   // false
    }
    
    fun exampleGetFeeEstimate() = runBlocking {
        val sdk = FreetimePaymentSDK()
        
        val recipientAddress = "1A1z7agoat2JLLSQwowL5fTDnwFLzhCe4Y"
        val donationAmount = BigDecimal("0.1")
        
        // Get fee estimate before creating donation
        val feeEstimate = sdk.getDonationFeeEstimate(
            toAddress = recipientAddress,
            amount = donationAmount,
            coinType = CoinType.BITCOIN
        )
        
        println("Estimated fee for 0.1 BTC donation: $feeEstimate BTC")
        
        // Get fee breakdown
        val feeBreakdown = sdk.getDonationFeeBreakdown(
            amount = donationAmount,
            networkFee = feeEstimate,
            coinType = CoinType.BITCOIN
        )
        
        println("Network Fee: ${feeBreakdown.networkFee}")
        println("Developer Fee: ${feeBreakdown.developerFee}")
        println("Total Fee: ${feeBreakdown.totalFee}")
        println("Recipient Amount: ${feeBreakdown.recipientAmount}")
    }
}

