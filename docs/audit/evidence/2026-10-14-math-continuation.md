# Continued automated math audit — October 14, 2026

Juliet explicitly requested Continuous Audit to find and fix more bugs, with
Chirplet available for comparison. Continue through verified batches. Prioritize
ordinary-input crashes and substantial wrong results; independent mathematics
decides correctness, not cross-implementation agreement alone. Work stays local
on codex/signalshow-math-audit; the earlier uncommitted reversal fix is preserved.
No new release or commit is requested. No desktop input is needed for this scope.

`check_usage: true`; resumption reading: weekly 61% remaining. Follow the
Continuous Audit usage policy at each batch boundary. No reset or new automation.

## Active queue

1. Hologram encoding: source ownership, eight periodic phase columns, quantized
   complex error feedback and repeated use of ordinary complex images.
2. Cepstrum and complement kernels: definition/direction/scale comparison with
   Chirplet, checked independently through Java production operations.
3. Noise generation: ordinary parameter meanings, distributions and complex use.
4. Remaining ordinary analytic/window generators and operation composition.

The code review found hologram quantization writes into input magnitude/phase
caches, and its two callers duplicate a seven-step quantizer for eight columns.
Reproduce before changing either implementation. Phase Detour's use of the real
input is explicit in Chirplet's contract, so it is not by itself channel loss.

## Hologram encoding — verified

BUG-047: the encoder overwrote source-owned magnitude/phase caches with integer
cell indices. Normal magnitude/phase input at +pi selected column 8 and threw an
ArrayIndexOutOfBoundsException. Seven phase steps were used for eight periodic
columns, and error diffusion used separate polar differences instead of the
actual complex residual. The duplicated Phase Detour quantizer had the same math.

Both operations now use HologramEncoding: read-only Cartesian source channels,
eight periodic phase bins, bounded aperture heights, and complex residual
feedback. Direct flat output also avoids the intermediate expanded image/cell
arrays. There is no new image-size cap. Java Phase Detour's existing inverse-FFT
and real-amplitude preparation are retained; its convention is not claimed to
match Chirplet's forward-FFT preparation.

MathHologramTest runs 14 cases: wide/tall images and both origins; unchanged source
channels and repeat use; authored half-cycle and eight phase directions; both
aperture widths; diffusion; blank images; edited complex Scale chains; and both
Phase Detour options after a direct inverse-DFT reference. Cell first-order
Fourier amplitudes/phases and an independently enumerated quantizer supply the
oracle. The transform fixture uses an asymmetric noninteger Gaussian center to
avoid phase-bin midpoint ties, which are not this audit's target.

Final tests against original code: 10 failures and two exceptions (only two blank
image cases pass). Corrected code: all 14 pass. Logs: 2026-10-14-hologram-before.log.gz
and 2026-10-14-hologram-after.log.gz. Full package validation follows at the broader
checkpoint. No native UI or Windows run is implied.

A separate Java-to-Node comparison exported actual corrected Java output for
8x16 and 16x8 complex images (r=1+0.3*cos(0.21*k), i=0.7*sin(0.17*k)), all four
diffusion/aperture combinations. Chirplet quantizeMagPhase/encodeCell matched all
65,536 binary pixels across eight cases; log: 2026-10-14-hologram-chirplet.log.gz.
This corroborates the independent tests; it is not evidence of whole-operation
Phase Detour parity or of either native GUI.

Compared Chirplet helper SHA-256: `69d740274517064af3d373b5abde3c2b1f728f222f82070fbd31adfc951b5083`.

## Cepstrum and complement kernels — verified

BUG-048: forward cepstrum applied a forward FFT to the log-magnitude spectrum.
For ordinary complex input this reverses the imaginary cepstrum (e.g. +0.1272
became -0.1272); forward/inverse chains reconstruct a reflected spectrum.
Both dimensions now implement IFFT(log(abs(FFT(input)))); inverse mode is written
as IFFT(exp(FFT(input))). The latter is mathematically equivalent to its previous
two-opposite-transform formulation. Existing zero-bin logarithm (-100) is retained.

This is a magnitude cepstrum, not phase-unwrapped complex cepstrum. The existing
help incorrectly described unwrapped phase; it now explains the actual operation
and why inverse cannot restore discarded spectral phase. The transform direction
is supported by MathWorks' definition: https://www.mathworks.com/help/signal/ref/rceps.html.
Chirplet's magnitude definition agrees; its 1e-10 floor and optional complex mode
are separate behavior and were not ported.

MathSpectralKernelsTest: 28 production cases on 32/64-sample signals and 16x8/8x16
images, both origins, using independent physical-coordinate DFT sums. All inputs
have ordinary nonzero spectral magnitudes. Twelve forward/chain checks fail on
original code; eight inverse and eight complement checks already pass. Corrected
28 cases and 16 existing independent transform/convolution cases all pass (44).
Complement order 1/2/5 matches the independent finite-series kernel with unitary
transforms; no change was necessary. No tiny spectral-bin or extreme-order audit.
Logs: 2026-10-14-kernels-before.log.gz and 2026-10-14-kernels-after.log.gz.

Continuation checkpoint: weekly 61% remaining. Next: ordinary noise parameters,
seed edits and distribution meanings, then remaining analytic/window generators.

## Noise distributions and seed edits — verified

BUG-049: Poisson inherited three parameter types (Double, Double, Integer) despite
having only mean and seed controls. Editing Seed stored a Double at index 1; the
next getSeed/generation cast it to Integer and threw ClassCastException. Both
1D and 2D editors were affected. Explicit Poisson parameter types (Double, Integer)
fix the shared owner, without changing labels or adding controls.

MathNoiseTest: eight distribution cases, each with 32,768 samples at two fixed
seeds, check ordinary means/variances or quantiles and deterministic regeneration.
Uniform is correctly labeled half-width; Cauchy/Lorentz is checked by quantiles
because its mean/variance do not exist. Poisson, Binomial, Gaussian, Exponential,
Rayleigh, Salt/Pepper and Uniform pass the independent distribution properties.

28 actual 2D editor cases (seven families, 128x64/64x128 and both origins) type
two new seeds through ParameterEditor, regenerate complex source/Intensity chains
and duplicate the result definition. A 1D Poisson editor case verifies the same
fix. Five Poisson cases throw on original code; the other 32 cases pass. After the
fix all 37 pass, as do eight copy and three histogram regressions (48 total).
The first test-development run lacked the app's Borders service; the saved
before log is the corrected harness against unchanged production code.

NoiseFunctionTerm2D's parameter-block constructor does not use its effective
1D settings, but the existing FunctionCopy owner already handles that correctly.
The ordinary copy tests pass; no speculative constructor refactor was made.
Native focus/typing and Windows remain separate from these headless Swing tests.
Logs: 2026-10-14-noise-before.log.gz and 2026-10-14-noise-after.log.gz.

Continuation checkpoint: weekly 61% remaining. Next: registered windows and
remaining analytic generation, including coordinate handling of polar impulses.
Extended Cosine Bell exists as unused source but is not registered or selectable;
it is outside the ordinary-use priority of this pass.

## Registered windows — verified

Hamming, Hanning, Welch and Parzen pass 16 independent production cases, with
128x64/64x128 images, both origins, separate signed real/imaginary window factors,
width edits and downstream Intensity. References use squared cosines, the beta
polynomial and four-box cubic B-spline rather than copying the piecewise code.
The same profiles also verify 1D generation. Chirplet's current window definitions
were inspected and agree at positive widths; its configurable Hann exponent and
negative-width policy are separate features, not Java defects. No correction was
necessary. Log: 2026-10-14-windows.log.gz.

The remaining registered analytic survey identified two ordinary-input candidates
for reproduction: Delta/Delta-Pair use array offsets on nonuniform polar radius
and angle arrays; Poisson Process uses Poisson-distributed waiting gaps instead
of exponential arrival times. Bessel's even extension is explicitly documented
in Java and must not be mistaken for a sign bug.

## Polar Delta and Delta Pair — verified

BUG-050: Delta, Delta Pair Even and Delta Pair Odd converted the requested center
to an array offset. That only works on unit-spaced 1D input. Polar creation passes
a nonuniform triangular radius table and full angle samples. For example, radial
Delta at radius 5 selected the fifth array element (radius sqrt(8)) rather than
the pixels at radius 5. Angular Delta could select one arbitrary pixel.

Each generator now evaluates the requested coordinate at every provided sample.
This follows the existing help's discrete Kronecker definition and preserves the
existing pair signs. Integer-coordinate ordinary 1D behavior remains identical;
no new subpixel snapping convention is introduced. Chirplet's current Delta also
evaluates coordinates rather than offsets (it separately rounds its center).

All 24 rectangular polar image cases failed before correction. Afterward all 36
MathImpulseCoordinatesTest cases pass, including 12 1D cases and complex Scale
chains on both radial/angular factors and both origins. The focused group also
passes eight polar and 38 older 1D generator cases (82 total). Logs:
2026-10-14-impulses-before.log.gz and 2026-10-14-impulses-after.log.gz.

Continuation checkpoint: weekly 60% remaining. Next: Poisson Process rate law,
then remaining ordinary analytic contracts. No native app or desktop input used.

## Poisson Process and random phase — verified

BUG-051: the process generator drew integer Poisson waiting gaps plus one, rather
than Poisson arrivals. At normal rates 0.5/1/2 it occupied about 33.3%/50.0%/66.5%
of samples, instead of 39.3%/63.2%/86.5%. It also introduced dependence between
adjacent samples. The output has always represented occupancy (one amplitude
per occupied sample), not accumulated event counts.

The generator now directly samples independent unit-bin occupancy with probability
1-exp(-rate), using the existing seeded Java PRNG. This has exactly the sampled
Poisson-process law without constructing continuous event times; the arbitrary
0.007 rate cutoff is no longer needed. Previously generated patterns change for
the same seed because their old distribution was wrong. The help now states
occupancy explicitly. This does not add Chirplet's complex random-phase event mode
or its coordinate-stable window sampling.

Independent reference: the zero-event Poisson probability from NIST implies the
occupied-bin law: https://www.itl.nist.gov/div898/handbook/eda/section3/eda366j.htm.
Chirplet's current Poisson Process contract checks this same probability and
adjacent independence; its PRNG/coordinate semantics are not copied.

MathStochasticProcessesTest: 12 cases, including rates 0.1/0.5/1/2 with two fixed
seeds and both origins, 65,536 unit samples per distribution check; rectangular
complex image rate edits, Intensity and copies; and random-phase images with
uniform angles/unit phasor properties. Three original rate cases fail; all 12
pass after correction. Logs: 2026-10-14-process-before.log.gz and
2026-10-14-process-after.log.gz. No native UI or Windows acceptance claimed.

## Remaining ordinary analytic profiles — verified with stated tolerance

MathAnalyticGeneratorsTest adds 72 cases across 18 profile variants, including
Besinc, Bessel orders 0/1/2/5, Lorentzian, polynomials, sinc-squared, square wave,
stairstep, steps/exponential, sign, comb, double slit, constants and zero. Both
rectangular orientations/origins, both complex channels, amplitude edits and
Intensity chains are checked. Bessel uses an independent integral representation
from NIST DLMF 10.9.2 (https://dlmf.nist.gov/10.9#E2); Java's documented even
extension and pi argument scale are retained. Other references use the normal
closed-form definition, never production generation helpers.

The initial four Bessel-order-5 cases exceeded the generic 1e-5 threshold. One
bounded measurement found maximum absolute error 4.525e-5 against a profile peak
0.522 (about 0.009% of peak). This is the kind of small numerical approximation
Juliet explicitly deprioritized. No production change was made; order-5 checks
use an explicit 1e-4 major-error tolerance, with the limitation recorded here.
All 72 cases pass. Evidence: 2026-10-14-analytic.log.gz and
2026-10-14-bessel-probe.log.gz. Full package verification is next, followed by
the remaining ordinary aperture geometry and 1D coordinate operations.


## Rectangular, E and multiarm apertures — verified

BUG-052: the shared rectangle helper filled an expanded bounding box, adding an
unrequested border. A normal half-width 8 / half-height 5 rectangle covered 247
pixels instead of 187 (32% extra area). E shapes shared the error. The bounds now
use the first/last pixel centers actually inside the rectangle, including its
boundary. Java's existing half-extent controls are preserved: both the interactive
guide and E composition establish that convention. Chirplet's full-width rectangle
and midpoint edge convention are not substituted for it. Previously empty help
pages now explain Java's sizes.

Eight original rectangle/E cases fail, then all 16 MathApertureGeometryTest cases
pass, plus 12 existing MathGenerators2DTest cases (28 total). Coverage includes
64x32 / 32x64, both origins, two complex channels, normal integer/fractional center
edits and Intensity. Separate geometry uses rectangle/E set membership and explicit
cardinal/equilateral multiarm centers. The filled and annular multiarm generators
pass without changes. Logs: 2026-10-14-apertures-before.log.gz and
2026-10-14-apertures-after.log.gz. Headless evidence only.


## 1D coordinate operations — verified

MathCoordinates1DTest adds 16 passing cases with 32/64 samples and both origins.
Independent physical reflection, polynomial finite differences and closed-form
Faulhaber sums check both complex channels. Actual linked chains cover reverse
involution, edited positive/negative translations with/without wrapping followed
by complex Scale, and derivative/integral compositions with their established
boundary constant. Source samples remain unchanged. No production fix was needed.
Evidence: 2026-10-14-coordinate-1d.log.gz. Headless execution only.

The remaining source survey identified ordinary arithmetic precedence, arbitrary
image rotations, and editable 1D data scaling as useful next checks. Pure image
assets do not need an invented analytic formula; existing import/edit tests will
be mapped separately from mathematical definitions.


## Mixed arithmetic precedence — verified

BUG-053: DivideOp had addition priority. The calculator obtains tokens directly
from operation.getPriority(), so A+B/C became (A+B)/C and A/B*C became A/(B*C).
The earlier grouped arithmetic tests manually supplied the correct division token,
which hid this issue. Division now declares product priority. Chirplet's
src/lib/signals/operations/binaryPrecedence.js confirms the same standard rule;
the Java CombineOpsRule contract already explicitly specifies it.

MathArithmeticPrecedenceTest has 40 cases: ten ordinary expressions in 1D/2D and
both origins, parentheses and left associativity, followed by linked Intensity.
The oracle multiplies/divides in polar form independently of production Cartesian
arithmetic; source arrays remain unchanged. Twelve original cases fail; all 40
pass after correction, plus eight selection and eight complex-chain regressions
(56 total). Logs: 2026-10-14-precedence-before.log.gz and
2026-10-14-precedence-after.log.gz. Existing saved in-memory expressions with old
explicit priority tokens are not automatically migrated; no persisted expression
format was introduced in this change.

## Arbitrary image rotations — verified within Java's convention

Eight MathRotationInterpolationTest cases check 30, -45 and 135 degrees in both
rectangular orientations/origins, nearest and bilinear interpolation, both complex
channels, edited angles and Intensity. Independent inverse geometry and affine
profiles verify a substantial interior; distant uncovered canvas must be zero.
All eight pass along with eight existing quarter-turn cases (all interpolation
choices). Evidence: 2026-10-14-rotation-interpolation.log.gz.

Java Rotate is documented as an image-center rotation. Its established positive
angle is clockwise on screen, around the midpoint of the pixel centers. Chirplet
uses counterclockwise physical-coordinate rotation; that convention difference
is recorded, not silently ported. Exact nearest-neighbor half-pixel ties depend
on JAI's float-angle rounding and are excluded from unique-neighbor expectations.
Bilinear checks allow its subpixel quantization; arbitrary bicubic kernels and
boundary interpolation are not certified. No production rotation change needed.


## Sampled 1D data placement — verified

BUG-054: scale-and-zero-pad resized the entire signal into the clipped viewport
interval. Ordinary center edits changed the signal's scale instead of merely
clipping it; the exclusive right edge also omitted the final sample. Resizing now
uses the full authored width, then copies its visible interval. Crop mode retains
its existing inclusive mask, and the existing data half-width convention remains.

Four of eight MathDataScalingTest cases fail on original code. All eight now pass
on 32/64 samples and both origins, with normal width/center edits, nonconstant
ramps, different complex-channel shifts/amplitudes and linked Intensity. Independent
linear-ramp interpolation checks clipping without reusing production helpers.
Original loaded arrays are unchanged. Seven complex-file and 16 coordinate cases
also pass (31 total). Logs: 2026-10-14-data-scaling-before.log.gz and
2026-10-14-data-scaling-after.log.gz.

Next checkpoint runs the full package. The final registered image-data paths will
then get a bounded nonconstant scaling/resource check; predefined artwork is not
an analytic mathematical function, and native UI acceptance remains separate.


## Sampled image geometry and packaged resources — verified

MathSampledImagesTest adds 30 cases. Four nonconstant affine-ramp cases verify
64x32 / 32x64 scaling by 0.5 and 0.75, normal center edits/clipping, amplitudes,
both channels/origins and Intensity. All samples are checked against independently
mapped coordinates; the existing image resize/placement path passes unchanged.
Sixteen cases compare eight packaged images at original resolution against
ImageIO-decoded PNG pixels, including amplitude edits and complex Intensity.
MRI/SAR images intentionally map black/white to -pi/+pi radians. Four additional
magnitude/phase chains verify their complex phasors and amplitude edits; their
initial generic grayscale-oracle mismatch was a test-contract correction, not a
production defect.

BUG-055: Bar Chart's three-argument resize method never overrode the actual
parent's two-argument method. Normal 256/512 images used a stretched 128 image,
losing supplied chart detail. The implementation now overrides the actual method,
uses its existing resolution-selection rule, invalidates its cache when the
selected resource changes, and shares the standard normalized resource loader.
The unreachable old path's raw 0..255 loading is not copied into the active path.
Six cases cover 128/256/512, both origins, an initial 128 cache followed by edits,
both channels, amplitudes and Intensity. Four original 256/512 cases fail; all 30
sampled-image cases pass after correction, plus 156 generator and 9 import-edit
regressions (195 total). Logs: 2026-10-14-sampled-images-before.log.gz and
2026-10-14-sampled-images-after.log.gz. No new native acceptance claimed.


## Completed source-led pass — final verification

The continuation adds **333 automated cases** and resolves **nine additional
bugs (BUG-047 through BUG-055)** beyond the first pass's vertical-reversal fix.
The full executable package passes **997 tests**, with no failures, errors or
skips. Evidence: 2026-10-14-math-final-package.log.gz. The local audit JAR contains
the new implementation and excludes project test classes, JUnit and audit docs;
its hash is in 2026-10-14-math-artifact.txt. No installer or release was published.
Diff whitespace and the authoritative audit inventory/report checks pass.

All 130 registered entries now have scoped definition/ordinary-computation
coverage; this is not a whole-application correctness claim. There is no remaining
confirmed major defect in this source survey awaiting a fix. The targeted normal-
input queue is complete. Native regressions, broader composition/parameter
coverage, arbitrary interpolation boundaries and the explicitly recorded
Phase Detour/data-coordinate conventions remain separate follow-up work.
Minor Bessel approximation error remains intentionally deferred.

The latest usage reading is weekly 59% remaining. Changes remain uncommitted on
codex/signalshow-math-audit. No SignalShow instance was launched, desktop focus
was untouched, and no test/build process remains active. The existing audit
viewer/server and published 1.2.5 release remain unchanged apart from the local
report's updated evidence. This report does not promise unattended work after
the current turn ends.
