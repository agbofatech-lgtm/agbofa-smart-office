# ADR-002 — Offline-First Authority Model

- Status: Accepted
- Date: 2026-09-07
- Product: AGBOFA SMART OFFICE
- Phase: 0 — Architecture Constitution
- Deciders: AGBOFA Technologies Owner Directive

## Context

A personal operations system that cannot capture a journal event without
a network is not the product that was specified.

Cloud-first templates assume accounts, remote IDs, and “offline mode”
as a degraded cache. That template would make connectivity a hidden
owner of truth.

## Decision

1. The **device is initially authoritative**.
2. Local persistence is the system of record until a later Owner
   directive authorizes another model.
3. Core capability must not require internet, cloud services, user
   accounts, authentication servers, Firebase, remote databases, or
   third-party backends.
4. Absence of connectivity is a normal condition. It is not an error
   and must not degrade Capture, Journal, Classification, Operations,
   State, Scheduling, Workflows, Finance, Integrity, or Analytics.
5. Domain identifiers must be creatable without a network.
6. If connectivity is later authorized, it is optional transport:

```
LOCAL AUTHORITATIVE RECORD
        → OPTIONAL TRANSPORT
        → REPLICA / BACKUP / MULTI-DEVICE
```

Never:

```
REMOTE SERVICE → LOCAL CACHE → DEGRADED OFFLINE MODE
```

7. Future sync, if authorized, must not become write authority over
   journal history.

## Consequences

### Allowed later

- Room / SQLite as local authority
- Local actor / device identity
- Local AlarmManager / WorkManager without a network success criterion

### Forbidden

- Login wall in front of capture
- Server-allocated IDs as the only identity
- Treating offline as a subset of the product
- Classification that cannot run without a remote model

### Phase 0 non-goals

No database, no network client, no “offline flag” in code.
This ADR binds later phases.

## Related

- OFFLINE_FIRST.md
- ADR-003 Journal as Operational Entry Point
- ADR-008 Time and Temporal Modeling
