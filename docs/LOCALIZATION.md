# Localization and RTL Plan

## Supported locales

`en`, `ur`, `ar`, `hi`, `fr`, `es`, `pt`, `de`, `it`, `ja`, `zh-CN`.

## Resource rules

- Every user-facing string lives in resources.
- Use placeholders, plurals, and quantity strings correctly.
- Never concatenate translatable sentence fragments.
- Keep technical coordinate values separate from surrounding translated labels.
- Date/time formatting follows locale while the stored instant/timezone remains language independent.
- Provide translator comments for ambiguous field-report terminology.

## Per-app language

Use AndroidX AppCompat/per-app locale support appropriate to the selected architecture. The first-launch language screen writes the locale and confirmation preference. Settings reuses the same locale catalog and selection component.

## RTL

Arabic and Urdu must:

- set application RTL support;
- use `start`/`end` padding and alignment;
- mirror navigation arrows and directional icons where semantically correct;
- preserve latitude/longitude numeric readability using bidi-safe formatting;
- keep camera controls intuitive and tested rather than blindly mirrored.

## Fonts and glyphs

Use a font stack that contains Urdu, Arabic, Devanagari, Japanese, and Chinese glyphs. If a custom Latin font lacks glyphs, define locale-aware fallback families or use system fonts. Never ship missing-square glyphs.

## Layout testing

Test:

- long German and French labels;
- Urdu and Arabic at normal and 200% font scale;
- Japanese/Chinese line wrapping;
- pseudolocales `en-XA` and `ar-XB`;
- 320dp width compact phone;
- landscape and split-screen where supported.

## Translation quality gate

Machine-generated translations may be used only as drafts. Before public release, each locale requires human review for product terms, permissions, privacy disclosures, reporting labels, and legal pages.

## Coordinate and timestamp formatting

- Provide decimal degree display with a consistent precision policy.
- Do not localize the underlying decimal representation stored in metadata.
- Support N/S/E/W labels in UI resources.
- Use user-selected 12/24-hour preference through platform format unless product explicitly overrides it.
