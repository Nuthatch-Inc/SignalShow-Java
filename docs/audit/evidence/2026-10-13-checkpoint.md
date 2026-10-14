# SignalShow checkpoint — October 13, 2026

The Mac usage round and local release preparation are complete. Juliet explicitly
authorized merging into main and publishing1.2.5 as the latest GitHub release.
That supersedes the earlier no-push instruction. Verify finished Mac/Windows
installer contents before publication. No GitHub website action is needed unless
a permissions/build gate requires it. Windows native usage testing is deferred.

`check_usage: true`; latest weekly71% remaining, no other applicable window.
Follow the Continuous Audit usage policy. No agents, new chats, automations or
resets. Use CUA for native input; keep at most one instance and close when unused.
Native input works when the Java window is focused. Stop if Juliet uses the desktop.

## Verified state and local files

Repository `/Users/julietfiss/src/SignalShow-Java`, branch
`codex/signalshow-usage-stabilization`, baseline28a4ffc. This checkpoint is included
in the1.2.5 local release commit; obtain its hash with git log rather than relying
on the previous e0ef990 checkpoint. No push or tag was performed.

Latest production fix: BUG-044 in signals/io/Utils.java. Dot-containing directory
names redirected saves and caused two distinct filenames to overwrite one external
path. Four before-failing regressions now pass;18 related focused checks and full
clean610-test package pass. The packaged Mac app successfully creates a Gaussian,
edits amplitude, creates Squared Magnitude and saves two separate outputs in the
requested dotted folder. All131,072 complex samples pass independent references;
the old unintended file is unchanged. Evidence:2026-10-13-release.md/replay.

The final ordinary resize batch also passed: complex Gaussian/Rectangle
128×64→256×128→64×256 through existing FFT→inverseFFT→Magnitude;12 exports,
229,376 samples; clean log. Evidence:2026-10-13-resize.md/replay.

Matrix:130 math entries,78 scenarios:74 checked, two pending Windows, two deferred
memory checks. These are bounded checks, not whole-app certification. Previous
complex-power, polar-angle, editor, sampling, rotation, save and memory fixes are
included, along with their existing evidence.

Local downloads in `dist/1.2.5/`:
- signalshow-1.2.5.jar: Java25+ required; includes JAI.
- SignalShow-1.2.5-portable.zip: same JAR, instructions and release notes.
- SignalShow-1.2.5.dmg: Apple Silicon Mac, Java bundled, ad-hoc signed/not notarized.
- SHA256SUMS.txt: checksums; artifact manifest also committed in release evidence.

Final JAR SHA256:8e262633bb114038c47dc76a8625c5c28efa25568e6f870443904e38bf02f795.
Packaged JAR matches exactly; codesign deep verification, hdiutil verification and
ZIP integrity pass. Native image copies differed only in identifier/user.home to
isolate configuration. The DMG itself was not installed into Applications.

Both native installer scripts requireJava25 and stage only the JAR. Windows script
syntax checked, not executed. The existing GitHub tag workflow builds native Mac
and Windows installers and creates a draft release; it has not run for1.2.5.
Release notes:docs/releases/1.2.5.md. Do not mark Windows accepted after a CI build.

## Resources and next action

Both audit and packaged test apps were closed through CUA; process inspection
confirmed none remain. No build/sampler is running. Old CUA helper closures retain
old app bindings: never reuse them for a new app or call getAX after closing, since
that can relaunch the old bundle. Use fresh helpers with an explicitly bound app.

Audit viewer http://127.0.0.1:8092/docs/audit/ remains available (prior session19418).
Native fixtures and replay scripts are committed under docs/audit/evidence.
Temporary homes /private/tmp/signalshow-roger-home and
/private/tmp/signalshow-1.2.5-home contain only test data. Diagnostic heaps and
packaged test copies stay uncommitted. Preserve unrelated browser/server state.

Main is merged/pushed at8f6dad1. Initial run38502634539 failed before publishing:
Windows TIFF file locks (BUG-045) and Mac metadata allowlist. TIFF/BMP disposal fix
and exact `.package` allowance are locally verified (focused16/full612). Retry the
unpublished1.2.5 tag with a lease, await all three jobs, inspect assets and publish
the draft with latest=true. See2026-10-13-publication.md. Verify the public latest endpoint and actual downloads.
Update this checkpoint with the main commit, workflow run and release URLs.
