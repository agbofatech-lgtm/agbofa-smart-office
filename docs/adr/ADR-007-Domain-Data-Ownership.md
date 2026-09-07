# ADR-007 — Domain Data Ownership

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 0 — Architecture Constitution
- Deciders: AGBOFA Technologies Owner Directive

## Context

The product will eventually store journal events, classifications,
operations, states, schedules, dependencies, workflows, rules, finance
records, integrity reports, analytics, and recommendations.

If two domains persist the same authoritative field, later phases will
diverge and one layer will silently rewrite another.

## Decision

1. **One domain has one authoritative owner of its truth.**
2. Other domains may store a foreign identifier. They may not store a
   second authoritative copy of the foreign fields.
3. Ownership follows the map in DOMAIN_BOUNDARIES.md.
4. Pipeline order is constitutional: a later layer may derive from an
   earlier layer; it may not overwrite earlier historical truth.
5. Journal text, capture identity, and capture instant are Journal-owned
   history. Corrections are new records that reference the original.
6. Finance owns money facts. Operations owns actionable work.
   State owns transitions. Scheduling owns temporal allocation.
   Those four must not collapse into one mutable row.
7. Analytics and Intelligence own derived / advisory records only.
   They have no write port into Journal, Finance, Operations, or State.
8. Integrity owns check results, not repairs.
9. Persistence mappings in Data copy snapshots for storage.
   A Room entity is not a second owner. Mappers must not invent fields
   the domain did not authorize.

## Consequences

### Allowed

- `FinanceObligation.journalEventId`
- `Operation.journalEventId`
- `Schedule.operationId`
- Read models that project another domain for queries

### Forbidden

- Operations rewriting journal text
- Finance writing `status = SETTLED` onto a record State owns
- Intelligence persisting a due time into Scheduling columns
- Analytics becoming the source of “overdue”
- Kernel accumulating business columns because it is convenient

### Phase 0 non-goals

No schemas, no DAOs, no placeholder entities.
Ownership is recorded so Phase 1+ does not invent a second map.

## Related

- DOMAIN_BOUNDARIES.md
- ADR-003 Journal as Operational Entry Point
- ADR-004 Domain Determinism
- ADR-006 Intelligence Advisory Boundary
- DEPENDENCY_RULES.md
