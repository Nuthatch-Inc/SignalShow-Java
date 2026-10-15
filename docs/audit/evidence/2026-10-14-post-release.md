# Post-release continuous audit — October 14, 2026

Authorized continuation on main from `3437460`, following the published 1.2.6.
Prioritize basic 2D creation and operation chains and major wrong results. After
a few ordinary checks pass, broaden into mathematical edge cases and compare
with Chirplet and independent external solutions. Do not force agreement where
the applications deliberately use different definitions. Keep native, Swing,
numerical and Windows evidence separate. No new release is requested.

`check_usage: true`; resumption weekly 53% remaining. Follow
[usage policy](/Users/julietfiss/src/nuthatch-desktop/.github/skills/continuous_audit/continuous_audit.md#monitor-usage).

## Queue

1. Actual 2D chain controls: translation/wrapping, derivative direction and FFT
   normalization/inverse; analytic magnitude/phase input and cached downstream
   intensity, with independent expected values and editor reopening.
2. Broaden to meaningful numerical boundaries once ordinary controls pass:
   zero-valued image regions and complex powers/reciprocals, then a second
   computation family selected from the resulting evidence.
3. Bounded native follow-up for any actual UI question or new production fix.

## Evidence

### Batch 1: ordinary 2D option controls

Six new cases pass through actual Swing editors: Translate wrapping and shifts,
Derivative X/Y direction, and all FFT normalization/inverse combinations. Inputs
are analytic 32×16 magnitude/phase Gaussian images, tested with both origins.
Independent Gaussian samples, finite differences, inverse index mapping and a
direct 2D DFT supply the expectations. Both channels, linked Intensity caches,
source preservation and editor reopening pass. Translation source editing was
not included. These are component checks, not native desktop acceptance.

`OperationEditingWorkflowTest`: 30 tests, zero failures/errors/skips.
See `2026-10-14-post-release-options.log.gz`. No production change required.
Batch-end usage remains weekly 53% remaining.

### Batch 2: division at common zero crossings

BUG-060 confirmed: a complex sinusoid B scaled by 2+3i and divided by B
returned 2+2i at its common zero, instead of the removable limit 2+3i. Linked
Intensity should be 13, not 8. Six original-code cases failed at the imaginary
sample (1D, 32×16 and 16×32; both origins). Correct the real derivative factor
in the shared division fallback. All 44 focused checks pass afterward, including
source-amplitude edits, two complex scale factors, linked caches and existing
ordinary arithmetic/operation-editor regressions. No native check claimed yet.
See `2026-10-14-division-zero-before.log.gz` and `-after.log.gz`.

Independent algebra gives (c B)/B = c away from zeros and the same removable
limit when B has a simple zero. NumPy 2.4.1 complex division confirms four
nonzero approaches (±1e-3, ±1e-6) for B=(1+i)t and c=2+3i. Direct 0/0 remains
undefined; this check does not claim otherwise.
[NumPy division reference](https://numpy.org/doc/stable/reference/generated/numpy.divide.html).

Chirplet's actual CPU DivideOp was executed at t=±.001, ±.0001 and 0. It agrees
with 2+3i above its squared-magnitude floor and returns zero at the common zero,
as documented by `regularizedComplexDivision.js`. Its 2D shader has the same
zero convention (source inspection only; no GPU run). Java deliberately retains
its existing neighbor-based fallback. Endpoint/flat-zero/pole handling is not
redefined by this correction. Batch-end usage: weekly 53% remaining.

### Batch 3: powers of unit-magnitude signals

BUG-061: (-1)^256 returned zero instead of one. Six new original-code chain
cases fail. The exponential helper zeroed purely imaginary exponents below
-700, although imaginary phase changes direction, not magnitude. Restrict the
existing underflow branch to the real part. All 64 affected tests pass after
the change: powers 2, ±256 and ±512, eight nonzero unit phasors plus zero masks,
linked Intensity, both dimensions/origins, and shared spectral/cepstrum callers.
Independent repeated multiplication supplies the integer-power expectations.
See `2026-10-14-unit-power-before.log.gz` and `-after.log.gz`.

NumPy 2.4.1 and Chirplet's actual CPU PowerOp both retain unit magnitude in all
40 nonzero cases (8 phasors × 5 exponents). No Chirplet GPU run claimed.
Java's zero-base convention and negative-real-axis phase of -π remain intact;
Chirplet uses +π for its principal fractional power. Zero^zero, negative powers
at zero, overflow-scale magnitudes and the old >2e9 phase reduction were not
redefined. Those limits should not be confused with this finite-output defect.
Batch-end usage: weekly 52% remaining.

### Batch 4: signed image display and histogram ranges

BUG-062 confirmed in actual image/histogram consumers. An Offset result with
real range -7…-3 rendered its brightest sample at grayscale 145 rather than 255.
A complex histogram whose observed range was -10…-3 instead extended to zero.
Both display extrema scanners initialized maximum to Double.MIN_VALUE, which
is positive. Initialize those two scanners to negative infinity.

Eight original cases fail. All 44 affected tests pass afterward: actual Swing
ImageDisplayPanel channel switches and two offset edits; every rendered pixel;
range text fields; histogram bins/counts; negative row-plot extrema; and existing
histogram baseline, operation-preview layout and editor regressions. Both
128×64 and 64×128 and both origins are covered. See
`2026-10-14-negative-display-before.log.gz` and `-after.log.gz`.

ArrayMath.max has the same initialization pattern but its current production
caller uses nonnegative FFT magnitudes. Changing its all-zero return also changes
Complement Filter's singularity handling. No speculative change made there.
Batch-end usage: weekly 52% remaining. A full package and bounded native
follow-up are next; native and Windows status remains separate until checked.

## Full package checkpoint

Full clean package: **1,156 tests pass**, zero failures/errors/skips, 29.881s.
See `2026-10-14-post-release-package.log.gz` and `-artifact.json`. All three
modified production classes in the executable match target/classes; no nested
JARs, project test classes, JUnit or audit documents were bundled. This is a local
1.2.6 audit build; no publication or commit has been performed.

Native follow-up remains incomplete. Creation/selection actions worked, but
amplitude and function-name entry did not change through typing or paste. An
initial coordinate click reported `noWindowsAvailable` while the process was
live. Manual focus, reattachment and two restarts (the last after Juliet manually
closed the app and the tool connection was reset) did not establish reliable
input. After the last bounded check, Quit succeeded and a process inventory
confirmed no SignalShow instance remains. No new crash report was observed;
these input-routing observations are not evidence of an application crash.

Juliet noted she was using Codex during the attempts. Focus remains a hypothesis,
not a confirmed cause: official Mac Computer Use documentation supports background
work while the user uses other apps. Headless work continues without desktop input.
Current usage: weekly 51% remaining. Next batch: sampled radial/angular profiles,
checking physical coordinates and the existing 1D preview domain before changes.

## Batch 5: sampled polar creation and chains

BUG-063: DataFunctionTerm1D ignored requested coordinates and used the query
array length as its sampling scale. In a polar image, that length counts pixels
or triangular slice entries, not the radius/angle domain. Sixteen original-code
cases fail with large wrong values or zeros. The correction linearly interpolates
at physical coordinates. Crop mode receives the existing 1D editor domain
explicitly; scale-and-zero-pad retains its authored center and width. Source
samples remain unchanged. Ordinary 1D crop/scale regressions still pass.

The combined preview also used Cartesian ranges for both polar profiles. Four
actual Swing control cases fail before aligning those plots with the radial and
angular editors. Parameter-field edits, mode changes, combined-preview data,
image samples and reopening now agree. Both real/imaginary channels, linked
Scale → Intensity after edits, 128×64/64×128 and both origins are checked. All
**174 affected tests pass**, including 20 new cases. Evidence:
`2026-10-14-sampled-polar-before.log.gz`, `-after.log.gz`, and
`2026-10-14-sampled-preview-before.log.gz`.

The saved Python replay compiles a small driver against actual production classes
and compares 408 results with NumPy 2.4.1 `interp`; all pass (maximum absolute
error 1.45e-15). It covers radial and both angular reference domains, two modes,
two parameter edits and both source channels. This is numerical evidence, not
native file import. See `2026-10-14-sampled-polar-oracle.py` and `.json`.

Chirplet source comparison: `src/lib/signals/generators/data.js` declares
`requiresSampleGrid()` and explicitly separates an authored grid from a list of
per-pixel radii. This supports the same domain distinction; its width/fill and
fixed sampled-2D acquisition-grid contracts differ from Java. No direct
cross-application equality or GPU run is claimed for this batch.

Scope limits: the correction uses the existing radial editor domain
0…max(width,height)/2−1 and angular domain −180…179 or 0…89. Samples outside the
source domain are zero; no periodic angular extension is introduced. Native
text-file selection, endpoint/wrapping product decisions and Windows interaction
remain separate. Batch-end usage: weekly 51% remaining. Full package checkpoint
follows this change; native input remains pending and no app is running.

## Second full package checkpoint

After BUG-063, the full clean package passes **1,176 tests**, with zero failures,
errors or skips (31.270s). Every compiled production class matches the executable;
no nested builds, project tests, JUnit or audit documents are bundled. See
`2026-10-14-post-release-package-2.log.gz` and `-artifact-2.json`. The earlier
1,156-test artifact is historical evidence of the first checkpoint. This remains
an unpublished local audit build; all edits are uncommitted on main. No native
app or test/build process is running. Native verification remains pending.

## Batch 6: duplicate sampled profiles and reuse the copy

BUG-064: the Data term factory lost its out-of-block scaling mode, so Duplicate
changed scale-and-zero-pad profiles into cropped profiles. Twelve original-code
cases fail with major output changes (for example 0.0313 becomes 1.2). Preserve
mode and imported filename in the term factory; the existing FunctionCopy still
owns the deep copy of sample arrays. All **54 affected tests pass**: 1D, Cartesian
2D and polar 2D, real/imaginary and magnitude/phase representations, both origins,
linked Intensity after amplitude edits, and independence after mode/data changes.
See `2026-10-14-sampled-copy-before.log.gz` and `-after.log.gz`.

These are actual function-copy and chain checks, not native Duplicate button
acceptance. Full package will follow the next coherent file-import checkpoint.
Current usage: weekly 50% remaining. Next: ordinary 16-bit and fractional-valued
TIFF import followed by Real/Threshold; investigate whether integer decoding
loses scientific-image values. No app or background audit process is running.

## Batch 7: scientific grayscale images → Real → Threshold

BUG-065: grayscale import decoded samples into an integer buffer. A valid -0.5
pixel became zero, as did positive fractional values below one; Threshold at
0.5 consequently gave wrong results. Two original-code cases fail. Read grayscale
samples as doubles and preserve them during centering/padding. All **64 affected
tests pass**, including four new 16-bit PNG/TIFF and floating TIFF cases plus
existing import, editing, opening, export and sampled-image math checks.

The fixtures use actual ImageIO TIFF encoding/decoding with ordinary signed,
binary-exact fractional values. Decoder values are independently checked before
the app importer; every imported and thresholded sample is compared with the
authored formula. This isolates conversion loss in SignalShow from codec loss.
No new native or Windows check is claimed. Existing color-image behavior
(conversion to the 8-bit red channel) is unchanged. See
`2026-10-14-fractional-tiff-before.log.gz` and `-after.log.gz`.

## Third full package checkpoint

After BUG-064–065, the full clean package passes **1,192 tests**, with zero
failures, errors or skips (30.626s). All 566 compiled production classes match
the executable. It contains no nested builds, project tests, JUnit or audit
documents. Evidence: `2026-10-14-post-release-package-3.log.gz` and
`2026-10-14-post-release-artifact-3.json`. This is an unpublished local 1.2.6
audit build; changes remain uncommitted on main. Native input verification is
pending, and no native instance or build/test process remains running.

Next: verify actual Profiles view channels and its cut overlay on rectangular
images. Existing core profile samples pass; prior Swing checks only established
that the panel opened. Weekly usage: 50% remaining, `check_usage: true`.

## Batch 8: Profiles channels and cut overlays

BUG-066: wide corner-origin images applied a display-padding offset twice to
horizontal/diagonal y-cut markers. The y = 0 marker missed the selected pixels by
100 pixels in a 200-pixel preview. Profile values themselves were correct. Remove
the extra offset for the already bottom-aligned image. One original overlay case
fails; all **23 affected tests pass** after the correction. Four earlier Swing
smoke cases now compare every displayed profile value and image-channel value
with independent formulas. Four new cases compare the marker against actual
rendered-image pixel coordinates, allowing the existing pixel-edge convention.

Actual radio buttons and parameter text fields are exercised over all four
channels, wide/tall shapes, both origins, horizontal/vertical/diagonal cuts and
ordinary offsets. Evidence: `2026-10-14-profile-cut-before.log.gz`, `-after.log.gz`
and `ImageProfileWorkflowTest`. No native or Windows acceptance is claimed.
Next: the overview's reused enlarged image and cursor readout after channel,
display-option and source-result changes. Its separate sample cache appears to
outlive the displayed data; verify through ordinary controls before changing it.
Weekly usage remains 50% available; no native app is running.

## Batch 9: enlarged-image cursor values and image growth

BUG-067: the reused overview cursor kept an independent sample cache after the
image changed. Sixteen ordinary workflows fail before correction: switching
channels, magnitude options, phase unwrapping, and editing a linked Scale result.
For example Squared displayed 25.361 while its cursor still reported 5.036.
Four additional isolated image-growth workflows throw ArrayIndexOutOfBoundsException
from the smaller stale buffer. These are actual Swing event-path failures, not
proof that a native process exited. No new native input check was attempted.

The cursor now reads the image graphic's current sample array. All **43 affected
tests pass**, including 20 new cases with both rectangular orientations and both
origins. Tests exercise overview thumbnail events, cursor buttons, actual
checkboxes, option transfer back to thumbnails, linked-result updates and ordinary
size increases. Displayed samples and formatted cursor values match independent
formulas. The phase case checks an ordinary smooth plane interior; this does not
certify general branch-cut phase unwrapping. Logs: `2026-10-14-image-cursor-before`,
`-resize-before`, and `-after.log.gz`.

A full package checkpoint follows both display fixes. Next investigate the
previously unverified corner-origin preparation in Phase Detour Hologram against
an independently computed transform; preserve Java's established inverse-transform
and real-amplitude contracts. Latest usage tool reading: weekly 100% remaining
(new reported window); no reset was requested or consumed by this audit.

## Fourth full package checkpoint

After BUG-066–067, **1,216 tests pass**, zero failures/errors/skips (29.925s). All
566 compiled production classes match the executable; no old builds, project
tests, JUnit or audit documents are bundled. See package/artifact checkpoint 4.
No native app or test/build process is running. This remains an unpublished local
1.2.6 audit build on main, with all changes uncommitted.

## Batch 10: Phase Detour source coordinates and random phase

BUG-068: Phase Detour assigned a centered origin to unchanged corner-origin
samples. This moved the interpreted object by half of each dimension; even an
impulse at physical (0,0) produced alternating phase instead of a constant
spectrum. Reorder the amplitude and generated phase together before assigning
the centered intermediate. Six original-code cases fail; all **68 affected tests
pass**, including 24 hologram cases and existing spectral/kernel regressions.

The expanded production-path oracle independently sums the inverse DFT using
source physical coordinates and centered output coordinates. It covers both
rectangular orientations/origins, nonzero imaginary source channels (intentionally
ignored by the signed-real-amplitude operation), random phase on/off, diffusion
and aperture width. Both source channels are preserved. Java uses the existing
Random Phase generator's default seed 1; it is not fresh randomness per call.

The saved Python replay uses NumPy 2.4.1 `ifft2`, independently implements the
JDK's specified seeded random sequence, and enumerates physical aperture choices.
All **32 combinations / 262,144 output pixels match exactly**. See
`2026-10-14-phase-origin-oracle.py`, `.json`, and `phase-origin-before/after.log.gz`.
References: [NumPy ifft2](https://numpy.org/doc/stable/reference/generated/numpy.fft.ifft2.html)
and [JDK Random](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Random.html).

Chirplet source comparison confirms its PhaseDetourHologram2DOp passes the source
`zeroCentered` setting into its transform. It uses a forward transform and fresh
random phase, while Java retains its inverse transform and fixed seed. The
comparison supports origin-aware preparation, not whole-operation parity; no
Chirplet GPU/browser run was performed. Native Mac input and Windows remain
separate. Weekly usage: 99% remaining. Next ordinary consumer: FFT overview
contents and channel values after source edits and switching views.

## Batch 11: actual FFT overview after channel edits

Four new ordinary 128×64/64×128, both-origin workflows pass without a production
change. They press the real Plot/FFT/Edit toolbar buttons, write amplitude fields
for both source channels, revisit views and inspect all four overview channels.
Closed-form Fourier coefficients of asymmetric complex sinusoidal components
verify the view's direction, unitary normalization, ordering and current samples.
An already evaluated Intensity chain updates after each actual editor event.

All **18 affected tests pass**, including prior sampled-opening and creation-default
regressions. Only the JFrame content destination and library repaint are supplied
by the fixture; computation, editor publication, controls and displayed image
buffers are production paths. Operator-toolbar routing and native acceptance are
not claimed. Evidence: `FourierViewWorkflowTest` and `2026-10-14-fourier-view.log.gz`.
Next: restore Histogram when returning to an ordinary function document. Source
review shows its saved state is not handled by the function toolbar dispatchers.
Weekly usage remains 99% available.

## Batch 12: restore Histogram after selecting another document

BUG-069: function toolbar dispatchers omitted the saved Histogram state. After
looking at another function, returning to the first document left the other
function's content visible. Twelve original-code cases fail. Shared Plot/FFT/
Histogram dispatch now belongs to DataGeneratorToolBar; function toolbars handle
their own editing and dimension-specific views, delegating shared views.

All **43 affected tests pass**. Twelve new cases cover 1D, wide/tall 2D, both
representations and both origins. They use actual Histogram buttons and the exact
saved-state dispatcher called by Function.show(), compare both channel distributions
before/after visiting another document, and check restored Plot/FFT dispatch too.
Existing creation, opening, view and invalid-size recovery regressions also pass.
The JFrame destination is supplied by the fixture; native library selection is
still pending. Evidence: `HistogramRestorationWorkflowTest` and
`2026-10-14-histogram-restore-before/after.log.gz`.

Next full package checkpoint, then one discriminating native input experiment:
CUA's documented target-specific setValue action, which avoids keyboard focus.
Prior attempts used typing/paste. Do not repeat blind keystrokes. Keep at most one
app, read back the actual field and result, and close it if the route is ineffective.
Weekly usage: 99% remaining. No native app is currently running.

## Fifth full package checkpoint

After BUG-068–069 and the ordinary FFT-view checks, **1,242 tests pass**, zero
failures/errors/skips (29.032s). All 566 compiled production classes match the
executable; no old builds, project tests, JUnit or audit documents are bundled.
See package/artifact checkpoint 5. The current executable is an unpublished local
1.2.6 audit build; all changes remain uncommitted on main.


## October 15 continuation: accessibility

The targeted native setValue experiment returned -10005 and did not alter the
field. The app exited after Quit. Juliet then suggested missing accessibility
features. BUG-070 fixes confirmed label/name omissions; ten new Java accessibility
workflows, 91 affected checks and the full 1,252-test package pass. Native names
and an Increment action work, while direct text replacement remains pending.
See [the accessibility evidence](2026-10-15-accessibility.md) for exact scope,
remaining limitations and the next ordinary workflow. All changes remain local
and uncommitted on main; no test instance is running.


## Batch 14: hologram options through the actual operation editor

Four additional ordinary 32×16 complex-Gaussian workflows pass for Hologram
Encoder and Phase Detour, with both origins. Actual diffusion, aperture-width
and random-phase checkboxes update the current preview pixels, published chain
and already evaluated Intensity result. Repeated option changes and reopening
preserve settings and output; source channels remain unchanged. The output is
256×128, centered. No additional production defect was found.

All **64 focused checks pass**, including the 24 independent mathematical
hologram cases and 40 operation-editor cases. This batch compares UI/cache output
with a fresh operation configured independently of the editor; the separate
MathHologramTest and saved NumPy replay supply the mathematical oracle. Preview
pixels are compared with a fresh display of the expected output, verifying that
the editor shows current samples, not independently certifying interpolation.

Evidence: `OperationEditingWorkflowTest.hologramOptionEditsUpdatePreviewPublicationAndCachedIntensity`
and `2026-10-15-hologram-options.log.gz`. No production source changed after the
verified 1,252-test package; these four new workflows passed in the focused run.
All changes remain uncommitted on main. Native follow-up, Windows and capacity
limits remain separate. No SignalShow or audit background process is running.
