package com.agbofa.smartoffice.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.presentation.common.displayLabel
import com.agbofa.smartoffice.presentation.components.AgbofaEmptyState
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaPrimaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSecondaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaStatusPill
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.components.PillTone
import com.agbofa.smartoffice.presentation.theme.BrandMuted

@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRebuild: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = "Search",
            title = "Search your office",
            subtitle = "Find records, actions, and decisions.",
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            label = { Text("Search records, actions, decisions…") },
        )
        AgbofaPrimaryButton("Search", onClick = onSearch)
        AgbofaSecondaryButton("Refresh searchable office", onClick = onRebuild)
        if (state.message.isNotBlank()) {
            val friendly = if (state.message.startsWith("Index rebuilt")) "Office search is up to date." else state.message
            Text(friendly, style = MaterialTheme.typography.bodySmall, color = BrandMuted)
        }
        when {
            state.loading -> Text("Searching…", color = BrandMuted)
            state.emptyResults -> AgbofaEmptyState(
                title = "No matching records",
                body = "Try another phrase. Search uses the words already stored in your office.",
            )
            state.results.isEmpty() && state.query.isBlank() -> AgbofaEmptyState(
                title = "Search your office records",
                body = "Look across journal entries, operational records, and decisions.",
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.results, key = { "${it.type}:${it.entityId}" }) { hit ->
                    AgbofaSurfaceCard {
                        AgbofaStatusPill(hit.type.displayLabel(), PillTone.Authority)
                        Text(hit.content, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
