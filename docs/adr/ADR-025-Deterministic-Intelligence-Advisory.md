# ADR-025 — Deterministic Intelligence Advisory

- Status: Accepted
- Date: 2026-09-07
- Phase: 18
- Product: AGBOFA SMART OFFICE

## Decision

Intelligence is an ephemeral, deterministic advisory derived from existing
projections (overview, integrity, due status, workflow, dependencies,
decisions). It owns no facts.

- No intelligence table, DAO, or migration. Room remains version 9.
- No UUID or Instant.now() in the engine.
- Recommendation keys are `TYPE:targetId`.
- Priority is a derived integer score, not a canonical field.
- OVERDUE is DueStatus.PAST_DUE. BLOCKED is prerequisite evidence.
  Neither is an OperationalState.
- Past-due concentration uses documented heuristic threshold 3.
- Intelligence never approves, rejects, withdraws, or executes.
- No AI/LLM/ML/network client is introduced.

Future AI may explain or draft. It must not own truth or execute.

## Consequences

Deleting the intelligence package leaves Phases 1–17 intact.
Human Decision remains the authorization boundary.
