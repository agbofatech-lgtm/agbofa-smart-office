# ADR-019 — Deterministic Operational Projection Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 12 — Deterministic Operational Projection Engine
- Deciders: AGBOFA Technologies Owner Directive

## Decision

`OperationalOverview` is an ephemeral deterministic projection composed
from canonical domains. Phase 12 owns no operational facts.

If the projection layer is deleted, canonical truth remains intact.

## Composition

Canonical domains → assembler → `OperationalOverview` → caller.

Authorities reused:

- Operations, Capture, Journal, Classification
- `OperationalStateProjection`
- `OperationalTemporalProjection` + `DueStatus.evaluate`
- Dependency queries (A REQUIRES B)
- `WorkflowProgression` / `WorkflowStepProjection`
- `IntegrityEvaluator` via `EvaluateIntegrityUseCase`

Caller supplies `EvaluationContext`. No hidden clock.

Unresolved temporal assignments produce `dueStatus = null`.

Pinned `classificationId` is used. Latest classification is not substituted.

## Persistence

Room remains version **7**. No migration. No overview table.
No `currentState` / `dueStatus` / `integrityStatus` columns.

## Rejected alternatives

- Persisted overview table — dual truth and staleness
- Fields on OperationalRecord — absorbs foreign authority
- Dashboard / analytics / intelligence — out of scope

## Deferred

Analytics, intelligence, AI, dashboards, tasks, reminders, notifications.
