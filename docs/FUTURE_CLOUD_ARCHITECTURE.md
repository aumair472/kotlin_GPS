# Future Cloud Architecture — Not MVP

GeoSnap MVP is local-first and requires no backend. This document prevents premature cloud coupling while defining a safe expansion path.

## Candidate capabilities

- authenticated backup/sync;
- organization/workspace accounts;
- shared report links;
- server-generated evidence signatures;
- team templates and branding;
- web dashboard;
- device-to-cloud resumable upload.

## Suggested shape

```text
Android app
→ authenticated HTTPS API
→ object storage for media/PDF
→ relational metadata database
→ async media/report workers
→ audit/event store
```

## Principles

- Local Room remains source of immediate UI truth.
- Sync state is explicit per record.
- Upload is opt-in and disclosed.
- Tenant ID is enforced server-side on every request.
- Object keys are server-generated, not raw filenames.
- Use signed, short-lived URLs.
- Encrypt transport and provider-managed storage; consider app-level encryption only with a defined key recovery model.
- Add idempotency keys and resumable uploads.
- Preserve capture metadata and server receipt time separately.

## Evidence integrity option

A stronger future design can hash finalized media plus canonical metadata on-device, upload the hash, and obtain a server timestamp/signature. This provides evidence that a specific byte sequence existed by server receipt time, but it still does not prove GPS was physically truthful without stronger attestation and operational controls.

## Do not add yet

- login screen;
- analytics SDKs;
- cloud permissions;
- background sync;
- remote database;
- subscriptions;
- web dashboard.

These require explicit product approval and privacy/security review.
