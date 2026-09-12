package com.agbofa.smartoffice.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: OfficePreferencesRepository,
) : ViewModel() {
    var draft by mutableStateOf(OfficePreferences())
        private set
    var status by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            draft = repository.preferences.first()
        }
    }

    fun onOfficeNameChange(value: String) {
        draft = draft.copy(officeName = value)
        status = ""
    }

    fun onOfficeSubtitleChange(value: String) {
        draft = draft.copy(officeSubtitle = value)
        status = ""
    }

    fun onMonogramChange(value: String) {
        draft = draft.copy(monogram = value.take(2))
        status = ""
    }

    fun onPaletteChange(value: OfficePalette) {
        draft = draft.copy(palette = value)
        status = ""
    }

    fun save() {
        viewModelScope.launch {
            val normalized = OfficePreferences.normalize(
                officeName = draft.officeName,
                officeSubtitle = draft.officeSubtitle,
                monogram = draft.monogram,
                palette = draft.palette,
            )
            repository.update(
                officeName = normalized.officeName,
                officeSubtitle = normalized.officeSubtitle,
                monogram = normalized.monogram,
                palette = normalized.palette,
            )
            draft = normalized
            status = "Saved on this device"
        }
    }
}
