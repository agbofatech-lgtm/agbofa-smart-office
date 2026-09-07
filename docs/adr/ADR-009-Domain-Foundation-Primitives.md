# ADR-009 — Domain Foundation Primitives

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 2 — Core Domain Primitives
- Deciders: AGBOFA Technologies Owner Directive

## Context

Phase 2 authorizes the language of the domain before any business domain exists. Identity, time, result, error, evaluation context, and a domain-event contract must be chosen without inventing Journal, Task, or Finance types.

## Decision

### Identity

Use `@JvmInline value class` wrappers over a non-blank `String`:

- `EntityId`
- `EventId`
- `OperationId`

Rejected: raw `UUID` in domain APIs, `TaskId` / `JournalId` / `FinanceId`, hidden `UUID.randomUUID()` inside domain construction, extra identifier libraries.

Generation is the caller's responsibility. The domain only validates and types the value. That keeps identifiers offline-capable, Room-compatible as strings, and testable with fixed values.

### Time

Keep `java.time`. Do not write a calendar library.

Use one-line role wrappers around `Instant` so capture, event, due, schedule, transition, and evaluation cannot be assigned to each other. Represent user-intended wall time as `CivilTime(LocalDateTime, ZoneId)`.

Domain decision functions receive `EvaluationInstant` through `EvaluationContext`. They do not call `Instant.now()`, `LocalDateTime.now()`, or `System.currentTimeMillis()`.

`DomainClock` exists only as an edge port that mints `EvaluationInstant`. Decision functions take `EvaluationInstant` or `EvaluationContext`, not the clock.

### Result and error

Use a custom `DomainResult<T> = Success(T) | Failure(DomainError)`.

Kotlin `Result` was rejected: failure is a `Throwable`, which pushes the domain toward exceptions as control flow and erases typed `DomainError` values.

Errors in this phase: `ValidationError`, `InvariantViolation`, `InvalidState` only.

### Domain event

A single immutable `DomainEvent` data class: id, occurredAt, type, optional source.

No event bus, no event store, no product event subtypes.

## Consequences

- Later phases add product IDs by wrapping or by adding new value classes in their own packages.
- Temporal collapse is a type error, not a comment.
- Unit tests supply fixed IDs and fixed `EvaluationInstant`.
- Phase 2 does not compile-certify against the Android SDK. Source remains pure Kotlin.

## Phase 2 boundary

No Journal, Capture, Finance, scheduling engine, Room, or UI types are introduced by this ADR.
