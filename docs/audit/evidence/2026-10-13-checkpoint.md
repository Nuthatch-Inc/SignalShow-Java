# SignalShow current checkpoint — October 13, 2026

Continuous Audit remains authorized. `check_usage: true`; last reading October 13:
weekly 87% remaining, no other applicable window, no usage pause. Follow the
[usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
Local commits at verified checkpoints authorized; do not push/release. No
subagents. Ordinary 2D workflows first; build hundreds of scenarios incrementally;
defer rare edges and tiny numerical errors.

Repository `/Users/julietfiss/src/SignalShow-Java`, branch
`codex/signalshow-usage-stabilization`, original baseline 28a4ffc. Local commits
through ab1d790 cover stabilization/imports, arithmetic/creation/size, complex
representation/filtering, and generator replacement. Nothing pushed.

Current batch fixes BUG-027 shared animation timer cleanup after leaving or
finishing a demonstration. Four before-failing 1D/2D cases pass; full package
**495 tests**, zero failures/errors/skips. Native play→source switch removes the
queued animation timer (one before, zero after); replay works. One Convolve2D
view remains in both heap snapshots, so full reclamation is still open.
Three 2D demonstration control checks pass, with settings resets and separate
numerical verification explicitly open. See [demo evidence](2026-10-13-demos.md).
Prior export fixes BUG-025/026 are committed and verified. Next: ordinary file
cancellation/overwrite; then basic 1D regression and bounded memory measurement.

Juliet asked whether typing works again: verified Constant amplitude 2.5, committed
expression, typed save filename and all 65,536 exported values exactly 2.5 + 0i.
Fresh-app input works; intermittent focus/routing is not proven resolved. Click
field, select existing text, type, read back before Return; stop any failed edit.
Swing dropdown values can be provisional: inspect then Return, verify model/sample.

No SignalShow process remains (verified). Bundle
`/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app` holds the 495-test JAR;
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
