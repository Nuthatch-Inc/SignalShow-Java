package signals.operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Diagnostic tests to understand the index layout of 2D arrays in this
 * codebase.
 */
public class DiagnosticTest {

  private static final int DIM_X = 8;
  private static final int DIM_Y = 8;
  private static final int SIZE = DIM_X * DIM_Y;

  @Test
  @DisplayName("Understand y_swap_expand index mapping")
  void testYSwapExpandMapping() {
    // Create a simple array where value at index i equals i
    double[] input = new double[SIZE];
    for (int i = 0; i < SIZE; i++) {
      input[i] = i;
    }

    // Use y_swap_expand to convert to 2D
    double[][] expanded = ArrayUtilities.y_swap_expand(input, DIM_X, DIM_Y);

    System.out.println("=== y_swap_expand mapping ===");
    System.out.println("1D index -> 2D[row][col]:");

    // Print the 2D array
    for (int row = 0; row < DIM_Y; row++) {
      System.out.print("Row " + row + ": ");
      for (int col = 0; col < DIM_X; col++) {
        System.out.printf("%4.0f ", expanded[row][col]);
      }
      System.out.println();
    }

    // Now verify the mapping formula
    System.out.println("\n=== Reverse mapping ===");
    System.out.println("For 1D index i = row * DIM_X + col:");
    System.out.println("The value ends up in expanded[DIM_Y-1-row][col]");

    // Check where index 0 ends up (should be "origin" in some sense)
    for (int row = 0; row < DIM_Y; row++) {
      for (int col = 0; col < DIM_X; col++) {
        if (expanded[row][col] == 0) {
          System.out.println("Index 0 is at 2D position [" + row + "][" + col + "]");
        }
      }
    }
  }

  @Test
  @DisplayName("Understand FFT origin for zero-centered data")
  void testFFTOrigin() {
    // For zero-centered data, where is the "origin"?
    // The checkerboard operation multiplies by (-1)^(i+j) to shift

    // Create delta at index 0 (naive origin)
    double[] deltaAt0 = new double[SIZE];
    deltaAt0[0] = 1.0;
    double[] zeros = new double[SIZE];

    // FFT of delta should be constant (all 1s, before normalization)
    double[][] fft0 = Transforms.computeFFT2D(deltaAt0, zeros, false, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);

    System.out.println("\n=== FFT of delta at index 0 (non-zero-centered) ===");
    System.out.println("Real part should be all 1.0:");
    printFirstFew(fft0[0]);

    // Now with zero-centered
    double[][] fft0zc = Transforms.computeFFT2D(deltaAt0, zeros, true, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);

    System.out.println("\n=== FFT of delta at index 0 (zero-centered) ===");
    System.out.println("Real part (with checkerboard shift):");
    printFirstFew(fft0zc[0]);

    // Create delta at center index (DIM_Y/2 * DIM_X + DIM_X/2)
    int centerIdx = (DIM_Y / 2) * DIM_X + (DIM_X / 2);
    System.out.println("\n=== Center index calculation ===");
    System.out.println("Center index = (DIM_Y/2) * DIM_X + (DIM_X/2) = " + centerIdx);

    double[] deltaAtCenter = new double[SIZE];
    deltaAtCenter[centerIdx] = 1.0;

    double[][] fftCenter = Transforms.computeFFT2D(deltaAtCenter, zeros, false, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);

    System.out.println("\n=== FFT of delta at center index " + centerIdx + " (non-zero-centered) ===");
    System.out.println("Real part:");
    printFirstFew(fftCenter[0]);

    double[][] fftCenterZC = Transforms.computeFFT2D(deltaAtCenter, zeros, true, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);

    System.out.println("\n=== FFT of delta at center index " + centerIdx + " (zero-centered) ===");
    System.out.println("Real part (should be all 1.0 if this is true origin):");
    printFirstFew(fftCenterZC[0]);
  }

  @Test
  @DisplayName("Find where FFT gives constant spectrum")
  void testFindTrueOrigin() {
    double[] zeros = new double[SIZE];

    System.out.println("\n=== Searching for true FFT origin ===");
    System.out.println("Looking for delta position that gives constant FFT spectrum...\n");

    // Test each position
    for (int idx = 0; idx < SIZE; idx++) {
      double[] delta = new double[SIZE];
      delta[idx] = 1.0;

      // Zero-centered FFT
      double[][] fft = Transforms.computeFFT2D(delta, zeros, true, false,
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
  @DisplayName("Test convolution with delta")
  void testConvolutionDelta() {
    // Create a simple pattern
    double[] pattern = new double[SIZE];
    double[] zeros = new double[SIZE];
    pattern[0] = 1.0;
    pattern[1] = 2.0;
    pattern[DIM_X] = 3.0;

    // Create delta at different positions
    double[] delta0 = new double[SIZE];
    delta0[0] = 1.0;

    // Convolve pattern with delta at 0 (non-zero-centered)
    double[][] fftPattern = Transforms.computeFFT2D(pattern, zeros, false, false,
        DIM_X, DIM_Y, Transforms.NORMALIZE_NONE);
    double[][] fftDelta = Transforms.computeFFT2D(delta0, zeros, false, false,
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

    System.out.println("\n=== Convolution of pattern with delta at 0 (non-ZC) ===");
    System.out.println(
        "Input pattern values: idx0=" + pattern[0] + ", idx1=" + pattern[1] + ", idx" + DIM_X + "=" + pattern[DIM_X]);
    System.out.println(
        "Result values: idx0=" + result[0][0] + ", idx1=" + result[0][1] + ", idx" + DIM_X + "=" + result[0][DIM_X]);

    // Verify convolution with delta at origin preserves the pattern
    System.out.println("\nComparison:");
    for (int i = 0; i < 16; i++) {
      if (Math.abs(pattern[i]) > 1e-10 || Math.abs(result[0][i]) > 1e-10) {
        System.out.printf("idx %2d: input=%.6f, output=%.6f%n", i, pattern[i], result[0][i]);
      }
    }
  }

  private void printFirstFew(double[] arr) {
    System.out.print("[ ");
    for (int i = 0; i < Math.min(16, arr.length); i++) {
      System.out.printf("%.3f ", arr[i]);
    }
    System.out.println("... ]");
  }
}
