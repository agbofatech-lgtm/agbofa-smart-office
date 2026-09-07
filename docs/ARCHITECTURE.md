# AGBOFA SMART OFFICE — Architecture

**Product:** AGBOFA SMART OFFICE  
**Owner:** AGBOFA Technologies  
**Classification:** Deterministic Offline Personal Operations System  
**Platform:** Native Android  
**Document status:** Phase 0 constitution (no runtime implemented)  
**Effective date:** 2026-09-07

This document defines the canonical system architecture. It is not an implementation report. No Android application, feature module, or runtime kernel exists at the time of writing.

---

## 1. Product identity

AGBOFA SMART OFFICE is a personal operational environment. It exists to capture real-world human events, preserve them as journal truth, classify them deterministically, and operate on the derived records.

It is not a conventional todo-list application.

**Core principle:** Capture once. Classify deterministically. Operate systematically.

**Authoritative pipeline:**

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

A later layer may derive from an earlier layer. A later layer may not silently rewrite the truth an earlier layer already preserved.

---

## 2. Architectural style

The system uses **MVVM + Clean Architecture** on native Android.

| Layer | Responsibility | Allowed to know |
|---|---|---|
| Presentation | Rendering, UI state, user gestures | Application use cases and UI models only |
| Application | Commands, queries, use-case orchestration | Domain types and ports |
| Domain | Deterministic truth, rules, transitions | Pure Kotlin only |
| Data | Persistence adapters, mapping, local repositories | Domain ports + Room/SQLite |
| Infrastructure | Android platform adapters | Android APIs; calls into application/domain via ports |
| Core | Shared primitives: Result, time ports, identifiers, test fixtures | No product truth |

**Dependency direction (constitutional):**

```text
PRESENTATION
      ↓
APPLICATION
      ↓
DOMAIN
      ↑
      │
DATA
      │
INFRASTRUCTURE
```

Presentation, Data, and Infrastructure may all depend on Domain. Domain depends on nothing above itself and nothing from the Android framework.

See [DEPENDENCY_RULES.md](DEPENDENCY_RULES.md) and [ADR-001](adr/ADR-001-Native-Android-Architecture.md).

---

## 3. Logical repository blueprint

Phase 0 authorizes a blueprint only. Packages below are names for a future native repository. They must not be pre-built as empty speculative code in this phase.

When Phase 1 is authorized, the first Gradle module should be a **single `app` module** with package-level layers. Multi-module Gradle extraction is a later hardening concern, not a Phase 0 requirement.

```text
agbofa-smart-office/
├── docs/                          # this constitution (exists after Phase 0)
│   ├── adr/
│   └── ...
├── app/                           # authorized to exist only in Phase 1+
│   ├── core/
│   │   ├── common/
│   │   ├── time/
│   │   ├── result/
│   │   └── testing/
│   ├── domain/
│   │   ├── kernel/
│   │   ├── journal/
│   │   ├── classification/
│   │   ├── operations/
│   │   ├── state/
│   │   ├── scheduling/
│   │   ├── dependencies/
│   │   ├── workflow/
│   │   ├── rules/
│   │   ├── integrity/
│   │   ├── finance/
│   │   ├── analytics/
│   │   └── intelligence/
│   ├── application/
│   │   ├── commands/
│   │   ├── queries/
│   │   └── usecases/
│   ├── data/
│   │   ├── database/
│   │   ├── repositories/
│   │   └── mappers/
│   ├── infrastructure/
│   │   ├── android/
│   │   ├── scheduling/
│   │   ├── notifications/
│   │   └── recovery/
│   └── presentation/
│       ├── navigation/
│       ├── screens/
│       ├── components/
│       └── viewmodels/
└── (Gradle / CI — Phase 1)
```

**Why a single app module first:** the constitution forbids speculative abstractions and over-engineering the initial repository. Package boundaries enforce the same rules as Gradle modules without multiplying build graphs before any product code exists.

---

## 4. Domain map

Each domain owns one truth. Other domains may reference that truth by identifier. They may not copy it as a second source of authority.

| Domain | Owns | Must not own |
|---|---|---|
| Kernel | Shared primitives, identifiers, command/result shapes | Journal facts, money, schedules, UI |
| Journal | Chronological human events as captured | Classification meaning, operational state |
| Classification | Meaning assignment derived from a journal event | Mutation of journal history |
| Operations | Actionable operational records | Lifecycle transition rules |
| State | Explicit lifecycle transitions | The operational payload itself |
| Scheduling | Temporal allocation of attention and due work | Device alarm APIs |
| Dependencies | Blocking / prerequisite relationships | Workflow step graphs |
| Workflow | Multi-step process definitions and progress | Financial settlement |
| Rules | Explicit deterministic authorization policies | Silent mutation of other domains |
| Integrity | Validation, consistency checks, contradiction reports | Authoritative business data |
| Finance | Obligations, allocations, payments, settlements | Journal narrative text |
| Analytics | Derived measurements | Write authority over operations |
| Intelligence | Recommendations, estimates, pattern notes | Any silent write to operational truth |

Full ownership rules: [DOMAIN_BOUNDARIES.md](DOMAIN_BOUNDARIES.md) and [ADR-007](adr/ADR-007-Domain-Data-Ownership.md).

---

## 5. Journal constitution

The General Journal is the universal operational entry point.

```text
ORIGINAL CAPTURE
       ↓
JOURNAL EVENT
       ↓
CLASSIFICATION
       ↓
DERIVED OPERATIONAL RECORDS
       ↓
EXECUTION
       ↓
OUTCOME
       ↓
AUDIT
```

**Rule:** Derived records may reference a Journal Event. They may not rewrite historical journal truth.

A journal event, once committed, keeps its original capture text, capture identity, and capture instant. Corrections are new journal events that reference the original. They are not in-place edits of history.

See [ADR-003](adr/ADR-003-Journal-as-Operational-Entry-Point.md).

---

## 6. Intelligence boundary

Intelligence is advisory.

```text
Allowed:
INTELLIGENCE → RECOMMENDATION → USER OR DETERMINISTIC RULE AUTHORIZATION → DOMAIN OPERATION

Forbidden:
INTELLIGENCE → SILENT DATA MUTATION
```

Intelligence may analyze, predict, recommend, and estimate. It may not delete operations, cancel commitments, modify financial records, override deterministic rules, or reschedule critical work without an explicit authorized path.

See [ADR-006](adr/ADR-006-Intelligence-Advisory-Boundary.md).

---

## 7. Offline authority

The device is initially authoritative. Core function must not require internet, accounts, Firebase, remote APIs, or a backend.

Connectivity, if introduced later, is optional. Offline is the normal mode, not a degraded mode.

See [OFFLINE_FIRST.md](OFFLINE_FIRST.md) and [ADR-002](adr/ADR-002-Offline-First-Authority-Model.md).

---

## 8. Determinism

For deterministic domain operations:

```text
Same Input + Same State + Same Rules = Same Result
```

Domain rules must be explicit, testable, explainable, and reproducible. Hidden `now()`, hidden randomness, and hidden I/O are forbidden inside domain decision functions.

See [ADR-004](adr/ADR-004-Domain-Determinism.md) and [TIME_MODEL.md](TIME_MODEL.md).

---

## 9. Presentation rule

Jetpack Compose screens render. They do not decide.

ViewModels hold UI state and invoke application use cases. Use cases invoke domain logic. Financial calculations, classification, scheduling, dependency resolution, workflow execution, and state-machine transitions do not live in composables.

See [ADR-005](adr/ADR-005-No-UI-Business-Logic.md).

---

## 10. Technology intent (not implemented)

| Concern | Intent | Status |
|---|---|---|
| Language | Kotlin | Not implemented |
| UI | Jetpack Compose | Not implemented |
| Persistence | Room + SQLite | Not implemented |
| Architecture | MVVM + Clean Architecture | Specified only |
| DI | Hilt at the Android boundary; domain remains constructor-injected and framework-agnostic | Specified only |
| Deferrable background work | WorkManager | Not implemented; not in Phase 0 |
| Exact local time signals | AlarmManager, evaluated later | Not implemented |
| Build | Gradle Kotlin DSL | Not implemented |
| Min SDK | API 26 (Android 8.0) | Specified |
| Target / compile SDK | Current stable production Android SDK at Phase 1 initialization | Specified |
| Tests | JUnit, coroutine tests, later Compose UI tests | Not implemented |

No experimental dependencies are authorized.

---

## 11. What Phase 0 contains

Phase 0 contains this documentation set and the ADRs listed in [docs/README.md](README.md).

Phase 0 does not contain:

- an Android project
- Gradle modules
- Room databases
- screens
- journal implementation
- placeholder interfaces that pretend later engines exist

---

## 12. Related documents

- [ENGINEERING_DISCIPLINE.md](ENGINEERING_DISCIPLINE.md)
- [DOMAIN_BOUNDARIES.md](DOMAIN_BOUNDARIES.md)
- [OFFLINE_FIRST.md](OFFLINE_FIRST.md)
- [DEPENDENCY_RULES.md](DEPENDENCY_RULES.md)
- [TIME_MODEL.md](TIME_MODEL.md)
- [PHASE_ROADMAP.md](PHASE_ROADMAP.md)
- [adr/](adr/)
