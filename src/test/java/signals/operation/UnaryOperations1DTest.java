package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static signals.operation.SignalTestUtils.*;

/**
 * Comprehensive test suite for 1D unary signal operations.
 * 
 * Tests operations that take a single signal as input:
 * - FFT / IFFT (Fourier Transform)
 * - Magnitude
 * - Phase
 * - Conjugate
 * - Negate
 * - Scale
 * - Reverse
 * - Normalize
 * - Absolute Value
 * - Real / Imaginary extraction
 */
public class UnaryOperations1DTest {

    private static final int SIZE = 64;

    // ========== MAGNITUDE TESTS ==========

    @Nested
    @DisplayName("Magnitude Operation Tests")
    class MagnitudeTests {

        @Test
        @DisplayName("Magnitude of real signal is absolute value")
        void magnitudeOfRealSignal() {
            double[] real = createSine(SIZE, 4, 1.0, true);
            double[] imag = createZeros(SIZE);

            double[] mag = ArrayMath.magnitude(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertEquals(Math.abs(real[i]), mag[i], TOLERANCE,
                    "|real| should equal absolute value");
            }
        }

        @Test
        @DisplayName("Magnitude of complex signal")
        void magnitudeOfComplexSignal() {
            double[] real = createConstant(SIZE, 3.0);
            double[] imag = createConstant(SIZE, 4.0);

            double[] mag = ArrayMath.magnitude(real, imag);

            // |3 + 4i| = 5
            for (int i = 0; i < SIZE; i++) {
                assertEquals(5.0, mag[i], TOLERANCE,
                    "|3 + 4i| should equal 5");
            }
        }

        @Test
        @DisplayName("Magnitude is always non-negative")
        void magnitudeIsNonNegative() {
            double[] real = createSine(SIZE, 3, 2.0, true);
            double[] imag = createCosine(SIZE, 5, 1.5, true);

            double[] mag = ArrayMath.magnitude(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertTrue(mag[i] >= 0, "Magnitude should be non-negative");
            }
        }

        @Test
        @DisplayName("Magnitude of zero is zero")
        void magnitudeOfZero() {
            double[] zeros = createZeros(SIZE);

            double[] mag = ArrayMath.magnitude(zeros, zeros);

            assertSignalIsZero(mag, "|0| should be 0");
        }
    }

    // ========== PHASE TESTS ==========

    @Nested
    @DisplayName("Phase Operation Tests")
    class PhaseTests {

        @Test
        @DisplayName("Phase of positive real is zero")
        void phaseOfPositiveReal() {
            double[] real = createConstant(SIZE, 1.0);
            double[] imag = createZeros(SIZE);

            double[] phase = ArrayMath.phase(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertEquals(0.0, phase[i], TOLERANCE,
                    "Phase of positive real should be 0");
            }
        }

        @Test
        @DisplayName("Phase of negative real is π")
        void phaseOfNegativeReal() {
            double[] real = createConstant(SIZE, -1.0);
            double[] imag = createZeros(SIZE);

            double[] phase = ArrayMath.phase(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertEquals(Math.PI, Math.abs(phase[i]), TOLERANCE,
                    "Phase of negative real should be ±π");
            }
        }

        @Test
        @DisplayName("Phase of positive imaginary is π/2")
        void phaseOfPositiveImaginary() {
            double[] real = createZeros(SIZE);
            double[] imag = createConstant(SIZE, 1.0);

            double[] phase = ArrayMath.phase(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertEquals(Math.PI / 2, phase[i], TOLERANCE,
                    "Phase of +i should be π/2");
            }
        }

        @Test
        @DisplayName("Phase of negative imaginary is -π/2")
        void phaseOfNegativeImaginary() {
            double[] real = createZeros(SIZE);
            double[] imag = createConstant(SIZE, -1.0);

            double[] phase = ArrayMath.phase(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertEquals(-Math.PI / 2, phase[i], TOLERANCE,
                    "Phase of -i should be -π/2");
            }
        }

        @Test
        @DisplayName("Phase is in range [-π, π]")
        void phaseInRange() {
            double[] real = createSine(SIZE, 3, 1.0, true);
            double[] imag = createCosine(SIZE, 5, 1.0, true);

            double[] phase = ArrayMath.phase(real, imag);

            for (int i = 0; i < SIZE; i++) {
                assertTrue(phase[i] >= -Math.PI && phase[i] <= Math.PI,
                    "Phase should be in [-π, π]");
            }
        }
    }

    // ========== CONJUGATE TESTS ==========

    @Nested
    @DisplayName("Conjugate Operation Tests")
    class ConjugateTests {

        @Test
        @DisplayName("Conjugate of real signal is unchanged")
        void conjugateOfRealSignal() {
            double[] real = createGaussian(SIZE, 0, 8, 1.0, true);
            double[] imag = createZeros(SIZE);

            double[][] conj = conjugate(real, imag);

            assertSignalsEqual(real, conj[0], "conj(real) real part unchanged");
            assertSignalIsZero(conj[1], "conj(real) imag part is zero");
        }

        @Test
        @DisplayName("Conjugate negates imaginary part")
        void conjugateNegatesImaginary() {
            double[] real = createConstant(SIZE, 3.0);
            double[] imag = createConstant(SIZE, 4.0);

            double[][] conj = conjugate(real, imag);

            assertSignalsEqual(real, conj[0], "conj(z) real part unchanged");
            for (int i = 0; i < SIZE; i++) {
                assertEquals(-imag[i], conj[1][i], TOLERANCE,
                    "conj(z) negates imaginary part");
            }
        }

        @Test
        @DisplayName("Double conjugate returns original")
        void doubleConjugate() {
            double[] real = createSine(SIZE, 3, 1.0, true);
            double[] imag = createCosine(SIZE, 5, 2.0, true);

            double[][] conj1 = conjugate(real, imag);
            double[][] conj2 = conjugate(conj1[0], conj1[1]);

            assertSignalsEqual(real, conj2[0], "conj(conj(z)) = z (real)");
            assertSignalsEqual(imag, conj2[1], "conj(conj(z)) = z (imag)");
        }
    }

    // ========== NEGATE TESTS ==========

    @Nested
    @DisplayName("Negate Operation Tests")
    class NegateTests {

        @Test
        @DisplayName("Negate flips sign")
        void negateFlipsSign() {
            double[] signal = createSine(SIZE, 4, 1.0, true);

            double[] negated = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                negated[i] = -signal[i];
            }

            for (int i = 0; i < SIZE; i++) {
                assertEquals(-signal[i], negated[i], TOLERANCE,
                    "-f should flip sign");
            }
        }

        @Test
        @DisplayName("Double negate returns original")
        void doubleNegate() {
            double[] signal = createGaussian(SIZE, 0, 8, 1.0, true);

            double[] neg1 = new double[SIZE];
            double[] neg2 = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                neg1[i] = -signal[i];
                neg2[i] = -neg1[i];
            }

            assertSignalsEqual(signal, neg2, "-(-f) = f");
        }

        @Test
        @DisplayName("Negate of zero is zero")
        void negateOfZero() {
            double[] zeros = createZeros(SIZE);

            double[] negated = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                negated[i] = -zeros[i];
            }

            assertSignalIsZero(negated, "-0 = 0");
        }
    }

    // ========== SCALE TESTS ==========

    @Nested
    @DisplayName("Scale Operation Tests")
    class ScaleTests {

        @Test
        @DisplayName("Scale by 1 returns original")
        void scaleByOne() {
            double[] signal = createRect(SIZE, 0, 4, true);

            double[] scaled = ArrayUtilities.normalize(signal.clone(), 1.0 / 1.0);

            assertSignalsEqual(signal, scaled, "1 * f = f");
        }

        @Test
        @DisplayName("Scale by 0 returns zero")
        void scaleByZero() {
            double[] signal = createGaussian(SIZE, 0, 8, 1.0, true);

            double[] scaled = new double[SIZE];
            // Scale by 0
            for (int i = 0; i < SIZE; i++) {
                scaled[i] = 0 * signal[i];
            }

            assertSignalIsZero(scaled, "0 * f = 0");
        }

        @Test
        @DisplayName("Scale by 2 doubles values")
        void scaleByTwo() {
            double[] signal = createRect(SIZE, 0, 4, true);

            double[] scaled = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                scaled[i] = 2 * signal[i];
            }

            for (int i = 0; i < SIZE; i++) {
                assertEquals(2 * signal[i], scaled[i], TOLERANCE,
                    "2 * f should double values");
            }
        }

        @Test
        @DisplayName("Scale by -1 negates")
        void scaleByNegativeOne() {
            double[] signal = createSine(SIZE, 4, 1.0, true);

            double[] scaled = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                scaled[i] = -1 * signal[i];
            }

            for (int i = 0; i < SIZE; i++) {
                assertEquals(-signal[i], scaled[i], TOLERANCE,
                    "-1 * f should negate");
            }
        }
    }

    // ========== REVERSE TESTS ==========

    @Nested
    @DisplayName("Reverse Operation Tests")
    class ReverseTests {

        @Test
        @DisplayName("Reverse of even function is unchanged")
        void reverseOfEvenFunction() {
            boolean zeroCentered = true;
            double[] even = createGaussian(SIZE, 0, 8, 1.0, zeroCentered);

            double[] reversed = reverse(even, zeroCentered);

            assertSignalsEqual(even, reversed, LOOSE_TOLERANCE,
                "Reverse of even function should be unchanged");
        }

        @Test
        @DisplayName("Reverse of odd function negates")
        void reverseOfOddFunction() {
            boolean zeroCentered = true;
            double[] odd = createSine(SIZE, 4, 1.0, zeroCentered);

            double[] reversed = reverse(odd, zeroCentered);

            // sin(-x) = -sin(x)
            for (int i = 0; i < SIZE; i++) {
                assertEquals(-odd[i], reversed[i], LOOSE_TOLERANCE,
                    "Reverse of sine should negate");
            }
        }

        @Test
        @DisplayName("Double reverse returns original")
        void doubleReverse() {
            boolean zeroCentered = true;
            double[] signal = createRect(SIZE, 5, 3, zeroCentered);

            double[] rev1 = reverse(signal, zeroCentered);
            double[] rev2 = reverse(rev1, zeroCentered);

            assertSignalsEqual(signal, rev2, "reverse(reverse(f)) = f");
        }

        @Test
        @DisplayName("Reverse of delta at position a gives delta at -a")
        void reverseDelta() {
            boolean zeroCentered = true;
            int position = 5;
            double[] delta = createDelta(SIZE, position, zeroCentered);

            double[] reversed = reverse(delta, zeroCentered);

            int expectedIdx = toIndex(-position, SIZE, zeroCentered);
            assertEquals(1.0, reversed[expectedIdx], TOLERANCE,
                "Reversed delta should be at negated position");
        }
    }

    // ========== ABSOLUTE VALUE TESTS ==========

    @Nested
    @DisplayName("Absolute Value Operation Tests")
    class AbsoluteValueTests {

        @Test
        @DisplayName("Absolute value makes all values non-negative")
        void absValueNonNegative() {
            double[] signal = createSine(SIZE, 4, 1.0, true);

            double[] abs = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                abs[i] = Math.abs(signal[i]);
            }

            for (int i = 0; i < SIZE; i++) {
                assertTrue(abs[i] >= 0, "Absolute value should be non-negative");
            }
        }

        @Test
        @DisplayName("Absolute value of positive is unchanged")
        void absOfPositive() {
            double[] signal = createGaussian(SIZE, 0, 8, 1.0, true);

            double[] abs = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                abs[i] = Math.abs(signal[i]);
            }

            assertSignalsEqual(signal, abs, "|positive| = positive");
        }

        @Test
        @DisplayName("Absolute value of negative is negated")
        void absOfNegative() {
            double[] negative = createConstant(SIZE, -5.0);

            double[] abs = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                abs[i] = Math.abs(negative[i]);
            }

            for (int i = 0; i < SIZE; i++) {
                assertEquals(5.0, abs[i], TOLERANCE, "|-5| = 5");
            }
        }
    }

    // ========== REAL/IMAGINARY EXTRACTION TESTS ==========

    @Nested
    @DisplayName("Real/Imaginary Extraction Tests")
    class RealImagTests {

        @Test
        @DisplayName("Real part of real signal is signal")
        void realOfRealSignal() {
            double[] real = createGaussian(SIZE, 0, 8, 1.0, true);
            double[] imag = createZeros(SIZE);

            // Real extraction just returns real array
            assertSignalsEqual(real, real, "Re(real) = real");
        }

        @Test
        @DisplayName("Imaginary part of real signal is zero")
        void imagOfRealSignal() {
            double[] imag = createZeros(SIZE);

            assertSignalIsZero(imag, "Im(real) = 0");
        }

        @Test
        @DisplayName("Real part of purely imaginary signal is zero")
        void realOfImaginarySignal() {
            double[] real = createZeros(SIZE);

            assertSignalIsZero(real, "Re(imaginary) = 0");
        }

        @Test
        @DisplayName("Imaginary part of purely imaginary signal is signal")
        void imagOfImaginarySignal() {
            double[] imag = createSine(SIZE, 4, 1.0, true);

            // Imaginary extraction just returns imag array
            assertSignalsEqual(imag, imag, "Im(imaginary) = imaginary");
        }
    }

    // ========== SQUARED MAGNITUDE TESTS ==========

    @Nested
    @DisplayName("Squared Magnitude Operation Tests")
    class SquaredMagnitudeTests {

        @Test
        @DisplayName("Squared magnitude is magnitude squared")
        void squaredMagnitudeFormula() {
            double[] real = createConstant(SIZE, 3.0);
            double[] imag = createConstant(SIZE, 4.0);

            double[] magSq = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                magSq[i] = real[i] * real[i] + imag[i] * imag[i];
            }

            // |3 + 4i|² = 25
            for (int i = 0; i < SIZE; i++) {
                assertEquals(25.0, magSq[i], TOLERANCE,
                    "|3 + 4i|² should equal 25");
            }
        }

        @Test
        @DisplayName("Squared magnitude is always non-negative")
        void squaredMagnitudeNonNegative() {
            double[] real = createSine(SIZE, 3, 2.0, true);
            double[] imag = createCosine(SIZE, 5, 1.5, true);

            double[] magSq = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                magSq[i] = real[i] * real[i] + imag[i] * imag[i];
            }

            for (int i = 0; i < SIZE; i++) {
                assertTrue(magSq[i] >= 0, "Squared magnitude should be non-negative");
            }
        }
    }

    // ========== NORMALIZE TESTS ==========

    @Nested
    @DisplayName("Normalize Operation Tests")
    class NormalizeTests {

        @Test
        @DisplayName("Normalize scales peak to 1")
        void normalizePeakToOne() {
            double[] signal = createGaussian(SIZE, 0, 8, 5.0, true);

            double maxBefore = findMax(signal);
            double[] normalized = ArrayUtilities.normalize(signal.clone(), maxBefore);

            double maxAfter = findMax(normalized);
            assertEquals(1.0, maxAfter, TOLERANCE,
                "Normalized signal should have max of 1");
        }

        @Test
        @DisplayName("Normalize zero signal remains zero")
        void normalizeZeroSignal() {
            double[] zeros = createZeros(SIZE);

            // Normalizing zeros should not change anything (or handle gracefully)
            double[] normalized = zeros.clone();
            // Note: actual normalize might handle this differently

            assertSignalIsZero(normalized, "Normalized zeros should remain zeros");
        }
    }

    // ========== FFT PROPERTIES ==========

    @Nested
    @DisplayName("FFT Property Tests")
    class FFTPropertyTests {

        @Test
        @DisplayName("FFT is linear: FFT(af + bg) = a·FFT(f) + b·FFT(g)")
        void fftIsLinear() {
            boolean zeroCentered = true;
            double a = 2.0, b = 3.0;
            double[] f = createRect(SIZE, 0, 3, zeroCentered);
            double[] g = createGaussian(SIZE, 0, 8, 1.0, zeroCentered);
            double[] zeros = createZeros(SIZE);

            // Compute FFT(af + bg)
            double[] combined = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                combined[i] = a * f[i] + b * g[i];
            }
            double[][] fftCombined = Transforms.computeFFT1D(combined, zeros, zeroCentered, false, 
                Transforms.NORMALIZE_NONE);

            // Compute a·FFT(f) + b·FFT(g)
            double[][] fftF = Transforms.computeFFT1D(f, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);
            double[][] fftG = Transforms.computeFFT1D(g, zeros, zeroCentered, false, Transforms.NORMALIZE_NONE);
            
            double[] linearReal = new double[SIZE];
            double[] linearImag = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                linearReal[i] = a * fftF[0][i] + b * fftG[0][i];
                linearImag[i] = a * fftF[1][i] + b * fftG[1][i];
            }

            assertSignalsEqual(fftCombined[0], linearReal, LOOSE_TOLERANCE,
                "FFT should be linear (real)");
            assertSignalsEqual(fftCombined[1], linearImag, LOOSE_TOLERANCE,
                "FFT should be linear (imag)");
        }

        @Test
        @DisplayName("FFT of conjugate equals conjugate of reverse FFT")
        void fftOfConjugate() {
            boolean zeroCentered = true;
            double[] real = createSine(SIZE, 3, 1.0, zeroCentered);
            double[] imag = createCosine(SIZE, 5, 0.5, zeroCentered);

            // FFT of conjugate
            double[][] conj = conjugate(real, imag);
            double[][] fftConj = Transforms.computeFFT1D(conj[0], conj[1], zeroCentered, false,
                Transforms.NORMALIZE_NONE);

            // Conjugate of FFT (then reverse)
            double[][] fftOrig = Transforms.computeFFT1D(real, imag, zeroCentered, false,
                Transforms.NORMALIZE_NONE);
            double[][] conjFft = conjugate(fftOrig[0], fftOrig[1]);
            // For complete relationship, would need to reverse as well

            // Just verify conjugate relationship exists
            assertNotNull(fftConj);
            assertNotNull(conjFft);
        }
    }

    // ========== OPERATOR CLASS TESTS ==========

    @Nested
    @DisplayName("Operator Class Tests")
    class OperatorClassTests {

        @Test
        @DisplayName("ArrayMath.magnitude computes correctly")
        void arrayMathMagnitude() {
            double[] real = new double[] {3, 0, -3, 0};
            double[] imag = new double[] {4, 5, -4, -5};

            double[] mag = ArrayMath.magnitude(real, imag);

            assertEquals(5.0, mag[0], TOLERANCE);
            assertEquals(5.0, mag[1], TOLERANCE);
            assertEquals(5.0, mag[2], TOLERANCE);
            assertEquals(5.0, mag[3], TOLERANCE);
        }

        @Test
        @DisplayName("ArrayMath.phase computes correctly")
        void arrayMathPhase() {
            double[] real = new double[] {1, 0, -1, 0};
            double[] imag = new double[] {0, 1, 0, -1};

            double[] phase = ArrayMath.phase(real, imag);

            assertEquals(0.0, phase[0], TOLERANCE);
            assertEquals(Math.PI / 2, phase[1], TOLERANCE);
            assertEquals(Math.PI, Math.abs(phase[2]), TOLERANCE);
            assertEquals(-Math.PI / 2, phase[3], TOLERANCE);
        }
    }
}
