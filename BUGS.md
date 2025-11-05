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
