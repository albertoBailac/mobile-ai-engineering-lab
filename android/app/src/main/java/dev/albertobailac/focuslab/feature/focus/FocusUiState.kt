package dev.albertobailac.focuslab.feature.focus

enum class FocusTimerStatus {
    Idle,
    Running,
    Paused,
    Completed,
}

data class FocusUiState(
    val durationMillis: Long = DEFAULT_FOCUS_DURATION_MILLIS,
    val remainingMillis: Long = durationMillis,
    val status: FocusTimerStatus = FocusTimerStatus.Idle,
) {
    val progress: Float
        get() = if (durationMillis == 0L) {
            0f
        } else {
            (remainingMillis.toFloat() / durationMillis).coerceIn(0f, 1f)
        }
}

const val DEFAULT_FOCUS_DURATION_MILLIS: Long = 25L * 60L * 1_000L
const val LONG_FOCUS_DURATION_MILLIS: Long = 50L * 60L * 1_000L

