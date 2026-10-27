package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.NovaTheme

@Composable
fun AnimatedCyberBackground(
    isDarkTheme: Boolean = NovaTheme.isDark,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "cyber_bg_anim")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_phase"
    )

    val baseGradient = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF040714),
                DeepNavy,
                Color(0xFF09132C),
                Color(0xFF050918)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0F7FB),
                Color(0xFFF7FAFD),
                Color(0xFFE8F3F9)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseGradient)
    ) {
        // Animated radiant neon orbs in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Orb 1: Cyan floating top-right
            val orb1X = width * 0.75f + (kotlin.math.sin(phase.toDouble()).toFloat() * 60f)
            val orb1Y = height * 0.18f + (kotlin.math.cos(phase.toDouble()).toFloat() * 50f)
            val orb1Radius = width * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isDarkTheme) CyanGlow else Color(0xFF00B4D8)).copy(alpha = if (isDarkTheme) 0.16f else 0.12f),
                        (if (isDarkTheme) NeonTeal else Color(0xFF009688)).copy(alpha = if (isDarkTheme) 0.06f else 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                ),
                radius = orb1Radius,
                center = Offset(orb1X, orb1Y)
            )

            // Orb 2: Purple / Pink neon bottom-left
            val orb2X = width * 0.22f + (kotlin.math.cos(phase.toDouble() * 0.7).toFloat() * 50f)
            val orb2Y = height * 0.72f + (kotlin.math.sin(phase.toDouble() * 0.7).toFloat() * 60f)
            val orb2Radius = width * 0.5f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isDarkTheme) Color(0xFFE040FB) else Color(0xFFFF4081)).copy(alpha = if (isDarkTheme) 0.12f else 0.08f),
                        (if (isDarkTheme) Color(0xFF7C4DFF) else Color(0xFF7E57C2)).copy(alpha = if (isDarkTheme) 0.05f else 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                ),
                radius = orb2Radius,
                center = Offset(orb2X, orb2Y)
            )

            // Subtle cyber grid dots
            val dotColor = if (isDarkTheme) Color(0x1500F5FF) else Color(0x18008394)
            val step = 48f
            var x = 24f
            while (x < width) {
                var y = 24f
                while (y < height) {
                    drawCircle(
                        color = dotColor,
                        radius = 1.2f,
                        center = Offset(x, y)
                    )
                    y += step
                }
                x += step
            }
        }

        content()
    }
}
