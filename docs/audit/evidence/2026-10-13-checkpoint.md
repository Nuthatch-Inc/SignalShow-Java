# SignalShow checkpoint — October 13, 2026

Current planned Mac baseline complete. Next queued acceptance checks require a
Windows environment and confirmation of Roger's actual OS/runtime/version.
No native test or build is running. This is a scoped checkpoint, not a claim that
all possible ordinary function/operation combinations have been tested. Continue
the incremental suite plan as further batches are selected; do not manufacture
edge cases or numerical trivia to expand it.

Juliet authorized Continuous Audit and local commits, with no push/release.
She resumed after Zoom and requested restart when manual/automated typing failed.
Restart restored typing; cause remains unknown. Native input has worked throughout
the subsequent batches. No agents, new chats, automations or resets authorized.
At most one SignalShow instance, closed when unused; verify exit.

`check_usage: true`; latest weekly77% remaining, no other applicable window.
Follow [usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
This checkpoint is not a usage-triggered pause.

## Current result

Repo /Users/julietfiss/src/SignalShow-Java, branch codex/signalshow-usage-stabilization,
baseline28a4ffc. Current code fixes are committed, with tracking-only evidence
commits afterward. No push. Relevant recent commits:

- 8334dd9: sampling reconstruction, default gain and retained settings.
- 6677b3c: native spatial Convolve/Correlate and forward/inverse FFT chains.
- 1a4b7df: BUG-040, ordinary rectangular90° rotation failed with a JAI Crop error.
  Preserve the input canvas and zero-fill outside the rotated overlap.
- 1e74162: native fill/wrap translation, complex scaling and addition chains.

Full package578 tests passed, zero failures/errors/skips. Focused rotation22
passed, including three before-failing ordinary rectangles and independent
both-origin/channel geometry plus editable Magnitude/Threshold chains. Native
rotation13 exports/106,496 complex samples verified; translation12 exports/98,304
verified. Both native retest logs clean. Spatial filtering12 exports/98,304 passed;
that earlier run had six nonblocking JDK AccessibleHTML exceptions, recorded.

Persistence capability scoping is now reconciled: files retain samples, dimensions,
origin and name, not analytic definitions or linked graphs. Existing1D/2D native
and production round trips reused; no unnecessary rebuild for documentation.
The authoritative matrix has130 math entries and63 scenarios:59 checked,
2 pending Windows checks,2 deferred memory stress checks. All assurance is scoped.

## Next action and resources

Next queue: launch-native-windows, windows-files-display. Requires Windows; Mac
checks cannot satisfy them. Confirm Roger's actual environment before choosing
packaging/runtime assumptions. No destructive memory stress or large-capacity
claim. The plan remains hundreds of meaningful usage scenarios incrementally,
prioritizing ordinary crashes and major wrong results over exotic inputs.

No app remains open; last CUA Close followed by process inspection confirmed exit.
No build/sampler running. Test bundle /private/tmp/signalshow-roger-app/SignalShowRogerAudit.app
contains verified578-test JAR, SHA256
c92a82fbaa49f34640451e83568997a6b2c156c7f6cc82806f5b5d7b69bb6d88.
Copy/sign only while closed; launch via CUA. Never query AX after closing, which
relaunches it. Refresh CUA documentation after compaction. Helpers ssClick,ssNew2D,
ssRenameCurrent,ssImageParam,ssDocumentSize,ssSaveVerified,ssDemoRow,
ssOperationParam(index,value),ssFileMenu,ssFs. ssDemoRow tolerates both AX
Description/Value orderings. Read back typed fields before Return. ssSaveVerified
takes a base name without .txt. Select an operation tile after revisiting its
system before editing its controls. Do not reuse prior evidence-record helpers.

Isolated home /private/tmp/signalshow-roger-home; append-only native log
/private/tmp/signalshow-roger-native.log. Save bounded launch slices from the last
Loaded configuration. All completed evidence is in docs/audit/evidence and linked
from signalshow-coverage.json. Full build log/private/tmp/signalshow-rotation-package.log.
Audit server previously session19418; preserve/reuse http://127.0.0.1:8092/docs/audit/.
Diagnostic heap/private/tmp/signalshow-preview-memory-resumed-deleted.hprof is never
committed. Existing saved native input/output files in isolated home are disposable
but useful for replay. No user documents were overwritten.

Limits: Windows untested; sample-only persistence; dense-memory workloads;
picker4096 is not capacity certification; binary dimensions must match; phase
entry3decimals; save replacement not atomic/confirmed. Obsolete2GiB launcher cap
removed. Deferred Rectangle notation BUG-021 and Translate help omission of
unchecked zero-fill behavior. No new control/wording changes authorized.
