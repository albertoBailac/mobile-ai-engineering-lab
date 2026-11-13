# Mobile AI Engineering Lab

A public portfolio for native Android and iOS engineering, built with AI-assisted development, pragmatic architecture, automated testing, and reproducible workflows.

## Current project: FocusLab

FocusLab is a focused-session timer implemented natively for Android and iOS. The two applications share behavior while demonstrating each platform's conventions:

- Monotonic elapsed-time tracking that is resilient to wall-clock changes.
- Correct recalculation after rotation and foreground/background transitions.
- Immutable unidirectional UI state with `StateFlow` on Android and Observation on iOS.
- Deterministic timer and ViewModel tests, including controllable tick schedulers.
- Compose and XCUITest coverage for interaction and accessibility.
- Independent Android and iOS GitHub Actions workflows.

FocusLab intentionally avoids durable persistence, dependency injection, background services, and premature modularization. See [the architecture notes](docs/architecture.md) for platform-specific restoration limits.

## Technology

- Kotlin 2.4.21 with Android Gradle Plugin 9.4.1 built-in Kotlin support
- Gradle 9.6.0 with JDK 17 for the build runtime and bytecode target
- Jetpack Compose BOM 2026.09.00 and Material 3
- Android API 37, minimum API 26
- AndroidX Lifecycle, `StateFlow`, and Kotlin coroutines
- JUnit and instrumented Compose UI testing
- SwiftUI, Swift 6, and Observation with complete concurrency checking
- `ContinuousClock`, Swift Testing, and XCUITest
- Xcode 26.6, iOS 17 minimum, and the iOS 26.5 SDK

## Repository

```text
android/         FocusLab Android application
ios/             Native SwiftUI FocusLab application
docs/            Architecture and AI-assisted workflow notes
.agents/skills/  Reusable Codex workflows
.github/         Continuous integration
```

## Build and test

Requirements:

- JDK 17
- Android SDK Platform 37
- Android SDK Build Tools 36.0.0 or newer

Run the CI checks from the Android project:

```bash
cd android
./gradlew lintDebug testDebugUnitTest assembleDebug
```

Instrumented Compose tests require a connected device or emulator and are intentionally not a CI gate yet:

```bash
./gradlew connectedDebugAndroidTest
```

For iOS, use Xcode 26.6 and run the build, unit-test, and UI-test commands documented in [`ios/README.md`](ios/README.md). They remain separate gates so compilation is never reported as test execution.

## AI-assisted engineering

Repository rules live in [`AGENTS.md`](AGENTS.md), while repeatable implementation and review workflows live in `.agents/skills/`. Codex is the primary supported agent; Claude Code integration is optional and uses the same canonical instructions. The workflow and privacy boundaries are documented in [AI-assisted development](docs/ai-assisted-development.md).

## Roadmap

- Polish FocusLab with durable user preferences and notifications only after defining their product requirements.
- Expand CI with instrumented Android tests when emulator cost and reliability are evaluated.
- Add release automation and store-ready quality gates in later milestones.

## License

This repository is available under the [MIT License](LICENSE).
