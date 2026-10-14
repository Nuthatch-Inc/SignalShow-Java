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


---

- ID: BUG-025
- Title: CSV and text image exports duplicate the filename extension
- Status: resolved
- Date: 2026-10-13
- Reproduction: File → Export Function as Data or Image produces `.csv.csv` and `.txt.txt` names for ordinary complex images.
- Correction: Shared format detection accepts dotted chooser values and bare suffixes returned by Utils.
- Verification: Before-failing filename regressions, full 491-test package and native CSV/text exports pass.
- Evidence: [Native exports](docs/audit/evidence/2026-10-13-exports.md).

---

- ID: BUG-026
- Title: Joint export of negative real and imaginary channels uses an incorrect scale
- Status: resolved
- Date: 2026-10-13
- Reproduction: Export real −2 and imaginary −1 together; imaginary PNG is gray127 instead of white255.
- Correction: Initialize shared extrema correctly for signed values.
- Verification: Negative/positive PNG/TIFF production exports, full 491-test package, native pixel and numeric-file verification pass.
- Evidence: [Native exports](docs/audit/evidence/2026-10-13-exports.md).


---

- ID: BUG-027
- Title: Convolution demonstrations keep animation timers after leaving or completion
- Status: resolved
- Date: 2026-10-13
- Reproduction: Play a convolution demo and select another document; the old timer remains queued. Reaching the final frame also leaves the repeating timer running.
- Correction: Connect AnimatedPane to the existing view-cleanup contract and stop its timer at completion.
- Verification: Four before-failing 1D/2D lifecycle tests, full 495-test package, native timer-queue before/after and replay checks pass. Full panel reclamation remains unverified.
- Evidence: [Demonstration audit](docs/audit/evidence/2026-10-13-demos.md).


---

- ID: BUG-028
- Title: Failed complex-data saves close the dialog and can disable native menus
- Status: resolved
- Date: 2026-10-13
- Reproduction: On macOS/JDK 25, save to a missing folder, dismiss the error, and save to a valid path. All menus become disabled while the editor remains usable. Reproduced after a clean launch.
- Correction: Propagate write errors to SaveComplexDialog, parent its error message to that dialog, retain it on failure, and close file resources on all exits.
- Verification: Native retry, cancellation, subsequent save/export/open and replacement checks pass; two 1D/2D failure/retry regressions and the full 497-test package pass. Windows remains untested.
- Evidence: [File dialog audit](docs/audit/evidence/2026-10-13-dialogs.md).


---

- ID: BUG-029
- Title: Enlarged Constant images become a finite patch of the original size
- Status: resolved
- Date: 2026-10-13
- Reproduction: Create an Image Constant at 256×256, use it in multiplication, then enlarge source images to 512 or 1024. The Constant fills only its original 256-square extent; linked products contain large incorrect zero regions.
- Correction: Generate the amplitude at every requested sample, independent of the old image-editor extent and origin, as required by the existing constant definition.
- Verification: Four before-failing Cartesian/magnitude-phase/origin resize regressions; full 503-test package; native 256/512/1024 chains and uncentered 512×256 constant verified sample by sample.
- Evidence: [Memory and ordinary-size audit](docs/audit/evidence/2026-10-13-memory.md).

---

- ID: BUG-030
- Title: Deleted thumbnails and closed complex-save dialogs retain document data
- Status: resolved within the identified paths
- Date: 2026-10-13
- Reproduction: Delete all native test documents after creation, edits and saves; a renderer and disposed save-dialog controls still lead to document/result data in a collected heap.
- Correction: Release removed thumbnail references and clear each disposed complex-save dialog's function reference.
- Verification: Before-failing renderer deletion regression, surviving-row check, native ownership paths gone, and full 503-test package. Batch retained heap decreased from 618.2 to 484.8 MiB; remaining native accessibility ownership is BUG-031.
- Evidence: [Memory ownership audit](docs/audit/evidence/2026-10-13-memory.md).

---

- ID: BUG-031
- Title: Native accessibility keeps obsolete Swing editor models reachable during the audit
- Status: resolved within the matched Constant/Cylinder native workflow
- Date: 2026-10-13
- Reproduction: After the bounded image workflow and deletion of all five documents, six model objects remain strongly reachable through JNI-local accessibility roots (operation buttons and dimension combo boxes). Collected heap remains 484.8 MiB after BUG-030 cleanup.
- Scope: macOS/JDK 25 with CUA accessibility interaction. Manual-use and Windows behavior unverified; do not generalize to all environments.
- Resolution: Retired editor ownership and preview/calculator cleanup verified in the matched native create/edit/save/delete workflow: zero remaining documents, zero strongly reachable large double arrays and 20.1 MiB collected heap. Other families, manual use and Windows remain unverified.
- Follow-up evidence: [Completed native memory check](docs/audit/evidence/2026-10-13-preview-memory-resumed.md).
- Evidence: [Memory ownership audit](docs/audit/evidence/2026-10-13-memory.md).


---

- ID: BUG-032
- Title: Convolution demonstrations show wrong values and stop before completing the output
- Status: resolved within the tested scope
- Date: 2026-10-13
- Reproduction: In 2D, convolve a 32×16 Constant amplitude 2 with itself using the default normalization. Playback ends at amplitude 2048 with one black output pixel. Ordinary complex Gaussian/reference animations also disagree with spatial sums in both 1D and 2D.
- Correction: Sample the moving filter/reference at the correct coordinates, conjugate correlation references, apply the existing normalization, complete every sample, and honor wrapping in the final calculation. Correct the imaginary overlay and source-origin display.
- Verification: Twenty-four before-failing calculation cases plus two display checks; full 529-test package. Native constant playback now stays at 2; a saved complex sine/rectangle correlation matches all 256 independent sample sums after editing/reopening.
- Scope: Frequency filtering/location demos and complex normalization semantics remain separate; no Windows claim.
- Evidence: [Demonstration calculations](docs/audit/evidence/2026-10-13-demo-calculations.md).


---

- ID: BUG-033
- Title: Uncentered frequency demos filter the wrong frequency bins
- Status: resolved within tested low/high-pass scope
- Date: 2026-10-13
- Reproduction: Create a real uncentered 32×16 Cosine with period 16; Gaussian low-pass width 3 introduces an imaginary oscillation ±0.123760. 1D filtering can also return a wrongly centered result.
- Correction: Sample the filter using signed FFT-bin coordinates and preserve the source origin in both demonstration dimensions.
- Verification: Independent harmonic-gain tests for Ideal/Gaussian low/high-pass, both origins and two widths; native cosine gains and imaginary display; full 539-test package.
- Evidence: [Frequency demonstration audit](docs/audit/evidence/2026-10-13-frequency.md).

---

- ID: BUG-034
- Title: DC-only selection produces a blank frequency reconstruction
- Status: resolved
- Date: 2026-10-13
- Reproduction: In Frequency Locations, select DC Component and deselect Low/Mid/High. A Constant amplitude 2 disappears.
- Correction: Keep DC and non-DC low-frequency contributions independent; retain the source origin in spatial previews.
- Verification: Both-origin band/DC component checks and native centered/uncentered Constant reconstructions; full 539-test package.
- Evidence: [Frequency demonstration audit](docs/audit/evidence/2026-10-13-frequency.md).

---

- ID: BUG-035
- Title: Uncentered RGB image previews use an invalid scale
- Status: resolved
- Date: 2026-10-13
- Reproduction: The uncentered Fourier/filter preview displays a largely white image with color bounds −1/−1.
- Correction: Enable RGB autoscaling independently of the origin, as for grayscale images.
- Verification: Three before-failing display-bound checks, native 0–1 frequency preview and uncentered DC color reconstruction; full 539-test package.
- Evidence: [Frequency demonstration audit](docs/audit/evidence/2026-10-13-frequency.md).

---

- ID: BUG-036
- Title: Frequency demonstration settings reset when switching documents
- Status: resolved within filtering and frequency-location scope
- Date: 2026-10-13
- Reproduction: Set Gaussian Band Pass amplitude .7, center 4, separation 12 and width 8. Visit the source and return: it becomes Ideal Low Pass and the output changes. Frequency Locations also resets cutoff choices.
- Correction: Store compact settings on the document, restore them into fresh views using current source data, and independently copy settings during duplication.
- Verification: Six before-failing both-origin 1D/2D EDT cases, nine native 2D stages including source edits/duplication; full 559-test package passes.
- Scope: Other demonstration types, native 1D revisit and Windows require separate checks.
- Evidence: [Frequency settings audit](docs/audit/evidence/2026-10-13-demo-settings.md).

---

- ID: BUG-037
- Title: Sampling reconstruction omits negative frequencies and uses the wrong origin
- Status: resolved within the tested sampling workflows
- Date: 2026-10-13
- Reproduction: Uncentered cosine period16 sampled every4 pixels reconstructs with half its real amplitude and a false imaginary wave. A different global creation origin can also shift the result.
- Correction: Build the mask using signed FFT bins and the actual spectrum origin.
- Verification: Eight EDT cases, seven before-failing; focused28/full567 pass. Native default/edited complex reconstruction, origin/source edits and duplication verified.
- Evidence: [Sampling audit](docs/audit/evidence/2026-10-13-sampling.md).

---

- ID: BUG-038
- Title: Default sampling reconstruction returns zero
- Status: resolved within the tested sampling workflows
- Date: 2026-10-13
- Reproduction: Enable reconstruction at the initial sampling period1 before changing any fields; its gain is zero.
- Correction: Initialize reconstruction gain from the current sampling period.
- Verification: Eight EDT cases, seven before-failing; focused28/full567 pass. Native default/edited complex reconstruction, origin/source edits and duplication verified.
- Evidence: [Sampling audit](docs/audit/evidence/2026-10-13-sampling.md).

---

- ID: BUG-039
- Title: Sampling demonstration resets settings on document changes
- Status: resolved within the tested sampling workflows
- Date: 2026-10-13
- Reproduction: Change period, reconstruction width and channel; visit the source and return. The controls reset to defaults.
- Correction: Retain compact model settings, copy them independently on duplication and recompute fresh views from current input.
- Verification: Eight EDT cases, seven before-failing; focused28/full567 pass. Native default/edited complex reconstruction, origin/source edits and duplication verified.
- Evidence: [Sampling audit](docs/audit/evidence/2026-10-13-sampling.md).


---

- ID: BUG-040
- Title: Default rotation fails on rectangular images
- Status: resolved within the tested rectangular-image workflows
- Date: 2026-10-13
- Reproduction: Create a128×64 Gaussian and apply Rotate at its default90°. JAI throws a Crop exception, the result disappears and result export is disabled.
- Correction: Copy the rotated overlap into the original canvas and zero-fill uncovered pixels for both complex channels.
- Verification: Three before-failing ordinary cases, independent geometry and editable-chain regressions; focused22/full578 pass. Native90°/180° and30° nearest-neighbor chains, source edits and12 after-fix exports all pass.
- Evidence: [Rectangular rotation audit](docs/audit/evidence/2026-10-13-rotation.md).

---

- ID: BUG-041
- Title: Loaded and exported 2D samples open with empty editors
- Status: resolved within the tested sampled-image workflows
- Date: 2026-10-13
- Reproduction: Load a saved image or Export Result as Data; its first real/imaginary editor has no Data term or editing controls despite populated pixels.
- Correction: Mark populated2D factory results as existing and use the toolbar's normal content attachment path.
- Verification: Six before-failing first-opening EDT cases, focused23/full584 pass; native loading, both exported editors, gain edits, live chain and detached copy independently verified.
- Evidence: [Directional/reuse audit](docs/audit/evidence/2026-10-13-directional-reuse.md).

---

- ID: BUG-042
- Title: Corner-origin polar images misalign angular and radial factors
- Status: resolved within tested polar creation and arithmetic workflows
- Date: 2026-10-13
- Reproduction: Compose radial Gaussian and angular Cosine, uncheck Zero-Centered; the angular samples use a different row from the radius and the error reaches Divide/Minus results.
- Correction: Calculate each corner-origin angle at the same bottom-left pixel coordinate as radial and Cartesian generation.
- Verification: Five before-failing cases across normal square/wide/tall images and editable complex chains; focused171/full592 pass. Eleven native after-fix exports, both origins and phase edits, independently pass.
- Evidence: [Polar arithmetic audit](docs/audit/evidence/2026-10-13-polar.md).

---

- ID: BUG-043
- Title: Complex powers discard the input phase
- Status: resolved within ordinary nonzero power workflows
- Date: 2026-10-13
- Reproduction: Create a radial complex exponential, apply Exponent2; the result becomes constant real magnitude squared. Fractional/complex powers and downstream reciprocals are also wrong.
- Correction: Include input phase in the complex logarithm used by exponentiation; preserve the separate magnitude-logarithm/cepstrum behavior.
- Verification: All14 new1D/2D cases fail before/pass after; focused56/full606 pass. Nine native after-fix source/power/reciprocal exports independently pass.
- Evidence: [Power and reciprocal audit](docs/audit/evidence/2026-10-13-power.md).


---

- ID: BUG-044
- Title: Dots in folder names redirect saves and collapse distinct output names
- Status: resolved within verified save and export path handling
- Reported by: Codex during authorized release smoke testing
- Date: 2026-10-13
- Environment: macOS arm64, JDK25, packaged1.2.5 candidate
- Description: Saving `gaussian` and `intensity` under `/private/tmp/signalshow-1.2.5-home/` wrote both to `/private/tmp/signalshow-1.2.txt`; the second save replaced the first. The helper treated a dot in the directory as the filename extension.
- Expected behavior: Preserve the selected directory and distinct basenames, then apply the requested output suffix.
- Fix: Restrict extension detection/removal to the final filename after either path separator. The shared helper serves complex saves, component/image exports and image-format replacement.
- Verification: All four regression path cases fail before the fix;18 focused filename, image-export and complex-data checks pass afterward. Windows-style strings are unit checks, not a native Windows acceptance pass. Full release suite610 passed. Native bundled-runtime retest created two distinct files in the requested dotted directory; all131,072 saved complex samples match independent Gaussian and intensity formulas. The unintended prior output remained byte-identical. See docs/audit/evidence/2026-10-13-release.md.


---

- ID: BUG-045
- Title: TIFF and BMP exports keep output files locked on Windows
- Status: resolved; Mac and Windows release CI verified
- Reported by: GitHub Windows regression job during1.2.5 release
- Date: 2026-10-13
- Environment: Windows GitHub runner, Temurin25.0.4
- Description: Three existing workflow cases could not remove exported TIFF files during cleanup. JAI FileStore owns an output stream until its returned rendered operation is disposed; the application discarded that operation without disposing it. BMP uses the same path.
- Fix: Dispose the completed FileStore operation after TIFF/BMP encoding, preserving the existing codecs and normalization.
- Verification: Add repeated ordinary export→rename→pixel-readback→delete checks for both formats. Three before-failing Windows TIFF cleanup cases now pass. Focused16 and full612 local tests pass; the corrected Mac and Windows CI jobs pass all612 tests and final-installer payload inspection. Windows interactive usage remains deferred. See docs/audit/evidence/2026-10-13-publication.md.


- ID: BUG-046
- Title: Vertical image reversal shifts the coordinate origin by two pixels
- Status: resolved; automated mathematical verification passed
- Reported by: source-led mathematical audit
- Date: 2026-10-14
- Environment: macOS arm64, JDK 25, headless production operation classes
- Description: Reverse Y negated top-to-bottom array row indices rather than upward physical y coordinates. A point at y=0 moved to y=-2 (cyclically), and an entire normal image was shifted after reflection. Both channels and both origin modes were affected. Earlier usage evidence verified the array rule, not its mathematical meaning.
- Fix: Reverse in the existing upward-y array representation and restore image row order afterward. Reverse X remains unchanged.
- Verification: Eight independent rectangular-coordinate and origin cases failed before the change. All 32 new coordinate/spectral checks pass, including fixed origins and agreement with two unitary Fourier transforms. The complete 664-test package passes after correcting the old top-row reversal expectation. See docs/audit/evidence/2026-10-14-math.md for scope.


- ID: BUG-047
- Title: Hologram encoding corrupts its source and uses incorrect phase/error quantization
- Status: resolved; scoped automated production checks pass
- Date: 2026-10-14
- Description: Encoding overwrites the source magnitude/phase arrays. A normal authored +pi phase selects an out-of-range column and crashes; seven steps are used for eight periodic phase columns, and error diffusion loses the complex phase error. The duplicate Phase Detour encoder shares the math defects.
- Fix: One read-only shared encoder with eight wrapped phase bins, bounded aperture heights and actual complex residual feedback. Both operations use it.
- Verification: 12 of 14 final cases fail on original code (including two exceptions); all 14 pass after correction. Optical first-order and enumerated-quantizer references are independent; a separate current Chirplet comparison agrees on 65,536 binary pixels. No native acceptance claimed. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-048
- Title: Cepstrum reverses the imaginary result for complex inputs
- Status: resolved; automated 1D/2D production and chain checks pass
- Date: 2026-10-14
- Description: The second transform was forward instead of inverse. Normal asymmetric complex signals/images returned a conjugated magnitude cepstrum; inverse chains consequently reconstructed the wrong spectrum. Real-only examples could hide the error.
- Fix: Use the standard inverse transform after the log-magnitude spectrum. Express inverse mode consistently as IFFT(exp(FFT(input))); correct the help's unsupported phase-unwrapping claim. Existing zero-bin handling remains.
- Verification: 12 forward/chain cases fail before the fix; all 28 kernel cases and 16 transform regressions pass. Direct DFT sums independently check directions, scales and both origins. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-049
- Title: Editing the Poisson noise Seed field breaks subsequent generation
- Status: resolved; actual 1D/2D editor regressions pass
- Date: 2026-10-14
- Description: The Poisson type model inherited a decimal type for its Seed field. Typing an ordinary integer stored Double, and generation threw ClassCastException when reading Integer.
- Fix: Declare the two Poisson parameter types explicitly as Double and Integer. The 2D wrapper inherits the correction.
- Verification: Five original-code editor cases throw; all 37 noise distribution/editor cases and 11 copy/histogram regressions pass after correction. Two seed edits, complex Intensity chains and copies are checked. Headless Swing evidence only. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-050
- Title: Polar Delta and Delta Pair images place impulses at unrelated coordinates
- Status: resolved; ordinary 1D and complex polar-image checks pass
- Date: 2026-10-14
- Description: All three impulse generators treated centers as array offsets even when polar creation supplied nonuniform radii/angles. Radius 5 could select radius sqrt(8), and an angular impulse could become one unrelated pixel.
- Fix: Evaluate every supplied physical coordinate against the requested impulse locations; retain the pair sign convention and discrete point-support definition.
- Verification: All 24 original polar cases fail; all 36 new coordinate/chain cases and 46 polar/1D regressions pass (82 total). Both origins and rectangular shapes covered. No native UI check claimed. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-051
- Title: Poisson Process produces too few events and incorrect spacing at normal rates
- Status: resolved; ordinary rate and complex image-chain checks pass
- Date: 2026-10-14
- Description: Integer Poisson waiting gaps plus one gave the wrong occupancy and adjacent-bin dependence. Rate 1 filled about 50% of samples instead of the correct 63.2%; rate 2 filled 66.5% instead of 86.5%.
- Fix: Directly sample independent unit-bin Poisson occupancy using probability 1-exp(-rate), retaining one amplitude per occupied sample and deterministic seeded generation. Old seed patterns necessarily change.
- Verification: Three of 12 original cases fail; all 12 pass after correction, including distribution/independence checks, rate edits in complex 2D chains, copying, and separate random-phase images. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-052
- Title: Rectangular and E apertures fill an extra border outside their requested bounds
- Status: resolved; independent complex image geometry and chain checks pass
- Date: 2026-10-14
- Description: The rectangle helper filled its expanded search bounding box without a membership check. Normal half-width 8 / half-height 5 produced 247 pixels instead of 187, with further asymmetry at fractional centers. The E generator shares this helper.
- Fix: Include only pixel centers within the requested half-extents using ceiling/floor bounds. Preserve the established half-extent convention and document it in the two help pages.
- Verification: Eight original rectangle/E cases fail. All 16 new aperture cases and 12 existing generator regressions pass; both rectangular orientations/origins, both complex channels, fractional center edits and Intensity chains covered. Multiarm and annular multiarm independent checks also pass. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-053
- Title: Division uses addition precedence in mixed calculator expressions
- Status: resolved; independent 1D/2D complex expression checks pass
- Date: 2026-10-14
- Description: DivideOp declared addition/subtraction priority. The actual calculator consequently evaluated A+B/C as (A+B)/C and A/B*C as A/(B*C), producing substantial wrong answers with ordinary inputs. Earlier grouped tests supplied their own correct priority tokens and missed this metadata error.
- Fix: Give division multiplication/division priority, as documented by CombineOpsRule and used by Chirplet.
- Verification: 12 of 40 expression cases fail on original metadata; all 40 pass after correction. Tests use actual default operation metadata, independent polar complex arithmetic, grouping, left associativity and linked Intensity in 1D/2D with both origins. Another 16 operation-selection/complex regressions pass. See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-054
- Title: Moving scaled 1D data off the viewport squeezes the whole signal into view
- Status: resolved; ordinary sampled complex signal checks pass
- Date: 2026-10-14
- Description: Scale-and-zero-pad clipped the placement bounds before resizing. Normal center edits consequently compressed all input samples into the remaining visible interval, and the exclusive right bound incorrectly discarded the last canvas sample.
- Fix: Resize to the authored width first, then copy only the visible part using an exclusive canvas-end bound. Existing crop mode and half-width convention remain unchanged.
- Verification: Four original scale/pad cases fail. All eight new 32/64-sample, both-origin, crop/pad cases pass with independent ramp interpolation and opposite channel shifts, width edits, amplitudes and Intensity. Seven persistence and 16 coordinate regressions also pass (31 total). See docs/audit/evidence/2026-10-14-math-continuation.md.


- ID: BUG-055
- Title: Bar Chart always enlarges its 128-pixel resource and loses higher-resolution detail
- Status: resolved; resource pixel and complex chain checks pass
- Date: 2026-10-14
- Description: The resolution selector had a three-argument signature that did not override the two-argument production resize method. Normal 256/512 creations consequently enlarged the 128 image instead of using the supplied detailed chart; the unreachable old path also bypassed sample normalization.
- Fix: Override the actual resize method, select the supplied resource using the existing size rule, refresh the cached image when selection changes, and use the shared normalized loader.
- Verification: Four original 256/512 cases fail against independent PNG decoding. All 30 sampled-image cases pass after correction: nonconstant rectangular scaling/placement, eight packaged images, phase images in magnitude/phase chains, resolution edits, amplitudes and Intensity. Another 165 creation/import-edit regressions pass (195 total). See docs/audit/evidence/2026-10-14-math-continuation.md.
