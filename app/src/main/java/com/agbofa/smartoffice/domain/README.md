# domain

Deterministic product truth. Pure Kotlin.

Authorized contents:

- `foundation/` — identity, time, result, error, event, context
- `capture/` — immutable capture evidence (Phase 3)
- `journal/` — append-only admission records (Phase 4)
- `classification/` — operational meaning assigned to JournalEntry (Phase 5)

This package must never depend on Android, Compose, Room, WorkManager,
Hilt, Retrofit, Firebase, or network APIs.

Classification does not create tasks, finance records, or schedules.
