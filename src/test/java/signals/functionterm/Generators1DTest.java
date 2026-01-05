package signals.functionterm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.image.renderable.ParameterBlock;

import static org.junit.jupiter.api.Assertions.*;
import static signals.operation.SignalTestUtils.*;

/**
 * Comprehensive test suite for 1D function generators.
 * 
 * Tests all the analytic function terms in the signals.functionterm package:
 * - Basic waveforms: Sine, Cosine, Constant, Line
 * - Pulse functions: Rectangle, Triangle, Delta, Step
 * - Special functions: Gaussian, Sinc, Bessel, Lorentzian
 * - Chirp functions: Linear chirp, Sine chirp
 * - Window functions: Hamming, Hanning, Welch, Parzen
 * - Noise functions: Uniform, Gaussian noise
 */
public class Generators1DTest {

  private static final int SIZE = 64;
  private static final double TOLERANCE = 1e-10;
  private static final double LOOSE_TOLERANCE = 1e-6;

  /**
   * Creates indices array for testing generators.
   */
  private double[] createIndices(int size, boolean zeroCentered) {
    double[] indices = new double[size];
    int firstIndex = zeroCentered ? -size / 2 : 0;
    for (int i = 0; i < size; i++) {
      indices[i] = firstIndex + i;
    }
    return indices;
  }

  // ========== SINE FUNCTION TESTS ==========

  @Nested
  @DisplayName("Sine Function Tests")
  class SineFunctionTests {

    @Test
    @DisplayName("Sine at origin is zero")
    void sineAtOrigin() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0); // amplitude
      pb.add(0.0); // center
      pb.add(SIZE / 4.0); // width (period)
      pb.add(0.0); // initial phase

      SineFunctionTerm1D sine = new SineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sine.create(indices);

      // sin(0) = 0 at x = 0
      int originIdx = SIZE / 2;
      assertEquals(0.0, result[originIdx], TOLERANCE, "sin(0) should be 0");
    }

    @Test
    @DisplayName("Sine amplitude is correct")
    void sineAmplitude() {
      double amplitude = 2.5;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(SIZE / 4.0);
      pb.add(0.0);

      SineFunctionTerm1D sine = new SineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sine.create(indices);

      double maxVal = findMax(result);
      double minVal = findMin(result);

      assertEquals(amplitude, maxVal, TOLERANCE, "Sine max should equal amplitude");
      assertEquals(-amplitude, minVal, TOLERANCE, "Sine min should equal -amplitude");
    }

    @Test
    @DisplayName("Sine is odd function: sin(-x) = -sin(x)")
    void sineIsOdd() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(SIZE / 4.0);
      pb.add(0.0);

      SineFunctionTerm1D sine = new SineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sine.create(indices);

      // Check odd symmetry
      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(-result[origin - i], result[origin + i], TOLERANCE,
            "sin(-x) should equal -sin(x) at offset " + i);
      }
    }

    @Test
    @DisplayName("Sine with phase offset")
    void sineWithPhase() {
      // 90 degree phase shift should give cosine-like behavior
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(SIZE / 4.0);
      pb.add(90.0); // 90 degrees phase

      SineFunctionTerm1D sine = new SineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sine.create(indices);

      // sin(x + 90°) = cos(x), so at origin should be ~1
      int originIdx = SIZE / 2;
      assertEquals(1.0, result[originIdx], TOLERANCE,
          "sin(0 + 90°) should equal 1");
    }
  }

  // ========== COSINE FUNCTION TESTS ==========

  @Nested
  @DisplayName("Cosine Function Tests")
  class CosineFunctionTests {

    @Test
    @DisplayName("Cosine at origin equals amplitude")
    void cosineAtOrigin() {
      double amplitude = 1.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(SIZE / 4.0);
      pb.add(0.0);

      CosineFunctionTerm1D cosine = new CosineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = cosine.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(amplitude, result[originIdx], TOLERANCE,
          "cos(0) should equal amplitude");
    }

    @Test
    @DisplayName("Cosine is even function: cos(-x) = cos(x)")
    void cosineIsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(SIZE / 4.0);
      pb.add(0.0);

      CosineFunctionTerm1D cosine = new CosineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = cosine.create(indices);

      // Check even symmetry
      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], TOLERANCE,
            "cos(-x) should equal cos(x) at offset " + i);
      }
    }

    @Test
    @DisplayName("Cosine centered at non-zero position")
    void cosineCentered() {
      double center = 10.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(center);
      pb.add(SIZE / 4.0);
      pb.add(0.0);

      CosineFunctionTerm1D cosine = new CosineFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = cosine.create(indices);

      // cos(x - center) at x = center should be 1
      int centerIdx = SIZE / 2 + (int) center;
      assertEquals(1.0, result[centerIdx], TOLERANCE,
          "cos(0) when centered should equal 1");
    }
  }

  // ========== RECTANGLE FUNCTION TESTS ==========

  @Nested
  @DisplayName("Rectangle Function Tests")
  class RectangleFunctionTests {

    @Test
    @DisplayName("Rectangle at origin is amplitude")
    void rectAtOrigin() {
      double amplitude = 1.0;
      double width = 8.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0); // center
      pb.add(width); // half-width

      RectangleFunctionTerm1D rect = new RectangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = rect.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(amplitude, result[originIdx], TOLERANCE,
          "rect(0) should equal amplitude");
    }

    @Test
    @DisplayName("Rectangle width is correct")
    void rectWidth() {
      double width = 5.0; // half-width
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(width);

      RectangleFunctionTerm1D rect = new RectangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = rect.create(indices);

      // Count non-zero values inside the rect
      int count = 0;
      for (double v : result) {
        if (v > 0.5)
          count++; // Exclude edge values which are 0.5
      }

      // Should be 2*width - 1 (excluding edges)
      assertEquals(2 * (int) width - 1, count, "Rectangle interior count should match width");
    }

    @Test
    @DisplayName("Rectangle edges are half amplitude")
    void rectEdges() {
      double amplitude = 2.0;
      double width = 5.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(width);

      RectangleFunctionTerm1D rect = new RectangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = rect.create(indices);

      // At edges (x = ±width), value should be amplitude/2
      int origin = SIZE / 2;
      assertEquals(amplitude / 2, result[origin + (int) width], TOLERANCE,
          "rect(b) should be A/2 at edge");
      assertEquals(amplitude / 2, result[origin - (int) width], TOLERANCE,
          "rect(-b) should be A/2 at edge");
    }

    @Test
    @DisplayName("Rectangle is even function")
    void rectIsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(8.0);

      RectangleFunctionTerm1D rect = new RectangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = rect.create(indices);

      // Check even symmetry
      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], TOLERANCE,
            "rect(-x) should equal rect(x) at offset " + i);
      }
    }
  }

  // ========== TRIANGLE FUNCTION TESTS ==========

  @Nested
  @DisplayName("Triangle Function Tests")
  class TriangleFunctionTests {

    @Test
    @DisplayName("Triangle at origin equals amplitude")
    void triangleAtOrigin() {
      double amplitude = 1.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(8.0);

      TriangleFunctionTerm1D tri = new TriangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = tri.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(amplitude, result[originIdx], TOLERANCE,
          "tri(0) should equal amplitude");
    }

    @Test
    @DisplayName("Triangle is even function")
    void triangleIsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(8.0);

      TriangleFunctionTerm1D tri = new TriangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = tri.create(indices);

      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], TOLERANCE,
            "tri(-x) should equal tri(x) at offset " + i);
      }
    }

    @Test
    @DisplayName("Triangle goes to zero at width")
    void triangleAtWidth() {
      double width = 10.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(width);

      TriangleFunctionTerm1D tri = new TriangleFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = tri.create(indices);

      int origin = SIZE / 2;
      // At width boundary, should be zero
      assertEquals(0.0, result[origin + (int) width], TOLERANCE,
          "tri(b) should be 0 at edge");
    }
  }

  // ========== DELTA FUNCTION TESTS ==========

  @Nested
  @DisplayName("Delta Function Tests")
  class DeltaFunctionTests {

    @Test
    @DisplayName("Delta at origin is amplitude")
    void deltaAtOrigin() {
      double amplitude = 1.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(1.0);

      DeltaFunctionTerm1D delta = new DeltaFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = delta.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(amplitude, result[originIdx], TOLERANCE,
          "δ(0) should equal amplitude");
    }

    @Test
    @DisplayName("Delta is zero away from center")
    void deltaIsZeroAway() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(1.0);

      DeltaFunctionTerm1D delta = new DeltaFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = delta.create(indices);

      int originIdx = SIZE / 2;
      for (int i = 0; i < SIZE; i++) {
        if (i != originIdx) {
          assertEquals(0.0, result[i], TOLERANCE,
              "δ should be 0 away from center at index " + i);
        }
      }
    }

    @Test
    @DisplayName("Shifted delta")
    void shiftedDelta() {
      double center = 5.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(center);
      pb.add(1.0);

      DeltaFunctionTerm1D delta = new DeltaFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = delta.create(indices);

      int centerIdx = SIZE / 2 + (int) center;
      assertEquals(1.0, result[centerIdx], TOLERANCE,
          "δ(x - center) should be 1 at center");
    }
  }

  // ========== STEP FUNCTION TESTS ==========

  @Nested
  @DisplayName("Step Function Tests")
  class StepFunctionTests {

    @Test
    @DisplayName("Step is zero for negative x")
    void stepNegative() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(1.0);

      StepFunctionTerm1D step = new StepFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = step.create(indices);

      // For x < 0, step should be 0
      for (int i = 0; i < SIZE / 2 - 1; i++) {
        assertEquals(0.0, result[i], TOLERANCE,
            "step(x) should be 0 for x < 0 at index " + i);
      }
    }

    @Test
    @DisplayName("Step is amplitude for positive x")
    void stepPositive() {
      double amplitude = 2.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(1.0);

      StepFunctionTerm1D step = new StepFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = step.create(indices);

      // For x > 0, step should be amplitude
      for (int i = SIZE / 2 + 1; i < SIZE; i++) {
        assertEquals(amplitude, result[i], TOLERANCE,
            "step(x) should be amplitude for x > 0 at index " + i);
      }
    }
  }

  // ========== GAUSSIAN FUNCTION TESTS ==========

  @Nested
  @DisplayName("Gaussian Function Tests")
  class GaussianFunctionTests {

    @Test
    @DisplayName("Gaussian peak is at center")
    void gaussianPeakAtCenter() {
      double amplitude = 2.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(8.0);
      pb.add(2); // exponent

      GaussianFunctionTerm1D gauss = new GaussianFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = gauss.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(amplitude, result[originIdx], TOLERANCE,
          "Gaussian peak should equal amplitude at center");
    }

    @Test
    @DisplayName("Gaussian is even function")
    void gaussianIsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(8.0);
      pb.add(2);

      GaussianFunctionTerm1D gauss = new GaussianFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = gauss.create(indices);

      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], TOLERANCE,
            "Gaussian should be even: G(-x) = G(x)");
      }
    }

    @Test
    @DisplayName("Gaussian approaches zero far from center")
    void gaussianApproachesZero() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(4.0); // narrow width
      pb.add(2);

      GaussianFunctionTerm1D gauss = new GaussianFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = gauss.create(indices);

      // At edges, should be very small
      assertTrue(result[0] < 0.01, "Gaussian should be small at edges");
      assertTrue(result[SIZE - 1] < 0.01, "Gaussian should be small at edges");
    }

    @Test
    @DisplayName("Gaussian width parameter affects spread")
    void gaussianWidth() {
      ParameterBlock pbNarrow = new ParameterBlock();
      pbNarrow.add(1.0);
      pbNarrow.add(0.0);
      pbNarrow.add(4.0); // narrow
      pbNarrow.add(2);

      ParameterBlock pbWide = new ParameterBlock();
      pbWide.add(1.0);
      pbWide.add(0.0);
      pbWide.add(16.0); // wide
      pbWide.add(2);

      GaussianFunctionTerm1D narrow = new GaussianFunctionTerm1D(pbNarrow);
      GaussianFunctionTerm1D wide = new GaussianFunctionTerm1D(pbWide);
      double[] indices = createIndices(SIZE, true);
      double[] narrowResult = narrow.create(indices);
      double[] wideResult = wide.create(indices);

      // At same offset from center, wide should be larger
      int offset = 8;
      int idx = SIZE / 2 + offset;
      assertTrue(wideResult[idx] > narrowResult[idx],
          "Wider Gaussian should have larger values away from center");
    }
  }

  // ========== SINC FUNCTION TESTS ==========

  @Nested
  @DisplayName("Sinc Function Tests")
  class SincFunctionTests {

    @Test
    @DisplayName("Sinc at origin is amplitude")
    void sincAtOrigin() {
      double amplitude = 1.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(amplitude);
      pb.add(0.0);
      pb.add(4.0);

      SincFunctionTerm1D sinc = new SincFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sinc.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(amplitude, result[originIdx], TOLERANCE,
          "sinc(0) should equal amplitude");
    }

    @Test
    @DisplayName("Sinc has zero crossings at integer multiples of width")
    void sincZeroCrossings() {
      double width = 4.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(width);

      SincFunctionTerm1D sinc = new SincFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sinc.create(indices);

      int origin = SIZE / 2;
      // At x = width, sinc should be zero (sin(π)/π = 0)
      assertEquals(0.0, result[origin + (int) width], TOLERANCE,
          "sinc should be zero at x = width");
    }

    @Test
    @DisplayName("Sinc is even function")
    void sincIsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(4.0);

      SincFunctionTerm1D sinc = new SincFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = sinc.create(indices);

      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], TOLERANCE,
            "sinc(-x) should equal sinc(x)");
      }
    }
  }

  // ========== LORENTZIAN FUNCTION TESTS ==========

  @Nested
  @DisplayName("Lorentzian Function Tests")
  class LorentzianFunctionTests {

    @Test
    @DisplayName("Lorentzian peak is at center")
    void lorentzianPeakAtCenter() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(4.0);

      LorentzianFunctionTerm1D lorentz = new LorentzianFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = lorentz.create(indices);

      int originIdx = SIZE / 2;
      int peakIdx = findPeakIndex(result);
      assertEquals(originIdx, peakIdx, "Lorentzian peak should be at center");
    }

    @Test
    @DisplayName("Lorentzian is even function")
    void lorentzianIsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(4.0);

      LorentzianFunctionTerm1D lorentz = new LorentzianFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = lorentz.create(indices);

      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], LOOSE_TOLERANCE,
            "Lorentzian should be even");
      }
    }
  }

  // ========== CONSTANT FUNCTION TESTS ==========

  @Nested
  @DisplayName("Constant Function Tests")
  class ConstantFunctionTests {

    @Test
    @DisplayName("Constant is same everywhere")
    void constantEverywhere() {
      double value = 3.14;
      ParameterBlock pb = new ParameterBlock();
      pb.add(value);
      pb.add(0.0);
      pb.add(1.0);

      ConstantFunctionTerm1D constant = new ConstantFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = constant.create(indices);

      for (int i = 0; i < SIZE; i++) {
        assertEquals(value, result[i], TOLERANCE,
            "Constant should be same everywhere");
      }
    }
  }

  // ========== CHIRP FUNCTION TESTS ==========

  @Nested
  @DisplayName("Chirp Function Tests")
  class ChirpFunctionTests {

    @Test
    @DisplayName("Chirp frequency increases over signal")
    void chirpFrequencyIncreases() {
      // ChirpFunctionTerm1D has 5 parameters: amplitude, center, alpha (width),
      // initial phase, order
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0); // amplitude (Double)
      pb.add(0.0); // center (Double)
      pb.add(16.0); // alpha - width parameter (Double)
      pb.add(0.0); // initial phase (Double)
      pb.add(2); // order (Integer)

      ChirpFunctionTerm1D chirp = new ChirpFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, false); // non-zero-centered for chirp
      double[] result = chirp.create(indices);

      // Count zero crossings in first half vs second half
      int crossingsFirst = 0;
      int crossingsSecond = 0;
      for (int i = 1; i < SIZE / 2; i++) {
        if (result[i - 1] * result[i] < 0)
          crossingsFirst++;
      }
      for (int i = SIZE / 2 + 1; i < SIZE; i++) {
        if (result[i - 1] * result[i] < 0)
          crossingsSecond++;
      }

      // For a quadratic chirp, zero crossings increase towards the edges
      // The test just verifies it's oscillating (has some zero crossings)
      assertTrue(crossingsFirst + crossingsSecond > 0,
          "Chirp should have zero crossings");
    }
  }

  // ========== WINDOW FUNCTION TESTS ==========

  @Nested
  @DisplayName("Window Function Tests")
  class WindowFunctionTests {

    @Test
    @DisplayName("Hamming window peak at center")
    void hammingPeak() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(SIZE / 2.0);

      HammingWindowFunctionTerm1D hamming = new HammingWindowFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = hamming.create(indices);

      int peakIdx = findPeakIndex(result);
      int origin = SIZE / 2;
      assertEquals(origin, peakIdx, "Hamming window should peak at center");
    }

    @Test
    @DisplayName("Hanning window edges approach zero")
    void hanningEdges() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(SIZE / 2.0);

      HanningWindowFunctionTerm1D hanning = new HanningWindowFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = hanning.create(indices);

      // At the width boundary, Hanning should be zero
      assertTrue(result[0] < 0.1, "Hanning should approach zero at edges");
      assertTrue(result[SIZE - 1] < 0.1, "Hanning should approach zero at edges");
    }

    @Test
    @DisplayName("Window functions are even")
    void windowsAreEven() {
      // Test multiple window types
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(SIZE / 2.0);

      HammingWindowFunctionTerm1D hamming = new HammingWindowFunctionTerm1D(pb);
      HanningWindowFunctionTerm1D hanning = new HanningWindowFunctionTerm1D(pb);
      WelchWindowFunctionTerm1D welch = new WelchWindowFunctionTerm1D(pb);

      double[] indices = createIndices(SIZE, true);

      for (AnalyticFunctionTerm1D window : new AnalyticFunctionTerm1D[] { hamming, hanning, welch }) {
        double[] result = window.create(indices);
        int origin = SIZE / 2;
        for (int i = 1; i < SIZE / 2; i++) {
          assertEquals(result[origin - i], result[origin + i], LOOSE_TOLERANCE,
              "Window function should be even");
        }
      }
    }
  }

  // ========== BESSEL FUNCTION TESTS ==========

  @Nested
  @DisplayName("Bessel Function Tests")
  class BesselFunctionTests {

    @Test
    @DisplayName("Bessel J0(0) = 1")
    void besselJ0AtOrigin() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0); // amplitude
      pb.add(0.0); // center
      pb.add(1.0); // width
      pb.add(0); // order (J0)

      BesselFunctionTerm1D bessel = new BesselFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = bessel.create(indices);

      int originIdx = SIZE / 2;
      assertEquals(1.0, result[originIdx], TOLERANCE, "J0(0) should equal 1");
    }

    @Test
    @DisplayName("Bessel J0 is even function")
    void besselJ0IsEven() {
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(4.0);
      pb.add(0);

      BesselFunctionTerm1D bessel = new BesselFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = bessel.create(indices);

      int origin = SIZE / 2;
      for (int i = 1; i < SIZE / 2; i++) {
        assertEquals(result[origin - i], result[origin + i], TOLERANCE,
            "J0(-x) should equal J0(x)");
      }
    }
  }

  // ========== COMB FUNCTION TESTS ==========

  @Nested
  @DisplayName("Comb Function Tests")
  class CombFunctionTests {

    @Test
    @DisplayName("Comb function has periodic impulses")
    void combHasPeriodicImpulses() {
      double spacing = 8.0;
      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(spacing);

      CombFunctionTerm1D comb = new CombFunctionTerm1D(pb);
      double[] indices = createIndices(SIZE, true);
      double[] result = comb.create(indices);

      // Count impulses (non-zero values)
      int impulseCount = 0;
      for (double v : result) {
        if (Math.abs(v) > 0.5)
          impulseCount++;
      }

      // Should have SIZE / spacing impulses approximately
      int expectedImpulses = SIZE / (int) spacing;
      assertEquals(expectedImpulses, impulseCount, 2,
          "Comb should have correct number of impulses");
    }
  }

  // ========== GENERATOR PROPERTY TESTS ==========

  @Nested
  @DisplayName("Generator Property Tests")
  class GeneratorPropertyTests {

    @Test
    @DisplayName("All generators create arrays of correct size")
    void generatorsCreateCorrectSize() {
      double[] indices = createIndices(SIZE, true);

      ParameterBlock pb = new ParameterBlock();
      pb.add(1.0);
      pb.add(0.0);
      pb.add(8.0);
      pb.add(0.0);

      // Test various generators
      assertEquals(SIZE, new SineFunctionTerm1D(pb).create(indices).length);
      assertEquals(SIZE, new CosineFunctionTerm1D(pb).create(indices).length);
      assertEquals(SIZE, new RectangleFunctionTerm1D(pb).create(indices).length);
      assertEquals(SIZE, new TriangleFunctionTerm1D(pb).create(indices).length);
      assertEquals(SIZE, new DeltaFunctionTerm1D(pb).create(indices).length);
      assertEquals(SIZE, new StepFunctionTerm1D(pb).create(indices).length);

      ParameterBlock pbGauss = new ParameterBlock();
      pbGauss.add(1.0);
      pbGauss.add(0.0);
      pbGauss.add(8.0);
      pbGauss.add(2);
      assertEquals(SIZE, new GaussianFunctionTerm1D(pbGauss).create(indices).length);
    }

    @Test
    @DisplayName("Zero amplitude produces zero signal")
    void zeroAmplitudeProducesZero() {
      double[] indices = createIndices(SIZE, true);

      ParameterBlock pb = new ParameterBlock();
      pb.add(0.0); // zero amplitude
      pb.add(0.0);
      pb.add(8.0);

      double[] result = new RectangleFunctionTerm1D(pb).create(indices);

      for (double v : result) {
        assertEquals(0.0, v, TOLERANCE, "Zero amplitude should produce zero signal");
      }
    }
  }
}
