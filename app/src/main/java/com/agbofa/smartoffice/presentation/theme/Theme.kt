package com.agbofa.smartoffice.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    onSurfaceVariant = Slate,
    secondary = Slate,
    onSecondary = Paper,
    surfaceVariant = Mist,
)

private val DarkColors = darkColorScheme(
    primary = InkDark,
    onPrimary = PaperDark,
    background = PaperDark,
    onBackground = InkDark,
    surface = PaperDark,
    onSurface = InkDark,
    onSurfaceVariant = SlateDark,
    secondary = SlateDark,
    onSecondary = PaperDark,
    surfaceVariant = MistDark,
)

@Composable
fun SmartOfficeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SmartOfficeTypography,
        content = content,
    )
}
