# ADR-014 — Operational State Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 7 — Deterministic Operational State Architecture
- Deciders: AGBOFA Technologies Owner Directive

## Context

An OperationalRecord is a structured identity. It still needs an explicit
lifecycle without becoming a task board, workflow engine, or scheduler.

## Decision

1. State belongs to OperationalRecord only. Capture, Journal, and
   Classification do not carry lifecycle.
2. Option B: immutable `OperationalStateTransition` history plus a
   deterministic projection. OperationalRecord has no `currentState`
   field (no dual truth).
3. No history → OPEN.
4. Graph:
   - OPEN → ACTIVE | CANCELLED
   - ACTIVE → COMPLETED | CANCELLED
   - COMPLETED and CANCELLED are terminal
   - same-state rejected
   - OPEN → COMPLETED rejected
5. Chronology: `OperationalTransitionInstant` then transition id.
   Insertion order is not authority.
6. Transitions are explicit and MANUAL in the UI. No automatic
   transitions. RULE is provenance-ready only.
7. Compose may request a successor from `OperationalStatePolicy`.
   Domain still validates.
8. This is a bounded history model, not event sourcing. No event bus,
   no command bus, no workflow engine.
9. Room version 4. `MIGRATION_3_4` creates `operational_state_transitions`.
   Existing destructive fallback remains a known 1→2 limitation and is
   not extended as the 3→4 path.
10. Future workflow/rules may recommend a next state. They may not
    silently write history.

## Consequences

- Phase 8 must not rewrite transition rows to make current state look
  cleaner.
- Classification revision after operationalization does not alter state
  history or pinned classification provenance.
