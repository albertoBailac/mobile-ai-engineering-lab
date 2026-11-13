import Foundation

@MainActor
protocol MonotonicClock {
    var now: Duration { get }
}

@MainActor
struct SystemMonotonicClock: MonotonicClock {
    private let clock = ContinuousClock()
    private let origin: ContinuousClock.Instant

    init() {
        origin = clock.now
    }

    var now: Duration {
        origin.duration(to: clock.now)
    }
}

@MainActor
struct FocusTimer {
    private let clock: any MonotonicClock

    init(clock: any MonotonicClock) {
        self.clock = clock
    }

    func deadline(after remainingMilliseconds: Int64) -> Duration {
        clock.now + .milliseconds(max(remainingMilliseconds, 0))
    }

    func remaining(until deadline: Duration, maximumMilliseconds: Int64) -> Int64 {
        let duration = deadline - clock.now
        guard duration > .zero else { return 0 }
        return min(duration.ceilingMilliseconds, maximumMilliseconds)
    }
}

private extension Duration {
    var ceilingMilliseconds: Int64 {
        let value = components
        let millisecondsPerSecond: Int64 = 1_000
        let attosecondsPerMillisecond: Int64 = 1_000_000_000_000_000
        let partialMilliseconds = (value.attoseconds + attosecondsPerMillisecond - 1) / attosecondsPerMillisecond
        return value.seconds * millisecondsPerSecond + partialMilliseconds
    }
}

@MainActor
protocol FocusTickScheduling {
    func ticks(every interval: Duration) -> AsyncStream<Void>
}

@MainActor
struct ContinuousFocusTickScheduler: FocusTickScheduling {
    func ticks(every interval: Duration) -> AsyncStream<Void> {
        AsyncStream { continuation in
            let task = Task { @MainActor in
                do {
                    while !Task.isCancelled {
                        try await ContinuousClock().sleep(for: interval)
                        try Task.checkCancellation()
                        continuation.yield()
                    }
                } catch is CancellationError {
                    // Cancellation is the expected way to stop the ticker.
                } catch {
                    assertionFailure("Unexpected ticker failure: \(error)")
                }
                continuation.finish()
            }

            continuation.onTermination = { @Sendable _ in
                task.cancel()
            }
        }
    }
}
