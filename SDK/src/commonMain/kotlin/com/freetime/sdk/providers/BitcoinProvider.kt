package com.freetime.sdk.providers

import com.freetime.sdk.PaymentRequest
import com.freetime.sdk.UriPaymentProvider
import com.freetime.sdk.urlEncode

/**
 * Bitcoin (BTC) Provider.
 * Uses the 'bitcoin:' URI scheme (BIP21).
 */
class BitcoinProvider(recipientAddress: String) : UriPaymentProvider("Bitcoin (BTC)", recipientAddress) {

    override fun buildUri(request: PaymentRequest): String {
        return "bitcoin:$recipientAddress?amount=${request.amount}&label=${urlEncode(request.description)}"
    }
}
