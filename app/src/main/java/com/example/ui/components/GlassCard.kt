package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NovaTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderColor: Color = NovaTheme.vars.surfaceBorder,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = NovaTheme.vars.surfaceCard,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = NovaTheme.isDark
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                backgroundColor.copy(alpha = 0.85f),
                NovaTheme.vars.surface.copy(alpha = 0.90f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.92f),
                Color(0xFFF3F8FB).copy(alpha = 0.95f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                borderWidth,
                Brush.linearGradient(
                    listOf(
                        borderColor,
                        borderColor.copy(alpha = if (isDark) 0.15f else 0.4f)
                    )
                ),
                shape
            )
            .background(bgBrush)
            .padding(contentPadding),
        content = content
    )
}
