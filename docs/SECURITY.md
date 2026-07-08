# Security Engineering Plan

## Threat model

Protect against:

- unauthorized reading of private drafts or source media;
- malicious or malformed external URIs;
- path traversal or unsafe filenames;
- leaked secrets/signing configuration;
- insecure file sharing;
- accidental location disclosure in logs or analytics;
- database corruption or destructive migration;
- tampered or missing media referenced by reports;
- cleartext network traffic if network features are added;
- exported Android components being invoked unexpectedly.

GeoSnap cannot fully prevent a device owner with root access or clock/location spoofing from manipulating evidence. Document this limitation honestly.

## Storage security

- Keep intermediate media and thumbnails in app-private storage.
- Use MediaStore only for user-visible finalized media according to product settings.
- Exclude sensitive private caches/drafts from backup when appropriate through data extraction rules.
- If encryption at rest is added for sensitive private files, keep keys in Android Keystore and use supported cryptography; never hardcode keys.
- Validate every persisted URI before use and handle revoked grants.

## Sharing

- Use `content://` URIs.
- Use `FileProvider` for app-private exports.
- Grant temporary read permission through the share intent.
- Never expose internal paths or `file://` URIs.
- Restrict provider paths to the exact export directory.
- Use Android chooser for user control.

## Components and intents

- Set `android:exported="false"` unless a component intentionally needs external access.
- Validate all incoming intents and MIME types.
- Do not accept arbitrary output paths from intents.
- Avoid mutable PendingIntents; if later required, choose explicit mutability.

## Network

MVP should not require a backend. If HTTPS links or future APIs are used:

- disable cleartext traffic;
- use Network Security Configuration;
- keep secrets on a server, not in the APK;
- apply timeouts, certificate validation, and safe error handling;
- never pin certificates without an operational rotation plan.

## Input validation

- Bound report title and notes lengths.
- Normalize control characters in filenames and PDF text.
- Validate coordinate ranges, dates, enum values, and media MIME types.
- Reject unexpectedly huge imported files before decoding.
- Decode images with sampled bounds to avoid memory exhaustion.

## Database

- Use foreign keys and transactions.
- Test migrations.
- Do not enable destructive migration in release.
- Parameterize all queries through Room.
- Treat database content as untrusted when rendering/exporting.

## Logging

Release logs must redact:

- exact latitude/longitude;
- addresses;
- report notes/titles when potentially sensitive;
- content URIs and local paths;
- filenames containing user text;
- signing data, tokens, and device identifiers.

Use event codes and coarse diagnostics instead.

## Supply chain

- Use version catalogs.
- Prefer stable AndroidX/Google libraries.
- Enable dependency verification/locking where practical.
- Run dependency vulnerability review before release.
- Remove unused libraries and debug tooling.

## Build security

- Keep upload keystore outside source control.
- Load signing values from local/CI secrets.
- Enable R8 minification and resource shrinking for release after tests pass.
- Verify release manifest, exported components, backup configuration, and network policy.
- Produce SBOM/dependency report if the organization requires it.

## Security acceptance tests

- Share URI works for recipient and expires with temporary grant semantics.
- No internal path appears in logs or intents.
- All components have intentional export state.
- Cleartext traffic is rejected.
- Imported malformed URI/file fails safely.
- Revoked permission/URI access produces recoverable UI.
- Release APK/AAB contains no test credentials or local absolute paths.
