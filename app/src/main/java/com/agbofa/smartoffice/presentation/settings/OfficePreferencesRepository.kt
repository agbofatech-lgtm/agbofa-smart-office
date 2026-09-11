package com.agbofa.smartoffice.presentation.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.officeSettingsDataStore by preferencesDataStore(name = "office_settings")

class OfficePreferencesRepository(context: Context) {
    private val dataStore = context.applicationContext.officeSettingsDataStore

    val preferences: Flow<OfficePreferences> = dataStore.data.map { prefs ->
        OfficePreferences.normalize(
            officeName = prefs[KEY_NAME].orEmpty(),
            officeSubtitle = prefs[KEY_SUBTITLE].orEmpty(),
            monogram = prefs[KEY_MONOGRAM].orEmpty(),
            palette = runCatching {
                OfficePalette.valueOf(prefs[KEY_PALETTE] ?: OfficePalette.Agbofa.name)
            }.getOrDefault(OfficePalette.Agbofa),
        )
    }

    suspend fun update(
        officeName: String,
        officeSubtitle: String,
        monogram: String,
        palette: OfficePalette,
    ) {
        val normalized = OfficePreferences.normalize(officeName, officeSubtitle, monogram, palette)
        dataStore.edit { prefs ->
            prefs[KEY_NAME] = normalized.officeName
            prefs[KEY_SUBTITLE] = normalized.officeSubtitle
            prefs[KEY_MONOGRAM] = normalized.monogram
            prefs[KEY_PALETTE] = normalized.palette.name
        }
    }

    private companion object {
        val KEY_NAME = stringPreferencesKey("office_name")
        val KEY_SUBTITLE = stringPreferencesKey("office_subtitle")
        val KEY_MONOGRAM = stringPreferencesKey("office_monogram")
        val KEY_PALETTE = stringPreferencesKey("office_palette")
    }
}
