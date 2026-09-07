# ADR-023 — Thin Presentation Architecture

- Status: Accepted
- Date: 2026-09-07
- Phase: 16

## Decision

Presentation renders application outputs. ViewModels request use cases.
Compose does not own operational state, due status, workflow completion,
integrity, or analytics aggregation.

Navigation selects a screen. It is not domain authority.

Theme follows system light/dark. Preference persistence is out of scope.

EvaluationContext is constructed at the presentation edge on explicit refresh.

No Room or DAO access from presentation.

ADR-022 is not rewritten.
