# SignalShow math audit checkpoint — October 14, 2026

Active authorization: Juliet explicitly requested Continuous Audit to find and fix
major ordinary-input bugs, with Chirplet comparison allowed. The bounded source-led
ordinary-computation pass is complete; no check is currently running. Use independent mathematical expectations; avoid numerical
minutiae and unlikely inputs. No new commit, push or release was requested.

`check_usage: true`; latest reading: weekly 59% remaining, no other applicable
window. Follow [usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).
No reset, agents, automation or desktop input used.

## State and evidence

- Repo `/Users/julietfiss/src/SignalShow-Java`, branch `codex/signalshow-math-audit`,
  based on main `99614d1`; all audit changes local and uncommitted.
- Latest full headless Maven package: **997 tests pass**, zero failures/errors/skips.
  Log `2026-10-14-math-final-package.log.gz`. Diff whitespace and matrix checks pass.
  Earlier 879-test evidence remains in `2026-10-14-math-continuation-full.log.gz`.
- Published 1.2.5 is unchanged. The rebuilt local target JAR includes audit changes
  and must not be confused with the released download.
- BUG-046: physical Y reversal/origin; initial 52 new tests, 664-test full package.
- BUG-047: hologram source mutation, phase-bin crash and complex residual feedback.
- BUG-048: forward cepstrum output transform direction.
- BUG-049: Poisson Seed editor Double/Integer crash.
- BUG-050: polar Delta/Delta Pair physical-coordinate support.
- BUG-051: Poisson Process rate/independence law.
- BUG-052: rectangle/E extra border outside half-extents.
- BUG-053: actual calculator division precedence (metadata, not hard-coded tokens).
- BUG-054: sampled 1D scale/pad squeezes at viewport clipping, drops final pixel.
- BUG-055: Bar Chart selects its supplied resolution and refreshes normalized data.
- Continuation adds 333 tests, including 1D/2D complex operations/chains, ordinary
  coordinates, analytic generators, noise, apertures, mixed arithmetic and rotation.
  All before/after counts and logs are in `2026-10-14-math-continuation.md`.

## Accepted contracts and limits

Preserve Java's componentwise square root, Bessel even extension, aperture
half-extents, magnitude-only cepstrum (zero-bin log -100), and scalar Poisson
occupancy. Old Poisson Process seed patterns change because their law was wrong.
Phase Detour retains inverse-transform/real-amplitude preparation; random-phase
and alternate-origin preparation remain unverified. Current Chirplet hologram
comparison matches 65,536 pixels, after independent optical/quantizer checks.

Java Rotate uses clockwise image-center geometry; Chirplet uses counterclockwise
physical coordinates. Keep that difference explicit. Ordinary nearest/bilinear
interiors and quarter turns pass; arbitrary bicubic/boundaries are not certified.
Nearest exact half-pixel ties are rounding-dependent. Bessel order 5 error 4.525e-5
against peak .522 is intentionally left unchanged. No unused Extended Cosine Bell
refactor. Noise2D constructor quirk is already handled by FunctionCopy.

## Scope closure and next action

All 130 registered entries have scoped definition/ordinary-computation evidence.
Nine additional fixes (BUG-047 through BUG-055) are complete, with 333 new cases
since the first 664-test pass. The full package now has 997 passing tests.
No confirmed major defect from the completed source survey remains unfixed.
All new work remains local and uncommitted; preserve it for review/check-in.

Remaining follow-up: native Mac regressions of the fixed editor/operation paths,
then broader normal compositions as prioritized by Juliet. Arbitrary interpolation
boundaries and Phase Detour/data-coordinate semantics remain explicitly limited;
no tiny-error or rare-input sweep was started. Windows remains deferred.
The current turn is finished; no unattended continuation is scheduled.

Update `docs/audit/signalshow-coverage.json` and generate/check with
`python3 scripts/signalshow_audit.py` / `--check`. Full first-pass record is
`2026-10-14-math.md`; detailed continuation is `2026-10-14-math-continuation.md`.

No build process or SignalShow app is running. Keep desktop focus unchanged and
preserve existing http://127.0.0.1:8092/docs/audit/ and other servers. At most one
native test app if testing resumes. Windows/native checks remain separate.
