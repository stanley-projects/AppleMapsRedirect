# Product

## Register

product

## Users

Android users who regularly receive `maps.apple.com` links from iPhone-using friends, family, or colleagues, and are tired of being dumped into the browser. Context of use: a single tap from a chat, email, or shared text, in the middle of doing something else (meeting a friend, looking up a venue). The user does not want to "use an app" — they want the link to open in the maps app they already prefer, with as few decisions as possible.

## Product Purpose

Close the small daily friction of the iOS-to-Android maps gap, one link at a time. Success is measured by absence: a tapped Apple Maps link opens in Google Maps / Waze / Organic Maps without the user thinking about Bridge at all. The manual paste-and-convert screen is the backup case, not the main attraction — it exists for the moments Android can't intercept the link (plain-text pastes, awkward share sheets).

## Brand Personality

Calm, exact, invisible. The app should feel like a system utility, not a product. Voice is plain, neutral, almost absent. No exclamation marks, no marketing tone, no personality stickers. The visual presence should be quiet enough that the user forgets which app produced the action.

Closest reference: Apple's own Shortcuts / system UI — restrained typography, generous whitespace, system-native feel. The cross-platform irony is intentional: a tool that bridges Apple's ecosystem to Android should carry some of Apple's quiet design discipline.

## Anti-references

- **The current design.** Busy, loud, mid-2010s Material Design with a saturated green accent and stacked cards. Do not repeat it.
- **A Google Maps clone.** No Google-Maps green (`#34A853`), no pin iconography, no leaning on Google's brand language. Bridge is map-agnostic; the visual language should not endorse one destination over another.
- **Skeuomorphic / decorative.** No fake textures, no shadow-on-shadow elevation, no glassmorphism, no gradients for decoration. No icons-for-the-sake-of-icons.

## Design Principles

1. **Invisible by default.** The redirect happens silently. The manual screen exists for fallback, not as a daily destination. Optimize for the case where the user opens the app rarely.
2. **One decision per screen.** Paste, convert. No tabs, no settings, no menus, no upsell. If a control isn't load-bearing, remove it.
3. **System-native, not skinned.** Use the platform's defaults — native typography stack, system color tokens where reasonable, adaptive light/dark following the OS. Bridge should look like it shipped with Android, not like it was branded by a startup.
4. **Quiet over loud.** Restrained color strategy. Tinted neutrals carry most of the surface. Color earns its place by communicating state (error, success), never by decorating.
5. **Map-agnostic.** The brand sits between map providers, not inside any one of them. Avoid the green/red/blue tribal palettes of Google / Apple / Waze.

## Accessibility & Inclusion

- WCAG 2.2 AA contrast minimum on all text and interactive elements, in both light and dark themes.
- Touch targets ≥48dp.
- Respect `prefers-reduced-motion` — no decorative animation. State transitions only.
- Single-action flow works for users on screen readers (TalkBack): clear labels, no icon-only controls without `contentDescription`.
- No reliance on color alone to convey state (error message has text, not just red).
