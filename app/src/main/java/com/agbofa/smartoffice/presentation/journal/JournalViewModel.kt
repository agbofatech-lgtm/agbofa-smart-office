package com.agbofa.smartoffice.presentation.journal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.classification.GetActiveClassificationUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.application.journal.JournalRecord
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import java.time.Instant

/**
 * Coordinates capture, journal, and classification use cases.
 *
 * Does not parse text, create tasks, or create finance records.
 */
class JournalViewModel(
    private val captureExpression: CaptureExpressionUseCase,
    private val admitCapture: AdmitCaptureToJournalUseCase,
    private val journalTimeline: GetJournalTimelineUseCase,
    private val classifyJournalEntry: ClassifyJournalEntryUseCase,
    private val getActiveClassification: GetActiveClassificationUseCase,
) : ViewModel() {
    var expression by mutableStateOf("")
        private set
    var message by mutableStateOf("")
        private set
    var records by mutableStateOf<List<JournalRecord>>(emptyList())
        private set
    var classifications by mutableStateOf<Map<String, Classification>>(emptyMap())
        private set
    var draftTypes by mutableStateOf<Map<String, ClassificationType>>(emptyMap())
        private set
    val pendingType: Map<String, ClassificationType> get() = draftTypes
    private var nextSequence by mutableIntStateOf(1)
    private var nextClassification by mutableIntStateOf(1)

    fun onExpressionChange(value: String) {
        expression = value
    }

    fun onDraftTypeSelected(journalEntryId: String, type: ClassificationType) {
        draftTypes = draftTypes + (journalEntryId to type)
    }

    fun onTypeSelected(journalEntryId: String, type: ClassificationType) {
        onDraftTypeSelected(journalEntryId, type)
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
        refresh()
    }

    fun classify(journalEntryId: String) {
        val type = draftTypes[journalEntryId] ?: return
        val classificationId = "cls-$nextClassification"
        nextClassification += 1
        val result = classifyJournalEntry.execute(
            classificationId = classificationId,
            journalEntryIdValue = journalEntryId,
            type = type,
            basis = ClassificationBasis.MANUAL,
            classifiedAt = ClassificationInstant(Instant.now()),
        )
        message = when (result) {
            is DomainResult.Success -> "Classified as ${result.value.type.name}"
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun refresh() {
        records = journalTimeline.execute()
        classifications = records.mapNotNull { record ->
            getActiveClassification.execute(record.entryId)?.let { record.entryId.value to it }
        }.toMap()
    }
}
