# SignalShow Java audit

## Active scope

Juliet authorized Continuous Audit on October 12, 2026. Prioritize ordinary
2D creation, editing and operations that crash or produce wrong/stale results.
Reproduce the failure, inspect its actual owner and callers, fix it, verify the
affected workflows, record the evidence and continue with the next useful gap.
Roger's platform is believed to be Windows and still needs confirmation.

The first milestone is reliable existing workflows. Chirplet provides a model
for coverage and a comparison implementation; agreement with it alone is not a
mathematical oracle. Feature parity and architectural changes follow stabilization.
Record genuine memory/capability limits now; do not use historical limits to
excuse ordinary failures. Juliet requested removal of the obsolete 2 GiB heap cap.

`check_usage: true` for this new SignalShow run. Follow
[Continuous Audit usage monitoring](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
The Chirplet-specific `false` override does not apply to this run. Last reading:
October 14, weekly window 57% remaining; no other applicable window reported.
On October13 Juliet authorized local commits and preparation of1.2.5. She then
explicitly authorized merging into main and publishing1.2.5 on GitHub as the latest
release. Publication is complete; both finished installer payloads were verified
to exclude old builds and tests. Native keystrokes remain authorized when the Java window is
focused; verify the target field and readback before submitting.

October 14 continuation: Juliet explicitly requested Continuous Audit to find and
fix more bugs and authorized comparison with Chirplet. Continue the major-error
queue using independent mathematical references and production Java callers.
[Continuation evidence](docs/audit/evidence/2026-10-14-math-continuation.md) records
verified batches. The source-led continuation is now verified through all
registered ordinary computation paths: nine additional fixes and 333 new cases,
with the full 997-test package passing. No confirmed major defect from this
bounded survey remains unfixed. Broader native/parameter coverage and recorded
convention differences remain follow-up work. Preserve local changes; no new
commit, push or publication has been performed.

## Standing testing priority and suite plan

Juliet clarified on October 12: **ordinary usage comes first**. People must be
able to create images with normal parameter values and combine them in operation
chains without crashes, frozen controls, stale results or broken layouts.

Build toward hundreds of distinct scenarios covering how functions and operations
work together. Organize them by creation family (Cartesian, polar, image, noise,
aperture), representation (real/imaginary and magnitude/phase), normal parameter
edits, operation chains (arithmetic, threshold, real-part extraction, transforms,
rotation and filtering), source/operand replacement, duplication, view changes,
and save/reopen/export. Include short multi-step tasks and longer compositions.
Choose meaningful combinations rather than inflating counts with repeated checks.

Each scenario should state its starting data, normal parameter values, user
steps, expected visible behavior, production-path checks and saved-result checks.
Record actual desktop evidence separately from model and component tests.
Mathematical accuracy has its own matrix; do not let tiny numerical discrepancies
or rare edge cases displace ordinary usage fixes. Zero-sized images, unusual
inputs and capacity extremes are low priority while these workflows are unstable.

**Current scope (October 14):** Juliet requested automated, source-led mathematical
testing of Java SignalShow. Check the actual creation and operation paths for
major errors with ordinary inputs: complex channel loss, shifted origins, reversed
axes, transform signs/scales and incorrect operation composition. Establish
independent mathematical expectations; Chirplet agreement and test-only formulas
are not sufficient. Fix demonstrated defects and record bounded evidence. Juliet subsequently confirmed desktop availability for bounded native follow-up.
Rare inputs and tiny numerical discrepancies remain outside this round.
Leave this new work local and reviewable; the previous publication is complete.

The first automated math pass is complete: 52 new production-path cases cover
coordinates, spectral operations, complex arithmetic/composition and selected
analytic image creation. BUG-046 (vertical reversal shifting the origin by two
pixels) is fixed locally. All 664 tests and the executable package pass.
[Evidence and limits](docs/audit/evidence/2026-10-14-math.md) remain distinct from
native GUI acceptance and the published release.

October13 resumption: Juliet requested more ordinary **Mac** 2D creation and
operation chains; check Windows later. The chirp/aperture/conjugation/intensity and
offset/clipping/square-root batches passed their scoped checks. Directional operations and exported-result reuse passed after fixing empty first-open
sampled-data editors. Noise creation and mean/intensity chains passed. Polar
creation with division/subtraction passed after correcting corner-origin angular
coordinates. Magnitude/phase creation and extraction passed. Signed
images with absolute value and reversal passed. Complex
powers and reciprocals passed after fixing lost input phase. Annular aperture masking
and diffraction intensity passed. Fourier basis editing
and frequency-domain chains passed. Spatial autocorrelation
and extraction passed. Multi-term image composition, grouping and independent
copy edits passed. Windowed signed images
and operation chains passed. Resizing a source through128×64,256×128 and64×256
with an existing FFT→inverseFFT→Magnitude chain passed all12 saved-array checks.
This Mac round is complete. Release verification exposed BUG-044: dots in parent
folders redirected saves. Its shared path fix passes four new regressions and the
native packaged save retest. SignalShow 1.2.5 is published as the latest GitHub
release with Windows MSI, Apple Silicon Mac DMG and portable JAR downloads. Both
finished installers passed payload inspection. CI also exposed BUG-045: TIFF/BMP
export handles now close before returning. All 612 automated tests pass on Mac
and Windows, including the Windows file-handle regressions.
Windows interactive usage remains pending.
Further usage and mathematical tests precede future Chirplet parity work. Prior
checked rows remain bounded evidence, not certification of every combination.

## Authoritative matrix

- [Read-only report](docs/audit/index.html)
- [Coverage data](docs/audit/signalshow-coverage.json)
- [Current evidence and handoff](docs/audit/evidence/2026-10-14-checkpoint.md)

The JSON owns statuses, evidence, limitations and the ordered queue. The report
is generated from it. Its 130 initial mathematical entries come from the actual
registered 1D/2D generator and operation lists. Special creation menus, editing,
views and demonstrations also need workflow coverage; the registry is not an
exhaustive inventory of every application behavior.

Mathematical checks track Definition, Equations, Numerics, Parameters and
Handbook independently. A numerical pass applies to its written fixture and
caller, not every parameter, view or platform. Reconcile each affected dimension
and consumer when correcting a shared implementation.

Workflow scenarios cover usage, files, platforms and memory. Use bounded rows:
Checked, Needs check, Known bug, Needs scoping or Deferred with a reason. Split a
completed model/component check from a pending native interaction check. Preserve
reported defects until reproduced or resolved, and distinguish historical reports
from current observed failures. Counts are not whole-application certification.

## Verification and maintenance

```sh
# Focused real-model and Swing-control checks
mvn -B -ntp -Djava.awt.headless=true -Dtest=BasicWorkflowTest test

# Full regression suite and executable JAR
mvn -B -ntp -Djava.awt.headless=true package

# After editing audit JSON
python3 scripts/signalshow_audit.py
python3 scripts/signalshow_audit.py --check
```

Use `-o` when Maven dependencies are already cached and an offline run is useful.
The audit validator checks registry completeness, IDs, status values, evidence
references, source paths, active/next queues and generated-report freshness. It
does not execute or certify the application.

At each batch boundary:

1. Save precise actions/inputs, environment, observed failures and expected
   behavior before changing code. Keep complete logs in `docs/audit/evidence/`.
2. Reproduce through production classes and actual native callers. Use independent
   expected values; do not substitute test-only math for application behavior.
3. Verify the correction and structural equivalents. For visible behavior,
   inspect a short native workflow and captured stderr; a headless pass alone
   is insufficient. Keep Windows and macOS evidence distinct.
4. Update affected matrix rows and known limitations. Empty `activeIds` when no
   check is running and maintain a short ordered `nextIds` queue.
5. Regenerate/check the report, update the compact handoff and continue within
   the authorized scope. Check usage at batch boundaries under the active flag.

Reuse valid results rather than rerunning passing suites for tracking-only edits.
Normal creation, editing, computation and save/reopen precede deliberately
exhausted memory, corrupted files or long endurance tests. Long soak tests remain
manual unless specifically requested. Memory measurements must identify heap
settings, dimensions, channels, operations, retained documents and hardware.

## Current continuation

Juliet published the math-audit branch at `a576f27` on October 14. The new local
follow-up corrects Clear/rebuild ownership, group boundaries and consecutive
unary operators (BUG-056–058), plus rectangular creation defaults and cross-
dimension origin settings (BUG-059). All **1,130 tests pass**, including 133 new
cases relative to the published 997-test baseline. Native Mac follow-up verifies
these fixes through real controls and 12 saved complex-data outcomes. See the
[current checkpoint](docs/audit/evidence/2026-10-14-checkpoint.md).

No confirmed crash or major wrong-result defect from this follow-up remains
unfixed. The targeted source/creation/calculator queue is complete. Broader
parameter combinations, lower-priority labels, and Windows interactive testing
remain distinct follow-up work. No test app is open and no audit process is
running; existing user servers are preserved. Changes remain local and uncommitted.

## Current verified baseline

The original 195 tests passed on macOS/JDK 25. Thirty new checks of actual
FunctionFactory/operation classes and Swing parameter editors exposed six failing
cases caused by reversed threshold outputs and parameter bindings. After the
threshold correction, all 225 tests passed. Subsequent search, generator and
image-editing and operation-layout batches reached 606 tests. Release 1.2.5
passed 612 on Mac and Windows CI. The current local audit package passes
1,122 tests on macOS, including 385 new math cases from the first passes and 125
new calculator/editing cases after Juliet published that baseline. The authoritative matrix records each batch's scope and before/after
evidence; native acceptance remains separate.

The new fixtures cover 1D eight-sample signals and 4×2 complex images, both origin
modes, arithmetic with nonzero denominators, threshold boundaries, source
preservation and cache regeneration. They do not certify the full native editor,
rendered plots, files, larger workloads or Windows. The old assertion that signal
class tests were unnecessary because of GUI initialization has been superseded:
the tests initialize non-window Core services and exercise real production models.

All maintained launcher settings now omit `-Xmx2g`. JVM default sizing still
imposes a runtime-specific heap budget; this Mac reports an estimated 6 GiB.
The built-in size picker still stops at 4096, and dense source/channel/result
arrays can require substantial memory. An ordinary three-source/two-operation workload has been measured through
1024 square, with two retention paths corrected. This does not establish a
universal safe size or large-image capacity; see the measured matrix row.

## Initial example supplied by Juliet

The initial scenario was to reproduce the nine-step circular-resolution target in the supplied
`/Users/julietfiss/Documents/SignalShow Tests/Step by Step.pdf`, compare with
the two supplied TIFFs and fix the operation-preview layout while retaining
visible Threshold inputs. Its native walkthrough and preview-layout evidence are now recorded separately
from model/component regressions in the matrix. The historical TIFF is not a
mathematical oracle. Continue with varied ordinary usage scenarios under the
active queue; this example is one class of tests, not the sole acceptance test.
