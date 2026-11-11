---
name: implement-mobile-feature
description: Implement or change an Android or iOS feature in Mobile AI Engineering Lab. Use for behavior-changing mobile work that needs repository-aware architecture, tests, and verification; do not trigger for review-only or documentation-only requests.
---

# Implement a mobile feature

Read the applicable `AGENTS.md`, the relevant platform build files, and `docs/architecture.md` before editing.

Define the smallest observable behavior that satisfies the request. Preserve the single-module, feature-oriented architecture and existing state flow. Do not introduce a new abstraction, dependency, persistence layer, or background component unless the requested behavior needs it and the user has approved the scope.

For Android work:

- Model screen state as immutable data exposed through `StateFlow`.
- Send user and lifecycle events to the ViewModel as explicit actions.
- Keep composables free of business and timing logic.
- Use resources for user-facing text and semantics.
- Add focused unit tests for logic and ViewModels, plus instrumented Compose coverage for important UI behavior.

Run the narrowest useful checks while iterating. Before handoff, run the Android verification commands from `AGENTS.md` when the environment supports them. Never claim an unexecuted check passed.

Finish with a concise summary of behavior, files changed, architecture decisions, executed checks, unexecuted checks, and remaining limitations. Do not commit or push unless explicitly requested.

