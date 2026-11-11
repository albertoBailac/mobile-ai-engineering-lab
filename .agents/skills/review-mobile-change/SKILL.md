---
name: review-mobile-change
description: Review an Android or iOS change in Mobile AI Engineering Lab. Use when asked for a code review, risk assessment, or pre-handoff review of a mobile diff; do not modify files unless the user separately requests fixes.
---

# Review a mobile change

Read `AGENTS.md`, `docs/architecture.md`, the current Git status, and the complete relevant diff. Enumerate untracked files with `git ls-files --others --exclude-standard`, inspect their contents directly, and do not rely on `git diff` to represent them. Review the code as it runs, not only individual files.

Prioritize concrete defects in this order:

1. Incorrect behavior, crashes, data loss, security, or privacy issues.
2. Lifecycle, concurrency, state restoration, and time-calculation errors.
3. Architecture violations that make the change harder to test or maintain.
4. Missing tests for meaningful behavior and regressions.
5. Accessibility, localization, performance, and build or CI problems.

For every finding, identify the file and tightest useful line range, explain the user-visible or engineering impact, and suggest the smallest viable correction. Avoid speculative findings without evidence in the diff.

Run focused read-only checks when useful. Do not edit code during a review-only request. Report findings first in severity order, then open questions and verification gaps. If there are no findings, say so explicitly and still state residual risks or tests not run.

