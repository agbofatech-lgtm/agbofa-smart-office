package com.agbofa.smartoffice.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

/**
 * Phase 4 instrumentation source. Not executed here (no Android SDK).
 */
@RunWith(AndroidJUnit4::class)
class CaptureScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun journalScreenShowsCaptureAndTimeline() {
        composeRule.onNodeWithText("Journal").assertIsDisplayed()
        composeRule.onNodeWithText("Expression").assertIsDisplayed()
        composeRule.onNodeWithText("Capture").assertIsDisplayed()
    }
}
