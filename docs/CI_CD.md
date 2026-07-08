# Build, CI, and Release Automation

## Toolchain

Use a current stable Android Studio/AGP/Kotlin combination supported by the repository. Do not upgrade blindly during feature work. Record JDK, Gradle, AGP, Kotlin, compileSdk, targetSdk, and dependency versions in the verification log.

## CI stages

1. Checkout and dependency verification.
2. Formatting/ktlint and static analysis/detekt if configured.
3. Android lint.
4. Unit tests.
5. Debug assemble.
6. Instrumentation tests on managed/emulated devices for non-hardware paths.
7. Room migration tests.
8. Release bundle compilation with CI secret signing only on protected branches/tags.
9. Artifact retention: reports, test results, mapping file, AAB, benchmark results.

## Suggested commands

```bash
./gradlew --version
./gradlew lint test assembleDebug
./gradlew connectedCheck
./gradlew bundleRelease
```

Add module-specific and benchmark tasks based on the actual repository.

## Signing

- Generate and protect a separate upload key.
- Never commit keystore or passwords.
- Read signing values from environment/CI secret store.
- Use Play App Signing.
- Back up upload key recovery material according to organization policy.

## Build types

- `debug`: debuggable, verbose sanitized diagnostics, test endpoints only if later needed.
- `release`: non-debuggable, R8 minification/resource shrink, no debug menus, strict network/security configuration.
- optional `benchmark`: profileable and configured for Macrobenchmark without weakening release.

## Versioning

- monotonically increasing `versionCode`;
- semantic user-facing `versionName`;
- version shown in Settings from build config;
- tag release commit and preserve schema/export evidence.

## Distribution

- Build AAB.
- Test with bundletool or Play internal track.
- Run pre-launch report.
- Stage rollout and monitor vitals.
- Keep rollback decision and previous stable artifact available.
