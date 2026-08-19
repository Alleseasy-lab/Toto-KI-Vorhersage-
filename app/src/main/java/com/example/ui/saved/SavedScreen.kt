package com.example.ui.saved

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TicketResult
import com.example.ui.generator.BentoCard
import com.example.ui.generator.BentoStatBadge
import com.example.ui.generator.LotteryBall
import com.example.ui.generator.TotoBadge
import com.example.ui.theme.*

@Composable
fun SavedScreen(
    savedTickets: List<TicketResult>,
    onDeleteTicket: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        BentoCard(
            backgroundColor = SurfaceDark,
            borderColor = SurfaceBorder
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Gespeicherte Scheine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${savedTickets.size} Kombinationen im Archiv",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                if (savedTickets.isNotEmpty()) {
                    TextButton(
                        onClick = onClearAll,
                        colors = ButtonDefaults.textButtonColors(contentColor = HotRed)
                    ) {
                        Text("Alle löschen", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (savedTickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Empty",
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Noch keine Zahlen gespeichert",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Tippe im Generator auf 'Speichern', um deine Glückszahlen hier zu sichern.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedTickets, key = { it.id }) { ticket ->
                    BentoCard(
                        backgroundColor = SurfaceDark,
                        borderColor = SurfaceBorder,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        // Top info row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = ticket.customTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = ticket.formattedDate,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(ticket.toShareableText()))
                                        Toast.makeText(context, "Kopiert!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteTicket(ticket.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = HotRed.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Balls
                        if (ticket.mainNumbers.size > 10) {
                            // Toto or large pool
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ticket.mainNumbers.forEachIndexed { idx, sym ->
                                    TotoBadge(symbol = sym, index = idx)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ticket.mainNumbers.forEach { num ->
                                    LotteryBall(number = num, modifier = Modifier.size(38.dp))
                                }

                                if (ticket.superNumbers.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Star",
                                        tint = AccentAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    ticket.superNumbers.forEach { num ->
                                        LotteryBall(
                                            number = num,
                                            isSuper = true,
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Stats pills
                        if (ticket.mainNumbers.size <= 10 && ticket.mainNumbers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BentoStatBadge(label = "Summe", value = "${ticket.sum}")
                                BentoStatBadge(label = "G/U", value = "${ticket.evenCount}/${ticket.oddCount}")
                            }
                        }
                    }
                }
            }
        }
    }
}
