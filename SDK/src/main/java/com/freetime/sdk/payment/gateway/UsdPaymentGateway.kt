package com.freetime.sdk.payment.gateway

import com.freetime.sdk.payment.CoinType
import com.freetime.sdk.payment.FreetimePaymentSDK
import java.math.BigDecimal

/**
 * Kompatibilitäts-Gateway für Apps, die die alte API verwenden.
 */
class UsdPaymentGateway(
    private val sdk: FreetimePaymentSDK,
    private val merchantWalletAddress: String,
    private val merchantCoinType: CoinType
) {

    /**
     * Alte API: createPaymentRequest(...)
     * Wird intern auf createPaymentAddress(...) gemappt.
     */
    suspend fun createPaymentRequest(
        usdAmount: BigDecimal,
        email: String? = null,
        note: String? = null
    ): PaymentRequest {
        return sdk.createPaymentGateway(
            merchantWalletAddress = merchantWalletAddress,
            merchantCoinType = merchantCoinType
        ).createPaymentAddress(
            amount = usdAmount,
            customerReference = email,
            description = note
        )
    }

    /**
     * Alte API: createUsdPaymentWithWalletSelection(...)
     * Dummy‑Implementierung, bis du Wallet‑Selection einbaust.
     */
    suspend fun createUsdPaymentWithWalletSelection(
        usdAmount: BigDecimal,
        email: String? = null,
        note: String? = null
    ): PaymentRequestWithWalletSelection {

        val request = createPaymentRequest(
            usdAmount = usdAmount,
            email = email,
            note = note
        )

        return PaymentRequestWithWalletSelection(
            paymentRequest = request,
            availableWalletApps = emptyList() // später erweiterbar
        )
    }
}
