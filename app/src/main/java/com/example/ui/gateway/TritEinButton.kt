package com.example.ui.gateway

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

// ─── Farben ───────────────────────────────────────────────────────────────────
private val RoyalBlue    = Color(0xFF1A3AFF)
private val ElectricBlue = Color(0xFF00AAFF)
private val DeepRed      = Color(0xFFCC0020)
private val HotRed       = Color(0xFFFF2244)
private val NeonYellow   = Color(0xFFFFE500)

// ─── Ton (440 Hz, 80 ms) ─────────────────────────────────────────────────────
private fun playHeartbeatTone() {
    runCatching {
        val sampleRate = 44100
        val samples = sampleRate * 80 / 1000
        val pcm = ShortArray(samples) { i ->
            val t = i.toDouble() / sampleRate
            val env = if (i < samples / 3) i.toDouble() / (samples / 3)
                      else (samples - i).toDouble() / (samples * 2.0 / 3.0)
            (sin(2 * PI * 440 * t) * env * Short.MAX_VALUE).toInt().toShort()
        }
        AudioTrack(
            AudioManager.STREAM_MUSIC, sampleRate,
            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
            samples * 2, AudioTrack.MODE_STATIC
        ).apply { write(pcm, 0, samples); play() }
    }
}

// ─── Daten für einen Touch-Ring ───────────────────────────────────────────────
private data class TouchRing(val id: Int, val center: Offset, val progress: Float)

private fun lerpColor(a: Color, b: Color, t: Float) = Color(
    red   = a.red   + (b.red   - a.red)   * t,
    green = a.green + (b.green - a.green) * t,
    blue  = a.blue  + (b.blue  - a.blue)  * t,
    alpha = 1f
)

@Composable
fun TritEinButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    // ── 60-BPM Herzschlag ────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "hb")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue  = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1.00f at 0   using FastOutSlowInEasing
                1.05f at 120 using FastOutSlowInEasing
                1.00f at 280 using FastOutSlowInEasing
                1.05f at 400 using FastOutSlowInEasing
                1.00f at 560 using LinearEasing
                1.00f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "hbScale"
    )

    // ── Blau-Rot-Schimmer ────────────────────────────────────────────────────
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    // ── Touch-Ringe ──────────────────────────────────────────────────────────
    var rings by remember { mutableStateOf(listOf<TouchRing>()) }
    var ringId by remember { mutableIntStateOf(0) }

    fun spawnRings(center: Offset) {
        val id = ringId++
        rings = rings + TouchRing(id, center, 0f)
        scope.launch {
            repeat(55) { step ->
                delay(14)
                rings = rings.map {
                    if (it.id == id) it.copy(progress = (step + 1) / 55f) else it
                }
            }
            rings = rings.filter { it.id != id }
        }
    }

    // Schimmer-Farbberechnung
    val c0 = lerpColor(RoyalBlue,    DeepRed,      shimmer)
    val c1 = lerpColor(ElectricBlue, HotRed,       shimmer)
    val c2 = lerpColor(HotRed,       ElectricBlue, shimmer)
    val c3 = lerpColor(DeepRed,      RoyalBlue,    shimmer)

    Box(
        modifier = modifier
            .size(220.dp, 76.dp)
            .scale(heartScale),
        contentAlignment = Alignment.Center
    ) {
        // ── Touch-Ringe auf Canvas ────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            rings.forEach { ring ->
                val maxR = size.maxDimension * 0.9f
                val r    = maxR * ring.progress
                val a    = (1f - ring.progress)
                drawCircle(
                    color  = ElectricBlue.copy(alpha = a * 0.7f),
                    radius = r,
                    center = ring.center,
                    style  = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color  = HotRed.copy(alpha = a * 0.5f),
                    radius = r * 0.6f,
                    center = ring.center,
                    style  = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // ── Schimmernder Blau-Rot-Button ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(38.dp))
                .background(
                    Brush.horizontalGradient(colors = listOf(c0, c1, c2, c3))
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(NeonYellow, Color.White.copy(alpha = 0.9f), NeonYellow)
                    ),
                    shape = RoundedCornerShape(38.dp)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            spawnRings(offset)
                            // Zweistufige Haptik: kurz + mittel
                            runCatching {
                                val vm = context.getSystemService(
                                    android.content.Context.VIBRATOR_MANAGER_SERVICE
                                ) as? VibratorManager
                                vm?.defaultVibrator?.vibrate(
                                    VibrationEffect.createWaveform(
                                        longArrayOf(0, 35, 25, 55),
                                        intArrayOf(0, 90, 0, 50),
                                        -1
                                    )
                                )
                            }
                            tryAwaitRelease()
                        },
                        onTap = {
                            playHeartbeatTone()
                            onClick()
                        }
                    )
                },
                contentAlignment = Alignment.Center
        ) {
            Text(
                text          = "Tritt ein",
                color         = Color.White,
                fontSize      = 22.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}
