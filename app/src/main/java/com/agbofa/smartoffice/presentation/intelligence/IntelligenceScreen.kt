package com.agbofa.smartoffice.presentation.intelligence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.domain.intelligence.IntelligenceReport

@Composable
fun IntelligenceScreen(
    state: IntelligenceUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Intelligence advisory", style = MaterialTheme.typography.headlineSmall)
        Text("Derived. Ephemeral. Not canonical. Not AI.")
        Button(onClick = onRefresh) { Text("Refresh") }
        when {
            state.loading -> Text("Loading")
            state.error != null -> Text("Error: ${state.error}")
            state.report == null -> Text("No advisory report.")
            else -> ReportBody(state.report!!)
        }
    }
}

@Composable
private fun ReportBody(report: IntelligenceReport) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Summary", style = MaterialTheme.typography.titleMedium)
            Text("Recommendations ${report.summary.recommendationCount}")
            Text("Anomalies ${report.summary.anomalyCount}")
            Text("Critical ${report.summary.criticalCount}")
            Text("Advisory priority items ${report.summary.prioritizedCount}")
        }
    }
    Text("Recommendations", style = MaterialTheme.typography.titleMedium)
    if (report.recommendations.isEmpty()) Text("None.")
    report.recommendations.forEach { item ->
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text(item.type.name)
                Text("Target: ${item.targetId ?: "-"}")
                Text(item.reason)
                Text("Source: ${item.source}  Severity: ${item.severity}")
            }
        }
    }
    Text("Anomalies", style = MaterialTheme.typography.titleMedium)
    if (report.anomalies.isEmpty()) Text("None.")
    report.anomalies.forEach { item ->
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text(item.type.name)
                Text("Target: ${item.targetId ?: "-"}")
                Text(item.description)
                Text("Evidence: ${item.evidence}")
            }
        }
    }
    Text("Advisory priority (not a business fact)", style = MaterialTheme.typography.titleMedium)
    if (report.prioritized.isEmpty()) Text("None.")
    report.prioritized.forEachIndexed { index, item ->
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("#${index + 1} ${item.targetId} score ${item.score}")
                Text(item.explanation)
            }
        }
    }
}
