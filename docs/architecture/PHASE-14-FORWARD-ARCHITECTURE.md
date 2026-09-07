# Phase 14 — Forward Architecture Analysis

Status: Analysis only. No implementation. No schema change.

## Post-core map

```
CANONICAL CORE
  Capture → Journal → Classification → Operational Record
       ├─ State history
       ├─ Temporal assignment
       └─ Dependency graph
            → Workflow
            → Rules (evaluate / recommend)
            → Integrity (observe)
                    ↓
            PROJECTION (ephemeral OperationalOverview)
                    ↓
            ANALYTICS (not implemented)
                    ↓
            INTELLIGENCE (not implemented)
                    ↓
            RECOMMENDATION (Rules already exist)
                    ↓
            AUTHORIZED HUMAN ACTION
                    ↓
            owning domain use case
```

## Candidate evaluation

| Candidate | Value | Authority risk | Duplicate risk | Prerequisite | Priority |
|---|---|---|---|---|---|
| Human decision / acknowledgement | Closes the last gap: recommendation → explicit action | Low if it only records a decision then calls owning use cases | Medium vs Workflow steps | Hardened core (done as design) | **1** |
| Deterministic analytics (ephemeral) | Counts and distributions over history | High if metrics are persisted as truth | Low if derived | Projection | 2 |
| Search / query | Find records without new truth | Low | Low | Projection | 3 |
| Reporting | Export of projections | Low | Low vs analytics | Projection | 4 |
| Dashboard UI | Display only | High if UI computes policy | Medium | Projection + no policy in Compose | 5 |
| Notifications / reminders | Surface due status | High if scheduler owns time | High vs Temporal | Human action + explicit schedule authority | 6 |
| Intelligence foundation | Interpret projection + rules | High if opaque | Medium vs Rules | Analytics + human action | 7 |
| AI advisory | Language over facts | Critical if it mutates | High | Intelligence boundary ADR | 8 |
| Task / todo domain | Familiar UX | Critical: second lifecycle | **Critical** vs State + Workflow | Should not precede human action | Not recommended now |

## Recommended roadmap

**OPTION E — Hybrid staged roadmap**

1. Human Action / Decision layer (acknowledgement, approval, explicit execution request)
2. Ephemeral deterministic analytics over projections
3. Search / reporting
4. Presentation surfaces that only render projections
5. Intelligence only after Rules + Action are stable
6. AI may advise; it may never write canonical truth

Do **not** add a Task domain first. Workflow steps and operational state already own work lifecycle.

## AI boundary

AI may:

- explain a projection
- draft a recommendation already expressible as Rules
- propose an action that a human must authorize

AI must not:

- mint time or IDs
- write Capture/Journal/Classification/Record/State/Temporal/Dependency/Workflow/Rules
- persist an interpretation as authority
- schedule or notify autonomously

## Future ADRs required before implementation

- ADR-021 Human Decision / Authorized Action
- ADR-022 Ephemeral Analytics Boundary
- ADR-023 Notification Authority (if ever authorized)
- ADR-024 Intelligence Advisory Boundary (extends ADR-006)
- ADR-025 AI Non-Mutation Contract
