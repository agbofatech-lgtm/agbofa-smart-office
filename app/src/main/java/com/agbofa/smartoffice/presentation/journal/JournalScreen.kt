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
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                )
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
                    items(
                        items = records,
                        key = { it.entryId.value },
                    ) { record ->
                        JournalRecordRow(
                            record = record,
                            pending = pendingType[record.entryId.value],
                            onTypeSelected = onTypeSelected,
                            onClassify = onClassify,
                            onCreateOperational = onCreateOperational,
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
                text = stringResource(R.string.operational_record_exists),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
