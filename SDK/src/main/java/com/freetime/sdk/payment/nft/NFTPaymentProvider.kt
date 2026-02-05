package com.freetime.sdk.payment.nft

import com.freetime.sdk.payment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

class NFTPaymentProvider(
    private val marketplaceUrl: String = "https://nft-marketplace.com/api",
    private val blockchain: String = "ethereum"
) : PaymentProvider {
    
    override val supportedMethods = listOf(PaymentMethod.CRYPTO)
    override val supportedCurrencies = listOf("ETH", "WETH", "USDC", "DAI")
    
    private val nftStore = mutableMapOf<String, NFT>()
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
            val purchaseId = executeNFTPurchase(request)
            PaymentResult.success(
                purchaseId,
                "NFT purchase completed. Purchase ID: $purchaseId"
            )
        } catch (e: Exception) {
            PaymentResult.failure(
                PaymentError.TRANSACTION_FAILED,
                "Failed to purchase NFT: ${e.message}"
            )
        }
    }
    
    override suspend fun validatePayment(request: PaymentRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (request.amount < BigDecimal("0.001")) {
            errors.add("Minimum NFT purchase amount is 0.001")
        }
        
        if (!supportedCurrencies.contains(request.currency)) {
            errors.add("Currency ${request.currency} is not supported for NFT purchases")
        }
        
        if (request.metadata["nft_id"].isNullOrBlank()) {
            errors.add("NFT ID is required for NFT purchases")
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
            "NFT purchases cannot be refunded automatically"
        )
    }
    
    override fun getTransactionHistory(address: String?): Flow<Transaction> {
        return flowOf(*transactionStore.values.toTypedArray())
    }
    
    override suspend fun getBalance(address: String): BigDecimal {
        return try {
            when {
                address.startsWith("0x") -> BigDecimal("2.5")
                else -> BigDecimal.ZERO
            }
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    override suspend fun estimateFee(request: PaymentRequest): BigDecimal {
        val baseFee = BigDecimal("0.002") // ETH gas fee
        val marketplaceFee = request.amount.multiply(BigDecimal("0.025")) // 2.5% marketplace fee
        val royaltyFee = request.amount.multiply(BigDecimal("0.10")) // 10% royalty
        return baseFee.add(marketplaceFee).add(royaltyFee)
    }
    
    suspend fun createNFT(
        name: String,
        description: String,
        imageUrl: String,
        creatorAddress: String,
        royaltyPercentage: BigDecimal = BigDecimal("0.10")
    ): NFT {
        val nftId = generateNFTId()
        val contractAddress = generateContractAddress()
        
        val nft = NFT(
            id = nftId,
            name = name,
            description = description,
            imageUrl = imageUrl,
            contractAddress = contractAddress,
            creatorAddress = creatorAddress,
            ownerAddress = creatorAddress,
            price = BigDecimal.ZERO,
            currency = "ETH",
            royaltyPercentage = royaltyPercentage,
            isListed = false,
            createdAt = System.currentTimeMillis(),
            metadata = mapOf(
                "blockchain" to blockchain,
                "standard" to "ERC-721",
                "creator_royalty" to royaltyPercentage.toString()
            )
        )
        
        nftStore[nftId] = nft
        return nft
    }
    
    suspend fun listNFT(
        nftId: String,
        price: BigDecimal,
        currency: String
    ): NFT {
        val nft = nftStore[nftId]
            ?: throw Exception("NFT not found")
        
        val updatedNft = nft.copy(
            price = price,
            currency = currency,
            isListed = true,
            listedAt = System.currentTimeMillis()
        )
        
        nftStore[nftId] = updatedNft
        return updatedNft
    }
    
    suspend fun getNFTDetails(nftId: String): NFT? {
        return nftStore[nftId]
    }
    
    suspend fun getNFTCollection(collectionId: String): NFTCollection {
        return NFTCollection(
            id = collectionId,
            name = "Freetime SDK Collection",
            description = "Official NFT collection for Freetime SDK",
            creatorAddress = "0x1234567890abcdef1234567890abcdef12345678",
            totalSupply = 1000,
            mintedSupply = 250,
            floorPrice = BigDecimal("0.1"),
            currency = "ETH",
            createdAt = System.currentTimeMillis()
        )
    }
    
    suspend fun searchNFTs(query: String, filters: NFTSearchFilters = NFTSearchFilters()): List<NFT> {
        return nftStore.values.filter { nft ->
            nft.name.contains(query, ignoreCase = true) ||
            nft.description.contains(query, ignoreCase = true)
        }.filter { nft ->
            if (filters.minPrice != null) nft.price >= filters.minPrice!! else true
        }.filter { nft ->
            if (filters.maxPrice != null) nft.price <= filters.maxPrice!! else true
        }.filter { nft ->
            if (filters.isListed != null) nft.isListed == filters.isListed!! else true
        }
    }
    
    suspend fun getNFTMarketplaceStats(): NFTMarketplaceStats {
        return NFTMarketplaceStats(
            totalVolume = BigDecimal("1250.5"),
            totalSales = 1500,
            activeListings = 500,
            floorPrice = BigDecimal("0.05"),
            averagePrice = BigDecimal("0.833"),
            topCollections = listOf(
                "Freetime SDK Collection",
                "Open Source Art",
                "Crypto Punks FOSS"
            )
        )
    }
    
    private suspend fun executeNFTPurchase(request: PaymentRequest): String {
        Thread.sleep(3000) // NFT transactions take longer
        
        val purchaseId = "nft_purchase_${System.currentTimeMillis()}"
        val nftId = request.metadata["nft_id"] ?: "unknown"
        
        val transaction = Transaction(
            id = purchaseId,
            amount = request.amount,
            currency = request.currency,
            from = request.senderAddress ?: "buyer",
            to = request.recipientAddress ?: "seller",
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis(),
            fee = estimateFee(request),
            metadata = mapOf(
                "payment_method" to "nft_purchase",
                "nft_id" to nftId,
                "marketplace" to "freetime_nft",
                "blockchain" to blockchain
            )
        )
        
        transactionStore[purchaseId] = transaction
        
        // Update NFT ownership
        val nft = nftStore[nftId]
        if (nft != null) {
            val updatedNft = nft.copy(
                ownerAddress = request.senderAddress ?: "buyer",
                isListed = false,
                lastSoldAt = System.currentTimeMillis(),
                lastSoldPrice = request.amount
            )
            nftStore[nftId] = updatedNft
        }
        
        return purchaseId
    }
    
    private fun generateNFTId(): String {
        return "nft_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    private fun generateContractAddress(): String {
        return "0x${(1..40).map { "0123456789abcdef".random() }.joinToString("")}"
    }
}

data class NFT(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val contractAddress: String,
    val creatorAddress: String,
    val ownerAddress: String,
    val price: BigDecimal,
    val currency: String,
    val royaltyPercentage: BigDecimal,
    val isListed: Boolean,
    val createdAt: Long,
    val listedAt: Long? = null,
    val lastSoldAt: Long? = null,
    val lastSoldPrice: BigDecimal? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    fun getFormattedNFT(): String {
        return buildString {
            appendLine("NFT DETAILS")
            appendLine("===========")
            appendLine()
            appendLine("ID: $id")
            appendLine("Name: $name")
            appendLine("Description: $description")
            appendLine("Contract: $contractAddress")
            appendLine("Creator: ${creatorAddress.take(20)}...")
            appendLine("Owner: ${ownerAddress.take(20)}...")
            appendLine("Price: $price $currency")
            appendLine("Royalty: ${royaltyPercentage.multiply(BigDecimal("100"))}%")
            appendLine("Status: ${if (isListed) "Listed" else "Not Listed"}")
            appendLine("Created: ${java.util.Date(createdAt)}")
            if (lastSoldAt != null) {
                appendLine("Last Sold: ${java.util.Date(lastSoldAt)} for $lastSoldPrice $currency")
            }
        }
    }
    
    fun calculateRoyalty(salePrice: BigDecimal): BigDecimal {
        return salePrice.multiply(royaltyPercentage)
    }
}

data class NFTCollection(
    val id: String,
    val name: String,
    val description: String,
    val creatorAddress: String,
    val totalSupply: Int,
    val mintedSupply: Int,
    val floorPrice: BigDecimal,
    val currency: String,
    val createdAt: Long
) {
    fun getMintProgress(): BigDecimal {
        return BigDecimal(mintedSupply).divide(BigDecimal(totalSupply), 4, BigDecimal.ROUND_HALF_UP)
    }
    
    fun isSoldOut(): Boolean {
        return mintedSupply >= totalSupply
    }
}

data class NFTSearchFilters(
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val isListed: Boolean? = null,
    val creatorAddress: String? = null,
    val ownerAddress: String? = null
)

data class NFTMarketplaceStats(
    val totalVolume: BigDecimal,
    val totalSales: Int,
    val activeListings: Int,
    val floorPrice: BigDecimal,
    val averagePrice: BigDecimal,
    val topCollections: List<String>
)
