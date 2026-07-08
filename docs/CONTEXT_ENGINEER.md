# Context Engineering Protocol

## Objective

Keep the coding agent grounded in the current repository, authoritative product requirements, and verified results while avoiding stale assumptions and context overload.

## Context priority

1. Current user requirement and accepted product decisions.
2. Actual repository code and configuration.
3. Local `stitch_geosnap/` design exports.
4. This documentation pack.
5. Official Android/Google documentation.
6. Agent assumptions, which must be labeled and minimized.

## Required context files maintained in the repo

- `docs/PROJECT_STATE.md` — current phase, build health, active task.
- `docs/DECISIONS.md` — architecture/product decisions with date and rationale.
- `docs/STITCH_ASSET_MAP.md` — source folder → Android asset/screen mapping.
- `docs/KNOWN_ISSUES.md` — reproducible defects and severity.
- `docs/VERIFICATION_LOG.md` — commands, device checks, screenshot checks.

## Session start context packet

Read only what is needed in this order:

```text
PROJECT_STATE
active TASKS section
relevant architecture/design contract
relevant code and tests
relevant Stitch folder/screenshots
```

Do not reread every document before every small change. Refresh source files whenever they change.

## Decision record format

```markdown
## ADR-XXX: Title
Date:
Status: proposed/accepted/superseded
Context:
Decision:
Alternatives:
Consequences:
Verification:
```

## Assumption handling

When a requirement is missing:

- infer only a reversible implementation detail;
- record it as an assumption;
- choose least-privilege and platform-standard behavior;
- do not invent visible product scope, data collection, subscriptions, accounts, or cloud sync.

## Context compression

At the end of each task, reduce the working state to:

- what changed;
- contracts now true;
- tests/evidence;
- unresolved risks;
- next task.

Discard exploration noise and superseded plans. Keep exact error messages and commands only in verification logs where useful.

## Screenshot context

For UI work, always pair:

- target route;
- reference screenshot;
- relevant Stitch source folder;
- design token values;
- implementation screenshot/diff;
- device dimensions and font scale.

Never judge visual parity from memory.
