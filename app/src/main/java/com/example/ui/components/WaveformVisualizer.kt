package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.NeonTeal
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    soundLevel: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    barCount: Int = 18
) {
    val transition = rememberInfiniteTransition(label = "wave_anim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .testTag("waveform_visualizer")
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val barWidth = (totalWidth / (barCount * 1.8f)).coerceAtLeast(3.dp.toPx())
        val spacing = (totalWidth - (barWidth * barCount)) / (barCount + 1)

        val gradient = Brush.verticalGradient(
            colors = listOf(
                CyanGlow,
                NeonTeal,
                CyanPrimary
            ),
            startY = 0f,
            endY = canvasHeight
        )

        for (i in 0 until barCount) {
            val x = spacing + i * (barWidth + spacing)

            // Sine wave pattern combined with sound level
            val sineVal = (sin(phase + (i * 0.45f)) + 1f) / 2f
            val baseHeight = if (isActive) {
                (canvasHeight * 0.15f) + (canvasHeight * 0.75f * ((soundLevel * 0.7f) + (sineVal * 0.3f)))
            } else {
                canvasHeight * 0.08f
            }.coerceIn(4.dp.toPx(), canvasHeight)

            val y = (canvasHeight - baseHeight) / 2f

            drawRoundRect(
                brush = if (isActive) gradient else Brush.verticalGradient(listOf(Color(0x4400E5FF), Color(0x2200E5FF))),
                topLeft = Offset(x, y),
                size = Size(barWidth, baseHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
