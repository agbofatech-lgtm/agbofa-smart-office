# ADR-022 — Deterministic Ephemeral Analytics Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 15
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phases 1–14 own canonical facts, projections, integrity, and human-authorized
action. The product still lacks derived counts over that surface.

## Decision

Phase 15 adds an ephemeral analytics evaluator.

`AnalyticsInput` + `EvaluationContext` → `AnalyticsEvaluator` →
`OperationalAnalyticsReport`.

Analytics owns **derived aggregates only**. It does not own operational facts.

## Consequences

- No Room table, DAO, entity, or migration.
- Room version remains 8.
- Evaluator receives explicit snapshots. It does not query Room.
- State, due status, workflow completeness, and integrity severity are reused
  from existing projections. They are not recalculated by a second machine.
- Same input + same context = same report.
- Deleting `domain.analytics` and `application.analytics` leaves Phases 1–14
  intact.

## Rejected alternatives

- Persisted analytics snapshots — would become a second truth.
- Dashboard / charts in this phase — presentation is not authorized.
- Invented KPIs (productivity, revenue, forecasts) — no such facts exist.
- Hidden clocks or UUID generation.

ADR-021 (Human Decision) is not rewritten.
