# Permissions and Privacy Design

## Manifest permissions for MVP

Required or conditional:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Declare camera hardware appropriately with `uses-feature`; decide whether it is required based on distribution strategy.

Do not request:

- `ACCESS_BACKGROUND_LOCATION`
- `MANAGE_EXTERNAL_STORAGE`
- legacy write storage permissions on modern targets
- notification permission unless a real notification feature requires it

## Runtime request sequence

### Camera

Ask when the user enters Camera and attempts to use preview. Precede the system dialog with a concise in-app rationale explaining that the camera is needed to capture field evidence.

### Location

Ask in context after camera access is available and before enabling GPS stamping. Request coarse and fine together according to platform guidance. The app must function with approximate location and must handle the process restart that can occur when precise access is downgraded.

### Microphone

Ask only when the user starts video recording with audio enabled. On denial, offer silent recording.

## Denial handling

- First denial: show rationale and retry action.
- “Don’t ask again”: show Settings action and a no-permission mode where possible.
- Location denied: camera still captures with explicit “No GPS” status.
- Camera denied: collection/reporting/templates/settings remain accessible.
- Permission revoked while app is paused: re-check on resume and stop affected hardware safely.

## Storage behavior

Write app-created photos/videos with MediaStore. The app can access its own media without broad media-read permission on modern Android. To attach media created by other apps, use Android Photo Picker or Storage Access Framework and persist only user-granted URI access where supported.

## Privacy principles

- Default to local-only processing.
- Do not upload coordinates, media, report notes, or identifiers in MVP.
- Do not log exact coordinates or user notes in production analytics/crash breadcrumbs.
- Explain visible stamps and embedded metadata.
- Provide deletion behavior and clarify exported/shared copies are outside app control.
- Include a privacy policy in app and Play listing before release.

## User-facing disclosure

Before the location permission prompt, disclose:

- location is used while the camera screen is active or at capture time;
- coordinates, time, altitude/accuracy when available may be stored with media and reports;
- stamped/exported/shared files may reveal location to recipients;
- the app does not need background location for MVP.

## Play policy stance

Foreground-only capture-time location is the intended scope. Any future background tracking feature requires a separate product justification, prominent disclosure, Play declaration, and policy review; it must not be added silently.
