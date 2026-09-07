package com.agbofa.smartoffice.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRebuild: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Search", style = MaterialTheme.typography.headlineSmall)
        Text("Deterministic substring search over a rebuildable derived index.")
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Query") },
        )
        Button(onClick = onSearch, modifier = Modifier.fillMaxWidth()) { Text("Search") }
        OutlinedButton(onClick = onRebuild, modifier = Modifier.fillMaxWidth()) { Text("Rebuild index") }
        if (state.message.isNotBlank()) Text(state.message)
        when {
            state.loading -> Text("Loading")
            state.emptyResults -> Text("No matches")
            else -> LazyColumn {
                items(state.results, key = { "${it.type}:${it.entityId}" }) { hit ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Text("${hit.type} ${hit.entityId}", style = MaterialTheme.typography.titleSmall)
                        Text(hit.content)
                    }
                }
            }
        }
    }
}
