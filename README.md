# AGBOFA SMART OFFICE

Deterministic, offline-first Personal Operations System  
Owner: AGBOFA Technologies

**Current mode: PHASE 1 — Native Repository Foundation**

This repository is a compilable Android shell. It is not the product.

SPECIFIED ≠ IMPLEMENTED  
CREATED ≠ COMPILED  
COMPILED ≠ TESTED

## What exists

- Single `app` module
- Package `com.agbofa.smartoffice`
- Jetpack Compose + Material 3 foundation screen
- Empty architecture packages (`domain`, `application`, `data`, `infrastructure`, `core`)
- Phase 0 constitution under `docs/`

## What does not exist

Journal, capture, tasks, finance, calendar, workflows, AI, notifications,
workers, Room schema, authentication, cloud, Firebase.

## Build on a real Android machine

This tree was authored in an environment **without** the Android SDK or Gradle.
Before the first build:

1. Install Android Studio / SDK with compile SDK 37 and JDK 17+.
2. Generate the Gradle wrapper JAR (not stored here):

```bash
gradle wrapper --gradle-version 9.6.0
```

3. Then:

```bash
./gradlew test
./gradlew assembleDebug
```

Expected versions:

| Tool | Version |
|---|---|
| AGP | 9.4.0 |
| Gradle | 9.6.0 |
| Kotlin | 2.4.10 |
| Compose BOM | 2026.08.00 |
| compileSdk | 37 |
| targetSdk | 36 |
| minSdk | 26 |

Hilt, Room, WorkManager, Navigation-Compose, Retrofit, and Firebase are
intentionally absent.

## Architecture

See `docs/ARCHITECTURE.md` and `docs/adr/`.

Dependency direction:

```
presentation → application → domain
data → domain
infrastructure → Android platform
```

Domain must remain free of Android types.

## Phase status

| Phase | State |
|---|---|
| 0 Constitution & Architecture | ACCEPTED |
| 1 Native Repository Foundation | IN PROGRESS / created here |
| 2+ | CLOSED |
