package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class GameType(
    val title: String,
    val count: Int,
    val minVal: Int,
    val maxVal: Int,
    val superCount: Int,
    val superMin: Int,
    val superMax: Int,
    val superName: String
) {
    LOTTO_6_49("Lotto 6 aus 49", 6, 1, 49, 1, 0, 9, "Superzahl (0–9)"),
    EUROJACKPOT("EuroJackpot", 5, 1, 50, 2, 1, 12, "Eurozahlen (1–12)"),
    TOTO_13("Toto / 13er Wette", 13, 1, 3, 0, 0, 0, ""),
    CUSTOM("Individuell", 6, 1, 49, 1, 0, 9, "Zusatzzahl")
}

data class TicketResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val gameType: GameType,
    val customTitle: String = gameType.title,
    val mainNumbers: List<Int>,
    val superNumbers: List<Int> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val sum: Int get() = mainNumbers.sum()
    val evenCount: Int get() = mainNumbers.count { it % 2 == 0 }
    val oddCount: Int get() = mainNumbers.count { it % 2 != 0 }
    val minNumber: Int get() = mainNumbers.minOrNull() ?: 0
    val maxNumber: Int get() = mainNumbers.maxOrNull() ?: 0

    val formattedDate: String
        get() = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date(timestamp))

    fun toShareableText(): String {
        val numsStr = mainNumbers.joinToString(", ")
        val superStr = if (superNumbers.isNotEmpty()) " | Super: ${superNumbers.joinToString(", ")}" else ""
        return "[$customTitle] Zahlen: $numsStr$superStr (Summe: $sum, Min/Max: $minNumber–$maxNumber)"
    }
}

object GeneratorLogic {
    fun draw(
        gameType: GameType,
        customCount: Int = 6,
        customMin: Int = 1,
        customMax: Int = 49,
        customSuperCount: Int = 1,
        customSuperMin: Int = 0,
        customSuperMax: Int = 9
    ): TicketResult {
        if (gameType == GameType.TOTO_13) {
            // Toto values represent 1 (Home win), 0 (Draw), 2 (Away win)
            val totoSymbols = listOf(1, 0, 2)
            val mains = List(13) { totoSymbols.random() }
            return TicketResult(
                gameType = gameType,
                mainNumbers = mains
            )
        }

        val count: Int
        val minV: Int
        val maxV: Int
        val superCnt: Int
        val sMin: Int
        val sMax: Int

        if (gameType == GameType.CUSTOM) {
            val safeMin = minOf(customMin, customMax)
            val safeMax = maxOf(customMin, customMax)
            val poolSize = (safeMax - safeMin + 1).coerceAtLeast(1)
            count = customCount.coerceIn(1, poolSize)
            minV = safeMin
            maxV = safeMax
            superCnt = customSuperCount
            sMin = customSuperMin
            sMax = customSuperMax
        } else {
            count = gameType.count
            minV = gameType.minVal
            maxV = gameType.maxVal
            superCnt = gameType.superCount
            sMin = gameType.superMin
            sMax = gameType.superMax
        }

        val pool = (minV..maxV).toMutableList()
        pool.shuffle()
        val pickedMain = pool.take(count).sorted()

        val pickedSuper = if (superCnt > 0) {
            val superPool = (sMin..sMax).toMutableList()
            superPool.shuffle()
            superPool.take(superCnt.coerceAtMost(superPool.size)).sorted()
        } else {
            emptyList()
        }

        return TicketResult(
            gameType = gameType,
            mainNumbers = pickedMain,
            superNumbers = pickedSuper
        )
    }
}
