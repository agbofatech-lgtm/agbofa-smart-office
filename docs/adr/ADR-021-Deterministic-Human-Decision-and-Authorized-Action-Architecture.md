# ADR-021 — Deterministic Human Decision and Authorized Action Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 14
- Deciders: AGBOFA Technologies Owner Directive

## Decision

Phase 14 records explicit human intent as append-only Decision history and
dispatches authorized action requests to existing owning-domain use cases.

Decision is not OperationalState, not a WorkflowStep, not a Task, and not
a Rule evaluation.

## Rejected alternatives

- Task / Todo domain — would duplicate Operational State and Workflow.
- Generic executor over repositories — would bypass domain validation.
- Rule MATCH → automatic execution — Rules remain advisory.
- Scheduler / notification driven action — Phase 14 is human-authorized.
- AI authorization — AI may not mint time, IDs, or mutate truth.

## Lifecycle

Empty history projects PROPOSED.

Legal transitions: PROPOSED → APPROVED | REJECTED | WITHDRAWN.

APPROVED, REJECTED, and WITHDRAWN are terminal.

Current status is `DecisionProjection.current` ordered by
`transitionedAt` then transition id.

## Action dispatch

Bounded types only:

- TRANSITION_OPERATIONAL_STATE → `TransitionOperationalRecordStateUseCase`
- ADVANCE_WORKFLOW → `AdvanceWorkflowUseCase`

Phase 14 does not `save` OperationalState or Workflow rows.

An action request may be created only for an APPROVED decision.
Each request may be executed at most once.

## Persistence

Decisions are canonical human history.

Room version **8**. `MIGRATION_7_8` adds:

- human_decisions
- human_decision_transitions
- authorized_action_requests
- authorized_action_executions

Destructive fallback remains removed. Workflow `saveWithSteps` is unchanged.
