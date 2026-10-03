package com.example.tableo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.tableo.data.enums.ThemeMode

/** Hält die App-weite Theme-Einstellung, änderbar aus den Einstellungen heraus. */
object ThemeState {
    var mode by mutableStateOf(ThemeMode.SYSTEM)
}

private val DarkColorScheme = darkColorScheme(
    primary = Copper80,
    onPrimary = Espresso10,
    secondary = Sage80,
    onSecondary = Espresso10,
    background = Espresso10,
    onBackground = Cream95,
    surface = Espresso20,
    onSurface = Cream95,
    surfaceVariant = Espresso30,
    surfaceContainer = EspressoContainer,
    onSurfaceVariant = MutedCream,
    outline = MutedCream.copy(alpha = 0.3f),
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = Copper40,
    onPrimary = Cream95,
    secondary = Sage40,
    onSecondary = Cream95,
    background = Cream95,
    onBackground = Espresso10,
    surface = Color.White,
    onSurface = Espresso10,
    surfaceVariant = Cream90,
    surfaceContainer = CreamContainer,
    onSurfaceVariant = MutedEspresso,
    outline = MutedEspresso.copy(alpha = 0.25f),
    error = ErrorRed
)

/**
 * App-Theme mit eigener Kupfer/Espresso-Farbpalette statt Material-You-Standardfarben.
 * Folgt standardmäßig dem Systemmodus, kann aber über ThemeState.mode fest auf
 * Hell/Dunkel gestellt werden (z.B. aus den Einstellungen).
 */
@Composable
fun TableoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (ThemeState.mode) {
        ThemeMode.SYSTEM -> darkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = tableoTypography(),
        content = content
    )
}