# ADR-005 — No UI Business Logic

Product: AGBOFA SMART OFFICE  
Status: ACCEPTED for Phase 0 constitution  
Date: 2026-09-07  
Mode: PHASE 0 — documentation only  
Rule: SPECIFIED ≠ IMPLEMENTED

---

## Context

The product will eventually have Jetpack Compose screens.
Those screens will be the most tempting place to put "just a little logic":

- marking a task done
- computing a balance
- deciding that a note is a payment
- choosing when a reminder should fire
- advancing a workflow step

If that happens, the deterministic core leaks into the UI, becomes untestable
without Android, and can diverge from domain rules.

Phase 0 does not build UI. It forbids this future failure mode now.

---

## Decision

Presentation may contain:

- composition and layout
- navigation
- ephemeral UI state (what is on screen, what field is focused)
- mapping of user gestures into application commands or queries
- rendering of results, errors, and recommendations

Presentation may not contain:

- classification rules
- state-machine transitions
- financial calculations
- scheduling algorithms
- dependency resolution
- workflow execution
- integrity predicates that decide whether an operation is legal
- intelligence inference
- any rule whose result must be identical for identical inputs and state

Required call path:

```text
Compose screen
      ↓
ViewModel
      ↓
Application use case / command / query
      ↓
Domain
```

The screen does not call domain services directly for operational decisions.
The ViewModel does not embed domain rules. It adapts domain or application
results into UI state.

---

## Consequences

### Allowed later

- a screen that displays a journal event exactly as stored
- a ViewModel that submits `CaptureJournalEvent` and shows success or failure
- a screen that shows an intelligence recommendation as a recommendation

### Forbidden later

- `onClick { task.status = DONE }`
- computing "amount remaining" inside a Composable
- parsing "Tuesday 8 AM" into a schedule inside a ViewModel
- auto-creating a financial obligation inside a text-field callback
- applying an AI suggestion directly to Room entities from the UI layer

### Test implication

When UI tests exist, they test rendering and intent dispatch.
Deterministic rules are tested in domain unit tests without Compose.

---

## Detection

Treat any of the following as a defect when product code is authorized:

- `when (status)` decision trees in a Composable that change operational meaning
- use of `Clock.System.now()` inside presentation to decide a domain outcome
- Room DAOs referenced from Composables
- duplicated money or date math in presentation and domain

---

## Phase 0 boundary

This ADR is law.
It is not a Compose module, not a ViewModel, and not a navigation graph.

Those artifacts belong to later authorized phases.
