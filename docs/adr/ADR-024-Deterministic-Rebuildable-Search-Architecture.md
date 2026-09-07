# ADR-024 — Deterministic Rebuildable Search Architecture

- Status: Accepted
- Date: 2026-09-07
- Phase: 17

## Decision

Search is a derived, rebuildable, non-canonical index.

- Identity = SearchType + ":" + entityId
- Normalization = trim + Locale.ROOT lower-case + collapse whitespace
- Matching = normalized substring
- Ordering = createdAt DESC, type ASC, entityId ASC
- Rebuild = explicit + transactional replace of search_index
- Room version 9 with MIGRATION_8_9
- No hidden clock or UUID
- No AI, FTS claim, relevance score, background indexer

Deleting search_index does not destroy canonical facts.

ADR-023 is not rewritten.
