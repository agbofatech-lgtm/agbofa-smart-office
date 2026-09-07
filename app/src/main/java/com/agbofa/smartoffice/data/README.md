# data

Local persistence adapters and repository implementations.

Phase 4:

- `InMemoryCaptureRepository` / `InMemoryJournalRepository` for unit tests
- Room `captures` and `journal_entries` for production

No tables for tasks, finance, classification, or workflows.
Domain models are mapped to entities here. Room types do not leave data/.
