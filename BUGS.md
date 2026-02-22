# BUGS

This file documents known bugs and issues in the SignalShow-Java repository. Use the entries below to collect bug reports, with reproducible steps, impact, and any temporary workarounds.

ID format: BUG-### (e.g. BUG-001)

---

## Template

- ID: BUG-XXX
- Title: Short descriptive title
- Status: open / in-progress / resolved / wontfix
- Reported by: <name>
- Date: YYYY-MM-DD
- Environment: OS, Java version, branch, steps to reproduce
- Description: Detailed description
- Steps to reproduce:
  1.
  2.
  3.
- Expected behavior:
- Actual behavior:
- Logs / stacktrace:
- Temporary workaround:
- Notes / PR:

---

## Example

- ID: BUG-001
- Title: Script fails when class files missing
- Status: resolved
- Reported by: julietfiss
- Date: 2025-10-09
- Environment: macOS, openjdk 11, branch main
- Description: Running `./run-signalshow.sh` failed with "Error: Could not find or load main class SignalShow" when class files were not compiled.
- Steps to reproduce:
  1. Clone repository
  2. Remove `SignalShow/SignalShow.class` if present
  3. Run `./run-signalshow.sh`
- Expected behavior: Script should compile sources and run the application.
- Actual behavior: Java threw ClassNotFoundException because `.class` not present.
- Logs / stacktrace:
  Error: Could not find or load main class SignalShow
  Caused by: java.lang.ClassNotFoundException: SignalShow
- Temporary workaround: Manually compile with `javac SignalShow.java signals/**/*.java` before running.
- Notes / PR: Updated `run-signalshow.sh` to automatically compile missing classes and committed as "Add automatic compilation to run-signalshow.sh script" (commit b996cd6).

---

## Active Bugs

- ID: BUG-002
- Title: Cross-correlation notation inconsistency with Roger Easton's textbook
- Status: resolved
- Reported by: Roger Easton Jr
- Date: 2024-12-03
- Environment: All platforms, all versions
- Description: The cross-correlation implementation followed the Wikipedia/Wolfram Alpha convention, but needed to be changed to match the notation used in Roger Easton's textbook. After a Zoom meeting on 2025-11-01, it was determined that the software was mathematically correct according to standard references, but used the opposite operand order from Roger's preferred notation. Both the binary operation (CorrelateOp) and the demo visualization were updated for consistency with the textbook.
- Steps to reproduce:
  1. Load two functions f1 and f2
  2. Compute cross-correlation using binary operation menu
  3. Compare with expected result from Roger's textbook notation
- Expected behavior: Cross-correlation should follow Roger Easton's textbook notation
- Actual behavior: Cross-correlation followed Wikipedia/Wolfram Alpha notation (opposite operand order)
- Logs / stacktrace: N/A
- Temporary workaround: None - required systematic code changes
- Notes / PR: Investigation history: Initially attempted fix in CombineOpsRule.java (reverted). Then swapped inputs in ConvolveDemoSetup.java for correlation demo. After Zoom meeting with Roger on 2025-11-01, determined that coordinated changes were needed to both CorrelateOp.java (the operation) and the demo files. Fixed by swapping operands in CorrelateOp.create() method and removing the input swap in ConvolveDemoSetup.java. This ensures consistency between the binary operation and the demo visualization while matching Roger's textbook notation. Date resolved: 2025-11-01

---

- ID: BUG-003
- Title: 2D chirp function default alpha parameter is incorrect
- Status: resolved
- Reported by: Roger Easton Jr
- Date: 2024-12-03
- Environment: All platforms, all versions
- Description: The default value for the alpha parameter in the 2D chirp function (and potentially other functions) is incorrect. The default should be the square root of the number of pixels to properly display the aliasing pattern. This issue had two parts: (1) the base default was 32 instead of 16, and (2) polar coordinate functions f(r)f(theta) were calculating width based on radial indices length (128) instead of full dimension (256).
- Steps to reproduce:
  1. Create a 2D chirp function using default parameters
  2. For polar coordinates: Create 2D complex chirp from menu, then select "Real and Imaginary Parts" and choose f(r)f(theta)
  3. Observe the alpha parameter value
- Expected behavior: Function should appear aliased with default alpha = sqrt(number of pixels) = sqrt(256) = 16 for all coordinate systems
- Actual behavior: (1) Initially showed default alpha = 32; (2) After first fix, polar coordinates showed alpha = 11.314 = sqrt(128)
- Logs / stacktrace: N/A
- Temporary workaround: Manually set alpha to sqrt(number of pixels)
- Notes / PR:
  - Part 1 fixed 2025-10-26: Changed default alpha from 32 to 16 in ChirpFunctionTerm1D.java and SineChirpFunctionTerm1D.java. Modified both param_defaults array and widthSpinnerModel.
  - Part 2 fixed 2025-11-05: For polar coordinate functions f(r)f(theta), radial indices only go from 0 to dimension/2 (128 for 256x256), but width calculation should still be based on full dimension (256). Added setDefaultWidthDimension() method to EditAnalyticFunctionTermPanel, EditAnalyticFunctionTerm1DPanel, and CreateAnalyticSubTerm1DPanel. Modified CreateFunctionTerm2DPolarPanel.setIndices() to call setDefaultWidthDimension(dimension) after setIndices(radialIndices). Added hasExplicitWidthDimension flag to EditAnalyticFunctionTerm1DPanel to prevent setDefaultWidth(true) from overriding the explicit dimension with indices.length. This ensures all polar coordinate functions (not just chirps) get correct default width parameter.
  - Date resolved: 2025-11-05

---

- ID: BUG-005
- Title: Real and imaginary parts not normalized to same scale in 2D export
- Status: resolved
- Reported by: Roger Easton Jr
- Date: 2024-12-03
- Environment: All platforms, all versions
- Description: When exporting 2D complex data, the real part and imaginary part should be normalized to the same range of gray values, but currently they are not.
- Steps to reproduce:
  1. Create a complex 2D function
  2. Export the data
  3. Check normalization of real and imaginary parts
- Expected behavior: Both real and imaginary parts should be normalized to the same scale/range
- Actual behavior: Real and imaginary parts use different normalization scales
- Logs / stacktrace: N/A
- Temporary workaround: Manually normalize after export
- Notes / PR: Fixed by adding shared normalization support. Modified ImageWriter.java to add overloaded writeImage() method that accepts explicit min/max values, and added calculateSharedMinMax() method. Modified SaveFunction2DDialog.java to detect when both real and imaginary parts are being exported and use shared min/max for normalization. Date resolved: 2025-10-26

---

- ID: BUG-006
- Title: Calculator strip thumbnail shows stale image after editing different term
- Status: open
- Reported by: julietfiss
- Date: 2025-01-16
- Environment: macOS, Java 11, main branch
- Description: In the 2D Function Calculator, when editing multiple terms in a combined expression, the calculator strip thumbnail for unselected terms shows stale cached images instead of refreshing to show the correct term's visualization.
- Steps to reproduce:
  1. Create a 2D function with two terms (term1 + term2)
  2. Select term2 and edit its parameters (e.g., change function type or parameters)
  3. Click on term1 in the calculator strip to select it
  4. Observe the thumbnail for term1
- Expected behavior: After clicking term1, its thumbnail should show term1's visualization (the function for term1)
- Actual behavior: The thumbnail for term1 shows term2's visualization (the previously edited term's cached image)
- Logs / stacktrace: N/A
- Temporary workaround: Unknown - may need to close and reopen the function editor
- Notes / PR: Root cause appears to be in the thumbnail caching mechanism. The cache invalidation doesn't properly track which term was edited vs which term is selected. When switching selection, the thumbnail renderer may be using a stale cached image from the previously active term. Related classes: `HorizontalThumbnailList`, `FunctionCalculator2D`, `CalculatorListItem`. This is relevant for the JavaScript port - need to ensure proper cache invalidation when implementing calculator strip in nuthatch-desktop.

---

- ID: BUG-007
- Title: ArrayMath.subtractMean() skips index 0 (off-by-one bug)
- Status: resolved
- Reported by: copilot (discovered during nuthatch-desktop port)
- Date: 2026-02-12
- Environment: All platforms, all Java versions
- Description: `ArrayMath.subtractMean()` has a loop that starts at `i = 1` instead of `i = 0`, so `subtracted[0]` is always 0.0 instead of `array[0] - mean`. The mean is computed correctly from all elements (including index 0), but the subtraction is not applied to the first element. This means SubtractMeanOp produces incorrect output at index 0 and the result does not truly have zero mean.
- Steps to reproduce:
  1. Create any signal (e.g., a ramp [2, 4, 6, 8])
  2. Apply SubtractMean operation
  3. Inspect output[0]
- Expected behavior: output[0] = array[0] - mean = 2 - 5 = -3, and the output sum should be 0
- Actual behavior: output[0] = 0.0 (Java zero-initialized array default), output sum = mean (nonzero)
- Logs / stacktrace: N/A — silent data corruption, no error thrown
- Temporary workaround: None
- Notes / PR: Found in `signals/ArrayMath.java`, method `subtractMean(double[])`. Fixed by changing `for (int i = 1; ...)` to `for (int i = 0; ...)` in `src/main/java/signals/operation/ArrayMath.java`. Added regression test `arrayMathSubtractMeanAllIndices()` in `src/test/java/signals/operation/UnaryOperations1DTest.java` to verify index 0 behavior and zero-sum output. The nuthatch-desktop (JavaScript) port already has the correct implementation starting at index 0. This bug affects both 1D and 2D signals since `SubtractMeanOp.create()` calls `ArrayMath.subtractMean()` independently on both the real and imaginary arrays. Date resolved: 2026-02-22.

---

- ID: BUG-008
- Title: Histogram.PDF() skips bin 0 (off-by-one bug)
- Status: resolved
- Reported by: copilot
- Date: 2026-02-22
- Environment: All platforms, all Java versions
- Description: `Histogram.PDF()` computes histogram counts correctly but fills `pdf[]` starting at `i = 1`, leaving `pdf[0]` at its default `0.0` even when bin 0 has data.
- Steps to reproduce:
  1. Use input values that populate bin 0 (e.g., `[0, 0, 1, 1]` with `numBins=2`, `min=0`, `max=2`)
  2. Call `Histogram.PDF(...)`
  3. Inspect `pdf[0]`
- Expected behavior: `pdf[0]` should equal `histogram[0] / input.length`.
- Actual behavior: `pdf[0]` is always `0.0` unless bin 0 truly has no samples.
- Logs / stacktrace: N/A — silent numerical error
- Temporary workaround: None
- Notes / PR: Fixed in `src/main/java/signals/operation/Histogram.java` by changing loop start from `i = 1` to `i = 0`. Added regression test `histogramPdfIncludesFirstBin()` in `src/test/java/signals/operation/UnaryOperations1DTest.java`. Date resolved: 2026-02-22.

---

- ID: BUG-009
- Title: ArrayMath.reverse2D() leaves first row/column zeroed
- Status: resolved
- Reported by: copilot
- Date: 2026-02-22
- Environment: All platforms, all Java versions
- Description: `ArrayMath.reverse2D()` starts reversal loops at index `1` in both direction branches, but unlike 1D reverse it does not explicitly preserve the first coordinate. This leaves the first column (x-direction reverse) or first row (y-direction reverse) at default zeros instead of copying original values.
- Steps to reproduce:
  1. Create a small nonzero 2D array with nonzero first row/column values
  2. Call `ArrayMath.reverse2D(input, xDim, yDim, true)` and `ArrayMath.reverse2D(input, xDim, yDim, false)`
  3. Inspect output first column / first row
- Expected behavior: First column (x reverse) and first row (y reverse) should be preserved, matching 1D reverse semantics for index 0.
- Actual behavior: First column / first row values become 0.0.
- Logs / stacktrace: N/A — silent data corruption
- Temporary workaround: None
- Notes / PR: Fixed in `src/main/java/signals/operation/ArrayMath.java` by explicitly copying preserved edge values (`output[j][0]` for x-reverse, `output[0][i]` for y-reverse). Added regression tests `arrayMathReverse2DXPreservesFirstColumn()` and `arrayMathReverse2DYPreservesFirstRow()` in `src/test/java/signals/operation/UnaryOperations1DTest.java`. Date resolved: 2026-02-22.
