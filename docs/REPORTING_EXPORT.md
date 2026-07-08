# Reporting and PDF Export Design

## Report lifecycle

```text
DRAFT → EXPORT_QUEUED → EXPORTING → EXPORTED → SHARED
                         ↘ FAILED (retryable)
```

The report remains editable after export. Editing creates a stale-export indicator until a new export version is generated.

## Draft behavior

- Create a report ID immediately when entering New Report.
- Debounce text writes but flush on lifecycle stop.
- Media additions/removals persist transactionally.
- “Save Draft” confirms persistence; it is not the only point where data is saved.
- Empty abandoned drafts may be cleaned only after a defined retention policy and confirmation logic.

## Attachment selection

Primary picker: in-app Collection selection by media IDs. Optional external attachment: Android Photo Picker/SAF with explicit user selection and persisted read permission where available.

Store order explicitly. Show processing/failed media and prevent final export until required attachments are ready.

## GPS summary rule

Use this deterministic priority:

1. Explicit report location selected/refreshed by user.
2. Location of the first attached media.
3. No location.

Never average coordinates silently. A future map/bounds summary can be added as a separate field.

## PDF content

Minimum professional PDF:

- GeoSnap/report header
- report title
- generated date/time and timezone
- location summary and coordinates/accuracy where available
- notes
- ordered media sections with image, capture timestamp, location, and caption
- page numbers
- optional disclaimer about location/device accuracy

Video attachments use a generated thumbnail and metadata; PDF cannot embed playable video by default.

## Rendering

- Use a deterministic page size and margins.
- Decode images to page-required dimensions.
- Preserve aspect ratio.
- Handle portrait/landscape media.
- Escape/control user text safely.
- Break long notes across pages.
- Keep report generation off main thread.
- Close all streams and PDF documents in `finally`/structured resource scopes.

## Output

Two supported flows:

1. Internal export file exposed through FileProvider for quick sharing.
2. “Save As” using `ACTION_CREATE_DOCUMENT` so the user chooses a destination.

Persist the resulting URI and metadata. Never assume a raw filesystem path.

## Sharing

Use `ACTION_SEND` with MIME `application/pdf`, a content URI, temporary read permission, and chooser. Record `shared_at` when the share intent is successfully launched; do not claim the recipient completed sharing because Android generally cannot guarantee that.

## Export tests

- no location;
- approximate location;
- 1, 8, 50+ photos;
- portrait/landscape images;
- video thumbnails;
- long multilingual notes including RTL;
- missing attachment URI;
- low storage;
- cancellation/retry;
- open exported PDF in at least two reader apps;
- share to email/messaging app.
