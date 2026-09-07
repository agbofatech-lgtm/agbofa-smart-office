# ADR-018 — Deterministic Integrity Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 11 — Deterministic Integrity Engine
- Deciders: AGBOFA Technologies Owner Directive

## Purpose

Integrity observes canonical operational truth and reports whether the
model is internally coherent. It does not own that truth.

## Boundary

Integrity MUST NOT mutate Capture, Journal, Classification,
OperationalRecord, OperationalState, Temporal records, Dependencies,
Workflow, or Rules.

No auto-fix. No auto-transition. No second source of truth.

If the Integrity Engine were deleted, canonical operational truth
would remain intact.

## Evaluation model

`IntegrityEvaluationInput` + `EvaluationContext`
→ `IntegrityEvaluator`
→ `IntegrityReport`

Same input + same context = same ordered findings.

No `Instant.now()`. No hidden IDs. No Room access from domain.

## Findings

Stable `IntegrityCode`. Severity `ERROR` | `WARNING` | `INFO`.

Ordering: severity, code, domain, entityId, findingKey.

Outcome:

- any ERROR → `ERRORS_PRESENT`
- else any WARNING → `WARNINGS_PRESENT`
- else `HEALTHY`

Outcome is derived, not stored.

## Persistence

Room version remains **7**. No migration. No integrity table.
No `integrityStatus` column on existing entities.

## UI

No dashboard. Application may invoke `EvaluateIntegrityUseCase` and
render the returned report.

## Deferred

Analytics, Intelligence, AI, remediation use cases, and Phase 12 remain closed.
