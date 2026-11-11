# Mobile AI Engineering Lab

A public portfolio for native Android and iOS engineering, built with AI-assisted development, pragmatic architecture, automated testing, and reproducible workflows.

## Current project: FocusLab for Android

FocusLab is a focused-session timer implemented with Kotlin, Jetpack Compose, Material 3, and MVVM. Its first iteration demonstrates:

- Monotonic elapsed-time tracking that is resilient to wall-clock changes.
- Correct recalculation after rotation and foreground/background transitions.
- Immutable UI state exposed through `StateFlow`.
- Local unit tests for timer and ViewModel behavior.
- Instrumented Compose tests for interaction and accessibility semantics.
- Android lint, unit tests, and debug assembly in GitHub Actions.

FocusLab intentionally avoids persistence, dependency injection, background services, and premature modularization. See [the architecture notes](docs/architecture.md) for the decisions and restoration limits.

## Technology

- Kotlin 2.4.21 with Android Gradle Plugin 9.4.1 built-in Kotlin support
- Gradle 9.6.0 with JDK 17 for the build runtime and bytecode target
- Jetpack Compose BOM 2026.09.00 and Material 3
- Android API 37, minimum API 26
- AndroidX Lifecycle, `StateFlow`, and Kotlin coroutines
- JUnit and instrumented Compose UI testing

## Repository

```text
android/         FocusLab Android application
ios/             Native iOS roadmap
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

## AI-assisted engineering

Repository rules live in [`AGENTS.md`](AGENTS.md), while repeatable implementation and review workflows live in `.agents/skills/`. Codex is the primary supported agent; Claude Code integration is optional and uses the same canonical instructions. The workflow and privacy boundaries are documented in [AI-assisted development](docs/ai-assisted-development.md).

## Roadmap

- Polish FocusLab with durable user preferences and notifications only after defining their product requirements.
- Add a native SwiftUI portfolio application under `ios/`.
- Expand CI with instrumented Android tests when emulator cost and reliability are evaluated.
- Add release automation and store-ready quality gates in later milestones.

## License

This repository is available under the [MIT License](LICENSE).
