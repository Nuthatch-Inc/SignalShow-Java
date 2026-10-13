# Independent operation copies and compact parameter layout — October 12, 2026

Ordinary continuation of Roger’s image workflow on macOS arm64, Oracle JDK 25+37.
Changes remain uncommitted; Windows has not been tested.

## Reproductions and changes

BUG-011: native duplication followed by a Threshold cutoff edit changed both copies.
Both clone methods shared their operation objects and expression rule. Operation
cloning now preserves configured fields and copies its mutable ParameterBlock;
each operator system receives its own operations, input list and matching rule.
Input producers remain intentionally linked. Four production regressions failed
before and pass after: 1D/2D nested Threshold/Real/Times edits and operand replacement,
configured rotation/interpolation, and inverse FFT settings/ParameterBlock ownership.

BUG-012: at about 1153×742 logical points, the wide parameter arrangement hid
operation documentation. Existing descriptions now sit above their numeric fields;
compound parameter groups stack vertically above the scrollable result preview.
No controls or wording were added. Two real Swing layout checks failed before and
pass after; the earlier preview sizing check remains passing.

## Verification

Focused regression checkpoint: 41 tests pass. Full offline Maven package: 408 tests
pass and the executable JAR builds. See adjacent before/after/package logs.

Native rebuilt app, one instance, 256×256 polar chirp:
- Duplicate Threshold at 0.5, type 0.75 into the copy, then select the original:
  original remains 0.5 and thumbnails show distinct outputs.
- At a 2306×1484 pixel window, Threshold help, all three fields and full-sized
  previews remain accessible. Scroll to the imaginary preview; fields stay fixed
  and typing 0.6 recomputes the original image.
- Change the copy’s operator to Rotate, type 45 degrees and select Nearest Neighbor.
  Angle, all interpolation choices, help and preview are visible; thumbnail and
  result update. Vertical scrolling exposes the second preview. The preview also
  retains a horizontal scrollbar at this width.
- Native stderr contains only configuration load/save messages. Close the window
  and verify the process exited; no unused instance remains.

This verifies the stated macOS interactions, not every operator layout or Windows.
The headless ownership checks cover both 1D and 2D production paths; native
independence was checked in 2D. Roger’s full nine-step export remains covered by
the preceding rotation batch and was not unnecessarily repeated here.
