# ADR-001 — Native Android Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 0 — Architecture Constitution
- Deciders: AGBOFA Technologies Owner Directive

## Context

AGBOFA SMART OFFICE is classified as a deterministic, offline-first
personal operations system. The Owner directed a native Android product
using Kotlin, Jetpack Compose, Room / SQLite, and MVVM + Clean
Architecture.

The risk in Phase 0 is building the wrong kind of repository: a
multi-module Gradle forest, a Flutter/KMP detour, or feature stubs that
pretend later engines exist.

## Decision

1. The product is **native Android only** in the authorized line of work.
   Kotlin is the implementation language. Jetpack Compose is the UI toolkit.
   Room + SQLite is the local system of record when persistence is authorized.
2. Architectural style is **MVVM + Clean Architecture** with these layers:
   Presentation → Application → Domain ← Data / Infrastructure.
3. Domain is pure Kotlin. It does not import Android, Compose, Room,
   WorkManager, Hilt, or network clients.
4. When Phase 1 is authorized, initialize **one `app` module** with
   package-level layers matching the Owner blueprint. Multi-module Gradle
   extraction is a later hardening option, not a Phase 0 or Phase 1
   requirement.
5. Dependency injection, when authorized: **Hilt at the Android boundary**.
   Domain and use cases remain constructor-injected and testable without Hilt.
6. Minimum SDK: API 26. Target/compile SDK: current stable production
   Android SDK at the moment Phase 1 initializes.
7. Background work: WorkManager for deferrable local work; AlarmManager
   evaluated where exact local time signals are required. Neither is
   implemented in Phase 0. Neither may own domain decisions.
8. No experimental dependencies unless a later Owner directive justifies
   a specific artifact.

## Consequences

### Allowed later

- Single-module package structure under `app/`
- Room as device-authoritative storage
- Compose screens that render Application results
- JUnit and coroutine tests against pure domain code

### Forbidden now

- Creating the Android project in Phase 0
- KMP / Flutter / React Native as the product core
- Firebase, auth, or analytics SDKs in the core
- Empty Gradle modules for every domain package

### Deviation record

The Owner blueprint lists logical packages under `app/`.
This ADR chooses a single Gradle module first.
Reason: the constitution forbids over-engineering the initial repository.
Package boundaries still enforce dependency direction.

## Related

- ARCHITECTURE.md
- DEPENDENCY_RULES.md
- ADR-005 No UI Business Logic
- ADR-002 Offline-First Authority Model
