# ADR-004 — Domain Determinism

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 0 — Architecture Constitution
- Deciders: AGBOFA Technologies Owner Directive

## Context

AGBOFA SMART OFFICE will later include analytics and intelligence.
Those layers can estimate, rank, and recommend.

Operational truth cannot depend on them.

If classification, state transitions, financial settlement, or scheduling
authorization were allowed to call a probabilistic model directly,
identical inputs could produce different records. That violates the
product class: a deterministic offline personal operations system.

## Decision

For every deterministic domain operation:

```
Same Input
+ Same State
+ Same Rules
= Same Result
```

Deterministic domain includes, at minimum:

- Journal acceptance of a capture payload already in hand
- Classification rules that are explicit and versioned
- Operation creation from classified meaning
- State transitions
- Financial obligation arithmetic and settlement application
- Dependency satisfaction checks
- Workflow step authorization against explicit rules
- Integrity predicates

Rules governing those operations must be:

- explicit
- testable
- explainable
- reproducible

No hidden randomness, clock jitter used as a business coin-flip,
model sampling, or “best guess” may decide operational state.

## Boundary with intelligence

Intelligence may analyze, predict, recommend, and estimate.

Intelligence may not:

- silently mutate operational truth
- silently modify financial records
- bypass deterministic state transitions
- overwrite journal history
- reschedule by writing due times directly

Required path:

```
Intelligence → Recommendation
  → User authorization OR explicit deterministic Rule
  → Domain operation
```

A recommendation is not a command.
A confidence score is not authority.

## Time and determinism

Time is an input, not a hidden source of chance.

A domain function that needs “now” must receive an explicit time value
or a domain clock port defined in TIME_MODEL.md.
Tests must be able to supply a fixed instant.

Local timezone and calendar rules are data, not ambient device magic
inside domain logic.

## Android and I/O stay outside the decision

Domain determinism requires the decision function to be pure Kotlin
with respect to:

- no Compose
- no Android framework types
- no Room
- no WorkManager
- no network
- no unseeded random
- no direct `System.currentTimeMillis()` inside domain rules

I/O happens before or after the decision, not inside it.

## Consequences

### Allowed

- Versioned rule tables and explicit state machines in later phases
- Advisory models that emit recommendations
- Non-deterministic infrastructure (OS scheduling of alarms) provided
  it cannot change domain results for a given decision input

### Forbidden

- `if (model.saysComplete) markDone()`
- Seeded or unseeded randomness in state, finance, or classification
- Hidden defaulting that changes settlement or schedule from run to run
- Domain code that “sometimes” applies a rule based on UI timing

### Phase 0 non-goals

- No state machine implementation
- No rule engine
- No tests of product rules yet, because no product rules are implemented
- Determinism is a constraint on future code, recorded now

## Related

- ADR-001 Native Android Architecture
- ADR-005 No UI Business Logic
- ADR-006 Intelligence Advisory Boundary
- DEPENDENCY_RULES.md
- TIME_MODEL.md
