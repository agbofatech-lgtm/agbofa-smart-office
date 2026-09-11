package com.agbofa.smartoffice.presentation.settings

import androidx.compose.ui.graphics.Color
import com.agbofa.smartoffice.presentation.theme.BrandAccent
import com.agbofa.smartoffice.presentation.theme.BrandAccentSoft
import com.agbofa.smartoffice.presentation.theme.BrandBackground
import com.agbofa.smartoffice.presentation.theme.BrandError
import com.agbofa.smartoffice.presentation.theme.BrandMuted
import com.agbofa.smartoffice.presentation.theme.BrandOnBackground
import com.agbofa.smartoffice.presentation.theme.BrandOnPrimary
import com.agbofa.smartoffice.presentation.theme.BrandOnSurface
import com.agbofa.smartoffice.presentation.theme.BrandPrimary
import com.agbofa.smartoffice.presentation.theme.BrandPrimaryDark
import com.agbofa.smartoffice.presentation.theme.BrandPrimaryLight
import com.agbofa.smartoffice.presentation.theme.BrandSecondary
import com.agbofa.smartoffice.presentation.theme.BrandSecondaryLight
import com.agbofa.smartoffice.presentation.theme.BrandSurface
import com.agbofa.smartoffice.presentation.theme.BrandSurfaceRaised

data class OfficePaletteTokens(
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val secondary: Color,
    val secondaryLight: Color,
    val accent: Color,
    val accentSoft: Color,
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val onPrimary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val muted: Color,
    val error: Color,
)

object OfficePaletteMapping {
    fun tokens(palette: OfficePalette): OfficePaletteTokens = when (palette) {
        OfficePalette.Agbofa -> OfficePaletteTokens(
            primary = BrandPrimary,
            primaryDark = BrandPrimaryDark,
            primaryLight = BrandPrimaryLight,
            secondary = BrandSecondary,
            secondaryLight = BrandSecondaryLight,
            accent = BrandAccent,
            accentSoft = BrandAccentSoft,
            background = BrandBackground,
            surface = BrandSurface,
            surfaceRaised = BrandSurfaceRaised,
            onPrimary = BrandOnPrimary,
            onBackground = BrandOnBackground,
            onSurface = BrandOnSurface,
            muted = BrandMuted,
            error = BrandError,
        )
        OfficePalette.Slate -> OfficePaletteTokens(
            primary = BrandPrimaryDark,
            primaryDark = BrandOnBackground,
            primaryLight = BrandPrimaryLight,
            secondary = BrandMuted,
            secondaryLight = BrandPrimaryLight,
            accent = BrandPrimary,
            accentSoft = BrandPrimaryLight,
            background = BrandBackground,
            surface = BrandSurface,
            surfaceRaised = BrandSurfaceRaised,
            onPrimary = BrandOnPrimary,
            onBackground = BrandOnBackground,
            onSurface = BrandOnSurface,
            muted = BrandMuted,
            error = BrandError,
        )
        OfficePalette.Forest -> OfficePaletteTokens(
            primary = BrandSecondary,
            primaryDark = BrandPrimaryDark,
            primaryLight = BrandSecondaryLight,
            secondary = BrandPrimary,
            secondaryLight = BrandSecondaryLight,
            accent = BrandAccent,
            accentSoft = BrandAccentSoft,
            background = BrandBackground,
            surface = BrandSurface,
            surfaceRaised = BrandSurfaceRaised,
            onPrimary = BrandOnPrimary,
            onBackground = BrandOnBackground,
            onSurface = BrandOnSurface,
            muted = BrandMuted,
            error = BrandError,
        )
    }
}
