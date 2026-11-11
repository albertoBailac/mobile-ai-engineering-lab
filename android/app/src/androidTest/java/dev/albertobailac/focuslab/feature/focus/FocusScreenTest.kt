package dev.albertobailac.focuslab.feature.focus

import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.albertobailac.focuslab.ui.theme.FocusLabTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FocusScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun idleStateShowsStartAndAccessibleTime() {
        setContent(FocusUiState())

        composeRule.onNodeWithText("25:00").assertIsDisplayed()
        composeRule.onNodeWithText("Start").assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithContentDescription("25 minutes 0 seconds remaining").assertIsDisplayed()
    }

    @Test
    fun startButtonDispatchesStartAction() {
        var receivedAction: FocusAction? = null
        setContent(FocusUiState()) { receivedAction = it }

        composeRule.onNodeWithText("Start").performClick()

        assertEquals(FocusAction.Start, receivedAction)
    }

    @Test
    fun pausedStateShowsResumeAndReset() {
        setContent(
            FocusUiState(
                remainingMillis = 12L * 60L * 1_000L,
                status = FocusTimerStatus.Paused,
            ),
        )

        composeRule.onNodeWithText("Resume").assertIsDisplayed()
        composeRule.onNodeWithText("Reset").assertIsDisplayed()
    }

    @Test
    fun completedStatusIsAPoliteLiveRegion() {
        setContent(
            FocusUiState(
                remainingMillis = 0L,
                status = FocusTimerStatus.Completed,
            ),
        )

        composeRule.onNodeWithText("Session complete").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.LiveRegion,
                LiveRegionMode.Polite,
            ),
        )
        composeRule.onNodeWithText("00:00").assert(
            SemanticsMatcher.keyNotDefined(SemanticsProperties.LiveRegion),
        )
    }

    private fun setContent(
        state: FocusUiState,
        onAction: (FocusAction) -> Unit = {},
    ) {
        composeRule.setContent {
            FocusLabTheme {
                FocusScreen(
                    uiState = state,
                    onAction = onAction,
                )
            }
        }
    }
}

