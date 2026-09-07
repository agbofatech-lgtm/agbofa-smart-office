# OFFLINE-FIRST

Product: AGBOFA SMART OFFICE  
Owner: AGBOFA Technologies  
Mode: Phase 0 — Architecture Constitution  
Status: Specified. Not implemented.

## 1. Purpose

This document defines the offline-first authority model.

AGBOFA SMART OFFICE must operate as a personal operations system when the device has no internet, no account, no cloud, and no remote API. Connectivity is not a precondition for truth.

## 2. Constitutional Rule

The device is initially authoritative.

Core operations must remain available without:

- internet
- cloud services
- user accounts
- authentication servers
- Firebase
- remote databases
- third-party backend services

Absence of connectivity is a normal operating condition. It is not an error state and must not degrade core capability.

## 3. System of Record

Until a later phase explicitly authorizes another model:

- local persistence on the device is the system of record
- operational truth lives on the device
- identifiers used by the domain must be creatable without a network
- journal capture must succeed with no remote round trip

Future network capability, if authorized, is optional infrastructure. It must not become the owner of journal history, financial truth, or operational state.

## 4. What Must Work Offline

The following capabilities are constitutionally required to remain available without a network. They are not implemented in Phase 0. The architecture must not make them depend on connectivity.

- Capture
- Journal
- Classification
- Operations
- State
- Scheduling
- Workflows
- Finance
- Integrity
- Analytics

Presentation, application, and domain layers must be designable without a required online session.

## 5. Identity and Authority Without Accounts

Phase 0 does not introduce authentication.

Implications:

- the product is a single-device personal system at this stage
- domain identity is not a cloud user id
- no feature may be blocked because a login server is unreachable
- "signed-in" is not a synonym for "authorized to operate"

If multi-device or account features are ever authorized, they arrive as a later phase with an explicit Owner gate. They must not rewrite this constitution by implication.

## 6. Persistence Boundary

| Concern | Offline-first rule |
|---|---|
| Writes | Complete locally. Do not queue a network call as a precondition of success. |
| Reads | Served from local authoritative stores. |
| Failure | Disk, schema, or integrity failure is a local failure. Network unavailability is not a write failure. |
| Cache | The local database is not a cache of a remote system. It is the record. |
| Sync | Unauthorized in Phase 0. If introduced later, sync is a replica/transport problem, not a source of original journal truth. |

Room / SQLite is the intended local persistence technology. That choice does not authorize schema implementation in Phase 0.

## 7. Time While Offline

Capture time is taken from the device clock at the moment of capture.

The architecture must not require an NTP source, network time API, or cloud scheduler for:

- recording a journal event
- storing a user-intended civil time
- evaluating deterministic rules that were given explicit time inputs

Clock quality, clock rollback, and later reconciliation are integrity concerns. They do not justify an online dependency. See `TIME_MODEL.md` and `docs/adr/ADR-008-Time-and-Temporal-Modeling.md`.

## 8. Background Work and Notifications

WorkManager and AlarmManager are Android infrastructure. They may be used in later authorized phases for local reminders and local maintenance.

They must not:

- require connectivity to be considered successful
- move scheduling rules into infrastructure
- become a hidden second source of operational truth

Phase 0 does not implement workers or notifications.

## 9. Forbidden Online Assumptions

The following assumptions are out of constitution unless a later Owner directive authorizes them:

- the app cannot start without a network check
- capture is stored first on a server and then cached
- classification requires a remote model
- identifiers come from a server
- "offline mode" is a reduced subset of the product
- Firebase, auth, or analytics SDKs are part of the core runtime
- conflict resolution with a cloud replica is needed to record a journal event

## 10. Future Connectivity

If a later phase authorizes network features, the required shape is:

```
LOCAL AUTHORITATIVE RECORD
        ↓
OPTIONAL TRANSPORT
        ↓
REPLICA / BACKUP / MULTI-DEVICE
```

Never:

```
REMOTE SERVICE
        ↓
LOCAL CACHE
        ↓
DEGRADED OFFLINE MODE
```

Connectivity may add transport, backup, or collaboration. It may not take ownership of journal history.

## 11. Phase 0 Implication

Phase 0 creates no database, no app module, and no network client.

This document only binds later implementation:

- do not introduce online prerequisites in Phase 1 repository foundation
- do not place remote APIs under `domain/`
- do not treat missing network as missing product
