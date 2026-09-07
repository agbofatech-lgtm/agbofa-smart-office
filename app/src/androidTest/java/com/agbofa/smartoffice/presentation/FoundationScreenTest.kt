package com.agbofa.smartoffice.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

/**
 * Phase 1 instrumentation infrastructure.
 * Not executed in the constitution workspace (no Android SDK).
 */
@RunWith(AndroidJUnit4::class)
class FoundationScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun foundationScreenShowsProductIdentity() {
        composeRule.onNodeWithText("AGBOFA SMART OFFICE").assertIsDisplayed()
        composeRule.onNodeWithText("Personal Operations System").assertIsDisplayed()
        composeRule.onNodeWithText("System foundation initialized").assertIsDisplayed()
    }
}
