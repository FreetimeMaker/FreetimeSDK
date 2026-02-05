package com.freetime.sdk.payment

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface PaymentProvider {
    val supportedMethods: List<PaymentMethod>
    val supportedCurrencies: List<String>
    
    suspend fun processPayment(request: PaymentRequest): PaymentResult
    
    suspend fun validatePayment(request: PaymentRequest): ValidationResult
    
    suspend fun getTransactionStatus(transactionId: String): TransactionStatus
    
    suspend fun refundPayment(transactionId: String, amount: BigDecimal? = null): PaymentResult
    
    fun getTransactionHistory(address: String? = null): Flow<Transaction>
    
    suspend fun getBalance(address: String): BigDecimal
    
    suspend fun estimateFee(request: PaymentRequest): BigDecimal
}

data class Transaction(
    val id: String,
    val amount: BigDecimal,
    val currency: String,
    val from: String,
    val to: String,
    val status: TransactionStatus,
    val timestamp: Long,
    val fee: BigDecimal,
    val confirmations: Int = 0,
    val blockNumber: Long? = null,
    val metadata: Map<String, String> = emptyMap()
)

enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED
}
