# Complex-data persistence and operation insertion — October 12, 2026

Continuation of ordinary image/operation usage on macOS arm64, Oracle JDK 25+37.
No Windows verification, commit or distribution.

## BUG-013: ordinary Save Complex Data / Load Complex Data failure

Native reproduction: create the built-in 256×256 Complex Chirp, save it, then load
`complex chirp.txt` through the File menu. The file is valid output from this app,
but the reader consumes only `complex` as its name and tries to read `chirp` as a
number. An AWT InputMismatchException follows; no image is added. This is an
ordinary default, not a malformed-file test. The writer also wrote an empty name
for operation results, whose display names live in their custom equations, and
loaded names did not populate the editable Function Name field.

ComplexIO now reads the whole name line, writes the current compact display name,
restores the loaded descriptor and closes its Scanner. The file layout is unchanged.
Five regressions fail before and pass after: 64-sample complex 1D and 32×16 asymmetric
complex 2D in both origin modes, plus existing single-word names. The 2D cases
apply Threshold/Real/Times to reopened data and save/reopen/resave an operation
result, comparing both sample arrays and names exactly.

## BUG-014: inserting an ordinary operation makes the chain its own input

After reopening the native chirp, create Threshold(source). Select its closing
parenthesis and press New Unary Operation. The default input selector chooses the
most recent usable item, now the current chain itself. Subsequent selection of
Real and Save Complex Data triggers repeated AWT StackOverflowError in
OperatorSystem.getFunction/checkDirtyInputs. The process remains alive with a
broken expression and stale preview.

Both dimensional calculators now receive their owning system and use the same
filtered library as the input picker. That library excludes the owner and chains
that depend on it, inspecting links without evaluating them. This also protects
ordinary returns to an earlier chain after creating downstream results. Eight
production Swing/ownership tests fail before and pass after: New Term, New Unary,
New Binary in both dimensions, and selecting inputs for an earlier chain with
later consumers. Tests complete inserted terms with a binary operator before
asserting evaluable output. An incomplete expression may legitimately have no
output; it must not manufacture a self-reference.

## Native after-check and independent stored-sample comparison

Built JAR: 421 tests pass (full offline Maven package). Native actions:
1. Load the previously failing chirp file; inspect Plot’s four channels and the
   preserved Function Name/dimensions/origin.
2. Create Threshold(source), append Real(source) through the previously crashing
   insertion, and insert Times between them to complete the expression.
3. Save `threshold(complex chirp) x Re{complex chirp}` to `operated result.txt`.
4. Reopen, inspect all four Plot panels and resave to `operated result reopened.txt`.
5. Close the window and verify process exit; only one test instance was used.

An independent Python comparison of the native saved files checks every one of
65,536 values in each channel. Expected real = threshold(real) × real; expected
imaginary = threshold(imaginary) × real, using the visible 0.5 cutoff and 0/1 bounds.
Both maximum absolute errors are 0; reopen/resave preserves all samples, name,
dimensions and origin exactly. The final native log has only configuration messages.
See the adjacent verification JSON and before/after logs.

Native coverage is 256×256, zero-centered. The attempted AX selection of 32×16
combo entries did not commit; the actual stored header was inspected and records
256×256. Do not claim native rectangular coverage. Rectangular/both-origin and
1D persistence are production-path checks; their native variants remain pending.

## Documented capability boundary

Save Complex Data stores dimensions, origin, name and real/imaginary samples.
Reopening creates sampled data; it does not restore generator controls, source
links or the editable operation chain. Preserve the exported samples as the
supported round trip and scope editable-project persistence separately. Operation
results still require an explicit base filename in the save dialog; the default
field may contain only the current directory. This usability gap remains pending.
