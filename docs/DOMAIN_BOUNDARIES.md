# DOMAIN BOUNDARIES

Product: AGBOFA SMART OFFICE  
Organization: AGBOFA Technologies  
Mode: Phase 0 — Architecture Constitution  
Status: Binding for all future phases  
This document defines ownership. It does not implement domains.

## 1. Purpose

Each domain owns one class of truth.

Cross-domain systems may reference another domain’s records.
They may not copy ownership, silently rewrite another domain’s fields,
or treat a derived view as authoritative.

Constitutional rule:

> One domain must have one authoritative owner of its truth.

## 2. Canonical Pipeline and Boundary Implication

```
CAPTURE
  → JOURNAL
  → CLASSIFICATION
  → OPERATIONAL RECORD
  → STATE
  → TIME / DEPENDENCIES
  → WORKFLOW
  → RULES
  → INTEGRITY
  → ANALYTICS
  → INTELLIGENCE
```

Ordering is constitutional.

A later layer may derive from an earlier layer.
A later layer may not silently rewrite the truth preserved by an earlier layer.

## 3. Domain Ownership Map

| Domain | Owns | Does not own | May reference |
|---|---|---|---|
| Kernel | Shared primitives: typed identifiers, command/query envelopes, result/error vocabulary, pure domain events as data shapes | Any business record, lifecycle, money, schedule, or meaning | Nothing authoritative |
| Journal | Chronological human events as captured; original text; capture time; capture identity; journal event identity | Meaning, tasks, money, schedules, workflow steps, analytics | Nothing. Journal is the source capture |
| Classification | Meaning assignment attached to a journal event; classification type; confidence declared as metadata only | The journal event itself; the resulting operational record; state transitions | Journal event identity |
| Operations | Actionable operational records derived from classified journal events | Original journal text; financial ledgers; schedules as authority; UI state | Journal event identity; classification identity |
| State | Explicit lifecycle transitions of an operational record: current state, allowed commands, transition result | The operational payload; schedules; finance amounts | Operations identity |
| Scheduling | Temporal allocation: due instants, windows, recurrence definitions, timezone-resolved intent | Device clocks as business rules; notification delivery; state transitions | Operations identity; Time model primitives |
| Dependencies | Blocking and enabling relationships between operational records | The records being related; workflow step graphs | Operations identities |
| Workflow | Multi-step process definitions and instance progress over operations | Individual operation truth; finance settlement; notifications | Operations, State, Dependencies, Rules |
| Rules | Explicit deterministic authorization and constraint policies | Silent mutation of records; probabilistic inference | Domain identities and declared facts |
| Finance | Financial obligations, held funds, payment operations, settlement outcomes | Journal narrative; classification labels; analytics totals as authority | Journal event identity; Operations identity |
| Integrity | Validation, consistency checks, invariant enforcement results | Source records being validated | Any domain record, read-only for checking |
| Analytics | Derived measurements and aggregations | Source operational truth; the right to correct source records | Read models of Journal, Operations, State, Finance, Time |
| Intelligence | Advisory analysis, predictions, recommendations, estimates | Write authority over Journal, Operations, State, Finance, Rules, Scheduling | Read models plus explicit recommendation objects |
| Presentation | Rendering, UI state, user gestures | Domain decisions of any kind | Application commands and queries |
| Application | Use-case orchestration: commands in, results out | Domain invariants; persistence format; Android APIs | Domain and repository contracts |
| Data | Persistence mapping and local storage of domain snapshots | Domain rules; UI; network as authority | Domain types via mappers |
| Infrastructure | Android platform adapters: database driver, alarms, workers, recovery hooks | Domain meaning; product decisions | Application/data contracts; platform APIs |

## 4. Journal Truth Preservation

The General Journal is not a notes feature.
It is the chronological capture of operational reality.

Example capture:

> "Ama gave me GH₵500 to pay school fees."

Allowed later derivations, each in their own domain:

- Classification: financial / obligation / held-funds candidate
- Finance: obligation, allocated funds, payment, settlement
- Operations: a payable action
- State: explicit transitions of that action
- Integrity / Audit (future): trace of who/what/when

Forbidden:

- Editing journal text to “match” a later classification
- Replacing the journal event with the derived finance record
- Deleting a journal event because a payment settled
- Letting Intelligence rewrite the capture after the fact

Canonical relationship:

```
ORIGINAL CAPTURE
  → JOURNAL EVENT
  → CLASSIFICATION
  → DERIVED OPERATIONAL RECORDS
  → EXECUTION
  → OUTCOME
  → AUDIT
```

Constitutional rule:

> Derived records may reference a Journal Event.
> They may not rewrite historical journal truth.

Correction of capture, when authorized in a future phase, must be an
append-only compensating journal event or an explicit amendment record.
It must never be an in-place mutation of history.

## 5. Authority vs Reference

A domain may hold a foreign identity.
Holding an identity is not ownership.

| Act | Allowed | Forbidden |
|---|---|---|
| Operations stores `journalEventId` | Yes | Operations rewriting journal text |
| Finance stores `operationId` | Yes | Finance changing operation state directly |
| State applies `CompleteOperation` | Yes | Presentation setting `status = DONE` |
| Intelligence emits `RecommendReschedule` | Yes | Intelligence writing a new due time |
| Analytics computes overdue count | Yes | Analytics marking records overdue as source truth |
| Classification labels an event as financial | Yes | Classification creating ledger balances |

## 6. Command Path

All mutating product intent must travel:

```
Presentation (intent)
  → Application (use case / command)
  → Domain (pure decision)
  → Persistence via Data (authorized snapshot)
  → optional Audit event (future Integrity phase)
```

Intelligence, if present in a later phase, must travel:

```
Intelligence
  → Recommendation
  → User authorization OR explicit deterministic Rule
  → Domain operation
```

Never:

```
Intelligence → silent data mutation
```

## 7. Kernel Boundary

`domain/kernel` exists so other domains share vocabulary without
sharing ownership.

Kernel may contain, as types only:

- identifier types
- `Result` / error vocabulary used by domain decisions
- command and domain-event shapes that carry no Android types
- clock-as-value interfaces defined in domain terms (see TIME_MODEL.md)

Kernel must not become a god-module.
If a rule is about money, it belongs in Finance.
If a rule is about capture, it belongs in Journal.

## 8. Future Phase Placement (informational)

These domains are named now so later phases do not invent competing owners.
Naming is not implementation.

| Phase | Domain touched | Still out of Phase 0 |
|---|---|---|
| 2 | Kernel | implementation |
| 3 | Capture as input path into Journal | UI, persistence |
| 4 | Journal | UI, storage engine |
| 5 | Classification | engine |
| 6 | Operations, State | engines |
| 7 | Scheduling | engine |
| 8 | Dependencies | engine |
| 9 | Workflow | engine |
| 10 | Finance | ledger |
| 11 | Infrastructure recovery | workers |
| 12 | Infrastructure notifications | delivery |
| 13 | Analytics | dashboards |
| 14 | Rules | adaptive rules |
| 15–16 | Intelligence | models |

## 9. Conflict Resolution

If two domains appear to own the same field, the earlier pipeline layer
wins for historical fact, and the named owner in Section 3 wins for
derived fact.

Examples:

- Amount as spoken in capture text: Journal owns the raw text.
- Amount as bookable money: Finance owns the parsed monetary value.
- “Done”: State owns the transition. Operations owns the record. UI does not.

No field may have two writers.

## 10. Phase 0 Constraint

This document is a boundary map.
It does not create packages, interfaces, databases, or placeholder classes.

## 11. Ownership Tensions Resolved in Phase 0

These collisions are predictable. They are resolved on paper now so later
phases do not invent a second owner.

### 11.1 Finance vs Operations

A sentence like “Ama gave me GH₵500 to pay school fees.” yields both
money facts and an actionable job.

- Finance owns obligation, held funds, payment application, and settlement.
- Operations owns the actionable record (“pay the school fees”).
- Neither table is the other. They share identifiers, not columns.

A payment operation is not a substitute general ledger.
A ledger line is not a task.

### 11.2 Classification vs Operations

Classification assigns meaning to a journal event.
It does not create the operational record.

A later authorized use case may read a classification and *command*
Operations or Finance to create derived records.
Classification has no write path into those domains.

### 11.3 State vs Scheduling (“overdue”)

“Overdue” is not a field Scheduling may stamp onto State by watching
the device clock.

- Scheduling owns due instants and windows.
- State owns lifecycle only after an explicit command.
- Whether a record is overdue at instant T is a deterministic function
  of (state, due instant, as-of instant), evaluated with time supplied
  as input.

Automatic overdue transitions, if ever authorized, must be a Rule plus
a command, not a clock side effect.

### 11.4 Rules vs State

Rules authorize. State transitions.
Rules must not persist “current status.”
State must not embed unnamed policy.

### 11.5 Workflow vs Operations vs State

Workflow owns process definition and instance step cursor.
Operations owns each actionable record in the process.
State owns each record’s lifecycle.

A workflow step being “current” is not the same as an operation being
“in progress.” Those flags must not be collapsed into one column.

### 11.6 Integrity has no repair authority

Integrity reports invariant violations.
It does not auto-rewrite Journal, Finance, or State to make checks pass.
Repair is a later authorized command path.

### 11.7 Kernel is not a dumping ground

If a type encodes money, time policy, or meaning, it does not belong
in Kernel. Kernel holds shared shapes only.

### 11.8 Identity does not imply an account

Provenance and actor identity are local concepts.
They must not require a cloud user, OAuth subject, or Firebase UID.
Future sync, if authorized, may map local identities. It may not
become the source of them.

## 12. Red Team Residue (accepted risks, not solved in Phase 0)

- Amendment records can hide original text if UI later shows only the
  latest amendment. Future Journal UI must present history, not a
  mutated headline.
- A parsed monetary amount can become the only number users trust,
  starving journal text of authority. Finance displays must remain
  derived.
- Recommendation objects that look like commands will be a bypass
  hazard. Command types and recommendation types must remain distinct
  in later phases.
- Device clock rollback can disorder capture timestamps. Integrity
  phase must treat clock movement as an integrity event, not as a
  license to rewrite history.
