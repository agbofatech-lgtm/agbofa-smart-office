package com.agbofa.smartoffice.presentation.dashboard

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.domain.analytics.OperationalAnalyticsReport
import com.agbofa.smartoffice.presentation.components.AgbofaEmptyState
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaSecondaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSectionHeader
import com.agbofa.smartoffice.presentation.components.AgbofaStatusPill
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.components.PillTone
import com.agbofa.smartoffice.presentation.settings.LocalOfficePreferences
import com.agbofa.smartoffice.presentation.theme.BrandMuted
import com.agbofa.smartoffice.presentation.theme.BrandPrimary

@Composable
fun DashboardScreen(state: DashboardUiState, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    val prefs = LocalOfficePreferences.current
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp)
            .semantics { contentDescription = prefs.officeName },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = prefs.monogram,
            title = prefs.officeName,
            subtitle = prefs.officeSubtitle,
        )
        AgbofaSecondaryButton(text = "Refresh office", onClick = onRefresh)
        when {
            state.loading -> Text("Gathering the office view…", color = BrandMuted)
            state.error != null -> Text("Unable to load the office view.", color = MaterialTheme.colorScheme.error)
            state.empty -> AgbofaEmptyState(
                title = "Your command center is ready",
                body = "Capture a journal entry to see operational activity here. Nothing is invented to fill this view.",
            )
            else -> state.analytics?.let { CommandCenter(it, state.overviewCount) }
        }
    }
}

@Composable
private fun CommandCenter(report: OperationalAnalyticsReport, overviewCount: Int) {
    val attention = report.temporal.pastDueCount + report.temporal.atDueCount +
        report.integrity.warningCount + report.integrity.errorCount
    AgbofaSectionHeader("Attention", "Only counts from live operational data.")
    AgbofaSurfaceCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Needs a look", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (attention == 0) "Nothing urgent is waiting." else "$attention item(s) from dues and office health.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandMuted,
                )
            }
            AgbofaStatusPill(
                text = if (attention == 0) "Clear" else "Review",
                tone = if (attention == 0) PillTone.Positive else PillTone.Attention,
            )
        }
        Text(
            "Past due ${report.temporal.pastDueCount}  ·  Due now ${report.temporal.atDueCount}",
            color = BrandMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
    AgbofaSectionHeader("Overview")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Metric("Records", overviewCount.toString(), Modifier.weight(1f))
        Metric("Active", report.state.activeCount.toString(), Modifier.weight(1f))
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Metric("Completed", report.state.completedCount.toString(), Modifier.weight(1f))
        Metric("Needs attention", attention.toString(), Modifier.weight(1f))
    }
    AgbofaSectionHeader("Workflow")
    AgbofaSurfaceCard {
        val total = report.workflow.recordsWithWorkflow
        Text("Work in motion", style = MaterialTheme.typography.titleMedium)
        Text(
            if (total == 0) "No workflows are attached yet."
            else "${report.workflow.workflowCompleteCount} complete of $total · ${report.workflow.workflowWithActiveStepCount} active",
            style = MaterialTheme.typography.bodyMedium,
            color = BrandMuted,
        )
        if (total > 0) {
            LinearProgressIndicator(
                progress = { report.workflow.workflowCompleteCount.toFloat() / total.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    AgbofaSectionHeader("Office health")
    val healthy = report.integrity.errorCount == 0 && report.integrity.warningCount == 0
    AgbofaSurfaceCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(if (healthy) "Healthy" else "Needs review", style = MaterialTheme.typography.titleMedium)
                Text("Integrity is advisory context, not a scoreboard.", style = MaterialTheme.typography.bodySmall, color = BrandMuted)
            }
            AgbofaStatusPill(
                text = if (healthy) "Healthy" else "Review",
                tone = if (healthy) PillTone.Positive else PillTone.Critical,
            )
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    AgbofaSurfaceCard(modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = BrandPrimary)
        Text(label, style = MaterialTheme.typography.labelMedium, color = BrandMuted)
    }
}
