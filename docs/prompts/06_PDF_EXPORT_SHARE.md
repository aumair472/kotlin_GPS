# Prompt — PDF Export, Save As, and Share

Implement production report export using the reporting contracts.

Use a durable unique WorkManager job. Generate a professional multi-page PDF containing header, report metadata, notes, GPS/accuracy when available, ordered photo sections, video thumbnails/metadata, page numbers, and safe handling of missing fields. Decode media at page-required size, preserve aspect ratio, paginate long multilingual/RTL notes, close resources, show progress, retry failures, and survive process death.

Support internal export exposed by a tightly scoped FileProvider and user-directed Save As through `ACTION_CREATE_DOCUMENT`. Share with `ACTION_SEND`, `application/pdf`, content URI, temporary read grant, and chooser. Persist export versions, URI, size, checksum if enabled, status, and launched share time. Editing after export marks it stale.

Test 1/8/50+ attachments, mixed orientations, videos, no GPS, approximate GPS, Urdu/Arabic/Japanese, low storage, revoked URI, cancellation/retry, two PDF reader apps, and multiple share targets. Never use file paths or `file://` URIs.
