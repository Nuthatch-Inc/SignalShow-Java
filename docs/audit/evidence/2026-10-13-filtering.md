# Ordinary filtering and complex representation — October 13, 2026

Continuous Audit; local commit authorized, no push. macOS arm64, Oracle JDK 25,
JAI 1.1.3. Production code unchanged from efebd69; twelve new workflow regressions.

## Production workflows

`FilteringChainWorkflowTest` exercises actual FunctionFactory and OperatorSystem2D
callers at 32×16 and 16×32, both origin settings. Convolve/Correlate → Real →
Threshold(0.5) follows source amplitude edits and smoothing-kernel replacement.
Each intermediate filter result is compared with direct circular spatial sums;
the mask, dimensions, source preservation and unchanged-result cache are checked.
Forward and inverse FFT use default √N normalization. The forward result has an
independent direct DFT reference, so a cancelling round-trip error cannot pass.
Complex source edits propagate through both operations. All twelve new tests
pass; the full package passes **484 tests, zero failures/errors/skips**.

## Native workflows

One SignalShow app, normal 256×256 centered images. Create A as real Constant 3
and imaginary Constant 4. Create B as magnitude Constant 5 and phase Constant 0.927,
then Real(B) → Threshold 0.5. Editing B's phase to 2.214 updates the real output
and changes the mask from 1 to 0. Seven exported arrays match calculations using
the committed phases.

The requested phases were atan2(4,3) and atan2(4,−3). The numeric editor commits
three decimal places, so the accepted values are 0.927 and 2.214. This is a bounded
ordinary representation/edit pass, **not exact Cartesian/polar equivalence**.
The precision limit is recorded and deferred under Juliet's priorities.

Construct Convolve(A,B), switch to Correlate(A,B), then append forward FFT and
inverse FFT (√N for both). Change A's real amplitude from 3 to 6. Eight exports
match independent constant-field convolution/correlation sums and the centered
DC transform, including edited-source propagation through all three operations.
Maximum relative complex error is 2.47e−13; no numerical repair is needed.

The new unary system chose the latest available input, Correlate(A,B). An initial
verification script incorrectly assumed the previously clicked source A would
be inserted. Saved descriptors and results identify the actual chain as
FFT(Correlate(A,B)); the oracle was corrected before recording a pass. Always
inspect the inserted operand. This is not evidence of a production math defect.

## Input and cleanup

After the earlier input-routing failure, a fresh app accepted parameter, name
and filename edits with explicit focus and readback before Return. Juliet's
subsequent request to check typing was verified in another fresh app: type
Constant amplitude 2.5, commit, inspect expression, save a typed filename and
confirm all 65,536 complex samples equal 2.5 + 0i. Intermittent routing remains an
automation limitation; this does not establish its underlying cause is fixed.

Both apps exited, with a process check confirming zero remaining test instances.
The bounded log contains only configuration load/save messages, no exceptions.
Screenshots, export hashes, independent oracle and package logs accompany this
record. Windows, other FFT normalizations, larger workloads and demonstration
controls remain separate coverage gaps. No production UI wording changed.
