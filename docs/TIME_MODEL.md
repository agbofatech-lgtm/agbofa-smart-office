# TIME MODEL

Product: AGBOFA SMART OFFICE  
Owner: AGBOFA Technologies  
Mode: Phase 0 — Architecture Constitution  
Status: Specified. Not implemented.

## 1. Purpose

This document defines how time is represented, owned, and used.

Time is not a UI convenience. It is part of operational state. Hidden or ambiguous time makes the domain non-deterministic and makes the journal historically unsafe.

Phase 0 specifies the model. It does not implement clocks, schedulers, calendars, or notifications.

## 2. Constitutional Rules

1. Domain logic that depends on time must receive time as an explicit input.
2. `now()` must not be read inside deterministic domain rules.
3. User-intended civil time is not the same thing as a UTC instant.
4. Ambiguous temporal language is unresolved intent. It is not a schedule.
5. Capture time records when the journal accepted the event. It does not rewrite when the real-world event happened.
6. The device clock is the offline source of capture time. It is not assumed to be perfect.

## 3. Why Time Must Be Explicit

Determinism constitution:

```
Same Input
+
Same State
+
Same Rules
=
Same Result
```

Wall-clock time is part of input or state. If a rule secretly reads the system clock, two evaluations of the same record can diverge.

Therefore:

- use cases may read a clock at the application/infrastructure boundary
- they pass an instant or a time context into domain functions
- domain functions are given the values they need
- tests supply fixed times

This is an architectural rule, not an implementation of a clock service.

A later phase may introduce an application-layer clock port that supplies EvaluationInstant or CaptureInstant. That port is not a Phase 0 artifact. Domain rules should still consume values, not the Android clock. Do not add a speculative `TimeProvider` interface before persistence and use cases exist.

## 4. Distinct Temporal Concepts

These concepts must not be collapsed into a single `timestamp` field.

| Concept | Meaning | Owner | Example |
|---|---|---|---|
| CaptureInstant | When the journal recorded the entry on this device | Journal | The moment the user saved "Ama gave me GH₵500…" |
| EventTime | When the real-world event occurred or was claimed to occur | Journal records the user's statement; Classification may interpret it | "yesterday", "this morning", unspecified |
| DueInstant | When an operational record becomes due | Scheduling / Operations, depending on later phase ownership | School-fees payment due date |
| ScheduleInstant | When the system should surface attention | Scheduling | Tuesday 08:00 reminder trigger |
| TransitionInstant | When a state machine accepted a command | State / Integrity | Obligation marked settled |
| EvaluationInstant | The time supplied to a rule for a deterministic evaluation | Application passes it; Rules consume it | "is this overdue as of T?" |

Cross-domain systems may reference these values. They may not silently replace another domain's value with a different kind of time.

## 5. Instant vs Civil Time vs Language

Three representations exist. They are not interchangeable.

### 5.1 Instant

An unambiguous point on the timeline.

- stored as UTC
- suitable for CaptureInstant, TransitionInstant, EvaluationInstant
- comparable, sortable, deterministic once supplied

### 5.2 Civil time

A human calendar date and clock time in a zone.

- what a user usually means by "Tuesday at 8 AM"
- requires a time zone to become an instant
- can be invalidated later by zone or policy changes if stored only as an instant

When the user states a civil time, the architecture must be able to retain:

- the civil components the user meant
- the zone used to interpret them
- the derived instant, if and only if interpretation was explicit

### 5.3 Temporal language

Natural language such as "Tuesday", "later", "next week", "in the morning".

This is journal content, not a schedule.

```
TEMPORAL LANGUAGE
      ↓
UNRESOLVED TEMPORAL INTENT
      ↓
EXPLICIT INTERPRETATION  (later authorized phase)
      ↓
CIVIL TIME AND/OR INSTANT
      ↓
SCHEDULE OR DUE RECORD
```

Never:

```
"Tuesday"
      ↓
SILENT ALARM
```

Phase 0 does not define the interpretation algorithm. It forbids pretending that language already is time.

## 6. Time Zone

- Instants persist in UTC.
- Display uses a user/device zone.
- The product examples use Ghanaian currency. That does not hard-code `Africa/Accra`.
- The model must tolerate zones with daylight saving and zones without it.
- Zone is configuration. It is not domain truth for journal text.

If a later phase interprets "Tuesday 8 AM", it must record which zone was used. Reinterpretation after a zone change is a later integrity/scheduling problem, not a license to rewrite journal text.

## 7. Device Clock

While offline, CaptureInstant comes from the device clock.

Known risks, not solved in Phase 0:

- the user can change the clock
- the clock can drift
- a rollback can make a new capture appear older than an earlier one
- a jump forward can make records look overdue

Phase 0 response:

- name CaptureInstant as device-local capture time
- do not treat capture order as a cryptographic proof
- do not require network time to record a journal event
- leave clock-integrity detection to a later Integrity phase

CaptureInstant remains historically useful even if imperfect. Integrity may later annotate distrust. It may not silently rewrite journal history to "fix" the clock.

## 8. Scheduling vs Recording

Recording is journal work. Scheduling is later-phase work.

| Recording | Scheduling |
|---|---|
| Preserves what was said and when it was captured | Allocates future attention or due time |
| Must not require AlarmManager | May use AlarmManager / WorkManager later |
| Belongs to Journal | Belongs to Scheduling, not to Presentation |
| Survives if a reminder fails | May fail operationally without erasing journal truth |

Infrastructure clocks and alarm APIs stay outside `domain/`.

## 9. Deterministic Evaluation Pattern

Authorized conceptual shape:

```
Command
+
Current Domain State
+
Explicit EvaluationInstant
+
Explicit Rules
      ↓
Domain Decision
      ↓
Transition Result
      ↓
Persist
      ↓
Audit Event   (later phase)
```

Forbidden conceptual shape:

```
Command
      ↓
domain reads system clock
      ↓
different result on replay
```

Overdue, escalation, and reminder calculations — when authorized — must be replayable by supplying the same EvaluationInstant.

## 10. Intelligence and Time

Intelligence may estimate durations, suggest dates, or detect temporal patterns.

Intelligence may not:

- silently reschedule a critical operation
- change DueInstant or ScheduleInstant without user or deterministic-rule authorization
- backdate journal CaptureInstant
- treat a prediction as a due date

Required path:

```
INTELLIGENCE SUGGESTION
      ↓
USER OR DETERMINISTIC RULE AUTHORIZATION
      ↓
DOMAIN OPERATION
```

## 11. Persistence Guidance for Later Phases

When persistence is authorized, prefer explicit fields over one overloaded timestamp:

- `capturedAtUtc` for CaptureInstant
- optional `eventTimeCivil` + `eventTimeZone` + optional `eventTimeUtc` when interpretation exists
- operational due and schedule fields on the owning operational record, not on the journal row
- store the interpretation audit (what was inferred, by which rule, at which EvaluationInstant) outside journal text

Do not implement these columns in Phase 0.

## 12. Phase 0 Implication

No clock service, no timezone picker, no calendar, no AlarmManager wiring.

Later phases must import this model rather than invent a second time vocabulary.
