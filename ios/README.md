# FocusLab for iOS

FocusLab is a native SwiftUI focus timer with the same user-visible behavior as the Android application. It supports 25- and 50-minute sessions, pause, resume, reset, light and dark appearances, rotation, and conservative scene restoration.

## Requirements

- Xcode 26.6
- Swift 6 language mode with complete concurrency checking
- iOS 17 or later

## Build and test

Run these commands from the repository root. Build, unit tests, and UI tests are separate gates so a compiled test bundle is never reported as an executed suite.

```bash
xcodebuild build \
  -project ios/FocusLab.xcodeproj \
  -scheme FocusLab \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath /tmp/focuslab-build-derived \
  CODE_SIGNING_ALLOWED=NO

xcodebuild test \
  -project ios/FocusLab.xcodeproj \
  -scheme FocusLab \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=26.5' \
  -derivedDataPath /tmp/focuslab-unit-derived \
  -only-testing:FocusLabTests \
  CODE_SIGNING_ALLOWED=NO

xcodebuild test \
  -project ios/FocusLab.xcodeproj \
  -scheme FocusLab \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=26.5' \
  -derivedDataPath /tmp/focuslab-ui-derived \
  -only-testing:FocusLabUITests \
  -parallel-testing-enabled NO \
  CODE_SIGNING_ALLOWED=NO
```

Run the same UI-test command with `-destination 'platform=iOS Simulator,name=iPad Pro 11-inch (M5),OS=26.5'` for the regular-width gate. CI executes the XCUITest suite independently on iPhone 17 and iPad Pro 11-inch. Select another installed simulator when iOS 26.5 is unavailable. A CoreSimulator failure is an environmental test failure, not a passing result.

## Adaptive layout matrix

FocusLab chooses its composition from the available content width rather than assuming that a size class maps to one physical device. The same session, timer, controls, state, and actions are reused in every arrangement.

| Case | Representative size | Expected layout | Verification |
| --- | --- | --- | --- |
| Compact portrait | 320×568 | Single column with scrolling | SwiftUI Preview |
| Compact landscape | 667×375 | Single column with reachable controls | Preview and XCUITest rotation |
| Regular iPad | 834×1194 and 1194×834 | Two columns when at least 680 points of content width are available | Preview and iPad XCUITest |
| Accessibility text | 390×844, Accessibility 3 | Single column and vertically stacked actions | SwiftUI Preview |
| Folded-like approximation | 390×844 | Compact single column | Preview only |
| Expanded-like approximation | 780×844 | Regular two-column composition | Preview only |

The folded-like and expanded-like sizes are generic stress cases. Xcode 26.6 does not provide an official iPhone Duo simulator, so they are not evidence of verified foldable-device compatibility. When an official simulator or device becomes available, its real folding and resizing behavior must be added as a separate UI-test destination.

SwiftUI retains the `FocusViewModel` in `FocusRoute` while the window changes size. Rotation and resizing therefore reuse the same monotonic deadline and ticker. The UI rotation test selects a 50-minute session, waits for real progress, changes orientation twice, and verifies that duration, running state, remaining time, and derived progress do not reset. The manual-scheduler unit suite separately verifies that repeated activation cannot create duplicate tickers.

## Restoration limits

The live `FocusViewModel` retains its monotonic deadline through SwiftUI recomposition, rotation, and ordinary scene-phase changes. `@SceneStorage` stores only a small snapshot of duration, remaining time, and status.

If SwiftUI reconstructs a scene and supplies that snapshot, a previously running session restores paused because its original monotonic deadline no longer exists. Scene storage may also be offered after a process relaunch, but this is not guaranteed. Force quit, discarded scenes, device restart, or a later relaunch may start a fresh idle session. FocusLab deliberately has no durable persistence, wall-clock fallback, notification, or background task.
