# SignalShow stabilization checkpoint — October 12, 2026

Continuous Audit remains authorized; Juliet is away and explicitly asked to keep
working. `check_usage: true`; weekly 93% remaining, no other applicable window
reported, no usage pause. Follow the [usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
No commit, push or distribution authorized. Keep at most one test instance open,
close it when unused, verify exit. Standing usage priorities are in AGENTS.md and
JAVA_TEST_PLAN.md: ordinary workflows first, hundreds of distinct scenarios planned
incrementally, concentrate on Roger now; no rare-edge or tiny-error rabbit holes.

Baseline 28a4ffc; uncommitted fixes include Threshold outputs/bindings, binary
search index, removal of the old 2 GiB cap, earlier bounded image-scaler fix,
preview preferred sizes/scrolling, rotation sample count and startup foreground
activation. Last full package: 455 tests pass on macOS arm64/JDK 25. Windows untested.

Roger's nine-step native workflow now completes and exports a readable 256×256
TIFF. The AWT failure was caused by one extra sample after rotation, not demonstrated
memory exhaustion. See [detailed evidence](2026-10-12-roger-rotation.md), before/after
logs and saved output. Historical black/white pixel parity is not claimed because
Threshold settings were not supplied. Actual Java controls accept native typing
when the app holds keyboard focus; direct AX value writes remain unsupported.
Only launch-time foreground activation remains in app code; experimental global
focus listeners and all input-event logging were removed.

Completed BUG-011 (independent duplicate operation settings) and BUG-012 (compact
operation parameters retaining help and previews). Four ownership and two layout
regressions failed before and now pass. Native 2D duplicate cutoff edits, smaller
Threshold layout/scrolling/editing, and Rotate angle/interpolation all verified.
See [batch evidence](2026-10-12-copy-layout.md).

Completed BUG-013 (native default complex chirp save/load name parsing and
operation-result naming) and BUG-014 (New Unary/Binary/Term inserts current chain
as its own input). Thirteen before/after regressions; native source load, chain
insertion/completion, save/reopen/resave and independent all-sample comparison
pass. See [batch evidence](2026-10-12-complex-insertion.md). Shared input library
also excludes downstream consumers when returning to edit an earlier chain.
Native fixture was 256×256; AX combo selection did not commit 32×16 and must not
be reported as a native rectangular pass. Production rectangular/both-origin
and 1D save/load checks pass. Sample-only persistence is recorded as a capability
boundary; operation-result save dialog still needs an explicit base filename.

Completed BUG-006: the native circle + Gaussian scenario corrupted the plus icon,
other term values/widths and thumbnails through shared editor events. Active term
broadcasters now isolate ownership; binding publishes once complete; operator
slots are guarded. Polar resizing preserves entered width. Five focused checks
pass (four failed before), native edits and Plot/Edit preserve the expression,
and all 65,536 saved samples agree with independent math. See
[batch evidence](2026-10-12-two-term.md).

Completed BUG-015: native 128×64 Profiles threw an array-index exception and
left Histogram visible. Corrected swapped origin dimensions and profile labels;
eight fixtures pass (four failed before), native four-channel profiles and two
exported cuts agree with the source exactly. SineChirp's observed cosine label
is corrected to sine. Source edits survive four-view visits and saved samples
match independent math. See [batch evidence](2026-10-12-profiles.md).

Completed BUG-016: Histogram now fits its zero baseline, decimal labels and
complete last bin; nonnegative imaginary channels remain visible. Three focused
checks pass (two failed before), and native 30/40-bin layouts and typing were
verified. See [batch evidence](2026-10-12-histogram.md).

Completed native non-centered 128×64 save/load/resave and Threshold/Times at
cutoffs 0.5/0.25; every saved sample matches independently. A first session lost
automated typing focus, so only the committed default was counted; the fresh
session passed field and filename readback. See [evidence](2026-10-12-corner-persistence.md).
No new app changes; intermittent native keyboard focus remains unconfirmed.

Completed BUG-017: linked result caches no longer remain dirty after source
edits. Two focused regressions fail before/pass after; native A×B → Real edits
and operand replacement match every saved sample exactly. [Evidence](2026-10-12-linked-sources.md).

Completed BUG-018: reopened operation picker now appears immediately and allows
replacement. Two before/after component checks and native replacement/Plot/save
pass. [Evidence](2026-10-12-operation-reopen.md).

Next active check: resize a source feeding an operation, verify the displayed dimensions and saved result.
The native size choice did not commit in the resize attempt (both dimensions
remained 256); that scenario stays pending. No resize bug or pass is claimed.
Native session closed. Keep ordinary usage priority.
Native combo selection works via click → Up/Down → Return; direct AX row selection
alone does not commit. Rectangular 128×64 save/reload is now verified; supersedes
the earlier native rectangular gap only for that fixture, not both origins.

No SignalShow process remains after verified close. Reusable native app bundle:
`/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app`; log-only AuditLauncher,
current 455-test JAR; isolated home `/private/tmp/signalshow-roger-home`; native log
`/private/tmp/signalshow-roger-native.log`. Rebuild/copy/sign only with old process
closed, then launch through CUA. Refresh CUA documentation after compaction.
Do not read AX after closing: it can relaunch the app; verify process exit instead.
Typing helper: click field, getAXState, super+a, typeText, Return, readback. Normal
native titlebar resizing now works while foreground. Last test dimensions were
3018×1778 screenshot pixels; opening again may retain this size.

Matrix preview remains http://127.0.0.1:8092/docs/audit/ (server session 19418),
with the user's existing in-app tab. Keep the preview. Regenerate/check matrix
and record each verified batch. No build is running.

Automation: require typed filename readback before confirming a save. CUA AX can
show hidden CardLayout controls; use screenshots to identify visible input libraries.

Native parameter helper now resolves each stepper by its description and verifies
its value after every edit; old AX indices can shift when equations change.

Completed BUG-019: independent copied definitions retain predefined complex type
and open existing controls. Eight before/after checks and native complex-chirp
copy/edit/save pass; all 65,536 complex samples confirm independence. Full package
455 tests passes; diff check clean. [Evidence](2026-10-12-duplicate-images.md).
Native app closed and process exit verified. The delete-linked scenario is checked; resize is active. No build is running.

Native ordinary source/result/final-document deletion passes with independently
checked samples and subsequent creation. No code change. [Evidence](2026-10-12-delete-linked.md).
App closed/exited; next is native linked-source resize.

Completed BUG-020: resize mismatch crashes/stale outputs corrected. Six focused
checks and full 455-test package pass. Native shared-source resizing, mismatch,
view/save safeguards and compatible operand-replacement recovery verified; all
8,192 complex samples match exactly. [Evidence](2026-10-12-resize-linked.md).
Direct native size restoration remains pending because key changes did not
commit. Do not repeat that ineffective approach. Preserve the documented lack
of automatic alignment and explanatory mismatch UI. No unapproved text added.
Next active check: basic Cartesian function composition; native app closed/exited.
Full/native logs and screenshots are recorded. No build running.

Additional native Cartesian x/y composition, independent term edit, plus-symbol
rendering, Plot/Edit and Threshold pass; all 65,536 samples agree independently.
[Evidence](2026-10-12-cartesian-composition.md). No source change. BUG-021 rectangle
text uses half-width in its formula; defer the notation fix behind ordinary usage.
Next: actual calculator grouping/removal with constants, then a spatial component.
App closed/exited; keyboard-dependent native search remains pending.
