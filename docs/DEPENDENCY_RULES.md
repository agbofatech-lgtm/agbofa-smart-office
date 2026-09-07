# AGBOFA SMART OFFICE — Dependency Rules

**Status:** Phase 0 constitutional rule  
**Related:** [ADR-001](adr/ADR-001-Native-Android-Architecture.md), [ADR-005](adr/ADR-005-No-UI-Business-Logic.md)

---

## 1. Allowed direction

```text
PRESENTATION
      ↓
APPLICATION
      ↓
DOMAIN
      ↑
      │
DATA
      │
INFRASTRUCTURE
```

Reading the arrows:

- Presentation may depend on Application (and on Domain types only if Application would otherwise become a dumb pass-through of UI models). Prefer Presentation → Application.
- Application may depend on Domain.
- Data may depend on Domain.
- Infrastructure may depend on Domain and on Application ports when it must invoke use cases from platform callbacks.
- Domain may depend on Core primitives only.
- Core may depend on nothing in Presentation, Application, Data, Infrastructure, or any product domain.

---

## 2. Forbidden directions

| From | To | Why forbidden |
|---|---|---|
| Domain | Presentation | UI would capture business truth |
| Domain | Application | Use cases would leak into rules |
| Domain | Data | Persistence details would capture rules |
| Domain | Infrastructure | Android APIs would capture rules |
| Domain | Room / WorkManager / AlarmManager / Compose | Platform coupling |
| Application | Presentation | Use cases would know screens |
| Application | Room entities | Persistence schema would leak upward |
| Presentation | Data | Screens would skip use cases |
| Presentation | Infrastructure platform APIs for business decisions | UI would schedule, classify, or calculate |
| Data | Presentation | Persistence would know screens |
| Intelligence | Data writes | Advisory layer would mutate truth |
| Analytics | Journal / Finance / State writes | Derived views would become authorities |

---

## 3. Layer contents

### Core

May contain: identifiers, `Result` / `Either` shapes, time ports, immutable common types, test clocks, test fixtures.

Must not contain: journal events, money ledgers, workflow graphs, Compose, Room.

### Domain

May contain: entities, value objects, domain events, state-machine definitions, classification rules, financial rules, integrity predicates, repository *ports* (interfaces).

Must not contain: Compose, Android framework types, Room annotations, WorkManager, Retrofit, Firebase, `android.*`, `androidx.*` except nothing — domain is pure Kotlin.

Domain functions that need time receive a `Clock` or an explicit instant. They do not call platform time.

### Application

May contain: use cases, command handlers, query handlers, transaction boundaries as interfaces, authorization-to-execute checks that call Domain and Rules.

Must not contain: Compose UI, Room DAOs, notification channel setup.

### Data

May contain: Room database, DAOs, entities, mappers between persistence models and domain models, repository implementations.

Must not contain: classification rules, financial calculations, UI state.

### Infrastructure

May contain: AlarmManager adapters, WorkManager workers (when authorized), notification adapters, file/backup/recovery adapters, Android `Context` wrappers.

Must not contain: domain decision logic. An alarm firing is a platform event. The meaning of that event is decided by Application + Domain.

### Presentation

May contain: composables, navigation, ViewModels, UI state holders, UI mappers from application results.

Must not contain: financial calculations, state transition rules, workflow rules, classification logic, scheduling algorithms, dependency resolution.

---

## 4. Dependency injection

**Decision:** Hilt at the Android boundary. Domain remains framework-agnostic.

- Domain classes expose constructor parameters. They do not import Hilt.
- Application use cases expose constructor parameters. They may be bound by Hilt in `app`.
- Data and Infrastructure modules bind ports to adapters.
- Tests construct domain objects directly, without a DI graph.

Koin is not selected. Hilt is the stable first-party Android recommendation and keeps compile-time binding at the edge.

This binding graph must not be created in Phase 0.

---

## 5. Cross-domain references

Domains collaborate by identifier, not by owning each other's records.

```text
FinanceObligation.journalEventId  →  JournalEvent.id
Operation.journalEventId          →  JournalEvent.id
Schedule.operationId              →  Operation.id
Recommendation.subjectId          →  the target record id
```

A domain may read another domain through an application query or a domain service port. It may not persist a second authoritative copy of that other domain's fields.

---

## 6. Future Gradle modules

If the repository is later split:

- `:domain` must have zero Android Gradle plugin usage.
- `:application` may be pure Kotlin.
- `:data` may use the Android library plugin because Room requires it.
- `:infrastructure` uses the Android library plugin.
- `:presentation` / `:app` use the Android application plugin.
- Module dependency arrows must match the layer arrows above.

Phase 0 does not create these modules.

---

## 7. Audit checks

A later implementation fails the dependency audit if any of these appear in `domain/`:

- `import android.`
- `import androidx.`
- `import androidx.room.`
- `import androidx.compose.`
- `import androidx.work.`
- `import dagger.hilt.`
- network clients
- `java.time.Clock.systemDefaultZone()` or `System.currentTimeMillis()` inside decision logic
