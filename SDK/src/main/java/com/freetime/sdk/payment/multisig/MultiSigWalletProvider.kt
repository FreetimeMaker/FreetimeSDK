package com.freetime.sdk.payment.multisig

import com.freetime.sdk.payment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

class MultiSigWalletProvider(
    private val networkUrl: String = "https://api.blockchair.com",
    private val requiredSignatures: Int = 2,
    private val totalSigners: Int = 3
) : PaymentProvider {
    
    override val supportedMethods = listOf(PaymentMethod.CRYPTO)
    override val supportedCurrencies = listOf("BTC", "ETH", "LTC")
    
    private val walletStore = mutableMapOf<String, MultiSigWallet>()
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
            val txId = createMultiSigTransaction(request)
            PaymentResult.success(
                txId,
                "Multi-signature transaction created. Waiting for signatures."
            )
        } catch (e: Exception) {
            PaymentResult.failure(
                PaymentError.TRANSACTION_FAILED,
                "Failed to create multi-signature transaction: ${e.message}"
            )
        }
    }
    
    override suspend fun validatePayment(request: PaymentRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (request.amount < BigDecimal("0.001")) {
            errors.add("Minimum multi-signature amount is 0.001")
        }
        
        if (!supportedCurrencies.contains(request.currency)) {
            errors.add("Currency ${request.currency} is not supported for multi-signature")
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
            "Multi-signature transactions cannot be refunded automatically"
        )
    }
    
    override fun getTransactionHistory(address: String?): Flow<Transaction> {
        return flowOf(*transactionStore.values.toTypedArray())
    }
    
    override suspend fun getBalance(address: String): BigDecimal {
        return try {
            val wallet = walletStore[address]
            wallet?.balance ?: BigDecimal.ZERO
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    override suspend fun estimateFee(request: PaymentRequest): BigDecimal {
        val baseFee = when (request.currency) {
            "BTC" -> BigDecimal("0.0001")
            "ETH" -> BigDecimal("0.002")
            "LTC" -> BigDecimal("0.00005")
            else -> BigDecimal("0.0001")
        }
        
        // Multi-signature transactions are larger, so higher fees
        val sizeMultiplier = BigDecimal(requiredSignatures.toString()).divide(BigDecimal("2"))
        return baseFee * sizeMultiplier
    }
    
    suspend fun createMultiSigWallet(
        name: String,
        signers: List<String>,
        currency: String
    ): MultiSigWallet {
        val walletId = generateWalletId()
        val redeemScript = generateRedeemScript(signers)
        
        val wallet = MultiSigWallet(
            id = walletId,
            name = name,
            address = generateMultiSigAddress(redeemScript, currency),
            signers = signers,
            requiredSignatures = requiredSignatures,
            totalSigners = totalSigners,
            currency = currency,
            redeemScript = redeemScript,
            balance = BigDecimal.ZERO,
            createdAt = System.currentTimeMillis()
        )
        
        walletStore[walletId] = wallet
        return wallet
    }
    
    suspend fun addSignature(transactionId: String, signerAddress: String, signature: String): SignatureResult {
        val transaction = transactionStore[transactionId]
            ?: return SignatureResult.failure("Transaction not found")
        
        if (!transaction.metadata["signers"]?.contains(signerAddress) ?: false) {
            return SignatureResult.failure("Signer not authorized")
        }
        
        val currentSignatures = transaction.metadata["signatures_count"]?.toIntOrNull() ?: 0
        val newSignatures = currentSignatures + 1
        
        val updatedMetadata = transaction.metadata.toMutableMap()
        updatedMetadata["signatures_count"] = newSignatures.toString()
        updatedMetadata["signatures"] = (updatedMetadata["signatures"]?.split(",") ?: emptyList())
            .plus(signature)
            .filter { it.isNotEmpty() }
            .joinToString(",")
        
        val updatedTransaction = transaction.copy(
            metadata = updatedMetadata,
            status = if (newSignatures >= requiredSignatures) {
                TransactionStatus.COMPLETED
            } else {
                TransactionStatus.PENDING
            }
        )
        
        transactionStore[transactionId] = updatedTransaction
        
        return if (newSignatures >= requiredSignatures) {
            SignatureResult.success("Transaction fully signed and executed")
        } else {
            SignatureResult.success("Signature added. $newSignatures/$requiredSignatures signatures collected")
        }
    }
    
    suspend fun getPendingTransactions(walletId: String): List<Transaction> {
        return transactionStore.values.filter { 
            it.status == TransactionStatus.PENDING && 
            it.metadata["wallet_id"] == walletId 
        }
    }
    
    suspend fun getWalletInfo(walletId: String): MultiSigWallet? {
        return walletStore[walletId]
    }
    
    private suspend fun createMultiSigTransaction(request: PaymentRequest): String {
        val txId = "msig_${System.currentTimeMillis()}"
        
        val transaction = Transaction(
            id = txId,
            amount = request.amount,
            currency = request.currency,
            from = "multisig_wallet",
            to = request.recipientAddress ?: "unknown",
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            fee = estimateFee(request),
            metadata = mapOf(
                "payment_method" to "multisig",
                "required_signatures" to requiredSignatures.toString(),
                "signatures_count" to "0",
                "signers" to "",
                "signatures" to ""
            )
        )
        
        transactionStore[txId] = transaction
        return txId
    }
    
    private fun generateWalletId(): String {
        return "wallet_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    private fun generateRedeemScript(signers: List<String>): String {
        return "5221${signers.joinToString("21") { it.take(40) }}21${requiredSignatures.toString().padStart(2, '0')}ae"
    }
    
    private fun generateMultiSigAddress(redeemScript: String, currency: String): String {
        return when (currency) {
            "BTC" -> "3${redeemScript.takeLast(32)}"
            "LTC" -> "3${redeemScript.takeLast(32)}"
            "ETH" -> "0x${redeemScript.takeLast(40)}"
            else -> "unknown_address"
        }
    }
}

data class MultiSigWallet(
    val id: String,
    val name: String,
    val address: String,
    val signers: List<String>,
    val requiredSignatures: Int,
    val totalSigners: Int,
    val currency: String,
    val redeemScript: String,
    val balance: BigDecimal,
    val createdAt: Long
) {
    fun getFormattedWallet(): String {
        return buildString {
            appendLine("MULTI-SIGNATURE WALLET")
            appendLine("========================")
            appendLine()
            appendLine("Wallet ID: $id")
            appendLine("Name: $name")
            appendLine("Address: $address")
            appendLine("Currency: $currency")
            appendLine("Balance: $balance $currency")
            appendLine("Signatures Required: $requiredSignatures/$totalSigners")
            appendLine("Signers:")
            signers.forEachIndexed { index, signer ->
                appendLine("  ${index + 1}. ${signer.take(20)}...")
            }
            appendLine()
            appendLine("Created: ${java.util.Date(createdAt)}")
        }
    }
    
    fun isFullySigned(signatureCount: Int): Boolean {
        return signatureCount >= requiredSignatures
    }
}

data class SignatureResult(
    val success: Boolean,
    val message: String
) {
    companion object {
        fun success(message: String) = SignatureResult(true, message)
        fun failure(message: String) = SignatureResult(false, message)
    }
}
