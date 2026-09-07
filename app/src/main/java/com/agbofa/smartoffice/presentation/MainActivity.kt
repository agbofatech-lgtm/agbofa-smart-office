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
 * Phase 4 host.
 *
 * Uses the Application composition root so Capture and Journal share
 * one Room database. Compose does not classify text.
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
                    onExpressionChange = journalViewModel::onExpressionChange,
                    onCapture = journalViewModel::captureAndAdmit,
                )
            }
        }
    }
}
