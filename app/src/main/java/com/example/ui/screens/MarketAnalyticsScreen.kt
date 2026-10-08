package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CryptoTicker
import com.example.ui.components.MiniSparklineChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RubyRed
import com.example.ui.theme.TetherTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun MarketAnalyticsScreen(
    viewModel: MainViewModel,
    language: AppLanguage
) {
    val tickers by viewModel.marketTickers.collectAsState()
    var selectedTimeframeIndex by remember { mutableIntStateOf(1) } // 0: 1H, 1: 24H, 2: 7D, 3: 30D, 4: 1Y
    val timeframes = listOf("1H", "24H", "7D", "30D", "1Y")

    val primaryTicker = tickers.firstOrNull { it.symbol == "BTC" } ?: tickers.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Real-Time Market Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Institutional grade pricing feeds, USDT peg stability & depth",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Hero Interactive Chart Card
        if (primaryTicker != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = primaryTicker.pairName,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format("$%,.2f", primaryTicker.priceUsd),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "+${String.format("%.2f%%", primaryTicker.change24hPercent)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TetherTeal,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }

                            // 24h Volume
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "24h Volume",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = primaryTicker.volume24hUsd,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Canvas Wave Chart
                        InteractiveWaveChart(
                            points = primaryTicker.sparkline,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            lineColor = TetherTeal
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Timeframe Pill Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            timeframes.forEachIndexed { index, tf ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedTimeframeIndex == index) TetherTeal else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedTimeframeIndex = index }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tf,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTimeframeIndex == index) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // USDT Peg Stability & Transparency Audit Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TetherTeal.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = TetherTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "USDT Peg Stability & Proof of Reserves",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "100.02%",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = TetherTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "100% Fully Backed Reserves by US Short-Term Treasuries & Cash Equivalents. Real-time smart contract attestation.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TetherTeal,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "US T-Bills: 85.2%", fontSize = 10.sp, color = TetherTeal)
                        Text(text = "Overnight Repos: 11.4%", fontSize = 10.sp, color = CyanAccent)
                        Text(text = "Cash Reserves: 3.4%", fontSize = 10.sp, color = GoldAccent)
                    }
                }
            }
        }

        // All Crypto Tickers List
        item {
            Text(
                text = "Live Crypto Pairs",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(tickers) { ticker ->
            CryptoTickerListItem(ticker = ticker)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CryptoTickerListItem(ticker: CryptoTicker) {
    val isPositive = ticker.change24hPercent >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ticker.symbol.take(2),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = ticker.pairName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "24h Vol: ${ticker.volume24hUsd}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sparkline Mini
            MiniSparklineChart(
                points = ticker.sparkline,
                modifier = Modifier
                    .width(70.dp)
                    .height(28.dp),
                lineColor = if (isPositive) TetherTeal else RubyRed
            )

            // Price & Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (ticker.priceUsd < 10) String.format("$%.4f", ticker.priceUsd) else String.format("$%,.2f", ticker.priceUsd),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = (if (isPositive) "+" else "") + String.format("%.2f%%", ticker.change24hPercent),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPositive) TetherTeal else RubyRed
                )
            }
        }
    }
}

@Composable
fun InteractiveWaveChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = TetherTeal
) {
    if (points.size < 2) return

    Canvas(modifier = modifier) {
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 1f
        val range = if (max - min == 0f) 1f else max - min

        val stepX = size.width / (points.size - 1)
        val strokePath = Path()
        val fillPath = Path()

        points.forEachIndexed { i, value ->
            val x = i * stepX
            val normalizedY = (value - min) / range
            val y = size.height - (normalizedY * (size.height - 20f)) - 10f

            if (i == 0) {
                strokePath.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (i - 1) * stepX
                val prevNormalizedY = (points[i - 1] - min) / range
                val prevY = size.height - (prevNormalizedY * (size.height - 20f)) - 10f

                val controlX1 = prevX + (x - prevX) / 2
                val controlY1 = prevY
                val controlX2 = prevX + (x - prevX) / 2
                val controlY2 = y

                strokePath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
            }
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        // Gradient Fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                startY = 0f,
                endY = size.height
            )
        )

        // Smooth Line Stroke
        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
