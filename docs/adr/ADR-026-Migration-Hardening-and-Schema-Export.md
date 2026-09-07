# ADR-026 — Migration Hardening and Schema Export

- Status: Accepted
- Date: 2026-09-07
- Option: E

Room stays at version 9. Path 1 through 9 is explicit.
exportSchema=true. KSP schemaLocation=app/schemas.
MigrationTestHelper androidTest source added.
No MIGRATION_9_10. No destructive fallback.
Search index remains derived. Analytics and Intelligence remain ephemeral.
Schema JSON is Room-generated only. SOURCE PRESENT is not RUNTIME VERIFIED.
