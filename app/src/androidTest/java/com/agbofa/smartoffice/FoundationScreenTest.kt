package com.agbofa.smartoffice

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.agbofa.smartoffice.presentation.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation proof that the foundation screen launches.
 * Not executable in this agent environment (no Android SDK).
 */
@RunWith(AndroidJUnit4::class)
class FoundationScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun foundationScreenShowsAuthorizedCopyOnly() {
        composeRule.onNodeWithText("AGBOFA SMART OFFICE").assertIsDisplayed()
        composeRule.onNodeWithText("Personal Operations System").assertIsDisplayed()
        composeRule.onNodeWithText("System foundation initialized").assertIsDisplayed()
    }
}
