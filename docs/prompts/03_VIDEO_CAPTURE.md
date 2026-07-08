# Prompt — Video Capture and GPS/Time Overlay

Complete real CameraX video recording. Ask for microphone permission only when recording with audio; allow silent recording after denial. Implement start/status timer/stop/finalize, lifecycle interruption, torch, rotation, low-storage errors, and playable MP4 output.

Choose the production overlay strategy after checking current code/device support:

- CameraX OverlayEffect applied to recording, or
- Media3 Transformer post-processing with a durable WorkManager job.

Document the decision in an ADR. The final video must contain the selected timestamp/GPS template or the product contract must be explicitly revised; preview-only overlay is not enough. If post-processing, persist provisional source and PROCESSING/READY/FAILED states, progress, unique idempotent work, retry/cancel, process-death recovery, output validation, and safe cleanup only after success.

Persist start capture metadata and final media metadata. Collection must not expose an unreadable “ready” item. Add tests for reducers/repository/worker and real-device verification with audio granted/denied, background interruption, playback, and overlay inspection.
