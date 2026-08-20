package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PaymentProvider(val displayName: String, val brandColorHex: Long) {
    PAYPAL("PayPal", 0xFF003087),
    MOLLIE("Mollie", 0xFF001428)
}

enum class MollieSubMethod(val title: String, val subtitle: String) {
    CREDIT_CARD("Kreditkarte", "Visa, Mastercard, Amex"),
    KLARNA_SOFORT("Klarna / Sofort", "Direktes Online-Banking"),
    IDEAL("iDEAL", "Niederlande Online-Zahlung"),
    BANCONTACT("Bancontact", "Belgien Online-Zahlung"),
    EPS("EPS Überweisung", "Österreich Online-Zahlung")
}

data class DrawPackage(
    val id: String,
    val title: String,
    val draws: Int,
    val priceEuros: Double,
    val isPopular: Boolean = false,
    val subtitle: String = ""
) {
    val formattedPrice: String get() = String.format(Locale.GERMAN, "%.2f €", priceEuros)
    val pricePerDrawText: String
        get() = if (draws > 1) {
            String.format(Locale.GERMAN, "(ca. %.2f € / Ziehung)", priceEuros / draws)
        } else {
            "1,35 € / Ziehung"
        }
}

val STANDARD_DRAW_PACKAGES = listOf(
    DrawPackage(
        id = "single_draw",
        title = "1 Ziehung",
        draws = 1,
        priceEuros = 1.35,
        isPopular = true,
        subtitle = "Standard-Einzelticket"
    ),
    DrawPackage(
        id = "bundle_5",
        title = "5 Ziehungen",
        draws = 5,
        priceEuros = 5.99,
        isPopular = false,
        subtitle = "Sparpaket (Spare 11%)"
    ),
    DrawPackage(
        id = "bundle_10",
        title = "10 Ziehungen",
        draws = 10,
        priceEuros = 9.99,
        isPopular = false,
        subtitle = "Vorteilspaket (Spare 26%)"
    ),
    DrawPackage(
        id = "monthly_abo",
        title = "Monats-Abo (VIP)",
        draws = 999,
        priceEuros = 1.99,
        isPopular = false,
        subtitle = "Unbegrenzte Ziehungen & KI-Berater"
    )
)

data class PaymentTransaction(
    val id: String = "TX-" + (100000..999999).random(),
    val packageTitle: String,
    val amount: Double,
    val provider: PaymentProvider,
    val subMethod: String? = null,
    val drawsAdded: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedDate: String
        get() = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date(timestamp))
    val formattedPrice: String get() = String.format(Locale.GERMAN, "%.2f €", amount)
}
