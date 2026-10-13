package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueLight,
    onPrimary = NavyDarkest,
    primaryContainer = NavyCard,
    onPrimaryContainer = ElectricBlueGlow,
    secondary = AccentCyan,
    onSecondary = NavyDarkest,
    secondaryContainer = NavySurface,
    onSecondaryContainer = Color.White,
    tertiary = AccentEmerald,
    onTertiary = NavyDarkest,
    background = NavyDarkest,
    onBackground = Color(0xFFF1F5F9),
    surface = NavyDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = NavySurface,
    onSurfaceVariant = SlateLight,
    outline = SlateBorder,
    outlineVariant = Color(0xFF1E293B),
    error = AccentRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = ElectricBlueHover,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = AccentEmerald,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = AccentRose,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep navy professional tech dark theme
    dynamicColor: Boolean = false, // Keep consistent SpecForge branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
