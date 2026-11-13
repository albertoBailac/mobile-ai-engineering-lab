import Testing
@testable import FocusLab

@Suite("Focus session snapshot")
struct FocusSessionSnapshotTests {
    @Test("Snapshot round trips through scene storage encoding")
    func codecRoundTrip() throws {
        let snapshot = FocusSessionSnapshot(
            durationMinutes: 50,
            remainingMilliseconds: 1_234_567,
            status: .paused
        )

        let encoded = try #require(FocusSessionSnapshotCodec.encode(snapshot))
        #expect(FocusSessionSnapshotCodec.decode(encoded) == snapshot)
    }

    @Test("Invalid duration and remaining values are normalized")
    func invalidValues() {
        let snapshot = FocusSessionSnapshot(
            durationMinutes: 42,
            remainingMilliseconds: Int64.max,
            status: .running
        )

        #expect(snapshot.restoredState == FocusState(
            remainingMilliseconds: FocusDuration.short.milliseconds,
            status: .paused
        ))
    }

    @Test("Completed snapshot always restores at zero")
    func completed() {
        let snapshot = FocusSessionSnapshot(
            durationMinutes: 50,
            remainingMilliseconds: 30_000,
            status: .completed
        )

        #expect(snapshot.restoredState == FocusState(
            duration: .long,
            remainingMilliseconds: 0,
            status: .completed
        ))
    }
}
