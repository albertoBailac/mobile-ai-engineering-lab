# Architecture

## Repository shape

Mobile AI Engineering Lab keeps Android and iOS projects independent so each platform can demonstrate its native ecosystem. FocusLab uses one Android `:app` module and one iOS application target. A new module or production target is justified only by measured build performance, clear ownership, or reuse across multiple applications.

## FocusLab

FocusLab is organized by feature and uses MVVM with unidirectional data flow:

```text
FocusScreen -> FocusAction -> FocusViewModel -> StateFlow<FocusUiState> -> FocusScreen
```

- `FocusScreen` renders immutable state and forwards events.
- `FocusViewModel` owns session state, lifecycle reactions, and coroutine scheduling.
- `FocusTimer` performs elapsed-time calculations against an injected monotonic clock.
- `SavedStateHandle` stores the small amount of state required to reconstruct a session.

The first version deliberately has no Repository, UseCase, dependency-injection framework, database, navigation graph, or background service. These would add indirection without supporting a current requirement.

The iOS flow mirrors the same responsibilities with native Swift conventions:

```text
FocusScreen -> FocusAction -> @MainActor FocusViewModel -> FocusState -> FocusScreen
```

- `FocusState` is an immutable value and the Observation-backed ViewModel exposes it read-only.
- SwiftUI owns the ViewModel with `@State`, so view recomposition and rotation do not recreate the live session.
- Explicit actions carry user input, one-second scheduler ticks, and `scenePhase` changes.
- Swift 6 complete concurrency checking and `@MainActor` keep state, the clock, and scheduler ownership on one isolation domain.

## Timer behavior

The timer uses `SystemClock.elapsedRealtime()`, which is monotonic and includes deep sleep. A running session stores a monotonic deadline instead of decrementing a counter. This prevents wall-clock changes and delayed ticks from changing the session duration.

On iOS the equivalent source is `ContinuousClock`, whose instants continue while the system sleeps. The injected one-second scheduler only requests a refresh; all remaining-time calculations use the deadline. A manual scheduler lets unit tests execute the real ticker loop, cancellation, and duplicate-prevention behavior without real sleeps.

When the app stops, the ViewModel records the latest remaining time and stops its ticker. On resume it recalculates from the stored deadline before restarting UI updates. Rotation keeps the same ViewModel and therefore keeps the live monotonic deadline.

### Restoration limits

- `SavedStateHandle` is best-effort state restoration, not durable persistence. The operating system may not restore it after the user force-stops the app, clears it from recents, clears app data, or after other task-removal scenarios.
- A monotonic timestamp is meaningful only for the current device boot, and process recreation cannot prove that the stored timestamp belongs to that boot. A session that was running is therefore restored paused with the last saved remaining time. Up to one UI tick (250 ms) may be lost.
- A device reboot never automatically continues a running focus session. There is intentionally no wall-clock fallback, alarm, notification, foreground service, or other background execution in this version.

On iOS, restoration has three distinct boundaries:

- A retained `FocusViewModel` keeps its live monotonic deadline through recomposition, rotation, and normal scene-phase changes. Background stops the ticker but not elapsed time; foreground recalculates from the deadline.
- `@SceneStorage` holds a small snapshot for scene reconstruction. When SwiftUI supplies it to a new ViewModel, a running session restores paused because the original monotonic instant cannot be recovered.
- Process relaunch does not guarantee scene restoration. If the system returns the snapshot it follows the same conservative path; otherwise the app starts idle. Force quit, scene discard, and reboot are intentionally outside the restoration guarantee.

## Testing boundaries

Pure timer behavior and ViewModel transitions are local unit tests. Compose interaction and semantics are instrumented tests under `androidTest`; Android CI gates lint, local tests, and assembly.

Swift Testing covers iOS timer math, the real ViewModel ticker loop with a manual scheduler, lifecycle transitions, cancellation, and snapshot normalization. XCUITest covers the main user flow, accessibility values, and rotation. iOS CI reports build, unit tests, and UI tests as separate jobs and retains each result bundle; a simulator infrastructure failure is reported as a failure, never as a passed test suite.

