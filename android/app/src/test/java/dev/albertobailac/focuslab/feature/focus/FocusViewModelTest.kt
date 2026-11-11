package dev.albertobailac.focuslab.feature.focus

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FocusViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = CountingMonotonicClock()

    @Test
    fun startPauseResumeAndResetPreserveExpectedTime() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        assertEquals(FocusTimerStatus.Running, viewModel.uiState.value.status)

        clock.nowMillis = 5_000L
        viewModel.onAction(FocusAction.Pause)
        assertEquals(FocusTimerStatus.Paused, viewModel.uiState.value.status)
        assertEquals(DEFAULT_FOCUS_DURATION_MILLIS - 5_000L, viewModel.uiState.value.remainingMillis)

        clock.nowMillis = 20_000L
        viewModel.onAction(FocusAction.Resume)
        clock.nowMillis = 22_000L
        viewModel.onAction(FocusAction.AppBackgrounded)
        assertEquals(DEFAULT_FOCUS_DURATION_MILLIS - 7_000L, viewModel.uiState.value.remainingMillis)

        viewModel.onAction(FocusAction.Reset)
        assertEquals(FocusUiState(), viewModel.uiState.value)
    }

    @Test
    fun resumeFromBackgroundRecalculatesAgainstDeadline() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        clock.nowMillis = 10_000L
        viewModel.onAction(FocusAction.AppBackgrounded)

        clock.nowMillis = 70_000L
        viewModel.onAction(FocusAction.AppResumed)

        assertEquals(FocusTimerStatus.Running, viewModel.uiState.value.status)
        assertEquals(DEFAULT_FOCUS_DURATION_MILLIS - 70_000L, viewModel.uiState.value.remainingMillis)

        viewModel.onAction(FocusAction.AppBackgrounded)
    }

    @Test
    fun elapsedDeadlineCompletesTheSession() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        clock.nowMillis = DEFAULT_FOCUS_DURATION_MILLIS + 1L
        viewModel.onAction(FocusAction.AppBackgrounded)

        assertEquals(FocusTimerStatus.Completed, viewModel.uiState.value.status)
        assertEquals(0L, viewModel.uiState.value.remainingMillis)
    }

    @Test
    fun tickerUpdatesRemainingTimePeriodically() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        runCurrent()
        clock.nowMillis = 1_000L
        advanceTimeBy(250L)
        runCurrent()

        assertEquals(
            DEFAULT_FOCUS_DURATION_MILLIS - 1_000L,
            viewModel.uiState.value.remainingMillis,
        )

        clock.nowMillis = 1_500L
        advanceTimeBy(250L)
        runCurrent()

        assertEquals(
            DEFAULT_FOCUS_DURATION_MILLIS - 1_500L,
            viewModel.uiState.value.remainingMillis,
        )
        viewModel.onAction(FocusAction.Reset)
    }

    @Test
    fun tickerCompletesSessionAutomaticallyAtDeadline() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        runCurrent()
        clock.nowMillis = DEFAULT_FOCUS_DURATION_MILLIS
        advanceTimeBy(250L)
        runCurrent()

        assertEquals(FocusTimerStatus.Completed, viewModel.uiState.value.status)
        assertEquals(0L, viewModel.uiState.value.remainingMillis)
    }

    @Test
    fun pauseCancelsTicker() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        runCurrent()
        clock.nowMillis = 1_000L
        viewModel.onAction(FocusAction.Pause)
        val pausedState = viewModel.uiState.value

        clock.nowMillis = 10_000L
        advanceTimeBy(1_000L)
        runCurrent()

        assertEquals(pausedState, viewModel.uiState.value)
    }

    @Test
    fun resetCancelsTicker() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        runCurrent()
        viewModel.onAction(FocusAction.Reset)

        clock.nowMillis = 10_000L
        advanceTimeBy(1_000L)
        runCurrent()

        assertEquals(FocusUiState(), viewModel.uiState.value)
    }

    @Test
    fun movingToBackgroundCancelsTicker() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        runCurrent()
        clock.nowMillis = 1_000L
        viewModel.onAction(FocusAction.AppBackgrounded)
        val backgroundState = viewModel.uiState.value

        clock.nowMillis = 10_000L
        advanceTimeBy(1_000L)
        runCurrent()

        assertEquals(backgroundState, viewModel.uiState.value)
        viewModel.onAction(FocusAction.Reset)
    }

    @Test
    fun repeatedResumeKeepsSingleTicker() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.Start)
        runCurrent()
        viewModel.onAction(FocusAction.AppBackgrounded)
        viewModel.onAction(FocusAction.AppResumed)
        viewModel.onAction(FocusAction.AppResumed)
        clock.resetReadCount()

        runCurrent()

        assertEquals(2, clock.readCount)
        viewModel.onAction(FocusAction.Reset)
    }

    @Test
    fun selectingSupportedDurationResetsIdleTimer() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(FocusAction.SelectDuration(LONG_FOCUS_DURATION_MILLIS))

        assertEquals(
            FocusUiState(
                durationMillis = LONG_FOCUS_DURATION_MILLIS,
                remainingMillis = LONG_FOCUS_DURATION_MILLIS,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun processRecreationRestoresRunningSessionPaused() = runTest {
        val lastSavedRemaining = DEFAULT_FOCUS_DURATION_MILLIS - 10_000L
        val viewModel = FocusViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    FocusViewModel.KEY_DURATION_MILLIS to DEFAULT_FOCUS_DURATION_MILLIS,
                    FocusViewModel.KEY_REMAINING_MILLIS to lastSavedRemaining,
                    FocusViewModel.KEY_STATUS to FocusTimerStatus.Running.name,
                    FocusViewModel.KEY_DEADLINE_MILLIS to DEFAULT_FOCUS_DURATION_MILLIS,
                ),
            ),
            clock = clock,
            tickIntervalMillis = 250L,
        )

        assertEquals(FocusTimerStatus.Paused, viewModel.uiState.value.status)
        assertEquals(lastSavedRemaining, viewModel.uiState.value.remainingMillis)
    }

    private fun createViewModel() = FocusViewModel(
        savedStateHandle = SavedStateHandle(),
        clock = clock,
        tickIntervalMillis = 250L,
    )
}

private class CountingMonotonicClock(
    var nowMillis: Long = 0L,
) : MonotonicClock {
    var readCount: Int = 0
        private set

    override fun nowMillis(): Long {
        readCount += 1
        return nowMillis
    }

    fun resetReadCount() {
        readCount = 0
    }
}

