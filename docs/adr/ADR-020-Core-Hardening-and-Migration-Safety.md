# ADR-020 — Core Hardening and Migration Safety

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 13 — Core Hardening & Certification
- Deciders: AGBOFA Technologies Owner Directive

## Decision

Phase 13 hardens durability infrastructure. It does not add product capability.

## Migrations

Room remains version **7**.

Registered path:

1 → 2 → 3 → 4 → 5 → 6 → 7

`MIGRATION_1_2` creates the classifications table for databases that
began as Capture + Journal only.

Unrestricted `fallbackToDestructiveMigration()` is removed.

Missing or unknown version jumps now fail closed instead of wiping history.

## Workflow create

`WorkflowRepository.saveWithSteps` persists a workflow and its required
steps atomically.

Room uses `runInTransaction`. In-memory implementations compensate on
partial failure.

## Main-thread queries

`allowMainThreadQueries()` remains.

Current use cases are synchronous and invoked from the UI thread.
Removing this flag requires an async application rewrite, which would
change infrastructure semantics and is outside Phase 13.

## Certification honesty

This environment has no Android SDK.

COMPILED, EXECUTED, PASSED, and RUNTIME VERIFIED remain unavailable
until a certified Android toolchain is provided.

## Deferred

Analytics, intelligence, AI, dashboards, tasks, reminders, notifications.
