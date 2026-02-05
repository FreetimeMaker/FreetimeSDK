package com.freetime.sdk.payment.open

import com.freetime.sdk.payment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

class BankTransferProvider : PaymentProvider {
    
    override val supportedMethods = listOf(PaymentMethod.BANK_TRANSFER)
    override val supportedCurrencies = listOf("EUR", "USD", "GBP", "CHF", "CAD", "AUD")
    
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
            val transactionId = generateBankTransferReference(request)
            val transaction = createTransaction(request, transactionId)
            transactionStore[transactionId] = transaction
            
            PaymentResult.success(
                transactionId,
                "Bank transfer instructions generated. Reference: $transactionId"
            )
        } catch (e: Exception) {
            PaymentResult.failure(
                PaymentError.TRANSACTION_FAILED,
                "Failed to generate bank transfer: ${e.message}"
            )
        }
    }
    
    override suspend fun validatePayment(request: PaymentRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (request.amount < BigDecimal("1.00")) {
            errors.add("Minimum bank transfer amount is 1.00")
        }
        
        if (request.amount > BigDecimal("100000.00")) {
            errors.add("Maximum bank transfer amount is 100,000.00")
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
            "Bank transfers require manual refund processing through your bank"
        )
    }
    
    override fun getTransactionHistory(address: String?): Flow<Transaction> {
        return flowOf(*transactionStore.values.toTypedArray())
    }
    
    override suspend fun getBalance(address: String): BigDecimal {
        return BigDecimal.ZERO
    }
    
    override suspend fun estimateFee(request: PaymentRequest): BigDecimal {
        return when (request.currency) {
            "EUR" -> BigDecimal("0.00")
            "USD" -> BigDecimal("3.00")
            "GBP" -> BigDecimal("1.00")
            else -> BigDecimal("2.50")
        }
    }
    
    fun generateBankTransferInstructions(request: PaymentRequest, transactionId: String): BankTransferInstructions {
        return BankTransferInstructions(
            reference = transactionId,
            amount = request.amount,
            currency = request.currency,
            beneficiaryName = "Freetime SDK Payments",
            beneficiaryAccount = getAccountNumber(request.currency),
            bankName = getBankName(request.currency),
            routingCode = getRoutingCode(request.currency),
            iban = getIban(request.currency),
            swift = getSwiftCode(request.currency),
            description = "Payment: ${request.description ?: "SDK Payment"} - Ref: $transactionId"
        )
    }
    
    private fun generateBankTransferReference(request: PaymentRequest): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "FT${timestamp}${random}"
    }
    
    private fun createTransaction(request: PaymentRequest, transactionId: String): Transaction {
        return Transaction(
            id = transactionId,
            amount = request.amount,
            currency = request.currency,
            from = "bank_account",
            to = "freetime_account",
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            fee = estimateFee(request),
            metadata = mapOf(
                "payment_method" to "bank_transfer",
                "description" to (request.description ?: ""),
                "reference" to transactionId
            )
        )
    }
    
    private fun getAccountNumber(currency: String): String {
        return when (currency) {
            "EUR" -> "DE89370400440532013000"
            "USD" -> "1234567890123456"
            "GBP" -> "60837100000023456789"
            else -> "000123456789"
        }
    }
    
    private fun getBankName(currency: String): String {
        return when (currency) {
            "EUR" -> "Freetime Bank GmbH"
            "USD" -> "Freetime Bank NA"
            "GBP" -> "Freetime Bank UK Ltd"
            else -> "Freetime Bank International"
        }
    }
    
    private fun getRoutingCode(currency: String): String {
        return when (currency) {
            "USD" -> "021000021"
            "GBP" -> "608371"
            else -> ""
        }
    }
    
    private fun getIban(currency: String): String {
        return when (currency) {
            "EUR" -> "DE89370400440532013000"
            "GBP" -> "GB29NWBK60161331926819"
            else -> ""
        }
    }
    
    private fun getSwiftCode(currency: String): String {
        return when (currency) {
            "EUR" -> "DEUTDEFF"
            "USD" -> "BOFAUS3N"
            "GBP" -> "NWBKGB2L"
            else -> "FTBKXXXX"
        }
    }
}

data class BankTransferInstructions(
    val reference: String,
    val amount: BigDecimal,
    val currency: String,
    val beneficiaryName: String,
    val beneficiaryAccount: String,
    val bankName: String,
    val routingCode: String,
    val iban: String,
    val swift: String,
    val description: String
) {
    fun getFormattedInstructions(): String {
        return buildString {
            appendLine("BANK TRANSFER INSTRUCTIONS")
            appendLine("==========================")
            appendLine()
            appendLine("Reference: $reference")
            appendLine("Amount: $amount $currency")
            appendLine("Beneficiary: $beneficiaryName")
            appendLine("Bank: $bankName")
            appendLine()
            
            if (iban.isNotEmpty()) {
                appendLine("IBAN: $iban")
            }
            if (beneficiaryAccount.isNotEmpty()) {
                appendLine("Account: $beneficiaryAccount")
            }
            if (routingCode.isNotEmpty()) {
                appendLine("Routing: $routingCode")
            }
            if (swift.isNotEmpty()) {
                appendLine("SWIFT: $swift")
            }
            
            appendLine()
            appendLine("Description: $description")
            appendLine()
            appendLine("IMPORTANT: Include the reference number in your transfer.")
        }
    }
}
