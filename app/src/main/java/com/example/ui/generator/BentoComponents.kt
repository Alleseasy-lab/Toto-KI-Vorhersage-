package com.example.ui.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceCard,
    borderColor: Color = SurfaceBorder,
    cornerRadius: Dp = BentoCardRadius,
    shadowElevation: Dp = BentoShadowElevation,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = BentoShadowColor,
                spotColor = BentoShadowColor
            )
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .then(clickModifier),
        color = backgroundColor,
        shape = shape
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun LotteryBall(
    number: Int,
    modifier: Modifier = Modifier,
    isSuper: Boolean = false,
    delayMs: Long = 0,
    generationKey: Any = number
) {
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(generationKey) {
        scale.snapTo(0.2f)
        alpha.snapTo(0f)
        if (delayMs > 0) {
            delay(delayMs)
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 320,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val gradient = if (isSuper) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFE082),
                Color(0xFFFFA000),
                Color(0xFFE65100)
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF7DD3FC),
                Color(0xFF0284C7),
                Color(0xFF0369A1)
            )
        )
    }

    val glowColor = if (isSuper) AccentAmber.copy(alpha = 0.45f) else ElectricBlue.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
            .size(46.dp)
            .shadow(6.dp, CircleShape, ambientColor = glowColor, spotColor = glowColor)
            .clip(CircleShape)
            .background(gradient)
            .border(1.2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TotoBadge(
    symbol: Int,
    index: Int,
    modifier: Modifier = Modifier,
    delayMs: Long = 0,
    generationKey: Any = symbol
) {
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(generationKey) {
        scale.snapTo(0.2f)
        alpha.snapTo(0f)
        if (delayMs > 0) {
            delay(delayMs)
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val text = when (symbol) {
        1 -> "1"
        0 -> "0"
        else -> "2"
    }
    val badgeColor = when (symbol) {
        1 -> ElectricBlue
        0 -> AccentAmber
        else -> AccentPink
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
            .padding(4.dp)
    ) {
        Text(
            text = "S${index + 1}",
            fontSize = 10.sp,
            color = TextMuted,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(4.dp, RoundedCornerShape(BentoBadgeRadius), ambientColor = badgeColor.copy(alpha = 0.3f), spotColor = badgeColor.copy(alpha = 0.3f))
                .clip(RoundedCornerShape(BentoBadgeRadius))
                .background(badgeColor.copy(alpha = 0.18f))
                .border(1.dp, badgeColor.copy(alpha = 0.7f), RoundedCornerShape(BentoBadgeRadius)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = badgeColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun BentoStatBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(BentoInnerRadius), ambientColor = BentoShadowColor, spotColor = BentoShadowColor)
            .clip(RoundedCornerShape(BentoInnerRadius))
            .background(SurfaceCardElevated)
            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(BentoInnerRadius))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$label:",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 12.sp,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
