package dev.albertobailac.focuslab.feature.focus

sealed interface FocusAction {
    data object Start : FocusAction
    data object Pause : FocusAction
    data object Resume : FocusAction
    data object Reset : FocusAction
    data object AppResumed : FocusAction
    data object AppBackgrounded : FocusAction

    data class SelectDuration(
        val durationMillis: Long,
    ) : FocusAction
}

