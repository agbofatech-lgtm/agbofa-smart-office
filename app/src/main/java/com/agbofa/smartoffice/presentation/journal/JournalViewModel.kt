package com.agbofa.smartoffice.presentation.journal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.application.journal.JournalRecord
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import java.time.Instant

/**
 * Coordinates capture and journal use cases.
 *
 * Does not classify expressions.
 */
class JournalViewModel(
    private val captureExpression: CaptureExpressionUseCase,
    private val admitCapture: AdmitCaptureToJournalUseCase,
    private val journalTimeline: GetJournalTimelineUseCase,
) : ViewModel() {
    var expression by mutableStateOf("")
        private set
    var message by mutableStateOf("")
        private set
    var records by mutableStateOf<List<JournalRecord>>(emptyList())
        private set
    private var nextSequence by mutableIntStateOf(1)

    fun onExpressionChange(value: String) {
        expression = value
    }

    fun captureAndAdmit() {
        val sequence = nextSequence
        nextSequence += 1
        val captureId = "capture-$sequence"
        val journalId = "journal-$sequence"
        val now = Instant.now()
        when (
            val captured = captureExpression.execute(
                idValue = captureId,
                expression = expression,
                capturedAt = CaptureInstant(now),
            )
        ) {
            is DomainResult.Failure -> {
                message = captured.error.message
                return
            }
            is DomainResult.Success -> Unit
        }
        when (
            val admitted = admitCapture.execute(
                journalEntryId = journalId,
                captureIdValue = captureId,
                admittedAt = JournalAdmissionInstant(now),
            )
        ) {
            is DomainResult.Failure -> {
                message = admitted.error.message
            }
            is DomainResult.Success -> {
                message = "Admitted"
                expression = ""
            }
        }
        records = journalTimeline.execute()
    }

    fun refresh() {
        records = journalTimeline.execute()
    }
}
