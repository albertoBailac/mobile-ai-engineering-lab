import Foundation

enum FocusDuration: Int, CaseIterable, Codable, Sendable {
    case short = 25
    case long = 50

    var milliseconds: Int64 {
        Int64(rawValue) * 60 * 1_000
    }

    var label: String {
        String(localized: "\(rawValue) min")
    }
}

enum FocusStatus: String, Codable, Sendable {
    case idle
    case running
    case paused
    case completed
}

struct FocusState: Equatable, Sendable {
    let duration: FocusDuration
    let remainingMilliseconds: Int64
    let status: FocusStatus

    init(
        duration: FocusDuration = .short,
        remainingMilliseconds: Int64? = nil,
        status: FocusStatus = .idle
    ) {
        self.duration = duration
        self.remainingMilliseconds = remainingMilliseconds ?? duration.milliseconds
        self.status = status
    }

    var progress: Double {
        guard duration.milliseconds > 0 else { return 0 }
        return min(max(Double(remainingMilliseconds) / Double(duration.milliseconds), 0), 1)
    }

    var totalSeconds: Int64 {
        Int64(ceil(Double(remainingMilliseconds) / 1_000))
    }

    var formattedTime: String {
        String(format: "%02lld:%02lld", totalSeconds / 60, totalSeconds % 60)
    }

    var spokenRemainingTime: String {
        let minutes = totalSeconds / 60
        let seconds = totalSeconds % 60
        let minuteUnit = minutes == 1 ? String(localized: "minute") : String(localized: "minutes")
        let secondUnit = seconds == 1 ? String(localized: "second") : String(localized: "seconds")
        return "\(minutes) \(minuteUnit) \(seconds) \(secondUnit) \(String(localized: "remaining"))"
    }
}

enum FocusAction: Sendable {
    case selectDuration(FocusDuration)
    case start
    case pause
    case resume
    case reset
    case timerTick
    case appBecameActive
    case appEnteredBackground
    case restore(FocusSessionSnapshot)
}

struct FocusSessionSnapshot: Codable, Equatable, Sendable {
    let durationMinutes: Int
    let remainingMilliseconds: Int64
    let status: FocusStatus

    init(state: FocusState) {
        durationMinutes = state.duration.rawValue
        remainingMilliseconds = state.remainingMilliseconds
        status = state.status
    }

    init(durationMinutes: Int, remainingMilliseconds: Int64, status: FocusStatus) {
        self.durationMinutes = durationMinutes
        self.remainingMilliseconds = remainingMilliseconds
        self.status = status
    }

    var restoredState: FocusState {
        let duration = FocusDuration(rawValue: durationMinutes) ?? .short
        let remaining = min(max(remainingMilliseconds, 0), duration.milliseconds)

        if remaining == 0 || status == .completed {
            return FocusState(duration: duration, remainingMilliseconds: 0, status: .completed)
        }

        switch status {
        case .idle:
            return FocusState(duration: duration)
        case .running, .paused:
            return FocusState(duration: duration, remainingMilliseconds: remaining, status: .paused)
        case .completed:
            return FocusState(duration: duration, remainingMilliseconds: 0, status: .completed)
        }
    }
}

enum FocusSessionSnapshotCodec {
    static func encode(_ snapshot: FocusSessionSnapshot) -> String? {
        guard let data = try? JSONEncoder().encode(snapshot) else { return nil }
        return String(data: data, encoding: .utf8)
    }

    static func decode(_ encoded: String) -> FocusSessionSnapshot? {
        guard let data = encoded.data(using: .utf8) else { return nil }
        return try? JSONDecoder().decode(FocusSessionSnapshot.self, from: data)
    }
}
