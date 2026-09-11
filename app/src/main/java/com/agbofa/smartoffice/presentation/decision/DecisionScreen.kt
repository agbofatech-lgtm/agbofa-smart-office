package com.agbofa.smartoffice.presentation.decision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.application.decision.DecisionListItem
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.presentation.components.AgbofaEmptyState
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaPrimaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSecondaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaStatusPill
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.components.PillTone
import com.agbofa.smartoffice.presentation.labels.asLabel
import com.agbofa.smartoffice.presentation.theme.BrandMuted

@Composable
fun DecisionScreen(
    state: DecisionUiState,
    onRefresh: () -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onWithdraw: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)
            .semantics { contentDescription = "Decisions workspace" },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = "Decisions",
            title = "Where the human decides.",
            subtitle = "Approve, decline, or withdraw. The office never decides for you.",
        )
        AgbofaSecondaryButton(text = "Refresh decisions", onClick = onRefresh)
        if (state.message.isNotBlank()) Text(state.message, color = BrandMuted)
        when {
            state.loading -> Text("Loading decisions…", color = BrandMuted)
            state.error != null -> Text("Unable to load decisions.", color = MaterialTheme.colorScheme.error)
            state.empty -> AgbofaEmptyState(
                title = "No decisions waiting",
                body = "When a proposed decision exists, it appears here for a human action.",
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(state.items, key = { it.decision.id.value }) { item ->
                    DecisionCard(item, onApprove, onReject, onWithdraw)
                }
            }
        }
    }
}

@Composable
private fun DecisionCard(
    item: DecisionListItem,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onWithdraw: (String) -> Unit,
) {
    val id = item.decision.id.value
    AgbofaSurfaceCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Decision", style = MaterialTheme.typography.titleMedium)
            AgbofaStatusPill(
                text = item.status.asLabel(),
                tone = when (item.status) {
                    DecisionStatus.PROPOSED -> PillTone.Authority
                    DecisionStatus.APPROVED -> PillTone.Positive
                    DecisionStatus.REJECTED -> PillTone.Critical
                    DecisionStatus.WITHDRAWN -> PillTone.Neutral
                },
            )
        }
        Text(item.decision.rationale, style = MaterialTheme.typography.bodyMedium)
        Text(id, style = MaterialTheme.typography.bodySmall, color = BrandMuted)
        if (item.status == DecisionStatus.PROPOSED) {
            AgbofaPrimaryButton(text = "Approve", onClick = { onApprove(id) })
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AgbofaSecondaryButton(text = "Decline", onClick = { onReject(id) }, modifier = Modifier.weight(1f))
                AgbofaSecondaryButton(text = "Withdraw", onClick = { onWithdraw(id) }, modifier = Modifier.weight(1f))
            }
        }
    }
}
