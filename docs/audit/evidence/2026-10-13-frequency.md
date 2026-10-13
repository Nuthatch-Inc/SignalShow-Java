# Frequency demonstrations — October 13, 2026

macOS arm64, Oracle JDK 25; CUA native testing with one app at a time. Ordinary
32×16 images and 128-sample signals, width edits and existing checkbox/radio
controls. Ten component cases are distinct from the two native scenario classes.

## Reproduced failures and corrections

- **BUG-033:** With Zero-Centered off, low/high-pass filters used the wrong bins.
  1D filtering also inherited the wrong origin from the term-based factory.
  FilteringDemo1D/2D now sample filters on signed FFT frequencies, including the
  wrapped negative bins, and retain the input origin. Centered cases remain valid.
- **BUG-034:** DC Component was suppressed when Low Frequencies was unchecked,
  despite being a separate control. DC and non-DC low frequencies now contribute
  independently. Spatial previews also retain the source origin; frequency bins
  in Frequency Locations remain centered as before.
- **BUG-035:** The RGB preview passed the source-origin flag as its autoscale
  flag. Uncentered previews kept the initial −1/−1 scale and rendered incorrectly.
  RGB autoscaling now follows the same policy as the grayscale display.

Six of the initial ten regressions failed. Native inspection then exposed the
RGB bug; three extended cases failed on its −1 scale. All ten now pass. The
final offline package passes **539 tests, zero failures/errors/skips**.

## Calculation and control evidence

Eight Swing cases cover 1D/2D, both origins and Ideal/Gaussian shape. They switch
Low Pass→High Pass and edit width 3→6 through the actual parameter editor. Input
contains DC and three ordinary Fourier modes, so each output sample has an
independent analytic gain without using production FFT/mask helpers. Two more
cases check low/mid/high bands, cutoffs 3/6, independent DC selection, real/imaginary
view and the source origin. RGB bounds are checked in the actual display objects.

Native scenario 1: create an uncentered 32×16 Cartesian Cosine, amplitude 1, period 16
and zero imaginary part. Select Gaussian Low Pass with b=3. Before, the imaginary
result oscillated ±0.123760 although the source and symmetric filter are real.
After, it is numerical zero (the normal display scale expands to about ±0.01).
The real amplitude is 0.2475201214, equal to exp(−π(2/3)²). High Pass gives the
complementary amplitude 0.7524798786. On the final build, the formerly invalid
RGB preview has bounds 0–1 and displays the wrapped frequency/filter pattern.

Native scenario 2: create a centered 32×16 Constant amplitude 2, select Frequency
Locations, check DC and uncheck Low/Mid/High. Before, the reconstruction went blank
(scale ±0.01); after, the blue DC channel remains at 2 (RGB scale 0–2). Repeat with
an uncentered Constant on the final build: amplitude 2 remains visible, and the
spatial axes use the source origin. Screenshots confirm the independent choices.

The standalone verifier checks saved source samples, matching before/after
hashes, expected Gaussian gain and native display extrema. It does not claim
native export of reconstructed samples: those are checked in the Swing tests.
The three launches appended only configuration load/save messages. Every test
app was closed before replacement and after testing; zero processes remain.
No new controls/wording, push, release or Windows verification.

## Remaining scope

Band Pass/Band Reject in the filtering demo need a separate bounded usage check.
The Frequency Locations low/mid/high bands are a different implementation and do
not certify those choices. Settings still reset on reopen (existing matrix issue).
Manual color-scale edits, broad image types and long sessions remain unverified.

Source finding for a later sampling check: Sampling1D still uses the old
term-based FunctionFactory.createFunction1D overload, which ignores its supplied
origin/name and uses creation defaults. It also builds its reconstruction mask
from ordinary coordinates. Verify an ordinary period/filter-width/reconstruction
workflow before changing that caller; this batch does not certify Sampling1D.
