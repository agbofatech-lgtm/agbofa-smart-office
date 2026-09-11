package com.agbofa.smartoffice.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.BuildConfig
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaPrimaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSectionHeader
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.theme.BrandMuted

@Composable
fun SettingsScreen(
    draft: OfficePreferences,
    status: String,
    onOfficeNameChange: (String) -> Unit,
    onOfficeSubtitleChange: (String) -> Unit,
    onMonogramChange: (String) -> Unit,
    onPaletteChange: (OfficePalette) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .semantics { contentDescription = "Office personalization settings" },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = "Settings",
            title = "Office Personalization",
            subtitle = "These values stay on this device. They change presentation, not operational records.",
        )
        AgbofaSectionHeader("Identity")
        AgbofaSurfaceCard {
            OutlinedTextField(
                value = draft.officeName,
                onValueChange = onOfficeNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Office Name") },
            )
            OutlinedTextField(
                value = draft.officeSubtitle,
                onValueChange = onOfficeSubtitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Office Subtitle") },
            )
            OutlinedTextField(
                value = draft.monogram,
                onValueChange = onMonogramChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Monogram") },
                supportingText = { Text("Maximum 2 characters") },
            )
        }
        AgbofaSectionHeader("Palette")
        AgbofaSurfaceCard {
            OfficePalette.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = draft.palette == option,
                            onClick = { onPaletteChange(option) },
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RadioButton(
                        selected = draft.palette == option,
                        onClick = { onPaletteChange(option) },
                    )
                    Text(option.name)
                }
            }
        }
        AgbofaPrimaryButton("Save", onSave)
        if (status.isNotEmpty()) {
            Text(status, color = BrandMuted, style = MaterialTheme.typography.bodyMedium)
        }
        AgbofaSectionHeader("In-app manual")
        AgbofaSurfaceCard {
            Text(
                "Journal is where office information is captured.\n" +
                    "Classification labels a captured record. It does not invent work.\n" +
                    "Decisions stay with a human. The app does not approve or decline on its own.\n" +
                    "Search rebuilds from existing records. It is not a second source of truth.\n" +
                    "Analytics are derived and ephemeral. They are not stored as facts.\n" +
                    "Intelligence is advisory and deterministic. It is not AI.\n" +
                    "Settings are stored only on this device.\n" +
                    "Personalization changes names and colors, not operational truth.\n" +
                    "Room records remain the office history.\n" +
                    "Nothing here contacts a network or cloud.",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandMuted,
            )
        }
        AgbofaSectionHeader("About")
        AgbofaSurfaceCard {
            Text(draft.officeName.ifBlank { OfficePreferences.DEFAULT_OFFICE_NAME })
            Text("Version ${BuildConfig.VERSION_NAME}", color = BrandMuted)
            Text("Local only. No cloud sync.", color = BrandMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
