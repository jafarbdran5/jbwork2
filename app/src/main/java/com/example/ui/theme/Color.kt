package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Base Dark Palette Constants
val CyberBgDark = Color(0xFF09090B)
val CyberSurfaceDark = Color(0xFF111113)
val CyberCardDark = Color(0xFF18181B)
val CyberCardElevatedDark = Color(0xFF27272A)
val CyberBorderDark = Color(0xFF27272A)

val TextPrimaryDark = Color(0xFFFAFAFA)
val TextSecondaryDark = Color(0xFFA1A1AA)
val TextTertiaryDark = Color(0xFF71717A)
val TextMutedDark = Color(0xFF52525B)

// Base Light Palette Constants
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightCard = Color(0xFFFFFFFF)
val LightCardElevated = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextTertiary = Color(0xFF64748B)
val LightTextMuted = Color(0xFF94A3B8)

// Accent Colors (Universal for Dark & Light)
val CyberPrimary = Color(0xFF4F46E5)
val CyberPrimaryLight = Color(0xFF6366F1)
val CyberSecondary = Color(0xFF7C3AED)
val CyberTertiary = Color(0xFF0891B2)

val CyberSuccess = Color(0xFF10B981)
val CyberWarning = Color(0xFFF59E0B)
val CyberDanger = Color(0xFFEF4444)
val CyberInfo = Color(0xFF3B82F6)

// Dynamic Theme-Aware Getters for Compose
val CyberBg: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val CyberSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val CyberCard: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val CyberCardElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val CyberBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == CyberBgDark) TextTertiaryDark else LightTextTertiary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == CyberBgDark) TextMutedDark else LightTextMuted


