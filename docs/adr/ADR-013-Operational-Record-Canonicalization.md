# ADR-013 — Operational Record Canonicalization

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 6 — Operational Record Engine
- Deciders: AGBOFA Technologies Owner Directive

## Context

A classified JournalEntry is historical meaning, not a structured operational
entity. Phase 6 creates that entity explicitly, without deciding lifecycle,
schedule, or finance execution.

## Decision

1. OperationalRecord is separate from Journal. Journal remains admission
   history. The record is the canonical structured continuation.
2. The record stores references: `journalEntryId` and the `classificationId`
   of the active revision used at creation. It does not copy
   `originalExpression`.
3. Creation is explicit. Classification does not auto-create a record.
   Unclassified journal entries cannot be operationalized.
4. One JournalEntry → one OperationalRecord (`UNIQUE(journalEntryId)`).
5. Later classification revisions do not rewrite the record. Provenance
   stays pinned to the classification id used at creation. No sync job.
6. State transitions are deferred to Phase 7.
7. Scheduling and "Tuesday" resolution are deferred.
8. No Task, Payment, or FollowUp subclasses. `OperationalRecordType`
   mirrors persistable `ClassificationType` values (Option A) so creation
   does not invent a second meaning vocabulary.
9. The device remains authoritative. Room is local. No sync.

### Time

`OperationalCreationInstant` is distinct from capture, admission, and
classification time.

### Persistence

Table `operational_records`. Room version 3. Explicit `Migration(2,3)`
is SOURCE PRESENT. `fallbackToDestructiveMigration()` remains because
1→2 was never verified on this host. Runtime migration is NOT CERTIFIED.

## Consequences

- Reclassification after operationalization is visible as classification
  history, not as a mutated operational type.
- Phase 7 may attach state to `OperationalRecordId` without rewriting
  Journal or Capture.
