# Functional Feature Contract

## Camera controls

- Initialize rear camera with lifecycle binding.
- Switch lens when more than one appropriate camera exists.
- Capture JPEG photo.
- Record MP4 video with optional microphone audio.
- Flash modes for photo: off, on, auto where supported.
- Torch for video/preview where supported.
- Tap-to-focus and focus feedback.
- Pinch-to-zoom with bounded ratio.
- Correct output rotation.
- Prevent concurrent capture/finalization conflicts.
- Show errors and recovery actions.

## GPS/time capture

- Foreground precise location request only when camera is being used and user consent exists.
- Continue with approximate location if that is all the user grants.
- Display latitude, longitude, optional altitude, accuracy, timestamp, and timezone.
- Store capture-time instant and location snapshot.
- Resolve address asynchronously and optionally.
- Mark stale or unavailable location clearly.

## Media stamping

- Template preview in camera.
- Visible overlay burned into photos.
- GPS/time metadata written into supported EXIF tags after image processing.
- Video overlay included in the finalized video using CameraX effect or Media3 Transformer; if post-processed, expose processing status.
- Preserve an unformatted machine-readable metadata record in Room.
- Stamp formatter supports locale-aware date/time and coordinate formatting.

## Collection

- Reactive grouped grid.
- Search by address text, coordinate string, report tag, or date text as defined by indexed query strategy.
- Filter by today, current week, photo, video, processing, and favorites if later added.
- View, select, share, and delete media.
- Deletion coordinates MediaStore and Room safely.

## Reports

- Create, edit, auto-save, duplicate, delete.
- Attach/reorder media.
- Add title, notes, report location, date/time.
- Save draft.
- Validate and export PDF.
- Track export versions.
- Share exported file through Android chooser.
- Statuses: Draft, Exported, Shared; maintain an event history rather than relying only on a single mutable label.

## Templates

- Minimal: date/time.
- Classic: date/time and coordinates.
- Detailed: full date/time, coordinates, altitude, and address.
- Reporter: GeoSnap/site report branding plus core metadata.
- Persist selected template.
- Format safely when altitude/address is absent.

## Localization

- All user-facing strings are resources.
- Per-app language preference.
- RTL for Arabic and Urdu.
- Locale-aware dates, times, numerals where appropriate, and translated plurals.
- User-entered report content is never machine-translated automatically.

## Settings/legal

- Change language.
- Share app.
- Open privacy policy and terms.
- Show app version.
- Future-safe entries for units, timestamp format, default template, and capture quality may be added only when designed and tested.
