package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import signals.functionterm.*;
import signals.gui.Colors;
import signals.io.ImageWriter;
import signals.operation.*;

/** Roger's ordinary image composition, through production generators and systems. */
class RogerWorkflowTest {
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
  @CsvSource({"256,256,45", "256,256,180", "128,64,30"})
  void rotatedImagesCanBeMultipliedAndAddedToSameSizedImages(int width, int height, double angle) {
    double[] real = new double[width * height];
    double[] imaginary = new double[real.length];
    double[] ones = new double[real.length];
    for (int i = 0; i < real.length; i++) {
      real[i] = (i % width) / (double) width;
      imaginary[i] = (i / width) / (double) height;
      ones[i] = 1;
    }
    Function source = FunctionFactory.createFunction2D(real, imaginary, true, "ramp", width, height);
    Function mask = FunctionFactory.createFunction2D(ones, new double[ones.length], true, "ones", width, height);
    Function rotated = rotation(angle).create(source);
    Function multiplied = assertDoesNotThrow(() -> new TimesOp(new ParameterBlock()).create(rotated, mask));
    Function added = assertDoesNotThrow(() -> new PlusOp(new ParameterBlock()).create(rotated, source));
    assertEquals(width * height, rotated.getReal().length);
    assertEquals(width * height, rotated.getImaginary().length);
    assertArrayEquals(rotated.getReal(), multiplied.getReal(), 0);
    assertArrayEquals(rotated.getImaginary(), multiplied.getImaginary(), 0);
    assertEquals(width * height, added.getReal().length);
    assertArrayEquals(real, source.getReal(), 0, "rotation must preserve its source");
    assertArrayEquals(imaginary, source.getImaginary(), 0);
  }

  @Test
  void rogersTargetCanBeComposedEditedAndExported(@TempDir Path directory) throws Exception {
    MonomialFunctionTerm1D phase = (MonomialFunctionTerm1D) new MonomialFunctionTerm1D().getDefaultInstance();
    phase.setCenter(128);
    phase.setWidth(16);
    phase.setExponent(2);
    Function2D rings = new MagPhaseFunction2D(cartesian(constant(1)), polar(phase), "rings", 256, 256, true);
    StepFunctionTerm1D stepTerm = (StepFunctionTerm1D) new StepFunctionTerm1D().getDefaultInstance();
    Function2D step = image(cartesian(stepTerm), "step");
    ChirpFunctionTerm1D chirpTerm = (ChirpFunctionTerm1D) new ChirpFunctionTerm1D().getDefaultInstance();
    chirpTerm.setWidth(16);
    chirpTerm.setOrder(2);
    Function2D chirp = image(polar(chirpTerm), "chirp");
    OperatorSystem2D firstHalf = half(rings, step);
    double[] firstBefore = firstHalf.getFunction().getReal().clone();
    OperatorSystem2D rotatedStep = system(new FunctionProducer[] {step},
        new Operation[] {rotation(180)}, UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN);
    OperatorSystem2D secondHalf = firstHalf.clone();
    // Operand replacement follows the editor's list replacement, retaining linked sources.
    secondHalf.setInputList(new ArrayList<>(Arrays.asList(chirp, rotatedStep)));
    OperatorSystem2D sum = system(new FunctionProducer[] {firstHalf, secondHalf},
        new Operation[] {new PlusOp(new ParameterBlock())}, FUNCTION, BINARY_OP_TIER_1, FUNCTION);
    RectangleFunctionTerm1D rectangle = (RectangleFunctionTerm1D) new RectangleFunctionTerm1D().getDefaultInstance();
    rectangle.setWidth(113); // The editor's full-width value is 226 pixels.
    Function2D mask = image(polar(rectangle), "mask");
    RotateOp2D rotate = rotation(45);
    OperatorSystem2D result = system(new FunctionProducer[] {sum, mask},
        new Operation[] {rotate, new TimesOp(new ParameterBlock())},
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN, BINARY_OP_TIER_2, FUNCTION);
    Function2D output = assertInstanceOf(Function2D.class, assertDoesNotThrow(result::getFunction));
    assertEquals(256, output.getDimensionX());
    assertEquals(256, output.getDimensionY());
    double[] pixels = output.getReal().clone();
    assertEquals(256 * 256, pixels.length);
    assertArrayEquals(new double[pixels.length], output.getImaginary(), 0);
    assertTrue(Arrays.stream(pixels).allMatch(v -> Double.isFinite(v) && v >= 0 && v <= 1));
    assertTrue(Arrays.stream(pixels).anyMatch(v -> v > 0.9), "target must contain bright structure");
    for (int i = 0; i < pixels.length; i++) {
      if (mask.getReal()[i] == 0) assertEquals(0, pixels[i], "mask must clip the final image");
    }
    assertArrayEquals(firstBefore, firstHalf.getFunction().getReal(), 0);
    // A normal operation edit and recalculation must leave an exportable image.
    rotate.setValue(0, 30);
    result.freeMemory();
    assertFalse(Arrays.equals(pixels, result.getFunction().getReal()));
    rotate.setValue(0, 45);
    result.freeMemory();
    output = (Function2D) result.getFunction();
    assertArrayEquals(pixels, output.getReal(), 0);
    Path tiff = directory.resolve("roger-target.tiff");
    assertTrue(ImageWriter.writeImage(tiff.toFile(), output, Constants.Part.REAL_PART, ImageWriter.ImageType.TIFF, 0.0, 1.0));
    BufferedImage reopened = ImageIO.read(tiff.toFile());
    assertNotNull(reopened);
    assertEquals(256, reopened.getWidth());
    assertEquals(256, reopened.getHeight());
    for (int y = 0; y < 256; y++) {
      for (int x = 0; x < 256; x++) {
        assertEquals(pixels[y * 256 + x] * 255, reopened.getRaster().getSample(x, y, 0), 1.0);
      }
    }
  }

  private static OperatorSystem2D half(FunctionProducer input, FunctionProducer mask) {
    return system(new FunctionProducer[] {input, mask},
        new Operation[] {new RealOp(new ParameterBlock()), (ThresholdOp) new ThresholdOp().getDefaultInstance(), new TimesOp(new ParameterBlock())},
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, UNARY_OP_TIER_2, RIGHT_PAREN, BINARY_OP_TIER_2, FUNCTION);
  }

  private static OperatorSystem2D system(FunctionProducer[] inputs, Operation[] operations, int... rule) {
    ArrayList<Operation> ops = new ArrayList<>(Arrays.asList(operations));
    return new OperatorSystem2D(new CombineOpsRule(rule, ops), new ArrayList<>(Arrays.asList(inputs)), ops);
  }

  private static RotateOp2D rotation(double angle) {
    RotateOp2D result = new RotateOp2D(new ParameterBlock());
    result.setValue(0, angle);
    return result;
  }

  private static ConstantFunctionTerm1D constant(double value) {
    ConstantFunctionTerm1D result = (ConstantFunctionTerm1D) new ConstantFunctionTerm1D().getDefaultInstance();
    result.setAmplitude(value);
    return result;
  }

  private static FunctionPart2D cartesian(FunctionTerm1D term) {
    return part(new XYFunctionTerm2D(new ParameterBlock().addSource(term).addSource(constant(1))));
  }

  private static FunctionPart2D polar(FunctionTerm1D term) {
    return part(new PolarFunctionTerm2D(new ParameterBlock().addSource(term).addSource(constant(1))));
  }

  private static FunctionPart2D part(FunctionTerm2D term) {
    return new FunctionPart2D(new ArrayList<>(Arrays.asList(term)), CombineTermsRule.getDefaultRule());
  }

  private static Function2D image(FunctionPart2D real, String name) {
    return new RealImagFunction2D(real, cartesian(constant(0)), name, 256, 256, true);
  }
}
