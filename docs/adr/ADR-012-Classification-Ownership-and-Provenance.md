# ADR-012 — Classification Ownership and Provenance

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 5 — Classification Engine
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phase 5 attaches operational meaning to a JournalEntry. Capture remains
evidence. Journal remains admission history. Classification must not
execute tasks, finance, or schedules, and must not invoke AI.

## Decision

### Ownership

- Capture owns original expression, capture identity, capture time, source.
- Journal owns journal identity, capture reference, admission time.
- Classification owns classification identity, journal reference, type,
  basis, classification time, revision, optional rule version, and the
  previous classification id it supersedes.

Classification references JournalEntry. It does not reference Capture
directly and does not copy `originalExpression`.

### Unclassified

Absence of a classification row means unclassified.
`ClassificationType.UNCLASSIFIED` is a projection value only.
Journal admission does not create a classification.

### Provenance

`ClassificationBasis` is `MANUAL` or `RULE`.

- MANUAL must not carry `ruleVersion`.
- RULE requires `ruleVersion`.
- AI recommendation is excluded.

The Phase 5 UI always submits MANUAL. RULE exists so later deterministic
rules can be distinguished from human assignment. No keyword parser
ships in this phase.

### Reclassification

Option B, minimal: a new immutable revision row.

- First assignment is revision 1.
- Later assignment is revision N+1 and `supersedesId` points at the
  previous active row.
- Active classification is the maximum revision for that JournalEntry.
- Rows are never overwritten.

This is not event sourcing and not an event bus.

### Time

`ClassificationInstant` is a distinct role. Domain functions do not
read `Instant.now()`.

### Why Classification is not execution

`FINANCIAL_OBLIGATION` does not create a ledger row.
`FOLLOW_UP` does not create a task or reminder.
`EVENT` does not resolve "Tuesday".

Intelligence remains advisory. It may not silently overwrite a
deterministic MANUAL or RULE classification.

## Consequences

- Timeline may display type as a projection field.
- History of types is reconstructable from revision rows.
- Phase 6 operational records remain closed.
