package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import signals.gui.Colors;
import signals.io.ComplexIO;
import signals.operation.RealOp;
import signals.operation.ThresholdOp;
import signals.operation.TimesOp;

/** Save/reopen ordinary complex data and reuse it in real operations. */
class ComplexDataWorkflowTest {
  private FunctionCreationOptions previousCreation;
  private DisplayOptions previousDisplay;
  private Colors previousColors;

  @BeforeEach
  void setUp() {
    Core core = Core.get();
    previousCreation = core.functionCreationOptions;
    previousDisplay = core.displayOptions;
    previousColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach
  void tearDown() {
    Core core = Core.get();
    core.functionCreationOptions = previousCreation;
    core.displayOptions = previousDisplay;
    core.colors = previousColors;
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void rectangularComplexImageCanBeSavedReopenedAndUsed(boolean zeroCentered, @TempDir Path directory) {
    int width = 32, height = 16;
    double[] real = new double[width * height];
    double[] imaginary = new double[real.length];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int i = y * width + x;
        real[i] = (x - 12.0) / 8.0 + y / 16.0;
        imaginary[i] = (y - 5.0) / 4.0 - x / 32.0;
      }
    }
    Function2D source = (Function2D) FunctionFactory.createFunction2D(
        real, imaginary, zeroCentered, "Roger target 45 degrees", width, height);
    source.setDescriptor("Roger target 45 degrees");
    Path file = directory.resolve("Roger complex image.txt");
    assertDoesNotThrow(() -> ComplexIO.write2D(file.toFile(), source));
    Function2D reopened = assertInstanceOf(Function2D.class,
        assertDoesNotThrow(() -> ComplexIO.read(file.toFile())));
    assertEquals(width, reopened.getDimensionX());
    assertEquals(height, reopened.getDimensionY());
    assertSameSamples(source, reopened);

    ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
    Function thresholded = threshold.create(reopened);
    Function nonnegative = new RealOp(new ParameterBlock()).create(thresholded);
    double[] expected = Arrays.stream(real).map(v -> v < 0.5 ? 0 : 1).toArray();
    assertArrayEquals(expected, nonnegative.getReal(), 0);
    assertArrayEquals(new double[real.length], nonnegative.getImaginary(), 0);
    Function product = new TimesOp(new ParameterBlock()).create(nonnegative, source);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i] * real[i], product.getReal()[i], 0);
      assertEquals(expected[i] * imaginary[i], product.getImaginary()[i], 0);
    }
    assertSameSamples(source, reopened);
    // Operation results carry their display name in a custom equation, not a descriptor.
    Path resultFile = directory.resolve("operated result.txt");
    assertDoesNotThrow(() -> ComplexIO.write(resultFile.toFile(), product));
    Function loadedResult = assertDoesNotThrow(() -> ComplexIO.read(resultFile.toFile()));
    assertSameSamples(product, loadedResult);
    assertDoesNotThrow(() -> ComplexIO.write(resultFile.toFile(), loadedResult));
    assertSameSamples(product, ComplexIO.read(resultFile.toFile()));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void namedComplexSignalCanBeSavedAndReopened(boolean zeroCentered, @TempDir Path directory) {
    double[] real = new double[64];
    double[] imaginary = new double[64];
    for (int i = 0; i < real.length; i++) {
      real[i] = Math.cos(2 * Math.PI * i / 16);
      imaginary[i] = Math.sin(2 * Math.PI * i / 16);
    }
    Function1D source = (Function1D) FunctionFactory.createFunction1D(
        real, imaginary, zeroCentered, "complex exponential");
    source.setDescriptor("complex exponential");
    Path file = directory.resolve("complex signal.txt");
    assertDoesNotThrow(() -> ComplexIO.write1D(file.toFile(), source));
    Function1D reopened = assertInstanceOf(Function1D.class,
        assertDoesNotThrow(() -> ComplexIO.read(file.toFile())));
    assertEquals(64, reopened.getDimension());
    assertSameSamples(source, reopened);
  }

  @Test
  void existingSingleWordNamesStillLoad(@TempDir Path directory) {
    Function1D source = (Function1D) FunctionFactory.createFunction1D(
        new double[] {1, 2, 3, 4}, new double[] {4, 3, 2, 1}, true, "f0");
    source.setDescriptor("f0");
    Path file = directory.resolve("old-format.txt");
    assertDoesNotThrow(() -> ComplexIO.write1D(file.toFile(), source));
    assertSameSamples(source, ComplexIO.read(file.toFile()));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void failedSaveCanBeRetriedWithoutChangingTheSource(boolean image, @TempDir Path directory) {
    double[] real = new double[64];
    double[] imaginary = new double[64];
    for (int i = 0; i < real.length; i++) {
      real[i] = i / 8.0 - 2;
      imaginary[i] = 3 - i / 16.0;
    }
    Function source = image
        ? FunctionFactory.createFunction2D(real, imaginary, true, "retry image", 8, 8)
        : FunctionFactory.createFunction1D(real, imaginary, true, "retry signal");
    source.setDescriptor(image ? "retry image" : "retry signal");
    double[] expectedReal = source.getReal().clone();
    double[] expectedImaginary = source.getImaginary().clone();
    Path missingDirectory = directory.resolve("missing folder");
    assertThrows(IOException.class,
        () -> ComplexIO.write(missingDirectory.resolve("result.txt").toFile(), source));
    assertFalse(Files.exists(missingDirectory));
    assertArrayEquals(expectedReal, source.getReal(), 0);
    assertArrayEquals(expectedImaginary, source.getImaginary(), 0);
    Path retry = directory.resolve("corrected result.txt");
    assertDoesNotThrow(() -> ComplexIO.write(retry.toFile(), source));
    assertSameSamples(source, ComplexIO.read(retry.toFile()));
  }

  private static void assertSameSamples(Function source, Function reopened) {
    assertEquals(source.getCompactDescriptor(), reopened.getDescriptor());
    assertEquals(source.getCompactDescriptor(), reopened.getCompactDescriptor());
    assertEquals(source.isZeroCentered(), reopened.isZeroCentered());
    assertArrayEquals(source.getReal(), reopened.getReal(), 0);
    assertArrayEquals(source.getImaginary(), reopened.getImaginary(), 0);
  }
}
