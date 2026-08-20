package com.example.ui.payment

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.generator.BentoCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    onDismissRequest: () -> Unit,
    onPaymentSuccess: (DrawPackage, PaymentProvider, String?) -> Unit,
    initialPackageId: String = "single_draw"
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPackage by remember {
        mutableStateOf(STANDARD_DRAW_PACKAGES.find { it.id == initialPackageId } ?: STANDARD_DRAW_PACKAGES.first())
    }
    var selectedProvider by remember { mutableStateOf(PaymentProvider.PAYPAL) }
    var selectedMollieMethod by remember { mutableStateOf(MollieSubMethod.CREDIT_CARD) }

    var isProcessing by remember { mutableStateOf(false) }
    var paymentComplete by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(SurfaceBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (paymentComplete) {
                // Success Confirmation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AccentEmerald.copy(alpha = 0.2f))
                            .border(2.dp, AccentEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Success",
                            tint = AccentEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = "Zahlung erfolgreich!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Du hast ${selectedPackage.title} (${selectedPackage.formattedPrice}) via ${selectedProvider.displayName} freigeschaltet.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Jetzt Zahlen ziehen", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            } else {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Ziehungs-Kasse",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Offizieller Tarif: 1,35 € pro Ziehung",
                            fontSize = 13.sp,
                            color = AccentAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentAmber.copy(alpha = 0.15f))
                            .border(1.dp, AccentAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "1 Ziehung = 1,35 €",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber
                        )
                    }
                }

                // ── Package Selection Bento Grid ──────────────────────────
                Text(
                    text = "Paket auswählen",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    STANDARD_DRAW_PACKAGES.forEach { pkg ->
                        val isSelected = selectedPackage.id == pkg.id
                        val borderColor = if (isSelected) ElectricBlue else SurfaceBorder
                        val bgColor = if (isSelected) ElectricBlue.copy(alpha = 0.12f) else SurfaceCard

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable { selectedPackage = pkg }
                                .padding(14.dp)
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
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPackage = pkg },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = ElectricBlue,
                                            unselectedColor = TextMuted
                                        )
                                    )
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = pkg.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = TextPrimary
                                            )
                                            if (pkg.isPopular) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(AccentPurple)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Standard", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${pkg.subtitle} ${pkg.pricePerDrawText}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = pkg.formattedPrice,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = if (isSelected) ElectricBlue else Color.White
                                )
                            }
                        }
                    }
                }

                // ── Payment Provider Selection ────────────────────────────
                Text(
                    text = "Zahlungsmethode wählen",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // PayPal Option Card
                    val isPayPal = selectedProvider == PaymentProvider.PAYPAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isPayPal) Color(0xFF003087).copy(alpha = 0.25f) else SurfaceCard)
                            .border(1.5.dp, if (isPayPal) Color(0xFF0079C1) else SurfaceBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedProvider = PaymentProvider.PAYPAL }
                            .padding(14.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = "PayPal",
                                tint = if (isPayPal) Color(0xFF0079C1) else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "PayPal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isPayPal) Color.White else TextSecondary
                            )
                            Text(
                                text = "Käuferschutz",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Mollie Option Card
                    val isMollie = selectedProvider == PaymentProvider.MOLLIE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isMollie) Color(0xFF8B5CF6).copy(alpha = 0.25f) else SurfaceCard)
                            .border(1.5.dp, if (isMollie) AccentPurple else SurfaceBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedProvider = PaymentProvider.MOLLIE }
                            .padding(14.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CreditCard,
                                contentDescription = "Mollie",
                                tint = if (isMollie) AccentPurple else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mollie",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isMollie) Color.White else TextSecondary
                            )
                            Text(
                                text = "Karte, Klarna & mehr",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Mollie Sub-Methods (if Mollie selected)
                if (selectedProvider == PaymentProvider.MOLLIE) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCard)
                            .border(0.5.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Mollie Zahlungsart:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        MollieSubMethod.entries.forEach { method ->
                            val isSubSelected = selectedMollieMethod == method
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSubSelected) AccentPurple.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { selectedMollieMethod = method }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(method.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                    Text(method.subtitle, fontSize = 10.sp, color = TextMuted)
                                }
                                if (isSubSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = AccentPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Security & Environment Note ───────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Secure",
                        tint = AccentEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "256-Bit SSL-verschlüsselte Abwicklung via ${selectedProvider.displayName} API",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // ── Pay CTA Button ────────────────────────────────────────
                Button(
                    onClick = {
                        isProcessing = true
                        coroutineScope.launch {
                            // Simulate checkout API handshake
                            delay(1200)
                            isProcessing = false
                            paymentComplete = true
                            onPaymentSuccess(
                                selectedPackage,
                                selectedProvider,
                                if (selectedProvider == PaymentProvider.MOLLIE) selectedMollieMethod.title else null
                            )
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), ambientColor = if (selectedProvider == PaymentProvider.PAYPAL) Color(0xFF0079C1) else AccentPurple, spotColor = if (selectedProvider == PaymentProvider.PAYPAL) Color(0xFF0079C1) else AccentPurple),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedProvider == PaymentProvider.PAYPAL) Color(0xFF0079C1) else AccentPurple
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Verbindung zu ${selectedProvider.displayName}...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        val btnIcon = if (selectedProvider == PaymentProvider.PAYPAL) Icons.Filled.AccountBalanceWallet else Icons.Filled.CreditCard
                        Icon(
                            imageVector = btnIcon,
                            contentDescription = "Pay",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jetzt für ${selectedPackage.formattedPrice} mit ${selectedProvider.displayName} zahlen",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
