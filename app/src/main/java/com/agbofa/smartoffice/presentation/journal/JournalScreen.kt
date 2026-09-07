package com.agbofa.smartoffice.presentation.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.R
import com.agbofa.smartoffice.application.journal.JournalRecord
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalStatePolicy
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.DueStatus

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
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.journal_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                value = expression,
                onValueChange = onExpressionChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                label = { Text(stringResource(R.string.capture_field_label)) },
            )
            Button(
                onClick = onCapture,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.capture_action))
            }
            if (message.isNotEmpty()) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
            if (records.isEmpty()) {
                Text(
                    text = stringResource(R.string.journal_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}

@Composable
private fun JournalRecordRow(
    record: JournalRecord,
    pending: ClassificationType?,
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
) {
    val selectable = ClassificationType.entries.filter { it != ClassificationType.UNCLASSIFIED }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = record.originalExpression.value,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(
                R.string.journal_classification_label,
                record.classificationType.name,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        selectable.forEach { type ->
            OutlinedButton(onClick = { onTypeSelected(record.entryId.value, type) }) {
                Text(type.name)
            }
        }
        Button(
            onClick = { onClassify(record.entryId.value) },
            enabled = pending != null,
        ) {
            Text(stringResource(R.string.classify_action))
        }
        if (record.classificationType != ClassificationType.UNCLASSIFIED &&
            !record.operationalRecordExists
        ) {
            Button(onClick = { onCreateOperational(record.entryId.value) }) {
                Text(stringResource(R.string.create_operational_record))
            }
        }
        if (record.operationalRecordExists) {
            Text(
                text = stringResource(
                    R.string.operational_state_label,
                    record.operationalState?.name ?: OperationalState.OPEN.name,
                ),
                style = MaterialTheme.typography.bodySmall,
            )
            val recordId = record.operationalRecordId?.value
            val current = record.operationalState ?: OperationalState.OPEN
            if (recordId != null) {
                OperationalStatePolicy.successors(current).forEach { target ->
                    OutlinedButton(onClick = { onTransitionState(recordId, target) }) {
                        Text(target.name)
                    }
                }
                Text(
                    text = temporal?.let {
                        if (it.dueInstant != null) "Due ${it.dueInstant.value}" else "Unresolved ${it.referenceExpression}"
                    } ?: "No temporal assignment",
                    style = MaterialTheme.typography.bodySmall,
                )
                if (dueStatus != null) {
                    Text(text = dueStatus.name, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = dueDraft,
                    onValueChange = { onDueDraftChange(recordId, it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Due ISO-8601") },
                )
                Button(onClick = { onAssignDue(recordId) }) { Text("Assign Due") }
                Button(onClick = { onEvaluateDue(recordId) }) { Text("Evaluate Due") }
                OutlinedTextField(
                    value = referenceDraft,
                    onValueChange = { onReferenceDraftChange(recordId, it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Unresolved reference") },
                )
                Button(onClick = { onAssignUnresolved(recordId) }) { Text("Store Unresolved") }
                if (prerequisiteLabel.isNotEmpty()) {
                    Text(
                        text = "Requires $prerequisiteLabel",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                OutlinedTextField(
                    value = prerequisiteDraft,
                    onValueChange = { onPrerequisiteDraftChange(recordId, it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Requires operational id") },
                )
                Button(onClick = { onCreateDependency(recordId) }) { Text("Create Dependency") }
            }
        }
    }
}
