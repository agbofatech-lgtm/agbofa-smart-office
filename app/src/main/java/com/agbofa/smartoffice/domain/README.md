# domain

Deterministic product truth. Pure Kotlin.

Authorized contents:

- `foundation/` — identity, time, result, error, event, context
- `capture/` — immutable capture evidence (Phase 3)
- `journal/` — append-only admission records that reference Capture (Phase 4)

This package must never depend on Android, Compose, Room, WorkManager,
Hilt, Retrofit, Firebase, or network APIs.

Do not add Classification, Task, Finance, or Workflow types until the
owning phase is authorized.
