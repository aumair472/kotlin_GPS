# Prompt — Final Production Audit

Perform a zero-assumption final audit of GeoSnap. Start from a clean checkout/build state and verify every item in `DEFINITION_OF_DONE.md` with evidence.

Run clean lint/tests/debug build/release bundle, migration tests, instrumentation suite, benchmark/profile generation, and bundle/device installation. On real devices execute: first launch and returning launch; every language including RTL sanity; onboarding swipe/skip; photo capture with precise, approximate, disabled, and denied location; video with/without audio; rotation/focus/zoom/flash; collection filters/search/share/delete; report draft/process death; PDF export/open/save/share; template changes; settings/legal.

Inspect final JPEG EXIF, visible photo/video stamps, MediaStore entries, Room state, WorkManager recovery, shared content URIs, release manifest, permissions, backup/network config, R8 output, and logs for sensitive data. Compare every screen against references at target size and adaptive sizes.

Update all task/evidence files. Do not modify reference assets or weaken tests. Final output must list exact verified successes, blocked hardware/account items, residual risks, AAB path, and release recommendation. “Production ready” is allowed only if no P0/P1 exists and all mandatory evidence passes.
