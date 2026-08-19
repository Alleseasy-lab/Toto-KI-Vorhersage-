package com.example.ui.generator

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameType
import com.example.model.GeneratorLogic
import com.example.model.TicketResult
import com.example.ui.theme.*

@Composable
fun GeneratorScreen(
    onSaveTicket: (TicketResult) -> Unit,
    savedTicketsCount: Int
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedGame by remember { mutableStateOf(GameType.LOTTO_6_49) }
    var showCustomizer by remember { mutableStateOf(false) }

    // Custom parameters
    var customCount by remember { mutableIntStateOf(6) }
    var customMin by remember { mutableIntStateOf(1) }
    var customMax by remember { mutableIntStateOf(49) }
    var customSuperCount by remember { mutableIntStateOf(1) }
    var customSuperMin by remember { mutableIntStateOf(0) }
    var customSuperMax by remember { mutableIntStateOf(9) }

    var currentTicket by remember {
        mutableStateOf(GeneratorLogic.draw(selectedGame))
    }

    var isDrawing by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun triggerDraw() {
        isDrawing = true
        currentTicket = GeneratorLogic.draw(
            gameType = selectedGame,
            customCount = customCount,
            customMin = customMin,
            customMax = customMax,
            customSuperCount = customSuperCount,
            customSuperMin = customSuperMin,
            customSuperMax = customSuperMax
        )
        isDrawing = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Header Bento Tile ─────────────────────────────────────────────
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = SurfaceBorder.copy(alpha = 0.6f),
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(AccentAmber, GradientOrange)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Casino,
                        contentDescription = "Dice",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = "Zahlen-Generator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Eindeutige Zufallszahlen & Superzahlen nach Wunsch",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // ── Game Type Chips Row ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameType.entries.forEach { game ->
                val isSelected = selectedGame == game
                val chipBackground = if (isSelected) RoyalBlue else SurfaceCard
                val chipBorder = if (isSelected) ElectricBlue else SurfaceBorder
                val textColor = if (isSelected) Color.White else TextSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(chipBackground)
                        .border(1.dp, chipBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            selectedGame = game
                            showCustomizer = (game == GameType.CUSTOM)
                            triggerDraw()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game.title,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // ── Filter info badge ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCard.copy(alpha = 0.6f))
                .border(0.5.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterAlt,
                    contentDescription = "Filter",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                val filterText = when (selectedGame) {
                    GameType.LOTTO_6_49 -> "6 aus [1 bis 49] + 1x Superzahl [0–9]"
                    GameType.EUROJACKPOT -> "5 aus [1 bis 50] + 2x Eurozahlen [1–12]"
                    GameType.TOTO_13 -> "13 Spielausgänge (1 = Heim, 0 = Remis, 2 = Gast)"
                    GameType.CUSTOM -> "$customCount aus [$customMin bis $customMax] + ${customSuperCount}x Super [$customSuperMin–$customSuperMax]"
                }
                Text(
                    text = filterText,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── Customizer Bento Sheet ────────────────────────────────────────
        AnimatedVisibility(
            visible = showCustomizer,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            BentoCard(
                backgroundColor = SurfaceDark,
                borderColor = AccentPurple.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "Generator Anpassen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AccentPurple
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Anzahl Zahlen: $customCount", fontSize = 12.sp, color = TextSecondary)
                        Slider(
                            value = customCount.toFloat(),
                            onValueChange = { customCount = it.toInt() },
                            valueRange = 1f..15f,
                            steps = 13,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentPurple,
                                activeTrackColor = AccentPurple
                            )
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Zahlenbereich: $customMin – $customMax", fontSize = 12.sp, color = TextSecondary)
                        Slider(
                            value = customMax.toFloat(),
                            onValueChange = { customMax = it.toInt() },
                            valueRange = 10f..100f,
                            steps = 17,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Superzahlen: $customSuperCount", fontSize = 12.sp, color = TextSecondary)
                        Slider(
                            value = customSuperCount.toFloat(),
                            onValueChange = { customSuperCount = it.toInt() },
                            valueRange = 0f..3f,
                            steps = 2,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentAmber,
                                activeTrackColor = AccentAmber
                            )
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { triggerDraw() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Text("Übernehmen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── Main Hero Result Bento Card ───────────────────────────────────
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = SurfaceBorder,
            cornerRadius = 24.dp,
            contentPadding = PaddingValues(18.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentTicket.customTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(currentTicket.toShareableText()))
                        Toast.makeText(context, "Zahlen in die Zwischenablage kopiert", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Kopieren",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = SurfaceBorder
            )

            // Numbers Section
            if (selectedGame == GameType.TOTO_13) {
                Text(
                    text = "Spielausgänge (13er Tipp)",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // 13 Toto outputs row/grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentTicket.mainNumbers.forEachIndexed { idx, symbol ->
                        TotoBadge(symbol = symbol, index = idx)
                    }
                }
            } else {
                // Main Lottery Balls
                Text(
                    text = "Hauptzahlen (${currentTicket.mainNumbers.size} aus ${if (selectedGame == GameType.CUSTOM) customMax else selectedGame.maxVal})",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp)
                )

                // Balls layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentTicket.mainNumbers.forEachIndexed { index, number ->
                        LotteryBall(
                            number = number,
                            delayMs = index * 40L
                        )
                    }
                }

                // Super numbers if available
                if (currentTicket.superNumbers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Super",
                            tint = AccentAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedGame == GameType.EUROJACKPOT) "Eurozahlen (1–12)" else "Superzahl (0–9)",
                            fontSize = 11.sp,
                            color = AccentAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        currentTicket.superNumbers.forEachIndexed { index, num ->
                            LotteryBall(
                                number = num,
                                isSuper = true,
                                delayMs = 250L + index * 50L
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Footer
            if (selectedGame != GameType.TOTO_13 && currentTicket.mainNumbers.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BentoStatBadge(label = "Summe", value = currentTicket.sum.toString())
                    BentoStatBadge(label = "G/U", value = "${currentTicket.evenCount}/${currentTicket.oddCount}")
                    BentoStatBadge(label = "Min/Max", value = "${currentTicket.minNumber}–${currentTicket.maxNumber}")
                }
            }
        }

        // ── Action Buttons ────────────────────────────────────────────────
        // Big primary generate button
        Button(
            onClick = { triggerDraw() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp), ambientColor = ElectricBlue, spotColor = ElectricBlue),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Casino,
                contentDescription = "Draw",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Zahlen ziehen",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Secondary Action Row (Speichern & Anpassen)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Speichern Button
            OutlinedButton(
                onClick = {
                    onSaveTicket(currentTicket)
                    Toast.makeText(context, "Tippschein gespeichert!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.BookmarkBorder,
                    contentDescription = "Speichern",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Speichern",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Anpassen Button
            OutlinedButton(
                onClick = {
                    showCustomizer = !showCustomizer
                    if (showCustomizer && selectedGame != GameType.CUSTOM) {
                        selectedGame = GameType.CUSTOM
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Anpassen",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Anpassen",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = if (showCustomizer) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ── Bento Grid Info Cards ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Bento Tile 1: Zufallsverteilung
            BentoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Analytics,
                        contentDescription = "Stats",
                        tint = AccentEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("100% Zufall", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Echte kryptografische Zufallsgenerierung ohne doppelte Zahlen.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
            }

            // Bento Tile 2: Gespeichert Zähler
            BentoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Saved",
                        tint = AccentPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Archiv", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$savedTicketsCount Tippscheine für die nächste Ziehung gesichert.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
