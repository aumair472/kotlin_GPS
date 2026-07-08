# Agent Roles and Responsibilities

The same coding agent may perform all roles, but each responsibility must be explicitly covered.

## 1. Lead Android architect

Owns module boundaries, dependency direction, navigation, state management, lifecycle behavior, and technical decisions. Prevents feature code from bypassing abstractions.

## 2. Camera and media engineer

Owns CameraX initialization, preview, image capture, recording, flash, lens switching, zoom, focus, orientation, MediaStore writes, thumbnails, EXIF, video post-processing, and low-storage behavior.

## 3. Location and integrity engineer

Owns foreground location acquisition, freshness/accuracy rules, permission handling, GPS snapshot creation, visible stamps, metadata consistency, and “location unavailable” behavior. Never invents coordinates.

## 4. Compose UI engineer

Owns screenshot-faithful layouts, adaptive sizing, system insets, accessibility semantics, focus behavior, animations, RTL layout, previews, and reusable design-system components.

## 5. Data engineer

Owns Room schema, migrations, transactions, search/filter queries, relationship integrity, DataStore preferences, and backup policy.

## 6. Reporting engineer

Owns report drafts, media attachment, validation, PDF generation, export state, share intents, FileProvider/SAF behavior, and recovery from interrupted work.

## 7. Security and privacy engineer

Owns least-privilege permissions, secure URI sharing, release configuration, backup exclusions, log redaction, dependency review, privacy disclosures, and Play policy readiness.

## 8. QA engineer

Owns unit, integration, Compose, instrumentation, migration, screenshot, performance, and physical-device testing. A task cannot close without evidence.

## 9. Performance engineer

Owns startup, camera readiness, frame/jank behavior, memory use, thumbnail loading, report export time, battery usage, Baseline Profiles, and Macrobenchmark.

## 10. Context engineer

Maintains a small, accurate working context. Reads authoritative files first, records decisions, avoids stale assumptions, and links each implementation change to a product requirement.

## 11. Loop engineer

Enforces the understand → implement → test → inspect → fix cycle. Detects false completion, skipped checks, and regressions.

## Cross-agent handoff format

Every handoff must contain:

- Goal and task ID
- Relevant contracts and screenshots
- Files changed
- Public interfaces changed
- Tests added and commands run
- Observed results
- Known limitations or unverified hardware behavior
- Next task and prerequisites

## Forbidden agent behavior

- Generating placeholder screens and calling the feature complete
- Using sample images as captured camera output
- Hardcoding Karachi coordinates or sample report data
- Requesting permissions at app launch without context
- Adding background location “just in case”
- Using `MANAGE_EXTERNAL_STORAGE`
- Loading full-resolution images into collection grids
- Exporting `file://` URIs
- Writing secrets into source control
- Editing design reference files to make comparison easier
