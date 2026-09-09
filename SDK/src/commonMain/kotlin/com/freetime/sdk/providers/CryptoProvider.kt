package com.freetime.sdk.providers

import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.UriPaymentProvider
import com.freetime.sdk.urlEncode

/**
 * A generic provider for cryptocurrencies that use URI schemes.
 */
class CryptoProvider(
    name: String,
    val scheme: String,
    recipientAddress: String,
    val amountParam: String = "amount"
) : UriPaymentProvider(name, recipientAddress) {
    
    override fun buildUri(request: PaymentRequest): String {
        return "$scheme:$recipientAddress?$amountParam=${request.amount}&label=${urlEncode(request.description)}"
    }
}
