# Phase 16 — Architectural Analysis

MODE = READ_ONLY. IMPLEMENTATION = NOT AUTHORIZED.

## Baseline after Phase 15

Canonical core + projection + human decision + authorized action +
ephemeral analytics.

Analytics is derived and disposable.

## Authority map (post-15)

| Concept | Canonical? | Derived? | Rebuildable? |
|---|---|---|---|
| Capture / Journal / Classification / Record | Yes | No | No (authority) |
| State / Temporal / Workflow history | Yes | Projection yes | Yes from history |
| OperationalOverview | No | Yes | Yes |
| Analytics result | No | Yes | Yes |
| Search index | Would be derived | Yes if rebuildable | Must be rebuildable |
| Report / Dashboard | Derived rendering | Yes | Yes |
| Intelligence recommendation | Advisory | Yes | Yes |
| AI response | Advisory | Yes | Yes |

## Options

| Option | Value | Risk | Persistence | Position |
|---|---|---|---|---|
| A Search | High | Index becoming authority | Rebuildable only | **Next** |
| B Reporting/Export | Medium | Artifact authority | Optional later | After search |
| C Presentation/Dashboard | High | UI policy leak | None | Parallel with search, render-only |
| D Intelligence | Medium | Execution leak | None | After search+action usage |
| E Hybrid | High | Scope bleed | None | Staged A then C then B then D |

## Recommendation

OPTION E — Hybrid staged:

1. Deterministic Search over existing projections (rebuildable, no new truth).
2. Thin Presentation that only renders Overview + Analytics + Decision.
3. Reporting/export only after search queries are stable.
4. Intelligence / AI advisory last, never executing.

## Prohibited next

Tasks, notifications, schedulers, persisted analytics, AI mutation,
generic executors, dashboards that compute policy.

## Certification debt unchanged

No Android SDK. Tests SOURCE PRESENT only. Migration 1→8 source-only.
allowMainThreadQueries remains documented.
