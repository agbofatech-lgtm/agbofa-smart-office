# Phase 13 — Core Hardening Notes

Hardening only. No new product capability.

## Changes

- Added `MIGRATION_1_2` creating the `classifications` table.
- Registered `1→2` through `6→7`.
- Removed `fallbackToDestructiveMigration()`. Missing paths now fail closed.
- `CreateWorkflowUseCase` persists through `WorkflowRepository.saveWithSteps`.
- Room implementation uses `database.runInTransaction`.
- In-memory implementation rolls back workflow and partial steps.
- `versionName` = `0.13.0-phase13`.

## Intentionally unchanged

- `allowMainThreadQueries()` remains. Current use cases are synchronous and invoked from the UI thread. Removing this requires an async rewrite, which would change application shape beyond hardening.
- No semantic change to Capture, Journal, Classification, Operations, State, Temporal, Dependency, Workflow, Rules, Integrity, or Projection.
- Android SDK was not present. Compile and test execution were not performed.

## Verification labels

| Claim | Status |
|---|---|
| SOURCE PRESENT | YES |
| COMPILED | NOT AVAILABLE |
| EXECUTED | NO |
| PASSED | NOT RUN |
| RUNTIME VERIFIED | NO |
| MIGRATION SOURCE | 1→7 present |
| MIGRATION EXECUTED | NO |
