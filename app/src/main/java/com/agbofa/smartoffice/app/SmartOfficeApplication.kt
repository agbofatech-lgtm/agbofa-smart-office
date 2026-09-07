package com.agbofa.smartoffice.app

import android.app.Application
import com.agbofa.smartoffice.application.capture.CaptureExpressionUseCase
import com.agbofa.smartoffice.application.classification.ClassifyJournalEntryUseCase
import com.agbofa.smartoffice.application.classification.GetActiveClassificationUseCase
import com.agbofa.smartoffice.application.classification.GetUnclassifiedJournalEntriesUseCase
import com.agbofa.smartoffice.application.integrity.EvaluateIntegrityUseCase
import com.agbofa.smartoffice.application.projection.GetOperationalOverviewUseCase
import com.agbofa.smartoffice.application.projection.GetOperationalOverviewsUseCase
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
import com.agbofa.smartoffice.application.workflow.AdvanceWorkflowUseCase
import com.agbofa.smartoffice.application.workflow.CreateWorkflowUseCase
import com.agbofa.smartoffice.application.workflow.GetWorkflowForOperationalRecordUseCase
import com.agbofa.smartoffice.application.workflow.GetWorkflowHistoryUseCase
import com.agbofa.smartoffice.application.workflow.GetWorkflowStepsUseCase
import com.agbofa.smartoffice.application.workflow.GetWorkflowUseCase
import com.agbofa.smartoffice.application.rules.CreateRuleUseCase
import com.agbofa.smartoffice.application.rules.EvaluateRuleSetUseCase
import com.agbofa.smartoffice.application.rules.EvaluateRuleUseCase
import com.agbofa.smartoffice.application.rules.GetRuleUseCase
import com.agbofa.smartoffice.application.rules.GetRuleVersionUseCase
import com.agbofa.smartoffice.data.persistence.RoomRuleRepository
import com.agbofa.smartoffice.application.workflow.TransitionWorkflowStepUseCase
import com.agbofa.smartoffice.data.persistence.RoomWorkflowRepository
import com.agbofa.smartoffice.data.persistence.RoomWorkflowStepRepository
import com.agbofa.smartoffice.data.persistence.RoomWorkflowStepTransitionRepository
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
    lateinit var createWorkflow: CreateWorkflowUseCase
        private set
    lateinit var getWorkflow: GetWorkflowUseCase
        private set
    lateinit var getWorkflowForOperationalRecord: GetWorkflowForOperationalRecordUseCase
        private set
    lateinit var getWorkflowSteps: GetWorkflowStepsUseCase
        private set
    lateinit var transitionWorkflowStep: TransitionWorkflowStepUseCase
        private set
    lateinit var advanceWorkflow: AdvanceWorkflowUseCase
        private set
    lateinit var getWorkflowHistory: GetWorkflowHistoryUseCase
        private set

    lateinit var createRule: CreateRuleUseCase
        private set
    lateinit var getRule: GetRuleUseCase
        private set
    lateinit var getRuleVersion: GetRuleVersionUseCase
        private set
    lateinit var evaluateRule: EvaluateRuleUseCase
        private set
    lateinit var evaluateRuleSet: EvaluateRuleSetUseCase
        private set
    lateinit var evaluateIntegrity: EvaluateIntegrityUseCase
        private set
    lateinit var getOperationalOverview: GetOperationalOverviewUseCase
        private set
    lateinit var getOperationalOverviews: GetOperationalOverviewsUseCase
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
        val rules = RoomRuleRepository(database.ruleDao())
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
        createRule = CreateRuleUseCase(rules)
        getRule = GetRuleUseCase(rules)
        getRuleVersion = GetRuleVersionUseCase(rules)
        evaluateRule = EvaluateRuleUseCase(rules)
        evaluateRuleSet = EvaluateRuleSetUseCase(rules)
        assignOperationalTemporal = AssignOperationalTemporalUseCase(operations, temporals)
        getOperationalTemporal = GetOperationalTemporalUseCase(temporals)
        evaluateDueStatus = EvaluateDueStatusUseCase(temporals)
        createOperationalDependency = CreateOperationalDependencyUseCase(operations, dependencies)
        getOperationalPrerequisites = GetOperationalPrerequisitesUseCase(dependencies)
        getOperationalDependents = GetOperationalDependentsUseCase(dependencies)
        val workflowRepo = RoomWorkflowRepository(database)
        val workflowSteps = RoomWorkflowStepRepository(database.workflowStepDao())
        val workflowTransitions = RoomWorkflowStepTransitionRepository(database.workflowStepTransitionDao())
        createWorkflow = CreateWorkflowUseCase(operations, workflowRepo, workflowSteps)
        getWorkflow = GetWorkflowUseCase(workflowRepo)
        getWorkflowForOperationalRecord = GetWorkflowForOperationalRecordUseCase(workflowRepo)
        getWorkflowSteps = GetWorkflowStepsUseCase(workflowSteps)
        transitionWorkflowStep = TransitionWorkflowStepUseCase(workflowSteps, workflowTransitions)
        advanceWorkflow = AdvanceWorkflowUseCase(workflowRepo, workflowSteps, transitionWorkflowStep, workflowTransitions)
        getWorkflowHistory = GetWorkflowHistoryUseCase(workflowSteps, workflowTransitions)
        evaluateIntegrity = EvaluateIntegrityUseCase(
            captures,
            journal,
            classifications,
            operations,
            states,
            temporals,
            dependencies,
            workflowRepo,
            workflowSteps,
            workflowTransitions,
            rules,
        )
        getOperationalOverview = GetOperationalOverviewUseCase(
            captures,
            journal,
            classifications,
            operations,
            states,
            temporals,
            dependencies,
            workflowRepo,
            workflowSteps,
            workflowTransitions,
            evaluateIntegrity,
        )
        getOperationalOverviews = GetOperationalOverviewsUseCase(
            captures,
            journal,
            classifications,
            operations,
            states,
            temporals,
            dependencies,
            workflowRepo,
            workflowSteps,
            workflowTransitions,
            evaluateIntegrity,
        )
    }
}

