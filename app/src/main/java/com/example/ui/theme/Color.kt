package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// NOVA AI Brain Futuristic Palette
val CyanGlow = Color(0xFF00F5FF)
val CyanPrimary = Color(0xFF00E5FF)
val CyanAccent = Color(0xFF2DE2E6)
val NeonTeal = Color(0xFF03DAC6)
val DeepNavy = Color(0xFF070B19)
val DarkNavy = Color(0xFF0A1128)
val SurfaceNavy = Color(0xFF101C38)
val SurfaceCard = Color(0xFF16254A)
val SurfaceCardBorder = Color(0x3300F5FF)
val SurfaceLight = Color(0xFFF1F6FA)
val SurfaceCardLight = Color(0xFFFFFFFF)
val SurfaceBorderLight = Color(0xFFCFE1EC)

val ElectricBlue = Color(0xFF1B82F6)
val SoftCyan = Color(0xFF70EEFF)
val CutePink = Color(0xFFFF69B4)
val GlowingGreen = Color(0xFF00FF88)
val WarningAmber = Color(0xFFFFB703)
val ErrorCrimson = Color(0xFFFF4D6D)

// High-contrast typography definitions
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFF90A4AE)
val TextPrimaryLight = Color(0xFF0F172A) // Deep black/slate for crisp light-mode readability
val TextSecondaryLight = Color(0xFF334155) // Slate-700 for high-contrast subtitles

// Global Theme Semantic Variables (Equivalent to CSS Variables)
data class NovaThemeVariables(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceBorder: Color,
    val primary: Color,
    val primaryGlow: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val cuteAccent: Color,
    val sectionHeader: Color,
    val readyBadgeBg: Color,
    val readyBadgeText: Color
)

val DarkThemeVariables = NovaThemeVariables(
    isDark = true,
    background = DeepNavy,
    surface = SurfaceNavy,
    surfaceCard = SurfaceCard,
    surfaceBorder = SurfaceCardBorder,
    primary = CyanPrimary,
    primaryGlow = CyanGlow,
    secondary = NeonTeal,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    cuteAccent = CutePink,
    sectionHeader = CyanGlow,
    readyBadgeBg = Color(0x3300FF88),
    readyBadgeText = Color(0xFF00FF88)
)

val LightThemeVariables = NovaThemeVariables(
    isDark = false,
    background = SurfaceLight,
    surface = Color(0xFFE5EEF5),
    surfaceCard = SurfaceCardLight,
    surfaceBorder = SurfaceBorderLight,
    primary = Color(0xFF007A87),
    primaryGlow = Color(0xFF0097A7),
    secondary = Color(0xFF00897B),
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    cuteAccent = Color(0xFFFF4081),
    sectionHeader = Color(0xFF006D77),
    readyBadgeBg = Color(0xFFD1FAE5),
    readyBadgeText = Color(0xFF065F46)
)

val LocalNovaVariables = staticCompositionLocalOf { DarkThemeVariables }
val LocalIsDarkTheme = compositionLocalOf { true }

object NovaTheme {
    val vars: NovaThemeVariables
        @Composable
        get() = LocalNovaVariables.current

    val isDark: Boolean
        @Composable
        get() = LocalIsDarkTheme.current
}

// Gradients
val NovaNavyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF050814),
        Color(0xFF0A1128),
        Color(0xFF0E1C3D)
    )
)

val NovaLightGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFEAF5FA),
        Color(0xFFF4F9FD),
        Color(0xFFE3F0F7)
    )
)

val NovaCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x3300F5FF),
        Color(0x1016254A)
    )
)

val NovaTealGlowGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF00F5FF),
        Color(0xFF03DAC6)
    )
)

val RobotEyeGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFF00F5FF),
        Color(0xFF0083B0)
    )
)
