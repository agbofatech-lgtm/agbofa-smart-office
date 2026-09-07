# Stage 14.5 / Phase 15 — Forward Architecture Analysis

Status: Analysis only. Implementation not authorized. No schema change.

After Phase 14 the deterministic chain is:

```
FACT → CAPTURE → JOURNAL → CLASSIFICATION → OPERATIONAL RECORD
     → STATE / TEMPORAL / DEPENDENCY → WORKFLOW
     → RULES (recommend) → INTEGRITY (observe) → PROJECTION (ephemeral)
     → HUMAN DECISION → AUTHORIZED ACTION → OWNING DOMAIN MUTATION
```

## New capability from Phase 14

Humans can propose, approve, reject, or withdraw a Decision and
explicitly request bounded actions that dispatch to owning use cases.
Rules still cannot execute. Integrity still cannot repair.

## Candidate matrix

| Candidate | Architecture ready | New authority required | Canonical mutation risk | Recommended |
|---|---|---|---|---|
| Presentation of Overview + Decision | Yes | No | Low if UI only renders | **1 — next** |
| Search / reporting over existing ports | Yes | No if ephemeral | Low | 2 |
| Ephemeral analytics | Yes after projection | Only if persisted | High if stored as truth | 3 |
| Notifications | No | Yes — delivery history + scheduler | High | Later |
| Intelligence | Partial (Rules exist) | No if advisory only | High if execute | After presentation |
| AI advisory | Boundary known (ADR-006/021) | No if constrained | Critical if mutate | After intelligence ADR |
| Tasks | No | Yes | Critical duplicate of State + Workflow | Rejected |

## Analytics

Canonical sources: OperationalRecord, State history, Temporal, Dependency,
Workflow, Decision history, Integrity findings, OperationalOverview.

Analytics can remain ephemeral. Persistence would create a second truth.
Analytics must not mutate domains, influence Rules autonomously, or trigger
Actions. Incomplete integrity is a finding, not a repaired fact.

## Search / reporting

Can use existing repository list/find ports and projections.
Must not invent ranking/priority authority.

## Presentation

Current UI is a Journal proof surface plus Phase 1 shells.
Bottleneck is exposing OperationalOverview and Decision projection,
not a new domain engine.

## Notifications

Require delivery-history authority and scheduler contract.
Due evaluation must not auto-create Decisions or execute Actions.

## Intelligence / AI

Intelligence → Recommendation → Human Decision → Authorized Action → Owner.
AI may explain, summarize, draft, recommend.
AI may not mint time/IDs, mutate repositories, authorize, or execute.

## Persistence implications for any Phase 15

Room is at version 8. Phase 13 fail-closed migrations must remain.
Presentation and search need no schema bump.
Analytics persistence and notification history would require later ADRs.

## Recommended next implementation (not authorized here)

Presentation architecture that renders OperationalOverview and Decision
projection through existing use cases. No new truth. No analytics engine.
No Tasks. No Notifications. No AI.

Required future ADRs before later layers:
- ADR-022 Presentation / Query Surface
- ADR-023 Ephemeral Analytics Boundary
- ADR-024 Notification Authority
- ADR-025 Intelligence Advisory / AI Non-Mutation

Do not implement this document.
