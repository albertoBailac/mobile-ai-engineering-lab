package dev.albertobailac.focuslab.feature.focus

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusTimerTest {
    private val clock = FakeMonotonicClock()
    private val timer = FocusTimer(clock)

    @Test
    fun deadlineUsesMonotonicNowAndRemainingDuration() {
        clock.nowMillis = 12_000L

        assertEquals(42_000L, timer.deadlineAfter(30_000L))
    }

    @Test
    fun remainingTimeIsClampedToTimerBounds() {
        clock.nowMillis = 10_000L

        assertEquals(15_000L, timer.remainingUntil(25_000L, 30_000L))
        assertEquals(0L, timer.remainingUntil(5_000L, 30_000L))
        assertEquals(30_000L, timer.remainingUntil(50_000L, 30_000L))
    }

    @Test
    fun deadlineBeyondOneFullDurationSignalsDifferentTimeBase() {
        clock.nowMillis = 1_000L

        assertTrue(timer.hasIncompatibleTimeBase(32_000L, 30_000L))
        assertFalse(timer.hasIncompatibleTimeBase(31_000L, 30_000L))
    }
}

class FakeMonotonicClock(
    var nowMillis: Long = 0L,
) : MonotonicClock {
    override fun nowMillis(): Long = nowMillis
}

