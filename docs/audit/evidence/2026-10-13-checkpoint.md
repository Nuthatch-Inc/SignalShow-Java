# SignalShow current checkpoint — October 13, 2026

Continuous Audit remains authorized. `check_usage: true`; last reading October 13:
weekly 87% remaining, no other applicable window, no usage pause. Follow the
[usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
Local commits at verified checkpoints authorized; do not push/release. No
subagents. Ordinary 2D workflows first; build hundreds of scenarios incrementally;
defer rare edges and tiny numerical errors.

Repository `/Users/julietfiss/src/SignalShow-Java`, branch
`codex/signalshow-usage-stabilization`, original baseline 28a4ffc. Local commits
through 79bd164 cover stabilization/imports, arithmetic/creation/size, complex
representation/filtering, and generator replacement. Nothing pushed.

Current batch fixes BUG-025 duplicated CSV/text suffixes and BUG-026 incorrect
shared scaling for negative complex exports. Seven production regressions:
five fail before, all pass after. Full package **491 tests**, zero failures,
errors or skips. Native eight-file retest passes exact pixels/data and filenames;
TIFF/PNG/BMP/JPEG and complex numeric export coverage is recorded separately.
See [export evidence](2026-10-13-exports.md). Next: bounded 2D demonstration
controls, then ordinary cancellation/overwrite dialogs.

Juliet asked whether typing works again: verified Constant amplitude 2.5, committed
expression, typed save filename and all 65,536 exported values exactly 2.5 + 0i.
Fresh-app input works; intermittent focus/routing is not proven resolved. Click
field, select existing text, type, read back before Return; stop any failed edit.
Swing dropdown values can be provisional: inspect then Return, verify model/sample.

No SignalShow process remains (verified). Bundle
`/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app` holds the 491-test JAR;
source JAR `target/signalshow-1.2.4.jar`. Copy/sign only while closed. Launch by
full path with CUA; at most one. Do not inspect AX after close (it relaunches).
Refresh CUA documentation after compaction. Isolated home
`/private/tmp/signalshow-roger-home` contains numeric exports; evidence stores
hashes/oracles. Native log `/private/tmp/signalshow-roger-native.log` includes
historical errors; use bounded slices. No build running. Keep existing audit
server session19418 and tab http://127.0.0.1:8092/docs/audit/ .

Limits: Windows untested; sample-only persistence; dense-memory capacity
unmeasured, obsolete 2 GiB cap removed; binary dimensions must match. Editor phase
precision: 3 decimals; intermittent automated input. Deferred Rectangle notation
BUG-021. No new product text or controls.
