# ADR-003 — Journal as Operational Entry Point

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 0 — Architecture Constitution
- Deciders: AGBOFA Technologies Owner Directive

## Context

Personal operational reality arrives as ordinary language:

- "Ama gave me GH₵500 to pay school fees."
- "Koho said I should call him Tuesday at 8 AM."

Those sentences are not yet tasks, invoices, reminders, or workflows.
If the first stored object is a Task or a Ledger line, the original event
is lost or rewritten to fit a later schema.

The product constitution names the General Journal as the universal
entry point and forbids later layers from destroying capture truth.

## Decision

1. Every user capture becomes a Journal Event before it becomes anything else.
2. A Journal Event preserves at least:
   - stable identity
   - original capture payload (human text or equivalent raw input)
   - capture timestamp in the product time model
   - capture provenance (who/what captured it), when that exists
3. Classification, Operations, Finance, Scheduling, Workflow, Analytics,
   and Intelligence may only *derive from* a Journal Event.
4. Derived records reference `journalEventId`.
   They do not replace, edit, or delete the historical journal payload
   in order to stay consistent with later interpretation.
5. The Journal is a domain concept, not a notes screen.
   Phase 0 does not implement capture UI, storage, or classification.

## Canonical flow

```
ORIGINAL CAPTURE
  → JOURNAL EVENT
  → CLASSIFICATION
  → DERIVED OPERATIONAL RECORDS
  → EXECUTION
  → OUTCOME
  → AUDIT
```

## Consequences

### Allowed

- Multiple derived records from one journal event
  (one sentence may yield a finance obligation and a follow-up).
- Reclassification that creates new derived records while leaving
  the original event intact.
- Future amendment via a new journal event or explicit amendment record
  that points at the original event.

### Forbidden

- Starting a product flow as a Task, Bill, or Reminder with no journal origin
  once Journal exists as a product capability.
- In-place mutation of historical journal text, time, or identity
  to match a later classification.
- Intelligence or Analytics overwriting capture history.
- Treating the Journal package as optional scaffolding that features may bypass.

### Phase 0 non-goals

- No journal schema implementation
- No capture screen
- No classifier
- No placeholder `JournalRepository` “for later”

Those belong to later authorized phases.

## Related

- DOMAIN_BOUNDARIES.md
- ADR-002 Offline-First Authority Model
- ADR-004 Domain Determinism
- ADR-006 Intelligence Advisory Boundary
- ADR-007 Domain Data Ownership
