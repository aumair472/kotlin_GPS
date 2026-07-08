# Stitch Asset Integration Procedure

## Inventory

Run a recursive inventory of `stitch_geosnap/` and record:

- folder name;
- contained image/vector/font/layout files;
- pixel dimensions;
- apparent target screen;
- whether it is final, enhanced, duplicate, or unused;
- destination Android resource name;
- license/source status.

Create `docs/STITCH_ASSET_MAP.md` from this inventory.

## Priority

When multiple variants exist:

1. Use explicit “updated” or “enhanced” export when it matches the supplied latest screenshot.
2. Compare variants visually rather than choosing by name alone.
3. Record the selected source and why.
4. Keep original Stitch files unchanged.

## Conversion

- Raster images: preserve quality, crop only according to screenshot, consider WebP/AVIF where supported and visually lossless.
- SVG/vector: convert to Android VectorDrawable only if features are supported; otherwise use raster/vector asset safely.
- Logos: prefer provided vector or high-resolution transparent asset.
- Fonts: use only licensed included fonts; otherwise system fallback.
- Generated HTML/CSS: use for measurement/reference only, not as runtime WebView UI.

## Image loading

Onboarding images are packaged resources and should be decoded efficiently. Collection/report thumbnails are runtime content URIs and must use size-aware async loading.

## Screenshot reconstruction

Do not use the full screenshot as one background image. Build real controls, text, lists, and navigation in Compose. Only photographic/artwork areas should use source imagery.

## Asset validation

- no accidental EXIF/private metadata in bundled images;
- no huge unnecessary dimensions;
- no copyrighted third-party trademarks without rights;
- no duplicate assets increasing APK size;
- dark/light contrast and crop tested on target aspect ratios.
