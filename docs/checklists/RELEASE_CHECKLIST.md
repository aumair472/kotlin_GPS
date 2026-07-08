# Release Checklist

## Build

- [ ] clean checkout builds
- [ ] lint/tests pass
- [ ] release R8 build passes
- [ ] signed AAB generated
- [ ] version code/name correct
- [ ] mapping/native symbols retained as required

## Product

- [ ] all critical journeys verified on real devices
- [ ] no sample/hardcoded production data
- [ ] offline functionality works
- [ ] process-death and low-storage behavior verified

## Privacy/security

- [ ] permissions minimal and contextual
- [ ] privacy policy active and linked
- [ ] Data safety answers match actual SDK/code behavior
- [ ] no sensitive logs or secrets
- [ ] FileProvider/SAF sharing verified
- [ ] exported components/network/backup reviewed

## Quality

- [ ] screenshot parity approved
- [ ] RTL and 200% font approved
- [ ] accessibility scan/manual check complete
- [ ] migration tests pass
- [ ] performance benchmarks recorded
- [ ] no P0/P1 defects

## Play

- [ ] target API rechecked
- [ ] app signing configured
- [ ] store assets and copy ready
- [ ] content rating complete
- [ ] pre-launch report reviewed
- [ ] internal/closed test completed
- [ ] staged rollout and rollback criteria defined
