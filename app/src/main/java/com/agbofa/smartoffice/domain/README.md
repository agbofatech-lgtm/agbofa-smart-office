
# domain

Deterministic product truth. Pure Kotlin.

Authorized contents:

- `foundation/` — identity, time, result, error, event, context
- `capture/` — immutable capture evidence (Phase 3)

This package must never depend on Android, Compose, Room, WorkManager,
Hilt, Retrofit, Firebase, or network APIs.

Do not add Journal, Task, Finance, or Workflow types until the owning
phase is authorized.
