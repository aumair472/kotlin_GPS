---
name: GeoSnap
colors:
  surface: '#faf8ff'
  surface-dim: '#d9d9e5'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3fe'
  surface-container: '#ededf9'
  surface-container-high: '#e7e7f3'
  surface-container-highest: '#e1e2ed'
  on-surface: '#191b23'
  on-surface-variant: '#434655'
  inverse-surface: '#2e3039'
  inverse-on-surface: '#f0f0fb'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#585f6c'
  on-secondary: '#ffffff'
  secondary-container: '#dce2f3'
  on-secondary-container: '#5e6572'
  tertiary: '#943700'
  on-tertiary: '#ffffff'
  tertiary-container: '#bc4800'
  on-tertiary-container: '#ffede6'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#dce2f3'
  secondary-fixed-dim: '#c0c7d6'
  on-secondary-fixed: '#151c27'
  on-secondary-fixed-variant: '#404754'
  tertiary-fixed: '#ffdbcd'
  tertiary-fixed-dim: '#ffb596'
  on-tertiary-fixed: '#360f00'
  on-tertiary-fixed-variant: '#7d2d00'
  background: '#faf8ff'
  on-background: '#191b23'
  surface-variant: '#e1e2ed'
typography:
  headline-lg:
    fontFamily: Hanken Grotesk
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Hanken Grotesk
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  mono-data:
    fontFamily: Geist
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  space-xs: 4px
  space-sm: 8px
  space-md: 16px
  space-lg: 24px
  space-xl: 40px
  container-max-width: 1200px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style

The design system is rooted in the principles of **Precision Minimalism**. It is engineered for a professional GPS camera application where clarity, accuracy, and utility are paramount. The visual language avoids all decorative distractions—such as gradients, shadows, or illustrations—to ensure that the user's data and imagery remain the focal point.

The target audience consists of field professionals, surveyors, and documentation experts who require a reliable, high-performance tool. The emotional response is one of trust and efficiency. By utilizing a strictly flat design aesthetic with ample whitespace, the interface feels lightweight and systematic, reducing cognitive load during critical data capture tasks.

## Colors

The palette is functional and high-contrast. 

- **Primary (#2563EB):** A vivid blue used for primary actions, active states, and critical data highlights. 
- **Secondary/Neutral Gray (#6B7280):** Used for metadata, labels, and non-essential UI elements to create hierarchy through tonal variance rather than weight.
- **Background (#FFFFFF):** A pure white base to maximize the "clean" feel and provide a clinical environment for photo review.
- **Surface (#F9FAFB):** A very light gray used for subtle grouping of content without the need for borders or shadows.
- **Text (#111827):** Deep charcoal for maximum legibility against the white background.

## Typography

The design system utilizes **Hanken Grotesk** for its sharp, contemporary geometry and professional clarity. To support the technical nature of GPS coordinates and timestamps, **Geist** is introduced as a secondary mono-spaced face for data-heavy readouts.

- Use **Headline-LG** for primary screen titles.
- Use **Body-LG** for general information.
- Use **Label-Caps** for metadata headers (e.g., "LATITUDE", "LONGITUDE").
- Use **Mono-Data** specifically for timestamps and coordinate strings to ensure character alignment and readability.

## Layout & Spacing

The layout philosophy follows a strict **8px grid system** to maintain mathematical alignment. 

- **Grid:** A 12-column fluid grid on desktop, transitioning to a single-column flow on mobile.
- **Whitespace:** Use `space-xl` (40px) between major logical sections to emphasize the minimal, airy aesthetic.
- **Margins:** Standard 16px margins on mobile devices to provide "breathability" around the camera viewfinder and data panels.
- **Alignment:** All text elements must be left-aligned to reinforce the professional, document-style layout.

## Elevation & Depth

This design system employs a **Flat Layering** model. Depth is communicated exclusively through color blocks and outlines; shadows and blurs are strictly prohibited.

- **Level 0 (Base):** Pure White (#FFFFFF).
- **Level 1 (Containers):** Soft Gray (#F9FAFB) surfaces are used to group related information.
- **Dividers:** 1px solid lines (#E5E7EB) are the primary tool for separation.
- **Interaction:** State changes (hover/active) are signaled by shifting the background color to a slightly darker shade or by swapping the primary color with the text color.

## Shapes

The design system uses a **Soft (0.25rem)** roundedness approach. This subtle rounding removes the harshness of a strictly 0px grid while maintaining a disciplined, professional appearance.

- **Standard Elements:** 4px radius (Buttons, Input Fields).
- **Large Elements:** 8px radius (Cards, Image Containers).
- **Icons:** Use linear, 2px stroke icons with square terminals to match the typography's precision.

## Components

### Buttons
- **Primary:** Solid #2563EB background, White text. No border. 4px radius.
- **Secondary:** White background, 1px solid #E5E7EB border, #111827 text.
- **Size:** Minimum hit target of 44px height for field use.

### Data Chips
- Small, rectangular labels with #F3F4F6 background and #6B7280 text. Used for status tags like "GPS Fixed" or "Syncing."

### Input Fields
- White background with a 1px solid #E5E7EB border. Focus state changes border color to #2563EB. No inner shadows.

### Lists
- Clean rows separated by 1px #E5E7EB lines. No chevrons unless the list item is strictly navigational. High vertical padding (16px) to ensure touch accuracy.

### Camera Overlay (Specialized)
- A transparent overlay using a 1px white stroke for the viewfinder.
- Metadata (Timestamp/GPS) should be rendered in a high-contrast mono-spaced font (Geist) on a semi-opaque #111827 rectangular block for maximum legibility over photos.

### Cards
- No shadows. Use a 1px solid #E5E7EB border or a light #F9FAFB fill to define the container area.