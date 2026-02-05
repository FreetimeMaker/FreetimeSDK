package com.freetime.sdk.payment

import com.freetime.sdk.payment.open.BankTransferProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal

class FDroidPaymentSDKTest {
    
    private val sdk = FDroidPaymentSDK.Builder()
        .addBitcoinProvider(testnet = true)
        .addBankTransferProvider()
        .build()
    
    @Test
    fun `test F-Droid supported payment methods`() {
        val supportedMethods = sdk.getSupportedMethods()
        assertTrue(supportedMethods.contains(PaymentMethod.CRYPTO))
        assertTrue(supportedMethods.contains(PaymentMethod.BANK_TRANSFER))
        assertFalse(supportedMethods.contains(PaymentMethod.CASH))
    }
    
    @Test
    fun `test F-Droid supported currencies`() {
        val cryptoCurrencies = sdk.getSupportedCurrencies(PaymentMethod.CRYPTO)
        assertTrue(cryptoCurrencies.contains("BTC"))
        assertTrue(cryptoCurrencies.contains("ETH"))
        
        val bankCurrencies = sdk.getSupportedCurrencies(PaymentMethod.BANK_TRANSFER)
        assertTrue(bankCurrencies.contains("EUR"))
        assertTrue(bankCurrencies.contains("USD"))
    }
    
    @Test
    fun `test bitcoin payment validation`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("0.001"),
            currency = "BTC",
            paymentMethod = PaymentMethod.CRYPTO,
            recipientAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            senderAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
            privateKey = "test_private_key"
        )
        
        val validation = sdk.validatePayment(request)
        assertTrue(validation is ValidationResult.Success)
    }
    
    @Test
    fun `test bank transfer validation`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("25.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            description = "Test payment"
        )
        
        val validation = sdk.validatePayment(request)
        assertTrue(validation is ValidationResult.Success)
    }
    
    @Test
    fun `test bank transfer processing`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("100.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            description = "Test bank transfer"
        )
        
        val result = sdk.processPayment(request)
        assertTrue(result.isSuccess)
        assertNotNull(result.transactionId)
        assertTrue(result.transactionId!!.startsWith("FT"))
    }
    
    @Test
    fun `test bank transfer instructions generation`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("50.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            description = "Test payment"
        )
        
        val bankProvider = BankTransferProvider()
        val result = bankProvider.processPayment(request)
        
        if (result.isSuccess) {
            val instructions = bankProvider.generateBankTransferInstructions(request, result.transactionId!!)
            
            assertEquals(request.amount, instructions.amount)
            assertEquals("EUR", instructions.currency)
            assertEquals("Freetime SDK Payments", instructions.beneficiaryName)
            assertTrue(instructions.reference.startsWith("FT"))
            
            val formatted = instructions.getFormattedInstructions()
            assertTrue(formatted.contains("BANK TRANSFER INSTRUCTIONS"))
            assertTrue(formatted.contains(instructions.reference))
            assertTrue(formatted.contains("IMPORTANT: Include the reference number"))
        }
    }
    
    @Test
    fun `test transaction history`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("25.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER
        )
        
        val result = sdk.processPayment(request)
        assertTrue(result.isSuccess)
        
        val transactions = mutableListOf<Transaction>()
        sdk.getTransactionHistory().collect { transaction ->
            transactions.add(transaction)
        }
        
        assertTrue(transactions.isNotEmpty())
        assertTrue(transactions.any { it.id == result.transactionId })
    }
    
    @Test
    fun `test refund limitations`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("100.00"),
            currency = "EUR",
            paymentMethod = PaymentMethod.BANK_TRANSFER
        )
        
        val result = sdk.processPayment(request)
        assertTrue(result.isSuccess)
        
        val refundResult = sdk.refundPayment(result.transactionId!!, PaymentMethod.BANK_TRANSFER)
        assertFalse(refundResult.isSuccess)
        assertEquals(PaymentError.TRANSACTION_FAILED, refundResult.error)
        assertTrue(refundResult.message!!.contains("manual refund"))
    }
    
    @Test
    fun `test unsupported payment method`() = runBlocking {
        val request = PaymentRequest(
            amount = BigDecimal("10.00"),
            currency = "USD",
            paymentMethod = PaymentMethod.CASH
        )
        
        val result = sdk.processPayment(request)
        assertFalse(result.isSuccess)
        assertEquals(PaymentError.API_ERROR, result.error)
    }
}
