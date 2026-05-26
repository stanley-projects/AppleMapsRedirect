# Bridge

Android app (previously named "AppleMapsRedirect" — the rename to Bridge happened 2026-05-25) that intercepts `maps.apple.com` links (and shared Apple Maps text) on Android and hands them off to Google Maps / Waze / any installed `geo:` handler via a chooser. Includes a manual paste-and-convert screen for cases where a link arrives as plain text the OS won't route.

- Package: `com.stanley.bridge`
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
- [Codex] Initialized Git repository on main, committed project source/docs, created public GitHub repository at https://github.com/stanley-projects/AppleMapsRedirect, pushed main, enabled GitHub Pages from docs/ at https://stanley-projects.github.io/AppleMapsRedirect/, and created release v1.0.0 with the debug APK asset. Verified assembleDebug succeeds when JAVA_HOME points to Android Studio JBR and ANDROID_HOME points to the local Android SDK.
- [Codex] Updated public-facing wording per user direction to say "or any other maps app of your choosing" in the GitHub repository description, README, and docs front page. Commits on main include 11b3f8c initial project, 590a198 intermediate wording cleanup, cf0d58f requested wording, and ad8f8f8 permission preference note.
- [Codex] User preference clarified: avoid unnecessary permission prompts. Proceed automatically for routine reads, edits, builds, tests, Git operations, metadata checks, and verification when tools allow it. Ask only when the environment blocks the action or when the operation is genuinely risky, such as destructive filesystem changes, credential/auth changes, installs/downloads, remote deletion/publishing changes, or actions likely to harm files, accounts, repos, or system state.
- [Codex] Also persisted the permission prompt preference globally at C:/Users/HP/.codex/memories/permission-style.md so future Codex sessions across projects should remember to minimize unnecessary approval prompts.
- [Claude] Full app redesign via the impeccable skill (manual via Read since the skill is installed at ~/.claude/skills/impeccable but not registered with the harness). Wrote PRODUCT.md (register=product, principles: invisible-by-default, one-decision-per-screen, system-native, quiet-over-loud, map-agnostic). Shape brief confirmed by user; craft applied to activity_main.xml, themes (adaptive light + dark), colors (restrained deep-ink accent, OKLCH-derived tinted neutrals), and MainActivity state machine (Convert disabled until 'maps.apple.com' typed; opacity fade-in with reduced-motion respect; error vs success use different typefaces).
- [Claude] Renamed AppleMapsRedirect -> Bridge across the project: strings.xml app_name, settings.gradle.kts rootProject.name, Kotlin package com.applemapsredirect -> com.stanley.bridge (files moved to app/src/main/java/com/stanley/bridge/), namespace + applicationId, README.md and docs/index.html body text and GitHub URLs. CLAUDE.md title updated; historical Codex agent-log entries preserved verbatim per protocol. Local project folder rename (AppleMapsRedirect\ -> Bridge\) deferred — cannot rename while Claude Code session is running inside it.
- [Claude] Fixed AppleMapsParser to handle modern Apple Maps URL format (`coordinate=lat,lng` and `name=...`) in addition to legacy `ll=`, `q=`, `address=`, `daddr=`, `saddr=`, `/place/<slug>`. When both `coordinate` and `name` are present, the coordinate is used for routing and the name is preserved as a Google Maps label via `geo:lat,lng?q=lat,lng(Label)`. Original URL the user surfaced — `https://maps.apple.com/place?coordinate=33.89713%2C-83.97469&name=Location` — was the trigger; old parser only recognized `ll`.
- [Claude] Created Bridge release keystore at C:\Users\HP\Coding Projects\Android Key Stores\Bridge\bridge.jks (alias bridge-key, 2048-bit RSA, valid 25 years, same DN as Spass Converter per user preference: `CN=Stanley Nwobosi, O=Stanley, L=Grayson, ST=Georgia, C=US`). Wrote credentials.txt next to it documenting password / alias / DN / wiring. Also wrote a matching credentials.txt for the existing Spass Converter keystore at the user's request. Release signing wired into app/build.gradle.kts reading from gitignored local.properties (`RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_PASSWORD`). Built and verified app-release.apk — signer cert DN matches Bridge keystore, not the universal Android debug key.
- [Claude] Local-only work complete. Debug APK shipped to C:\Users\HP\Downloads\bridge.apk for user design verification. Public-facing steps (GitHub repo rename stanley-projects/AppleMapsRedirect -> Bridge, release v1.0.0 asset replace with release-signed APK, repo description / homepage update, optional Unsplash hero drop on docs/index.html) intentionally deferred until user confirms the redesign on their phone.
- [Claude] User installed and approved the new design ("i like the look of the app"), but caught a real-world UX gap: Android does not auto-enable per-domain link handling for apps that don't own the domain. autoVerify=true would fail because Apple won't host an assetlinks.json for us. Built a first-run onboarding flow to close the gap: new OnboardingActivity + activity_onboarding.xml shows on first launch only (gated by BridgePrefs.onboarding_seen in SharedPreferences), one-tap deep link to Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS (API 31+) with fallback to ACTION_APPLICATION_DETAILS_SETTINGS, auto-dismisses if user returns from settings with maps.apple.com verified. Also added a small status chip on MainActivity that appears only when auto-bridging is OFF ("Auto-bridging is off — tap to set up"), honoring the "invisible by default" principle. Verification detection via DomainVerificationManager.getDomainVerificationUserState on API 31+ (no equivalent on older APIs; chip shows as a nudge there too). README.md and docs/index.html updated with auto-bridging setup instructions and the platform-security explanation so downloaders know why the one-time toggle exists.
- [Claude] Replaced the green-pin app icon (leftover from the original scaffold; flagged in screenshots; matches the Google-Maps-clone anti-reference in PRODUCT.md). New Bridge mark: two cream anchor dots connected by an arched span on a deep-ink (#1B1F28) background, monochrome and geometric. Adaptive icon updated (drawable/ic_launcher_foreground + ic_launcher_background), round variant added (mipmap-anydpi-v26/ic_launcher_round.xml), and AndroidManifest fixed to reference @mipmap/ic_launcher + @mipmap/ic_launcher_round instead of pointing directly at the foreground vector (which is why only the green pin was rendering before).
- [Claude] Dropped the Unsplash hero on docs/index.html. Replaced the layered linear-gradient + Unsplash url() with a simple cream-to-sage linear gradient (`linear-gradient(135deg, #ffffff 0%, #f3f6f3 100%)`). No more external CDN dependency on every page view. Also updated the 4-step "How it works" section to reflect the new auto-bridging onboarding flow (step 2 is now "Enable auto-bridging" rather than "Choose redirect").
- [Claude] Fresh bridge.apk (6.9 MB) at C:\Users\HP\Downloads\bridge.apk contains: onboarding flow, status chip, new Bridge launcher icon, parser fix, restyled error vs success typeface. Still local-only — GitHub repo rename / v1.0.0 asset replacement / pages homepage update still queued behind user verification of this build.
