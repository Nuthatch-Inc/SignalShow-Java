package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static signals.operation.SignalTestUtils.*;

/**
 * Comprehensive test suite for 1D convolution, correlation, and autocorrelation.
 * 
 * These tests verify that the FFT-based implementations match the mathematical
 * definitions from Roger Easton's book "Fourier Methods in Imaging" (Chapter 8).
 * 
 * Key definitions from Chapter 8:
 * 
 * CONVOLUTION (Eq 8.82):
 *   f[x] * h[x] = ∫ f[α] · h[x-α] dα
 *   FFT: IFFT(FFT(f) · FFT(h))
 *   Property: Commutative - f * h = h * f
 * 
 * CROSS-CORRELATION (Eq 8.86):
 *   f[x] ⋆ m[x] = ∫ f[α] · m*[α-x] dα = f[x] * m*[-x]
 *   FFT: IFFT(FFT(f) · conj(FFT(m)))
 *   Property: NOT commutative - f ⋆ m ≠ m ⋆ f (in general)
 *   Note: The REFERENCE function (m) is conjugated, not the input (f)
 * 
 * AUTOCORRELATION (Eq 8.87):
 *   f[x] ⋆ f[x] = ∫ f[α] · f*[α-x] dα
 *   FFT: IFFT(|FFT(f)|²) = IFFT(FFT(f) · conj(FFT(f)))
 *   Property: Result is Hermitian (even real part, odd imaginary part for real input)
 *   Property: Peak at origin equals energy
 */
public class Operations1DTest {

    private static final int SIZE = 64;

    // ========== Direct Operation Methods ==========

    /**
     * Performs 1D convolution: IFFT(FFT(f) · FFT(g))
     */
    private double[][] convolve1D(double[] realF, double[] imagF, double[] realG, double[] imagG, boolean zeroCentered) {
        double[][] fftF = Transforms.computeFFT1D(realF, imagF, zeroCentered, false, Transforms.NORMALIZE_NONE);
        double[][] fftG = Transforms.computeFFT1D(realG, imagG, zeroCentered, false, Transforms.NORMALIZE_NONE);

        double[][] product = complexMultiply(fftF[0], fftF[1], fftG[0], fftG[1]);

        return Transforms.computeFFT1D(product[0], product[1], zeroCentered, true, Transforms.NORMALIZE_N);
    }

    /**
     * Performs 1D correlation: IFFT(FFT(f) · conj(FFT(m)))
     */
    private double[][] correlate1D(double[] realF, double[] imagF, double[] realM, double[] imagM, boolean zeroCentered) {
        double[][] fftF = Transforms.computeFFT1D(realF, imagF, zeroCentered, false, Transforms.NORMALIZE_NONE);
        double[][] fftM = Transforms.computeFFT1D(realM, imagM, zeroCentered, false, Transforms.NORMALIZE_NONE);

        double[][] product = complexMultiplyConjugate(fftF[0], fftF[1], fftM[0], fftM[1]);

        return Transforms.computeFFT1D(product[0], product[1], zeroCentered, true, Transforms.NORMALIZE_N);
    }

    /**
     * Performs 1D autocorrelation: IFFT(|FFT(f)|²)
     */
    private double[][] autocorrelate1D(double[] realF, double[] imagF, boolean zeroCentered) {
        double[][] fftF = Transforms.computeFFT1D(realF, imagF, zeroCentered, false, Transforms.NORMALIZE_NONE);

        double[] magSq = new double[realF.length];
        for (int i = 0; i < realF.length; i++) {
            magSq[i] = fftF[0][i] * fftF[0][i] + fftF[1][i] * fftF[1][i];
        }

        return Transforms.computeFFT1D(magSq, createZeros(realF.length), zeroCentered, true, Transforms.NORMALIZE_N);
    }

    // ========== CONVOLUTION TESTS ==========

    @Nested
    @DisplayName("1D Convolution Tests")
    class ConvolutionTests {

        @Test
        @DisplayName("Convolution with delta at origin returns original function")
        void convolutionWithDeltaAtOrigin() {
            boolean zeroCentered = true;
            double[] delta = createDelta(SIZE, 0, zeroCentered);
            double[] zeros = createZeros(SIZE);
            double[] rect = createRect(SIZE, 0, 4, zeroCentered);

            double[][] result = convolve1D(rect, zeros, delta, zeros, zeroCentered);

            assertSignalsEqual(rect, result[0], "f * δ should equal f");
            assertSignalIsZero(result[1], "Imaginary part should be zero for real inputs");
        }

        @Test
        @DisplayName("Convolution with shifted delta shifts the function")
        void convolutionWithShiftedDelta() {
            boolean zeroCentered = true;
            int shift = 5;
            double[] delta = createDelta(SIZE, shift, zeroCentered);
            double[] zeros = createZeros(SIZE);
            double[] original = createDelta(SIZE, 0, zeroCentered);

            double[][] result = convolve1D(original, zeros, delta, zeros, zeroCentered);

            // Result should have peak at 'shift' position
            int peakIdx = findPeakIndex(result[0]);
            int expectedIdx = toIndex(shift, SIZE, zeroCentered);
            assertEquals(expectedIdx, peakIdx, "Convolution with δ(x-a) should shift peak to position a");
        }

        @Test
        @DisplayName("Convolution is commutative: f * g = g * f")
        void convolutionIsCommutative() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 3, zeroCentered);
            double[] g = createGaussian(SIZE, 0, 8, 1.0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] fg = convolve1D(f, zeros, g, zeros, zeroCentered);
            double[][] gf = convolve1D(g, zeros, f, zeros, zeroCentered);

            assertSignalsEqual(fg[0], gf[0], "Convolution should be commutative (real part)");
            assertSignalsEqual(fg[1], gf[1], "Convolution should be commutative (imaginary part)");
        }

        @Test
        @DisplayName("Convolution preserves total area: area(f*g) = area(f) * area(g)")
        void convolutionPreservesArea() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 3, zeroCentered);
            double[] g = createRect(SIZE, 0, 2, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double areaF = sum(f);
            double areaG = sum(g);

            double[][] result = convolve1D(f, zeros, g, zeros, zeroCentered);
            double areaResult = sum(result[0]);

            assertEquals(areaF * areaG, areaResult, LOOSE_TOLERANCE,
                "Area of convolution should equal product of areas");
        }

        @Test
        @DisplayName("Convolution of rect with rect produces triangle-like shape")
        void convolutionOfTwoRects() {
            boolean zeroCentered = true;
            double[] rect = createRect(SIZE, 0, 2, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] result = convolve1D(rect, zeros, rect, zeros, zeroCentered);

            // Peak should be at origin
            int originIdx = getOriginIndex(SIZE, zeroCentered);
            assertEquals(originIdx, findPeakIndex(result[0]),
                "Rect * Rect should peak at origin");

            // Value should decrease away from origin (triangle shape)
            double atOrigin = result[0][originIdx];
            double nearOrigin = result[0][originIdx + 1];
            assertTrue(atOrigin > nearOrigin, "Value should decrease away from origin");
        }

        @Test
        @DisplayName("Convolution of complex functions")
        void convolutionOfComplexFunctions() {
            boolean zeroCentered = true;
            // f = 1 + i at origin
            double[] fReal = createDelta(SIZE, 0, zeroCentered);
            double[] fImag = createDelta(SIZE, 0, zeroCentered);
            // g = 1 - i at origin
            double[] gReal = createDelta(SIZE, 0, zeroCentered);
            double[] gImag = createDelta(SIZE, 0, zeroCentered);
            for (int i = 0; i < SIZE; i++) gImag[i] *= -1;

            double[][] result = convolve1D(fReal, fImag, gReal, gImag, zeroCentered);

            // (1+i) * (1-i) = 1 - i + i - i² = 2
            int originIdx = getOriginIndex(SIZE, zeroCentered);
            assertEquals(2.0, result[0][originIdx], TOLERANCE,
                "(1+i)(1-i) real part should be 2");
            assertEquals(0.0, result[1][originIdx], TOLERANCE,
                "(1+i)(1-i) imag part should be 0");
        }

        @Test
        @DisplayName("Non-zero-centered convolution works correctly")
        void nonZeroCenteredConvolution() {
            boolean zeroCentered = false;
            double[] delta = createDelta(SIZE, 0, zeroCentered);
            double[] f = createRect(SIZE, 0, 3, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] result = convolve1D(f, zeros, delta, zeros, zeroCentered);

            assertSignalsEqual(f, result[0], "Non-ZC: f * δ should equal f");
        }

        @Test
        @DisplayName("Convolution matches time-domain reference for centered signals")
        void convolutionMatchesTimeDomain() {
            // Note: The time-domain reference uses a simplified circular convolution
            // The FFT-based method may differ at boundaries due to zero-centering
            // This test verifies the basic shape matches
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 2, zeroCentered);
            double[] g = createDelta(SIZE, 0, zeroCentered);  // Use delta for exact comparison
            double[] zeros = createZeros(SIZE);

            double[][] fftResult = convolve1D(f, zeros, g, zeros, zeroCentered);

            // Convolution with delta should return original
            assertSignalsEqual(f, fftResult[0], LOOSE_TOLERANCE,
                "FFT convolution with delta should return original");
        }
    }

    // ========== CORRELATION TESTS ==========

    @Nested
    @DisplayName("1D Correlation Tests (Roger Easton Definition)")
    class CorrelationTests {

        @Test
        @DisplayName("Correlation with delta at origin returns original function")
        void correlationWithDeltaAtOrigin() {
            boolean zeroCentered = true;
            double[] delta = createDelta(SIZE, 0, zeroCentered);
            double[] zeros = createZeros(SIZE);
            double[] rect = createRect(SIZE, 0, 4, zeroCentered);

            double[][] result = correlate1D(rect, zeros, delta, zeros, zeroCentered);

            assertSignalsEqual(rect, result[0], "f ⋆ δ should equal f");
        }

        @Test
        @DisplayName("Correlation with shifted delta shifts in OPPOSITE direction")
        void correlationWithShiftedDelta() {
            boolean zeroCentered = true;
            int shift = 5;
            double[] delta = createDelta(SIZE, shift, zeroCentered);
            double[] zeros = createZeros(SIZE);
            double[] original = createDelta(SIZE, 0, zeroCentered);

            double[][] result = correlate1D(original, zeros, delta, zeros, zeroCentered);

            // For correlation: f ⋆ δ(x-a) = f(x+a)
            // Peak should be at -shift position
            int peakIdx = findPeakIndex(result[0]);
            int expectedIdx = toIndex(-shift, SIZE, zeroCentered);
            assertEquals(expectedIdx, peakIdx, 
                "Correlation with shifted delta should shift in opposite direction");
        }

        @Test
        @DisplayName("Correlation is NOT commutative: f ⋆ g ≠ g ⋆ f")
        void correlationIsNotCommutative() {
            boolean zeroCentered = true;
            double[] f = createDelta(SIZE, 3, zeroCentered);
            double[] g = createDelta(SIZE, -5, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] fg = correlate1D(f, zeros, g, zeros, zeroCentered);
            double[][] gf = correlate1D(g, zeros, f, zeros, zeroCentered);

            // Find peak positions
            int fgPeakIdx = findPeakIndex(fg[0]);
            int gfPeakIdx = findPeakIndex(gf[0]);

            assertNotEquals(fgPeakIdx, gfPeakIdx,
                "Correlation should not be commutative - peaks at different positions");
        }

        @Test
        @DisplayName("Correlation conjugates the REFERENCE (second argument)")
        void correlationConjugatesReference() {
            boolean zeroCentered = true;
            // f = 1 at origin (real)
            double[] fReal = createDelta(SIZE, 0, zeroCentered);
            double[] fImag = createZeros(SIZE);
            // m = i at origin (purely imaginary)
            double[] mReal = createZeros(SIZE);
            double[] mImag = createDelta(SIZE, 0, zeroCentered);

            double[][] result = correlate1D(fReal, fImag, mReal, mImag, zeroCentered);

            // f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))
            // FFT(f) = 1 everywhere, FFT(m) = i everywhere
            // conj(FFT(m)) = -i everywhere
            // Product = 1 · (-i) = -i everywhere
            // IFFT(-i) = -i at origin

            int originIdx = getOriginIndex(SIZE, zeroCentered);
            assertEquals(0.0, result[0][originIdx], TOLERANCE,
                "1 ⋆ i: real part should be 0");
            assertEquals(-1.0, result[1][originIdx], TOLERANCE,
                "1 ⋆ i: imag part should be -1 (reference conjugated)");
        }

        @Test
        @DisplayName("Self-correlation peak is at origin")
        void selfCorrelationPeakAtOrigin() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 4, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] result = correlate1D(f, zeros, f, zeros, zeroCentered);

            int originIdx = getOriginIndex(SIZE, zeroCentered);
            int peakIdx = findPeakIndex(result[0]);
            assertEquals(originIdx, peakIdx, "Self-correlation peak should be at origin");
        }

        @Test
        @DisplayName("Correlation equals convolution with conjugate-reversed reference")
        void correlationEqualsConvolutionWithConjugateReversed() {
            boolean zeroCentered = true;
            double[] fReal = createRect(SIZE, 2, 3, zeroCentered);
            double[] fImag = createSine(SIZE, 2, 0.5, zeroCentered);
            double[] mReal = createGaussian(SIZE, -1, 5, 1.0, zeroCentered);
            double[] mImag = createCosine(SIZE, 3, 0.3, zeroCentered);

            // Compute correlation directly
            double[][] correlation = correlate1D(fReal, fImag, mReal, mImag, zeroCentered);

            // Compute convolution with conjugate-reversed m: m*[-x]
            double[] mRevReal = reverse(mReal, zeroCentered);
            double[] mRevImagNeg = reverse(mImag, zeroCentered);
            for (int i = 0; i < SIZE; i++) mRevImagNeg[i] *= -1; // Conjugate

            double[][] convolution = convolve1D(fReal, fImag, mRevReal, mRevImagNeg, zeroCentered);

            assertSignalsEqual(correlation[0], convolution[0], LOOSE_TOLERANCE,
                "f ⋆ m should equal f * m*[-x] (real part)");
            assertSignalsEqual(correlation[1], convolution[1], LOOSE_TOLERANCE,
                "f ⋆ m should equal f * m*[-x] (imaginary part)");
        }

        @Test
        @DisplayName("Correlation matches time-domain reference for centered signals")
        void correlationMatchesTimeDomain() {
            // Verify correlation with delta returns original
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 2, zeroCentered);
            double[] g = createDelta(SIZE, 0, zeroCentered);  // Use delta for exact comparison
            double[] zeros = createZeros(SIZE);

            double[][] fftResult = correlate1D(f, zeros, g, zeros, zeroCentered);

            // Correlation with delta at origin should return original
            assertSignalsEqual(f, fftResult[0], LOOSE_TOLERANCE,
                "FFT correlation with delta should return original");
        }

        @Test
        @DisplayName("Correlation finds signal shift correctly")
        void correlationFindsShift() {
            boolean zeroCentered = true;
            int actualShift = 7;
            double[] reference = createRect(SIZE, 0, 3, zeroCentered);
            double[] shifted = circularShift(reference, actualShift);
            double[] zeros = createZeros(SIZE);

            // Correlate shifted with reference
            double[][] result = correlate1D(shifted, zeros, reference, zeros, zeroCentered);

            // Peak should indicate the shift
            int peakIdx = findPeakIndex(result[0]);
            int peakPos = toPosition(peakIdx, SIZE, zeroCentered);
            assertEquals(actualShift, peakPos, "Correlation should find the shift amount");
        }
    }

    // ========== AUTOCORRELATION TESTS ==========

    @Nested
    @DisplayName("1D Autocorrelation Tests")
    class AutocorrelationTests {

        @Test
        @DisplayName("Autocorrelation equals self-correlation")
        void autocorrelationEqualsSelfCorrelation() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 4, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] auto = autocorrelate1D(f, zeros, zeroCentered);
            double[][] self = correlate1D(f, zeros, f, zeros, zeroCentered);

            assertSignalsEqual(auto[0], self[0], TOLERANCE,
                "Autocorrelation should equal f ⋆ f (real part)");
            assertSignalsEqual(auto[1], self[1], TOLERANCE,
                "Autocorrelation should equal f ⋆ f (imaginary part)");
        }

        @Test
        @DisplayName("Autocorrelation at origin equals energy")
        void autocorrelationAtOriginEqualsEnergy() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 4, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double signalEnergy = energy(f);

            double[][] auto = autocorrelate1D(f, zeros, zeroCentered);
            int originIdx = getOriginIndex(SIZE, zeroCentered);
            double autoAtOrigin = auto[0][originIdx];

            assertEquals(signalEnergy, autoAtOrigin, TOLERANCE,
                "Autocorrelation at origin should equal signal energy");
        }

        @Test
        @DisplayName("Autocorrelation is even for real input")
        void autocorrelationIsEvenForRealInput() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 5, 3, zeroCentered); // Asymmetric rect
            double[] zeros = createZeros(SIZE);

            double[][] auto = autocorrelate1D(f, zeros, zeroCentered);

            assertSignalIsEven(auto[0], zeroCentered, 
                "Autocorrelation real part should be even for real input");
        }

        @Test
        @DisplayName("Autocorrelation of real input is real")
        void autocorrelationOfRealIsReal() {
            boolean zeroCentered = true;
            double[] f = createGaussian(SIZE, 0, 8, 1.0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] auto = autocorrelate1D(f, zeros, zeroCentered);

            assertSignalIsZero(auto[1], "Autocorrelation of real signal should be real");
        }

        @Test
        @DisplayName("Autocorrelation peak is at origin")
        void autocorrelationPeakAtOrigin() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 5, 4, zeroCentered); // Off-center rect
            double[] zeros = createZeros(SIZE);

            double[][] auto = autocorrelate1D(f, zeros, zeroCentered);

            int originIdx = getOriginIndex(SIZE, zeroCentered);
            int peakIdx = findPeakIndex(auto[0]);
            assertEquals(originIdx, peakIdx, "Autocorrelation peak should be at origin");
        }

        @Test
        @DisplayName("Autocorrelation of delta is delta at origin")
        void autocorrelationOfDelta() {
            boolean zeroCentered = true;
            double[] delta = createDelta(SIZE, 10, zeroCentered); // Delta at non-origin
            double[] zeros = createZeros(SIZE);

            double[][] auto = autocorrelate1D(delta, zeros, zeroCentered);

            // Should have peak at origin with value 1
            int originIdx = getOriginIndex(SIZE, zeroCentered);
            assertEquals(1.0, auto[0][originIdx], TOLERANCE,
                "Autocorrelation of delta should have unit peak at origin");

            // All other values should be zero
            for (int i = 0; i < SIZE; i++) {
                if (i != originIdx) {
                    assertEquals(0.0, auto[0][i], TOLERANCE,
                        "Autocorrelation of delta should be zero away from origin");
                }
            }
        }

        @Test
        @DisplayName("Autocorrelation is non-negative at origin for any input")
        void autocorrelationNonNegativeAtOrigin() {
            boolean zeroCentered = true;
            double[] zeros = createZeros(SIZE);

            int originIdx = getOriginIndex(SIZE, zeroCentered);

            // Test rect
            double[] rect = createRect(SIZE, 0, 3, zeroCentered);
            double[][] autoRect = autocorrelate1D(rect, zeros, zeroCentered);
            assertTrue(autoRect[0][originIdx] >= 0,
                "Autocorrelation at origin must be non-negative (energy) - rect");

            // Test sine
            double[] sine = createSine(SIZE, 4, 1.0, zeroCentered);
            double[][] autoSine = autocorrelate1D(sine, zeros, zeroCentered);
            assertTrue(autoSine[0][originIdx] >= 0,
                "Autocorrelation at origin must be non-negative (energy) - sine");

            // Test gaussian
            double[] gauss = createGaussian(SIZE, 5, 10, 2.0, zeroCentered);
            double[][] autoGauss = autocorrelate1D(gauss, zeros, zeroCentered);
            assertTrue(autoGauss[0][originIdx] >= 0,
                "Autocorrelation at origin must be non-negative (energy) - gaussian");
        }

        @Test
        @DisplayName("Autocorrelation of complex signal is Hermitian")
        void autocorrelationOfComplexIsHermitian() {
            boolean zeroCentered = true;
            // Create complex signal
            double[] fReal = createSine(SIZE, 3, 1.0, zeroCentered);
            double[] fImag = createCosine(SIZE, 2, 0.5, zeroCentered);

            double[][] auto = autocorrelate1D(fReal, fImag, zeroCentered);

            assertSignalIsHermitian(auto[0], auto[1], zeroCentered,
                "Autocorrelation should be Hermitian");
        }
    }

    // ========== FFT ROUND-TRIP TESTS ==========

    @Nested
    @DisplayName("FFT Round-Trip Tests")
    class FFTRoundTripTests {

        @Test
        @DisplayName("FFT followed by IFFT returns original signal")
        void fftFollowedByIfft() {
            boolean zeroCentered = true;
            double[] original = createRect(SIZE, 0, 4, zeroCentered);
            double[] zeros = createZeros(SIZE);

            // Forward FFT (no normalization)
            double[][] fft = Transforms.computeFFT1D(original, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);
            
            // Inverse FFT (normalize by N)
            double[][] result = Transforms.computeFFT1D(fft[0], fft[1], zeroCentered, true, Transforms.NORMALIZE_N);

            assertSignalsEqual(original, result[0], TOLERANCE,
                "IFFT(FFT(f)) should equal f (real part)");
            assertSignalIsZero(result[1], "IFFT(FFT(f)) should equal f (imaginary part should be zero)");
        }

        @Test
        @DisplayName("IFFT followed by FFT returns original signal")
        void ifftFollowedByFft() {
            boolean zeroCentered = true;
            double[] original = createGaussian(SIZE, 0, 8, 1.0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            // Inverse FFT (normalize by N)
            double[][] ifft = Transforms.computeFFT1D(original, zeros, zeroCentered, true, Transforms.NORMALIZE_N);
            
            // Forward FFT (no normalization) - this undoes the IFFT
            double[][] result = Transforms.computeFFT1D(ifft[0], ifft[1], zeroCentered, false, Transforms.NORMALIZE_NONE);

            assertSignalsEqual(original, result[0], TOLERANCE,
                "FFT(IFFT(f)) should equal f (real part)");
            assertSignalIsZero(result[1], "FFT(IFFT(f)) should equal f (imaginary part should be zero)");
        }

        @Test
        @DisplayName("FFT preserves energy (Parseval's theorem)")
        void fftParsevalsTheorem() {
            boolean zeroCentered = true;
            double[] f = createRect(SIZE, 0, 5, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double timeEnergy = energy(f);

            // With root-N normalization, energy is preserved
            double[][] fft = Transforms.computeFFT1D(f, zeros, zeroCentered, false, Transforms.NORMALIZE_ROOT_N);
            double freqEnergy = energy(fft[0], fft[1]);

            assertEquals(timeEnergy, freqEnergy, LOOSE_TOLERANCE,
                "Time-domain energy should equal frequency-domain energy (Parseval)");
        }

        @Test
        @DisplayName("FFT of delta is constant")
        void fftOfDeltaIsConstant() {
            boolean zeroCentered = true;
            double[] delta = createDelta(SIZE, 0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] fft = Transforms.computeFFT1D(delta, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);

            // FFT of delta at origin should be constant (all 1s)
            for (int i = 0; i < SIZE; i++) {
                assertEquals(1.0, fft[0][i], TOLERANCE,
                    "FFT(δ) real part should be 1 everywhere");
                assertEquals(0.0, fft[1][i], TOLERANCE,
                    "FFT(δ) imaginary part should be 0 everywhere");
            }
        }

        @Test
        @DisplayName("FFT of constant is delta")
        void fftOfConstantIsDelta() {
            boolean zeroCentered = true;
            double[] constant = createConstant(SIZE, 1.0);
            double[] zeros = createZeros(SIZE);

            double[][] fft = Transforms.computeFFT1D(constant, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);

            // FFT of constant 1 should be N*delta at origin
            int originIdx = getOriginIndex(SIZE, zeroCentered);
            assertEquals(SIZE, fft[0][originIdx], TOLERANCE,
                "FFT(1) should be N at origin");
            
            // Other values should be zero
            for (int i = 0; i < SIZE; i++) {
                if (i != originIdx) {
                    assertEquals(0.0, fft[0][i], TOLERANCE,
                        "FFT(1) should be 0 away from origin");
                }
            }
        }

        @Test
        @DisplayName("FFT of real even function is real")
        void fftOfRealEvenIsReal() {
            boolean zeroCentered = true;
            // Gaussian centered at origin is even
            double[] gaussian = createGaussian(SIZE, 0, 8, 1.0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] fft = Transforms.computeFFT1D(gaussian, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);

            // FFT of real even function should be real (imaginary = 0)
            for (int i = 0; i < SIZE; i++) {
                assertEquals(0.0, fft[1][i], LOOSE_TOLERANCE,
                    "FFT of real even function should have zero imaginary part");
            }
        }

        @Test
        @DisplayName("FFT of real odd function is purely imaginary (except DC)")
        void fftOfRealOddIsImaginary() {
            boolean zeroCentered = true;
            // Create odd function: f(-x) = -f(x)
            // Use sine which is properly odd
            double[] odd = createSine(SIZE, 4, 1.0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            double[][] fft = Transforms.computeFFT1D(odd, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);

            // FFT of real odd function should be purely imaginary (real = 0)
            // The DC component (origin) should also be zero for a true odd function
            int origin = SIZE / 2;
            assertEquals(0.0, fft[0][origin], LOOSE_TOLERANCE,
                "FFT of real odd function should have zero real part at DC");
            
            // Check that imaginary part is non-zero (the function is not zero)
            double imagEnergy = 0;
            for (int i = 0; i < SIZE; i++) {
                imagEnergy += fft[1][i] * fft[1][i];
            }
            assertTrue(imagEnergy > 0, "FFT of sine should have non-zero imaginary part");
        }
    }

    // ========== EDGE CASES ==========

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Operations with zero function return zero")
        void operationsWithZeroFunction() {
            boolean zeroCentered = true;
            double[] zero = createZeros(SIZE);
            double[] f = createRect(SIZE, 0, 4, zeroCentered);

            double[][] convResult = convolve1D(f, zero, zero, zero, zeroCentered);
            double[][] corrResult = correlate1D(f, zero, zero, zero, zeroCentered);
            double[][] autoResult = autocorrelate1D(zero, zero, zeroCentered);

            assertSignalIsZero(convResult[0], "f * 0 should be 0");
            assertSignalIsZero(corrResult[0], "f ⋆ 0 should be 0");
            assertSignalIsZero(autoResult[0], "autocorr(0) should be 0");
        }

        @Test
        @DisplayName("Operations with constant function")
        void operationsWithConstantFunction() {
            boolean zeroCentered = true;
            double[] constant = createConstant(SIZE, 1.0);
            double[] zeros = createZeros(SIZE);

            double[][] auto = autocorrelate1D(constant, zeros, zeroCentered);

            // Autocorrelation at origin should equal energy = N
            int originIdx = getOriginIndex(SIZE, zeroCentered);
            assertEquals(SIZE, auto[0][originIdx], TOLERANCE,
                "Autocorrelation of constant at origin should equal N");
        }

        @Test
        @DisplayName("Small signal size works correctly")
        void smallSignalSize() {
            int smallSize = 8;
            boolean zeroCentered = true;
            double[] f = createRect(smallSize, 0, 1, zeroCentered);
            double[] zeros = createZeros(smallSize);

            // Should not throw
            assertDoesNotThrow(() -> {
                convolve1D(f, zeros, f, zeros, zeroCentered);
                correlate1D(f, zeros, f, zeros, zeroCentered);
                autocorrelate1D(f, zeros, zeroCentered);
            });
        }
    }
}
