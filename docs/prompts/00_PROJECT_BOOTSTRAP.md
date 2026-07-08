# Prompt — Project Bootstrap

Inspect the entire GeoSnap repository and all files under `stitch_geosnap/`. Read every root engineering MD contract. Do not implement features yet.

Produce:

1. current repository/module/package tree;
2. Gradle/AGP/Kotlin/JDK/SDK/dependency inventory;
3. baseline output of build, lint, and tests;
4. recursive Stitch asset inventory and `docs/STITCH_ASSET_MAP.md`;
5. gap analysis against `ARCHITECTURE.md` and `TASKS.md`;
6. proposed minimal module/package migration plan that preserves working code;
7. risk list for camera, GPS, video overlay, MediaStore, localization, PDF, and release.

Then implement only Phase 1 foundation: version catalog, architecture boundaries, Hilt, Room schema/export, DataStore, navigation shell, design tokens, test foundation, and CI verification script. Use stable compatible dependencies. Run build/tests after each step. Do not add mock capture/report behavior. Update `TASKS.md`, `docs/PROJECT_STATE.md`, `docs/DECISIONS.md`, and verification evidence.
