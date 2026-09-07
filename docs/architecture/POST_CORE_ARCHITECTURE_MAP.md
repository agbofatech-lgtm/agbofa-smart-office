# Post-Core Architecture Map

```
CANONICAL CORE
  Capture → Journal → Classification → OperationalRecord
       → State history
       → Temporal history
       → Dependency graph
       → Workflow history
       → Rule versions
       → Integrity evaluator (read-only)
        ↓
PROJECTION (Phase 12, ephemeral)
  OperationalOverview
        ↓
ANALYTICS (not implemented)
  counts / distributions derived from projections
        ↓
INTELLIGENCE (not implemented)
  interpretation of derived facts
        ↓
RECOMMENDATION
  Phase 10 RuleDecision already exists
        ↓
AUTHORIZED ACTION
  existing owning-domain use cases only
```

No implemented layer after Projection may persist derived fields as authority.
