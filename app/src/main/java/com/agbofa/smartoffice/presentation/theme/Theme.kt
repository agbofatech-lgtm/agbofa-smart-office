package com.agbofa.smartoffice.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.agbofa.smartoffice.presentation.settings.LocalOfficePreferences
import com.agbofa.smartoffice.presentation.settings.OfficePaletteMapping

@Composable
fun SmartOfficeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val tokens = OfficePaletteMapping.tokens(LocalOfficePreferences.current.palette)
    val lightColors = lightColorScheme(
        primary = tokens.primary,
        onPrimary = tokens.onPrimary,
        primaryContainer = tokens.primaryLight,
        onPrimaryContainer = tokens.primaryDark,
        secondary = tokens.secondary,
        onSecondary = tokens.onPrimary,
        secondaryContainer = tokens.secondaryLight,
        onSecondaryContainer = tokens.onBackground,
        tertiary = tokens.accent,
        onTertiary = tokens.primaryDark,
        tertiaryContainer = tokens.accentSoft,
        onTertiaryContainer = tokens.primaryDark,
        background = tokens.background,
        onBackground = tokens.onBackground,
        surface = tokens.surfaceRaised,
        onSurface = tokens.onSurface,
        surfaceVariant = tokens.surface,
        onSurfaceVariant = tokens.muted,
        error = tokens.error,
        onError = tokens.onPrimary,
    )
    val darkColors = darkColorScheme(
        primary = BrandPrimaryDarkTheme,
        onPrimary = BrandPrimaryDark,
        primaryContainer = BrandPrimaryDark,
        onPrimaryContainer = BrandOnBackgroundDark,
        secondary = tokens.secondary,
        onSecondary = tokens.onPrimary,
        tertiary = tokens.accent,
        onTertiary = tokens.primaryDark,
        background = BrandBackgroundDark,
        onBackground = BrandOnBackgroundDark,
        surface = BrandSurfaceDark,
        onSurface = BrandOnBackgroundDark,
        surfaceVariant = BrandSurfaceDark,
        onSurfaceVariant = BrandOnBackgroundDark,
        error = tokens.error,
        onError = tokens.onPrimary,
    )
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        typography = SmartOfficeTypography,
        content = content,
    )
}
