package com.agbofa.smartoffice.presentation.decision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.application.decision.DecisionListItem
import com.agbofa.smartoffice.domain.decision.DecisionStatus

@Composable
fun DecisionScreen(
    state: DecisionUiState,
    onRefresh: () -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onWithdraw: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Decisions", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onRefresh) { Text("Refresh") }
        if (state.message.isNotBlank()) Text(state.message)
        when {
            state.loading -> Text("Loading")
            state.error != null -> Text("Error: ${state.error}")
            state.empty -> Text("No decisions.")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.decision.id.value, style = MaterialTheme.typography.titleMedium)
            Text("Status: ${item.status}")
            Text(item.decision.rationale)
            if (item.status == DecisionStatus.PROPOSED) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onApprove(item.decision.id.value) }) { Text("Approve") }
                    OutlinedButton(onClick = { onReject(item.decision.id.value) }) { Text("Reject") }
                    OutlinedButton(onClick = { onWithdraw(item.decision.id.value) }) { Text("Withdraw") }
                }
            }
        }
    }
}
