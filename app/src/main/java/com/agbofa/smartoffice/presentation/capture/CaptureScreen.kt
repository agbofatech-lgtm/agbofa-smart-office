package com.agbofa.smartoffice.presentation.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.R

/**
 * Narrow Phase 3 proof UI.
 *
 * Displays input, a capture action, and a result string supplied by
 * the caller. Contains no classification, finance, or journal logic.
 */
@Composable
fun CaptureScreen(
    expression: String,
    resultText: String,
    onExpressionChange: (String) -> Unit,
    onCapture: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.capture_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.capture_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = expression,
                onValueChange = onExpressionChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                label = { Text(stringResource(R.string.capture_field_label)) },
            )
            Button(
                onClick = onCapture,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.capture_action))
            }
            if (resultText.isNotEmpty()) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
