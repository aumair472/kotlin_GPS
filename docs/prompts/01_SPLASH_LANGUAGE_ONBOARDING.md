# Prompt — Splash, Language, and Onboarding

Implement Phase 2 using the exact Stitch folders and reference screenshots for splash, language, and onboarding.

Requirements:

- Android 12+ system splash plus branded GeoSnap composable splash with centered logo/name/tagline, bottom animated progress, and real version.
- Startup resolver reads DataStore and routes once: unconfirmed language → Language; incomplete onboarding → Onboarding; otherwise Camera.
- Language catalog supports en, ur, ar, hi, fr, es, pt, de, it, ja, zh-CN. Use per-app locales, persistent selection, immediate update, RTL, font fallback, and all strings in resources.
- First-launch and settings language variants reuse components.
- Three onboarding pages match references, support horizontal swiping both directions, indicators, Skip, Next, Get Started, rotation restoration, and correct back behavior.
- Do not request camera/location permission in onboarding.

Add unit tests for startup resolver, Compose tests for selection/pager/navigation, RTL and large-font previews/tests, and screenshot comparisons. Run build/lint/tests and update evidence. No hardcoded delay or duplicated navigation.
