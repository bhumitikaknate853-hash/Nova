package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = DeepNavy,
    primaryContainer = SurfaceCard,
    onPrimaryContainer = CyanGlow,
    secondary = NeonTeal,
    onSecondary = DeepNavy,
    secondaryContainer = SurfaceNavy,
    onSecondaryContainer = SoftCyan,
    tertiary = ElectricBlue,
    onTertiary = Color.White,
    background = DeepNavy,
    onBackground = TextPrimaryDark,
    surface = SurfaceNavy,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceCardBorder,
    outlineVariant = Color(0x2200E5FF),
    error = ErrorCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF008394),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3F8FF),
    onPrimaryContainer = Color(0xFF00363E),
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00201B),
    tertiary = ElectricBlue,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2ECF5),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFB0BEC5),
    outlineVariant = Color(0xFFCFD8DC),
    error = ErrorCrimson,
    onError = Color.White
)

@Composable
fun NovaAiTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val themeVars = if (darkTheme) DarkThemeVariables else LightThemeVariables
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalNovaVariables provides themeVars,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
