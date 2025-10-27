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
- Status: open
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
- Notes / PR: Affects both 1-D and 2-D operations

---

- ID: BUG-003
- Title: 2D chirp function default alpha parameter is incorrect
- Status: open
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
- Notes / PR: May affect other functions as well - needs investigation

---

- ID: BUG-004
- Title: 2D export only supports TIFF or TXT formats
- Status: open
- Reported by: Roger Easton Jr
- Date: 2024-12-03
- Environment: All platforms, all versions
- Description: When exporting 2D data, the application currently only supports TIFF or TXT formats. CSV export would be more useful for data analysis.
- Steps to reproduce:
  1. Create a 2D function
  2. Attempt to export data
  3. Note available formats
- Expected behavior: Should support CSV export format for 2D data
- Actual behavior: Only TIFF or TXT export available
- Logs / stacktrace: N/A
- Temporary workaround: Export as TXT and manually convert to CSV
- Notes / PR: Feature request - add CSV export for 2D data

---

- ID: BUG-005
- Title: Real and imaginary parts not normalized to same scale in 2D export
- Status: open
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
- Notes / PR: N/A
