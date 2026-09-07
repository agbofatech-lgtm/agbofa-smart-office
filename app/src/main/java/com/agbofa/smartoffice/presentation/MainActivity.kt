package com.agbofa.smartoffice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agbofa.smartoffice.app.SmartOfficeApplication
import com.agbofa.smartoffice.domain.foundation.context.EvaluationContext
import com.agbofa.smartoffice.domain.foundation.time.EvaluationInstant
import com.agbofa.smartoffice.presentation.analytics.AnalyticsScreen
import com.agbofa.smartoffice.presentation.analytics.AnalyticsViewModel
import com.agbofa.smartoffice.presentation.dashboard.DashboardScreen
import com.agbofa.smartoffice.presentation.dashboard.DashboardViewModel
import com.agbofa.smartoffice.presentation.decision.DecisionScreen
import com.agbofa.smartoffice.presentation.decision.DecisionViewModel
import com.agbofa.smartoffice.presentation.journal.JournalScreen
import com.agbofa.smartoffice.presentation.journal.JournalViewModel
import com.agbofa.smartoffice.presentation.navigation.AppDestination
import com.agbofa.smartoffice.presentation.navigation.SmartOfficeScaffold
import com.agbofa.smartoffice.presentation.search.SearchScreen
import com.agbofa.smartoffice.presentation.search.SearchViewModel
import com.agbofa.smartoffice.presentation.intelligence.IntelligenceScreen
import com.agbofa.smartoffice.presentation.intelligence.IntelligenceViewModel
import com.agbofa.smartoffice.presentation.theme.SmartOfficeTheme
import java.time.Instant

class MainActivity : ComponentActivity() {
    private val app: SmartOfficeApplication
        get() = application as SmartOfficeApplication

    private val journalViewModel: JournalViewModel by viewModels { factory { journalVm() } }
    private val dashboardViewModel: DashboardViewModel by viewModels {
        factory { DashboardViewModel(app.getOperationalOverviews, app.getOperationalAnalytics) }
    }
    private val decisionViewModel: DecisionViewModel by viewModels {
        factory {
            DecisionViewModel(
                app.getDecisions,
                app.approveDecision,
                app.rejectDecision,
                app.withdrawDecision,
            )
        }
    }
    private val analyticsViewModel: AnalyticsViewModel by viewModels {
        factory { AnalyticsViewModel(app.getOperationalAnalytics) }
    }
    private val searchViewModel: SearchViewModel by viewModels {
        factory { SearchViewModel(app.search, app.rebuildSearchIndex) }
    }
    private val intelligenceViewModel: IntelligenceViewModel by viewModels {
        factory { IntelligenceViewModel(app.generateIntelligence) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        journalViewModel.refresh()
        val context = edgeContext()
        dashboardViewModel.refresh(context)
        decisionViewModel.refresh()
        intelligenceViewModel.refresh(EvaluationContext(EvaluationInstant(Instant.now())))
        analyticsViewModel.refresh(context)
        enableEdgeToEdge()
        setContent {
            SmartOfficeTheme {
                var destination by rememberSaveable { mutableStateOf(AppDestination.JOURNAL.name) }
                val selected = AppDestination.valueOf(destination)
                SmartOfficeScaffold(destination = selected, onDestination = { destination = it.name }) { modifier ->
                    when (selected) {
                        AppDestination.JOURNAL -> JournalScreen(
                            expression = journalViewModel.expression,
                            message = journalViewModel.message,
                            records = journalViewModel.records,
                            pendingType = journalViewModel.pendingType,
                            onExpressionChange = journalViewModel::onExpressionChange,
                            onCapture = journalViewModel::captureAndAdmit,
                            onTypeSelected = journalViewModel::onTypeSelected,
                            onClassify = journalViewModel::classify,
                            onCreateOperational = journalViewModel::createOperational,
                            onTransitionState = journalViewModel::transitionState,
                            temporals = journalViewModel.temporals,
                            dueStatuses = journalViewModel.dueStatuses,
                            prerequisiteLabels = journalViewModel.prerequisiteLabels,
                            dueDrafts = journalViewModel.dueDrafts,
                            referenceDrafts = journalViewModel.referenceDrafts,
                            prerequisiteDrafts = journalViewModel.prerequisiteDrafts,
                            onDueDraftChange = journalViewModel::onDueDraftChange,
                            onReferenceDraftChange = journalViewModel::onReferenceDraftChange,
                            onPrerequisiteDraftChange = journalViewModel::onPrerequisiteDraftChange,
                            onAssignDue = journalViewModel::assignDue,
                            onAssignUnresolved = journalViewModel::assignUnresolved,
                            onEvaluateDue = journalViewModel::evaluateDue,
                            onCreateDependency = journalViewModel::createDependency,
                            modifier = modifier,
                        )
                        AppDestination.DASHBOARD -> DashboardScreen(
                            state = dashboardViewModel.state,
                            onRefresh = { dashboardViewModel.refresh(edgeContext()) },
                            modifier = modifier,
                        )
                        AppDestination.DECISION -> DecisionScreen(
                            state = decisionViewModel.state,
                            onRefresh = decisionViewModel::refresh,
                            onApprove = { id -> decisionViewModel.approve(id, Instant.now()) },
                            onReject = { id -> decisionViewModel.reject(id, Instant.now()) },
                            onWithdraw = { id -> decisionViewModel.withdraw(id, Instant.now()) },
                            modifier = modifier,
                        )
                        AppDestination.ANALYTICS -> AnalyticsScreen(
                            state = analyticsViewModel.state,
                            onRefresh = { intelligenceViewModel.refresh(EvaluationContext(EvaluationInstant(Instant.now())))
        analyticsViewModel.refresh(edgeContext()) },
                            modifier = modifier,
                        )
                        AppDestination.INTELLIGENCE -> IntelligenceScreen(
                            state = intelligenceViewModel.state,
                            onRefresh = { intelligenceViewModel.refresh(EvaluationContext(EvaluationInstant(Instant.now()))) },
                            modifier = modifier,
                        )
                        AppDestination.SEARCH -> SearchScreen(
                            state = searchViewModel.state,
                            onQueryChange = searchViewModel::onQueryChange,
                            onSearch = searchViewModel::search,
                            onRebuild = { searchViewModel.rebuild(edgeContext()) },
                            modifier = modifier,
                        )
                    }
                }
            }
        }
    }

    private fun edgeContext() = EvaluationContext(EvaluationInstant(Instant.now()))

    private fun journalVm() = JournalViewModel(
        captureExpression = app.captureExpression,
        admitCapture = app.admitCapture,
        journalTimeline = app.journalTimeline,
        classifyJournalEntry = app.classifyJournalEntry,
        getActiveClassification = app.getActiveClassification,
        createOperationalRecord = app.createOperationalRecord,
        transitionOperationalRecordState = app.transitionOperationalRecordState,
        assignOperationalTemporal = app.assignOperationalTemporal,
        getOperationalTemporal = app.getOperationalTemporal,
        evaluateDueStatus = app.evaluateDueStatus,
        createOperationalDependency = app.createOperationalDependency,
        getOperationalPrerequisites = app.getOperationalPrerequisites,
    )

    private fun factory(create: () -> ViewModel): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
        }
}
