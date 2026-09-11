package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = TextPrimaryDark,
    primaryContainer = CyberCardElevatedDark,
    onPrimaryContainer = CyberPrimaryLight,
    secondary = CyberSecondary,
    onSecondary = TextPrimaryDark,
    secondaryContainer = CyberCardDark,
    onSecondaryContainer = CyberSecondary,
    tertiary = CyberTertiary,
    background = CyberBgDark,
    onBackground = TextPrimaryDark,
    surface = CyberSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CyberCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CyberBorderDark,
    outlineVariant = CyberBorderDark,
    error = CyberDanger,
    onError = TextPrimaryDark
)

private val LightCyberColorScheme = lightColorScheme(
    primary = CyberPrimary,
    onPrimary = LightSurface,
    primaryContainer = LightCardElevated,
    onPrimaryContainer = CyberPrimary,
    secondary = CyberSecondary,
    onSecondary = LightSurface,
    secondaryContainer = LightCard,
    onSecondaryContainer = CyberSecondary,
    tertiary = CyberTertiary,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = CyberDanger,
    onError = LightSurface
)

@Composable
fun JaffarForensicsTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CyberColorScheme else LightCyberColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

