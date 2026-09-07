# Value object conventions

Phase 2 convention only. Not a framework.

## Rules

1. Immutable. Public properties are `val`. No `var`. No mutable collections exposed.
2. Construct through an explicit factory (`of`, `from`) that returns `DomainResult`.
3. Reject invalid values at construction. Do not create a half-valid object.
4. Equality is structural (value class / data class).
5. No hidden clock, network, Android, or random source inside the type.
6. Identifiers are typed. Do not pass a raw `String` across domain boundaries once an ID type exists.
7. Do not add product value objects (Money, TaskStatus, JournalEntry) in this phase.

## Current types that follow this convention

- `EntityId`, `EventId`, `OperationId`
- Instant role wrappers and `CivilTime`
- `DomainEvent`
- `DomainError` variants
- `EvaluationContext`
