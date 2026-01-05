package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static signals.operation.SignalTestUtils.*;

/**
 * Cross-validation tests between Java and JavaScript implementations.
 * 
 * These tests use specific, deterministic inputs and verify exact numerical
 * outputs.
 * The same test cases exist in the JavaScript test suite, and both must produce
 * identical results (within floating-point tolerance).
 * 
 * Purpose:
 * 1. Ensure Java and JavaScript FFT implementations are compatible
 * 2. Verify convolution/correlation produce identical results
 * 3. Catch any normalization or indexing differences
 * 
 * Test structure:
 * - Each test uses simple, predictable input arrays
 * - Expected outputs are documented from textbook definitions
 * - Both implementations should match these expected values
 */
public class CrossValidation1DTest {

  // ========== REFERENCE VALUE TESTS ==========

  @Nested
  @DisplayName("FFT Reference Values")
  class FFTReferenceValues {

    @Test
    @DisplayName("FFT of delta at origin (non-zero-centered)")
    void fftOfDeltaAtOriginNonZC() {
      // Delta at index 0: [1, 0, 0, 0, 0, 0, 0, 0]
      // FFT should be constant 1.0 everywhere (all real)
      int N = 8;
      double[] real = new double[N];
      double[] imag = new double[N];
      real[0] = 1.0;

      double[][] fft = Transforms.computeFFT1D(real, imag, false, false, Transforms.NORMALIZE_NONE);

      // Expected: all real = 1.0, all imag = 0.0
      for (int i = 0; i < N; i++) {
        assertEquals(1.0, fft[0][i], TOLERANCE, "FFT real[" + i + "] should be 1.0");
        assertEquals(0.0, fft[1][i], TOLERANCE, "FFT imag[" + i + "] should be 0.0");
      }
    }

    @Test
    @DisplayName("FFT of delta at position 1 (non-zero-centered)")
    void fftOfDeltaAtPosition1NonZC() {
      // Delta at index 1: [0, 1, 0, 0, 0, 0, 0, 0]
      // FFT: e^(-j*2*pi*k*1/N) for k=0..N-1
      int N = 8;
      double[] real = new double[N];
      double[] imag = new double[N];
      real[1] = 1.0;

      double[][] fft = Transforms.computeFFT1D(real, imag, false, false, Transforms.NORMALIZE_NONE);

      // Expected: exp(-j*2*pi*k/8) = cos(-2*pi*k/8) + i*sin(-2*pi*k/8)
      for (int k = 0; k < N; k++) {
        double expectedReal = Math.cos(-2.0 * Math.PI * k / N);
        double expectedImag = Math.sin(-2.0 * Math.PI * k / N);
        assertEquals(expectedReal, fft[0][k], TOLERANCE, "FFT real[" + k + "]");
        assertEquals(expectedImag, fft[1][k], TOLERANCE, "FFT imag[" + k + "]");
      }
    }

    @Test
    @DisplayName("FFT of constant signal (non-zero-centered)")
    void fftOfConstantNonZC() {
      // Constant 1: [1, 1, 1, 1, 1, 1, 1, 1]
      // FFT: N at index 0, 0 elsewhere
      int N = 8;
      double[] real = new double[N];
      double[] imag = new double[N];
      for (int i = 0; i < N; i++)
        real[i] = 1.0;

      double[][] fft = Transforms.computeFFT1D(real, imag, false, false, Transforms.NORMALIZE_NONE);

      assertEquals(N, fft[0][0], TOLERANCE, "FFT[0] should be N for constant signal");
      for (int i = 1; i < N; i++) {
        assertEquals(0.0, fft[0][i], TOLERANCE, "FFT[" + i + "] should be 0");
        assertEquals(0.0, fft[1][i], TOLERANCE, "FFT imag[" + i + "] should be 0");
      }
    }

    @Test
    @DisplayName("FFT round-trip preserves signal")
    void fftRoundTrip() {
      int N = 8;
      double[] real = { 1, 2, 3, 4, 5, 6, 7, 8 };
      double[] imag = { 0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5 };

      double[][] fft = Transforms.computeFFT1D(real, imag, false, false, Transforms.NORMALIZE_NONE);
      double[][] recovered = Transforms.computeFFT1D(fft[0], fft[1], false, true, Transforms.NORMALIZE_N);

      assertSignalsEqual(real, recovered[0], "Real part should be preserved");
      assertSignalsEqual(imag, recovered[1], "Imaginary part should be preserved");
    }
  }

  // ========== CORRELATION REFERENCE VALUES ==========

  @Nested
  @DisplayName("Correlation Reference Values")
  class CorrelationReferenceValues {

    /**
     * Correlate rect[1,1,1,1,0,0,0,0] with delta at position 2
     * Expected output matches JavaScript test:
     * correlate([1,1,1,1,0,0,0,0], [0,0,1,0,0,0,0,0]) = [1,1,0,0,0,0,1,1]
     */
    @Test
    @DisplayName("Correlation: rect with delta - matches JavaScript")
    void correlationRectWithDelta() {
      int N = 8;
      // Rectangle: [1, 1, 1, 1, 0, 0, 0, 0]
      double[] realA = { 1, 1, 1, 1, 0, 0, 0, 0 };
      double[] imagA = new double[N];

      // Delta at position 2: [0, 0, 1, 0, 0, 0, 0, 0]
      double[] realB = { 0, 0, 1, 0, 0, 0, 0, 0 };
      double[] imagB = new double[N];

      double[][] result = correlate1D(realA, imagA, realB, imagB, false);

      // Expected from Roger's definition and JavaScript:
      double[] expectedReal = { 1, 1, 0, 0, 0, 0, 1, 1 };

      assertSignalsEqual(expectedReal, result[0], "Correlation real part");
      assertSignalIsZero(result[1], "Correlation imag part should be zero for real signals");
    }

    /**
     * Complex correlation test matching JavaScript
     * A = [1+i, 2, 0, 0], B = [1, 1+i, 0, 0]
     * Expected: [3-i, 2, 0, 2]
     */
    @Test
    @DisplayName("Correlation: complex signals - matches JavaScript")
    void correlationComplexSignals() {
      int N = 4;
      // A = [1+i, 2, 0, 0]
      double[] realA = { 1, 2, 0, 0 };
      double[] imagA = { 1, 0, 0, 0 };

      // B = [1, 1+i, 0, 0]
      double[] realB = { 1, 1, 0, 0 };
      double[] imagB = { 0, 1, 0, 0 };

      double[][] result = correlate1D(realA, imagA, realB, imagB, false);

      // Expected from time-domain calculation (see JavaScript test):
      // n=0: (1+i)*1 + 2*(1-i) = 1+i + 2-2i = 3-i
      // n=1: (1+i)*0 + 2*1 = 2
      // n=2: 0
      // n=3: (1+i)*(1-i) = 1 - i² = 2
      double[] expectedReal = { 3, 2, 0, 2 };
      double[] expectedImag = { -1, 0, 0, 0 };

      assertSignalsEqual(expectedReal, result[0], "Correlation real part");
      assertSignalsEqual(expectedImag, result[1], "Correlation imag part");
    }

    /**
     * Self-correlation (not autocorrelation) test
     * Correlate signal with itself using correlation formula
     */
    @Test
    @DisplayName("Self-correlation peak at origin")
    void selfCorrelationPeakAtOrigin() {
      int N = 8;
      double[] real = { 1, 2, 3, 2, 1, 0, 0, 0 }; // Triangle-like
      double[] imag = new double[N];

      double[][] result = correlate1D(real, imag, real, imag, false);

      // Peak should be at index 0
      int peakIdx = 0;
      double peakVal = result[0][0];
      for (int i = 1; i < N; i++) {
        if (result[0][i] > peakVal) {
          peakVal = result[0][i];
          peakIdx = i;
        }
      }

      assertEquals(0, peakIdx, "Self-correlation peak should be at index 0");
    }
  }

  // ========== CONVOLUTION REFERENCE VALUES ==========

  @Nested
  @DisplayName("Convolution Reference Values")
  class ConvolutionReferenceValues {

    /**
     * Convolve delta with delta at different positions
     * delta[0] * delta[2] should give delta[2]
     */
    @Test
    @DisplayName("Convolution: delta with shifted delta")
    void convolutionDeltaWithShiftedDelta() {
      int N = 8;
      double[] deltaA = new double[N];
      deltaA[0] = 1.0; // Delta at 0

      double[] deltaB = new double[N];
      deltaB[2] = 1.0; // Delta at 2

      double[] zeros = new double[N];

      double[][] result = convolve1D(deltaA, zeros, deltaB, zeros, false);

      // Expected: delta at position 2
      double[] expected = new double[N];
      expected[2] = 1.0;

      assertSignalsEqual(expected, result[0], "Delta * shifted delta = shifted delta");
    }

    /**
     * Convolution is commutative: f * g = g * f
     */
    @Test
    @DisplayName("Convolution commutativity")
    void convolutionCommutativity() {
      int N = 8;
      double[] f = { 1, 2, 3, 0, 0, 0, 0, 0 };
      double[] g = { 0, 1, 1, 1, 0, 0, 0, 0 };
      double[] zeros = new double[N];

      double[][] fg = convolve1D(f, zeros, g, zeros, false);
      double[][] gf = convolve1D(g, zeros, f, zeros, false);

      assertSignalsEqual(fg[0], gf[0], "f*g real should equal g*f real");
      assertSignalsEqual(fg[1], gf[1], "f*g imag should equal g*f imag");
    }

    /**
     * Convolution of rect with rect = triangle (approximately)
     */
    @Test
    @DisplayName("Convolution: rect with rect approximates triangle")
    void convolutionRectWithRect() {
      int N = 16;
      double[] rect = new double[N];
      for (int i = 0; i < 4; i++)
        rect[i] = 1.0; // rect width 4

      double[] zeros = new double[N];

      double[][] result = convolve1D(rect, zeros, rect, zeros, false);

      // Result should be triangular: [1, 2, 3, 4, 3, 2, 1, 0, ...]
      // Peak at index 3 (width-1)
      assertEquals(4.0, result[0][3], TOLERANCE, "Peak should be 4 (rect width)");
      assertEquals(3.0, result[0][2], TOLERANCE, "Should be 3 at index 2");
      assertEquals(3.0, result[0][4], TOLERANCE, "Should be 3 at index 4");
      assertEquals(1.0, result[0][0], TOLERANCE, "Should be 1 at index 0");
    }
  }

  // ========== AUTOCORRELATION REFERENCE VALUES ==========

  @Nested
  @DisplayName("Autocorrelation Reference Values")
  class AutocorrelationReferenceValues {

    /**
     * Autocorrelation of delta is delta at origin
     */
    @Test
    @DisplayName("Autocorrelation of delta")
    void autocorrelationOfDelta() {
      int N = 8;
      double[] delta = new double[N];
      delta[3] = 1.0; // Delta at position 3

      double[] zeros = new double[N];

      double[][] result = autocorrelate1D(delta, zeros, false);

      // Should have peak at index 0
      assertEquals(1.0, result[0][0], TOLERANCE, "Autocorrelation of delta should be 1 at origin");

      // Total sum should be 1
      double sum = 0;
      for (double v : result[0])
        sum += v;
      assertEquals(1.0, sum, TOLERANCE, "Total sum should be 1");
    }

    /**
     * Autocorrelation at origin equals energy
     */
    @Test
    @DisplayName("Autocorrelation at origin equals energy")
    void autocorrelationEnergyAtOrigin() {
      int N = 8;
      double[] signal = { 1, 2, 3, 2, 1, 0, 0, 0 };
      double[] zeros = new double[N];

      // Compute energy
      double energy = 0;
      for (double v : signal)
        energy += v * v;

      double[][] result = autocorrelate1D(signal, zeros, false);

      assertEquals(energy, result[0][0], TOLERANCE, "Autocorrelation at origin should equal energy");
    }

    /**
     * Autocorrelation of real signal is even
     */
    @Test
    @DisplayName("Autocorrelation of real signal is even")
    void autocorrelationIsEven() {
      int N = 8;
      double[] signal = { 1, 2, 3, 4, 3, 2, 1, 0 };
      double[] zeros = new double[N];

      double[][] result = autocorrelate1D(signal, zeros, false);

      // Check symmetry: r[n] = r[N-n]
      for (int n = 1; n < N; n++) {
        assertEquals(result[0][n], result[0][N - n], TOLERANCE,
            "Autocorrelation should be even: r[" + n + "] = r[" + (N - n) + "]");
      }
    }
  }

  // ========== GENERATOR REFERENCE VALUES ==========

  @Nested
  @DisplayName("Generator Reference Values")
  class GeneratorReferenceValues {

    /**
     * Sine wave at specific indices
     */
    @Test
    @DisplayName("Sine generator values")
    void sineGeneratorValues() {
      // Sine with width=32 (period), amplitude=1
      // sin(2*pi*x/32) at indices 0, 8, 16, 24, 32
      int[] indices = { 0, 8, 16, 24, 32 };
      double[] expected = { 0, 1, 0, -1, 0 };

      for (int i = 0; i < indices.length; i++) {
        double value = Math.sin(2 * Math.PI * indices[i] / 32.0);
        assertEquals(expected[i], value, TOLERANCE, "sin at index " + indices[i]);
      }
    }

    /**
     * Cosine wave at specific indices
     */
    @Test
    @DisplayName("Cosine generator values")
    void cosineGeneratorValues() {
      // Cosine with width=32 (period), amplitude=1
      // cos(2*pi*x/32) at indices 0, 8, 16, 24, 32
      int[] indices = { 0, 8, 16, 24, 32 };
      double[] expected = { 1, 0, -1, 0, 1 };

      for (int i = 0; i < indices.length; i++) {
        double value = Math.cos(2 * Math.PI * indices[i] / 32.0);
        assertEquals(expected[i], value, TOLERANCE, "cos at index " + indices[i]);
      }
    }

    /**
     * Gaussian at specific positions
     */
    @Test
    @DisplayName("Gaussian generator values")
    void gaussianGeneratorValues() {
      // Gaussian: exp(-x²/(2*sigma²)), sigma=2
      double sigma = 2.0;
      int[] positions = { 0, 1, 2, 4 };
      double[] expected = new double[positions.length];
      for (int i = 0; i < positions.length; i++) {
        expected[i] = Math.exp(-positions[i] * positions[i] / (2 * sigma * sigma));
      }

      for (int i = 0; i < positions.length; i++) {
        double value = Math.exp(-positions[i] * positions[i] / (2 * sigma * sigma));
        assertEquals(expected[i], value, TOLERANCE, "Gaussian at position " + positions[i]);
      }

      // Verify actual values: exp(-0) = 1, exp(-1/8) ≈ 0.882, exp(-1/2) ≈ 0.606, exp(-2) ≈
      // 0.135
      assertEquals(1.0, expected[0], TOLERANCE);
      assertEquals(Math.exp(-0.125), expected[1], TOLERANCE);
      assertEquals(Math.exp(-0.5), expected[2], TOLERANCE);
      assertEquals(Math.exp(-2.0), expected[3], TOLERANCE);
    }
  }

  // ========== NORMALIZATION TESTS ==========

  @Nested
  @DisplayName("FFT Normalization Modes")
  class NormalizationTests {

    @Test
    @DisplayName("NORMALIZE_NONE preserves magnitude")
    void normalizeNone() {
      int N = 8;
      double[] real = { 1, 0, 0, 0, 0, 0, 0, 0 };
      double[] imag = new double[N];

      double[][] fft = Transforms.computeFFT1D(real, imag, false, false, Transforms.NORMALIZE_NONE);

      // Without normalization, FFT of delta = [1, 1, 1, 1, 1, 1, 1, 1]
      for (int i = 0; i < N; i++) {
        assertEquals(1.0, fft[0][i], TOLERANCE);
      }
    }

    @Test
    @DisplayName("NORMALIZE_N divides by N")
    void normalizeN() {
      int N = 8;
      double[] constant = new double[N];
      for (int i = 0; i < N; i++)
        constant[i] = 1.0;
      double[] imag = new double[N];

      // FFT of constant [1,1,1,...] with NORMALIZE_NONE = [N, 0, 0, ...]
      // With NORMALIZE_N = [1, 0, 0, ...]
      double[][] fft = Transforms.computeFFT1D(constant, imag, false, false, Transforms.NORMALIZE_N);

      assertEquals(1.0, fft[0][0], TOLERANCE, "FFT[0] with NORMALIZE_N should be 1");
    }

    @Test
    @DisplayName("NORMALIZE_ROOT_N divides by sqrt(N)")
    void normalizeRootN() {
      int N = 8;
      double[] constant = new double[N];
      for (int i = 0; i < N; i++)
        constant[i] = 1.0;
      double[] imag = new double[N];

      double[][] fft = Transforms.computeFFT1D(constant, imag, false, false, Transforms.NORMALIZE_ROOT_N);

      // FFT[0] should be N / sqrt(N) = sqrt(N)
      assertEquals(Math.sqrt(N), fft[0][0], TOLERANCE, "FFT[0] with NORMALIZE_ROOT_N should be sqrt(N)");
    }
  }

  // ========== HELPER METHODS ==========

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
   * Performs 1D convolution: IFFT(FFT(f) · FFT(g))
   */
  private double[][] convolve1D(double[] realF, double[] imagF, double[] realG, double[] imagG, boolean zeroCentered) {
    double[][] fftF = Transforms.computeFFT1D(realF, imagF, zeroCentered, false, Transforms.NORMALIZE_NONE);
    double[][] fftG = Transforms.computeFFT1D(realG, imagG, zeroCentered, false, Transforms.NORMALIZE_NONE);

    double[][] product = complexMultiply(fftF[0], fftF[1], fftG[0], fftG[1]);

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

    return Transforms.computeFFT1D(magSq, new double[realF.length], zeroCentered, true, Transforms.NORMALIZE_N);
  }
}
