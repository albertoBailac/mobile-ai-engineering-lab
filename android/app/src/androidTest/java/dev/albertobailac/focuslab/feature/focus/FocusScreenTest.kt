package dev.albertobailac.focuslab.feature.focus

import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
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

        composeRule.onNodeWithContentDescription("25 minutes 0 seconds remaining")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("timerText").assertIsDisplayed()
        composeRule.onNodeWithText("Start").performScrollTo().assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun startButtonDispatchesStartAction() {
        var receivedAction: FocusAction? = null
        setContent(FocusUiState()) { receivedAction = it }

        composeRule.onNodeWithText("Start").performScrollTo().performClick()

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

        composeRule.onNodeWithText("Resume").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Reset").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun compactLandscapeCanScrollToStart() {
        var receivedAction: FocusAction? = null
        setContent(FocusUiState(), windowSize = DpSize(667.dp, 375.dp)) { receivedAction = it }

        composeRule.onNodeWithText("Start")
            .assertIsNotDisplayed()
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle { assertEquals(FocusAction.Start, receivedAction) }
    }

    @Test
    fun compactLandscapeCanScrollToResumeAndReset() {
        val receivedActions = mutableListOf<FocusAction>()
        setContent(
            state = FocusUiState(status = FocusTimerStatus.Paused),
            windowSize = DpSize(667.dp, 375.dp),
        ) { receivedActions.add(it) }

        val resume = composeRule.onNodeWithText("Resume").assertIsNotDisplayed()
        val reset = composeRule.onNodeWithText("Reset").assertIsNotDisplayed()
        resume.performScrollTo().assertIsDisplayed().performClick()
        reset.performScrollTo().assertIsDisplayed().performClick()

        composeRule.runOnIdle {
            assertEquals(listOf(FocusAction.Resume, FocusAction.Reset), receivedActions)
        }
    }

    @Test
    fun compactContentIsCappedAt520DpAndCentered() {
        setContent(FocusUiState(), windowSize = DpSize(640.dp, 800.dp))

        assertContentWidthAndCenter(maxWidth = 520.dp)
    }

    @Test
    fun expandedContentIsCappedAt920DpAndCentered() {
        setContent(FocusUiState(), windowSize = DpSize(1120.dp, 800.dp))

        assertContentWidthAndCenter(maxWidth = 920.dp)
    }

    @Test
    fun compactContentFillsNarrowParentWithinGutters() {
        setContent(FocusUiState(), windowSize = DpSize(400.dp, 800.dp))

        val viewport = composeRule.onNode(hasScrollAction()).getUnclippedBoundsInRoot()
        assertContentWidthAndCenter(maxWidth = viewport.right - viewport.left - 48.dp)
    }

    @Test
    fun runningStateDisablesDurationSelection() {
        setContent(FocusUiState(status = FocusTimerStatus.Running))

        composeRule.onNodeWithText("25 min").assertIsNotEnabled()
        composeRule.onNodeWithText("50 min").assertIsNotEnabled()
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
        composeRule.onNodeWithContentDescription("0 minutes 0 seconds remaining").assert(
            SemanticsMatcher.keyNotDefined(SemanticsProperties.LiveRegion),
        )
    }

    private fun assertContentWidthAndCenter(maxWidth: Dp) {
        val viewport = composeRule.onNode(hasScrollAction()).getUnclippedBoundsInRoot()
        val content = composeRule.onNodeWithTag("focusContent")
        val bounds = content.getUnclippedBoundsInRoot()
        val tolerance = with(content.fetchSemanticsNode().layoutInfo.density) { 1.toDp().value }
        assertEquals("content width", maxWidth.value, (bounds.right - bounds.left).value, tolerance)
        assertEquals(
            "horizontal center",
            ((viewport.left + viewport.right) / 2).value,
            ((bounds.left + bounds.right) / 2).value,
            tolerance,
        )
    }

    private fun setContent(
        state: FocusUiState,
        windowSize: DpSize = DpSize(390.dp, 844.dp),
        onAction: (FocusAction) -> Unit = {},
    ) {
        composeRule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.ForcedSize(windowSize)) {
                FocusLabTheme {
                    FocusScreen(
                        uiState = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

