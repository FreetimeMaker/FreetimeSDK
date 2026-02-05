package com.freetime.sdk.payment.open

import com.freetime.sdk.payment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

class GitHubSponsorsProvider(
    private val username: String = "freetime-sdk",
    private val accessToken: String? = null
) : PaymentProvider {
    
    override val supportedMethods = listOf(PaymentMethod.DIGITAL_WALLET)
    override val supportedCurrencies = listOf("USD", "EUR")
    
    private val transactionStore = mutableMapOf<String, Transaction>()
    
    override suspend fun processPayment(request: PaymentRequest): PaymentResult {
        val validation = validatePayment(request)
        if (validation is ValidationResult.Failure) {
            return PaymentResult.failure(
                PaymentError.INVALID_AMOUNT,
                "Validation failed: ${validation.errors.joinToString(", ")}"
            )
        }
        
        return try {
            val sponsorId = createGitHubSponsorship(request)
            val transaction = createTransaction(request, sponsorId)
            transactionStore[sponsorId] = transaction
            
            PaymentResult.success(
                sponsorId,
                "GitHub Sponsors sponsorship created. Sponsor ID: $sponsorId"
            )
        } catch (e: Exception) {
            PaymentResult.failure(
                PaymentError.TRANSACTION_FAILED,
                "Failed to create GitHub Sponsors sponsorship: ${e.message}"
            )
        }
    }
    
    override suspend fun validatePayment(request: PaymentRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (request.amount < BigDecimal("1.00")) {
            errors.add("Minimum GitHub Sponsors amount is 1.00")
        }
        
        if (request.amount > BigDecimal("12000.00")) {
            errors.add("Maximum GitHub Sponsors amount is 12000.00 per month")
        }
        
        if (!supportedCurrencies.contains(request.currency)) {
            errors.add("Currency ${request.currency} is not supported")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
    
    override suspend fun getTransactionStatus(transactionId: String): TransactionStatus {
        val transaction = transactionStore[transactionId]
        return transaction?.status ?: TransactionStatus.PENDING
    }
    
    override suspend fun refundPayment(transactionId: String, amount: BigDecimal?): PaymentResult {
        return PaymentResult.failure(
            PaymentError.TRANSACTION_FAILED,
            "GitHub Sponsors cannot be refunded automatically. Please cancel your sponsorship."
        )
    }
    
    override fun getTransactionHistory(address: String?): Flow<Transaction> {
        return flowOf(*transactionStore.values.toTypedArray())
    }
    
    override suspend fun getBalance(address: String): BigDecimal {
        return BigDecimal.ZERO
    }
    
    override suspend fun estimateFee(request: PaymentRequest): BigDecimal {
        return BigDecimal("0.00") // GitHub Sponsors doesn't charge fees to sponsors
    }
    
    fun generateSponsorUrl(request: PaymentRequest, sponsorId: String): String {
        return "https://github.com/sponsors/$username"
    }
    
    suspend fun createSponsorshipTier(request: PaymentRequest): GitHubSponsorshipTier {
        val sponsorId = createGitHubSponsorship(request)
        return GitHubSponsorshipTier(
            id = sponsorId,
            amount = request.amount,
            currency = request.currency,
            description = request.description ?: "Custom sponsorship",
            sponsorUrl = generateSponsorUrl(request, sponsorId),
            isRecurring = true,
            createdAt = System.currentTimeMillis()
        )
    }
    
    suspend fun getSponsorshipTiers(): List<GitHubSponsorshipTier> {
        return listOf(
            GitHubSponsorshipTier(
                id = "bronze",
                amount = BigDecimal("5.00"),
                currency = "USD",
                description = "Bronze Sponsor",
                sponsorUrl = "https://github.com/sponsors/$username",
                isRecurring = true,
                createdAt = System.currentTimeMillis()
            ),
            GitHubSponsorshipTier(
                id = "silver",
                amount = BigDecimal("10.00"),
                currency = "USD",
                description = "Silver Sponsor",
                sponsorUrl = "https://github.com/sponsors/$username",
                isRecurring = true,
                createdAt = System.currentTimeMillis()
            ),
            GitHubSponsorshipTier(
                id = "gold",
                amount = BigDecimal("25.00"),
                currency = "USD",
                description = "Gold Sponsor",
                sponsorUrl = "https://github.com/sponsors/$username",
                isRecurring = true,
                createdAt = System.currentTimeMillis()
            )
        )
    }
    
    private suspend fun createGitHubSponsorship(request: PaymentRequest): String {
        Thread.sleep(1000)
        val sponsorId = "GH${System.currentTimeMillis()}"
        
        val transaction = createTransaction(request, sponsorId)
        transactionStore[sponsorId] = transaction
        
        return sponsorId
    }
    
    private fun createTransaction(request: PaymentRequest, sponsorId: String): Transaction {
        return Transaction(
            id = sponsorId,
            amount = request.amount,
            currency = request.currency,
            from = "github_sponsor",
            to = username,
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            fee = BigDecimal.ZERO,
            metadata = mapOf(
                "payment_method" to "github_sponsors",
                "description" to (request.description ?: ""),
                "sponsor_url" to generateSponsorUrl(request, sponsorId),
                "recurring" to "true"
            )
        )
    }
}

data class GitHubSponsorshipTier(
    val id: String,
    val amount: BigDecimal,
    val currency: String,
    val description: String,
    val sponsorUrl: String,
    val isRecurring: Boolean,
    val createdAt: Long
) {
    fun getFormattedTier(): String {
        return buildString {
            appendLine("GITHUB SPONSORS TIER")
            appendLine("=====================")
            appendLine()
            appendLine("Tier: $description")
            appendLine("Amount: $amount $currency")
            appendLine("Type: ${if (isRecurring) "Monthly recurring" else "One-time"}")
            appendLine("Sponsor URL: $sponsorUrl")
            appendLine()
            appendLine("Thank you for supporting open-source development! 🎉")
        }
    }
}
