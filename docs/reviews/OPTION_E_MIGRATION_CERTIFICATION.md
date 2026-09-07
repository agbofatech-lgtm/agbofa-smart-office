# Option E — Database migration certification

- Status: source configured
- Date: 2026-09-07
- Room version: 9
- Path: 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9
- New migration: NONE

## Authorized work

- `exportSchema = true`
- KSP `room.schemaLocation` = `app/schemas`
- `androidx.room:room-testing` for androidTest
- `RoomMigrationCertificationTest` using `MigrationTestHelper`
- Inventory assertions: version 9, export on, no analytics/intelligence entities

## Not done by this change

- Schema JSON is not hand-written.
- `9.json` appears only after a successful Room/KSP compile.
- Historical `1.json`…`8.json` were never exported at those versions.
- Tests that need a missing schema **skip**; they do not pass.

## Runtime

Android SDK was unset when this document was written.
`SOURCE PRESENT ≠ RUNTIME VERIFIED`.
