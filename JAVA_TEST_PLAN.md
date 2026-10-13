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
October 13, weekly window 91% remaining; no other applicable window reported.
On October13 Juliet authorized local commits at verified checkpoints. Do not push,
distribute installers or deploy. Juliet reauthorized native keystrokes after finishing with her terminal;
verify the target field and readback before submitting.

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

**Current scope:** Roger's circular-resolution target and requested layout are
verified in scoped rows. Continue the authorized audit with one ordinary workflow
at a time; the matrix owns the active check and next queue. The larger suite is
implemented incrementally, not all at once.

## Authoritative matrix

- [Read-only report](docs/audit/index.html)
- [Coverage data](docs/audit/signalshow-coverage.json)
- [Current evidence and handoff](docs/audit/evidence/2026-10-13-checkpoint.md)

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

## Current verified baseline

The original 195 tests passed on macOS/JDK 25. Thirty new checks of actual
FunctionFactory/operation classes and Swing parameter editors exposed six failing
cases caused by reversed threshold outputs and parameter bindings. After the
threshold correction, all 225 tests passed. Subsequent search, generator and
image-editing and operation-layout batches now pass 457 tests and build the executable JAR. The
authoritative matrix records the scope and before/after evidence for each batch.

The new fixtures cover 1D eight-sample signals and 4×2 complex images, both origin
modes, arithmetic with nonzero denominators, threshold boundaries, source
preservation and cache regeneration. They do not certify the full native editor,
rendered plots, files, larger workloads or Windows. The old assertion that signal
class tests were unnecessary because of GUI initialization has been superseded:
the tests initialize non-window Core services and exercise real production models.

All maintained launcher settings now omit `-Xmx2g`. JVM default sizing still
imposes a runtime-specific heap budget; this Mac reports an estimated 6 GiB.
The built-in size picker still stops at 4096, and dense source/channel/result
arrays can require substantial memory. Actual safe operating sizes remain
unmeasured; no memory optimization or large-image capacity claim is made.

## Current priority from Juliet

Reproduce the nine-step circular-resolution target in the supplied
`/Users/julietfiss/Documents/SignalShow Tests/Step by Step.pdf`, compare with
the two supplied TIFFs and fix the operation-preview layout while retaining
visible Threshold inputs. Track the native walkthrough separately from model
and component regressions. The historical TIFF is not a mathematical oracle.
