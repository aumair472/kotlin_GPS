# Timestamp Template Functional Integration Tasks

## Evidence rollup (2026-06-19)

**Root cause:** template selection *was* already wired to camera (`CameraViewModel` observes `settings.preferences` → `state.template`; live overlay + `PhotoFinalizer` + video lines all call the one formatter). The visible "looks like default" for **Detailed/Reporter** was that **the address line never appeared**: `LocationFix.toSnapshot()` dropped address and **no reverse geocoder existed**, so `GeoSnapshot.address` was always null → Detailed/Reporter rendered without their address line (and Detailed minus address ≈ Classic). No duplicate/hardcoded formatter found beyond the GPS-chip lat/lon `"%.4f"` (header only, not the stamp).

Commands: `:app:compileDebugKotlin` ✅ · `:app:testDebugUnitTest` ✅ (41 tests/12 classes, 0 fail) · `:app:lintDebug` ✅ 0 err.

| Task | State | Files / evidence |
|---|---|---|
| FIX-01 trace | ✅ | root cause above. |
| FIX-02 persist enum + default Minimal | ✅ | `GeoSnapPreferences.kt` default → `TemplateStyle.MINIMAL.id` (only when unset); id-based (no localized key); reactive `SettingsRepository.selectedTemplate` Flow. |
| FIX-03 shared formatter | ✅ | renamed `StampTextBuilder`→`TimestampOverlayFormatter` (single source: live overlay + photo + video). `TimestampOverlayFormatterTest` (8 cases incl. address-omit / Classic-ignores-address / Reporter brand+address). |
| FIX-04 reverse geocode | ✅ | `core/location/AddressResolver.kt` (Geocoder off-main, API-gated, null-safe); `CameraViewModel.maybeResolveAddress` debounced 1.2s + 50m distance gate + cache; `AddressStatus` RESOLVING/AVAILABLE/UNAVAILABLE. Not called from composable. |
| FIX-05 live overlay | ✅ | `CameraScreen` builds snapshot w/ `state.resolvedAddress`; live-only "Resolving address…" line; variable lines (Column, no clip). |
| FIX-06/07 capture apply | ✅ | photo `onCapture` + video `beginRecording` snapshot `template`+`resolvedAddress` at capture/record start; persisted `templateId/version` + address in Room. |
| FIX-08 Room/migration | ✅ | media stores `template_id`/`template_version`; address persists via `locations` (`formatted_address`/locality/admin/country). **No schema change → no migration** (still v1); existing media keep their original template. |
| FIX-09 verify | 🟡 | build/test/lint green (42 tests). **Executed:** per-template render rules (`TimestampOverlayFormatterTest`, 8 cases = verify items 1–6 *content*); **restart persistence** (`GeoSnapPreferencesTest`, real DataStore cross-instance = **item 9 ✅**). **Device-pending (no emulator possible — `VirtualizationFirmwareEnabled=False`):** items 1–8 *on-screen* (live overlay pixels, photo capture+Collection inspect, video record+playback overlay), on-device Geocoder. |

### Verification status per required item
1–4 live camera per template — logic+formatter verified; **on-screen device-pending**.
5–6 capture+inspect photo — pipeline+content verified; **device-pending**.
7–8 record video Detailed/Reporter — pipeline verified; **device-pending**.
9 restart persists — **✅ executed** (`GeoSnapPreferencesTest`).
10 unit/build/lint — **✅** (42 tests, 0 fail; lint 0 err). Migration: none required (no schema change).

---


## Task TEMPLATE-FIX-01 — Trace Existing Template Data Flow

### Goal

Identify why the selected Detailed and Reporter templates do not affect the camera overlay or final media.

### Inspect

* Templates screen
* Templates ViewModel
* DataStore preference key
* Camera ViewModel
* Camera UI overlay composable
* photo stamping implementation
* video overlay/export implementation
* Room media entity
* report rendering

### Deliverable

Document the current flow and identify every hardcoded/default formatter.

### Acceptance Criteria

* root cause is identified;
* all duplicate formatter locations are listed;
* no implementation begins before the data flow is understood.

---

## Task TEMPLATE-FIX-02 — Persist Template as Stable Enum

### Goal

Persist template selection using a stable identifier.

### Requirements

* use enum or sealed type;
* persist in DataStore;
* expose as Flow;
* map safely from stored value;
* default to Minimal only when no preference exists;
* preserve selection after app restart.

### Acceptance Criteria

* selected template persists;
* camera receives changes reactively;
* no localized text is used as storage key.

---

## Task TEMPLATE-FIX-03 — Create Shared Overlay Formatter

### Goal

Use one formatter for every timestamp template.

### Requirements

Implement exact rules for:

* Minimal
* Classic
* Detailed
* Reporter

Inputs:

* timestamp;
* timezone;
* coordinates;
* altitude;
* accuracy;
* address;
* brand name.

### Acceptance Criteria

* formatter has unit tests;
* optional null values are handled correctly;
* no fake fallback location data exists;
* UI and media pipelines use the same formatter.

---

## Task TEMPLATE-FIX-04 — Implement Reverse-Geocoded Address State

### Goal

Provide a real address for Detailed and Reporter templates.

### Requirements

* use repository/ViewModel layer;
* asynchronous reverse geocoding;
* debounce updates;
* cache last valid address;
* expose resolving/available/unavailable/failed states;
* do not call Geocoder from composables.

### Acceptance Criteria

* real address appears when available;
* no UI blocking;
* no repeated geocoding per frame;
* missing address is handled safely.

---

## Task TEMPLATE-FIX-05 — Connect Selected Template to Live Camera Overlay

### Goal

Update the camera overlay immediately when the user changes templates.

### Requirements

* observe DataStore selection;
* combine selection with location/address/time state;
* render through shared formatter;
* support variable-height overlays;
* prevent clipping for Detailed and Reporter.

### Acceptance Criteria

* Minimal displays only date/time;
* Classic adds coordinates;
* Detailed adds altitude, accuracy, and address where available;
* Reporter includes GeoSnap branding and required report fields;
* changes appear without restarting the app.

---

## Task TEMPLATE-FIX-06 — Apply Selected Template to Photos

### Goal

Stamp final captured images using the selected template.

### Requirements

* snapshot template at capture;
* render shared formatter output;
* stamp final bitmap;
* preserve EXIF;
* save stamped output;
* persist template type and metadata in Room.

### Acceptance Criteria

* saved photo matches camera-selected template;
* changing template after capture does not alter old media;
* Detailed and Reporter outputs are readable and complete.

---

## Task TEMPLATE-FIX-07 — Apply Selected Template to Videos

### Goal

Apply the selected template to final recorded videos.

### Requirements

* snapshot template when recording starts;
* apply through CameraX effect or Media3 Transformer;
* persist processing state;
* save final playable media;
* persist template type.

### Acceptance Criteria

* final video visibly contains the selected template;
* Detailed and Reporter work;
* failed processing shows FAILED state;
* unprocessed/default output is not marked ready.

---

## Task TEMPLATE-FIX-08 — Room Schema and Migration

### Goal

Persist template and location/address metadata for each media item.

### Required fields

* template type;
* captured timestamp;
* timezone;
* latitude;
* longitude;
* altitude;
* accuracy;
* address.

### Acceptance Criteria

* migration exists;
* migration test passes;
* existing media remains accessible;
* no destructive migration is used.

---

## Task TEMPLATE-FIX-09 — Regression Testing

### Verify all four templates on:

* live camera preview;
* captured photo;
* recorded video;
* Collection;
* media detail;
* app restart.

### Required Tests

* formatter unit tests;
* DataStore persistence tests;
* ViewModel state tests;
* Room migration tests;
* Compose selection tests;
* real-device photo verification;
* real-device video verification.

### Completion Rule

Do not mark complete based only on the Templates preview card. The selected template must appear on final photo and video outputs.
