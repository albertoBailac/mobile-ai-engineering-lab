import Foundation
import Observation

@MainActor
@Observable
final class FocusViewModel {
    private(set) var state: FocusState

    @ObservationIgnored private let timer: FocusTimer
    @ObservationIgnored private let scheduler: any FocusTickScheduling
    @ObservationIgnored private var deadline: Duration?
    @ObservationIgnored private var tickerTask: Task<Void, Never>?

    init(
        state: FocusState = FocusState(),
        clock: any MonotonicClock = SystemMonotonicClock(),
        scheduler: any FocusTickScheduling = ContinuousFocusTickScheduler()
    ) {
        self.state = state
        timer = FocusTimer(clock: clock)
        self.scheduler = scheduler
    }

    deinit {
        tickerTask?.cancel()
    }

    func send(_ action: FocusAction) {
        switch action {
        case let .selectDuration(duration):
            selectDuration(duration)
        case .start, .resume:
            startOrResume()
        case .pause:
            pause()
        case .reset:
            reset()
        case .timerTick:
            refreshRunningState()
        case .appBecameActive:
            becomeActive()
        case .appEnteredBackground:
            enterBackground()
        case let .restore(snapshot):
            restore(snapshot)
        }
    }

    private func selectDuration(_ duration: FocusDuration) {
        guard state.status != .running else { return }
        cancelTicker()
        deadline = nil
        state = FocusState(duration: duration)
    }

    private func startOrResume() {
        guard state.status != .running, state.remainingMilliseconds > 0 else { return }
        deadline = timer.deadline(after: state.remainingMilliseconds)
        state = FocusState(
            duration: state.duration,
            remainingMilliseconds: state.remainingMilliseconds,
            status: .running
        )
        startTicker()
    }

    private func pause() {
        guard state.status == .running else { return }
        refreshRunningState()
        guard state.status != .completed else { return }
        cancelTicker()
        deadline = nil
        state = FocusState(
            duration: state.duration,
            remainingMilliseconds: state.remainingMilliseconds,
            status: .paused
        )
    }

    private func reset() {
        cancelTicker()
        deadline = nil
        state = FocusState(duration: state.duration)
    }

    private func becomeActive() {
        guard state.status == .running else { return }
        refreshRunningState()
        if state.status == .running {
            startTicker()
        }
    }

    private func enterBackground() {
        refreshRunningState()
        cancelTicker()
    }

    private func restore(_ snapshot: FocusSessionSnapshot) {
        cancelTicker()
        deadline = nil
        state = snapshot.restoredState
    }

    private func refreshRunningState() {
        guard state.status == .running, let deadline else { return }
        let remaining = timer.remaining(
            until: deadline,
            maximumMilliseconds: state.duration.milliseconds
        )

        if remaining == 0 {
            cancelTicker()
            self.deadline = nil
            state = FocusState(
                duration: state.duration,
                remainingMilliseconds: 0,
                status: .completed
            )
        } else {
            state = FocusState(
                duration: state.duration,
                remainingMilliseconds: remaining,
                status: .running
            )
        }
    }

    private func startTicker() {
        guard tickerTask == nil else { return }
        let ticks = scheduler.ticks(every: .seconds(1))
        tickerTask = Task { @MainActor [weak self] in
            for await _ in ticks {
                guard !Task.isCancelled else { break }
                self?.send(.timerTick)
            }
        }
    }

    private func cancelTicker() {
        tickerTask?.cancel()
        tickerTask = nil
    }
}
