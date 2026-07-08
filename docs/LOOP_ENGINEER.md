# Loop Engineering Protocol

## Purpose

Prevent incomplete AI-generated implementation by enforcing measurable feedback after every change.

## Core loop

```text
Observe → Define success → Implement → Execute → Measure → Compare → Diagnose → Fix → Re-run → Record
```

## Task loop

### Observe

Read the active task, implementation, tests, logs, and design source. Reproduce the current failure or missing behavior.

### Define success

Write explicit acceptance checks before coding. Example:

- real CameraX capture succeeds;
- final content URI opens;
- database record exists;
- JPEG visible stamp matches template;
- EXIF coordinates equal the capture snapshot within formatting precision;
- UI returns to ready state.

### Implement

Make the smallest complete vertical change. Avoid unrelated cleanup.

### Execute

Run the narrowest tests first, then broader gates. Hardware work requires a device workflow; do not substitute compilation for verification.

### Measure and compare

- Compare expected vs actual state.
- Inspect logs and generated files.
- Query Room when needed.
- Open exported media/PDF.
- Compare UI screenshot.
- Measure latency/memory when the task is performance-sensitive.

### Diagnose and fix

Identify root cause, not just symptom. Add a regression test when possible. Repeat until success conditions pass.

### Record

Update task evidence and project state. Distinguish verified, implemented-only, and blocked portions.

## Automated loop command concept

Create a repository script such as `scripts/verify.sh` that runs formatting, lint, unit tests, debug build, and selected module tests. It must return non-zero on failure and print a compact summary.

## Guardrails

- Maximum two speculative fixes without new evidence; after that, gather logs/traces or simplify reproduction.
- Never mark a task done with skipped tests unless the task is explicitly blocked.
- Never delete a failing test to make the build green unless the requirement was intentionally removed and documented.
- Never silence lint globally for a local issue.
- Never use sample/hardcoded data to bypass a hardware or persistence defect.

## Phase completion loop

At phase end:

1. Run all phase acceptance tests.
2. Run regression suite.
3. Perform design screenshot checks.
4. Run security/permission checks relevant to phase.
5. Test upgrade/process-death paths.
6. Update known issues.
7. Only then advance the phase.
