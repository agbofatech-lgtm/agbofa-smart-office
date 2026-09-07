# ADR-011 — Journal Historical Integrity

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 4 — Journal Engine
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phase 4 introduces chronological operational memory. Capture is already
immutable evidence. The Journal must record admission without becoming a
second owner of the original expression, and without classifying meaning.

## Decision

### Ownership

- Capture owns `originalExpression`, `CaptureId`, and `CaptureInstant`.
- Journal owns `JournalEntryId`, `CaptureId` reference, and
  `JournalAdmissionInstant`.
- `JournalRecord` is a read projection. It is not durable truth.

### Admission rule

One Capture → one Journal admission.

A second admission of the same `CaptureId` fails with `InvalidState`.
Orphan Journal entries are rejected: the Capture must exist first.

A Capture that exists but is not yet admitted is valid. A crash between
persist-capture and admit-journal does not auto-admit or delete the Capture.

### Chronology

Primary: `JournalAdmissionInstant` ascending.
Tie-break: `JournalEntryId.value` lexicographic.
Insertion order is not chronology.

`JournalAdmissionInstant` is a distinct Phase 2-style role wrapper.
Capture time and admission time may differ.

### Persistence

Room is production storage for Capture and JournalEntry only.

- `captures` and `journal_entries`
- Foreign key `journal_entries.captureId → captures.id` (RESTRICT)
- Unique index on `journal_entries.captureId`
- Index on `journal_entries.admittedAt`

`InMemoryCaptureRepository` and `InMemoryJournalRepository` remain for
unit tests. Production wiring uses Room repositories.

Domain and application do not import Room types.

### Append-only

No edit or delete APIs in Phase 4.

## Consequences

- Timeline displays Capture text through projection, not Journal ownership.
- Process restart preserves Room data; in-memory tests do not.
- Classification, finance, and scheduling remain closed.
