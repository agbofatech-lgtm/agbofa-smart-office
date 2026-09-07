# PHASE 0 — Architecture Audit and Red Team

**Date:** 2026-09-07  
**Mode:** Phase 0 — Constitution & Architecture  
**Result:** Pass with residual risks recorded. No product code exists.

This review covers the Owner validation checklist. It does not claim an application was built.

---

## A. Architecture audit

| Check | Result |
|---|---|
| No product features implemented | Pass. No Kotlin, Gradle, Compose, Room, workers, or screens. |
| No speculative feature code | Pass. No placeholder repositories, use cases, or interfaces. |
| No UI business logic | Pass. ADR-005 records the prohibition. No UI exists to leak into. |
| Dependency direction explicit | Pass. DEPENDENCY_RULES.md and ADR-001. |
| Domain ownership unambiguous | Pass. DOMAIN_BOUNDARIES.md and ADR-007, including named collision resolutions. |
| Journal truth preservation explicit | Pass. ADR-003: derived records reference journal events; they do not rewrite history. |
| Intelligence cannot silently mutate | Pass. ADR-006 required path is recommendation → user or deterministic rule → domain. |
| Offline authority explicit | Pass. OFFLINE_FIRST.md and ADR-002: device is system of record. |
| Time model explicit | Pass. TIME_MODEL.md and ADR-008. |
| Phase 1 not started | Pass. No Android repository created. |

## B. Red team findings

Findings are specification risks. Documentation cannot enforce them. Each must be re-checked when Phase 1+ writes code or schema.

### Ownership and journal

| ID | Finding | Severity | Phase 0 treatment |
|---|---|---|---|
| RT-J1 | In-place journal edit to "match" a later classification | High | Forbidden by ADR-003. Amendments must be new records. |
| RT-J2 | UI shows only latest amendment and hides original capture | Medium | Residual. Future Journal UI must present history. |
| RT-J3 | Parsed GH₵ amount becomes the only trusted number | Medium | Residual. Finance amount is derived; journal text remains source capture. |
| RT-J4 | Finance vs Operations column collapse | High | Resolved on paper: shared IDs, not shared columns. |
| RT-J5 | Classification writes operations or ledger balances | High | Forbidden. Classification labels only. |
| RT-J6 | Workflow cursor stored as operation status | Medium | Resolved on paper: cursor ≠ lifecycle. |
| RT-J7 | Integrity auto-repairs source records | Medium | Forbidden. Integrity reports only. |
| RT-J8 | Kernel becomes a god-module | Medium | Forbidden. Kernel holds shapes, not business truth. |
| RT-J9 | Actor identity requires Firebase UID | High | Forbidden. Identity is local. |

### Intelligence and UI

| ID | Finding | Severity | Phase 0 treatment |
|---|---|---|---|
| RT-I1 | Intelligence writes journal, finance, operations, or state | High | Forbidden by ADR-006. |
| RT-I2 | Auto-apply recommendation when confidence ≥ X | High | Residual. Forbidden unless Owner authorizes an explicit deterministic rule that names the threshold. |
| RT-I3 | LLM classification used as an implicit rule engine | High | Residual. Classification rules must be explicit and versioned when implemented. |
| RT-I4 | `applyRecommendation(id)` skips visible authorization | High | Residual. Command types and recommendation types must stay distinct. |
| RT-I5 | Compose / ViewModel contains money math, overdue, or `status = DONE` | High | Forbidden by ADR-005. |
| RT-I6 | Presentation "helpers" accumulate domain rules | Medium | Residual. Detect by package audit in later phases. |

### Time, offline, dependencies

| ID | Finding | Severity | Phase 0 treatment |
|---|---|---|---|
| RT-T1 | Hidden `now()` in domain | High | Forbidden. Time is an explicit input. |
| RT-T2 | One timestamp for capture, event, due, and schedule | High | Six concepts named in TIME_MODEL.md. |
| RT-T3 | "Tuesday at 8 AM" silently becomes a schedule | High | Language is unresolved intent, not a schedule. |
| RT-T4 | Device clock rollback reorders capture history | Medium | Named, not solved. Integrity may annotate distrust; it may not rewrite history. |
| RT-T5 | Hard-coded Africa/Accra because examples use GH₵ | Low/Med | Zone is configuration. |
| RT-T6 | Future sync overwrites journal history | High | Sync, if ever authorized, is transport only. |
| RT-T7 | AlarmManager becomes a second scheduling domain | Medium | Infrastructure fires attention; Scheduling owns meaning. |
| RT-T8 | Presentation computes overdue with device clock | Medium | Overdue is a domain query given EvaluationInstant. |
| RT-T9 | Intelligence backdates or reschedules | High | No write authority. |
| RT-T10 | Speculative TimeProvider port creates a cycle | Medium | Phase 0 creates no clock port. Domain consumes values. |
| RT-T11 | Phase 1 template treats offline as degraded mode | High | ADR-002 binds Phase 1: no online prerequisites. |
| RT-T12 | Commands omit EvaluationInstant and cannot replay | Medium | Recorded for command design in later phases. |

### Circular dependency watch

Workflow → Rules → State → Workflow is the likely cycle.

Break recorded now:

- Rules are pure functions over facts
- State applies one transition
- Workflow advances cursor only after a State result
- Integrity must not import write ports

## C. Corrections made during Phase 0

- Retired the reconnaissance brief's phase numbering. Journal is Phase 4, not Phase 1.
- Chose one Gradle `app` module for Phase 1 instead of a multi-module forest. Deviation recorded in ADR-001.
- Named six time concepts instead of one timestamp.
- Banned hidden `now()` and banned treating temporal language as a schedule.
- Declared local persistence the system of record, not a cache.
- Declared recommendations and commands as different types.
- Did not create packages, interfaces, schemas, or a GitHub repository.
- Added this audit and the time/offline red-team working paper.

## D. Verification

- Required constitutional documents: present
- Required ADRs 001–008: present
- Product source files: none
- Android repository: none
- Phase 1 status: CLOSED

## E. Residual Owner questions (not defects)

These are not Phase 0 failures. They need Owner answers before the relevant later phase:

1. Create the GitHub repository during Phase 1 only, or also publish these docs now?
2. Single-device personal actor model until a later multi-device phase?
3. Will Phase 14 ever be allowed to auto-apply recommendations under an explicit rule?
