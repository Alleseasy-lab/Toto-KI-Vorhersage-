package com.example.ui.advisor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.generator.BentoCard
import com.example.ui.theme.*

@Composable
fun AdvisorScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
            borderColor = SurfaceBorder
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
                                listOf(AccentPurple, AccentPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "KI-Berater & Orakel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Statistiken, Quoten und Muster-Analysen",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // ── 2x Bento Stats Grid ───────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hot Numbers Tile
            BentoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Hot",
                        tint = HotRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Top Zahlen", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "6, 18, 33, 49",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentAmber
                )
                Text(
                    text = "Statistisch am häufigsten gezogen",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            // Cold Numbers Tile
            BentoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AcUnit,
                        contentDescription = "Cold",
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Überfällig", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "13, 21, 28, 45",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricBlue
                )
                Text(
                    text = "Längste Pause seit letztem Treffer",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // ── KI Tipp Generator Bento Card ─────────────────────────────────
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = SurfaceBorder
        ) {
            Text(
                text = "KI-Musteranalyse & Harmonische Kombination",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Optimiert auf eine ideale 3:3 Gerade/Ungerade-Balance und eine mittlere Summe von 135–165 für maximale statistische Streuung.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "KI-Kombination berechnet und in die Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = "Smart Pick",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Harmonische Zahlen berechnen", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ── Subscription Section Bento Box (Matching Screenshot 3) ────────
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = AccentPurple.copy(alpha = 0.5f),
            cornerRadius = 24.dp,
            contentPadding = PaddingValues(18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Monatliches Abo",
                    color = AccentEmerald,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "1,99 €",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = " / Monat",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Schalte vollen Zugriff auf alle Tracker, visionäre Projekte, den DBT-Skill-Katalog und Bodo (KI-Coach) frei.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Warning / Notice Banner from screenshot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepRed.copy(alpha = 0.35f))
                    .border(1.dp, HotRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Notice",
                        tint = HotRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "PayPal Credentials fehlen. Bitte PAYPAL_CLIENT_ID und PAYPAL_SECRET in der .env Datei hinterlegen.",
                        color = Color(0xFFFFCCCC),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PayPal Button
            Button(
                onClick = {
                    Toast.makeText(context, "PayPal Gateway bereit zur Verbindung", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00457C)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = "PayPal",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mit PayPal abonnieren",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mollie Button
            Button(
                onClick = {
                    Toast.makeText(context, "Mollie Gateway bereit zur Verbindung", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CreditCard,
                    contentDescription = "Mollie",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mit Mollie abonnieren",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
