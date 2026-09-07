# ADR-006 — Intelligence Advisory Boundary

Product: AGBOFA SMART OFFICE  
Status: ACCEPTED for Phase 0 constitution  
Date: 2026-09-07  
Mode: PHASE 0 — documentation only  
Rule: SPECIFIED ≠ IMPLEMENTED

---

## Context

Later phases name analytics, prediction, and behavioral intelligence.
Those capabilities are useful only if they cannot seize operational authority.

A personal operations system that lets a model silently:

- delete a commitment
- rewrite a journal line
- change a balance
- reschedule a critical follow-up
- mark a school-fees obligation settled

is no longer deterministic and is no longer auditable.

Phase 0 does not build intelligence. It locks the authority boundary.

---

## Decision

Intelligence may:

- analyze existing records
- detect patterns
- estimate likelihoods
- recommend classifications, actions, schedules, or reviews
- explain a recommendation

Intelligence may not:

- silently mutate journal history
- silently create, edit, complete, or cancel operations
- silently modify financial records
- bypass deterministic state transitions
- reschedule or dismiss work without policy authorization
- become the owner of any operational truth

Canonical path:

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

A deterministic rule engine (Phase 14, currently CLOSED) is not intelligence.
If an explicit, testable rule authorizes a command, that command still executes
in domain, not inside the intelligence component.

---

## Ownership

| Component | Authority |
|---|---|
| Intelligence | Advisory output only |
| Analytics | Derived measurements; read-only over operational truth |
| Rules | Explicit deterministic authorization policies, when later authorized |
| User | May accept or reject a recommendation |
| Domain | Sole executor of authorized operational change |
| Journal | Historical capture; not writable by intelligence |
| Finance / Operations / State | Not writable by intelligence |

Intelligence may hold copies or projections for analysis.
Those copies are not authoritative.

---

## Consequences

### Allowed later

- "this journal event looks like a financial obligation; accept?"
- "Koho follow-up appears due Tuesday; schedule 08:00?"
- "payment pattern suggests shortfall; review recommended"
- confidence scores attached to a recommendation object

### Forbidden later

- background job that completes tasks because a model is confident
- classifier that overwrites journal text with a "cleaned" version
- predictor that moves money between obligation records
- any API from intelligence packages to domain write ports

### Persistence implication

Recommendations, if stored, are recommendation records.
They are not operations, not journal events, and not financial postings.

---

## Interaction with determinism

Domain results must remain reproducible from:

```text
same input + same state + same rules = same result
```

Intelligence output may be non-deterministic.
That is why intelligence cannot be on the write path.

If a future phase wants automatic action, the automation must be an explicit
deterministic rule that a human authorized, not a model weight.

---

## Phase 0 boundary

No intelligence engine, prompt pipeline, model client, or recommendation
store is created in Phase 0.

This ADR only names the authority wall those future systems must not cross.
