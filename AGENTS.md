# Mobile AI Engineering Lab

## Project map

- Android code lives in `android/`; the only Android module is `:app`.
- iOS code lives in `ios/`; it has one application target plus unit-test and UI-test targets.
- Architecture and AI-assisted workflow notes live in `docs/`.
- Repository-local skills live in `.agents/skills/`.

## Required skills

- Use `$implement-mobile-feature` for Android or iOS feature implementation and behavior-changing fixes.
- Use `$review-mobile-change` when reviewing a mobile diff or preparing a substantive mobile change for handoff.

## Android architecture

- Keep a single `:app` module until a measured build, ownership, or reuse problem justifies another module.
- Organize production code by feature. Keep shared UI theme code under `ui/theme`.
- Use MVVM with immutable `StateFlow` UI state and explicit UI actions.
- Keep composables focused on rendering and event forwarding. Put timer and state-transition logic outside composables.
- Prefer small concrete dependencies and constructor injection. Do not add Repository, UseCase, Hilt, Room, or another architectural layer without an approved requirement.
- Use a monotonic clock for elapsed-time calculations. Do not implement background services for FocusLab.

## iOS architecture

- Keep one production application target until a measured build, ownership, or reuse problem justifies another target or package.
- Organize production code by feature and use SwiftUI with MVVM, immutable state values, and explicit actions.
- Keep the Observation-backed ViewModel and its mutable dependencies isolated to `@MainActor`; enable complete Swift 6 concurrency checking.
- Keep views focused on rendering and event forwarding. Put timing and state transitions outside views.
- Use `ContinuousClock` deadlines and an injected scheduler for refreshes. Do not add background tasks or notifications without an approved requirement.
- Use `@SceneStorage` only for best-effort scene snapshots. Do not describe it as guaranteed process persistence.

## Quality and testing

- Add or update tests for every behavior change and regression fix.
- Add representative light and dark Compose Previews for relevant screens and reusable UI components, keeping them independent of ViewModels and runtime dependencies.
- Prefer local unit tests for pure logic and ViewModels; use instrumented Compose tests for user-visible behavior and semantics.
- Keep accessibility labels and visible strings in Android resources.
- Keep iOS strings in a String Catalog and add light/dark SwiftUI previews for idle, running, paused, and completed states.
- Prefer Swift Testing for timer and ViewModel behavior; use XCUITest for user-visible flows, accessibility, and rotation.
- Do not silence lint or compiler diagnostics without documenting the reason next to the narrowest possible suppression.
- Before handing off Android changes, run from `android/`: `./gradlew lintDebug testDebugUnitTest assembleDebug`.
- Run `./gradlew connectedDebugAndroidTest` only when a compatible device or emulator is available; it is not yet a CI gate.
- Before handing off iOS changes, separately run the app build, `FocusLabTests`, and `FocusLabUITests` commands documented in `ios/README.md` when a compatible simulator is available.
- Report simulator infrastructure failures with their evidence. Never report compiled-but-unexecuted tests as passing.

## Git and security

- Do not work directly on `main`; use a focused feature or fix branch.
- Keep commits small and use Conventional Commit-style subjects when commits are requested.
- Never commit SDK paths, credentials, signing files, tokens, local agent settings, build output, or generated IDE state.
- Do not commit, push, open a pull request, or rewrite history unless the user explicitly requests it.
- Preserve unrelated user changes and report the exact verification performed.

