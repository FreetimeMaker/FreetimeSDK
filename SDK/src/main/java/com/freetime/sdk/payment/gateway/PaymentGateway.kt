package com.freetime.sdk.payment.gateway

import com.freetime.sdk.payment.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

/**
 * Payment Gateway für automatische Weiterleitung von Zahlungen
 * an eine fest konfigurierte Empfängeradresse
 */
class PaymentGateway(
    private val sdk: FreetimePaymentSDK,
    private val merchantWalletAddress: String,
    private val merchantCoinType: CoinType
) {
    
    private val pendingPayments = ConcurrentHashMap<String, PendingPayment>()
    private val confirmedPayments = ConcurrentHashMap<String, ConfirmedPayment>()
    
    /**
     * Erstellt eine temporäre Zahlungsadresse für einen Kunden
     */
    suspend fun createPaymentAddress(
        amount: BigDecimal,
        customerReference: String? = null,
        description: String? = null
    ): PaymentRequest {
        
        // Erstelle ein temporäres Wallet für diese Zahlung
        val tempWallet = sdk.createWallet(merchantCoinType, "Payment-$customerReference")
        
        val paymentRequest = PaymentRequest(
            id = generatePaymentId(),
            customerAddress = tempWallet.address,
            merchantAddress = merchantWalletAddress,
            amount = amount,
            coinType = merchantCoinType,
            customerReference = customerReference,
            description = description,
            status = PaymentStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + PAYMENT_TIMEOUT
        )
        
        pendingPayments[paymentRequest.id] = PendingPayment(
            paymentRequest = paymentRequest,
            tempWallet = tempWallet
        )
        
        return paymentRequest
    }
    
    /**
     * Überprüft den Zahlungsstatus und leitet bei vollständiger Zahlung weiter
     */
    suspend fun checkPaymentStatus(paymentId: String): PaymentStatus {
        val pendingPayment = pendingPayments[paymentId] 
            ?: return PaymentStatus.NOT_FOUND
        
        val paymentRequest = pendingPayment.paymentRequest
        
        // Prüfe ob die Zahlung abgelaufen ist
        if (System.currentTimeMillis() > paymentRequest.expiresAt) {
            pendingPayments.remove(paymentId)
            return PaymentStatus.EXPIRED
        }
        
        // Prüfe das Guthaben auf der temporären Adresse
        val currentBalance = sdk.getBalance(pendingPayment.tempWallet.address)
        
        if (currentBalance >= paymentRequest.amount) {
            // Zahlung erhalten - leite an Händler weiter
            try {
                val txHash = sdk.send(
                    fromAddress = pendingPayment.tempWallet.address,
                    toAddress = merchantWalletAddress,
                    amount = paymentRequest.amount,
                    coinType = merchantCoinType
                )
                
                // Markiere als bestätigt
                val confirmedPayment = ConfirmedPayment(
                    paymentRequest = paymentRequest,
                    receivedAmount = currentBalance,
                    forwardedTxHash = txHash.transaction.id,
                    confirmedAt = System.currentTimeMillis()
                )
                
                confirmedPayments[paymentId] = confirmedPayment
                pendingPayments.remove(paymentId)
                
                return PaymentStatus.CONFIRMED
                
            } catch (e: Exception) {
                // Fehler beim Weiterleiten
                paymentRequest.status = PaymentStatus.FORWARDING_FAILED
                return PaymentStatus.FORWARDING_FAILED
            }
        }
        
        return PaymentStatus.PENDING
    }
    
    /**
     * Ruft detaillierte Zahlungsinformationen ab
     */
    fun getPaymentDetails(paymentId: String): PaymentDetails? {
        val pending = pendingPayments[paymentId]
        if (pending != null) {
            val currentBalance = try {
                // Synchroner Aufruf für Balance-Check
                runBlocking { sdk.getBalance(pending.tempWallet.address) }
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
            
            return PaymentDetails(
                paymentRequest = pending.paymentRequest,
                currentBalance = currentBalance,
                remainingAmount = maxOf(BigDecimal.ZERO, pending.paymentRequest.amount - currentBalance)
            )
        }
        
        val confirmed = confirmedPayments[paymentId]
        if (confirmed != null) {
            return PaymentDetails(
                paymentRequest = confirmed.paymentRequest,
                currentBalance = confirmed.receivedAmount,
                remainingAmount = BigDecimal.ZERO,
                forwardedTxHash = confirmed.forwardedTxHash,
                confirmedAt = confirmed.confirmedAt
            )
        }
        
        return null
    }
    
    /**
     * Storniert eine ausstehende Zahlung
     */
    fun cancelPayment(paymentId: String): Boolean {
        val pending = pendingPayments.remove(paymentId)
        return pending != null
    }
    
    /**
     * Ruft alle ausstehenden Zahlungen ab
     */
    fun getPendingPayments(): List<PaymentRequest> {
        return pendingPayments.values.map { it.paymentRequest }
    }
    
    /**
     * Ruft alle bestätigten Zahlungen ab
     */
    fun getConfirmedPayments(): List<ConfirmedPayment> {
        return confirmedPayments.values.toList()
    }
    
    /**
     * Generiert eine eindeutige Zahlungs-ID
     */
    private fun generatePaymentId(): String {
        return "pay_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    companion object {
        private const val PAYMENT_TIMEOUT = 30 * 60 * 1000L // 30 Minuten in Millisekunden
    }
}

/**
 * Repräsentiert eine Zahlungsanfrage
 */
data class PaymentRequest(
    val id: String,
    val customerAddress: String,
    val merchantAddress: String,
    val amount: BigDecimal,
    val coinType: CoinType,
    val customerReference: String? = null,
    val description: String? = null,
    var status: PaymentStatus = PaymentStatus.PENDING,
    val createdAt: Long,
    val expiresAt: Long
)

/**
 * Repräsentiert eine ausstehende Zahlung
 */
private data class PendingPayment(
    val paymentRequest: PaymentRequest,
    val tempWallet: Wallet
)

/**
 * Repräsentiert eine bestätigte Zahlung
 */
data class ConfirmedPayment(
    val paymentRequest: PaymentRequest,
    val receivedAmount: BigDecimal,
    val forwardedTxHash: String,
    val confirmedAt: Long
)

/**
 * Detaillierte Zahlungsinformationen
 */
data class PaymentDetails(
    val paymentRequest: PaymentRequest,
    val currentBalance: BigDecimal,
    val remainingAmount: BigDecimal,
    val forwardedTxHash: String? = null,
    val confirmedAt: Long? = null
)

/**
 * Zahlungsstatus Enumeration
 */
enum class PaymentStatus {
    PENDING,
    CONFIRMED,
    EXPIRED,
    FORWARDING_FAILED,
    NOT_FOUND
}
