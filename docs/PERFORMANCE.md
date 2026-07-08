# Performance and Reliability Plan

## Performance goals

Use measured baselines rather than guesses. Initial targets on a representative mid-range device:

- Cold startup to first usable non-camera screen: target <1.5 s where hardware permits.
- Returning launch to camera shell: target <2.0 s; camera stream readiness measured separately.
- Camera control response: no main-thread blocking.
- Photo shutter feedback: immediate; finalization completes in background with visible state.
- Collection scrolling: smooth with thumbnails, not full-resolution media.
- Search/filter update: target <300 ms for expected local dataset.
- Report export: progress shown; no ANR; bounded memory.
- Crash-free capture/export flows across device matrix.

These are engineering targets, not guarantees; record actual benchmark hardware and results.

## Startup

- Use Android splash API.
- Initialize only essential preferences/database on startup.
- Lazy initialize camera/location/media exporters.
- Avoid reading the entire media database before first frame.
- Add Startup and Baseline Profiles covering startup and critical journeys.

## Camera

- Bind/unbind with lifecycle.
- Reuse executors responsibly and shut them down.
- Avoid per-frame allocations.
- Do not run reverse geocoding on every location update.
- Throttle coordinate UI updates while retaining the best latest snapshot.
- Select sensible resolution/quality with device fallback.

## Images

- Query image bounds before decode.
- Generate and cache small thumbnails.
- Use an image loader with size constraints and content URI support.
- Never hold multiple full-resolution bitmaps in a collection/report list.
- Recycle/release temporary resources through structured scopes.

## Video

- Do not transcode on main thread.
- Use WorkManager for durable post-processing.
- Expose progress and cancellation.
- Check free storage before record/export.
- Preserve raw source until final output validation.
- Test thermal and long-recording behavior.

## Database

- Add indexes for filter/sort columns.
- Use paging or bounded queries for large collections.
- Avoid N+1 relation queries.
- Use Flow distinctness and debounced search.
- Profile queries with realistic 1k, 10k, and larger media metadata fixtures.

## Compose

- Keep state stable/immutable.
- Provide keys to lazy lists/grids.
- Avoid reading rapidly changing camera/location state high in the entire composition tree.
- Isolate timer/recording updates.
- Use derived state and memoization where measured.
- Test jank using Macrobenchmark and traces rather than premature micro-optimization.

## Battery

- No background location in MVP.
- Stop high-accuracy updates when camera is not active.
- Do not keep camera, microphone, or wake locks after lifecycle stop.
- Constrain non-urgent WorkManager tasks.
- Avoid repeated media rescans.

## Reliability cases

Test and recover from:

- low storage during capture/export;
- camera disconnect/in-use by another app;
- location provider disabled;
- permission revoked mid-session;
- process death during report edit/video processing;
- corrupted/missing media URI;
- device rotation and multi-window;
- app update with database migration;
- long collection and report lists.

## Benchmark suite

Macrobenchmark critical journeys:

1. Cold startup to Camera shell.
2. Navigate Camera → Collection and scroll.
3. Navigate Reporting and scroll cards.
4. Open Templates and select a template.
5. Open existing report draft.

Hardware capture latency is additionally measured with instrumented timestamps on physical devices.
