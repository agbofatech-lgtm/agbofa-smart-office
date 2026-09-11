package com.agbofa.smartoffice.presentation.settings

import androidx.compose.runtime.compositionLocalOf

enum class OfficePalette {
    Agbofa,
    Slate,
    Forest,
}

data class OfficePreferences(
    val officeName: String = DEFAULT_OFFICE_NAME,
    val officeSubtitle: String = DEFAULT_OFFICE_SUBTITLE,
    val monogram: String = DEFAULT_MONOGRAM,
    val palette: OfficePalette = OfficePalette.Agbofa,
) {
    companion object {
        const val DEFAULT_OFFICE_NAME = "AGBOFA Smart Office"
        const val DEFAULT_OFFICE_SUBTITLE = "A calm view of your office operations."
        const val DEFAULT_MONOGRAM = "A"

        fun normalize(
            officeName: String,
            officeSubtitle: String,
            monogram: String,
            palette: OfficePalette,
        ): OfficePreferences {
            val name = officeName.trim().ifBlank { DEFAULT_OFFICE_NAME }
            val subtitle = officeSubtitle.trim().ifBlank { DEFAULT_OFFICE_SUBTITLE }
            val mark = monogram.trim().take(2).ifBlank { DEFAULT_MONOGRAM }
            return OfficePreferences(
                officeName = name,
                officeSubtitle = subtitle,
                monogram = mark,
                palette = palette,
            )
        }
    }
}

val LocalOfficePreferences = compositionLocalOf { OfficePreferences() }
