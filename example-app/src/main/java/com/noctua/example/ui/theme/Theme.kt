package com.noctua.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OwlPurple = Color(0xFF7C4DFF)
val OwlPurpleDark = Color(0xFFB39DDB)
val NightBlue = Color(0xFF0D1B2A)
val MintGreen = Color(0xFF4ADE80)
val WarmAmber = Color(0xFFFBBF24)
val AlertRed = Color(0xFFEF5350)

private val DarkColors = darkColorScheme(
    primary = OwlPurpleDark,
    secondary = MintGreen,
    tertiary = WarmAmber,
    background = NightBlue,
    surface = Color(0xFF16283D),
    onBackground = Color(0xFFE8EEF5),
    onSurface = Color(0xFFE8EEF5),
)

private val LightColors = lightColorScheme(
    primary = OwlPurple,
    secondary = Color(0xFF16A34A),
    tertiary = Color(0xFFB45309),
)

@Composable
fun NoctuaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
