package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import signals.gui.Colors;
import signals.operation.ConvolveOp;
import signals.operation.CorrelateOp;
import signals.operation.FourierTransformOp;
import signals.operation.RealOp;
import signals.operation.ThresholdOp;

/** Ordinary complex-image chains checked against spatial sums and a direct DFT. */
class FilteringChainWorkflowTest {
  private static final double TOLERANCE = 1e-10;
  private FunctionCreationOptions oldCreation;
  private DisplayOptions oldDisplay;
  private Colors oldColors;

  @BeforeEach void initializeServices() {
    Core core = Core.get();
    oldCreation = core.functionCreationOptions;
    oldDisplay = core.displayOptions;
    oldColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach void restoreServices() {
    Core core = Core.get();
    core.functionCreationOptions = oldCreation;
    core.displayOptions = oldDisplay;
    core.colors = oldColors;
  }

  static Stream<Arguments> geometries() {
    return Stream.of(Arguments.of(32, 16, false), Arguments.of(32, 16, true),
        Arguments.of(16, 32, false), Arguments.of(16, 32, true));
  }

  static Stream<Arguments> filteringCases() {
    return Stream.of(false, true).flatMap(correlation -> geometries().map(a -> {
      Object[] geometry = a.get();
      return Arguments.of(geometry[0], geometry[1], geometry[2], correlation);
    }));
  }

  @ParameterizedTest(name = "{0}x{1}, centered={2}, correlation={3}")
  @MethodSource("filteringCases")
  void filteringAndThresholdFollowSourceEditsAndKernelReplacement(
      int width, int height, boolean centered, boolean correlation) {
    double[][] input = imageSamples(width, height, 1.0);
    double[][] kernel = kernelSamples(width, height, centered, 0);
    Function2D source = sampled(input, width, height, centered);
    Function2D reference = sampled(kernel, width, height, centered);
    BinaryOperation operation = correlation ? new CorrelateOp(new ParameterBlock())
        : new ConvolveOp(new ParameterBlock());
    OperatorSystem2D filtered = system(operation, new FunctionProducer[] {source, reference},
        FUNCTION, BINARY_OP_TIER_2, FUNCTION);
    OperatorSystem2D real = unary(new RealOp(new ParameterBlock()), filtered);
    OperatorSystem2D mask = unary(new ThresholdOp(new ParameterBlock()), real);

    for (int stage = 0; stage < 3; stage++) {
      if (stage == 1) {
        input = imageSamples(width, height, 1.6);
        Function2D edited = sampled(input, width, height, centered);
        source.setPartA(edited.getPartA());
        source.setPartB(edited.getPartB());
      } else if (stage == 2) {
        kernel = kernelSamples(width, height, centered, 3);
        reference = sampled(kernel, width, height, centered);
        filtered.setInputList(new ArrayList<>(Arrays.asList(source, reference)));
      }
      double[][] expected = spatialFilter(input, kernel, width, height, centered, correlation);
      assertSamples(filtered.getFunction(), expected, width, height, centered);
      double[][] expectedMask = new double[2][width * height];
      for (int i = 0; i < expectedMask[0].length; i++) {
        expectedMask[0][i] = expected[0][i] >= 0.5 ? 1 : 0;
      }
      Function masked = mask.getFunction();
      assertSamples(masked, expectedMask, width, height, centered);
      assertSame(masked, mask.getFunction(), "Unchanged linked result should remain cached");
      assertSamples(source, input, width, height, centered);
      assertSamples(reference, kernel, width, height, centered);
    }
  }

  @ParameterizedTest(name = "FFT chain {0}x{1}, centered={2}")
  @MethodSource("geometries")
  void forwardAndInverseFftPreserveEditedComplexImages(int width, int height, boolean centered) {
    double[][] input = imageSamples(width, height, 1.0);
    Function2D source = sampled(input, width, height, centered);
    OperatorSystem2D forward = unary(new FourierTransformOp(new ParameterBlock()), source);
    FourierTransformOp inverse = new FourierTransformOp(new ParameterBlock());
    inverse.setInverse(true);
    OperatorSystem2D recovered = unary(inverse, forward);
    for (double amplitude : new double[] {1.0, 1.6}) {
      if (amplitude != 1.0) {
        input = imageSamples(width, height, amplitude);
        Function2D edited = sampled(input, width, height, centered);
        source.setPartA(edited.getPartA());
        source.setPartB(edited.getPartB());
      }
      assertSamples(forward.getFunction(), directDft(input, width, height, centered),
          width, height, centered);
      assertSamples(recovered.getFunction(), input, width, height, centered);
      assertSamples(source, input, width, height, centered);
    }
  }

  private static double[][] imageSamples(int width, int height, double amplitude) {
    double[][] samples = new double[2][width * height];
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        double x = (col - width * 0.6) / 4.0;
        double y = (row - height * 0.4) / 3.0;
        int i = row * width + col;
        samples[0][i] = amplitude * (0.2 + Math.exp(-0.5 * (x * x + y * y)));
        samples[1][i] = amplitude * 0.15 * Math.sin(2 * Math.PI * col / width)
            * Math.cos(2 * Math.PI * row / height);
      }
    }
    return samples;
  }

  private static double[][] kernelSamples(int width, int height, boolean centered, int shift) {
    double[][] kernel = new double[2][width * height];
    int ox = centered ? width / 2 : 0;
    int oy = centered ? height / 2 - 1 : height - 1;
    double total = 0;
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int x = Math.floorMod(col - ox - shift + width / 2, width) - width / 2;
        int y = Math.floorMod(oy - row + height / 2, height) - height / 2;
        int i = row * width + col;
        kernel[0][i] = Math.exp(-0.5 * (x * x / 4.0 + y * y / 2.25));
        kernel[1][i] = 0.1 * x * kernel[0][i];
        total += kernel[0][i];
      }
    }
    for (int i = 0; i < width * height; i++) {
      kernel[0][i] /= total;
      kernel[1][i] /= total;
    }
    return kernel;
  }

  // Direct circular sums in image coordinates, independent of production FFT helpers.
  private static double[][] spatialFilter(double[][] a, double[][] b, int width,
      int height, boolean centered, boolean correlation) {
    double[][] result = new double[2][width * height];
    int ox = centered ? width / 2 : 0;
    int oy = centered ? height / 2 - 1 : height - 1;
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int out = row * width + col;
        for (int ar = 0; ar < height; ar++) {
          int br = Math.floorMod(oy + (correlation ? ar - row : row - ar), height);
          for (int ac = 0; ac < width; ac++) {
            int bc = Math.floorMod(ox + (correlation ? ac - col : col - ac), width);
            int ai = ar * width + ac;
            int bi = br * width + bc;
            double imaginaryB = correlation ? -b[1][bi] : b[1][bi];
            result[0][out] += a[0][ai] * b[0][bi] - a[1][ai] * imaginaryB;
            result[1][out] += a[1][ai] * b[0][bi] + a[0][ai] * imaginaryB;
          }
        }
      }
    }
    return result;
  }

  private static double[][] directDft(double[][] input, int width, int height, boolean centered) {
    double[][] output = new double[2][width * height];
    int ox = centered ? width / 2 : 0;
    int oy = centered ? height / 2 - 1 : height - 1;
    double scale = Math.sqrt(width * height);
    for (int fr = 0; fr < height; fr++) {
      for (int fc = 0; fc < width; fc++) {
        int out = fr * width + fc;
        for (int row = 0; row < height; row++) {
          for (int col = 0; col < width; col++) {
            double phase = -2 * Math.PI * ((double) (col - ox) * (fc - ox) / width
                + (double) (oy - row) * (oy - fr) / height);
            double c = Math.cos(phase), s = Math.sin(phase);
            int i = row * width + col;
            output[0][out] += input[0][i] * c - input[1][i] * s;
            output[1][out] += input[1][i] * c + input[0][i] * s;
          }
        }
        output[0][out] /= scale;
        output[1][out] /= scale;
      }
    }
    return output;
  }

  private static Function2D sampled(double[][] values, int width, int height, boolean centered) {
    return (Function2D) FunctionFactory.createFunction2D(values[0].clone(), values[1].clone(), centered,
        "filtering workflow", width, height);
  }

  private static OperatorSystem2D unary(Operation operation, FunctionProducer source) {
    return system(operation, new FunctionProducer[] {source},
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN);
  }

  private static OperatorSystem2D system(Operation operation, FunctionProducer[] inputs, int... code) {
    ArrayList<Operation> operations = new ArrayList<>(Arrays.asList(operation));
    return new OperatorSystem2D(new CombineOpsRule(code, operations),
        new ArrayList<>(Arrays.asList(inputs)), operations);
  }

  private static void assertSamples(Function result, double[][] expected, int width,
      int height, boolean centered) {
    Function2D image = assertInstanceOf(Function2D.class, result);
    assertEquals(width, image.getDimensionX());
    assertEquals(height, image.getDimensionY());
    assertEquals(centered, image.isZeroCentered());
    assertArrayEquals(expected[0], image.getReal(), TOLERANCE, "real samples");
    assertArrayEquals(expected[1], image.getImaginary(), TOLERANCE, "imaginary samples");
  }
}
