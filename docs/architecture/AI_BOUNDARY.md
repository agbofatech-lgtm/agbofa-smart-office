# AI Boundary

AI is not part of the deterministic core.

Allowed later, only as an advisory consumer:

- read OperationalOverview / IntegrityReport / RuleSetOutcome
- propose a recommendation
- never mint IDs or clocks inside domain evaluation
- never call save/transition/advance itself

Forbidden:

- silent mutation of canonical history
- stored model output as operational truth
- substituting latest classification for a pinned revision
- hidden clocks or nondeterministic ranking as authority

Any AI output that should change the system must re-enter through an explicit owning-domain use case supplied by a human or a separately authorized execution gate.
