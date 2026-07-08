# Navigation Contract

## Startup decision

```kotlin
sealed interface StartupDestination {
    data object Language : StartupDestination
    data object Onboarding : StartupDestination
    data object Camera : StartupDestination
}
```

Decision order:

1. If no language has ever been confirmed, open Language.
2. Else if onboarding is incomplete, open Onboarding.
3. Else open Camera.

## Main destinations

Stable route IDs:

```text
camera
collection
reporting
templates
```

The bottom bar must use `launchSingleTop`, restore state, and pop to the main graph start destination so repeated taps do not create duplicate destinations.

## Secondary routes

```text
settings
language-settings
media/{mediaId}
report/{reportId}
report/{reportId}/edit
template/{templateId}/edit
legal/privacy
legal/terms
```

Use encoded IDs, not file paths or raw URIs, as route arguments.

## Back behavior

- Splash: no back navigation.
- First-launch Language: close/back exits only if product explicitly permits; otherwise it stays until selection.
- Onboarding: system back moves to the previous page before leaving the flow.
- Main destinations: back follows Android navigation expectations; Camera is the main start.
- Editors: prompt before discarding unsaved changes, though report changes should normally auto-save.
- System picker/share sheet: return to the originating screen without duplicate navigation.

## Deep links

Not required for MVP. If added, deep links must validate IDs, never accept arbitrary file URIs, and route through repository lookups.

## Navigation tests

Automate:

- first launch path;
- returning-user path;
- language change from Settings;
- onboarding skip and completion;
- bottom-bar state restoration;
- plus → new report → back;
- process recreation on report edit;
- invalid route ID handling.
