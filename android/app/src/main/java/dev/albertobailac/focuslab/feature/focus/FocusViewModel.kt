package dev.albertobailac.focuslab.feature.focus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusViewModel internal constructor(
    private val savedStateHandle: SavedStateHandle,
    clock: MonotonicClock,
    private val tickIntervalMillis: Long = DEFAULT_TICK_INTERVAL_MILLIS,
) : ViewModel() {
    constructor(savedStateHandle: SavedStateHandle) : this(
        savedStateHandle = savedStateHandle,
        clock = SystemMonotonicClock,
    )

    private val timer = FocusTimer(clock)
    private var deadlineMillis: Long? = savedStateHandle[KEY_DEADLINE_MILLIS]
    private var tickerJob: Job? = null

    private val mutableUiState = kotlinx.coroutines.flow.MutableStateFlow(restoreState())
    val uiState: kotlinx.coroutines.flow.StateFlow<FocusUiState> = mutableUiState.asStateFlow()

    init {
        persistState()
        if (mutableUiState.value.status == FocusTimerStatus.Running) {
            startTicker()
        }
    }

    fun onAction(action: FocusAction) {
        when (action) {
            FocusAction.Start,
            FocusAction.Resume -> startOrResume()

            FocusAction.Pause -> pause()
            FocusAction.Reset -> reset()
            FocusAction.AppBackgrounded -> moveToBackground()
            FocusAction.AppResumed -> resumeFromBackground()
            is FocusAction.SelectDuration -> selectDuration(action.durationMillis)
        }
    }

    private fun startOrResume() {
        val current = mutableUiState.value
        if (current.status == FocusTimerStatus.Running || current.remainingMillis <= 0L) return

        deadlineMillis = timer.deadlineAfter(current.remainingMillis)
        updateState(current.copy(status = FocusTimerStatus.Running))
        startTicker()
    }

    private fun pause() {
        if (mutableUiState.value.status != FocusTimerStatus.Running) return

        refreshRunningState()
        tickerJob?.cancel()
        deadlineMillis = null
        val current = mutableUiState.value
        if (current.status != FocusTimerStatus.Completed) {
            updateState(current.copy(status = FocusTimerStatus.Paused))
        }
    }

    private fun reset() {
        tickerJob?.cancel()
        deadlineMillis = null
        val duration = mutableUiState.value.durationMillis
        updateState(
            FocusUiState(
                durationMillis = duration,
                remainingMillis = duration,
                status = FocusTimerStatus.Idle,
            ),
        )
    }

    private fun selectDuration(durationMillis: Long) {
        if (mutableUiState.value.status == FocusTimerStatus.Running) return
        val supportedDuration = durationMillis.takeIf {
            it == DEFAULT_FOCUS_DURATION_MILLIS || it == LONG_FOCUS_DURATION_MILLIS
        } ?: return

        tickerJob?.cancel()
        deadlineMillis = null
        updateState(
            FocusUiState(
                durationMillis = supportedDuration,
                remainingMillis = supportedDuration,
                status = FocusTimerStatus.Idle,
            ),
        )
    }

    private fun moveToBackground() {
        refreshRunningState()
        tickerJob?.cancel()
    }

    private fun resumeFromBackground() {
        if (mutableUiState.value.status != FocusTimerStatus.Running) return
        refreshRunningState()
        if (mutableUiState.value.status == FocusTimerStatus.Running) {
            startTicker()
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive && mutableUiState.value.status == FocusTimerStatus.Running) {
                refreshRunningState()
                delay(tickIntervalMillis)
            }
        }
    }

    private fun refreshRunningState() {
        val current = mutableUiState.value
        val deadline = deadlineMillis
        if (current.status != FocusTimerStatus.Running || deadline == null) return

        if (timer.hasIncompatibleTimeBase(deadline, current.durationMillis)) {
            deadlineMillis = null
            updateState(current.copy(status = FocusTimerStatus.Paused))
            return
        }

        val remaining = timer.remainingUntil(deadline, current.durationMillis)
        if (remaining == 0L) {
            deadlineMillis = null
            tickerJob?.cancel()
            updateState(current.copy(remainingMillis = 0L, status = FocusTimerStatus.Completed))
        } else {
            updateState(current.copy(remainingMillis = remaining))
        }
    }

    private fun restoreState(): FocusUiState {
        val duration = savedStateHandle.get<Long>(KEY_DURATION_MILLIS)
            ?.takeIf { it == DEFAULT_FOCUS_DURATION_MILLIS || it == LONG_FOCUS_DURATION_MILLIS }
            ?: DEFAULT_FOCUS_DURATION_MILLIS
        val savedRemaining = savedStateHandle.get<Long>(KEY_REMAINING_MILLIS)
            ?.coerceIn(0L, duration)
            ?: duration
        val status = savedStateHandle.get<String>(KEY_STATUS)
            ?.let { saved -> FocusTimerStatus.entries.firstOrNull { it.name == saved } }
            ?: FocusTimerStatus.Idle
        if (status != FocusTimerStatus.Running) {
            deadlineMillis = null
            return FocusUiState(duration, savedRemaining, status)
        }

        // A persisted elapsed-realtime deadline cannot be proven to belong to the
        // current device boot. Restore conservatively and let the user resume.
        deadlineMillis = null
        return FocusUiState(duration, savedRemaining, FocusTimerStatus.Paused)
    }

    private fun updateState(state: FocusUiState) {
        mutableUiState.value = state
        persistState()
    }

    private fun persistState() {
        val current = mutableUiState.value
        savedStateHandle[KEY_DURATION_MILLIS] = current.durationMillis
        savedStateHandle[KEY_REMAINING_MILLIS] = current.remainingMillis
        savedStateHandle[KEY_STATUS] = current.status.name
        savedStateHandle[KEY_DEADLINE_MILLIS] = deadlineMillis
    }

    companion object {
        private const val DEFAULT_TICK_INTERVAL_MILLIS = 250L
        internal const val KEY_DURATION_MILLIS = "durationMillis"
        internal const val KEY_REMAINING_MILLIS = "remainingMillis"
        internal const val KEY_STATUS = "status"
        internal const val KEY_DEADLINE_MILLIS = "deadlineMillis"
    }
}

