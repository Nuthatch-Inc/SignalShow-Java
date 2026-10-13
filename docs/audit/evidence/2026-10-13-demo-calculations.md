# Demonstration calculations — October 13, 2026

Ordinary convolution/correlation playback, restart and saved results. macOS
arm64, Oracle JDK 25, CUA native interaction; one app at a time. No new controls
or wording. These are two native usage scenarios plus component regressions,
not 26 additional distinct native acceptance scenarios.

## BUG-032: wrong values and incomplete convolution/correlation animations

The previous 2D demo ignored its default Normalize Filter/Reference checkbox.
Both dimensions used incorrect sliding coordinates and stopped before all
samples had been written. Correlation omitted conjugating its reference.
The imaginary 2D overlay used the filter's real values. A separate completion
shortcut always used circular convolution, even with Wrap Around disabled.

Twenty-four Gaussian/offset-image cases failed before correction. The 128-sample
1D animation omitted its last sample; centered 32×16 animation omitted one pixel,
and uncentered playback left 33 pixels uncomputed. The failures included large
amplitude/phase errors, not small floating-point discrepancies. The before log
records maximum errors, completed steps and last samples for each case.

A shared coordinate sampler now supplies the reversed filter or conjugated
reference at each output coordinate, respecting the source origin and wrapping.
The existing normalization operation is applied once when preparing the filter.
Frame counts include every output sample; restart clears previous output.
The completion shortcut uses zero padding when wrapping is disabled. The
imaginary preview uses imaginary filter samples and both plots use the source
origin. No backend ConvolveOp/CorrelateOp definition changed.

## Independent component checks

The production Swing demonstrations run on the event-dispatch thread. Twenty-four
cases cover 128-sample signals and 32×16 images, both origins, convolution and
correlation, wrap on/off, normalized real smoothing filters and unnormalized
complex references. Tests step, go back, replay, restart, finish the animation,
and compare every real/imaginary sample with explicit independent spatial sums.
The separate FFT completion path is checked against those sums too. Sources
and references stay unchanged. Two additional checks verify zero imaginary
previews and the origin of both 2D plots. Existing timer regressions also pass.

Final offline package: **529 tests, zero failures/errors/skips**. Existing JAR
resource-overlap warnings remain. The final 2D code avoids creating an unused
output function on every animation frame; numerical behavior is unchanged from
the native-verified build.

## Native reproduction and recovery

1. Create a centered 32×16 Image Constant, type amplitude 2, read back before
   Return, save the source, and select it in both convolution input slots.
   Leave Wrap Around and Normalize selected. Play at the fastest existing speed.
   Before: output scale 0–2048 and an uncomputed black lower-right pixel.
   After: uniform amplitude 2, scale 1.99–2.01, no missing pixel. Show Imaginary
   gives zero input/filter and output (scale −0.01–0.01), rather than a real filter
   in the imaginary preview. Restart clears output; three Step presses produce
   the first output value at amplitude 2. Source exports before/after are identical.
2. Create a 256-sample Sine (amplitude 2, center 8, period 32, phase 30°) with imaginary
   Constant 0.5, and a Rectangle (amplitude 1.5, center 12, width 32) with imaginary
   Constant 0.25. Choose correlation and disable normalization. Edit the reference
   width to 16, save it, reopen the demo and play to completion. Save Result As Data
   creates a usable signal; Save Complex Data and Plot remain functional. All 256
   exported complex samples match direct circular correlation of the saved
   inputs (maximum complex error 2.34e−14); real output spans 1.606–62.394.
   The last sample is present and correct. This catches the reference-conjugation
   sign and demonstrates reference edits reaching a reopened demonstration.

The saved verifier reads the accompanying native sample files; images and AX
snapshots record the displayed results. Both launches appended only configuration
load/save messages. SignalShow was closed and zero remaining test processes were
verified after the native checks. Nothing pushed or released.

## Limits and next work

Native numerical examples are centered; both origins and Wrap Around off are
component-tested. Complex normalization semantics were not changed or certified;
normalization checks use ordinary positive real smoothing filters. Frequency
filtering and frequency-location demonstrations still need their own numerical
checks. Settings retention, native accessibility ownership (BUG-031), Windows,
large demonstration runtime and long playback sessions remain separate gaps.
