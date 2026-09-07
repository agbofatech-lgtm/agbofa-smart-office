# Phase 14 — Forward Architecture Analysis

Analysis only. No implementation.

## Strategic question

What next layer adds value without creating a second authority?

## Options

### A. Deterministic Analytics

Value: counts, durations, state distributions over existing projections.
Risk: materialized metrics becoming truth.
Requirement: ephemeral or separately labeled derived store. Never write canonical tables.

### B. Operational Intelligence

Value: interpretation of anomalies / context.
Risk: interpretation becoming execution.
Requires a recommendation object that is not a write.

### C. Task / Action Management

Not recommended as the next domain.
Workflow steps, operational state, and rule recommendations already cover structured work.
A Task type would duplicate lifecycle authority.

### D. Notification / Reminder

Derived from DueStatus + EvaluationInstant, not a new due authority.
Notification history, if persisted, must be its own append-only log and must not mutate Temporal or State.

### E. Human Decision / Action Layer

Missing link: Phase 10 can recommend; nothing records accept / reject / acknowledge as a first-class fact.
A Decision record would own only the human response, then call the existing owning use case.

## Decision matrix

| Candidate | Value | Authority risk | Duplicate risk | Prerequisites | Priority |
|---|---|---|---|---|---|
| Human Decision Layer | High | Low if it only records decisions | Low if it does not own state | Rules + Projection | 1 |
| Ephemeral Analytics | Medium | Medium if persisted | Low | Projection | 2 |
| Search / Query | Medium | Low | Low | Projection | 3 |
| Reporting | Medium | Medium if KPI stored | Low | Analytics | 4 |
| Intelligence | Medium | High | Medium vs Rules | Decision layer | 5 |
| Dashboard UI | Low-Medium | High if UI computes policy | Low | Projection | 6 |
| Notifications | Medium | High if they change due/state | Medium | Decision + Temporal | 7 |
| AI Advisory | Medium | Very high | High | Intelligence boundary | 8 |
| Tasks | Low now | Very high | Very high vs Workflow/State | None sufficient | Not next |

## Recommendation

OPTION E — Hybrid staged roadmap:

1. Keep the core frozen except certification when an SDK exists.
2. Next product architecture: Human Decision / Acknowledgement of rule recommendations, executing only through existing use cases.
3. Then ephemeral analytics over OperationalOverview.
4. Intelligence / AI only as advisory consumers.
5. Do not introduce Task or Notification engines until Decision and Analytics boundaries exist.

## Future ADRs required before implementation

- ADR-020 Human Decision / Acknowledgement boundary
- ADR-021 Analytics are derived, not canonical
- ADR-022 Intelligence / AI advisory-only contract
- ADR-023 Notification history is not due-status authority
