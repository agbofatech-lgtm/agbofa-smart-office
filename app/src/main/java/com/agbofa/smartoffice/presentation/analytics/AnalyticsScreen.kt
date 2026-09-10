package com.agbofa.smartoffice.presentation.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.presentation.components.AgbofaEmptyState
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaSecondaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSectionHeader
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.theme.BrandMuted
import com.agbofa.smartoffice.presentation.theme.BrandPrimary

@Composable
fun AnalyticsScreen(state: AnalyticsUiState, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp)
            .semantics { contentDescription = "Office performance analytics" },
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = "Analytics",
            title = "Office performance",
            subtitle = "A live snapshot. It is derived, ephemeral, and not a second record of truth.",
        )
        AgbofaSecondaryButton("Refresh snapshot", onClick = onRefresh)
        when {
            state.loading -> Text("Building the snapshot…", color = BrandMuted)
            state.error != null -> Text("Unable to load analytics.", color = MaterialTheme.colorScheme.error)
            state.empty -> AgbofaEmptyState(
                title = "No activity to measure yet",
                body = "Analytics appear after operational records exist.",
            )
            state.report != null -> {
                val report = state.report
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgbofaSurfaceCard(Modifier.weight(1f)) {
                        Text(report.state.totalRecords.toString(), style = MaterialTheme.typography.headlineMedium, color = BrandPrimary)
                        Text("Records", style = MaterialTheme.typography.labelMedium, color = BrandMuted)
                    }
                    AgbofaSurfaceCard(Modifier.weight(1f)) {
                        Text(report.state.openCount.toString(), style = MaterialTheme.typography.headlineMedium, color = BrandPrimary)
                        Text("Open", style = MaterialTheme.typography.labelMedium, color = BrandMuted)
                    }
                }
                AgbofaSectionHeader("Activity")
                AgbofaSurfaceCard {
                    Text("Open ${report.state.openCount} · Active ${report.state.activeCount} · Completed ${report.state.completedCount}")
                    if (report.state.totalRecords > 0) {
                        LinearProgressIndicator(progress = { report.state.completedCount.toFloat() / report.state.totalRecords }, modifier = Modifier.fillMaxWidth())
                    }
                }
                AgbofaSectionHeader("Timing")
                AgbofaSurfaceCard {
                    Text("On track ${report.temporal.beforeDueCount} · Due now ${report.temporal.atDueCount} · Past due ${report.temporal.pastDueCount}")
                    Text("Open references ${report.temporal.unresolvedTemporalCount}", style = MaterialTheme.typography.bodySmall, color = BrandMuted)
                }
                AgbofaSectionHeader("Workflow and health")
                AgbofaSurfaceCard {
                    Text("Workflows ${report.workflow.recordsWithWorkflow} · Complete ${report.workflow.workflowCompleteCount}")
                    Text("Health warnings ${report.integrity.warningCount} · Issues ${report.integrity.errorCount}", style = MaterialTheme.typography.bodySmall, color = BrandMuted)
                }
            }
        }
    }
}
