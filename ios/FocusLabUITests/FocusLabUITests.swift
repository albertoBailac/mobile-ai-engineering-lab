import XCTest

@MainActor
final class FocusLabUITests: XCTestCase {
    private var app: XCUIApplication!

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments = ["-uiTesting"]
        app.launch()
    }

    override func tearDown() {
        XCUIDevice.shared.orientation = .portrait
        app = nil
        super.tearDown()
    }

    func testInitialStateAndDurationSelection() {
        XCTAssertTrue(timerGauge.waitForExistence(timeout: 3))
        XCTAssertTrue(app.buttons["Start"].exists)
        XCTAssertTrue(app.staticTexts["Ready to focus"].exists)
        XCTAssertEqual(timerGauge.value as? String, "25 minutes 0 seconds remaining")
        assertReachable(app.segmentedControls["durationPicker"])
        assertReachable(timerGauge)
        assertReachable(app.buttons["Start"])

        app.buttons["50 min"].tap()

        XCTAssertEqual(timerGauge.value as? String, "50 minutes 0 seconds remaining")
        XCTAssertTrue(app.buttons["50 min"].isSelected)
    }

    func testStartPauseResumeAndReset() {
        app.buttons["Start"].tap()
        XCTAssertTrue(app.buttons["Pause"].waitForExistence(timeout: 2))
        XCTAssertTrue(app.staticTexts["Focus in progress"].exists)

        app.buttons["Pause"].tap()
        XCTAssertTrue(app.buttons["Resume"].exists)
        XCTAssertTrue(app.buttons["Reset"].exists)
        XCTAssertTrue(app.staticTexts["Session paused"].exists)

        app.buttons["Resume"].tap()
        XCTAssertTrue(app.buttons["Pause"].exists)

        app.buttons["Reset"].tap()
        XCTAssertTrue(app.buttons["Start"].exists)
        XCTAssertEqual(timerGauge.value as? String, "25 minutes 0 seconds remaining")
    }

    func testRotationPreservesRunningSession() {
        app.buttons["50 min"].tap()
        app.buttons["Start"].tap()
        XCTAssertTrue(app.buttons["Pause"].waitForExistence(timeout: 2))
        XCTAssertTrue(app.buttons["50 min"].isSelected)

        let initialValue = try? XCTUnwrap(timerGauge.value as? String)
        let tickerAdvanced = NSPredicate(format: "value != %@", initialValue ?? "")
        expectation(for: tickerAdvanced, evaluatedWith: timerGauge)
        waitForExpectations(timeout: 3)
        let beforeRotation = remainingSeconds

        XCUIDevice.shared.orientation = .landscapeLeft

        XCTAssertTrue(app.buttons["Pause"].waitForExistence(timeout: 3))
        XCTAssertTrue(app.staticTexts["Focus in progress"].exists)
        XCTAssertTrue(app.buttons["50 min"].isSelected)
        XCTAssertLessThanOrEqual(remainingSeconds, beforeRotation)
        XCTAssertGreaterThanOrEqual(remainingSeconds, beforeRotation - 10)
        XCTAssertGreaterThan(remainingSeconds, FocusDurationThreshold.shortSessionSeconds)
        assertReachable(timerGauge)
        assertReachable(app.buttons["Pause"])

        XCUIDevice.shared.orientation = .portrait

        XCTAssertTrue(app.buttons["Pause"].waitForExistence(timeout: 3))
        XCTAssertTrue(app.buttons["50 min"].isSelected)
        XCTAssertLessThanOrEqual(remainingSeconds, beforeRotation)
        XCTAssertGreaterThanOrEqual(remainingSeconds, beforeRotation - 15)
    }

    private var timerGauge: XCUIElement {
        app.descendants(matching: .any)["timerGauge"]
    }

    private var remainingSeconds: Int {
        guard
            let value = timerGauge.value as? String,
            case let parts = value.split(separator: " "),
            parts.count >= 4,
            let minutes = Int(parts[0]),
            let seconds = Int(parts[2])
        else {
            XCTFail("Unexpected timer accessibility value")
            return 0
        }

        return minutes * 60 + seconds
    }

    private func assertReachable(_ element: XCUIElement) {
        XCTAssertTrue(element.waitForExistence(timeout: 3))
        let window = app.windows.firstMatch

        for _ in 0..<4 where !window.frame.contains(element.frame) {
            app.swipeUp()
        }

        XCTAssertTrue(window.frame.contains(element.frame))
    }
}

private enum FocusDurationThreshold {
    static let shortSessionSeconds = 25 * 60
}
