# Phase 1 — Foundation notes

**Status:** Implementation notes. Not a constitutional amendment.  
**Date:** 2026-09-07

Phase 0 ADRs were not rewritten.

## Decisions applied in this phase

| Topic | Phase 1 action | ADR relationship |
|---|---|---|
| Single `app` module | Implemented | ADR-001 |
| Hilt | Omitted | ADR-001 said Hilt when authorized. Phase 1 forbids Hilt unless the shell needs it. The shell does not. Deferred. |
| Room | Omitted | ADR-001 / Phase 1: Room-ready packages only. No schema. |
| WorkManager / AlarmManager | Omitted | Later phases |
| Navigation library | Omitted | Single Activity + `setContent` is the navigation-ready structure |
| compileSdk 37 / targetSdk 36 / minSdk 26 | Recorded in Gradle | ADR-001: current stable at Phase 1 init; Compose 1.12 requires compileSdk 37 |
| Domain Kotlin | None | Empty boundary by design |

## Verification in the constitution workspace

Android SDK is not installed here. Gradle wrapper files exist. Compilation was not performed.

CREATED ≠ COMPILED.
