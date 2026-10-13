# Ordinary spatial convolution, correlation and FFT — October 13, 2026

Native macOS arm64/JDK25 audit of the 567-test package at 8334dd9. No product code changed in this batch. All 12 complex-data exports (98,304 complex samples) match independent references. Maximum complex error is 1.04e-11 or less, well inside the stated comparison tolerance. This is scoped evidence for normal centered 128×64 images, not all transforms or platforms.

## Workflow and inputs

Create a Cartesian Gaussian real image with x-factor amplitude1.5, center−6, width12, order2, and y-factor amplitude1, center3, width8, order2. Add a constant imaginary image of0.25. Create a second real Cartesian rectangle centered at(10,−5) with editor full widths8×4 and unit amplitude; imaginary part is zero. Rectangle boundaries have half weight and the total mask mass is32.

Insert a binary operation, explicitly select Convolve, then select Gaussian as its first operand and rectangle as the second. Save the result. Replace Convolve with Correlate and save. Chain a forward FFT and an inverse FFT, both using the visible square-root-N normalization; verify the inverse checkbox. Save both. Edit the Gaussian real amplitude to2, leaving its imaginary channel unchanged, and save the refreshed source, correlation, forward and inverse results. Replace Correlate with Convolve again and save both that result and the downstream inverse. Plot the final result and inspect both channels.

The Gaussian peak is(−6,3), convolution peak(4,−2), and correlation peak(−16,8). The convolved imaginary channel is8. The edited final real peak is53.195 to displayed precision. All typed values read back correctly; the live edit and operation replacement propagated through the chain. No application crash or unresponsive control was observed. Close the single application and verify process exit.

## Independent checks

The replayable Python verifier reconstructs both analytic sources, evaluates direct circular spatial sums, and uses separable DFT matrices for the forward result. It does not use an FFT library as its oracle. It checks every real/imaginary sample, dimensions, origin and export hashes. The inverse result is compared to the independently computed spatial result. NumPy2.4.1; tolerance1e-9+1e-10 times the expected peak.

Run `python3 docs/audit/evidence/2026-10-13-spatial-verifier.py docs/audit/evidence` to verify the compressed native files. The accompanying JSON records all12 checks, SHA256 hashes, channel ranges and peak coordinates. Screenshots and AX records preserve operation/control observations.

## Observed nonblocking accessibility exceptions

The bounded native log contains six identical JDK accessibility NullPointerExceptions from `AccessibleHTML$IconElementInfo$IconAccessibleContext.getAccessibleIconDescription`, calling `ImageView.getAltText()` through a null view. Stack frames pass through macOS CAccessibility and the AWT event dispatch thread, with no application-code frame. The app remained responsive and all saved calculations passed. The exact triggering UI action was not isolated. Track this help-image accessibility limitation separately; no speculative workaround or claim of an exception-free run.

Windows, other interpolation/transform settings and long manual sessions remain unverified. Legacy text descriptors do not distinguish Convolve/Correlate or forward/inverse FFT; actual operator selections, inverse checkbox and independent arrays establish which operations ran. No change to those descriptions in this batch.
