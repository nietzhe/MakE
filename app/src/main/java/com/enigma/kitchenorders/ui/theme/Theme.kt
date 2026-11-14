package com.enigma.kitchenorders.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LightColorScheme = lightColorScheme(
    primary = Sambal,
    onPrimary = Color.White,
    primaryContainer = SambalLight,
    onPrimaryContainer = Color.White,
    secondary = Kunyit,
    onSecondary = Kopi,
    secondaryContainer = KunyitPale,
    onSecondaryContainer = Kopi,
    tertiary = Pandan,
    onTertiary = Color.White,
    background = Tepung,
    onBackground = Kopi,
    surface = Color.White,
    onSurface = Kopi,
    surfaceVariant = Kelapa,
    onSurfaceVariant = TextSecondary,
    outline = Line
)

val DarkColorScheme = darkColorScheme(
    primary = Kunyit,
    onPrimary = Kopi,
    primaryContainer = KunyitLight,
    onPrimaryContainer = Kopi,
    secondary = Sambal,
    onSecondary = Color.White,
    background = Kopi,
    onBackground = Tepung,
    surface = Color(0xFF3D2A20),
    onSurface = Tepung,
    surfaceVariant = Color(0xFF4A3125),
    onSurfaceVariant = TextSecondaryDark,
    outline = Line
)

@Composable
fun KitchenOrdersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
