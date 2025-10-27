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
- Title: Cross-correlation operation is backwards (f2 correlated with f1 instead of f1 with f2)
- Status: resolved
- Reported by: Roger Easton Jr
- Date: 2024-12-03
- Environment: All platforms, all versions
- Description: The cross-correlation operations in both 1-D and 2-D (f "box" g) compute f2[x] correlated with f1[x] instead of the expected f1[x] correlated with f2[x]. This produces mathematically incorrect results.
- Steps to reproduce:
  1. Load two functions f1 and f2
  2. Select cross-correlation operation (f "box" g)
  3. Observe result
- Expected behavior: Should compute f1[x] correlated with f2[x]
- Actual behavior: Computes f2[x] correlated with f1[x] (backwards)
- Logs / stacktrace: N/A
- Temporary workaround: Swap the order of inputs (use f2 "box" f1 instead of f1 "box" f2)
- Notes / PR: Fixed in CombineOpsRule.java by swapping operand order in create() call (line 241). The stack pops operands in reverse order, so operand2 (second pop) is actually the first input. Changed from `operator.create(operand1, operand2)` to `operator.create(operand2, operand1)`. This fix affects all binary operations (1-D and 2-D). Date resolved: 2025-10-26

---

- ID: BUG-003
- Title: 2D chirp function default alpha parameter is incorrect
- Status: resolved
- Reported by: Roger Easton Jr
- Date: 2024-12-03
- Environment: All platforms, all versions
- Description: The default value for the alpha parameter in the 2D chirp function (and potentially other functions) is incorrect. The default should be the square root of the number of pixels to properly display the aliasing pattern.
- Steps to reproduce:
  1. Create a 2D chirp function using default parameters
  2. Observe the display
- Expected behavior: Function should appear aliased with default alpha = sqrt(number of pixels)
- Actual behavior: Function does not display properly with current default alpha value
- Logs / stacktrace: N/A
- Temporary workaround: Manually set alpha to sqrt(number of pixels)
- Notes / PR: Fixed in ChirpFunctionTerm1D.java and SineChirpFunctionTerm1D.java by changing default alpha from 32 to 16. Since the default dimension is 256, sqrt(256) = 16. Changed both the param_defaults array and the widthSpinnerModel. Date resolved: 2025-10-26

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
