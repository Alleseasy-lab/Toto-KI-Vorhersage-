package com.example.ui.generator

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import com.example.model.DrawPackage
import com.example.model.GameType
import com.example.model.GeneratorLogic
import com.example.model.PaymentProvider
import com.example.model.TicketResult
import com.example.ui.payment.PaymentBottomSheet
import com.example.ui.theme.*

@Composable
fun GeneratorScreen(
    onSaveTicket: (TicketResult) -> Unit,
    savedTicketsCount: Int,
    availableDraws: Int,
    onUseDraw: () -> Boolean,
    onTopUpSuccess: (DrawPackage, PaymentProvider, String?) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedGame by remember { mutableStateOf(GameType.LOTTO_6_49) }
    var showCustomizer by remember { mutableStateOf(false) }
    var showPaymentModal by remember { mutableStateOf(false) }

    // Custom parameters
    var customCount by remember { mutableIntStateOf(6) }
    var customMin by remember { mutableIntStateOf(1) }
    var customMax by remember { mutableIntStateOf(49) }
    var customSuperCount by remember { mutableIntStateOf(1) }
    var customSuperMin by remember { mutableIntStateOf(0) }
    var customSuperMax by remember { mutableIntStateOf(9) }

    var currentTicket by remember {
        mutableStateOf(
            GeneratorLogic.draw(GameType.LOTTO_6_49)
        )
    }

    // History of generated tickets for frequency distribution analysis
    var drawHistory by remember {
        mutableStateOf(
            listOf(
                currentTicket,
                GeneratorLogic.draw(GameType.LOTTO_6_49),
                GeneratorLogic.draw(GameType.LOTTO_6_49),
                GeneratorLogic.draw(GameType.EUROJACKPOT),
                GeneratorLogic.draw(GameType.LOTTO_6_49)
            )
        )
    }
    var isDrawing by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun triggerDraw() {
        if (availableDraws > 0) {
            val success = onUseDraw()
            if (success) {
                isDrawing = true
                val newTicket = GeneratorLogic.draw(
                    gameType = selectedGame,
                    customCount = customCount,
                    customMin = customMin,
                    customMax = customMax,
                    customSuperCount = customSuperCount,
                    customSuperMin = customSuperMin,
                    customSuperMax = customSuperMax
                )
                currentTicket = newTicket
                drawHistory = listOf(newTicket) + drawHistory
                isDrawing = false
                Toast.makeText(context, "1 Ziehung eingelöst (Verbleibend: ${availableDraws - 1})", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Eine Ziehung kostet 1,35 €. Bitte wähle PayPal oder Mollie zum Freischalten.", Toast.LENGTH_LONG).show()
            showPaymentModal = true
        }
    }

    if (showPaymentModal) {
        PaymentBottomSheet(
            onDismissRequest = { showPaymentModal = false },
            onPaymentSuccess = { pkg, provider, method ->
                onTopUpSuccess(pkg, provider, method)
                showPaymentModal = false
                // Auto execute first draw after purchase
                val newTicket = GeneratorLogic.draw(
                    gameType = selectedGame,
                    customCount = customCount,
                    customMin = customMin,
                    customMax = customMax,
                    customSuperCount = customSuperCount,
                    customSuperMin = customSuperMin,
                    customSuperMax = customSuperMax
                )
                currentTicket = newTicket
                drawHistory = listOf(newTicket) + drawHistory
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Header Bento Card ─────────────────────────────────────────────
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = SurfaceBorder,
            shadowElevation = BentoShadowElevation,
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = AccentAmber.copy(alpha = 0.3f), spotColor = AccentAmber.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(BentoInnerRadius))
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
                            text = "1 Ziehung = 1,35 € (PayPal / Mollie)",
                            fontSize = 12.sp,
                            color = AccentAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Balance Pill Button with subtle Bento shadow
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = if (availableDraws > 0) AccentEmerald.copy(alpha = 0.25f) else HotRed.copy(alpha = 0.25f), spotColor = if (availableDraws > 0) AccentEmerald.copy(alpha = 0.25f) else HotRed.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(BentoInnerRadius))
                        .background(if (availableDraws > 0) AccentEmerald.copy(alpha = 0.18f) else DeepRed.copy(alpha = 0.25f))
                        .border(1.dp, if (availableDraws > 0) AccentEmerald.copy(alpha = 0.7f) else HotRed.copy(alpha = 0.7f), RoundedCornerShape(BentoInnerRadius))
                        .clickable { showPaymentModal = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ConfirmationNumber,
                            contentDescription = "Ticket",
                            tint = if (availableDraws > 0) AccentEmerald else HotRed,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "$availableDraws Ziehung${if (availableDraws != 1) "en" else ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (availableDraws > 0) AccentEmerald else Color(0xFFFF9999)
                        )
                    }
                }
            }
        }

        // ── Tarife & Guthaben Bento Banner ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(BentoShadowElevation, RoundedCornerShape(BentoCardRadius), ambientColor = Color(0xFF003087).copy(alpha = 0.35f), spotColor = Color(0xFF003087).copy(alpha = 0.35f))
                .clip(RoundedCornerShape(BentoCardRadius))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF002255), Color(0xFF1F1A3A))
                    )
                )
                .border(1.dp, Color(0xFF0079C1).copy(alpha = 0.4f), RoundedCornerShape(BentoCardRadius))
                .clickable { showPaymentModal = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AccentAmber.copy(alpha = 0.2f))
                            .border(1.dp, AccentAmber.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = AccentAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Tarif: 1,35 € pro Ziehung",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Sichere Bezahlung via PayPal & Mollie",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Button(
                    onClick = { showPaymentModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(BentoInnerRadius),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .shadow(4.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = ElectricBlue.copy(alpha = 0.4f), spotColor = ElectricBlue.copy(alpha = 0.4f))
                ) {
                    Text("+ Aufladen", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                }
            }
        }

        // ── Game Selector Bento Scroll / Grid ─────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameType.entries.forEach { game ->
                val isSelected = selectedGame == game
                val chipBg = if (isSelected) SurfaceCardElevated else SurfaceCard
                val chipBorder = if (isSelected) ElectricBlue else SurfaceBorder
                val textColor = if (isSelected) ElectricBlue else TextSecondary

                Box(
                    modifier = Modifier
                        .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else BentoShadowColor, spotColor = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else BentoShadowColor)
                        .clip(RoundedCornerShape(BentoInnerRadius))
                        .background(chipBg)
                        .border(1.2.dp, chipBorder, RoundedCornerShape(BentoInnerRadius))
                        .clickable {
                            selectedGame = game
                            showCustomizer = (game == GameType.CUSTOM)
                        }
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Active",
                                tint = ElectricBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = game.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }

        // ── Customizer Bento Panel (Expanded for CUSTOM or when opened) ──
        AnimatedVisibility(
            visible = showCustomizer || selectedGame == GameType.CUSTOM,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BentoCard(
                backgroundColor = SurfaceCardElevated,
                borderColor = AccentPurple.copy(alpha = 0.6f),
                cornerRadius = BentoCardRadius,
                shadowElevation = BentoShadowElevation,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Customize",
                            tint = AccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Individuelle Parameter konfigurieren",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                    IconButton(
                        onClick = { showCustomizer = false },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Number of picks slider
                Text(
                    text = "Anzahl Hauptzahlen: $customCount",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = customCount.toFloat(),
                    onValueChange = { customCount = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 13,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentPurple,
                        activeTrackColor = AccentPurple,
                        inactiveTrackColor = SurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Range slider representation
                Text(
                    text = "Zahlenbereich: $customMin bis $customMax",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Min: $customMin", fontSize = 11.sp, color = TextMuted)
                        Slider(
                            value = customMin.toFloat(),
                            onValueChange = {
                                val v = it.toInt()
                                if (v < customMax) customMin = v
                            },
                            valueRange = 1f..50f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = SurfaceBorder
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Max: $customMax", fontSize = 11.sp, color = TextMuted)
                        Slider(
                            value = customMax.toFloat(),
                            onValueChange = {
                                val v = it.toInt()
                                if (v > customMin) customMax = v
                            },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = SurfaceBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Super numbers count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Superzahlen: $customSuperCount", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Slider(
                            value = customSuperCount.toFloat(),
                            onValueChange = { customSuperCount = it.toInt() },
                            valueRange = 0f..4f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentAmber,
                                activeTrackColor = AccentAmber,
                                inactiveTrackColor = SurfaceBorder
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
                            shape = RoundedCornerShape(BentoInnerRadius),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .shadow(4.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = AccentPurple.copy(alpha = 0.3f), spotColor = AccentPurple.copy(alpha = 0.3f))
                        ) {
                            Text("Übernehmen", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // ── Main Hero Result Bento Card ───────────────────────────────────
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = SurfaceBorder,
            cornerRadius = BentoCardRadiusLarge,
            shadowElevation = BentoShadowElevation + 2.dp,
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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Copy icon button with Bento container
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(2.dp, RoundedCornerShape(BentoBadgeRadius), ambientColor = BentoShadowColor, spotColor = BentoShadowColor)
                            .clip(RoundedCornerShape(BentoBadgeRadius))
                            .background(SurfaceCardElevated)
                            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(BentoBadgeRadius))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(currentTicket.toShareableText()))
                                Toast.makeText(context, "Kombination kopiert!", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Customize Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(2.dp, RoundedCornerShape(BentoBadgeRadius), ambientColor = BentoShadowColor, spotColor = BentoShadowColor)
                            .clip(RoundedCornerShape(BentoBadgeRadius))
                            .background(SurfaceCardElevated)
                            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(BentoBadgeRadius))
                            .clickable { showCustomizer = !showCustomizer },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Customize",
                            tint = if (showCustomizer) ElectricBlue else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Numbers Container with smooth fade-in & scale-up animation
            AnimatedContent(
                targetState = currentTicket,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing)) +
                     scaleIn(initialScale = 0.88f, animationSpec = tween(320, easing = FastOutSlowInEasing)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(150, easing = FastOutSlowInEasing)) +
                            scaleOut(targetScale = 0.94f, animationSpec = tween(150, easing = FastOutSlowInEasing))
                        )
                },
                label = "bento_numbers_transition"
            ) { targetTicket ->
                if (targetTicket.gameType == GameType.TOTO_13) {
                    // Toto Grid representation
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "13er Tippreihe (1 = Heimsieg, 0 = Remis, 2 = Auswärtssieg):",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            targetTicket.mainNumbers.forEachIndexed { idx, symbol ->
                                TotoBadge(
                                    symbol = symbol,
                                    index = idx,
                                    delayMs = idx * 35L,
                                    generationKey = "${targetTicket.id}_$idx"
                                )
                            }
                        }
                    }
                } else {
                    // Ball Representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        targetTicket.mainNumbers.forEachIndexed { index, number ->
                            LotteryBall(
                                number = number,
                                delayMs = index * 50L,
                                generationKey = "${targetTicket.id}_$index"
                            )
                        }

                        if (targetTicket.superNumbers.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AccentAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Super",
                                    tint = AccentAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            targetTicket.superNumbers.forEachIndexed { index, number ->
                                LotteryBall(
                                    number = number,
                                    isSuper = true,
                                    delayMs = (targetTicket.mainNumbers.size + index) * 50L,
                                    generationKey = "${targetTicket.id}_super_$index"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bento Quick Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedGame != GameType.TOTO_13) {
                    BentoStatBadge(
                        label = "Summe",
                        value = "${currentTicket.sum}",
                        accentColor = ElectricBlue
                    )
                    BentoStatBadge(
                        label = "G / U",
                        value = "${currentTicket.evenCount} / ${currentTicket.oddCount}",
                        accentColor = AccentPurple
                    )
                    if (currentTicket.mainNumbers.isNotEmpty()) {
                        BentoStatBadge(
                            label = "Spanne",
                            value = "${currentTicket.mainNumbers.minOrNull()} - ${currentTicket.mainNumbers.maxOrNull()}",
                            accentColor = AccentAmber
                        )
                    }
                } else {
                    val count1 = currentTicket.mainNumbers.count { it == 1 }
                    val count0 = currentTicket.mainNumbers.count { it == 0 }
                    val count2 = currentTicket.mainNumbers.count { it == 2 }
                    BentoStatBadge(label = "Tipps (1)", value = "$count1", accentColor = ElectricBlue)
                    BentoStatBadge(label = "Tipps (0)", value = "$count0", accentColor = AccentAmber)
                    BentoStatBadge(label = "Tipps (2)", value = "$count2", accentColor = AccentPink)
                }
            }
        }

        // ── Primary Action Button (Draw / Pay) ────────────────────────────
        Button(
            onClick = { triggerDraw() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(
                    BentoShadowElevation + 4.dp,
                    RoundedCornerShape(BentoCardRadius),
                    ambientColor = if (availableDraws > 0) ElectricBlue.copy(alpha = 0.5f) else AccentAmber.copy(alpha = 0.5f),
                    spotColor = if (availableDraws > 0) ElectricBlue.copy(alpha = 0.5f) else AccentAmber.copy(alpha = 0.5f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (availableDraws > 0) ElectricBlue else AccentAmber
            ),
            shape = RoundedCornerShape(BentoCardRadius)
        ) {
            Icon(
                imageVector = if (availableDraws > 0) Icons.Filled.Casino else Icons.Filled.ShoppingCart,
                contentDescription = "Draw",
                tint = if (availableDraws > 0) PrimaryDark else PrimaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (availableDraws > 0) "Zahlen ziehen (1 Ziehung)" else "Zahlen ziehen (1,35 € mit PayPal / Mollie)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )
        }

        // ── Secondary Action Row (Save & Customize) ───────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Save Button
            OutlinedButton(
                onClick = {
                    onSaveTicket(currentTicket)
                    Toast.makeText(context, "Schein gespeichert!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .shadow(3.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = BentoShadowColor, spotColor = BentoShadowColor),
                shape = RoundedCornerShape(BentoInnerRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.BookmarkBorder,
                    contentDescription = "Save",
                    tint = AccentPurple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Speichern", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            // Customize trigger Button
            OutlinedButton(
                onClick = { showCustomizer = !showCustomizer },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .shadow(3.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = BentoShadowColor, spotColor = BentoShadowColor),
                shape = RoundedCornerShape(BentoInnerRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Config",
                    tint = ElectricBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Anpassen", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // ── 2x2 Bento Micro Grid ──────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tile 1: Echter Zufall
            BentoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                shadowElevation = BentoShadowElevation,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(BentoBadgeRadius))
                            .background(AccentEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "RNG",
                            tint = AccentEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "100% Zufall",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Kryptografisch sichere Ziehung ohne Wiederholungen.",
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 13.sp
                )
            }

            // Tile 2: Gespeicherte Tickets
            BentoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                shadowElevation = BentoShadowElevation,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(BentoBadgeRadius))
                            .background(AccentPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Saved",
                            tint = AccentPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Archiv",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$savedTicketsCount Schein${if (savedTicketsCount != 1) "e" else ""} gesichert",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                )
            }
        }

        // ── Frequency Distribution Bar Chart Bento Card ───────────────────
        FrequencyBarChartCard(
            gameType = selectedGame,
            currentTicket = currentTicket,
            drawHistory = drawHistory,
            onResetFrequency = {
                drawHistory = listOf(currentTicket)
                Toast.makeText(context, "Frequenz-Historie zurückgesetzt", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
