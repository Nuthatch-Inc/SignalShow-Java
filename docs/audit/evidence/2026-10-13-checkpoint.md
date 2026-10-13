# SignalShow current checkpoint — October 13, 2026

Continuous Audit remains authorized. `check_usage: true`; last reading October 13:
weekly 84% remaining, no other applicable window, no usage pause. Follow the
[usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
Local commits at verified checkpoints authorized; do not push/release. No
subagents. Ordinary 2D workflows first; build hundreds of scenarios incrementally;
defer rare edges and tiny numerical errors.

Repository `/Users/julietfiss/src/SignalShow-Java`, branch
`codex/signalshow-usage-stabilization`, original baseline 28a4ffc. Local commits
through a7c9edf cover stabilization/imports, arithmetic/creation/size, complex
representation/filtering, and generator replacement. Nothing pushed.

Current batch fixes BUG-029 (Constant images retain a finite old extent after
resize) and BUG-030 (deleted thumbnail/closed save-dialog data ownership).
Four constant regressions failed before; two renderer lifetime checks pass.
Full package **503 tests**, zero failures/errors/skips. Native three-source +
threshold/multiply chains through 1024 square now match every exported sample;
uncentered 512×256 Constant and failed-save recovery also pass.
See [memory evidence](2026-10-13-memory.md). After deletion and collection, heap
fell 618.2→484.8 MiB; six models remain strongly reachable through native JNI
accessibility roots (BUG-031). This is an explicit macOS/CUA limitation, not a
proved general/manual/Windows leak. Read-only heap path parser and compact
ownership evidence are saved. Binary dumps remain only in /private/tmp:
signalshow-deleted-88988.hprof (before), signalshow-deleted-90566.hprof (after).
Both samplers completed. Next: independent ordinary demonstration numerics;
then native accessibility ownership/settings retention, within usage-first scope.

Juliet asked whether typing works again: verified Constant amplitude 2.5, committed
expression, typed save filename and all 65,536 exported values exactly 2.5 + 0i.
Fresh-app input works; intermittent focus/routing is not proven resolved. Click
field, select existing text, type, read back before Return; stop any failed edit.
Swing dropdown values can be provisional: inspect then Return, verify model/sample.

No SignalShow process remains (verified). Bundle
`/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app` holds the 503-test JAR;
source JAR `target/signalshow-1.2.4.jar`. Copy/sign only while closed. Launch by
full path with CUA; at most one. Do not inspect AX after close (it relaunches).
Refresh CUA documentation after compaction. Isolated home
`/private/tmp/signalshow-roger-home` contains numeric exports; evidence stores
hashes/oracles. Native log `/private/tmp/signalshow-roger-native.log` includes
historical errors; use bounded slices. No build running. Keep existing audit
server session19418 and tab http://127.0.0.1:8092/docs/audit/ .

Limits: Windows untested; sample-only persistence; bounded memory measured through 1024-square ordinary chains; full capacity
unmeasured, obsolete 2 GiB cap removed; binary dimensions must match. Editor phase
precision: 3 decimals; intermittent automated input. Direct replacement has no confirmation/atomicity guarantee. Deferred Rectangle notation
BUG-021. No new product text or controls.
