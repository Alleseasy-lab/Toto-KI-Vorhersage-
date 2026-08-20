package com.example.ui.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameType
import com.example.model.TicketResult
import com.example.ui.theme.*

enum class ChartFilterMode(val label: String) {
    TOP("Top Frequenzen"),
    HOT_COLD("Heiß / Kalt"),
    ALL("Alle Zahlen")
}

data class NumberFrequencyItem(
    val number: Int,
    val count: Int,
    val isInLatestDraw: Boolean
)

@Composable
fun FrequencyBarChartCard(
    gameType: GameType,
    currentTicket: TicketResult,
    drawHistory: List<TicketResult>,
    onResetFrequency: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(ChartFilterMode.TOP) }

    // Calculate frequencies based on all past draws in history + initial seed for visual richness
    val frequencyMap = remember(drawHistory, gameType) {
        val map = mutableMapOf<Int, Int>()
        // Initialize range for current game type
        val (minNum, maxNum) = when (gameType) {
            GameType.LOTTO_6_49 -> 1 to 49
            GameType.EUROJACKPOT -> 1 to 50
            GameType.TOTO_13 -> 0 to 2
            GameType.CUSTOM -> 1 to 50
        }
        for (i in minNum..maxNum) {
            map[i] = 0
        }
        // Count from draw history
        drawHistory.filter { it.gameType == gameType }.forEach { ticket ->
            ticket.mainNumbers.forEach { num ->
                map[num] = (map[num] ?: 0) + 1
            }
        }
        map
    }

    val latestNumbersSet = remember(currentTicket) {
        currentTicket.mainNumbers.toSet()
    }

    val totalDrawsCount = drawHistory.count { it.gameType == gameType }

    // Derived chart items based on selected filter
    val chartItems: List<NumberFrequencyItem> = remember(frequencyMap, selectedFilter, latestNumbersSet) {
        when (selectedFilter) {
            ChartFilterMode.TOP -> {
                frequencyMap.entries
                    .filter { it.value > 0 }
                    .sortedByDescending { it.value }
                    .take(10)
                    .map {
                        NumberFrequencyItem(
                            number = it.key,
                            count = it.value,
                            isInLatestDraw = latestNumbersSet.contains(it.key)
                        )
                    }
                    .ifEmpty {
                        frequencyMap.entries
                            .take(8)
                            .map {
                                NumberFrequencyItem(
                                    number = it.key,
                                    count = it.value,
                                    isInLatestDraw = latestNumbersSet.contains(it.key)
                                )
                            }
                    }
            }
            ChartFilterMode.HOT_COLD -> {
                val sorted = frequencyMap.entries.sortedByDescending { it.value }
                val topItems = sorted.take(4)
                val bottomItems = sorted.takeLast(4)
                (topItems + bottomItems).distinctBy { it.key }.map {
                    NumberFrequencyItem(
                        number = it.key,
                        count = it.value,
                        isInLatestDraw = latestNumbersSet.contains(it.key)
                    )
                }
            }
            ChartFilterMode.ALL -> {
                frequencyMap.entries
                    .sortedBy { it.key }
                    .map {
                        NumberFrequencyItem(
                            number = it.key,
                            count = it.value,
                            isInLatestDraw = latestNumbersSet.contains(it.key)
                        )
                    }
            }
        }
    }

    val maxFrequency = (chartItems.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
    val mostFrequentItem = frequencyMap.maxByOrNull { it.value }

    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SurfaceDark,
        borderColor = SurfaceBorder,
        shadowElevation = BentoShadowElevation,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Card Header ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(4.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = ElectricBlue.copy(alpha = 0.3f), spotColor = ElectricBlue.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(BentoInnerRadius))
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricBlue, RoyalBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = "Frequency Chart",
                            tint = PrimaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Zahlen-Häufigkeit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Frequenz-Verteilung im Zeitverlauf",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Reset Action
                if (totalDrawsCount > 0) {
                    IconButton(
                        onClick = onResetFrequency,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reset History",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ── Filter Selector Chips ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChartFilterMode.values().forEach { mode ->
                    val isSelected = selectedFilter == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(BentoBadgeRadius))
                            .background(if (isSelected) ElectricBlue.copy(alpha = 0.18f) else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) ElectricBlue.copy(alpha = 0.6f) else SurfaceBorderLight,
                                RoundedCornerShape(BentoBadgeRadius)
                            )
                            .clickable { selectedFilter = mode }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElectricBlue else TextSecondary
                        )
                    }
                }
            }

            // ── Interactive Frequency Bar Chart Canvas ───────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(BentoInnerRadius))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceBorderLight, RoundedCornerShape(BentoInnerRadius))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                // Background grid guide lines
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(SurfaceBorder.copy(alpha = 0.5f))
                        )
                    }
                }

                // Scrollable or centered bar container
                val chartScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (selectedFilter == ChartFilterMode.ALL) {
                                Modifier.horizontalScroll(chartScrollState)
                            } else {
                                Modifier
                            }
                        ),
                    horizontalArrangement = if (selectedFilter == ChartFilterMode.ALL) {
                        Arrangement.spacedBy(8.dp)
                    } else {
                        Arrangement.SpaceEvenly
                    },
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartItems.forEach { item ->
                        FrequencyBarColumn(
                            item = item,
                            maxFrequency = maxFrequency,
                            gameType = gameType,
                            modifier = if (selectedFilter == ChartFilterMode.ALL) {
                                Modifier.width(32.dp)
                            } else {
                                Modifier.weight(1f, fill = false)
                            }
                        )
                    }
                }
            }

            // ── Frequency Summary Badges ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stat 1: Total Draws
                BentoStatBadge(
                    label = "Ziehungen",
                    value = "$totalDrawsCount",
                    accentColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )

                // Stat 2: Hot Number
                BentoStatBadge(
                    label = "Heißeste Zahl",
                    value = if (mostFrequentItem != null && mostFrequentItem.value > 0) "#${mostFrequentItem.key} (${mostFrequentItem.value}x)" else "–",
                    accentColor = AccentAmber,
                    modifier = Modifier.weight(1.3f)
                )

                // Stat 3: Status
                BentoStatBadge(
                    label = "Aktuell im Schein",
                    value = "${chartItems.count { it.isInLatestDraw }} Treffer",
                    accentColor = AccentPurple,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}

@Composable
private fun FrequencyBarColumn(
    item: NumberFrequencyItem,
    maxFrequency: Int,
    gameType: GameType,
    modifier: Modifier = Modifier
) {
    // Normalization between 0.08f (minimum visible height) and 1.0f
    val fraction = if (maxFrequency > 0 && item.count > 0) {
        (item.count.toFloat() / maxFrequency.toFloat()).coerceIn(0.12f, 1f)
    } else {
        0.08f
    }

    val animatedHeightFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "bar_height_${item.number}"
    )

    val isToto = gameType == GameType.TOTO_13
    val displayLabel = if (isToto) {
        when (item.number) {
            1 -> "1"
            0 -> "0"
            else -> "2"
        }
    } else {
        item.number.toString()
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Frequency count badge on top of bar
        AnimatedVisibility(
            visible = item.count > 0,
            enter = androidx.compose.animation.fadeIn()
        ) {
            Text(
                text = "${item.count}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isInLatestDraw) ElectricBlue else TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // The animated vertical bar
        Box(
            modifier = Modifier
                .width(if (isToto) 24.dp else 16.dp)
                .fillMaxHeight(0.72f * animatedHeightFraction)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                .background(
                    if (item.isInLatestDraw) {
                        Brush.verticalGradient(
                            listOf(ElectricBlue, RoyalBlue)
                        )
                    } else if (item.count > 0) {
                        Brush.verticalGradient(
                            listOf(AccentPurple, AccentPurpleDark)
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(SurfaceBorderLight, SurfaceBorder)
                        )
                    }
                )
                .then(
                    if (item.isInLatestDraw) {
                        Modifier.border(
                            1.dp,
                            ElectricBlue,
                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Number Label underneath the bar
        Box(
            modifier = Modifier
                .size(if (isToto) 22.dp else 18.dp)
                .clip(CircleShape)
                .background(
                    if (item.isInLatestDraw) {
                        ElectricBlue.copy(alpha = 0.25f)
                    } else {
                        Color.Transparent
                    }
                )
                .then(
                    if (item.isInLatestDraw) {
                        Modifier.border(1.dp, ElectricBlue.copy(alpha = 0.8f), CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayLabel,
                fontSize = 10.sp,
                fontWeight = if (item.isInLatestDraw) FontWeight.Bold else FontWeight.Medium,
                color = if (item.isInLatestDraw) ElectricBlue else TextSecondary
            )
        }
    }
}
