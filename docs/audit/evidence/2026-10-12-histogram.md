# Histogram display audit — October 12, 2026

Native Histogram on the 128×64 complex chirp clipped its horizontal axis labels
and the right edge of its last bar. Autofit used counts without their zero
baseline and treated the final bin start as the end of the plot. Histogram
ranges now include zero, vertical label space and the complete last bin. The
horizontal axis displays sample values rather than integer sample indices.

The same consumer hid an imaginary channel whose values ranged from zero to a
positive number. Channel presence now checks for any nonzero sample; a purely
real image still shows only one histogram.

## Verification

- Three actual Swing/paint regressions pass; two failed before correction.
  Ordinary 128×64 data and 30/16/40 bins retain all 8,192 samples in each channel,
  the zero baseline and the entire last bar. Nonnegative imaginary and real-only
  images show the appropriate channel count.
- Full offline Maven package: **437 tests pass**, no failures/errors/skips.
- Native reload of `rectangular chirp views.txt`: the default 30-bin histogram
  displays its baseline, decimal axis labels and complete edge bars. Typing 40
  into Number of Bins commits and updates the visible bars; readback confirms 40.
  Both layouts were inspected and saved as screenshots.
- Native log is clean. Test app closed and process exit verified. Initial typing
  while the app lacked keyboard focus did not change the value; that attempt
  is not a passing check. Foreground coordinate activation followed by native
  typing and explicit readback produced the verified 40-bin result.

Environment: macOS arm64, Oracle JDK 25+37. This is bounded display and channel
coverage, not certification of every histogram mode, transform or Windows UI.
The prior clipped-axis issue in the Profiles evidence is superseded by this fix.
