package com.agbofa.smartoffice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agbofa.smartoffice.app.SmartOfficeApplication
import com.agbofa.smartoffice.presentation.journal.JournalScreen
import com.agbofa.smartoffice.presentation.journal.JournalViewModel
import com.agbofa.smartoffice.presentation.theme.SmartOfficeTheme

/**
 * Phase 5 host.
 *
 * Compose renders state and forwards classify selections.
 * It does not parse text or assign meaning itself.
 */
class MainActivity : ComponentActivity() {
    private val journalViewModel: JournalViewModel by viewModels {
        val app = application as SmartOfficeApplication
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JournalViewModel(
                    captureExpression = app.captureExpression,
                    admitCapture = app.admitCapture,
                    journalTimeline = app.journalTimeline,
                    classifyJournalEntry = app.classifyJournalEntry,
                    getActiveClassification = app.getActiveClassification,
                    createOperationalRecord = app.createOperationalRecord,
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        journalViewModel.refresh()
        enableEdgeToEdge()
        setContent {
            SmartOfficeTheme {
                JournalScreen(
                    expression = journalViewModel.expression,
                    message = journalViewModel.message,
                    records = journalViewModel.records,
                    pendingType = journalViewModel.pendingType,
                    onExpressionChange = journalViewModel::onExpressionChange,
                    onCapture = journalViewModel::captureAndAdmit,
                    onTypeSelected = journalViewModel::onTypeSelected,
                    onClassify = journalViewModel::classify,
                    onCreateOperational = journalViewModel::createOperational,
                )
            }
        }
    }
}
