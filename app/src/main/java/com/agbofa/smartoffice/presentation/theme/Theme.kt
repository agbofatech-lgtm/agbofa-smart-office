package com.agbofa.smartoffice.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = BrandOnPrimary,
    secondary = BrandSecondary,
    onSecondary = BrandOnPrimary,
    tertiary = BrandAccent,
    onTertiary = BrandOnBackground,
    background = BrandBackground,
    onBackground = BrandOnBackground,
    surface = BrandSurface,
    onSurface = BrandOnSurface,
    surfaceVariant = BrandSurface,
    onSurfaceVariant = BrandOnSurface,
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDarkTheme,
    onPrimary = BrandPrimaryDark,
    secondary = BrandSecondary,
    onSecondary = BrandOnPrimary,
    tertiary = BrandAccent,
    onTertiary = BrandOnBackground,
    background = BrandBackgroundDark,
    onBackground = BrandOnBackgroundDark,
    surface = BrandSurfaceDark,
    onSurface = BrandOnBackgroundDark,
    surfaceVariant = BrandSurfaceDark,
    onSurfaceVariant = BrandOnBackgroundDark,
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
