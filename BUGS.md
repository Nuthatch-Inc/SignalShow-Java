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
- Title: Two-term editing corrupts arithmetic symbols, thumbnails and parameters
- Status: resolved
- Reported by: julietfiss
- Date: 2025-01-16; reproduced and resolved 2026-10-12
- Environment: reproduced on macOS arm64, JDK 25, 421-test audit build
- Description: Removed 2D term editors kept receiving shared events. Initializing a new editor could replace the selected arithmetic symbol with an image and overwrite another term's parameters. The historical cache explanation was unconfirmed and is superseded by this reproduction.
- Steps to reproduce: Polar Rectangle (full width 64) → Add → Cartesian Gaussian; select and edit the earlier term.
- Expected behavior: The symbol, each term's parameters/thumbnails and combined samples remain consistent.
- Actual behavior before fix: The plus slot became an image; rectangle width reset; selecting the rectangle showed the Gaussian thumbnail; the equation could contain two rectangles.
- Fix: Scope events to the active term editor, publish only after initialization, and guard calculator updates by slot type. Preserve entered polar width on image dimension changes.
- Verification: Five Swing workflows pass (four failed before); full 426-test package passes. Native replay including independent edits and Plot/Edit preserves the expression; all 65,536 saved real samples match an independent oracle within 2.23e-16, imaginary samples remain zero. Windows untested.
- Evidence: [two-term audit](docs/audit/evidence/2026-10-12-two-term.md).

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


---

- ID: BUG-010
- Title: Rotated images break subsequent arithmetic and export
- Status: resolved
- Date: 2026-10-12
- Environment: Native macOS/JDK 25; production regression covers square and rectangular 2D images.
- Steps: Create a 256×256 image, Rotate it, then multiply by another 256×256 image.
- Actual behavior: AWT array-bounds exception, stale thumbnail, export dialog fails to open.
- Correction: `ImageDisplayMath.image2Data` now returns exactly the raster samples, removing the spurious extra element. Four regressions fail before and pass after; full 402-test package and native Roger export pass.
- Evidence: [Roger workflow](docs/audit/evidence/2026-10-12-roger-rotation.md).

---

- ID: BUG-011
- Title: Duplicated operation systems share editable parameters
- Status: resolved
- Date: 2026-10-12
- Steps: Duplicate a Threshold chain, change one copy's cutoff from 0.5 to 0.75, select the other copy.
- Expected: Each copy retains its own operation settings while linked source functions stay linked.
- Actual behavior: Both copies show 0.75. Both 1D and 2D clone methods share the operation list and rule.
- Evidence: [Native reproduction](docs/audit/evidence/2026-10-12-roger-rotation.md).
- Correction: Operation settings and ParameterBlock are copied; each clone owns its operation list, matching rule and input list, retaining linked producers. Four before/after regressions and native independent edits pass.
- Verification: [408-test package and native checks](docs/audit/evidence/2026-10-12-copy-layout.md).

---

- ID: BUG-012
- Title: Wide operation parameter row hides documentation in a smaller window
- Status: resolved
- Date: 2026-10-12
- Steps: Select Threshold in a 2D chain and resize to about 1153×742 logical points.
- Actual behavior: The long parameter row consumes the right-column width; operation documentation disappears. Previews and Threshold fields are visible after the earlier collapse fix.
- Expected: Keep parameters, previews, selector and operation documentation usable at normal desktop sizes.
- Evidence: [Native reproduction](docs/audit/evidence/2026-10-12-roger-rotation.md).
- Correction: Existing parameter descriptions and groups now stack above the scrollable previews. Two before/after Swing checks and native Threshold/Rotate controls/help at the smaller size pass.
- Verification: [408-test package and native checks](docs/audit/evidence/2026-10-12-copy-layout.md).

---

- ID: BUG-013
- Title: Normal complex-data files fail to reopen or lose their name
- Status: resolved
- Date: 2026-10-12
- Environment: Native macOS/JDK 25; production 1D/2D regressions.
- Reproduction: Save the built-in complex chirp and load its native .txt file: InputMismatchException; multiword names are parsed as numeric samples. Operation outputs also write an empty descriptor.
- Correction: Read the complete name line, preserve display names and restore loaded descriptors. Five before/after regressions and native save/load/resave pass.
- Evidence: [Persistence and insertion batch](docs/audit/evidence/2026-10-12-complex-insertion.md).

---

- ID: BUG-014
- Title: Adding an operation can make a chain its own input
- Status: resolved
- Date: 2026-10-12
- Environment: Native macOS/JDK 25; production 1D/2D regressions.
- Reproduction: Create Threshold(image), select its closing parenthesis and add a unary operation. The default source becomes the current chain; updates/export cause StackOverflowError.
- Correction: Pass the current owner to both calculators and filter owner/downstream dependencies from the shared input library. Eight before/after regressions and native insertion/operation/save pass.
- Evidence: [Persistence and insertion batch](docs/audit/evidence/2026-10-12-complex-insertion.md).

---

- ID: BUG-015
- Title: Profiles fails to open for ordinary rectangular images
- Status: resolved
- Reported by: Continuous Audit, native reproduction
- Date: 2026-10-12
- Environment: macOS arm64, JDK 25; 128×64 complex chirp
- Steps to reproduce: New 2D Complex Chirp → Width 128 → Height 64 → Profiles.
- Actual behavior before fix: ArrayIndexOutOfBoundsException; previous view remains displayed.
- Cause: ImageProfile.getYProfile interchanged x/y dimensions when locating the centered origin.
- Fix: Use each dimension for its own axis and preserve mathematical intercepts in profile labels.
- Verification: Four of eight focused fixtures failed before; all pass after. Full 434-test package passes. Native horizontal/diagonal complex profiles export and match the source exactly; all four channels work.
- Evidence: [Profiles audit](docs/audit/evidence/2026-10-12-profiles.md).

---

- ID: BUG-016
- Title: Histogram clips its baseline/last bar and can hide a nonzero imaginary channel
- Status: resolved
- Date: 2026-10-12
- Reproduction: Open Histogram for the ordinary rectangular complex chirp; horizontal labels and the last bar are clipped. A separate nonnegative imaginary channel containing zero is omitted.
- Correction: Fit complete bins and the zero baseline with label space; display sample-value ticks; identify channel presence from any nonzero sample.
- Verification: Two failures reproduced before; three focused checks and full 437-test package pass. Native 30/40-bin layouts are readable and update through the real input field.
- Evidence: [Histogram audit](docs/audit/evidence/2026-10-12-histogram.md).

---

- ID: BUG-017
- Title: Edited linked operations continuously regenerate unchanged outputs
- Status: resolved
- Date: 2026-10-12
- Reproduction: Edit a source feeding Plus → Threshold → Real, then read the result three times. Both 1D and 2D run 27 additions and 9 threshold passes instead of one each.
- Cause: Cached input IDs were never refreshed after recomputing an edited source; dirty checks also read each producer twice.
- Correction: Remember current input IDs after computing a result and evaluate each input once per dirty check.
- Verification: Two before/after production regressions, 14 focused ownership/insertion checks and 439-test package pass. Native linked multiplication/Real updates and operand replacement match every saved sample exactly.
- Evidence: [Linked-source audit](docs/audit/evidence/2026-10-12-linked-sources.md).

---

- ID: BUG-018
- Title: Reopened operation shows a selected input but an empty input picker
- Status: resolved
- Date: 2026-10-12
- Reproduction: Create A × B, switch to another document, return to Edit System and click its already-selected final input. No input choices appear until selecting a different term first.
- Correction: Present the loaded selection after initialization using the existing event path. Two before/after component checks and full 441-test package pass; native picker appears immediately and operand replacement/Plot/save succeed.
- Evidence: [Native reproduction](docs/audit/evidence/2026-10-12-linked-sources.md).

- Verification: [Reopen audit](docs/audit/evidence/2026-10-12-operation-reopen.md).

---

- ID: BUG-019
- Title: Duplicated images open empty editors and share editable parameters with the original
- Status: resolved
- Date: 2026-10-12
- Native reproduction: Duplicate a complex chirp; the copy initially has an empty editor. Plot → Edit Real Part, then increment its amplitude to 1.1. The original also changes to 1.1, while the copy loses linked real/imaginary complex editing.
- Source findings: Function clone methods share both parts and reset FunctionType/new-editor state.
- Evidence: [Image duplication investigation](docs/audit/evidence/2026-10-12-duplicate-images.md).

- Correction: Deep-copy editable definitions and preserve complex type/existing editor state. Eight before/after regressions and full 449-test package pass; native copy/original edits and all saved complex samples verify independence.

---

- ID: BUG-020
- Title: Resizing one operation input crashes thumbnail drawing or silently truncates data
- Status: resolved
- Date: 2026-10-12
- Reproduction: Multiply two images, enlarge the first while the second remains 128×64; TimesOp indexes beyond the second buffer during thumbnail painting.
- Correction: Validate binary operand grids, propagate unavailable results and keep editor/result consumers safe until compatible inputs return. Six focused checks, full 455-test package and native mismatch/replacement recovery pass. Direct native dropdown restoration remains unverified because typing did not commit.
- Evidence: [Linked-source resize audit](docs/audit/evidence/2026-10-12-resize-linked.md).

---

- ID: BUG-021
- Title: Rectangle text equation formats half-width as full-width denominator
- Status: open — deferred behind ordinary usage
- Date:2026-10-12
- Evidence: Native full width16 produces the correct width16 samples but displays RECT[x/8]. [Cartesian composition](docs/audit/evidence/2026-10-12-cartesian-composition.md).
- Scope: Equation text only; no numeric or UI correction attempted in this ordinary-usage batch.

---

- ID: BUG-022
- Title: Function equation text omits subtraction and appends a digit to multiplication
- Status: resolved
- Date: 2026-10-13
- Reproduction: Compose ordinary image terms with Subtract/Multiply; plotted equation prints AB ×1 C for A−B×C.
- Correction: Restore the subtraction symbol and remove the stray1 from multiplication in the shared formatter.
- Verification: Independent equation/arithmetic probe, full457-test package and native plotted expression pass.
- Evidence: [Equation symbols](docs/audit/evidence/2026-10-13-equation.md).


---

- ID: BUG-023
- Title: Ordinary JPEG loading fails and rectangular image imports can throw or clip data
- Status: resolved
- Date: 2026-10-13
- Reproduction: Load Roger's JPEG on JDK 25; obsolete JAI decoder fails. Load 192×128 PNG/text or 160×96 BMP; undersized square padding throws or loses pixels.
- Correction: Use JDK ImageIO at the native Load Image boundary, preserve raw grayscale/red-channel values, and round padding up to the next power of two.
- Verification: Six focused import/operation cases, full 463-test package, native JPEG/TIFF loading and every TIFF/threshold pixel pass.
- Evidence: [Image import audit](docs/audit/evidence/2026-10-13-image-import.md).


---

- ID: BUG-024
- Title: Imported-image edits are ignored and image previews can show old parameters
- Status: resolved
- Date: 2026-10-13
- Reproduction: Load a same-sized image, edit amplitude/position/size/crop; output and linked threshold retain original pixels. Image replacement can leave the prior preview, and translation rescales clipped images.
- Correction: Restrict raw-data reuse to identity transforms, initialize image origin/size consistently, scale before clipping and refresh the selected image preview.
- Verification: Nine before-failing regressions, full 472-test package and native exported amplitude/translation/resize/crop and threshold pixels pass.
- Evidence: [Imported-image editing audit](docs/audit/evidence/2026-10-13-import-edit.md).
