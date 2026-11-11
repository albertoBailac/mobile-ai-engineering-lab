# Architecture

## Repository shape

Mobile AI Engineering Lab keeps Android and iOS projects independent so each platform can demonstrate its native ecosystem. FocusLab starts as one Android `:app` module. A new module is justified only by measured build performance, clear ownership, or reuse across multiple applications.

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

## Timer behavior

The timer uses `SystemClock.elapsedRealtime()`, which is monotonic and includes deep sleep. A running session stores a monotonic deadline instead of decrementing a counter. This prevents wall-clock changes and delayed ticks from changing the session duration.

When the app stops, the ViewModel records the latest remaining time and stops its ticker. On resume it recalculates from the stored deadline before restarting UI updates. Rotation keeps the same ViewModel and therefore keeps the live monotonic deadline.

### Restoration limits

- `SavedStateHandle` is best-effort state restoration, not durable persistence. The operating system may not restore it after the user force-stops the app, clears it from recents, clears app data, or after other task-removal scenarios.
- A monotonic timestamp is meaningful only for the current device boot, and process recreation cannot prove that the stored timestamp belongs to that boot. A session that was running is therefore restored paused with the last saved remaining time. Up to one UI tick (250 ms) may be lost.
- A device reboot never automatically continues a running focus session. There is intentionally no wall-clock fallback, alarm, notification, foreground service, or other background execution in this version.

## Testing boundaries

Pure timer behavior and ViewModel transitions are local unit tests. Compose interaction and semantics are instrumented tests under `androidTest`. CI initially gates lint, local unit tests, and debug assembly; instrumented tests remain available for a developer-provided device or emulator.

