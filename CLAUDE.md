# AppleMapsRedirect

Android app that intercepts `maps.apple.com` links (and shared Apple Maps text) on Android and hands them off to Google Maps / Waze / any installed `geo:` handler via a chooser. Includes a manual paste-and-convert screen for cases where a link arrives as plain text the OS won't route.

- Package: `com.applemapsredirect`
- minSdk 21, targetSdk 35, Kotlin 2.0.21, AGP 8.7.3
- Two activities: `RedirectActivity` (transparent, handles intents) and `MainActivity` (manual UI)
- Core logic: `AppleMapsParser` - resolves redirects, extracts `ll` / `daddr` / `address` / `q` / `saddr` / `/place/<name>`, emits `geo:` or `google.navigation:` URIs.

## Agent Memory Protocol

Use this file as the standing project memory shared between Claude Code and Codex.

Rules:

1. Any meaningful code change, architecture decision, bug fix, regression, or workflow adjustment should be recorded here.
2. Each note should be prefixed with the agent name in brackets, for example `[Claude]` or `[Codex]`.
3. Prefer concise entries that explain what changed, why it changed, and any follow-up risk.
4. Keep older entries, including failed attempts and mistakes. Do not rewrite agent log history; append corrections and follow-up outcomes instead.
5. If the file's encoding gets damaged, normalize it back to plain ASCII or valid UTF-8 while preserving meaning.

Suggested note format:

```text
### YYYY-MM-DD
- [AgentName] What changed. Why it changed. Any follow-up note.
```

## Agent Log

### 2026-05-25
- [Claude] Initialized CLAUDE.md with project description and Agent Memory Protocol. No code changes; bootstrap only so future Claude/Codex sessions share context.
- [Codex] Added public-facing project materials: README, GitHub Pages front page in docs/index.html, and .gitignore for Android/Gradle output. Preparing repository for public GitHub publishing.
- [Codex] User preference clarified: avoid unnecessary permission prompts. Proceed automatically for routine reads, edits, builds, tests, Git operations, metadata checks, and verification when tools allow it. Ask only when the environment blocks the action or when the operation is genuinely risky, such as destructive filesystem changes, credential/auth changes, installs/downloads, remote deletion/publishing changes, or actions likely to harm files, accounts, repos, or system state.
