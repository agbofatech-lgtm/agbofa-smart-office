# ADR-010 — Capture Evidence Boundary

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 3 — Capture Engine
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phase 3 authorizes the first operational entry: a user expresses something, and the system preserves that expression as immutable evidence. Journal, classification, finance, and tasks remain closed.

Two decisions were required: how to treat the original text, and whether to introduce Room.

## Decision

### Evidence

A capture stores the exact submitted string when the string is not blank.

- `""` and whitespace-only strings are rejected.
- Accepted strings are not trimmed, folded, or rewritten.
- There is no setter and no replacement API.

Later interpretation may derive amount, person, or schedule. Those derivatives must not overwrite `originalExpression`.

### Source

Only `CaptureSource.TEXT` exists. Voice, image, and import are not stubbed.

### Persistence

Phase 3 uses an in-memory `CaptureRepository` implementation.

Room is not introduced. Reasons:

- Capture is real as a domain operation plus a process-local store.
- This host cannot compile or verify Room.
- A Room schema now would be a single-table start, but it would also add Android persistence types that the domain must never see. That can wait until durable on-device storage is authorized with a toolchain that can compile it.

The repository port lives in domain. The in-memory implementation lives in data.

### UI

A narrow Compose screen proves input → use case → result. Compose does not classify text.

Identity and `CaptureInstant` are minted at the Android edge (`MainActivity`) and passed in. Domain code does not call `Instant.now()` or `UUID.randomUUID()`.

## Consequences

- Restarting the process loses captures until a later phase adds durable storage.
- Phase 4 Journal must project from capture evidence, not replace it.
- No capture table exists in SQLite yet.

## Boundary

Not in this ADR: Journal timeline, classification, finance parsing, scheduling, Room entities for other domains.
