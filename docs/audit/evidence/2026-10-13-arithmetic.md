# Native complex-image arithmetic and linked threshold — October 13, 2026

Environment: macOS arm64, JDK 25, package at efebd69 (472 passing tests).
One isolated native SignalShow instance; CUA clicks and verified typing only.
No source changes were needed in this batch.

## Ordinary workflow

1. Create centered 256×256 source A (`f0`): Image Gradient real part, amplitude 1;
   Image Constant imaginary part, amplitude 0.25. Independent samples are
   `column / 255 + 0.25i`.
2. Create source B, name it `B`: Image Constant real amplitude 2 and imaginary
   amplitude 1. Samples are `2 + i`.
3. Create a mathematical system, add a binary operation, select A then B with
   the input picker. Choose Plus, Minus, Times and Divide in turn, saving each
   result through File → Save Complex Data. Inspect the quotient's four-channel
   Plot and return to Edit System.
4. Create a second system applying Threshold to the quotient, set threshold 0.2
   (Maximum 1, Minimum 0), and save. Edit B's real amplitude to4, save B, the linked
   quotient and the linked threshold. Both systems update without manual rebuild.
5. Return to the quotient editor, replace its first input with B and second with
   A; save B/A and its downstream threshold. This exercises noncommutative operand
   replacement with ordinary nonzero complex data.
6. Select the binary operator, type `div` into its search field and verify only
   Divide appears. Replace the query with `plu`, select Plus and save B+A. Clear
   the query and verify all six binary choices reappear. Exact field text was
   read back before proceeding. Minus and unmatched-query native search remain
   pending; their component tests are separate evidence.
7. Close the app. Process inspection confirms no audit SignalShow instance remains.

## Results and limits

All 13 exported files (851,968 complex pixels in total) agree with independent
Python complex arithmetic and per-channel threshold comparisons. Largest complex
error is 3.98e-15, ordinary floating-point rounding; threshold arrays match exactly.
Initial A/(2+i) threshold has 40,960 nonzero real samples; editing B to 4+i changes
that to 14,080. Reversing to B/A gives 65,536 nonzero real threshold samples. Imaginary
threshold outputs are zero in these selected ranges.

The source exports also match independent Gradient/Constant definitions. Both
channel previews, Plot, expression symbols, source replacement and linked results
remained usable. The bounded native log contains configuration messages only,
without exceptions, crashes or errors. The native search screenshots show the
filtered list and recovery. No full test rerun was needed for this evidence-only
batch; the preceding 472-test package remains the executable baseline.

Scope is centered 256×256 complex images, four basic arithmetic choices and linked
Threshold 0.2. Convolution/correlation, more generator families, other dimensions,
Windows and memory capacity are not certified by this check. Raw exports remain
in the isolated `/private/tmp/signalshow-roger-home/`; the verification JSON records
sample counts, errors and SHA-256 hashes rather than checking large arrays into Git.
