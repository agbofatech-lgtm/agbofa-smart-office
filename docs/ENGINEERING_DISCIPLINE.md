# ENGINEERING DISCIPLINE

Product: AGBOFA SMART OFFICE  
Owner: AGBOFA Technologies  
Mode: PHASE 0 — Constitution & Architecture  
Status: AUTHORIZED DOCUMENTATION — NOT IMPLEMENTATION  
Rule: SPECIFIED ≠ IMPLEMENTED

This document is the engineering operating law for every future phase.
It is not a product feature specification.

---

## 1. Purpose

AGBOFA SMART OFFICE will be built under gated discipline so that:

- scope cannot silently expand
- later layers cannot rewrite earlier truth
- documentation and code stay distinguishable
- Owner remains the only authority that opens a phase

Phase 0 establishes this discipline. It does not build the application.

---

## 2. Mandatory Sequence

Every authorized phase MUST follow this exact sequence:

```text
MODE DECLARED
      ↓
REPOSITORY BASELINE
      ↓
SCOPE AUTHORIZED
      ↓
INSPECT FIRST
      ↓
IMPLEMENT ONLY AUTHORIZED SCOPE
      ↓
TEST
      ↓
RED TEAM
      ↓
AUDIT
      ↓
COMMIT
      ↓
REPORT REALITY
      ↓
OWNER GATE
```

Do not skip stages.
Do not reorder stages to "save time."
Do not treat a later stage as permission to reopen an earlier unauthorized scope.

---

## 3. Stage Definitions

### 3.1 MODE DECLARED

Name the phase and the class of work before touching files.

Examples of valid modes:

- PHASE 0 — architecture documentation
- PHASE 1 — native repository foundation
- PHASE 4 — general journal

A mode that is not declared is not in force.

### 3.2 REPOSITORY BASELINE

Inspect what actually exists.

Report:

- repository structure
- modules
- build system
- dependencies
- tests
- architectural patterns
- debt

If nothing exists, say:

`EMPTY REPOSITORY BASELINE CONFIRMED`

Do not invent a codebase.
Do not describe a planned tree as if it were already committed.

### 3.3 SCOPE AUTHORIZED

Work only inside the Owner-authorized scope for that phase.

Unauthorized work is defect, even if it is "obviously needed later."

### 3.4 INSPECT FIRST

Read existing documents, ADRs, and code before writing.

Phase 0 inspection result: this workspace contained only pasted Owner briefs. No Android repository existed. No Kotlin modules existed.

### 3.5 IMPLEMENT ONLY AUTHORIZED SCOPE

In Phase 0, authorized implementation means architecture artifacts only:

- architecture documents
- domain-boundary documents
- ADRs

Unauthorized in Phase 0:

- application source
- Gradle modules
- Compose screens
- Room databases
- placeholder interfaces that pretend later engines exist
- workers, notifications, auth, sync, AI

### 3.6 TEST

Every phase that produces executable code must add tests for the authorized scope.

Phase 0 produces documents, not executable product code. The Phase 0 "test" is:

- internal consistency of documents
- explicit dependency direction
- unambiguous ownership
- no feature-code leakage into the artifacts

### 3.7 RED TEAM

After implementation of the authorized scope, attempt to break the constitution:

- circular dependencies
- dual owners of the same truth
- UI logic leakage paths
- intelligence bypass paths
- cloud assumptions
- journal mutability
- hidden time sources
- silent scope expansion

Findings must be written down. Silence is not a passing review.

### 3.8 AUDIT

Verify that the delivered artifacts match the authorized scope and the constitution.

Audit asks:

- Was only authorized work done?
- Do documents claim implementation that does not exist?
- Are dependency rules explicit?
- Can a later phase obey these documents without rewriting them?

### 3.9 COMMIT

Commit only the authorized artifacts, with a message that names the mode and the scope.

Do not commit speculative files "for later."

Phase 0 commits, if and when a repository exists, are documentation-only.

### 3.10 REPORT REALITY

Report what exists, not what is intended.

Forbidden claims in a Phase 0 report:

- "the app is built"
- "the journal works"
- "modules are implemented"
- "tests pass" when no product tests exist

Required claims:

- which documents exist
- which decisions were recorded
- which risks remain open
- which phase is still closed

### 3.11 OWNER GATE

No next phase starts without explicit Owner authorization.

Authorization of Phase N is not authorization of Phase N+1.
Authorization of documentation is not authorization of product features.

---

## 4. Scope Control Rules

1. A future-phase name in the roadmap is informational. It is not a work order.
2. Creating an empty package or interface "so the later phase is easier" is speculative abstraction and is forbidden unless the current authorization requires it.
3. Copying a later-phase engine into Phase 0 as a stub is forbidden.
4. If a needed decision is not yet authorized, record it as an open Owner question. Do not invent the missing product.

---

## 5. Truth and Language Rules

Use these distinctions in every report:

| Phrase | Meaning |
|---|---|
| SPECIFIED | Written in constitution or ADR |
| BLUEPRINTED | Named as a future module or boundary |
| IMPLEMENTED | Exists as working product code |
| VERIFIED | Tested against an explicit rule |
| AUTHORIZED | Owner has opened that scope |
| CLOSED | Owner has not opened that scope |

Do not collapse these words.

---

## 6. Layer Discipline During Implementation Phases

When product code is later authorized:

```text
Presentation  →  Application  →  Domain
                                      ↑
Data ---------------------------------|
Infrastructure → Android platform
```

- Domain contains deterministic rules and remains free of Android, Compose, Room, and WorkManager.
- Presentation renders and routes user intent. It does not own classification, finance, scheduling, or state transitions.
- Application orchestrates use cases. It does not become a second domain.
- Data persists and maps. It does not own meaning.
- Infrastructure adapts platform services. It does not own operational truth.

Time-dependent domain logic must receive time as an explicit input. Hidden `now()` inside domain rules is a determinism defect.

---

## 7. Intelligence Discipline

Intelligence, when later authorized, is advisory.

Required path:

```text
INTELLIGENCE
     ↓
RECOMMENDATION
     ↓
USER OR DETERMINISTIC RULE AUTHORIZATION
     ↓
DOMAIN OPERATION
```

Forbidden path:

```text
INTELLIGENCE → SILENT DATA MUTATION
```

See `docs/adr/ADR-006-Intelligence-Advisory-Boundary.md`.

---

## 8. Journal Discipline

The original capture is historical truth.

Derived records may reference a journal event.
Derived records may not rewrite journal text, capture identity, or historical capture time.

See `docs/adr/ADR-003-Journal-as-Operational-Entry-Point.md` and `docs/DOMAIN_BOUNDARIES.md`.

---

## 9. Phase 0 Application of This Discipline

| Stage | Phase 0 action |
|---|---|
| Mode declared | PHASE 0 — Constitution & Architecture |
| Repository baseline | EMPTY REPOSITORY BASELINE CONFIRMED |
| Scope authorized | Architecture documents and ADRs only |
| Inspect first | Workspace contained only Owner pastes |
| Implement authorized scope | Create the documents listed in the Phase 0 authorization |
| Test | Consistency review of documents |
| Red team | Attack ownership, UI leakage, AI bypass, cloud assumptions, time ambiguity |
| Audit | Confirm no feature code and no Phase 1 repository |
| Commit | Documentation artifacts only, when a repo is authorized |
| Report reality | A–J report; no claim that an Android app exists |
| Owner gate | Phase 1 remains CLOSED |

---

## 10. Non-Compliance

The following are Phase 0 defects:

- product screens or ViewModels
- Room schemas or DAOs
- fake use cases for journal, finance, or workflows
- Firebase, auth, or analytics SDK additions
- claiming a later phase is complete because its folder was named
- treating the informational roadmap as a current work breakdown

---

## 11. Successor Rule

A later agent or engineer must obey this document even if a faster path appears convenient.

Convenience is not authorization.
