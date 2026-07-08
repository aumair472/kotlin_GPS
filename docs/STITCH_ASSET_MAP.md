# Stitch Asset Map

Recursive inventory of `stitch_geosnap/` (read-only design source) mapped to reference screenshots, target Compose routes, and Android resource names. **Never modify or delete anything under `stitch_geosnap/`.** Assets are copied into `app/src/main/res` with the stable names below.

## Screen sources → Compose routes

| Stitch folder | `code.html` | `screen.png` | Reference screenshot | Target route / screen | Notes |
|---|:---:|:---:|---|---|---|
| `geosnap_splash_screen` | ✓ | ✓ | `01_splash.png` | `splash` (system + branded) | Logo + "GeoSnap" #2563EB + tagline "Capture. Locate. Report." + progress bar + `v1.0.0`. |
| `geosnap_circular_logo` | ✓ | ✓ | — | brand asset | Blue circle, white camera + location-pin glyph. Source for `ic_geosnap_logo`. |
| `select_language` | ✓ | ✓ | `02_language.png` | `language` (first launch) | Close (X), title "Select Language", logo, radio cards (native + endonym), Continue. |
| `select_language_updated` | ✓ | ✓ | `11_language_extended.png` | `language` / `language-settings` | Updated/extended list variant; authoritative for the 11-locale catalog. |
| `onboarding_step_1` (+`_enhanced_visuals`) | ✓ | ✓ | `03_onboarding_gps_stamp.png` | `onboarding` page 1 | "Stamp Every Photo with GPS". Enhanced = final photographic visual. |
| `onboarding_step_2` (+`_enhanced_visuals`) | ✓ | ✓ | `04_onboarding_realtime_location.png` | `onboarding` page 2 | Real-time location. |
| `onboarding_step_3` (+`_enhanced_visuals`) | ✓ | ✓ | `05_onboarding_reports.png` | `onboarding` page 3 | Reports. Last page CTA = "Get Started". |
| `main_camera_interface` | ✓ | ✓ | `06_camera.png` | `camera` | Dark header (GPS chip, title, settings), edge-to-edge preview, viewfinder corners, metadata overlay, white control panel (mode tabs, gallery, shutter, lens). |
| `collection_screen` | ✓ | ✓ | `07_collection.png` | `collection` | Search, filter chips (All/Today/This Week/Videos/Photos), date group headers, square thumbnail grid, location label, video badge, bottom nav. |
| `new_report_screen` | ✓ | ✓ | `08_new_report.png` | `report/{id}/edit` | Title, Location (+refresh), Date/Time, Notes, Attached Photos (+/See All), GPS Data card (LIVE), Submit. |
| `reporting_screen` | ✓ | ✓ | `09_reporting.png` | `reporting` | Search, status filter chips (All/Draft/Exported/Shared), report cards w/ status badge, thumbs, counts, share. |
| `timestamp_templates` (+`_enhanced_previews`) | ✓ | ✓ | `10_templates.png` | `templates` | Filter row, selectable cards: Minimal, Classic, Detailed, Reporter w/ live preview. |
| `settings_screen` | ✓ | ✓ | `12_settings.png` | `settings` | PREFERENCES list (Language, Share App, Privacy Policy, Terms), logo + version footer. |

## Photographic assets (Stitch demo imagery — NOT shipped as app content)

These are landmark/landscape stock renders used only inside Stitch mockups. They must **not** be bundled as captured media or sample cards. They may seed *onboarding illustration* slots only.

| Folder | Use |
|---|---|
| `a_high_quality_colorful_photograph_..._taj_mahal_or` | mockup filler only |
| `a_professional_..._construction_site_or_engineering` | candidate onboarding/illustration |
| `a_professional_..._scenic_landscape_like_a_mountain` | candidate onboarding illustration |
| `beautiful_colorful_landscape_of_the_grand_canyon...` | onboarding step 1 visual reference |
| `colorful_aerial_view_of_the_eiffel_tower...` | mockup filler only |
| `colorful_view_of_the_great_wall_of_china...` | mockup filler only |
| `vibrant_street_scene_in_tokyo_at_night...` | mockup filler only |

## Resource name mapping

| Source | Android resource | Type |
|---|---|---|
| `geosnap_circular_logo/screen.png` | `res/drawable-nodpi/geosnap_logo.webp` (transparent corners, lossless, 4 KB) — used in splash, language header, settings footer | WebP |
| `geosnap_circular_logo` glyph (reproduced) | `ic_geosnap_logo.xml` (vector) — system Android-12 splash + adaptive launcher icon foreground | vector |
| brand wordmark color | `colorPrimary` `#2563EB` | color token |
| `onboarding_step_1_enhanced_visuals` hero photo (lh3 CDN ref) | `res/drawable-nodpi/img_onboarding_gps_stamp.webp` (1080-capped, q80) | WebP |
| `onboarding_step_2_enhanced_visuals` hero photo | `res/drawable-nodpi/img_onboarding_realtime_location.webp` | WebP |
| `onboarding_step_3_enhanced_visuals` hero photo | `res/drawable-nodpi/img_onboarding_reports.webp` | WebP |

**Onboarding image source note:** the enhanced-visual mockups reference their hero photos from `lh3.googleusercontent.com` (not embedded). Those exact source images were fetched, downscaled, and saved as WebP into app resources. The original `stitch_geosnap/` files were not modified. Conversion: `python` + Pillow (see commit). Wired in `feature/onboarding/OnboardingScreen.kt` (swipe + page indicators preserved).

The brand logo is reproducible as a vector (`ic_geosnap_logo.xml`) so it scales for the adaptive launcher icon, splash, language header, and settings footer without shipping the long Stitch folder names. The decoded PNG is kept at `docs/extracted_assets/geosnap_logo.png` for pixel reference.

## Design tokens (authoritative: `stitch_geosnap/Design/DESIGN.md` + `docs/DESIGN_SYSTEM.md`)

- Primary `#2563EB` (`primary-container`), deep primary `#004ac6`, on-primary `#FFFFFF`.
- Background `#FFFFFF` / surface `#F9FAFB` (token `surface #faf8ff`), text `#111827` (`on-surface #191b23`).
- Divider/outline `#E5E7EB` (`outline-variant #c3c6d7`), secondary text `#6B7280`.
- Type: Hanken Grotesk (UI) + Geist mono (coordinates/timestamps). Flat — no shadows; 1px dividers; 4px/8px radii; 8px grid; 44dp min touch target.

These are encoded in `core/designsystem` (`Color.kt`, `Type.kt`, `Theme.kt`, `Dimens.kt`).
