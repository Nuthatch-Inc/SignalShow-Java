# SignalShow checkpoint — October 13, 2026

Continuous Audit is active at Juliet's request: ordinary Mac2D creation and
operation chains, crashes and major wrong results first. Windows later. Commit
verified batches locally; no push/release. No agents, new chats, automations or
resets authorized. At most one native app, closed when unused with exit verified.
Target/readback native typing works after the earlier restart; cause of that
interruption remains unknown. CUA only for native actions.

`check_usage: true`; latest weekly74% remaining, no other applicable window.
Follow [usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
This checkpoint is not a pause; continue with the active Mac scenario.

## Verified checkpoint

Repo /Users/julietfiss/src/SignalShow-Java, branch codex/signalshow-usage-stabilization,
baseline28a4ffc. Recent local commits df13c3a (chirp/aperture/conjugate/intensity,
10 exports),46f05e4 (offset/clip/square-root and duplicate isolation,11 exports).
Current batch fixes BUG-041: populated2D FunctionFactory results had newState=true,
so first loaded/exported editors were empty. Mark existing data appropriately;
use normal toolbar content attachment. Six first-opening EDT cases failed before,
now pass. Retain the imported-image test fixture's weakly registered listener.
Full offline package584 tests pass, zero failures/errors/skips; focused23 pass.

Native retest: saved64×128 Triangle×Gaussian reload and amplitude edits; X/Y
Derivative→Integral; Export Result as Data; first real/imaginary editors; independent
exported gain and Negate; later source changes update live chain but not copy.
All19 exports/155,648 complex samples independently pass, max8.882e−16/tol1e−9.
Native logs clean. Evidence and replay:2026-10-13-directional-reuse.md and
2026-10-13-direction-verifier.py. Committed locally as4f9aed3. Code/tests, logs, screenshots, raw compressed
exports and JSON/report retained together. Subsequent complex Gaussian/Uniform
noise→Subtract Mean→Squared Magnitude passed12 exports/98,304 samples, including
mean/spread/seed edits and Plot/Histogram stability. Clean native log; unchanged
584-test package. See2026-10-13-noise.md and its replay script.

Latest batch BUG-042: corner-origin polar angular rows disagreed with radius.
Fixed Indices.createAngleIndicesZeroStart;8 production tests,5 before-failing,
171 focused/592 full pass. Native128×64 Gaussian×angular Cosine with Divide/Minus,
edits/replacement and both origins:9 valid before+11 after exports/163,840 samples
pass;3 wrong before outputs retained separately. Logs clean. Current fix ready
for local commit with2026-10-13-polar.md, replay, raw data and screenshots.

Matrix130 math entries,69 scenarios:64 checked,1 active Mac check,2 pending Windows,
2 deferred memory stress. Bounded assurance only. Previous documented rotation,
filtering, sampling, persistence and input fixes remain in their dated records.

## Next action and resources

Active: gui-2d-phase-extraction-chain. Create a rectangular magnitude/phase image
with varying phase; exercise Phase/Real/Threshold, edits and independent saved
intermediates. Inspect local conventions before interpreting phase wrapping. Do not study tiny statistical errors,
capacity extremes or long endurance. Continue Mac batches; Windows does not block.

No SignalShow app remains open; CUA Close and process inspection confirmed exit.
No build/sampler running. Native bundle:
/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app, verified592-test JAR
SHA256 0d3519910dbf0ac841a1826fec5b4fdd402d3838b831c9191e396037a9328dc1.
Copy/sign only while closed. Launch/query via CUA. Never query AX after closing
unless deliberately launching. Refresh CUA docs after compaction.

CUA helpers: ssClick,ssNew2D(exact menu label),ssImageParam,ssDocumentSize,
ssSaveVerified(base without .txt),ssDemoRow(label,index),ssOperationParam,
ssFileMenu,ssFs. New operation: 2D→New 2D Mathematical System, then New Unary
Operation and choose operator. Revisit operation tile before editing parameters.
Fresh AX after actions; stop ineffective keyboard attempts. File chooser invisible
AX fields may need screenshot-grounded clicks. Custom-equation rename unverified.

Isolated home/private/tmp/signalshow-roger-home; append log/private/tmp/signalshow-roger-native.log.
Save bounded launch slices from last Loaded configuration. Build log:
/private/tmp/signalshow-polar-package.log (also committed evidence).
Audit viewer http://127.0.0.1:8092/docs/audit/ (prior session19418), preserve server.
Diagnostic heap/private/tmp/signalshow-preview-memory-resumed-deleted.hprof stays
uncommitted. Test exports are disposable; no user documents overwritten.

Limits: Windows untested; sample-only persistence; dense-memory workloads;
picker4096 is not capacity certification; matching binary dimensions; phase
entry3decimals; nonatomic save replacement. Obsolete2GiB launcher cap removed.
Deferred Rectangle notation BUG-021 and Translate help omission. No new control
or final wording changes authorized.
