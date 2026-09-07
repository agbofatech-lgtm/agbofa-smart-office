# ADR-008 — Time and Temporal Modeling

Product: AGBOFA SMART OFFICE  
Owner: AGBOFA Technologies  
Status: Accepted for Phase 0 architecture  
Date: 2026-09-07

## Context

The product converts human operational language into structured records. That language is full of time: "Tuesday at 8 AM", "pay school fees", "later", "yesterday".

A naive model stores one timestamp per row and reads `now()` inside domain rules. That model fails this product because:

- journal capture time is not the same as event time or due time
- "Tuesday at 8 AM" is civil time, not an instant, until zone and date are resolved
- hidden clock reads make deterministic rules unreproducible
- offline operation cannot depend on network time
- future intelligence must not silently reschedule records

Phase 0 must freeze the time vocabulary before any schema or scheduler exists.

## Decision

1. Time used by deterministic domain logic is an explicit input. Domain code does not read the system clock.
2. The architecture distinguishes CaptureInstant, EventTime, DueInstant, ScheduleInstant, TransitionInstant, and EvaluationInstant. These are not one field.
3. Instants persist as UTC. User-intended civil time and zone are retained when the user stated civil time.
4. Natural-language temporals remain unresolved intent until a later authorized interpretation step. Language is not a schedule.
5. CaptureInstant is taken from the device clock at capture. Clock error is an integrity concern. It does not justify an online time source for core capture.
6. Scheduling infrastructure (AlarmManager, WorkManager) may fire later-phase attention. It does not own domain time rules.
7. Intelligence may propose times. It may not write due or schedule truth without user or deterministic-rule authorization.

Canonical documents: `docs/TIME_MODEL.md`, `docs/OFFLINE_FIRST.md`, `docs/adr/ADR-004-Domain-Determinism.md`, `docs/adr/ADR-006-Intelligence-Advisory-Boundary.md`.

## Consequences

### Allowed

- Application or infrastructure reads a clock once and passes EvaluationInstant into use cases.
- Tests fix EvaluationInstant and assert identical decisions.
- Journal preserves original text plus CaptureInstant even if later classification extracts a date.
- Later scheduling can derive a ScheduleInstant without editing journal history.

### Forbidden

- `domain/` calling platform clock, Java/Kotlin `Instant.now()`, or Android time APIs
- Compose screens computing overdue, due, or reminder times as business logic
- collapsing capture, event, due, and schedule into one `timestamp`
- treating "Tuesday" as an implicit AlarmManager event
- requiring NTP or a cloud scheduler to save a journal entry
- intelligence writing schedule fields directly

### Deferred to later phases

- actual clock port implementation
- civil-time interpretation rules
- reminder delivery
- clock-rollback detection
- temporal reconciliation across devices

## Alternatives considered

### Single timestamp per record

Rejected. It cannot represent "captured now, event yesterday, due next Friday, remind Tuesday 08:00".

### Domain reads `now()` internally

Rejected. It violates determinism and makes tests depend on wall time.

### Network time as source of truth

Rejected. It violates offline-first authority and blocks capture when disconnected.

### Store only civil time

Rejected. Civil time without an instant is not reliably ordered. Store both when interpretation exists.

## Compliance check (Phase 0)

- No scheduler implemented.
- No time-zone database packaged as a feature.
- No claim that device time is cryptographically trusted.
- Time model is documentation only.
