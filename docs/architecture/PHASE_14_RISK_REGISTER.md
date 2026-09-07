# Phase 14 Risk Register

| ID | Risk | Attack | Mitigation |
|---|---|---|---|
| F14-01 | Analytics overwrite facts | stored KPI replaces state | ephemeral aggregates only |
| F14-02 | Intelligence becomes truth | model output persisted as state | advisory objects, no save |
| F14-03 | AI executes directly | tool-call into repositories | execution only via owner use case |
| F14-04 | Task duplicates workflow/state | second lifecycle | do not add Task next |
| F14-05 | Notification owns due time | reminder mutates temporal | derive from DueStatus |
| F14-06 | Dashboard policy | Compose computes transitions | UI renders overview only |
| F14-07 | Recommendation auto-writes | evaluate then save | keep evaluate read-only |
| F14-08 | Cached overview becomes canonical | overview table | keep Phase 12 disposable |
| F14-09 | Historical reinterpretation | new projector rewrites old pins | keep pinned classificationId |
| F14-10 | Cross-domain cycles | analytics writing operations | one-way read from core |
