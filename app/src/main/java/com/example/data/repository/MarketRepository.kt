package com.example.data.repository

import com.example.data.model.CryptoTicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MarketRepository {

    private var initialTickers = listOf(
        CryptoTicker(
            symbol = "USDT",
            pairName = "USDT / USD (Peg)",
            priceUsd = 1.0002,
            change24hPercent = 0.02,
            high24h = 1.0008,
            low24h = 0.9996,
            volume24hUsd = "$78.4 Billion",
            sparkline = listOf(1.0001f, 1.0003f, 1.0000f, 0.9998f, 1.0002f, 1.0004f, 1.0002f)
        ),
        CryptoTicker(
            symbol = "BTC",
            pairName = "BTC / USDT",
            priceUsd = 91420.50,
            change24hPercent = 3.42,
            high24h = 92350.00,
            low24h = 88100.00,
            volume24hUsd = "$41.2 Billion",
            sparkline = listOf(88100f, 89200f, 88900f, 90400f, 91100f, 90800f, 91420f)
        ),
        CryptoTicker(
            symbol = "ETH",
            pairName = "ETH / USDT",
            priceUsd = 3450.80,
            change24hPercent = 2.15,
            high24h = 3510.00,
            low24h = 3380.00,
            volume24hUsd = "$18.6 Billion",
            sparkline = listOf(3380f, 3410f, 3395f, 3440f, 3430f, 3465f, 3450f)
        ),
        CryptoTicker(
            symbol = "SOL",
            pairName = "SOL / USDT",
            priceUsd = 214.60,
            change24hPercent = 5.80,
            high24h = 219.00,
            low24h = 201.20,
            volume24hUsd = "$8.9 Billion",
            sparkline = listOf(201f, 205f, 208f, 212f, 210f, 216f, 214f)
        ),
        CryptoTicker(
            symbol = "BNB",
            pairName = "BNB / USDT",
            priceUsd = 645.20,
            change24hPercent = -0.45,
            high24h = 655.00,
            low24h = 638.00,
            volume24hUsd = "$3.1 Billion",
            sparkline = listOf(652f, 650f, 648f, 642f, 640f, 647f, 645f)
        )
    )

    fun getLiveMarketTickers(): Flow<List<CryptoTicker>> = flow {
        var current = initialTickers
        while (true) {
            emit(current)
            delay(4000)
            // Add subtle realistic market fluctuations
            current = current.map { ticker ->
                val delta = (Random.nextDouble(-0.15, 0.18))
                val newPrice = if (ticker.symbol == "USDT") {
                    1.0 + (Random.nextDouble(-0.0006, 0.0006))
                } else {
                    ticker.priceUsd * (1 + delta / 100.0)
                }
                val newPoints = ticker.sparkline.drop(1) + newPrice.toFloat()
                ticker.copy(
                    priceUsd = newPrice,
                    change24hPercent = ticker.change24hPercent + (delta * 0.1),
                    sparkline = newPoints
                )
            }
        }
    }
}
