package com.example.ui.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.advisor.AdvisorScreen
import com.example.ui.generator.GeneratorScreen
import com.example.ui.payment.PaymentBottomSheet
import com.example.ui.saved.SavedScreen
import com.example.ui.theme.*

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Ziehungs-Guthaben (Initial 2 kostenlose Begrüßungs-Ziehungen, danach 1,35 € pro Ziehung)
    var availableDraws by remember { mutableIntStateOf(2) }

    // Transaktionshistorie
    var transactions by remember {
        mutableStateOf(
            listOf<PaymentTransaction>()
        )
    }

    // Modal Sheet State for global trigger
    var activePaymentPackageId by remember { mutableStateOf<String?>(null) }

    // Persistent in-memory saved tickets list
    var savedTickets by remember {
        mutableStateOf(
            listOf(
                TicketResult(
                    id = "init_1",
                    gameType = GameType.LOTTO_6_49,
                    mainNumbers = listOf(12, 18, 33, 35, 37, 48),
                    superNumbers = listOf(7)
                )
            )
        )
    }

    // Handler for successful payment
    fun handlePaymentSuccess(pkg: DrawPackage, provider: PaymentProvider, subMethod: String?) {
        val addedDraws = if (pkg.id == "monthly_abo") 999 else pkg.draws
        availableDraws += addedDraws

        val tx = PaymentTransaction(
            packageTitle = pkg.title,
            amount = pkg.priceEuros,
            provider = provider,
            subMethod = subMethod,
            drawsAdded = addedDraws
        )
        transactions = listOf(tx) + transactions

        Toast.makeText(
            context,
            "Erfolgreich gebucht: ${pkg.title} via ${provider.displayName}!",
            Toast.LENGTH_LONG
        ).show()
    }

    // Global payment sheet triggered from Advisor or Header
    activePaymentPackageId?.let { pkgId ->
        PaymentBottomSheet(
            onDismissRequest = { activePaymentPackageId = null },
            initialPackageId = pkgId,
            onPaymentSuccess = { pkg, provider, subMethod ->
                handlePaymentSuccess(pkg, provider, subMethod)
                activePaymentPackageId = null
            }
        )
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, SurfaceBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                color = SurfaceDark,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Filled.Casino,
                                contentDescription = "Generator",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                "Generator",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricBlue,
                            unselectedIconColor = TextMuted,
                            selectedTextColor = ElectricBlue,
                            unselectedTextColor = TextMuted,
                            indicatorColor = ElectricBlue.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            BadgedBox(badge = {
                                if (savedTickets.isNotEmpty()) {
                                    Badge(
                                        containerColor = AccentPurple,
                                        contentColor = Color.White
                                    ) {
                                        Text(savedTickets.size.toString())
                                    }
                                }
                            }) {
                                Icon(
                                    Icons.Filled.Bookmark,
                                    contentDescription = "Gespeichert",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                "Gespeichert",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPurple,
                            unselectedIconColor = TextMuted,
                            selectedTextColor = AccentPurple,
                            unselectedTextColor = TextMuted,
                            indicatorColor = AccentPurple.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = "KI-Berater",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                "KI & Kasse",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentAmber,
                            unselectedIconColor = TextMuted,
                            selectedTextColor = AccentAmber,
                            unselectedTextColor = TextMuted,
                            indicatorColor = AccentAmber.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> GeneratorScreen(
                    onSaveTicket = { ticket ->
                        savedTickets = listOf(ticket) + savedTickets.filter { it.id != ticket.id }
                    },
                    savedTicketsCount = savedTickets.size,
                    availableDraws = availableDraws,
                    onUseDraw = {
                        if (availableDraws > 0) {
                            availableDraws -= 1
                            true
                        } else {
                            false
                        }
                    },
                    onTopUpSuccess = { pkg, provider, subMethod ->
                        handlePaymentSuccess(pkg, provider, subMethod)
                    }
                )
                1 -> SavedScreen(
                    savedTickets = savedTickets,
                    onDeleteTicket = { id ->
                        savedTickets = savedTickets.filter { it.id != id }
                    },
                    onClearAll = {
                        savedTickets = emptyList()
                    }
                )
                2 -> AdvisorScreen(
                    onOpenPayment = { pkgId ->
                        activePaymentPackageId = pkgId
                    },
                    transactions = transactions
                )
            }
        }
    }
}
