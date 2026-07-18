package com.freetime.sdk

import com.freetime.sdk.providers.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CryptoProvidersTest {

    @Test
    fun testProviderNames() {
        assertEquals("Dogecoin (DOGE)", DogecoinProvider("addr").name)
        assertEquals("Bitcoin Cash (BCH)", BitcoinCashProvider("addr").name)
        assertEquals("Dash (DASH)", DashProvider("addr").name)
        assertEquals("Zcash (ZEC)", ZcashProvider("addr").name)
        assertEquals("Ripple (XRP)", XRPProvider("addr").name)
        assertEquals("Cardano (ADA)", CardanoProvider("addr").name)
        assertEquals("Polkadot (DOT)", PolkadotProvider("addr").name)
        assertEquals("BNB", BNBProvider("addr").name)
        assertEquals("Nano (XNO)", NanoProvider("addr").name)
        assertEquals("Stellar (XLM)", StellarProvider("addr").name)
        assertEquals("TRON (TRX)", TronProvider("addr").name)
        assertEquals("Algorand (ALGO)", AlgorandProvider("addr").name)
        assertEquals("Cosmos Hub (ATOM)", CosmosProvider("addr").name)
        assertEquals("Tezos (XTZ)", TezosProvider("addr").name)
        assertEquals("Avalanche (AVAX)", AvalancheProvider("addr").name)
        assertEquals("Polygon (MATIC)", PolygonProvider("addr").name)
        assertEquals("Fantom (FTM)", FantomProvider("addr").name)
        assertEquals("Near Protocol (NEAR)", NearProvider("addr").name)
        assertEquals("Optimism (OP)", OptimismProvider("addr").name)
        assertEquals("Arbitrum One (ARB)", ArbitrumProvider("addr").name)
        assertEquals("Base", BaseProvider("addr").name)
        assertEquals("Celo", CeloProvider("addr").name)
        assertEquals("MultiversX (EGLD)", MultiversXProvider("addr").name)
        assertEquals("Hedera (HBAR)", HederaProvider("addr").name)
        assertEquals("Aptos (APT)", AptosProvider("addr").name)
        assertEquals("Sui (SUI)", SuiProvider("addr").name)
        assertEquals("VeChain (VET)", VeChainProvider("addr").name)
    }

    @Test
    fun testRegisterDefaultCryptoProviders() {
        val config = DeveloperConfig("dev_123")
        val sdk = FreetimePay(config)
        val addresses = mapOf(
            "BTC" to "btc_addr",
            "ETH" to "eth_addr",
            "DOGE" to "doge_addr",
            "BCH" to "bch_addr",
            "DASH" to "dash_addr",
            "ZEC" to "zec_addr",
            "XRP" to "xrp_addr",
            "ADA" to "ada_addr",
            "DOT" to "dot_addr",
            "BNB" to "bnb_addr",
            "XNO" to "xno_addr",
            "XMR" to "xmr_addr",
            "LTC" to "ltc_addr",
            "SOL" to "sol_addr",
            "XLM" to "xlm_addr",
            "TRX" to "trx_addr",
            "ALGO" to "algo_addr",
            "ATOM" to "atom_addr",
            "XTZ" to "xtz_addr",
            "AVAX" to "avax_addr",
            "MATIC" to "matic_addr",
            "FTM" to "ftm_addr",
            "NEAR" to "near_addr",
            "OP" to "op_addr",
            "ARB" to "arb_addr",
            "BASE" to "base_addr",
            "CELO" to "celo_addr",
            "EGLD" to "egld_addr",
            "HBAR" to "hbar_addr",
            "APT" to "apt_addr",
            "SUI" to "sui_addr",
            "VET" to "vet_addr"
        )
        
        sdk.registerDefaultCryptoProviders(addresses)
        
        val providers = sdk.getAvailableProviders()
        assertEquals(32, providers.size)
        
        assertEquals("Bitcoin (BTC)", providers[0].name)
        assertEquals("Ethereum (ETH)", providers[1].name)
        assertEquals("Dogecoin (DOGE)", providers[2].name)
    }
}
