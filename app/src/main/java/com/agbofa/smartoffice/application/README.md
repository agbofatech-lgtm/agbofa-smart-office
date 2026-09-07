
# application

Use cases, commands, and queries.

Phase 3: `CaptureExpressionUseCase` accepts an expression and persists
an immutable capture through `CaptureRepository`.

Application may depend on domain. It must not depend on Compose or Room
entities. It must not interpret captured text.
