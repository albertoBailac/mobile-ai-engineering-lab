import Foundation
import Testing
@testable import FocusLab

@Suite("Focus view model", .serialized)
@MainActor
struct FocusViewModelTests {
    @Test("Start, ticker, pause, resume, and reset preserve deadline time")
    func coreActions() async {
        let clock = TestMonotonicClock()
        let scheduler = ManualFocusTickScheduler()
        let viewModel = makeViewModel(clock: clock, scheduler: scheduler)

        viewModel.send(.start)
        #expect(viewModel.state.status == .running)
        #expect(scheduler.activeSubscriptionCount == 1)

        clock.now = .seconds(5)
        scheduler.emitTick()
        await settle()
        #expect(viewModel.state.remainingMilliseconds == FocusDuration.short.milliseconds - 5_000)

        viewModel.send(.pause)
        await settle()
        #expect(viewModel.state.status == .paused)
        #expect(scheduler.activeSubscriptionCount == 0)

        clock.now = .seconds(20)
        viewModel.send(.resume)
        clock.now = .seconds(22)
        scheduler.emitTick()
        await settle()
        #expect(viewModel.state.remainingMilliseconds == FocusDuration.short.milliseconds - 7_000)

        viewModel.send(.reset)
        await settle()
        #expect(viewModel.state == FocusState())
        #expect(scheduler.activeSubscriptionCount == 0)
    }

    @Test("Delayed ticker uses the deadline and completes the session")
    func delayedTickCompletes() async {
        let clock = TestMonotonicClock()
        let scheduler = ManualFocusTickScheduler()
        let viewModel = makeViewModel(clock: clock, scheduler: scheduler)

        viewModel.send(.start)
        clock.now = .milliseconds(FocusDuration.short.milliseconds)
        scheduler.emitTick()
        await settle()

        #expect(viewModel.state.status == .completed)
        #expect(viewModel.state.remainingMilliseconds == 0)
        #expect(scheduler.activeSubscriptionCount == 0)
    }

    @Test("Background cancels updates and active recalculates elapsed time")
    func lifecycle() async {
        let clock = TestMonotonicClock()
        let scheduler = ManualFocusTickScheduler()
        let viewModel = makeViewModel(clock: clock, scheduler: scheduler)

        viewModel.send(.start)
        clock.now = .seconds(10)
        viewModel.send(.appEnteredBackground)
        await settle()
        #expect(viewModel.state.remainingMilliseconds == FocusDuration.short.milliseconds - 10_000)
        #expect(scheduler.activeSubscriptionCount == 0)

        clock.now = .seconds(70)
        viewModel.send(.appBecameActive)
        #expect(viewModel.state.remainingMilliseconds == FocusDuration.short.milliseconds - 70_000)
        #expect(scheduler.activeSubscriptionCount == 1)
    }

    @Test("Repeated active events keep exactly one ticker")
    func repeatedActive() async {
        let scheduler = ManualFocusTickScheduler()
        let viewModel = makeViewModel(scheduler: scheduler)

        viewModel.send(.start)
        viewModel.send(.appEnteredBackground)
        await settle()
        viewModel.send(.appBecameActive)
        viewModel.send(.appBecameActive)

        #expect(scheduler.activeSubscriptionCount == 1)
        #expect(scheduler.createdSubscriptionCount == 2)
    }

    @Test("Pause and reset both cancel a running ticker")
    func cancellation() async {
        let scheduler = ManualFocusTickScheduler()
        let viewModel = makeViewModel(scheduler: scheduler)

        viewModel.send(.start)
        viewModel.send(.pause)
        await settle()
        #expect(scheduler.activeSubscriptionCount == 0)

        viewModel.send(.resume)
        #expect(scheduler.activeSubscriptionCount == 1)
        viewModel.send(.reset)
        await settle()
        #expect(scheduler.activeSubscriptionCount == 0)
    }

    @Test("Duration selection resets an idle or paused session")
    func selection() {
        let viewModel = makeViewModel()

        viewModel.send(.selectDuration(.long))

        #expect(viewModel.state == FocusState(duration: .long))
    }

    @Test("A running snapshot restores paused without a monotonic deadline")
    func runningRestoration() {
        let viewModel = makeViewModel()
        let remaining = FocusDuration.short.milliseconds - 10_000

        viewModel.send(
            .restore(
                FocusSessionSnapshot(
                    durationMinutes: FocusDuration.short.rawValue,
                    remainingMilliseconds: remaining,
                    status: .running
                )
            )
        )

        #expect(viewModel.state == FocusState(
            remainingMilliseconds: remaining,
            status: .paused
        ))
    }

    private func makeViewModel(
        clock: TestMonotonicClock = TestMonotonicClock(),
        scheduler: ManualFocusTickScheduler = ManualFocusTickScheduler()
    ) -> FocusViewModel {
        FocusViewModel(clock: clock, scheduler: scheduler)
    }

    private func settle() async {
        for _ in 0..<5 {
            await Task.yield()
        }
    }
}

@MainActor
final class ManualFocusTickScheduler: FocusTickScheduling {
    private var continuations: [UUID: AsyncStream<Void>.Continuation] = [:]
    private(set) var createdSubscriptionCount = 0

    var activeSubscriptionCount: Int {
        continuations.count
    }

    func ticks(every _: Duration) -> AsyncStream<Void> {
        let identifier = UUID()
        createdSubscriptionCount += 1

        return AsyncStream { continuation in
            continuations[identifier] = continuation
            continuation.onTermination = { @Sendable [weak self] _ in
                Task { @MainActor [weak self] in
                    self?.continuations.removeValue(forKey: identifier)
                }
            }
        }
    }

    func emitTick() {
        for continuation in continuations.values {
            continuation.yield()
        }
    }
}
