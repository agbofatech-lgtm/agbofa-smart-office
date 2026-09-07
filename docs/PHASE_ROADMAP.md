# PHASE ROADMAP

Product: AGBOFA SMART OFFICE  
Owner: AGBOFA Technologies  
Mode: PHASE 0 — Constitution & Architecture  
Status: INFORMATIONAL ROADMAP — ONLY PHASE 0 IS AUTHORIZED  
Rule: SPECIFIED ≠ IMPLEMENTED

This roadmap is the approved high-level sequence.
It is not a work order for Phases 1–16.

Authorization of Phase 0 does not authorize Phase 1 or beyond.

---

## 1. Authority Note

An earlier reconnaissance brief used a different phase numbering
(journal as Phase 1, classification as Phase 2, and so on).

**This document supersedes that numbering.**

The authorized map is the Owner Directive dated as Phase 0 Authorization.
Do not mix the two maps in future reports.

---

## 2. Current Position

| Item | State |
|---|---|
| Current mode | PHASE 0 — Constitution & Architecture |
| Phase 0 | AUTHORIZED and in progress as documentation |
| Phases 1–16 | CLOSED |
| Android application | NOT BUILT |
| Native repository | NOT CREATED |
| Product features | NOT AUTHORIZED |

---

## 3. Approved Phase Map

### PHASE 0 — Constitution & Architecture

**Status:** AUTHORIZED (documentation only)

Establish:

- product identity
- architectural boundaries
- domain ownership
- dependency rules
- offline-first and determinism constitutions
- journal-first operational model
- intelligence advisory boundary
- time model names
- engineering discipline
- ADRs

Do not create the Android project in this phase unless a later Owner directive explicitly says so. The current authorization is architecture artifacts, not repository scaffolding.

### PHASE 1 — Native Repository Foundation

**Status:** CLOSED

Informational intent:

- create the real Android repository
- Gradle Kotlin DSL
- minimum SDK API 26
- application shell
- empty layer directories consistent with the blueprint
- baseline test harness
- no product features

Phase 1 is repository plumbing, not journal, not classification, not UI product surfaces.

### PHASE 2 — Core Deterministic Kernel

**Status:** CLOSED

Informational intent:

- shared identifiers
- result types
- command/query primitives
- explicit time-input conventions
- deterministic test fixtures

The kernel owns shared primitives. It does not own journal truth, finance, or workflows.

### PHASE 3 — Universal Capture

**Status:** CLOSED

Informational intent:

- the act of recording raw operational input
- preservation of original text and capture metadata
- no classification engine yet

Capture is how reality enters the system. It is not yet a notes app with business behavior.

### PHASE 4 — General Journal

**Status:** CLOSED

Informational intent:

- chronological journal as the system of historical record
- immutable capture history
- identifiers that later derived records can reference

The journal preserves original human events. Later phases may interpret them. They may not rewrite them.

### PHASE 5 — Classification Engine

**Status:** CLOSED

Informational intent:

- deterministic assignment of meaning to journal events
- production of classification results that point back to journal events

Classification assigns meaning. It does not become the owner of operations, money, or state.

### PHASE 6 — Operations & State Engine

**Status:** CLOSED

Informational intent:

- operational records derived from classified journal events
- explicit state transitions driven by commands
- no uncontrolled field mutation such as `status = DONE`

Operations own actionable records. State owns legal transitions of those records.

### PHASE 7 — Time & Scheduling

**Status:** CLOSED

Informational intent:

- due instants
- schedule instants
- civil-time versus exact-instant distinctions
- unresolved temporal intent versus committed schedules

See `docs/TIME_MODEL.md`. Naming time concepts in Phase 0 is not implementing a calendar.

### PHASE 8 — Dependencies & Follow-Up

**Status:** CLOSED

Informational intent:

- blocking relationships
- follow-up links
- commitment references

Dependencies own relationship truth. They do not silently reschedule or complete work.

### PHASE 9 — Workflow & SOP Engine

**Status:** CLOSED

Informational intent:

- multi-step operational procedures
- ordered steps that still obey state and journal constitutions

A workflow is not allowed to erase journal history or bypass state transitions.

### PHASE 10 — Financial Obligations

**Status:** CLOSED

Informational intent:

- obligations, held funds, payment operations, settlement
- money records that reference journal events
- no silent rewrite of the original capture

Finance owns financial truth. Intelligence and UI do not.

### PHASE 11 — Offline Reliability & Recovery

**Status:** CLOSED

Informational intent:

- local durability
- recovery after process death
- conflict/repair policies that cannot resurrect cloud authority

Offline is normal mode. Recovery may restore local truth. It may not invent a remote source of truth.

### PHASE 12 — Notification & Attention Engine

**Status:** CLOSED

Informational intent:

- surfacing due or blocked work
- AlarmManager / WorkManager as infrastructure adapters only

Notifications request attention. They do not complete, cancel, or reschedule operations.

### PHASE 13 — Analytics & Operations Dashboard

**Status:** CLOSED

Informational intent:

- derived measurements
- operator-facing views of existing truth

Analytics reads. It does not own Journal, Finance, Operations, or State.

### PHASE 14 — Adaptive Deterministic Rules

**Status:** CLOSED

Informational intent:

- explicit, testable rules that can authorize domain operations
- no hidden heuristics

A deterministic rule may authorize a domain command. It is not intelligence, and it is not UI logic.

### PHASE 15 — Predictive & Behavioral Intelligence

**Status:** CLOSED

Informational intent:

- analysis, prediction, recommendation
- no silent mutation of operational truth

See `docs/adr/ADR-006-Intelligence-Advisory-Boundary.md`.

### PHASE 16 — Intelligence Expansion & Product Hardening

**Status:** CLOSED

Informational intent:

- broader advisory capability
- security, performance, release hardening
- red team of the running product

Hardening cannot relax Articles of this constitution.

---

## 4. Pipeline Alignment

The product pipeline remains:

```text
CAPTURE
   ↓
JOURNAL
   ↓
CLASSIFICATION
   ↓
OPERATIONAL RECORD
   ↓
STATE
   ↓
TIME / DEPENDENCIES
   ↓
WORKFLOW
   ↓
RULES
   ↓
INTEGRITY
   ↓
ANALYTICS
   ↓
INTELLIGENCE
```

Later layers may derive from earlier layers.
Later layers may not silently rewrite earlier truth.

Phase numbers exist to sequence construction.
The pipeline exists to sequence meaning.
Do not confuse the two.

---

## 5. What Each Closed Phase Must Inherit

Regardless of phase, future work inherits:

1. Offline-first device authority
2. Deterministic domain core
3. Journal history immutability
4. One domain, one owner of truth
5. No UI business logic
6. Intelligence as recommendation only
7. Explicit time inputs in domain rules
8. This engineering sequence and Owner gate

---

## 6. Entry Rule for a Later Phase

A later phase may start only when all of the following are true:

- Owner has named that phase and authorized its scope
- previous authorized work has been reported in reality
- the repository baseline of that moment has been inspected
- the new scope does not include unnamed later phases

Until then, the phase is CLOSED.
