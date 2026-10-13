package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.image.renderable.ParameterBlock;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import signals.gui.Colors;
import signals.gui.ParameterEditor;
import signals.gui.operation.OperationOptionsPanel;
import signals.operation.ArrayMath;
import signals.operation.DivideOp;
import signals.operation.MinusOp;
import signals.operation.PlusOp;
import signals.operation.ThresholdOp;
import signals.operation.TimesOp;

/**
 * Exercises production function factories, operations and Swing parameter editors.
 * Only non-window Core services are initialized; no substitute signal/math path
 * is used, and the previous services are restored after every test.
 */
class BasicWorkflowTest {
  private static final double TOLERANCE = 1e-12;
  private FunctionCreationOptions previousCreationOptions;
  private DisplayOptions previousDisplayOptions;
  private Colors previousColors;

  @BeforeEach
  void initializeFunctionServices() {
    Core core = Core.get();
    previousCreationOptions = core.functionCreationOptions;
    previousDisplayOptions = core.displayOptions;
    previousColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach
  void restoreFunctionServices() {
    Core core = Core.get();
    core.functionCreationOptions = previousCreationOptions;
    core.displayOptions = previousDisplayOptions;
    core.colors = previousColors;
  }

  @Test
  void thresholdUsesHighOutputAtAndAboveBoundary() {
    double[] input = {-2, 0.49, 0.5, 0.51, 2};
    assertArrayEquals(new double[] {-3, -3, 7, 7, 7},
        ArrayMath.threshold(input, 0.5, 7, -3), TOLERANCE);
    assertArrayEquals(new double[] {-2, 0.49, 0.5, 0.51, 2}, input);
  }

  @ParameterizedTest(name = "sample creation: image={0}, centered={1}")
  @CsvSource({"false, false", "false, true", "true, false", "true, true"})
  void sampledFunctionsRetainDataAndGeometry(boolean image, boolean centered) {
    double[] real = {1, -2, 0, 3, 4, -1, 2, 0.5};
    double[] imag = {2, 1, 0, -1, 3, 0, -2, 4};
    Function function = sampledFunction(real, imag, image, centered);
    assertSamples(function, real, imag, image, centered);
    function.freeMemory();
    assertSamples(function, real, imag, image, centered);
  }

  @ParameterizedTest(name = "default threshold: image={0}, centered={1}")
  @CsvSource({"false, false", "false, true", "true, false", "true, true"})
  void defaultThresholdPreservesRealSignalAndImageChannels(boolean image, boolean centered) {
    double[] real = {-2, 0.49, 0.5, 1, -0.5, 3, 0, 2};
    double[] imag = new double[real.length];
    Function input = sampledFunction(real, imag, image, centered);
    ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
    Function output = threshold.create(input);
    assertSamples(output, new double[] {0, 0, 1, 1, 0, 1, 0, 1},
        new double[8], image, centered);
    assertSamples(input, real, imag, image, centered);
  }

  @Test
  void thresholdControlsShowDefaultsUnderTheirExistingLabels() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
      OperationOptionsPanel panel = threshold.getOptionsInterface();
      assertEquals(1.0, ((Number) editorNamed(panel, "Maximum").getValue()).doubleValue());
      assertEquals(0.0, ((Number) editorNamed(panel, "Minimum").getValue()).doubleValue());
      assertEquals(0.5, ((Number) editorNamed(panel, "Threshold").getValue()).doubleValue());
    });
  }

  @ParameterizedTest(name = "threshold controls: image={0}, centered={1}")
  @CsvSource({"false, false", "false, true", "true, false", "true, true"})
  void thresholdControlEditsReachBothOutputChannels(boolean image, boolean centered) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      double[] real = {-2, 0.49, 0.5, 1, -0.5, 3, 0, 2};
      double[] imag = {1, 0, 0.5, -2, 3, 2, 0.49, -1};
      Function input = sampledFunction(real, imag, image, centered);
      ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
      OperationOptionsPanel panel = threshold.getOptionsInterface();
      editorNamed(panel, "Maximum").setValue(7.0);
      editorNamed(panel, "Minimum").setValue(-3.0);
      editorNamed(panel, "Threshold").setValue(1.0);
      Function output = threshold.create(input);
      assertSamples(output, new double[] {-3, -3, -3, 7, -3, 7, -3, 7},
          new double[] {7, -3, -3, -3, 7, 7, -3, -3}, image, centered);
      assertSamples(input, real, imag, image, centered);
    });
  }

  @ParameterizedTest(name = "{0}: image={1}, centered={2}")
  @MethodSource("arithmeticCases")
  void arithmeticUsesActualFunctionsAndOperations(String kind, boolean image, boolean centered) {
    double[] realA = {1, -2, 0, 3, 4, -1, 2, 0.5};
    double[] imagA = {2, 1, 0, -1, 3, 0, -2, 4};
    double[] realB = {2, 1, 4, -2, 1, 3, -1, 2};
    double[] imagB = {0, -1, 2, 1, -2, 1, 1, 0};
    Function inputA = sampledFunction(realA, imagA, image, centered);
    Function inputB = sampledFunction(realB, imagB, image, centered);
    BinaryOperation operation;
    double[] expectedReal;
    double[] expectedImag;
    // Explicit complex-arithmetic reference values, independent of ArrayMath.
    switch (kind) {
      case "sum":
        operation = new PlusOp(new ParameterBlock());
        expectedReal = new double[] {3, -1, 4, 1, 5, 2, 1, 2.5};
        expectedImag = new double[] {2, 0, 2, 0, 1, 1, -1, 4};
        break;
      case "difference":
        operation = new MinusOp(new ParameterBlock());
        expectedReal = new double[] {-1, -3, -4, 5, 3, -4, 3, -1.5};
        expectedImag = new double[] {2, 2, -2, -2, 5, -1, -3, 4};
        break;
      case "product":
        operation = new TimesOp(new ParameterBlock());
        expectedReal = new double[] {2, -1, 0, -5, 10, -3, 0, 1};
        expectedImag = new double[] {4, 3, 0, 5, -5, -1, 4, 8};
        break;
      case "quotient":
        operation = new DivideOp(new ParameterBlock());
        expectedReal = new double[] {0.5, -1.5, 0, -1.4, -0.4, -0.3, -2, 0.25};
        expectedImag = new double[] {1, -0.5, 0, -0.2, 2.2, 0.1, 0, 2};
        break;
      default:
        throw new AssertionError("Unknown arithmetic fixture: " + kind);
    }
    Function output = operation.create(inputA, inputB);
    assertSamples(output, expectedReal, expectedImag, image, centered);
    output.freeMemory();
    assertSamples(output, expectedReal, expectedImag, image, centered);
    assertSamples(inputA, realA, imagA, image, centered);
    assertSamples(inputB, realB, imagB, image, centered);
  }

  static Stream<Arguments> arithmeticCases() {
    return Stream.of("sum", "difference", "product", "quotient").flatMap(kind ->
        Stream.of(Arguments.of(kind, false, false), Arguments.of(kind, false, true),
            Arguments.of(kind, true, false), Arguments.of(kind, true, true)));
  }

  private Function sampledFunction(double[] real, double[] imag, boolean image, boolean centered) {
    return image
        ? FunctionFactory.createFunction2D(real, imag, centered, "fixture", 4, 2)
        : FunctionFactory.createFunction1D(real, imag, centered, "fixture");
  }

  private void assertSamples(Function function, double[] real, double[] imag,
      boolean image, boolean centered) {
    assertEquals(centered, function.isZeroCentered());
    if (image) {
      Function2D imageFunction = assertInstanceOf(Function2D.class, function);
      assertEquals(4, imageFunction.getDimensionX());
      assertEquals(2, imageFunction.getDimensionY());
    } else {
      assertEquals(real.length, assertInstanceOf(Function1D.class, function).getDimension());
    }
    assertArrayEquals(real, function.getReal(), TOLERANCE, "real samples");
    assertArrayEquals(imag, function.getImaginary(), TOLERANCE, "imaginary samples");
  }

  private ParameterEditor editorNamed(OperationOptionsPanel panel, String title) {
    for (Component component : panel.getComponents()) {
      if (component instanceof JPanel editorPanel
          && editorPanel.getBorder() instanceof TitledBorder border
          && title.equals(border.getTitle())) {
        return (ParameterEditor) editorPanel.getComponent(0);
      }
    }
    throw new AssertionError("No parameter editor named " + title);
  }
}
