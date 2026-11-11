package dev.albertobailac.focuslab.feature.focus

class FocusTimer(
    private val clock: MonotonicClock,
) {
    fun deadlineAfter(remainingMillis: Long): Long =
        clock.nowMillis() + remainingMillis.coerceAtLeast(0L)

    fun remainingUntil(
        deadlineMillis: Long,
        maximumMillis: Long,
    ): Long = (deadlineMillis - clock.nowMillis()).coerceIn(0L, maximumMillis)

    fun hasIncompatibleTimeBase(
        deadlineMillis: Long,
        maximumMillis: Long,
    ): Boolean = deadlineMillis - clock.nowMillis() > maximumMillis
}

