# Prompt — Security, Performance, and QA Hardening

Audit and harden the completed GeoSnap app against every requirement in `SECURITY.md`, `PERFORMANCE.md`, `PERMISSIONS_PRIVACY.md`, `QA_TESTING.md`, and `DEFINITION_OF_DONE.md`.

Remove unnecessary permissions/components/dependencies. Confirm no background location, all-files access, cleartext traffic, exported component mistakes, hardcoded secrets, private logs, file URIs, destructive migrations, unsafe provider paths, or full-resolution grid decoding. Validate backup rules, input bounds, URI grants, R8 release behavior, and signing configuration.

Complete unit, Room migration, DataStore, repository, WorkManager, Compose, instrumentation, screenshot, failure-injection, physical-device, accessibility, RTL, font-scale, low-storage, process-death, and upgrade tests. Create Macrobenchmarks and Baseline/Startup Profiles for startup and main journeys. Profile camera readiness, collection scrolling, memory, PDF export, battery/resource cleanup.

Generate a release-candidate evidence report with commands, devices/APIs, screenshots/diffs, benchmark results, remaining defects, permission/privacy matrix, and AAB result. Fix every P0/P1 and unaccepted P2. Do not suppress failures to make the report green.
