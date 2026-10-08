package com.example.data.model

data class InvestmentPlan(
    val id: Int,
    val name: String,
    val tierCategory: String, // Bronze, Silver, Gold, Platinum, Diamond, Crown
    val amountUsdt: Double,
    val dailyRatePercent: Double = 0.5, // 0.5% daily
    val durationDays: Int = 30,
    val description: String,
    val popular: Boolean = false,
    val colorHex: Long = 0xFF00D09C
) {
    val dailyYieldUsdt: Double get() = amountUsdt * (dailyRatePercent / 100.0)
    val monthlyYieldUsdt: Double get() = dailyYieldUsdt * durationDays
    val totalReturnUsdt: Double get() = amountUsdt + monthlyYieldUsdt
    val monthlyRoiPercent: Double get() = dailyRatePercent * durationDays // 15.0%
}

object InvestmentPlansData {
    val ALL_PLANS: List<InvestmentPlan> = listOf(
        InvestmentPlan(
            id = 1,
            name = "Micro Seed",
            tierCategory = "Bronze",
            amountUsdt = 5.0,
            description = "Entry-level micro vault tier for beginners starting with low capital.",
            colorHex = 0xFF94A3B8
        ),
        InvestmentPlan(
            id = 2,
            name = "Starter Pulse",
            tierCategory = "Bronze",
            amountUsdt = 10.0,
            description = "Lightweight flexible staking plan with continuous daily compounding.",
            colorHex = 0xFF94A3B8
        ),
        InvestmentPlan(
            id = 3,
            name = "Bronze Core",
            tierCategory = "Bronze",
            amountUsdt = 25.0,
            description = "Solid starting tier with stable daily automated yield distribution.",
            colorHex = 0xFFCD7F32
        ),
        InvestmentPlan(
            id = 4,
            name = "Bronze Plus",
            tierCategory = "Bronze",
            amountUsdt = 50.0,
            description = "Double power micro tier with fast yield accrual.",
            colorHex = 0xFFCD7F32
        ),
        InvestmentPlan(
            id = 5,
            name = "Silver Scout",
            tierCategory = "Silver",
            amountUsdt = 75.0,
            description = "Balanced intermediate vault with instant automated harvest capability.",
            colorHex = 0xFFA0AEC0
        ),
        InvestmentPlan(
            id = 6,
            name = "Silver Standard",
            tierCategory = "Silver",
            amountUsdt = 100.0,
            popular = true,
            description = "Most popular benchmark plan offering optimal risk-adjusted staking.",
            colorHex = 0xFFA0AEC0
        ),
        InvestmentPlan(
            id = 7,
            name = "Silver Prime",
            tierCategory = "Silver",
            amountUsdt = 150.0,
            description = "Enhanced silver pool allocation with dedicated liquidity routing.",
            colorHex = 0xFFA0AEC0
        ),
        InvestmentPlan(
            id = 8,
            name = "Gold Explorer",
            tierCategory = "Gold",
            amountUsdt = 200.0,
            description = "Gold class staking tier with priority smart contract settlement.",
            colorHex = 0xFFF59E0B
        ),
        InvestmentPlan(
            id = 9,
            name = "Gold Matrix",
            tierCategory = "Gold",
            amountUsdt = 300.0,
            description = "Advanced yield generation with elevated referral bonus leverage.",
            colorHex = 0xFFF59E0B
        ),
        InvestmentPlan(
            id = 10,
            name = "Gold Executive",
            tierCategory = "Gold",
            amountUsdt = 400.0,
            popular = true,
            description = "Executive investor choice with 2.00 USDT passive return distributed every 24h.",
            colorHex = 0xFFF59E0B
        ),
        InvestmentPlan(
            id = 11,
            name = "Platinum Shield",
            tierCategory = "Platinum",
            amountUsdt = 500.0,
            description = "Institutional-grade vault with dual liquidity collateral backing.",
            colorHex = 0xFF06B6D4
        ),
        InvestmentPlan(
            id = 12,
            name = "Platinum Zenith",
            tierCategory = "Platinum",
            amountUsdt = 650.0,
            description = "High liquidity pool tier generating 3.25 USDT daily yield credits.",
            colorHex = 0xFF06B6D4
        ),
        InvestmentPlan(
            id = 13,
            name = "Diamond Vault",
            tierCategory = "Diamond",
            amountUsdt = 750.0,
            description = "Elite diamond staking allocation with priority automated withdrawal queues.",
            colorHex = 0xFF38BDF8
        ),
        InvestmentPlan(
            id = 14,
            name = "Diamond Apex",
            tierCategory = "Diamond",
            amountUsdt = 900.0,
            description = "Near maximum tier yielding 4.50 USDT daily directly into available wallet.",
            colorHex = 0xFF38BDF8
        ),
        InvestmentPlan(
            id = 15,
            name = "Crown Sovereign",
            tierCategory = "Crown",
            amountUsdt = 1000.0,
            popular = true,
            description = "Flagship maximum allocation plan earning 5.00 USDT daily ($150/month net return).",
            colorHex = 0xFF8B5CF6
        )
    )
}

data class CryptoTicker(
    val symbol: String,
    val pairName: String,
    val priceUsd: Double,
    val change24hPercent: Double,
    val high24h: Double,
    val low24h: Double,
    val volume24hUsd: String,
    val sparkline: List<Float>
)

data class ReferralTierConfig(
    val tierLevel: Int,
    val title: String,
    val commissionPercent: Double,
    val description: String,
    val badgeColorHex: Long
)

val REFERRAL_TIERS = listOf(
    ReferralTierConfig(
        tierLevel = 1,
        title = "Direct Affiliates",
        commissionPercent = 5.0,
        description = "Earn 5% on all subscriptions directly invited with your code.",
        badgeColorHex = 0xFF00D09C
    ),
    ReferralTierConfig(
        tierLevel = 2,
        title = "Sub-Affiliates",
        commissionPercent = 3.0,
        description = "Earn 3% on 2nd-generation subscriptions in your team.",
        badgeColorHex = 0xFF06B6D4
    ),
    ReferralTierConfig(
        tierLevel = 3,
        title = "Network Members",
        commissionPercent = 1.0,
        description = "Earn 1% on 3rd-generation community staking volume.",
        badgeColorHex = 0xFFF59E0B
    )
)
