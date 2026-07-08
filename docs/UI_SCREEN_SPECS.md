# UI Screen Specifications

## 1. Splash

Reference: `reference_screenshots/01_splash.png` and Stitch folder `geosnap_splash_screen`.

Required behavior:

- White full-screen background.
- Centered circular GeoSnap camera/location logo, product name, and tagline.
- Bottom progress track, animated primary progress, and version text.
- Use Android 12+ SplashScreen API for process launch, then the branded composable screen.
- Read language/onboarding preferences and initialize required local services.
- No arbitrary multi-second wait. Apply a short minimum visual duration only to avoid a flash.
- Navigate exactly once and remove Splash from the back stack.

## 2. Language selection

References: `02_language.png` and `11_language_extended.png`.

Languages for the initial UI:

- English `en`
- Urdu `ur` — RTL
- Arabic `ar` — RTL
- Hindi `hi`
- French `fr`
- Spanish `es`
- Portuguese `pt`
- German `de`
- Italian `it`
- Japanese `ja`
- Simplified Chinese `zh-CN`

Behavior:

- Header with close/back icon and title.
- Optional logo/intro section on first launch; settings variant may omit it.
- Single-select bordered language cards.
- Selected card uses primary border, light-blue background, and selected radio.
- Continue is disabled only when no selection exists.
- Apply app locale through supported per-app locale APIs and persist selection.
- UI changes immediately and handles RTL without restart loops.

## 3. Onboarding pager

References: `03`, `04`, and `05` onboarding screenshots.

Pages:

1. Stamp every photo with GPS.
2. Real-time location and time data.
3. Generate reports instantly.

Behavior:

- Horizontal swipe in both directions.
- Pager indicator follows current page.
- Skip goes to the final app entry flow and marks onboarding complete.
- Next advances one page; final button is “Get Started”.
- Back gesture/page handling is predictable.
- Page state survives rotation.
- Do not request permissions inside a swipe page. Ask in context when entering the camera or through a dedicated rationale step if added.

## 4. Camera

Reference: `06_camera.png` and Stitch `main_camera_interface`.

Required content:

- Dark top header with current coordinate summary, GeoSnap title, and Settings icon.
- Full camera preview.
- Focus framing guides.
- Visible timestamp/location overlay preview.
- White bottom control sheet with four main navigation destinations.
- Photo and Video mode controls.
- Latest thumbnail, shutter/record control, and secondary control.

Required behavior:

- Real rear camera preview; optional front lens switch if supported.
- Photo capture and video recording.
- Tap-to-focus, pinch-to-zoom, flash/torch state, orientation handling.
- Real location/altitude/accuracy state; no hardcoded values.
- Location indicator states: acquiring, accurate, approximate, unavailable, disabled.
- Capture works when location is unavailable after clear user feedback.
- Selected timestamp template is previewed and applied to final output.
- Prevent double capture while finalizing.
- Recording shows duration, audio state, pause/resume only if implemented reliably, and stop.
- Camera resources release when destination/lifecycle stops.

## 5. Collection

Reference: `07_collection.png`.

Required content:

- Title and back/up/filter action as in reference.
- Search by location/date.
- Filter chips: All, Today, This Week, Videos, Photos.
- Date-sectioned media grid.
- Video badge, location label overlay, missing/processing state.
- Persistent bottom navigation.

Required behavior:

- Data is queried from Room, not hardcoded.
- Search is debounced and cancellable.
- Filters combine correctly.
- New captures appear reactively.
- Tap opens media detail; long press enters selection mode.
- Share/delete actions use real files and confirmation.
- Failed/broken URI cards offer repair/remove rather than showing a generic broken image forever.

## 6. Reporting list

Reference: `09_reporting.png`.

Required content:

- Top bar title and plus action.
- Report search field.
- Status chips: All Reports, Draft, Exported, Shared.
- Cards with title, location, date/time, preview thumbnails, attachment count, status badge, share action.
- Persistent bottom navigation.

Behavior:

- Plus creates a persistent draft and navigates to New Report.
- Search and status filters query Room.
- Share is enabled only when an export exists; otherwise prompt export.
- Status reflects real lifecycle events.

## 7. New/Edit Report

Reference: `08_new_report.png`.

Fields:

- Report title
- Location with refresh
- Date
- Time
- Notes/description
- Attached media
- GPS summary

Behavior:

- Save Draft persists immediately.
- Add photos opens an in-app collection picker; optional system picker may import external media only through user selection.
- GPS card derives from report location or selected media according to a documented rule.
- Validate title and at least one attachment before final submission unless product requirements later change.
- Submit creates/updates report and offers export.
- Process death restores draft by ID.

## 8. Timestamp Templates

Reference: `10_templates.png`.

Required content:

- Top bar with title and plus action.
- Filter chips: All, Minimal, Detailed, Classic.
- Template cards with preview, title, description, and radio/check selection.
- Built-ins: Minimal, Classic, Detailed, Reporter.
- Persistent bottom navigation.

Behavior:

- Selecting a card updates DataStore and camera preview.
- Preview uses real formatter logic, not a static screenshot.
- Plus opens a custom template editor only after built-ins are complete; it may be feature-flagged for a later phase.

## 9. Settings

Reference: `12_settings.png`.

Required rows:

- Language
- Share App
- Privacy Policy
- Terms & Conditions
- App logo, product name, and version

Behavior:

- Language opens the extended selection screen.
- Share App uses a chooser and does not crash before the Play listing exists; use a configurable URL.
- Privacy and terms open local content or secure HTTPS URLs.
- Version comes from build configuration.
- Settings is not a bottom-navigation destination; the reference bottom bar shown in the mockup must not create inconsistent navigation state. When opened from Camera, either retain the shell correctly or hide it consistently according to the final Stitch flow.

## Common states required on every data screen

- Loading
- Empty
- Content
- Recoverable error with retry
- Permission-required state where relevant
- Offline state only if a future network feature exists
- Accessibility and RTL layouts
