package com.agbofa.smartoffice.presentation.intelligence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.domain.intelligence.IntelligenceReport
import com.agbofa.smartoffice.presentation.components.AgbofaEmptyState
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaSecondaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSectionHeader
import com.agbofa.smartoffice.presentation.components.AgbofaStatusPill
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.components.PillTone
import com.agbofa.smartoffice.presentation.theme.BrandMuted

@Composable
fun IntelligenceScreen(
    state: IntelligenceUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .semantics { contentDescription = "Advisory intelligence" },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = "Advisory",
            title = "What deserves your attention?",
            subtitle = "Derived. Ephemeral. Not canonical. Not AI. A human still decides.",
        )
        AgbofaSecondaryButton("Refresh advisory", onRefresh)
        AgbofaStatusPill("Advisory only", PillTone.Neutral)
        when {
            state.loading -> Text("Deriving advisory view…", color = BrandMuted)
            state.error != null -> Text("Unable to derive advisory view.", color = MaterialTheme.colorScheme.error)
            state.report == null -> AgbofaEmptyState(
                title = "No advisory report",
                body = "Refresh after office records exist. Nothing is invented here.",
            )
            else -> ReportBody(state.report!!)
        }
    }
}

@Composable
private fun ReportBody(report: IntelligenceReport) {
    AgbofaSectionHeader("Summary")
    AgbofaSurfaceCard {
        Text("Recommendations ${report.summary.recommendationCount}  ·  Anomalies ${report.summary.anomalyCount}")
        Text("Critical ${report.summary.criticalCount}  ·  Prioritized ${report.summary.prioritizedCount}", color = BrandMuted)
    }
    AgbofaSectionHeader("Recommendations")
    if (report.recommendations.isEmpty()) {
        AgbofaEmptyState(title = "No recommendations", body = "Nothing advisory is waiting.")
    } else {
        report.recommendations.forEach { item ->
            AgbofaSurfaceCard {
                Text(item.type.name.replace('_', ' '), style = MaterialTheme.typography.titleSmall)
                Text(item.reason)
                Text("Target ${item.targetId ?: "—"}  ·  ${item.severity}", color = BrandMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    AgbofaSectionHeader("Anomalies")
    if (report.anomalies.isEmpty()) {
        AgbofaEmptyState(title = "No anomalies", body = "No derived irregularities in the current office data.")
    } else {
        report.anomalies.forEach { item ->
            AgbofaSurfaceCard {
                Text(item.type.name.replace('_', ' '), style = MaterialTheme.typography.titleSmall)
                Text(item.description)
                Text(item.evidence, color = BrandMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    AgbofaSectionHeader("Advisory priority")
    if (report.prioritized.isEmpty()) {
        AgbofaEmptyState(title = "No priority list", body = "Priority is advisory context, not a business fact.")
    } else {
        report.prioritized.forEachIndexed { index, item ->
            AgbofaSurfaceCard {
                Text("#${index + 1}  ${item.targetId}", style = MaterialTheme.typography.titleSmall)
                Text(item.explanation)
                Text("Score ${item.score}", color = BrandMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
