# Google Play and Release Compliance

## Target API

At implementation time, use at least the current Google Play-required target API and recheck immediately before release. The documented 2025 requirement is Android 15/API 35 for new apps and updates, but policy can change.

## Location

MVP uses foreground capture-time location only. Do not declare background location. Provide an in-app disclosure immediately before the runtime prompt and explain how stamped/shared files reveal location.

## User data and privacy

Before publication:

- host an active privacy policy URL and link it inside the app;
- complete Data safety accurately;
- disclose camera, microphone, and location use;
- document local processing and any SDK collection;
- provide deletion instructions for app-managed data;
- avoid claims that GPS evidence cannot be altered.

## Storage

Do not request all-files access. Use MediaStore, Photo Picker, and SAF. Explain media access only where required.

## Store assets

Prepare:

- final app icon and adaptive icon;
- feature graphic;
- phone screenshots showing real UI/data without exposing private coordinates;
- short and full description;
- privacy policy;
- content rating;
- app access instructions if future login is introduced.

## Release checklist

- package name registered/verified as required;
- signing configured with Play App Signing;
- version code increased;
- release notes localized where needed;
- AAB inspected;
- target API compliant;
- permissions match actual features;
- no debug/test content;
- pre-launch report reviewed;
- internal/closed test completed;
- staged rollout plan and rollback criteria approved.
