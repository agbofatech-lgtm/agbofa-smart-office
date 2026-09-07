# ADR-016 — Deterministic Workflow Architecture

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 9 — Deterministic Workflow Engine
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phase 8 added temporal and dependency meaning. Phase 9 adds a deterministic
process without replacing OperationalState or becoming a task/reminder system.

## Decision

1. Workflow owns process structure and step progression for one
   OperationalRecord. UNIQUE(operationalRecordId).
2. Workflow ≠ OperationalState. State answers lifecycle. Workflow answers
   which process step is active. Workflow does not write OperationalState.
3. Identity is caller-supplied `WorkflowId` / `WorkflowStepId` /
   `WorkflowStepTransitionId`.
4. A step is `(id, workflowId, ordinal, key, label)`. No due, priority,
   assignee, reminder, or checklist fields.
5. Ordering is `ordinal` then step id. Duplicate ordinals are rejected.
6. Step lifecycle: PENDING, ACTIVE, COMPLETED, CANCELLED.
   PENDING → ACTIVE|CANCELLED; ACTIVE → COMPLETED|CANCELLED; terminals reject.
7. History is immutable `WorkflowStepTransition` rows.
8. Projection: no history → PENDING; otherwise latest `toStatus` by
   `transitionedAt` then transition id.
9. Advancement is explicit `AdvanceWorkflowUseCase` only. It completes the
   ACTIVE step and activates the next PENDING ordinal. Time passing does
   not advance a workflow.
10. OperationalState remains owned by Phase 7. Phase 9 does not call
    state transitions automatically.
11. Temporal ownership remains Phase 8. Due does not advance workflow.
12. Dependency ownership remains Phase 8. Workflow does not copy the graph.
13. Ports: WorkflowRepository, WorkflowStepRepository,
    WorkflowStepTransitionRepository.
14. In-memory repositories enforce the same uniqueness as Room.
15. Room tables: workflows, workflow_steps, workflow_step_transitions.
16. Room version 6. `MIGRATION_5_6` SOURCE PRESENT. Destructive fallback
    inherited, not expanded.
17. Same workflow + steps + history = same projection. No hidden clock.
18. Device-local Room is authoritative. No network.
19. Deferred: Rules Engine, Tasks, Reminders, Notifications, AI.
20. Rejected: mutable currentStep field (dual truth); auto-advance from
    DueInstant; treating steps as tasks.

## Consequences

Phase 10 Rules remains closed. Workflow completion is "all steps COMPLETED".
