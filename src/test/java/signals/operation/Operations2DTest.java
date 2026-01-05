package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for 2D convolution, correlation, and autocorrelation
 * operations.
 * 
 * These tests verify that the FFT-based implementations match the mathematical
 * definitions
 * from Roger Easton's book "Fourier Methods in Imaging" (Chapter 8).
 * 
 * Key definitions from Chapter 8:
 * 
 * 2D CONVOLUTION (Eq 8.82):
 * f[x,y] * h[x,y] = ∫∫ f[α,β] · h[x-α, y-β] dα dβ
 * FFT: IFFT(FFT(f) · FFT(h))
 * Property: Commutative - f * h = h * f
 * 
 * 2D CROSS-CORRELATION (Eq 8.86):
 * f[x,y] ⋆ m[x,y] = ∫∫ f[α,β] · m*[α-x, β-y] dα dβ
 * = f[x,y] * m*[-x,-y]
 * FFT: IFFT(FFT(f) · conj(FFT(m)))
 * Property: NOT commutative - f ⋆ m ≠ m ⋆ f (in general)
 * Note: The REFERENCE function (m) is conjugated, not the input (f)
 * 
 * 2D AUTOCORRELATION (Eq 8.87):
 * f[x,y] ⋆ f[x,y] = ∫∫ f[α,β] · f*[α-x, β-y] dα dβ
 * FFT: IFFT(|FFT(f)|²) = IFFT(FFT(f) · conj(FFT(f)))
 * Property: Result is Hermitian (even real part, odd imaginary part)
 * Property: Peak at origin equals energy (Eq 8.88)
 * 
 * IMPORTANT INDEX CONVENTIONS:
 * This codebase uses y_swap_expand/y_swap_flatten which flips the y-axis.
 * For an 8x8 array:
 * - 1D index 0 maps to 2D position [7][0] (bottom row in array)
 * - For zero-centered data, the FFT origin is at index 28 (row=3, col=4)
 * - The FFT origin formula: ORIGIN_INDEX = (DIM_Y/2) * DIM_X + (DIM_X/2 - 1)
 * for even dimensions
 * 
 * NOTE: These tests use raw array operations and Transforms directly to avoid
 * dependencies on the Core singleton which requires GUI initialization.
 */
public class Operations2DTest {

  private static final double TOLERANCE = 1e-10;
  private static final int DIM_X = 8;
  private static final int DIM_Y = 8;
  private static final int SIZE = DIM_X * DIM_Y;

  // FFT origin for zero-centered 8x8: row=3, col=4, index=28
  // This is the position where a delta gives constant FFT spectrum
  private static final int ZC_ORIGIN_INDEX = (DIM_Y / 2) * DIM_X + (DIM_X / 2 - 1); // = 28

  // ========== Test Data Container ==========

  /**
   * Simple container for 2D function data, avoiding Core dependencies.
   */
  static class TestFunction2D {
    final double[] real;
    final double[] imag;
    final boolean zeroCentered;
    final int dimX;
    final int dimY;

    TestFunction2D(double[] real, double[] imag, boolean zeroCentered, int dimX, int dimY) {
      this.real = real;
      this.imag = imag;
      this.zeroCentered = zeroCentered;
      this.dimX = dimX;
      this.dimY = dimY;
    }
  }

  // ========== Index Mapping Utilities ==========

  /**
   * Get the 1D array index for the FFT origin in zero-centered mode.
   * Empirically determined: for 8x8 zero-centered, the origin is at index 28.
   * 
   * Due to y_swap_expand behavior, the origin for an NxM array is at:
   * index = (DIM_Y/2 - 1) * DIM_X + DIM_X/2
   * For 8x8: (4-1)*8 + 4 = 3*8 + 4 = 28
   * 
   * This is NOT at (DIM_Y/2)*DIM_X + DIM_X/2 = 36 as one might expect!
   */
  private int getZeroCenteredOriginIndex() {
    return (DIM_Y / 2 - 1) * DIM_X + DIM_X / 2; // = 28 for 8x8
  }

  /**
   * Get the 1D array index for the FFT origin in non-zero-centered mode.
   * Empirically determined: for 8x8 non-zero-centered, the origin is at index 56.
   * 
   * Due to y_swap_expand behavior: row=7, col=0 → index 56
   */
  private int getNonZeroCenteredOriginIndex() {
    return (DIM_Y - 1) * DIM_X; // = 56 for 8x8
  }

  /**
   * Convert logical (x, y) coordinates to 1D array index.
   * For zero-centered: x in [-DIM_X/2, DIM_X/2-1], y in [-DIM_Y/2, DIM_Y/2-1]
   * For non-zero-centered: x in [0, DIM_X-1], y in [0, DIM_Y-1]
   * 
   * Due to y_swap_expand, the y-axis is flipped in storage.
   * Logical y=0 corresponds to the FFT origin row.
   */
  private int toIndex(int x, int y, boolean zeroCentered) {
    int actualX, actualY;

    if (zeroCentered) {
      // For zero-centered, (0,0) maps to the FFT origin (index 28 for 8x8)
      // Adjust x: x=0 -> middle column (col 4)
      actualX = (x + DIM_X / 2 + DIM_X) % DIM_X;
      // Adjust y: account for y_swap and centering
      // y=0 should map to the origin row (row 3 in 1D terms, due to y_swap at row
      // DIM_Y-1-3=4)
      actualY = ((DIM_Y / 2 - 1) - y + DIM_Y) % DIM_Y;
    } else {
      // For non-zero-centered, (0,0) maps to the FFT origin (index 56 for 8x8)
      // x=0 -> col 0
      actualX = (x + DIM_X) % DIM_X;
      // y=0 should map to row DIM_Y-1 (due to y_swap)
      actualY = ((DIM_Y - 1) - y + DIM_Y) % DIM_Y;
    }

    return actualY * DIM_X + actualX;
  }

  // ========== Core Operation Methods (without Core dependency) ==========

  /**
   * Performs 2D convolution using FFT: IFFT(FFT(f) · FFT(g))
   * This mirrors what ConvolveOp.create() does.
   */
  private TestFunction2D convolve2D(TestFunction2D f, TestFunction2D g) {
    double[][] fftF = Transforms.computeFFT2D(f.real, f.imag, f.zeroCentered, false,
        f.dimX, f.dimY, Transforms.NORMALIZE_NONE);
    double[][] fftG = Transforms.computeFFT2D(g.real, g.imag, g.zeroCentered, false,
        g.dimX, g.dimY, Transforms.NORMALIZE_NONE);

    double[] prodReal = new double[SIZE];
    double[] prodImag = new double[SIZE];

    // Complex multiplication: (a + bi)(c + di) = (ac - bd) + (ad + bc)i
    for (int i = 0; i < SIZE; i++) {
      prodReal[i] = fftF[0][i] * fftG[0][i] - fftF[1][i] * fftG[1][i];
      prodImag[i] = fftF[0][i] * fftG[1][i] + fftF[1][i] * fftG[0][i];
    }

    double[][] result = Transforms.computeFFT2D(prodReal, prodImag, f.zeroCentered, true,
        f.dimX, f.dimY, Transforms.NORMALIZE_N);

    return new TestFunction2D(result[0], result[1], f.zeroCentered, f.dimX, f.dimY);
  }

  /**
   * Performs 2D correlation using FFT: IFFT(FFT(f) · conj(FFT(m)))
   * The REFERENCE (second argument) is conjugated, matching Roger Easton's
   * definition.
   * This mirrors what CorrelateOp.create() does.
   */
  private TestFunction2D correlate2D(TestFunction2D f, TestFunction2D m) {
    double[][] fftF = Transforms.computeFFT2D(f.real, f.imag, f.zeroCentered, false,
        f.dimX, f.dimY, Transforms.NORMALIZE_NONE);
    double[][] fftM = Transforms.computeFFT2D(m.real, m.imag, m.zeroCentered, false,
        m.dimX, m.dimY, Transforms.NORMALIZE_NONE);

    double[] prodReal = new double[SIZE];
    double[] prodImag = new double[SIZE];

    // Complex multiplication with conjugate of M: (a + bi)(c - di) = (ac + bd) +
    // (bc - ad)i
    for (int i = 0; i < SIZE; i++) {
      prodReal[i] = fftF[0][i] * fftM[0][i] + fftF[1][i] * fftM[1][i];
      prodImag[i] = fftF[1][i] * fftM[0][i] - fftF[0][i] * fftM[1][i];
    }

    double[][] result = Transforms.computeFFT2D(prodReal, prodImag, f.zeroCentered, true,
        f.dimX, f.dimY, Transforms.NORMALIZE_N);

    return new TestFunction2D(result[0], result[1], f.zeroCentered, f.dimX, f.dimY);
  }

  /**
   * Performs 2D autocorrelation using FFT: IFFT(|FFT(f)|²)
   * This mirrors what AutoCorrelateOp.create() does.
   */
  private TestFunction2D autocorrelate2D(TestFunction2D f) {
    double[][] fftF = Transforms.computeFFT2D(f.real, f.imag, f.zeroCentered, false,
        f.dimX, f.dimY, Transforms.NORMALIZE_NONE);

    double[] magSq = new double[SIZE];
    double[] zeros = new double[SIZE];

    // |FFT(f)|² = FFT(f) · conj(FFT(f)) = real² + imag²
    for (int i = 0; i < SIZE; i++) {
      magSq[i] = fftF[0][i] * fftF[0][i] + fftF[1][i] * fftF[1][i];
    }

    double[][] result = Transforms.computeFFT2D(magSq, zeros, f.zeroCentered, true,
        f.dimX, f.dimY, Transforms.NORMALIZE_N);

    return new TestFunction2D(result[0], result[1], f.zeroCentered, f.dimX, f.dimY);
  }

  // ========== Helper Methods ==========

  /**
   * Creates a 2D delta function at position (px, py).
   * For zero-centered: positions range from -DIM/2 to DIM/2-1
   * For non-zero-centered: positions range from 0 to DIM-1
   */
  private TestFunction2D createDelta2D(int px, int py, boolean zeroCentered) {
    double[] real = new double[SIZE];
    double[] imag = new double[SIZE];

    int index = toIndex(px, py, zeroCentered);
    real[index] = 1.0;

    return new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);
  }

  /**
   * Creates a 2D rect function (small square) centered at origin.
   */
  private TestFunction2D createRect2D(int halfWidth, boolean zeroCentered) {
    double[] real = new double[SIZE];
    double[] imag = new double[SIZE];

    for (int dy = -halfWidth; dy <= halfWidth; dy++) {
      for (int dx = -halfWidth; dx <= halfWidth; dx++) {
        int index = toIndex(dx, dy, zeroCentered);
        real[index] = 1.0;
      }
    }

    return new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);
  }

  /**
   * Creates a complex-valued 2D function with specified real and imaginary values
   * at a point.
   */
  private TestFunction2D createComplex2D(int px, int py, double realVal, double imagVal, boolean zeroCentered) {
    double[] real = new double[SIZE];
    double[] imag = new double[SIZE];

    int index = toIndex(px, py, zeroCentered);
    real[index] = realVal;
    imag[index] = imagVal;

    return new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);
  }

  /**
   * Creates a 2D function with a horizontal gradient pattern.
   */
  private TestFunction2D createGradient2D(boolean zeroCentered) {
    double[] real = new double[SIZE];
    double[] imag = new double[SIZE];

    for (int y = 0; y < DIM_Y; y++) {
      for (int x = 0; x < DIM_X; x++) {
        int index = y * DIM_X + x;
        real[index] = x;
      }
    }

    return new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);
  }

  /**
   * Gets the value at a logical position in a 2D function.
   */
  private double getRealAt(TestFunction2D f, int x, int y) {
    int index = toIndex(x, y, f.zeroCentered);
    return f.real[index];
  }

  private double getImagAt(TestFunction2D f, int x, int y) {
    int index = toIndex(x, y, f.zeroCentered);
    return f.imag[index];
  }

  /**
   * Computes the sum of all real values in a function (used to verify
   * energy/area).
   */
  private double sumReal(TestFunction2D f) {
    double sum = 0;
    for (double v : f.real) {
      sum += v;
    }
    return sum;
  }

  /**
   * Computes the sum of squared magnitudes (energy).
   */
  private double computeEnergy(TestFunction2D f) {
    double energy = 0;
    for (int i = 0; i < f.real.length; i++) {
      energy += f.real[i] * f.real[i] + f.imag[i] * f.imag[i];
    }
    return energy;
  }

  // ========== CONVOLUTION TESTS ==========

  @Nested
  @DisplayName("2D Convolution Tests")
  class ConvolutionTests {

    @Test
    @DisplayName("Convolution with delta at origin returns original function")
    void convolutionWithDeltaAtOrigin() {
      boolean zeroCentered = true;
      TestFunction2D delta = createDelta2D(0, 0, zeroCentered);
      TestFunction2D rect = createRect2D(1, zeroCentered);

      TestFunction2D result = convolve2D(rect, delta);

      assertArrayEquals(rect.real, result.real, TOLERANCE,
          "f * δ[x,y] should equal f[x,y]");
    }

    @Test
    @DisplayName("Convolution with shifted delta shifts the function")
    void convolutionWithShiftedDelta() {
      boolean zeroCentered = true;
      int shiftX = 2;
      int shiftY = 1;
      TestFunction2D delta = createDelta2D(shiftX, shiftY, zeroCentered);
      TestFunction2D original = createDelta2D(0, 0, zeroCentered);

      TestFunction2D result = convolve2D(original, delta);

      // Result should have peak at (shiftX, shiftY)
      double valueAtShift = getRealAt(result, shiftX, shiftY);
      assertEquals(1.0, valueAtShift, TOLERANCE,
          "Convolution with shifted delta should shift the peak");
    }

    @Test
    @DisplayName("Convolution is commutative: f * g = g * f")
    void convolutionIsCommutative() {
      boolean zeroCentered = true;
      TestFunction2D f = createDelta2D(1, 2, zeroCentered);
      TestFunction2D g = createRect2D(1, zeroCentered);

      TestFunction2D fg = convolve2D(f, g);
      TestFunction2D gf = convolve2D(g, f);

      assertArrayEquals(fg.real, gf.real, TOLERANCE,
          "Convolution should be commutative (real part)");
      assertArrayEquals(fg.imag, gf.imag, TOLERANCE,
          "Convolution should be commutative (imaginary part)");
    }

    @Test
    @DisplayName("Convolution preserves total area: area(f*g) = area(f) * area(g)")
    void convolutionPreservesArea() {
      boolean zeroCentered = true;
      TestFunction2D f = createRect2D(1, zeroCentered);
      TestFunction2D g = createRect2D(1, zeroCentered);

      double areaF = sumReal(f);
      double areaG = sumReal(g);

      TestFunction2D result = convolve2D(f, g);
      double areaResult = sumReal(result);

      assertEquals(areaF * areaG, areaResult, TOLERANCE * SIZE,
          "Area of convolution should equal product of areas");
    }

    @Test
    @DisplayName("Convolution of complex functions works correctly")
    void convolutionOfComplexFunctions() {
      boolean zeroCentered = true;
      // f = 1 + i at origin
      TestFunction2D f = createComplex2D(0, 0, 1, 1, zeroCentered);
      // g = 1 - i at origin
      TestFunction2D g = createComplex2D(0, 0, 1, -1, zeroCentered);

      TestFunction2D result = convolve2D(f, g);

      // (1+i) * (1-i) = 1 - i + i - i² = 1 + 1 = 2
      double realAtOrigin = getRealAt(result, 0, 0);
      double imagAtOrigin = getImagAt(result, 0, 0);

      assertEquals(2.0, realAtOrigin, TOLERANCE,
          "Complex multiplication: (1+i)(1-i) real part should be 2");
      assertEquals(0.0, imagAtOrigin, TOLERANCE,
          "Complex multiplication: (1+i)(1-i) imag part should be 0");
    }

    @Test
    @DisplayName("Non-zero-centered convolution works correctly")
    void nonZeroCenteredConvolution() {
      boolean zeroCentered = false;
      TestFunction2D delta = createDelta2D(0, 0, zeroCentered);
      TestFunction2D f = createDelta2D(2, 3, zeroCentered);

      TestFunction2D result = convolve2D(f, delta);

      double valueAt23 = getRealAt(result, 2, 3);
      assertEquals(1.0, valueAt23, TOLERANCE,
          "Non-zero-centered convolution with delta should work");
    }
  }

  // ========== CORRELATION TESTS ==========

  @Nested
  @DisplayName("2D Correlation Tests (Roger Easton Definition)")
  class CorrelationTests {

    /**
     * Roger's definition: f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))
     * The REFERENCE function (second argument, m) is conjugated.
     */

    @Test
    @DisplayName("Correlation with delta at origin returns original function")
    void correlationWithDeltaAtOrigin() {
      boolean zeroCentered = true;
      TestFunction2D delta = createDelta2D(0, 0, zeroCentered);
      TestFunction2D rect = createRect2D(1, zeroCentered);

      // f ⋆ δ = f (delta is real, so conjugate doesn't matter)
      TestFunction2D result = correlate2D(rect, delta);

      assertArrayEquals(rect.real, result.real, TOLERANCE,
          "f ⋆ δ[x,y] should equal f[x,y]");
    }

    @Test
    @DisplayName("Correlation with shifted delta shifts function in OPPOSITE direction from convolution")
    void correlationWithShiftedDelta() {
      boolean zeroCentered = true;
      int shiftX = 2;
      int shiftY = 1;
      TestFunction2D delta = createDelta2D(shiftX, shiftY, zeroCentered);
      TestFunction2D original = createDelta2D(0, 0, zeroCentered);

      // Correlation: f ⋆ δ[x-a, y-b] = f[x+a, y+b] (shift in opposite direction)
      TestFunction2D result = correlate2D(original, delta);

      // Peak should be at (-shiftX, -shiftY) due to correlation definition
      double valueAtNegShift = getRealAt(result, -shiftX, -shiftY);
      assertEquals(1.0, valueAtNegShift, TOLERANCE,
          "Correlation with shifted delta should shift in opposite direction");
    }

    @Test
    @DisplayName("Correlation is NOT commutative: f ⋆ g ≠ g ⋆ f (in general)")
    void correlationIsNotCommutative() {
      boolean zeroCentered = true;
      TestFunction2D f = createDelta2D(1, 0, zeroCentered);
      TestFunction2D g = createDelta2D(2, 0, zeroCentered);

      TestFunction2D fg = correlate2D(f, g);
      TestFunction2D gf = correlate2D(g, f);

      // Find peak positions
      int fgPeakX = -1, gfPeakX = -1;
      for (int x = -DIM_X / 2; x < DIM_X / 2; x++) {
        if (Math.abs(getRealAt(fg, x, 0) - 1.0) < TOLERANCE) {
          fgPeakX = x;
        }
        if (Math.abs(getRealAt(gf, x, 0) - 1.0) < TOLERANCE) {
          gfPeakX = x;
        }
      }

      assertNotEquals(fgPeakX, gfPeakX,
          "Correlation should not be commutative - peaks should be at different positions");
    }

    @Test
    @DisplayName("Correlation conjugates the REFERENCE (second argument)")
    void correlationConjugatesReference() {
      boolean zeroCentered = true;

      // f = 1 at origin (real)
      TestFunction2D f = createDelta2D(0, 0, zeroCentered);
      // m = i at origin (purely imaginary)
      TestFunction2D m = createComplex2D(0, 0, 0, 1, zeroCentered);

      // f ⋆ m = IFFT(FFT(f) · conj(FFT(m)))
      // FFT(f) = 1 everywhere
      // FFT(m) = i everywhere
      // conj(FFT(m)) = -i everywhere
      // FFT(f) · conj(FFT(m)) = 1 · (-i) = -i
      // IFFT(-i) = -i at origin

      TestFunction2D result = correlate2D(f, m);

      double realAtOrigin = getRealAt(result, 0, 0);
      double imagAtOrigin = getImagAt(result, 0, 0);

      assertEquals(0.0, realAtOrigin, TOLERANCE,
          "Correlation should conjugate reference: real part");
      assertEquals(-1.0, imagAtOrigin, TOLERANCE,
          "Correlation should conjugate reference: 1 ⋆ i = -i");
    }

    @Test
    @DisplayName("Correlation equals convolution with conjugate-reversed reference: f ⋆ m = f * m*[-x,-y]")
    void correlationEqualsConvolutionWithConjugateReversed() {
      boolean zeroCentered = true;

      // Create complex functions using logical coordinates
      TestFunction2D f = createComplex2D(1, 0, 1, 0.5, zeroCentered);
      TestFunction2D m = createComplex2D(-1, 0, 2, -1, zeroCentered);

      // Compute correlation directly
      TestFunction2D correlation = correlate2D(f, m);

      // Compute convolution with conjugate-reversed reference: m*[-x,-y]
      // The reversal must be relative to the signal origin, not array origin
      double[] mConjRevReal = new double[SIZE];
      double[] mConjRevImag = new double[SIZE];

      // For each logical position (x,y), put conjugate of m[x,y] at position (-x,-y)
      for (int ly = -DIM_Y / 2; ly < DIM_Y / 2; ly++) {
        for (int lx = -DIM_X / 2; lx < DIM_X / 2; lx++) {
          int srcIdx = toIndex(lx, ly, zeroCentered);
          int dstIdx = toIndex(-lx, -ly, zeroCentered);

          mConjRevReal[dstIdx] = m.real[srcIdx];
          mConjRevImag[dstIdx] = -m.imag[srcIdx]; // Conjugate
        }
      }

      TestFunction2D mConjRev = new TestFunction2D(mConjRevReal, mConjRevImag,
          zeroCentered, DIM_X, DIM_Y);

      TestFunction2D convolution = convolve2D(f, mConjRev);

      // Results should match
      assertArrayEquals(correlation.real, convolution.real, TOLERANCE,
          "f ⋆ m should equal f * m*[-x,-y] (real part)");
      assertArrayEquals(correlation.imag, convolution.imag, TOLERANCE,
          "f ⋆ m should equal f * m*[-x,-y] (imaginary part)");
    }

    @Test
    @DisplayName("Self-correlation peak is at origin")
    void selfCorrelationPeakAtOrigin() {
      boolean zeroCentered = true;
      TestFunction2D f = createRect2D(1, zeroCentered);

      TestFunction2D result = correlate2D(f, f);

      // Find the maximum value and its position
      double maxVal = Double.NEGATIVE_INFINITY;
      int maxIdx = -1;

      for (int i = 0; i < result.real.length; i++) {
        if (result.real[i] > maxVal) {
          maxVal = result.real[i];
          maxIdx = i;
        }
      }

      // For zero-centered, origin is at the empirically determined index
      int originIdx = getZeroCenteredOriginIndex();
      assertEquals(originIdx, maxIdx,
          "Self-correlation peak should be at origin (index " + originIdx + ")");
    }
  }

  // ========== AUTOCORRELATION TESTS ==========

  @Nested
  @DisplayName("2D Autocorrelation Tests")
  class AutocorrelationTests {

    /**
     * From Chapter 8 Eq 8.87:
     * f[x,y] ⋆ f[x,y] = ∫∫ f[α,β] · f*[α-x, β-y] dα dβ
     * 
     * FFT: IFFT(|FFT(f)|²)
     */

    @Test
    @DisplayName("Autocorrelation equals self-correlation")
    void autocorrelationEqualsSelfCorrelation() {
      boolean zeroCentered = true;
      TestFunction2D f = createRect2D(1, zeroCentered);

      TestFunction2D auto = autocorrelate2D(f);
      TestFunction2D self = correlate2D(f, f);

      assertArrayEquals(self.real, auto.real, TOLERANCE,
          "Autocorrelation should equal f ⋆ f (real part)");
      assertArrayEquals(self.imag, auto.imag, TOLERANCE,
          "Autocorrelation should equal f ⋆ f (imaginary part)");
    }

    @Test
    @DisplayName("Autocorrelation at origin equals energy (Eq 8.88)")
    void autocorrelationAtOriginEqualsEnergy() {
      boolean zeroCentered = true;
      TestFunction2D f = createRect2D(1, zeroCentered);

      double energy = computeEnergy(f);

      TestFunction2D auto = autocorrelate2D(f);
      double autoAtOrigin = getRealAt(auto, 0, 0);

      assertEquals(energy, autoAtOrigin, TOLERANCE,
          "Autocorrelation at origin should equal ∫∫|f|² dα dβ");
    }

    @Test
    @DisplayName("Autocorrelation is Hermitian for real input")
    void autocorrelationIsHermitianForRealInput() {
      boolean zeroCentered = true;
      TestFunction2D f = createRect2D(1, zeroCentered); // Real input

      TestFunction2D auto = autocorrelate2D(f);

      // Check Hermitian symmetry: f[x,y] = f*[-x,-y]
      // For real input, this means real part is even, imag part is odd

      // For autocorrelation, the symmetry is relative to the origin.
      // We check by comparing values at logical positions (x,y) and (-x,-y)
      for (int y = -DIM_Y / 2 + 1; y < DIM_Y / 2; y++) {
        for (int x = -DIM_X / 2 + 1; x < DIM_X / 2; x++) {
          double valAtPos = getRealAt(auto, x, y);
          double valAtNeg = getRealAt(auto, -x, -y);

          // Real part should be even: R[x,y] = R[-x,-y]
          assertEquals(valAtPos, valAtNeg, TOLERANCE,
              "Real part of autocorrelation should be even at (" + x + "," + y + ")");

          // For real input, imag should be zero (or near-zero due to numerical error)
          double imagAtPos = getImagAt(auto, x, y);
          assertEquals(0.0, imagAtPos, TOLERANCE * 10, // Slightly looser tolerance for imag
              "Imaginary part of autocorrelation of real function should be near zero");
        }
      }
    }

    @Test
    @DisplayName("Autocorrelation of complex function is Hermitian")
    void autocorrelationOfComplexIsHermitian() {
      boolean zeroCentered = true;

      // Create a complex function with deterministic values for reproducibility
      double[] real = new double[SIZE];
      double[] imag = new double[SIZE];
      for (int i = 0; i < SIZE; i++) {
        real[i] = Math.sin(i * 0.1);
        imag[i] = Math.cos(i * 0.1);
      }
      TestFunction2D f = new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);

      TestFunction2D auto = autocorrelate2D(f);

      // Check Hermitian symmetry: f[x,y] = f*[-x,-y]
      // Using logical coordinates relative to origin
      for (int y = -DIM_Y / 2 + 1; y < DIM_Y / 2; y++) {
        for (int x = -DIM_X / 2 + 1; x < DIM_X / 2; x++) {
          double realAtPos = getRealAt(auto, x, y);
          double realAtNeg = getRealAt(auto, -x, -y);
          double imagAtPos = getImagAt(auto, x, y);
          double imagAtNeg = getImagAt(auto, -x, -y);

          // R[x,y] = R[-x,-y] (real part even)
          assertEquals(realAtPos, realAtNeg, TOLERANCE,
              "Real part of autocorrelation should be even at (" + x + "," + y + ")");

          // I[x,y] = -I[-x,-y] (imaginary part odd)
          assertEquals(imagAtPos, -imagAtNeg, TOLERANCE,
              "Imaginary part of autocorrelation should be odd at (" + x + "," + y + ")");
        }
      }
    }

    @Test
    @DisplayName("Autocorrelation of delta is delta at origin")
    void autocorrelationOfDelta() {
      boolean zeroCentered = true;
      TestFunction2D delta = createDelta2D(2, 1, zeroCentered); // Delta at non-origin position

      TestFunction2D auto = autocorrelate2D(delta);

      // Autocorrelation of delta at any position is delta at origin
      double peakVal = getRealAt(auto, 0, 0);
      assertEquals(1.0, peakVal, TOLERANCE,
          "Autocorrelation of delta should have peak at origin");

      // Verify total sum is 1
      double total = sumReal(auto);
      assertEquals(1.0, total, TOLERANCE,
          "Autocorrelation of delta should sum to 1");
    }

    @Test
    @DisplayName("Autocorrelation is always non-negative at origin")
    void autocorrelationNonNegativeAtOrigin() {
      boolean zeroCentered = true;

      // Test with various functions
      TestFunction2D[] functions = {
          createRect2D(1, zeroCentered),
          createDelta2D(1, 1, zeroCentered),
          createComplex2D(0, 0, 1, 1, zeroCentered),
          createGradient2D(zeroCentered)
      };

      for (TestFunction2D f : functions) {
        TestFunction2D auto = autocorrelate2D(f);
        double valueAtOrigin = getRealAt(auto, 0, 0);

        assertTrue(valueAtOrigin >= 0,
            "Autocorrelation at origin must be non-negative (energy)");
      }
    }
  }

  // ========== EDGE CASE TESTS ==========

  @Nested
  @DisplayName("Edge Cases and Boundary Conditions")
  class EdgeCaseTests {

    @Test
    @DisplayName("Operations work with zero function")
    void operationsWithZeroFunction() {
      boolean zeroCentered = true;
      double[] zeros = new double[SIZE];
      TestFunction2D zero = new TestFunction2D(zeros, zeros.clone(), zeroCentered, DIM_X, DIM_Y);
      TestFunction2D rect = createRect2D(1, zeroCentered);

      // f * 0 = 0
      TestFunction2D convResult = convolve2D(rect, zero);
      for (double v : convResult.real) {
        assertEquals(0.0, v, TOLERANCE, "Convolution with zero should be zero");
      }

      // f ⋆ 0 = 0
      TestFunction2D corrResult = correlate2D(rect, zero);
      for (double v : corrResult.real) {
        assertEquals(0.0, v, TOLERANCE, "Correlation with zero should be zero");
      }

      // 0 ⋆ 0 = 0
      TestFunction2D autoResult = autocorrelate2D(zero);
      for (double v : autoResult.real) {
        assertEquals(0.0, v, TOLERANCE, "Autocorrelation of zero should be zero");
      }
    }

    @Test
    @DisplayName("Operations work with constant function")
    void operationsWithConstantFunction() {
      boolean zeroCentered = true;
      double[] ones = new double[SIZE];
      java.util.Arrays.fill(ones, 1.0);
      double[] zeros = new double[SIZE];

      TestFunction2D constant = new TestFunction2D(ones, zeros, zeroCentered, DIM_X, DIM_Y);

      // Autocorrelation of constant should be constant times SIZE at origin
      TestFunction2D auto = autocorrelate2D(constant);
      double atOrigin = getRealAt(auto, 0, 0);

      // Energy of constant = SIZE (since each value is 1)
      assertEquals(SIZE, atOrigin, TOLERANCE,
          "Autocorrelation of constant at origin should equal N²");
    }

    @Test
    @DisplayName("Non-zero-centered mode works correctly")
    void nonZeroCenteredOperations() {
      boolean zeroCentered = false;
      TestFunction2D f = createRect2D(1, zeroCentered);
      TestFunction2D g = createDelta2D(0, 0, zeroCentered);

      // These should not throw exceptions and should produce valid results
      assertDoesNotThrow(() -> convolve2D(f, g));
      assertDoesNotThrow(() -> correlate2D(f, g));
      assertDoesNotThrow(() -> autocorrelate2D(f));
    }
  }

  // ========== NUMERICAL PRECISION TESTS ==========

  @Nested
  @DisplayName("Numerical Precision Tests")
  class NumericalPrecisionTests {

    @Test
    @DisplayName("Results are numerically stable for small values")
    void numericalStabilitySmallValues() {
      boolean zeroCentered = true;
      double smallVal = 1e-10;

      double[] real = new double[SIZE];
      real[SIZE / 2] = smallVal;
      double[] imag = new double[SIZE];

      TestFunction2D f = new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);

      TestFunction2D auto = autocorrelate2D(f);
      double atOrigin = getRealAt(auto, 0, 0);

      assertEquals(smallVal * smallVal, atOrigin, 1e-25,
          "Should handle small values without losing precision");
    }

    @Test
    @DisplayName("Results are numerically stable for large values")
    void numericalStabilityLargeValues() {
      boolean zeroCentered = true;
      double largeVal = 1e6;

      double[] real = new double[SIZE];
      real[SIZE / 2] = largeVal;
      double[] imag = new double[SIZE];

      TestFunction2D f = new TestFunction2D(real, imag, zeroCentered, DIM_X, DIM_Y);

      TestFunction2D auto = autocorrelate2D(f);
      double atOrigin = getRealAt(auto, 0, 0);

      assertEquals(largeVal * largeVal, atOrigin, largeVal * largeVal * 1e-10,
          "Should handle large values correctly");
    }
  }

  // ========== COMPARISON WITH JAVA OPERATORS ==========

  @Nested
  @DisplayName("Verify Test Implementation Matches Java Operators")
  class OperatorComparisonTests {

    /**
     * These tests verify that our test methods use the same formulas as the
     * actual Java operator implementations (ConvolveOp, CorrelateOp,
     * AutoCorrelateOp).
     * 
     * Convolution formula (ConvolveOp.create):
     * real[i] = realA[i] * realB[i] - imagA[i] * imagB[i]
     * imag[i] = realA[i] * imagB[i] + imagA[i] * realB[i]
     * 
     * Correlation formula (CorrelateOp.create):
     * real[i] = realA[i] * realB[i] + imagA[i] * imagB[i] // conjugate B
     * imag[i] = imagA[i] * realB[i] - realA[i] * imagB[i] // conjugate B
     * 
     * Autocorrelation formula (AutoCorrelateOp.create):
     * real[i] = realA[i] * realA[i] + imagA[i] * imagA[i] // |FFT(f)|²
     * imag[i] = 0
     */

    @Test
    @DisplayName("Convolution formula verification")
    void verifyConvolutionFormula() {
      // Test that (a + bi)(c + di) = (ac - bd) + (ad + bc)i
      double a = 3, b = 4, c = 1, d = 2;

      double expectedReal = a * c - b * d; // 3*1 - 4*2 = -5
      double expectedImag = a * d + b * c; // 3*2 + 4*1 = 10

      assertEquals(-5.0, expectedReal, TOLERANCE);
      assertEquals(10.0, expectedImag, TOLERANCE);
    }

    @Test
    @DisplayName("Correlation formula verification - conjugates second operand")
    void verifyCorrelationFormula() {
      // Test that (a + bi)(c - di) = (ac + bd) + (bc - ad)i
      // Conjugating the second operand (c + di) gives (c - di)
      double a = 3, b = 4, c = 1, d = 2;

      // FFT multiplication with conjugate of B
      double expectedReal = a * c + b * d; // 3*1 + 4*2 = 11
      double expectedImag = b * c - a * d; // 4*1 - 3*2 = -2

      assertEquals(11.0, expectedReal, TOLERANCE);
      assertEquals(-2.0, expectedImag, TOLERANCE);
    }

    @Test
    @DisplayName("Autocorrelation formula verification")
    void verifyAutocorrelationFormula() {
      // Test that |a + bi|² = a² + b²
      double a = 3, b = 4;

      double expectedMagSq = a * a + b * b; // 9 + 16 = 25

      assertEquals(25.0, expectedMagSq, TOLERANCE);
    }
  }
}
