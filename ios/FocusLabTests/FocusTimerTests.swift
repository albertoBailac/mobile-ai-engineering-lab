import Testing
@testable import FocusLab

@Suite("Focus timer", .serialized)
@MainActor
struct FocusTimerTests {
    @Test("Deadline uses monotonic time and clamps the remaining value")
    func deadlineAndRemaining() {
        let clock = TestMonotonicClock(now: .seconds(12))
        let timer = FocusTimer(clock: clock)

        let deadline = timer.deadline(after: 30_000)
        #expect(timer.remaining(until: deadline, maximumMilliseconds: 30_000) == 30_000)

        clock.now = .seconds(27)
        #expect(timer.remaining(until: deadline, maximumMilliseconds: 30_000) == 15_000)

        clock.now = .seconds(50)
        #expect(timer.remaining(until: deadline, maximumMilliseconds: 30_000) == 0)
    }

    @Test("Remaining time rounds up for the displayed second")
    func remainingRoundsUp() {
        let clock = TestMonotonicClock(now: .zero)
        let timer = FocusTimer(clock: clock)
        let deadline = timer.deadline(after: 1_001)

        clock.now = .milliseconds(1)
        #expect(timer.remaining(until: deadline, maximumMilliseconds: 1_001) == 1_000)

        clock.now = .milliseconds(2)
        #expect(timer.remaining(until: deadline, maximumMilliseconds: 1_001) == 999)
    }
}

@MainActor
final class TestMonotonicClock: MonotonicClock {
    var now: Duration

    init(now: Duration = .zero) {
        self.now = now
    }
}
