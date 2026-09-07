# Option E — Room migration certification

- Room version: 9
- Path: 1→2→3→4→5→6→7→8→9
- `exportSchema = true`
- Schema output: `app/schemas/` via KSP `room.schemaLocation`
- Destructive fallback: absent
- No `MIGRATION_9_10`
- Analytics / Intelligence: not persisted
- Search index: derived table created empty in 8→9; dropping it does not
  delete Capture/Journal rows
- Finding corrected additively: 8→9 now also creates
  `index_search_index_type_entityId` to match `SearchIndexEntity`
- Historical 1–8 schema JSON was never exported; instrumented tests seed
  v1 Capture/Journal SQL and apply the registered `Migration` objects
