# ADR-017 — Deterministic Rules Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 10 — Deterministic Rules Engine
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phases 5–9 already carry `basis = RULE` and `ruleVersion` as provenance.
Phase 10 supplies the evaluator those versions refer to. It must not
become an autonomous mutator of OperationalState, Workflow, Temporal,
Dependency, or Classification.

## Decision

1. Rules own identity, immutable versions, conditions, evaluation, and
   recommendation results. They do not own other domains.
2. Identity is caller-supplied `RuleId` + `RuleVersion`.
3. A version is immutable. New meaning requires a new version. No update
   path rewrites an existing `(id, version)`.
4. Conditions are a typed algebra: `All`, `Any`, `Not`, and leaves over
   existing facts (`OperationalStateIs`, `DueStatusIs`, workflow flags,
   `HasDependencies`, `ClassificationIs`). Not a scripting language.
5. Empty `All` is MATCH. Empty `Any` is NO_MATCH. Missing leaf input is
   INAPPLICABLE, not false.
6. Evaluation uses explicit `RuleEvaluationInput` and
   `EvaluationContext` / `EvaluationInstant`. No `Instant.now()`.
7. Same rule version + same input + same context = same result.
8. Results identify rule, version, match, decision, and evaluation time.
9. Decisions are recommendations only: `NoAction`,
   `RecommendStateTransition`, `RecommendWorkflowAdvancement`.
10. Applying a recommendation requires the owning domain use case.
    Evaluation never calls `save` on other repositories.
11. Rule-set order: `key`, then `id`, then `version`.
12. Two MATCH results with incompatible decisions yield `CONFLICT`.
    No last-write-wins.
13. Evaluation history is not persisted in Phase 10. Evaluation is a
    pure computation.
14. Ports: `RuleRepository`. In-memory and Room enforce unique
    `(id, version)`.
15. Table `rules` PK `(id, version)`. Room version 7. `MIGRATION_6_7`.
16. Offline-first. No network rule server.
17. Deferred: autonomous execution, tasks, reminders, notifications,
    schedulers, AI, Phase 11 Integrity.
18. Rejected: eval()/script engines, mutable rule rows, hidden clocks,
    evaluation that writes OperationalState or Workflow history.

## Consequences

`ruleVersion` strings used by earlier phases can now name a real
immutable rule version. Intelligence and analytics remain closed.
