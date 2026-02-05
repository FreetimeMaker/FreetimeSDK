package com.freetime.sdk.payment

import java.math.BigDecimal

data class PaymentRequest(
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod,
    val description: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val recipientAddress: String? = null,
    val senderAddress: String? = null,
    val privateKey: String? = null,
    val returnUrl: String? = null,
    val cancelUrl: String? = null
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (amount <= BigDecimal.ZERO) {
            errors.add("Amount must be greater than zero")
        }
        
        if (currency.isBlank()) {
            errors.add("Currency cannot be blank")
        }
        
        when (paymentMethod) {
            PaymentMethod.CRYPTO -> {
                if (recipientAddress.isNullOrBlank()) {
                    errors.add("Recipient address is required for crypto payments")
                }
                if (senderAddress.isNullOrBlank()) {
                    errors.add("Sender address is required for crypto payments")
                }
                if (privateKey.isNullOrBlank()) {
                    errors.add("Private key is required for crypto payments")
                }
            }
            PaymentMethod.BANK_TRANSFER -> {
                // Bank transfers don't require additional fields
            }
            PaymentMethod.DIGITAL_WALLET -> {
                if (returnUrl.isNullOrBlank()) {
                    errors.add("Return URL is required for digital wallet payments")
                }
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()
}
