package com.agbofa.smartoffice.app

import android.app.Application
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.classification.GetActiveClassificationUseCase
import com.agbofa.smartoffice.application.classification.GetUnclassifiedJournalEntriesUseCase
import com.agbofa.smartoffice.application.journal.AdmitCaptureToJournalUseCase
import com.agbofa.smartoffice.application.journal.GetJournalTimelineUseCase
import com.agbofa.smartoffice.application.operations.AssignOperationalTemporalUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalDependencyUseCase
import com.agbofa.smartoffice.application.operations.CreateOperationalRecordUseCase
import com.agbofa.smartoffice.application.operations.EvaluateDueStatusUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalDependentsUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalPrerequisitesUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalTemporalUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalRecordForJournalEntryUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalRecordStateUseCase
import com.agbofa.smartoffice.application.operations.GetOperationalStateHistoryUseCase
import com.agbofa.smartoffice.application.operations.TransitionOperationalRecordStateUseCase
import com.agbofa.smartoffice.data.persistence.RoomCaptureRepository
import com.agbofa.smartoffice.data.persistence.RoomClassificationRepository
import com.agbofa.smartoffice.data.persistence.RoomJournalRepository
import com.agbofa.smartoffice.data.persistence.RoomOperationalDependencyRepository
import com.agbofa.smartoffice.data.persistence.RoomOperationalRecordRepository
import com.agbofa.smartoffice.data.persistence.RoomOperationalTemporalRepository
import com.agbofa.smartoffice.data.persistence.RoomOperationalStateRepository
import com.agbofa.smartoffice.data.persistence.SmartOfficeDatabase

class SmartOfficeApplication : Application() {
    lateinit var database: SmartOfficeDatabase
        private set
    lateinit var captureExpression: CaptureExpressionUseCase
        private set
    lateinit var admitCapture: AdmitCaptureToJournalUseCase
        private set
    lateinit var journalTimeline: GetJournalTimelineUseCase
        private set
    lateinit var classifyJournalEntry: ClassifyJournalEntryUseCase
        private set
    lateinit var getActiveClassification: GetActiveClassificationUseCase
        private set
    lateinit var getUnclassifiedJournalEntries: GetUnclassifiedJournalEntriesUseCase
        private set
    lateinit var createOperationalRecord: CreateOperationalRecordUseCase
        private set
    lateinit var getOperationalRecordForJournalEntry: GetOperationalRecordForJournalEntryUseCase
        private set
    lateinit var transitionOperationalRecordState: TransitionOperationalRecordStateUseCase
        private set
    lateinit var getOperationalRecordState: GetOperationalRecordStateUseCase
        private set
    lateinit var getOperationalStateHistory: GetOperationalStateHistoryUseCase
        private set
    lateinit var assignOperationalTemporal: AssignOperationalTemporalUseCase
        private set
    lateinit var getOperationalTemporal: GetOperationalTemporalUseCase
        private set
    lateinit var evaluateDueStatus: EvaluateDueStatusUseCase
        private set
    lateinit var createOperationalDependency: CreateOperationalDependencyUseCase
        private set
    lateinit var getOperationalPrerequisites: GetOperationalPrerequisitesUseCase
        private set
    lateinit var getOperationalDependents: GetOperationalDependentsUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        database = SmartOfficeDatabase.create(this)
        val captures = RoomCaptureRepository(database.captureDao())
        val journal = RoomJournalRepository(database.journalEntryDao())
        val classifications = RoomClassificationRepository(database.classificationDao())
        val operations = RoomOperationalRecordRepository(database.operationalRecordDao())
        val states = RoomOperationalStateRepository(database.operationalStateTransitionDao())
        val temporals = RoomOperationalTemporalRepository(database.operationalTemporalRecordDao())
        val dependencies = RoomOperationalDependencyRepository(database.operationalDependencyDao())
        captureExpression = CaptureExpressionUseCase(captures)
        admitCapture = AdmitCaptureToJournalUseCase(captures, journal)
        journalTimeline = GetJournalTimelineUseCase(
            captures, journal, classifications, operations, states,
        )
        classifyJournalEntry = ClassifyJournalEntryUseCase(journal, classifications)
        getActiveClassification = GetActiveClassificationUseCase(classifications)
        getUnclassifiedJournalEntries = GetUnclassifiedJournalEntriesUseCase(journal, classifications)
        createOperationalRecord = CreateOperationalRecordUseCase(journal, classifications, operations)
        getOperationalRecordForJournalEntry = GetOperationalRecordForJournalEntryUseCase(operations)
        transitionOperationalRecordState = TransitionOperationalRecordStateUseCase(operations, states)
        getOperationalRecordState = GetOperationalRecordStateUseCase(states)
        getOperationalStateHistory = GetOperationalStateHistoryUseCase(states)
        assignOperationalTemporal = AssignOperationalTemporalUseCase(operations, temporals)
        getOperationalTemporal = GetOperationalTemporalUseCase(temporals)
        evaluateDueStatus = EvaluateDueStatusUseCase(temporals)
        createOperationalDependency = CreateOperationalDependencyUseCase(operations, dependencies)
        getOperationalPrerequisites = GetOperationalPrerequisitesUseCase(dependencies)
        getOperationalDependents = GetOperationalDependentsUseCase(dependencies)
    }
}
