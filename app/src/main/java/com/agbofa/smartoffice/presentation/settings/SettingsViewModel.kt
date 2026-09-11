package com.agbofa.smartoffice.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            repository.preferences.collect { draft = it }
        }
    }

    fun onOfficeNameChange(value: String) {
        draft = draft.copy(officeName = value)
    }

    fun onOfficeSubtitleChange(value: String) {
        draft = draft.copy(officeSubtitle = value)
    }

    fun onMonogramChange(value: String) {
        draft = draft.copy(monogram = value.take(2))
    }

    fun onPaletteChange(value: OfficePalette) {
        draft = draft.copy(palette = value)
    }

    fun save() {
        viewModelScope.launch {
            repository.update(
                officeName = draft.officeName,
                officeSubtitle = draft.officeSubtitle,
                monogram = draft.monogram,
                palette = draft.palette,
            )
            status = "Saved on this device"
        }
    }
}
