# Native source replacement — October 13, 2026

macOS arm64/JDK 25, efebd69 production code in the 484-test executable. No code
changed or numerical suite rerun in this native-only batch.

Create a 256×256 centered complex source: real Cartesian Gaussian with amplitude
1.5, center −12 and width32; imaginary Image Constant0.25. Connect Squared
Magnitude, then Threshold0.5. Save the source and both results at each stage.

1. Replace Gaussian with Rectangle in the picker. Set amplitude−0.75, center24,
   displayed full width64. The imaginary channel remains0.25.
2. Delete the real term, choose New Term → Image → Gradient, amplitude2.
3. Delete/replace with Aperture → Cylinder, amplitude−1.25, center(16,−20), radius40.
4. Delete/replace with Noise → Uniform Noise, mean0.5, half-width0.5, seed17.
5. Delete/replace with Polar → Gaussian, radial center48 and width20; angular
   factor remains Constant1.

All18 exported arrays pass per-sample checks. The Gaussian, Rectangle, Gradient,
Cylinder and polar Gaussian sources match independent formulas; every imaginary
sample remains0.25. The noise source is checked only for finite values in[0,1],
not distribution quality. All six squared-magnitude results equal r²+i², and
all masks equal1 when that value≥0.5, otherwise0. Both operation imaginary
channels are zero. The log contains no exceptions.

Removing and replacing the real term keeps the existing source document and
both links usable. Returning to the editor does not alter the saved values.
The final screenshot shows the current ring mask and accessible Threshold fields.

The accessibility stepper's parent value can lag its editable child's displayed
text after generator replacement. For example the parent reported32 while the
visible Gaussian field and expression both showed16; saved samples matched16.
This was not a confirmed product wrong-result bug. Read the child field and
inspect the actual output before drawing conclusions from accessibility metadata.

Scope: representative family transitions at ordinary size, including negative
amplitudes/centers. Not every generator, drag interaction, invalid input, capacity
or Windows path is certified. The single app remains open for the next active
File export test; no second instance was launched.
