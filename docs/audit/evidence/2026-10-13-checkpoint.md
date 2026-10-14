# SignalShow checkpoint — October 13, 2026

Continuous Audit remains active: ordinary Mac 2D creation and operation chains,
crashes and major wrong results first. Windows later. Juliet authorized local
commits; no push/release. No agents, new chats, automations or resets authorized.
At most one native app; close when unused and verify exit. Use CUA for all native
actions. Automated typing works; target, type, verify readback, then submit.

`check_usage: true`; latest weekly 72% remaining, no other applicable window.
Follow [usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
This checkpoint is not a pause. Continue the active Mac scenario.

## Verified checkpoint

Repository `/Users/julietfiss/src/SignalShow-Java`, branch
`codex/signalshow-usage-stabilization`, baseline 28a4ffc. Recent local commits:

- 4f9aed3: BUG-041, loaded/exported sampled 2D images opened empty editors.
  Six before-failing EDT cases; native directional/reuse chain19 exports passed.
- f0b34ad: Gaussian/Uniform noise→Subtract Mean→Squared Magnitude,12 exports.
- 2473080: BUG-042, corner-origin polar angle rows disagreed with radius.
  Five before-failing cases; native polar Divide/Minus retest11 exports passed.
- 5b20465: magnitude/phase→Phase/Real/Threshold branches,12 exports passed.
- eede73c: signed Cartesian source→Absolute Value→Reverse X/Y,10 exports passed.

Latest local fix f6e1192: BUG-043 complex powers lost input phase. Fourteen
before-failing production cases pass; focused56/full606 tests pass. Native power
and reciprocal retest9 exports passed; all seven wrong before outputs retained.

Local evidence commits63b420c/48061bc/fbbd0cf/9b968bf: annular diffraction,
Fourier basis edits, spatial autocorrelation/scaling, multi-term grouping/copy
independence, all passed. Latest batch:256×256 real Sine/Hamming and imaginary
Rectangle/Sine→Signum→Offset→Magnitude; window/phase and complex offset edits.
All10 exports/655,360 samples pass independent full-array formulas, including true
outside-window zeros. Max3.414e−15/tol1e−9; clean log. Evidence:windowed.md/replay.

Matrix:130 math entries,77 scenarios:72 checked,1 active Mac,2 pending Windows,
2 deferred memory stress. These are bounded checks, not whole-app certification.

## Next action and resources

Active `gui-2d-resize-transform-chain`: create ordinary128×64 complex Cartesian
source, suggested real Gaussian A1.3,center(−8,5),widths(18,14); imaginary Rectangle
A.4,center(10,−9),full widths(16,12). Create FFT→inverseFFT→Magnitude with sqrt(N)
normalization. Save all stages; resize source256×128, then64×256, and inspect/save
all stages after each change. Verify actual controls/descriptors and analytic source
parameters, dimensions and transforms. No unusual sizes or capacity extremes.

No SignalShow app remains open; CUA Close plus process inspection confirmed exit.
No build/sampler running. Test bundle:
`/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app`,606-test JAR SHA256
`35a110618d7268ee02a2e4290f0b57a2b8e616c0fbf6e66084c681b952a8bee1`.
Copy/sign only while closed. Launch through CUA; never getAX after closing unless
intentionally relaunching. Refresh CUA documentation after compaction.

Persistent helpers: ssClick,ssNew2D(exact menu label),ssImageParam,ssDocumentSize,
ssSaveVerified(base without.txt),ssDemoRow(label,index),ssOperationParam,
ssFileMenu,ssFs. Always fresh AX after actions. New operation:2D→New2D Mathematical
System, then New Unary/Binary Operation and explicitly choose the operation.
Revisit its tile before editing parameters. Source and input-picker lists have
similar AX rows; main document rows are depth4. AX can briefly return incomplete
lists; refresh before retry. Coordinate input pickers use screenshot grounding.

Isolated home `/private/tmp/signalshow-roger-home`; append native log
`/private/tmp/signalshow-roger-native.log`. Keep bounded launch slices beginning
at the last Loaded configuration. Full current build log
`/private/tmp/signalshow-power-package.log` is also committed evidence.
Audit viewer `http://127.0.0.1:8092/docs/audit/` (prior session19418); preserve server.
Diagnostic heap `/private/tmp/signalshow-preview-memory-resumed-deleted.hprof`
stays uncommitted. Test exports are disposable; no user documents overwritten.

## Limits and conventions

Windows untested; sample-only persistence; dense-memory workloads; picker4096 is
not capacity certification; binary dimensions must match; phase entry3decimals;
save replacement not atomic. Obsolete2GiB heap cap removed. Deferred Rectangle
notation BUG-021 and Translate help omission. No new control/final-copy changes.
Threshold is componentwise: threshold0 maps imaginary0 to1; final Real discards it.
Reverse preserves sample index0 and reverses cyclic order; coordinate-reflection
semantics not approved. Polar angle at radius0 keeps the existing90° convention.
Custom-equation source rename has not been verified. Do not reopen these low
priority convention questions at the expense of ordinary broken workflows.
