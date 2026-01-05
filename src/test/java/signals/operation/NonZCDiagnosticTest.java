package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Diagnostic tests to understand the index layout for non-zero-centered arrays.
 */
public class NonZCDiagnosticTest {

  private static final int DIM_X = 8;
  private static final int DIM_Y = 8;
  private static final int SIZE = DIM_X * DIM_Y;

  @Test
  @DisplayName("Find FFT origin for non-zero-centered data")
  void testFindNZCOrigin() {
    double[] zeros = new double[SIZE];

    System.out.println("\n=== Searching for non-zero-centered FFT origin ===");
    System.out.println("Looking for delta position that gives constant FFT spectrum...\n");

    // Test each position
    for (int idx = 0; idx < SIZE; idx++) {
      double[] delta = new double[SIZE];
      delta[idx] = 1.0;

      // Non-zero-centered FFT
      double[][] fft = Transforms.computeFFT2D(delta, zeros, false, false,
          DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);

      // Check if all real values are approximately 1.0 and all imag are 0
      boolean allOnes = true;
      for (int i = 0; i < SIZE; i++) {
        if (Math.abs(fft[0][i] - 1.0) > 1e-10 || Math.abs(fft[1][i]) > 1e-10) {
          allOnes = false;
          break;
        }
      }

      if (allOnes) {
        int row = idx / DIM_X;
        int col = idx % DIM_X;
        System.out.println("FOUND: Delta at index " + idx + " (row=" + row + ", col=" + col + ") gives constant FFT!");
      }
    }
  }

  @Test
  @DisplayName("Test convolution with delta at index 0 (non-ZC)")
  void testConvolutionDeltaAt0() {
    // Create a simple pattern
    double[] pattern = new double[SIZE];
    double[] zeros = new double[SIZE];
    pattern[0] = 1.0;
    pattern[1] = 2.0;
    pattern[DIM_X] = 3.0;

    // Create delta at index 0
    double[] delta = new double[SIZE];
    delta[0] = 1.0;

    // Convolve pattern with delta at 0 (non-zero-centered)
    double[][] fftPattern = Transforms.computeFFT2D(pattern, zeros, false, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);
    double[][] fftDelta = Transforms.computeFFT2D(delta, zeros, false, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);

    // Multiply
    double[] prodReal = new double[SIZE];
    double[] prodImag = new double[SIZE];
    for (int i = 0; i < SIZE; i++) {
      prodReal[i] = fftPattern[0][i] * fftDelta[0][i] - fftPattern[1][i] * fftDelta[1][i];
      prodImag[i] = fftPattern[0][i] * fftDelta[1][i] + fftPattern[1][i] * fftDelta[0][i];
    }

    // IFFT
    double[][] result = Transforms.computeFFT2D(prodReal, prodImag, false, true,
        DIM_X, DIM_Y, Transforms.NORMALIZE_N);

    System.out.println("\n=== Non-ZC Convolution of pattern with delta at 0 ===");
    System.out.println("Input pattern values at 0,1,8: " + pattern[0] + ", " + pattern[1] + ", " + pattern[DIM_X]);
    System.out.println("Result values at 0,1,8: " + result[0][0] + ", " + result[0][1] + ", " + result[0][DIM_X]);

    System.out.println("\nAll non-zero results:");
    for (int i = 0; i < SIZE; i++) {
      if (Math.abs(result[0][i]) > 1e-10) {
        System.out.printf("  idx %d: %.6f%n", i, result[0][i]);
      }
    }

    // Show FFT of delta at 0
    System.out.println("\nFFT of delta at 0 (first 16 real values):");
    for (int i = 0; i < 16; i++) {
      System.out.printf("%.4f ", fftDelta[0][i]);
    }
    System.out.println();
  }

  @Test
  @DisplayName("Test Hermitian symmetry index mapping")
  void testHermitianSymmetry() {
    boolean zeroCentered = true;

    // For Hermitian symmetry check, we compare f[x,y] with f*[-x,-y]
    // The negation formula for indices needs to account for wrap-around

    System.out.println("\n=== Hermitian symmetry index pairs ===");
    System.out.println("For an 8x8 zero-centered array:");

    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < DIM_X; x++) {
        int idx = y * DIM_X + x;

        // Standard negation with wrap-around
        int negX = (DIM_X - x) % DIM_X;
        int negY = (DIM_Y - y) % DIM_Y;
        int negIdx = negY * DIM_X + negX;

        if (idx <= negIdx) { // Only show unique pairs
          System.out.printf("  (%d,%d) idx=%d  <->  (%d,%d) idx=%d%n",
              x, y, idx, negX, negY, negIdx);
        }
      }
    }
  }
}
