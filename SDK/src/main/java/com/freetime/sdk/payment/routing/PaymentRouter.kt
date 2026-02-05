package com.freetime.sdk.payment.routing

import com.freetime.sdk.payment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

class PaymentRouter(
    private val providers: Map<PaymentMethod, List<PaymentProvider>>
) {
    
    suspend fun findBestProvider(
        request: PaymentRequest,
        criteria: RoutingCriteria = RoutingCriteria.LOWEST_FEE
    ): RoutingResult {
        val availableProviders = providers[request.paymentMethod]?.filter { 
            it.supportedCurrencies.contains(request.currency) 
        } ?: emptyList()
        
        if (availableProviders.isEmpty()) {
            return RoutingResult.failure("No providers available for this payment method and currency")
        }
        
        return when (criteria) {
            RoutingCriteria.LOWEST_FEE -> findLowestFeeProvider(availableProviders, request)
            RoutingCriteria.FASTEST -> findFastestProvider(availableProviders, request)
            RoutingCriteria.HIGHEST_SUCCESS_RATE -> findMostReliableProvider(availableProviders, request)
            RoutingCriteria.BEST_VALUE -> findBestValueProvider(availableProviders, request)
        }
    }
    
    suspend fun optimizePayment(
        request: PaymentRequest,
        maxProviders: Int = 3
    ): List<OptimizedPaymentOption> {
        val availableProviders = providers[request.paymentMethod]?.filter { 
            it.supportedCurrencies.contains(request.currency) 
        } ?: emptyList()
        
        val options = mutableListOf<OptimizedPaymentOption>()
        
        for (provider in availableProviders.take(maxProviders)) {
            try {
                val fee = provider.estimateFee(request)
                val estimatedTime = estimateProcessingTime(provider, request)
                val successRate = getSuccessRate(provider)
                val totalCost = request.amount.add(fee)
                
                options.add(
                    OptimizedPaymentOption(
                        provider = provider::class.simpleName ?: "Unknown",
                        fee = fee,
                        estimatedTime = estimatedTime,
                        successRate = successRate,
                        totalCost = totalCost,
                        score = calculateOptionScore(fee, estimatedTime, successRate)
                    )
                )
            } catch (e: Exception) {
                // Skip provider if estimation fails
            }
        }
        
        return options.sortedByDescending { it.score }
    }
    
    suspend fun splitPayment(
        request: PaymentRequest,
        splitStrategy: SplitStrategy = SplitStrategy.EQUAL
    ): SplitPaymentResult {
        val availableProviders = providers[request.paymentMethod]?.filter { 
            it.supportedCurrencies.contains(request.currency) 
        } ?: emptyList()
        
        if (availableProviders.size < 2) {
            return SplitPaymentResult.failure("Need at least 2 providers for split payment")
        }
        
        return when (splitStrategy) {
            SplitStrategy.EQUAL -> executeEqualSplit(request, availableProviders)
            SplitStrategy.WEIGHTED -> executeWeightedSplit(request, availableProviders)
            SplitStrategy.OPTIMAL -> executeOptimalSplit(request, availableProviders)
        }
    }
    
    suspend fun getRoutingAnalytics(): RoutingAnalytics {
        return RoutingAnalytics(
            totalRoutings = 1000,
            averageFeeSaved = BigDecimal("0.05"),
            averageTimeSaved = 2000L, // 2 seconds
            successRateImprovement = BigDecimal("0.15"),
            mostUsedProvider = "BitcoinPaymentProvider",
            leastUsedProvider = "MoneroPaymentProvider",
            averageRoutingTime = 500L
        )
    }
    
    private suspend fun findLowestFeeProvider(
        providers: List<PaymentProvider>,
        request: PaymentRequest
    ): RoutingResult {
        var bestProvider: PaymentProvider? = null
        var lowestFee = BigDecimal(Double.MAX_VALUE)
        
        for (provider in providers) {
            try {
                val fee = provider.estimateFee(request)
                if (fee < lowestFee) {
                    lowestFee = fee
                    bestProvider = provider
                }
            } catch (e: Exception) {
                // Skip provider on error
            }
        }
        
        return if (bestProvider != null) {
            RoutingResult.success(bestProvider::class.simpleName ?: "Unknown", lowestFee)
        } else {
            RoutingResult.failure("Could not estimate fees for any provider")
        }
    }
    
    private suspend fun findFastestProvider(
        providers: List<PaymentProvider>,
        request: PaymentRequest
    ): RoutingResult {
        var bestProvider: PaymentProvider? = null
        var fastestTime = Long.MAX_VALUE
        
        for (provider in providers) {
            try {
                val time = estimateProcessingTime(provider, request)
                if (time < fastestTime) {
                    fastestTime = time
                    bestProvider = provider
                }
            } catch (e: Exception) {
                // Skip provider on error
            }
        }
        
        return if (bestProvider != null) {
            RoutingResult.success(bestProvider::class.simpleName ?: "Unknown", fastestTime.toString())
        } else {
            RoutingResult.failure("Could not estimate processing time for any provider")
        }
    }
    
    private suspend fun findMostReliableProvider(
        providers: List<PaymentProvider>,
        request: PaymentRequest
    ): RoutingResult {
        var bestProvider: PaymentProvider? = null
        var highestSuccessRate = BigDecimal.ZERO
        
        for (provider in providers) {
            try {
                val successRate = getSuccessRate(provider)
                if (successRate > highestSuccessRate) {
                    highestSuccessRate = successRate
                    bestProvider = provider
                }
            } catch (e: Exception) {
                // Skip provider on error
            }
        }
        
        return if (bestProvider != null) {
            RoutingResult.success(bestProvider::class.simpleName ?: "Unknown", highestSuccessRate.toString())
        } else {
            RoutingResult.failure("Could not determine success rate for any provider")
        }
    }
    
    private suspend fun findBestValueProvider(
        providers: List<PaymentProvider>,
        request: PaymentRequest
    ): RoutingResult {
        var bestProvider: PaymentProvider? = null
        var bestScore = BigDecimal.ZERO
        
        for (provider in providers) {
            try {
                val fee = provider.estimateFee(request)
                val time = estimateProcessingTime(provider, request)
                val successRate = getSuccessRate(provider)
                val score = calculateValueScore(fee, time, successRate)
                
                if (score > bestScore) {
                    bestScore = score
                    bestProvider = provider
                }
            } catch (e: Exception) {
                // Skip provider on error
            }
        }
        
        return if (bestProvider != null) {
            RoutingResult.success(bestProvider::class.simpleName ?: "Unknown", bestScore.toString())
        } else {
            RoutingResult.failure("Could not calculate value score for any provider")
        }
    }
    
    private suspend fun executeEqualSplit(
        request: PaymentRequest,
        providers: List<PaymentProvider>
    ): SplitPaymentResult {
        val splitAmount = request.amount.divide(BigDecimal(providers.size.toString()), 8, BigDecimal.ROUND_HALF_UP)
        val results = mutableListOf<SplitPaymentPart>()
        
        for (provider in providers.take(2)) { // Use top 2 providers for equal split
            try {
                val splitRequest = request.copy(amount = splitAmount)
                val fee = provider.estimateFee(splitRequest)
                val time = estimateProcessingTime(provider, splitRequest)
                
                results.add(
                    SplitPaymentPart(
                        providerName = provider::class.simpleName ?: "Unknown",
                        amount = splitAmount,
                        fee = fee,
                        estimatedTime = time
                    )
                )
            } catch (e: Exception) {
                return SplitPaymentResult.failure("Failed to split payment with ${provider::class.simpleName}")
            }
        }
        
        val totalFee = results.sumOf { it.fee }
        val totalTime = results.maxOfOrNull { it.estimatedTime } ?: 0L
        
        return SplitPaymentResult.success(results, totalFee, totalTime)
    }
    
    private suspend fun executeWeightedSplit(
        request: PaymentRequest,
        providers: List<PaymentProvider>
    ): SplitPaymentResult {
        // Weight by success rate
        val totalSuccessRate = providers.sumOf { getSuccessRate(it).toDouble() }
        val results = mutableListOf<SplitPaymentPart>()
        
        for (provider in providers.take(2)) {
            try {
                val weight = getSuccessRate(provider).divide(BigDecimal(totalSuccessRate.toString()), 8, BigDecimal.ROUND_HALF_UP)
                val splitAmount = request.amount.multiply(weight)
                val splitRequest = request.copy(amount = splitAmount)
                val fee = provider.estimateFee(splitRequest)
                val time = estimateProcessingTime(provider, splitRequest)
                
                results.add(
                    SplitPaymentPart(
                        providerName = provider::class.simpleName ?: "Unknown",
                        amount = splitAmount,
                        fee = fee,
                        estimatedTime = time
                    )
                )
            } catch (e: Exception) {
                return SplitPaymentResult.failure("Failed to split payment with ${provider::class.simpleName}")
            }
        }
        
        val totalFee = results.sumOf { it.fee }
        val totalTime = results.maxOfOrNull { it.estimatedTime } ?: 0L
        
        return SplitPaymentResult.success(results, totalFee, totalTime)
    }
    
    private suspend fun executeOptimalSplit(
        request: PaymentRequest,
        providers: List<PaymentProvider>
    ): SplitPaymentResult {
        // Find optimal split based on fees and success rates
        val options = optimizePayment(request, 2)
        
        if (options.size < 2) {
            return SplitPaymentResult.failure("Not enough providers for optimal split")
        }
        
        val results = mutableListOf<SplitPaymentPart>()
        val totalAmount = request.amount
        
        // Distribute based on scores
        val totalScore = options.sumOf { it.score.toDouble() }
        
        for (option in options) {
            val weight = BigDecimal(option.score.toString()).divide(BigDecimal(totalScore.toString()), 8, BigDecimal.ROUND_HALF_UP)
            val splitAmount = totalAmount.multiply(weight)
            
            results.add(
                SplitPaymentPart(
                    providerName = option.provider,
                    amount = splitAmount,
                    fee = option.fee.multiply(weight),
                    estimatedTime = option.estimatedTime
                )
            )
        }
        
        val totalFee = results.sumOf { it.fee }
        val totalTime = results.maxOfOrNull { it.estimatedTime } ?: 0L
        
        return SplitPaymentResult.success(results, totalFee, totalTime)
    }
    
    private suspend fun estimateProcessingTime(provider: PaymentProvider, request: PaymentRequest): Long {
        return when (provider::class.simpleName) {
            "LightningNetworkProvider" -> 1000L // 1 second
            "BitcoinPaymentProvider" -> 60000L // 1 minute
            "EthereumPaymentProvider" -> 120000L // 2 minutes
            "BankTransferProvider" -> 86400000L // 1 day
            else -> 30000L // 30 seconds default
        }
    }
    
    private fun getSuccessRate(provider: PaymentProvider): BigDecimal {
        return when (provider::class.simpleName) {
            "LightningNetworkProvider" -> BigDecimal("0.98")
            "BitcoinPaymentProvider" -> BigDecimal("0.95")
            "EthereumPaymentProvider" -> BigDecimal("0.93")
            "BankTransferProvider" -> BigDecimal("0.99")
            "MoneroPaymentProvider" -> BigDecimal("0.92")
            else -> BigDecimal("0.90")
        }
    }
    
    private fun calculateOptionScore(fee: BigDecimal, time: Long, successRate: BigDecimal): BigDecimal {
        val feeScore = BigDecimal("1.0").subtract(fee.divide(BigDecimal("0.1"), 4, BigDecimal.ROUND_HALF_UP))
        val timeScore = BigDecimal("1.0").subtract(BigDecimal(time).divide(BigDecimal("300000"), 4, BigDecimal.ROUND_HALF_UP))
        return feeScore.add(timeScore).add(successRate).divide(BigDecimal("3"), 4, BigDecimal.ROUND_HALF_UP)
    }
    
    private fun calculateValueScore(fee: BigDecimal, time: Long, successRate: BigDecimal): BigDecimal {
        val normalizedFee = BigDecimal("1.0").subtract(fee.divide(BigDecimal("0.1"), 4, BigDecimal.ROUND_HALF_UP))
        val normalizedTime = BigDecimal("1.0").subtract(BigDecimal(time).divide(BigDecimal("300000"), 4, BigDecimal.ROUND_HALF_UP))
        return normalizedFee.multiply(BigDecimal("0.4")).add(normalizedTime.multiply(BigDecimal("0.3"))).add(successRate.multiply(BigDecimal("0.3")))
    }
}

enum class RoutingCriteria {
    LOWEST_FEE,
    FASTEST,
    HIGHEST_SUCCESS_RATE,
    BEST_VALUE
}

enum class SplitStrategy {
    EQUAL,
    WEIGHTED,
    OPTIMAL
}

data class RoutingResult(
    val success: Boolean,
    val providerName: String?,
    val metric: String?,
    val error: String?
) {
    companion object {
        fun success(providerName: String, metric: String) = RoutingResult(true, providerName, metric, null)
        fun failure(error: String) = RoutingResult(false, null, null, error)
    }
}

data class OptimizedPaymentOption(
    val provider: String,
    val fee: BigDecimal,
    val estimatedTime: Long,
    val successRate: BigDecimal,
    val totalCost: BigDecimal,
    val score: BigDecimal
) {
    fun getFormattedOption(): String {
        return buildString {
            appendLine("PAYMENT OPTION")
            appendLine("==============")
            appendLine()
            appendLine("Provider: $provider")
            appendLine("Fee: $fee")
            appendLine("Estimated Time: ${estimatedTime / 1000}s")
            appendLine("Success Rate: ${successRate.multiply(BigDecimal("100"))}%")
            appendLine("Total Cost: $totalCost")
            appendLine("Score: $score")
        }
    }
}

data class SplitPaymentPart(
    val providerName: String,
    val amount: BigDecimal,
    val fee: BigDecimal,
    val estimatedTime: Long
)

data class SplitPaymentResult(
    val success: Boolean,
    val parts: List<SplitPaymentPart>?,
    val totalFee: BigDecimal?,
    val totalTime: Long?,
    val error: String?
) {
    companion object {
        fun success(parts: List<SplitPaymentPart>, totalFee: BigDecimal, totalTime: Long) = 
            SplitPaymentResult(true, parts, totalFee, totalTime, null)
        fun failure(error: String) = SplitPaymentResult(false, null, null, null, error)
    }
    
    fun getFormattedResult(): String {
        if (!success) return "Split Payment Failed: $error"
        
        return buildString {
            appendLine("SPLIT PAYMENT RESULT")
            appendLine("====================")
            appendLine()
            parts?.forEach { part ->
                appendLine("${part.providerName}: ${part.amount} (Fee: ${part.fee}, Time: ${part.estimatedTime / 1000}s)")
            }
            appendLine()
            appendLine("Total Fee: $totalFee")
            appendLine("Total Time: ${totalTime / 1000}s")
        }
    }
}

data class RoutingAnalytics(
    val totalRoutings: Int,
    val averageFeeSaved: BigDecimal,
    val averageTimeSaved: Long,
    val successRateImprovement: BigDecimal,
    val mostUsedProvider: String,
    val leastUsedProvider: String,
    val averageRoutingTime: Long
)
