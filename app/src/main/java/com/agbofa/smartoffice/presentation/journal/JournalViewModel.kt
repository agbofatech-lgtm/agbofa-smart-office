package com.agbofa.smartoffice.presentation.journal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.classification.GetActiveClassificationUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.application.journal.JournalRecord
import com.agbofa.smartoffice.application.operations.AssignOperationalTemporalUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalDependencyUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalRecordUseCase
import com.agbofa.smartoffice.application.operations.EvaluateDueStatusUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalPrerequisitesUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalTemporalUseCase
import com.agbofa.smartoffice.application.operations.TransitionOperationalRecordStateUseCase
import com.agbofa.smartoffice.domain.foundation.time.OperationalTransitionInstant
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.operations.OperationalTransitionBasis
import com.agbofa.smartoffice.domain.classification.Classification
import com.agbofa.smartoffice.domain.classification.ClassificationBasis
import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.CaptureInstant
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.foundation.time.JournalAdmissionInstant
import com.agbofa.smartoffice.domain.foundation.time.DueInstant
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.OperationalDependencyCreationInstant
import com.agbofa.smartoffice.domain.foundation.time.TemporalAssignmentInstant
import com.agbofa.smartoffice.domain.operations.OperationalTemporalRecord
import com.agbofa.smartoffice.domain.operations.TemporalResolution
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalCreationBasis
import java.time.Instant
import java.util.UUID

/**
 * Coordinates capture, journal, classification, and explicit operationalization.
 *
 * Does not parse text, create tasks, or create finance records.
 */
class JournalViewModel(
    private val captureExpression: CaptureExpressionUseCase,
    private val admitCapture: AdmitCaptureToJournalUseCase,
    private val journalTimeline: GetJournalTimelineUseCase,
    private val classifyJournalEntry: ClassifyJournalEntryUseCase,
    private val getActiveClassification: GetActiveClassificationUseCase,
    private val createOperationalRecord: CreateOperationalRecordUseCase,
    private val transitionOperationalRecordState: TransitionOperationalRecordStateUseCase,
    private val assignOperationalTemporal: AssignOperationalTemporalUseCase,
    private val getOperationalTemporal: GetOperationalTemporalUseCase,
    private val evaluateDueStatus: EvaluateDueStatusUseCase,
    private val createOperationalDependency: CreateOperationalDependencyUseCase,
    private val getOperationalPrerequisites: GetOperationalPrerequisitesUseCase,
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
    var dueDrafts by mutableStateOf<Map<String, String>>(emptyMap())
        private set
    var referenceDrafts by mutableStateOf<Map<String, String>>(emptyMap())
        private set
    var prerequisiteDrafts by mutableStateOf<Map<String, String>>(emptyMap())
        private set
    var temporals by mutableStateOf<Map<String, OperationalTemporalRecord>>(emptyMap())
        private set
    var dueStatuses by mutableStateOf<Map<String, DueStatus>>(emptyMap())
        private set
    var prerequisiteLabels by mutableStateOf<Map<String, String>>(emptyMap())
        private set

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
        val captureId = "capture-${UUID.randomUUID()}"
        val journalId = "journal-${UUID.randomUUID()}"
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
            is DomainResult.Failure -> message = admitted.error.message
            is DomainResult.Success -> {
                message = "Admitted"
                expression = ""
            }
        }
        refresh()
    }

    fun classify(journalEntryId: String) {
        val type = draftTypes[journalEntryId] ?: return
        val classificationId = "cls-${UUID.randomUUID()}"
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

    fun createOperational(journalEntryId: String) {
        val id = "op-${UUID.randomUUID()}"
        val result = createOperationalRecord.execute(
            operationalRecordId = id,
            journalEntryIdValue = journalEntryId,
            createdAt = OperationalCreationInstant(Instant.now()),
            creationBasis = OperationalCreationBasis.MANUAL,
        )
        message = when (result) {
            is DomainResult.Success -> "Operational record created"
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun transitionState(operationalRecordId: String, toState: OperationalState) {
        val result = transitionOperationalRecordState.execute(
            transitionId = "st-${UUID.randomUUID()}",
            operationalRecordIdValue = operationalRecordId,
            toState = toState,
            transitionedAt = OperationalTransitionInstant(Instant.now()),
            basis = OperationalTransitionBasis.MANUAL,
        )
        message = when (result) {
            is DomainResult.Success -> "State ${result.value.fromState} → ${result.value.toState}"
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun onDueDraftChange(operationalRecordId: String, value: String) {
        dueDrafts = dueDrafts + (operationalRecordId to value)
    }

    fun onReferenceDraftChange(operationalRecordId: String, value: String) {
        referenceDrafts = referenceDrafts + (operationalRecordId to value)
    }

    fun onPrerequisiteDraftChange(operationalRecordId: String, value: String) {
        prerequisiteDrafts = prerequisiteDrafts + (operationalRecordId to value)
    }

    fun assignDue(operationalRecordId: String) {
        val raw = dueDrafts[operationalRecordId].orEmpty()
        val instant = runCatching { Instant.parse(raw.trim()) }.getOrNull()
        if (instant == null) {
            message = "Due must be an explicit ISO-8601 instant"
            return
        }
        val result = assignOperationalTemporal.execute(
            temporalId = "tmp-${UUID.randomUUID()}",
            operationalRecordIdValue = operationalRecordId,
            resolution = TemporalResolution.RESOLVED,
            assignedAt = TemporalAssignmentInstant(Instant.now()),
            dueInstant = DueInstant(instant),
        )
        message = when (result) {
            is DomainResult.Success -> "Due assigned"
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun assignUnresolved(operationalRecordId: String) {
        val result = assignOperationalTemporal.execute(
            temporalId = "tmp-${UUID.randomUUID()}",
            operationalRecordIdValue = operationalRecordId,
            resolution = TemporalResolution.UNRESOLVED,
            assignedAt = TemporalAssignmentInstant(Instant.now()),
            referenceExpression = referenceDrafts[operationalRecordId],
        )
        message = when (result) {
            is DomainResult.Success -> "Unresolved temporal reference stored"
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun evaluateDue(operationalRecordId: String) {
        val id = com.agbofa.smartoffice.domain.operations.OperationalRecordId.of(operationalRecordId)
        if (id !is DomainResult.Success) {
            message = "Invalid operational record id"
            return
        }
        val result = evaluateDueStatus.execute(id.value, EvaluationInstant(Instant.now()))
        message = when (result) {
            is DomainResult.Success -> result.value.name
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun createDependency(dependentId: String) {
        val prerequisite = prerequisiteDrafts[dependentId].orEmpty()
        val result = createOperationalDependency.execute(
            dependencyId = "dep-${UUID.randomUUID()}",
            dependentIdValue = dependentId,
            prerequisiteIdValue = prerequisite,
            createdAt = OperationalDependencyCreationInstant(Instant.now()),
        )
        message = when (result) {
            is DomainResult.Success -> "Dependency created"
            is DomainResult.Failure -> result.error.message
        }
        refresh()
    }

    fun refresh() {
        records = journalTimeline.execute()
        classifications = records.mapNotNull { record ->
            getActiveClassification.execute(record.entryId)?.let { record.entryId.value to it }
        }.toMap()
        val operationalIds = records.mapNotNull { it.operationalRecordId }
        temporals = operationalIds.mapNotNull { id ->
            getOperationalTemporal.execute(id)?.let { id.value to it }
        }.toMap()
        dueStatuses = operationalIds.mapNotNull { id ->
            when (val result = evaluateDueStatus.execute(id, EvaluationInstant(Instant.now()))) {
                is DomainResult.Success -> id.value to result.value
                is DomainResult.Failure -> null
            }
        }.toMap()
        prerequisiteLabels = operationalIds.associate { id ->
            val labels = getOperationalPrerequisites.execute(id)
                .joinToString { it.prerequisiteOperationalRecordId.value }
            id.value to labels
        }
    }
}
