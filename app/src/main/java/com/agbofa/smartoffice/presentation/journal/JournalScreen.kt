package com.agbofa.smartoffice.presentation.journal

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.R
import com.agbofa.smartoffice.application.journal.JournalRecord
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStatePolicy
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.presentation.components.AgbofaEmptyState
import com.agbofa.smartoffice.presentation.components.AgbofaPageHeader
import com.agbofa.smartoffice.presentation.components.AgbofaPrimaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaSecondaryButton
import com.agbofa.smartoffice.presentation.components.AgbofaStatusPill
import com.agbofa.smartoffice.presentation.components.AgbofaSurfaceCard
import com.agbofa.smartoffice.presentation.components.PillTone
import com.agbofa.smartoffice.presentation.theme.BrandMuted

@Composable
fun JournalScreen(
    expression: String,
    message: String,
    records: List<JournalRecord>,
    pendingType: Map<String, ClassificationType>,
    onExpressionChange: (String) -> Unit,
    onCapture: () -> Unit,
    onTypeSelected: (String, ClassificationType) -> Unit,
    onClassify: (String) -> Unit,
    onCreateOperational: (String) -> Unit,
    onTransitionState: (String, OperationalState) -> Unit,
    temporals: Map<String, OperationalTemporalRecord> = emptyMap(),
    dueStatuses: Map<String, DueStatus> = emptyMap(),
    prerequisiteLabels: Map<String, String> = emptyMap(),
    dueDrafts: Map<String, String> = emptyMap(),
    referenceDrafts: Map<String, String> = emptyMap(),
    prerequisiteDrafts: Map<String, String> = emptyMap(),
    onDueDraftChange: (String, String) -> Unit = { _, _ -> },
    onReferenceDraftChange: (String, String) -> Unit = { _, _ -> },
    onPrerequisiteDraftChange: (String, String) -> Unit = { _, _ -> },
    onAssignDue: (String) -> Unit = {},
    onAssignUnresolved: (String) -> Unit = {},
    onEvaluateDue: (String) -> Unit = {},
    onCreateDependency: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp)
            .semantics { contentDescription = "Journal capture workspace" },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AgbofaPageHeader(
            eyebrow = "Journal",
            title = "Capture what matters.",
            subtitle = "Operational information enters the office here.",
        )
        OutlinedTextField(
            value = expression,
            onValueChange = onExpressionChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            label = { Text(stringResource(R.string.capture_field_label)) },
        )
        AgbofaPrimaryButton(stringResource(R.string.capture_action), onCapture)
        if (message.isNotEmpty()) Text(message, style = MaterialTheme.typography.bodyMedium, color = BrandMuted)
        if (records.isEmpty()) {
            AgbofaEmptyState(
                title = stringResource(R.string.journal_empty),
                body = "Write an entry and capture it. Records stay empty until you do.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(records, key = { it.entryId.value }) { record ->
                    JournalRecordRow(
                        record = record,
                        pending = pendingType[record.entryId.value],
                        onTypeSelected = onTypeSelected,
                        onClassify = onClassify,
                        onCreateOperational = onCreateOperational,
                        onTransitionState = onTransitionState,
                        temporal = record.operationalRecordId?.let { temporals[it.value] },
                        dueStatus = record.operationalRecordId?.let { dueStatuses[it.value] },
                        prerequisiteLabel = record.operationalRecordId?.let { prerequisiteLabels[it.value] }.orEmpty(),
                        dueDraft = record.operationalRecordId?.let { dueDrafts[it.value] }.orEmpty(),
                        referenceDraft = record.operationalRecordId?.let { referenceDrafts[it.value] }.orEmpty(),
                        prerequisiteDraft = record.operationalRecordId?.let { prerequisiteDrafts[it.value] }.orEmpty(),
                        onDueDraftChange = onDueDraftChange,
                        onReferenceDraftChange = onReferenceDraftChange,
                        onPrerequisiteDraftChange = onPrerequisiteDraftChange,
                        onAssignDue = onAssignDue,
                        onAssignUnresolved = onAssignUnresolved,
                        onEvaluateDue = onEvaluateDue,
                        onCreateDependency = onCreateDependency,
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalRecordRow(
    record: JournalRecord,
    pending: ClassificationType?,
    onTypeSelected: (String, ClassificationType) -> Unit,
    onClassify: (String) -> Unit,
    onCreateOperational: (String) -> Unit,
    onTransitionState: (String, OperationalState) -> Unit,
    temporal: OperationalTemporalRecord?,
    dueStatus: DueStatus?,
    prerequisiteLabel: String,
    dueDraft: String,
    referenceDraft: String,
    prerequisiteDraft: String,
    onDueDraftChange: (String, String) -> Unit,
    onReferenceDraftChange: (String, String) -> Unit,
    onPrerequisiteDraftChange: (String, String) -> Unit,
    onAssignDue: (String) -> Unit,
    onAssignUnresolved: (String) -> Unit,
    onEvaluateDue: (String) -> Unit,
    onCreateDependency: (String) -> Unit,
) {
    val selectable = ClassificationType.entries.filter { it != ClassificationType.UNCLASSIFIED }
    AgbofaSurfaceCard {
        Text(record.originalExpression.value, style = MaterialTheme.typography.bodyLarge)
        AgbofaStatusPill(record.classificationType.name.replace('_', ' '), PillTone.Authority)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            selectable.forEach { type ->
                FilterChip(
                    selected = pending == type,
                    onClick = { onTypeSelected(record.entryId.value, type) },
                    label = { Text(type.name.replace('_', ' ')) },
                )
            }
        }
        AgbofaPrimaryButton(stringResource(R.string.classify_action), { onClassify(record.entryId.value) }, enabled = pending != null)
        if (record.classificationType != ClassificationType.UNCLASSIFIED && !record.operationalRecordExists) {
            AgbofaSecondaryButton(stringResource(R.string.create_operational_record), { onCreateOperational(record.entryId.value) })
        }
        if (record.operationalRecordExists) {
            val recordId = record.operationalRecordId?.value
            val current = record.operationalState ?: OperationalState.OPEN
            Text(
                stringResource(R.string.operational_state_label, current.name),
                style = MaterialTheme.typography.bodySmall,
                color = BrandMuted,
            )
            if (recordId != null) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OperationalStatePolicy.successors(current).forEach { target ->
                        FilterChip(selected = false, onClick = { onTransitionState(recordId, target) }, label = { Text(target.name) })
                    }
                }
                Text(
                    temporal?.let {
                        if (it.dueInstant != null) "Due ${it.dueInstant.value}" else "Unresolved ${it.referenceExpression}"
                    } ?: "No temporal assignment",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandMuted,
                )
                if (dueStatus != null) AgbofaStatusPill(dueStatus.name.replace('_', ' '), PillTone.Attention)
                OutlinedTextField(value = dueDraft, onValueChange = { onDueDraftChange(recordId, it) }, modifier = Modifier.fillMaxWidth(), label = { Text("Due date") })
                AgbofaSecondaryButton("Assign due date", { onAssignDue(recordId) })
                AgbofaSecondaryButton("Evaluate due date", { onEvaluateDue(recordId) })
                OutlinedTextField(value = referenceDraft, onValueChange = { onReferenceDraftChange(recordId, it) }, modifier = Modifier.fillMaxWidth(), label = { Text("Unresolved reference") })
                AgbofaSecondaryButton("Store unresolved time", { onAssignUnresolved(recordId) })
                if (prerequisiteLabel.isNotEmpty()) Text("Requires $prerequisiteLabel", style = MaterialTheme.typography.bodySmall, color = BrandMuted)
                OutlinedTextField(value = prerequisiteDraft, onValueChange = { onPrerequisiteDraftChange(recordId, it) }, modifier = Modifier.fillMaxWidth(), label = { Text("Requires operational id") })
                AgbofaSecondaryButton("Create dependency", { onCreateDependency(recordId) })
            }
        }
    }
}
