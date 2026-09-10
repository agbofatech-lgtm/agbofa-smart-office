package com.agbofa.smartoffice.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryLight,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandSecondary,
    onSecondary = BrandOnPrimary,
    secondaryContainer = BrandSecondaryLight,
    onSecondaryContainer = BrandOnBackground,
    tertiary = BrandAccent,
    onTertiary = BrandPrimaryDark,
    tertiaryContainer = BrandAccentSoft,
    onTertiaryContainer = BrandPrimaryDark,
    background = BrandBackground,
    onBackground = BrandOnBackground,
    surface = BrandSurfaceRaised,
    onSurface = BrandOnSurface,
    surfaceVariant = BrandSurface,
    onSurfaceVariant = BrandMuted,
    error = BrandError,
    onError = BrandOnPrimary,
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDarkTheme,
    onPrimary = BrandPrimaryDark,
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = BrandOnBackgroundDark,
    secondary = BrandSecondary,
    onSecondary = BrandOnPrimary,
    tertiary = BrandAccent,
    onTertiary = BrandPrimaryDark,
    background = BrandBackgroundDark,
    onBackground = BrandOnBackgroundDark,
    surface = BrandSurfaceDark,
    onSurface = BrandOnBackgroundDark,
    surfaceVariant = BrandSurfaceDark,
    onSurfaceVariant = BrandOnBackgroundDark,
    error = BrandError,
    onError = BrandOnPrimary,
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
