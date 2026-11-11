# AI-assisted development

This repository keeps durable engineering rules separate from task-specific workflows:

- `AGENTS.md` contains architecture, quality, testing, Git, and security rules that apply to every task.
- `.agents/skills/implement-mobile-feature/` contains the implementation workflow used by Codex.
- `.agents/skills/review-mobile-change/` contains the review workflow used by Codex.

The skills are intentionally short and contain only public, repository-specific guidance. They do not embed prompts, policies, source code, or paths from another project.

## Codex

Codex is the initial supported coding agent and discovers the canonical skills from `.agents/skills/`. Invoke them explicitly as `$implement-mobile-feature` and `$review-mobile-change`, or let Codex select them from their descriptions.

## Claude Code

Claude Code support is optional. Small adapters under `.claude/skills/` route to the canonical skill files so both tools follow one maintained workflow. Current Claude Code versions can also read the root `AGENTS.md` when no project `CLAUDE.md` overrides it.

Do not commit personal permissions, API keys, model choices, or `.claude/settings.local.json`. Agent output is never evidence by itself: handoffs must list the actual commands and tests that ran.

