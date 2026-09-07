# application

Use cases, commands, and queries.

Phase 4:

- `CaptureExpressionUseCase`
- `AdmitCaptureToJournalUseCase`
- `GetJournalTimelineUseCase`
- `JournalRecord` read projection

Application may depend on domain. It must not depend on Room entities
or Compose. It must not interpret captured text.
