package com.agbofa.smartoffice.presentation.dashboard

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
import com.agbofa.smartoffice.domain.analytics.OperationalAnalyticsReport

@Composable
fun DashboardScreen(state: DashboardUiState, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onRefresh) { Text("Refresh") }
        when {
            state.loading -> Text("Loading")
            state.error != null -> Text("Error: ${state.error}")
            state.empty -> Text("No operational records.")
            else -> state.analytics?.let { AnalyticsCards(it, state.overviewCount) }
        }
    }
}

@Composable
fun AnalyticsCards(report: OperationalAnalyticsReport, overviewCount: Int) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Operational summary", style = MaterialTheme.typography.titleMedium)
            Text("Records: $overviewCount")
            Text("OPEN ${report.state.openCount} ACTIVE ${report.state.activeCount}")
            Text("COMPLETED ${report.state.completedCount} CANCELLED ${report.state.cancelledCount}")
        }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Temporal", style = MaterialTheme.typography.titleMedium)
            Text("Assigned ${report.temporal.recordsWithTemporalAssignment} unresolved ${report.temporal.unresolvedTemporalCount}")
            Text("Due ${report.temporal.beforeDueCount}/${report.temporal.atDueCount}/${report.temporal.pastDueCount}")
        }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Workflow", style = MaterialTheme.typography.titleMedium)
            Text("With ${report.workflow.recordsWithWorkflow} complete ${report.workflow.workflowCompleteCount} active ${report.workflow.workflowWithActiveStepCount}")
        }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Integrity", style = MaterialTheme.typography.titleMedium)
            Text("Healthy ${report.integrity.healthyCount} warn ${report.integrity.warningCount} error ${report.integrity.errorCount}")
        }
    }
}
