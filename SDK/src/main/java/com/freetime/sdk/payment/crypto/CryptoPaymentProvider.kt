package com.freetime.sdk.payment.crypto

import com.freetime.sdk.payment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

abstract class CryptoPaymentProvider : PaymentProvider {
    
    override val supportedMethods = listOf(PaymentMethod.CRYPTO)
    
    override suspend fun processPayment(request: PaymentRequest): PaymentResult {
        val validation = validatePayment(request)
        if (validation is ValidationResult.Failure) {
            return PaymentResult.failure(
                PaymentError.INVALID_AMOUNT,
                "Validation failed: ${validation.errors.joinToString(", ")}"
            )
        }
        
        return try {
            val transactionId = executeTransaction(request)
            PaymentResult.success(transactionId, "Crypto payment processed successfully")
        } catch (e: Exception) {
            val error = mapExceptionToError(e)
            PaymentResult.failure(error, "Failed to process crypto payment: ${e.message}")
        }
    }
    
    override suspend fun validatePayment(request: PaymentRequest): ValidationResult {
        val baseValidation = request.validate()
        if (baseValidation is ValidationResult.Failure) {
            return baseValidation
        }
        
        val cryptoValidation = validateCryptoSpecifics(request)
        return if (cryptoValidation.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(cryptoValidation)
        }
    }
    
    protected abstract suspend fun executeTransaction(request: PaymentRequest): String
    protected abstract suspend fun validateCryptoSpecifics(request: PaymentRequest): List<String>
    protected abstract fun mapExceptionToError(exception: Exception): PaymentError
    
    override suspend fun getBalance(address: String): BigDecimal {
        return try {
            fetchBalance(address)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    protected abstract suspend fun fetchBalance(address: String): BigDecimal
    
    override suspend fun estimateFee(request: PaymentRequest): BigDecimal {
        return try {
            calculateFee(request)
        } catch (e: Exception) {
            BigDecimal("0.0001")
        }
    }
    
    protected abstract suspend fun calculateFee(request: PaymentRequest): BigDecimal
}
