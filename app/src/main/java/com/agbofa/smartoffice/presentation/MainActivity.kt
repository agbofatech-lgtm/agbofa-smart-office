package com.agbofa.smartoffice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.agbofa.smartoffice.presentation.foundation.FoundationScreen
import com.agbofa.smartoffice.presentation.theme.SmartOfficeTheme

/**
 * Phase 1 shell activity.
 *
 * Hosts the foundation screen so Compose and Material 3 can be verified.
 * Contains no journal, task, finance, or scheduling logic.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartOfficeTheme {
                FoundationScreen()
            }
        }
    }
}
