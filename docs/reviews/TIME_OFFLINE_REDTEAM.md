# RED TEAM — Time, Offline, and Dependency Risks

Reviewer: Lucas  
Mode: Phase 0 documentation audit  
Scope: time model, offline authority, dependency leakage through time/infrastructure

## Findings

### RT-T1  Hidden `now()` in domain rules
Severity: High if later phases ignore ADR-008  
Risk: Determinism collapses. Replay, tests, and overdue calculations diverge.  
Status after Phase 0 docs: Mitigated in specification by requiring EvaluationInstant as an explicit input. Not enforced by code because no code exists.

### RT-T2  Collapsing all times into one timestamp
Severity: High  
Risk: Journal capture time gets overwritten by due dates or reminder times. Historical truth is lost.  
Mitigation: TIME_MODEL.md separates CaptureInstant, EventTime, DueInstant, ScheduleInstant, TransitionInstant, EvaluationInstant. Schema is not created in Phase 0, so the risk returns the moment persistence is designed.

### RT-T3  "Tuesday at 8 AM" silently becomes a schedule
Severity: High  
Risk: Classification or a helper writes AlarmManager state during capture. Journal language is treated as execution. User intent may be wrong, timezone-wrong, or only a note.  
Mitigation: Temporal language is unresolved intent until an authorized interpretation step. Intelligence and UI are forbidden from scheduling as a side effect of capture.

### RT-T4  Device clock rollback reorders capture history
Severity: Medium  
Risk: A user or a faulty clock sets time backward. A new journal row appears older than an earlier one. Later analytics treat capture order as reality order.  
Mitigation named, not solved: CaptureInstant is device-local and not a proof. Integrity phase must not rewrite journal rows to "correct" order. Optional later monotonic sequence is outside Phase 0.

### RT-T5  Assuming Africa/Accra because examples use GH₵
Severity: Low / Medium  
Risk: Hard-coded zone makes civil-time interpretation wrong for travel or other locales.  
Mitigation: Zone is configuration. Examples do not freeze a zone.

### RT-T6  Future sync overwrites journal history
Severity: High if Phase 1+ introduces cloud casually  
Risk: A replica or "server time" becomes authoritative and rewrites captured text, CaptureInstant, or order.  
Mitigation: OFFLINE_FIRST.md states local record is system of record. Sync, if ever authorized, is transport. Journal text and capture identity are immutable history.

### RT-T7  AlarmManager / WorkManager as a second domain
Severity: Medium  
Risk: Infrastructure stores the only copy of "when to remind", and domain cannot explain or test it.  
Mitigation: Infrastructure may fire attention. Scheduling domain owns meaning. Phase 0 implements neither.

### RT-T8  Presentation computes overdue
Severity: Medium  
Risk: Compose screens or ViewModels implement "is overdue" with `Clock.System.now()`, violating no-UI-business-logic and determinism.  
Mitigation: ADR-005 and ADR-008. Overdue is a domain query given EvaluationInstant.

### RT-T9  Intelligence backdates or reschedules
Severity: High in later phases  
Risk: A model "fixes" a date by editing journal or due fields.  
Mitigation: ADR-006 path: recommendation → user or deterministic rule → domain operation. Intelligence has no write authority.

### RT-T10  Circular dependency via time ports
Severity: Medium if over-abstracted  
Risk: domain → timeport interface in infrastructure → infrastructure calling domain scheduling → cycle.  
Mitigation: Domain accepts time values, not a clock. Clock lives at application/infrastructure boundary. Phase 0 creates no ports, so do not invent a TimeProvider interface now.

### RT-T11  Offline treated as degraded mode
Severity: High if Phase 1 copies conventional app templates
Risk: Repository foundation adds a network-first SDK, analytics, or auth because "that is how Android apps start". Offline becomes a flag.  
Mitigation: OFFLINE_FIRST.md. Phase 1, when authorized, must not add online prerequisites.

### RT-T12  EvaluationInstant omitted from commands
Severity: Medium  
Risk: Commands later contain only business fields. Use cases stamp time internally and the command log cannot replay.  
Mitigation: When commands are designed, time-affecting commands should carry or be paired with the evaluation/capture instant used.

## Dependency direction risks related to this review

Allowed:

```
presentation → application → domain
data → domain
infrastructure → android platform
application may depend on domain and on small core abstractions
```

Forbidden through time/offline:

```
domain → Android Clock / AlarmManager / WorkManager / Room
presentation → scheduling rules / overdue policy / finance dates
intelligence → domain writes
data sync → journal rewrite
```

## Corrections applied in Lucas artifacts

- Named six distinct time concepts instead of one timestamp.
- Banned hidden `now()` in domain.
- Declared temporal language ≠ schedule.
- Declared device clock as capture source, not proof.
- Declared local persistence as system of record, not a cache.
- Deferred clock ports so Phase 0 does not grow speculative interfaces.

## Residual risk

Documentation cannot enforce these rules. The first later phase that adds a database or a reminder will reintroduce every finding unless the Owner gate inspects schema and use cases against this review.
