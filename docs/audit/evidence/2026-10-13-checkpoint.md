# SignalShow current checkpoint — October 13, 2026

Continuous Audit remains authorized. Juliet unlocked the Mac, but native
verification is **blocked by automated text input**: clicks respond, while typing
and paste leave fields unchanged, including after a refreshed CUA connection.
A manual typing comparison is pending. See [input evidence](2026-10-13-input-after-unlock.md).
This is not a usage pause. `check_usage: true`; last reading October 13:
weekly 81% remaining, no other applicable window, no usage pause. Follow the
[usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
Local commits at verified checkpoints authorized; do not push/release. No
subagents. Ordinary creation, edits and operation chains first. Build hundreds
of usage scenarios incrementally; defer rare edges and tiny numerical errors.

Repository `/Users/julietfiss/src/SignalShow-Java`, branch
`codex/signalshow-usage-stabilization`, original baseline 28a4ffc. Local commit
0174857 releases retired document ownership; 8021b9f releases image preview
buffers. Nothing pushed.

Full offline package: **549 tests, zero failures/errors/skips**. Retired 1D/2D
model ownership and Constant/Cylinder editor replacement/reopening regressions
pass. Prior matched native run: deleted document models three→zero, collected
heap 142.6→79.5 MiB. Latest code also clears retired 2D preview pixel arrays and
calculator thumbnails. The repeated native 512-square create/multiply/edit/reopen
sequence preserves every saved sample; collected live heap 134.2 MiB.
**Latest post-deletion heap result is unverified:** macOS locked during the next
CUA action batch, before an empty-list snapshot was produced. Do not conflate the
latest live measurement with post-deletion memory. BUG-031 remains open. See
[preview memory evidence](2026-10-13-preview-memory.md) and
[prior matched ownership evidence](2026-10-13-editor-memory.md).

Next action: resolve the text-input check, then repeat the short
Constant×Cylinder workflow in the linked evidence, including deletion and
collected histogram/heap/root paths. Only then assess remaining image-array
retention. Do not replay other completed workflows. Existing next queue remains
demo settings, sampling and band filtering, with normal usage first.

Matrix: 130 registered math entries; 60 scenarios (51 checked, 1 scope, 4 pending,
2 issues, 2 deferred). Earlier 1024 memory evidence remains a different workload.
Local current dumps: /private/tmp/signalshow-editor-memory-before.hprof and
/private/tmp/signalshow-editor-memory-after.hprof. Read-only array-root diagnostic
and compact paths are in the current evidence. Older dumps remain in /private/tmp.
One audit app (PID 98337) is open for Juliet's manual typing comparison; no sampler runs.

Earlier automated typing checks passed, but the latest post-unlock attempt does
not: `f0` stays unchanged and Amplitude stays `1` after typing/paste requests.
Do not send Return without readback. The current disposable document is a default
256-square Constant in the Image editor, with amplitude 1. Juliet has been asked
to type 3 manually. Keep at most one app and close it when no longer needed.

Bundle `/private/tmp/signalshow-roger-app/SignalShowRogerAudit.app` holds the final
549-test JAR; source JAR `target/signalshow-1.2.4.jar`. Copy/sign only while closed.
Launch by full path using CUA. Do not inspect AX after close (it relaunches).
CUA was reset during diagnosis: only signalshowApp, ssFs and ssState are bound.
Prior native helper definitions were saved at /private/tmp/signalshow-cua-helpers.js.
Refresh CUA documentation after compaction. Isolated home
`/private/tmp/signalshow-roger-home` contains native exports; demonstration evidence
includes small source/result files and a standalone verifier. Native append-only
log `/private/tmp/signalshow-roger-native.log` contains historical errors: use
bounded launch slices. No build running. Reuse audit server session 19418 and tab
http://127.0.0.1:8092/docs/audit/ .

Limits: Windows untested; sample-only persistence; bounded memory measured, not
capacity certified; dimension picker 4096; binary input dimensions must match;
phase entry 3 decimals; direct replacement has no confirmation/atomicity guarantee.
Deferred Rectangle notation BUG-021. No new controls or product wording. Complex
normalization semantics unchanged; filtering Band Pass/Band Reject and Sampling/Aliasing still unverified.

Source note for future sampling work: FunctionFactory.createFunction1D(term,
dimension,centered,name) ignores the supplied origin/name; Sampling1D still uses
that overload and an ordinary-coordinate filter. Verify the normal reconstruction
workflow before changing it. FrequencyDemoFilter now handles the filtering demos
separately with signed frequency coordinates. No build/sampler is running. One audit app remains for the manual input check.
The existing audit HTTP server remains available;
no automation or background native test will resume on its own.
