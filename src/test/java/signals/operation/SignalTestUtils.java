package signals.operation;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Utility class for signal testing.
 * Provides helper methods for creating test signals, comparing arrays, and
 * computing
 * signal properties without depending on the GUI-based Core singleton.
 */
public class SignalTestUtils {

  public static final double TOLERANCE = 1e-10;
  public static final double LOOSE_TOLERANCE = 1e-6;

  // ========== Signal Creation Utilities ==========

  /**
   * Creates a delta function at the specified position.
   * For zero-centered: position 0 is at the center of the array.
   * For non-zero-centered: position 0 is at array index 0.
   */
  public static double[] createDelta(int size, int position, boolean zeroCentered) {
    double[] delta = new double[size];
    int index = zeroCentered ? position + size / 2 : position;
    if (index >= 0 && index < size) {
      delta[index] = 1.0;
    }
    return delta;
  }

  /**
   * Creates a rectangular pulse centered at position with given half-width.
   */
  public static double[] createRect(int size, int position, int halfWidth, boolean zeroCentered) {
    double[] rect = new double[size];
    int offset = zeroCentered ? size / 2 : 0;
    for (int i = position - halfWidth; i <= position + halfWidth; i++) {
      int index = i + offset;
      if (index >= 0 && index < size) {
        rect[index] = 1.0;
      }
    }
    return rect;
  }

  /**
   * Creates a sine wave with given frequency (cycles per signal length).
   */
  public static double[] createSine(int size, double frequency, double amplitude, boolean zeroCentered) {
    double[] sine = new double[size];
    int firstIndex = zeroCentered ? -size / 2 : 0;
    for (int i = 0; i < size; i++) {
      int x = firstIndex + i;
      sine[i] = amplitude * Math.sin(2 * Math.PI * frequency * x / size);
    }
    return sine;
  }

  /**
   * Creates a cosine wave with given frequency (cycles per signal length).
   */
  public static double[] createCosine(int size, double frequency, double amplitude, boolean zeroCentered) {
    double[] cosine = new double[size];
    int firstIndex = zeroCentered ? -size / 2 : 0;
    for (int i = 0; i < size; i++) {
      int x = firstIndex + i;
      cosine[i] = amplitude * Math.cos(2 * Math.PI * frequency * x / size);
    }
    return cosine;
  }

  /**
   * Creates a Gaussian function centered at position with given width parameter.
   */
  public static double[] createGaussian(int size, double center, double width, double amplitude, boolean zeroCentered) {
    double[] gaussian = new double[size];
    int firstIndex = zeroCentered ? -size / 2 : 0;
    for (int i = 0; i < size; i++) {
      double x = firstIndex + i;
      double arg = Math.abs((x - center) / width);
      gaussian[i] = amplitude * Math.exp(-Math.PI * arg * arg);
    }
    return gaussian;
  }

  /**
   * Creates a triangle function centered at position with given half-width.
   */
  public static double[] createTriangle(int size, int position, int halfWidth, boolean zeroCentered) {
    double[] triangle = new double[size];
    int offset = zeroCentered ? size / 2 : 0;
    for (int i = position - halfWidth; i <= position + halfWidth; i++) {
      int index = i + offset;
      if (index >= 0 && index < size) {
        double dist = Math.abs(i - position);
        triangle[index] = 1.0 - dist / halfWidth;
      }
    }
    return triangle;
  }

  /**
   * Creates a constant signal with given value.
   */
  public static double[] createConstant(int size, double value) {
    double[] constant = new double[size];
    java.util.Arrays.fill(constant, value);
    return constant;
  }

  /**
   * Creates a zero-filled array of given size.
   */
  public static double[] createZeros(int size) {
    return new double[size];
  }

  // ========== Signal Properties ==========

  /**
   * Computes the sum of all values in the signal.
   */
  public static double sum(double[] signal) {
    double total = 0;
    for (double v : signal) {
      total += v;
    }
    return total;
  }

  /**
   * Computes the energy (sum of squared values) of the signal.
   */
  public static double energy(double[] real, double[] imag) {
    double e = 0;
    for (int i = 0; i < real.length; i++) {
      e += real[i] * real[i] + imag[i] * imag[i];
    }
    return e;
  }

  /**
   * Computes the energy of a real signal.
   */
  public static double energy(double[] real) {
    return energy(real, createZeros(real.length));
  }

  /**
   * Finds the index of the maximum value in the signal.
   */
  public static int findPeakIndex(double[] signal) {
    int maxIdx = 0;
    double maxVal = signal[0];
    for (int i = 1; i < signal.length; i++) {
      if (signal[i] > maxVal) {
        maxVal = signal[i];
        maxIdx = i;
      }
    }
    return maxIdx;
  }

  /**
   * Finds the maximum value in the signal.
   */
  public static double findMax(double[] signal) {
    double maxVal = signal[0];
    for (int i = 1; i < signal.length; i++) {
      if (signal[i] > maxVal) {
        maxVal = signal[i];
      }
    }
    return maxVal;
  }

  /**
   * Finds the minimum value in the signal.
   */
  public static double findMin(double[] signal) {
    double minVal = signal[0];
    for (int i = 1; i < signal.length; i++) {
      if (signal[i] < minVal) {
        minVal = signal[i];
      }
    }
    return minVal;
  }

  /**
   * Computes the magnitude of a complex signal.
   */
  public static double[] magnitude(double[] real, double[] imag) {
    double[] mag = new double[real.length];
    for (int i = 0; i < real.length; i++) {
      mag[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
    }
    return mag;
  }

  /**
   * Computes the phase of a complex signal.
   */
  public static double[] phase(double[] real, double[] imag) {
    double[] ph = new double[real.length];
    for (int i = 0; i < real.length; i++) {
      ph[i] = Math.atan2(imag[i], real[i]);
    }
    return ph;
  }

  // ========== Index Conversions ==========

  /**
   * Converts a logical position to array index for 1D signals.
   * For zero-centered: position 0 is at center.
   * For non-zero-centered: position 0 is at index 0.
   */
  public static int toIndex(int position, int size, boolean zeroCentered) {
    if (zeroCentered) {
      return (position + size / 2 + size) % size;
    }
    return (position + size) % size;
  }

  /**
   * Converts array index to logical position for 1D signals.
   */
  public static int toPosition(int index, int size, boolean zeroCentered) {
    if (zeroCentered) {
      return index - size / 2;
    }
    return index;
  }

  /**
   * Gets the index of the origin (position 0) in an array.
   */
  public static int getOriginIndex(int size, boolean zeroCentered) {
    return zeroCentered ? size / 2 : 0;
  }

  // ========== Array Operations ==========

  /**
   * Reverses an array (time-reversal operation).
   */
  public static double[] reverse(double[] signal, boolean zeroCentered) {
    int size = signal.length;
    double[] reversed = new double[size];
    if (zeroCentered) {
      // For zero-centered, reverse around origin
      for (int i = 0; i < size; i++) {
        int pos = toPosition(i, size, true);
        int newIdx = toIndex(-pos, size, true);
        reversed[newIdx] = signal[i];
      }
    } else {
      // For non-zero-centered, simple array reversal with special case for index 0
      reversed[0] = signal[0];
      for (int i = 1; i < size; i++) {
        reversed[size - i] = signal[i];
      }
    }
    return reversed;
  }

  /**
   * Conjugates a complex signal (negates imaginary part).
   */
  public static double[][] conjugate(double[] real, double[] imag) {
    double[] conjReal = real.clone();
    double[] conjImag = new double[imag.length];
    for (int i = 0; i < imag.length; i++) {
      conjImag[i] = -imag[i];
    }
    return new double[][] { conjReal, conjImag };
  }

  /**
   * Shifts a signal by the specified amount (circular shift).
   */
  public static double[] circularShift(double[] signal, int shift) {
    int size = signal.length;
    double[] shifted = new double[size];
    for (int i = 0; i < size; i++) {
      int newIdx = (i + shift + size) % size;
      shifted[newIdx] = signal[i];
    }
    return shifted;
  }

  // ========== Assertion Helpers ==========

  /**
   * Asserts two signals are equal within the default tolerance.
   */
  public static void assertSignalsEqual(double[] expected, double[] actual, String message) {
    assertArrayEquals(expected, actual, TOLERANCE, message);
  }

  /**
   * Asserts two signals are equal within a specified tolerance.
   */
  public static void assertSignalsEqual(double[] expected, double[] actual, double tolerance, String message) {
    assertEquals(expected.length, actual.length, message + " (length mismatch)");
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], actual[i], tolerance,
          message + " (mismatch at index " + i + ")");
    }
  }

  /**
   * Asserts a signal is all zeros (within tolerance).
   */
  public static void assertSignalIsZero(double[] signal, String message) {
    for (int i = 0; i < signal.length; i++) {
      assertEquals(0.0, signal[i], TOLERANCE, message + " (non-zero at index " + i + ")");
    }
  }

  /**
   * Asserts a signal is real (imaginary part is zero).
   */
  public static void assertSignalIsReal(double[] imag, String message) {
    assertSignalIsZero(imag, message);
  }

  /**
   * Asserts a signal is even (symmetric): f(-x) = f(x).
   */
  public static void assertSignalIsEven(double[] signal, boolean zeroCentered, String message) {
    int size = signal.length;
    for (int i = 1; i < size / 2; i++) {
      int pos = zeroCentered ? i : i;
      int neg = zeroCentered ? size - i : size - i;
      assertEquals(signal[pos], signal[neg], TOLERANCE,
          message + " (not even at position " + i + ")");
    }
  }

  /**
   * Asserts a signal is odd (antisymmetric): f(-x) = -f(x).
   */
  public static void assertSignalIsOdd(double[] signal, boolean zeroCentered, String message) {
    int size = signal.length;
    // Check origin is zero
    int origin = zeroCentered ? size / 2 : 0;
    assertEquals(0.0, signal[origin], TOLERANCE, message + " (origin not zero)");
    // Check antisymmetry
    for (int i = 1; i < size / 2; i++) {
      int pos = zeroCentered ? size / 2 + i : i;
      int neg = zeroCentered ? size / 2 - i : size - i;
      assertEquals(signal[pos], -signal[neg], TOLERANCE,
          message + " (not odd at position " + i + ")");
    }
  }

  /**
   * Asserts a complex signal is Hermitian: f(-x) = f*(x).
   */
  public static void assertSignalIsHermitian(double[] real, double[] imag, boolean zeroCentered, String message) {
    int size = real.length;
    for (int i = 1; i < size / 2; i++) {
      int pos = zeroCentered ? size / 2 + i : i;
      int neg = zeroCentered ? size / 2 - i : size - i;
      // Real part even
      assertEquals(real[pos], real[neg], TOLERANCE,
          message + " (real part not even at position " + i + ")");
      // Imaginary part odd
      assertEquals(imag[pos], -imag[neg], TOLERANCE,
          message + " (imaginary part not odd at position " + i + ")");
    }
  }

  // ========== Complex Arithmetic ==========

  /**
   * Complex multiplication: (a + bi)(c + di) = (ac - bd) + (ad + bc)i
   */
  public static double[][] complexMultiply(double[] realA, double[] imagA, double[] realB, double[] imagB) {
    int size = realA.length;
    double[] realOut = new double[size];
    double[] imagOut = new double[size];
    for (int i = 0; i < size; i++) {
      realOut[i] = realA[i] * realB[i] - imagA[i] * imagB[i];
      imagOut[i] = realA[i] * imagB[i] + imagA[i] * realB[i];
    }
    return new double[][] { realOut, imagOut };
  }

  /**
   * Complex multiplication with conjugate of second operand: (a + bi)(c - di) =
   * (ac + bd) + (bc - ad)i
   */
  public static double[][] complexMultiplyConjugate(double[] realA, double[] imagA, double[] realB, double[] imagB) {
    int size = realA.length;
    double[] realOut = new double[size];
    double[] imagOut = new double[size];
    for (int i = 0; i < size; i++) {
      realOut[i] = realA[i] * realB[i] + imagA[i] * imagB[i];
      imagOut[i] = imagA[i] * realB[i] - realA[i] * imagB[i];
    }
    return new double[][] { realOut, imagOut };
  }

  // ========== Time-Domain Reference Implementations ==========

  /**
   * Computes time-domain convolution (reference implementation).
   * f * g [n] = sum_k { f[k] * g[n-k] }
   */
  public static double[] timeDomainConvolve(double[] f, double[] g, boolean zeroCentered) {
    int size = f.length;
    double[] result = new double[size];
    for (int n = 0; n < size; n++) {
      double sum = 0;
      for (int k = 0; k < size; k++) {
        int gIdx = (n - k + size) % size; // Circular indexing
        sum += f[k] * g[gIdx];
      }
      result[n] = sum;
    }
    return result;
  }

  /**
   * Computes time-domain correlation (reference implementation).
   * f ⋆ g [n] = sum_k { f[k] * g*[k-n] }
   * For real signals, this is: sum_k { f[k] * g[k-n] }
   */
  public static double[] timeDomainCorrelate(double[] f, double[] g, boolean zeroCentered) {
    int size = f.length;
    double[] result = new double[size];
    for (int n = 0; n < size; n++) {
      double sum = 0;
      for (int k = 0; k < size; k++) {
        int gIdx = (k - n + size) % size; // Circular indexing for g[k-n]
        sum += f[k] * g[gIdx];
      }
      result[n] = sum;
    }
    return result;
  }

  // ========== Helper for printing signals (debugging) ==========

  /**
   * Prints a signal to stdout (for debugging).
   */
  public static void printSignal(String name, double[] signal) {
    System.out.print(name + ": [");
    for (int i = 0; i < signal.length; i++) {
      System.out.printf("%.6f", signal[i]);
      if (i < signal.length - 1)
        System.out.print(", ");
    }
    System.out.println("]");
  }
}
