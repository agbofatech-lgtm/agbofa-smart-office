# ADR-015 — Temporal and Dependency Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 8 — Temporal & Dependency Architecture
- Deciders: AGBOFA Technologies Owner Directive

## Context

Operational records needed explicit WHEN and DEPENDS ON WHAT without
becoming a reminder system or a workflow engine.

## Decision

### Temporal ownership

Temporal attachments belong to OperationalRecord. They do not rewrite
Capture, Journal, Classification, OperationalRecord provenance, or state.

Existing Phase 2 roles are reused: `DueInstant`, `EvaluationInstant`,
`ScheduleInstant`, `CivilTime`. New roles are only
`TemporalAssignmentInstant` and `OperationalDependencyCreationInstant`.

### Resolution boundary

`UNRESOLVED` preserves `referenceExpression` such as "Tuesday at 8 AM".
It cannot carry a `DueInstant`. No NLP. No silent conversion.

`RESOLVED` requires `DueInstant`. The original expression may be kept.

History is append-only. Current attachment = latest by `assignedAt`
then id.

### Provenance

`MANUAL` | `RULE`. RULE requires `ruleVersion`. MANUAL forbids it.
No AI provenance. No rules engine.

### Due semantics

`DueStatus.evaluate(DueInstant, EvaluationInstant)` →
`BEFORE_DUE` | `AT_DUE` | `PAST_DUE`.

Due does not create a reminder, schedule, or state transition.

### Dependency ownership

`OperationalDependency`: dependent REQUIRES prerequisite.

Self, missing records, duplicates, and cycles are rejected.
Cycle detection is DFS with neighbors sorted by id. Complexity
O(V+E) on the reachable subgraph.

Dependencies do not imply workflow execution or automatic blocking.

### Persistence

Tables `operational_temporal_records` and `operational_dependencies`.
Room version 5. `MIGRATION_4_5` SOURCE PRESENT.
`fallbackToDestructiveMigration()` is an inherited limitation.

## Consequences

Phase 9 workflow remains closed. Scheduling remains a separate role.
Intelligence remains advisory.
