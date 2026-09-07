package com.agbofa.smartoffice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.data.capture.InMemoryCaptureRepository
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.presentation.capture.CaptureScreen
import com.agbofa.smartoffice.presentation.theme.SmartOfficeTheme
import java.time.Instant
import java.util.UUID

/**
 * Phase 3 host.
 *
 * Wires the capture use case. Does not classify, price, or schedule
 * the entered text. Identity and capture time are supplied at this
 * Android edge, not inside domain rules.
 */
class MainActivity : ComponentActivity() {
    private val captureExpression = CaptureExpressionUseCase(InMemoryCaptureRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var expression by remember { mutableStateOf("") }
            var resultText by remember { mutableStateOf("") }
            SmartOfficeTheme {
                CaptureScreen(
                    expression = expression,
                    resultText = resultText,
                    onExpressionChange = { expression = it },
                    onCapture = {
                        val result = captureExpression.execute(
                            idValue = UUID.randomUUID().toString(),
                            expression = expression,
                            capturedAt = CaptureInstant(Instant.now()),
                        )
                        resultText = when (result) {
                            is DomainResult.Success ->
                                "Captured: " + result.value.originalExpression.value
                            is DomainResult.Failure ->
                                result.error.message
                        }
                    },
                )
            }
        }
    }
}
