# Prompt — Camera Screen and Photo Capture

Implement the production Camera destination using `main_camera_interface`, the camera screenshot, and shared design system.

Use CameraX with lifecycle-safe preview, Photo/Video mode state, rear camera default, lens switch where supported, photo flash, video torch, tap focus, pinch zoom, correct rotation, and camera error recovery. The header must show real coordinate status and open Settings. The preview overlay and white rounded control area must visually match the reference and use the four-destination bottom navigation.

Implement contextual camera and foreground coarse/fine location permission flows. Camera denial must not block other app sections. Location states: acquiring, precise, approximate, disabled, unavailable, stale. Never display sample coordinates.

For photo capture, obtain a bounded capture-time location snapshot, capture to private temp JPEG, apply selected visible stamp, write final JPEG to MediaStore, write supported EXIF time/GPS after recompression, persist media/location/template metadata in Room, generate thumbnail, update latest preview, and clean temp files. Prevent double capture and handle low storage/failure.

Add unit/repository/Compose/instrumentation tests. Verify on physical devices: preview, focus/zoom/flash, rotation, capture, visible stamp, readable content URI, Room row, and EXIF coordinates. Record screenshots and evidence; do not claim verified from emulator/build alone.
