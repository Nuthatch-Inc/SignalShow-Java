package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.Interpolation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import signals.gui.Colors;
import signals.operation.MagnitudeOp;
import signals.operation.RotateOp2D;
import signals.operation.ThresholdOp;

class RectangularRotationWorkflowTest {
  private FunctionCreationOptions oldCreation;
  private DisplayOptions oldDisplay;
  private Colors oldColors;

  @BeforeEach
  void setUp() {
    Core core = Core.get();
    oldCreation = core.functionCreationOptions;
    oldDisplay = core.displayOptions;
    oldColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach
  void tearDown() {
    Core.get().functionCreationOptions = oldCreation;
    Core.get().displayOptions = oldDisplay;
    Core.get().colors = oldColors;
  }

  @ParameterizedTest
  @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
  void quarterTurnsRetainBothChannelsAndCanvasForEveryInterpolation(int width, int height, boolean centered) {
    Function2D source = source(width, height, centered);
    double[] originalReal = source.getReal().clone();
    double[] originalImaginary = source.getImaginary().clone();
    RotateOp2D rotate = new RotateOp2D(new ParameterBlock());
    for (int interpolation : new int[] {Interpolation.INTERP_NEAREST, Interpolation.INTERP_BILINEAR,
        Interpolation.INTERP_BICUBIC, Interpolation.INTERP_BICUBIC_2}) {
      rotate.setInterpolation(interpolation);
      for (int angle : new int[] {90, -90, 180}) {
        rotate.setValue(0, angle);
        Function2D result = (Function2D) rotate.create(source);
        assertEquals(width, result.getDimensionX());
        assertEquals(height, result.getDimensionY());
        assertEquals(centered, result.isZeroCentered());
        assertArrayEquals(quarterTurn(originalReal, width, height, angle), result.getReal(), 1e-9);
        assertArrayEquals(quarterTurn(originalImaginary, width, height, angle), result.getImaginary(), 1e-9);
      }
    }
    assertArrayEquals(originalReal, source.getReal(), 0);
    assertArrayEquals(originalImaginary, source.getImaginary(), 0);
  }

  @ParameterizedTest
  @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
  void magnitudeThresholdChainRemainsEditableAfterQuarterTurn(int width, int height, boolean centered) {
    Function2D input = source(width, height, centered);
    OperatorSystem2D magnitude = unary(input, new MagnitudeOp(new ParameterBlock()));
    ThresholdOp threshold = new ThresholdOp(new ParameterBlock());
    threshold.setValue(2, .75);
    OperatorSystem2D mask = unary(magnitude, threshold);
    RotateOp2D rotate = new RotateOp2D(new ParameterBlock());
    OperatorSystem2D rotated = unary(mask, rotate);
    for (int angle : new int[] {90, -90, 180}) {
      rotate.setValue(0, angle);
      rotated.freeMemory();
      double[] expectedMask = new double[width * height];
      for (int i = 0; i < expectedMask.length; i++) {
        expectedMask[i] = Math.hypot(input.getReal()[i], input.getImaginary()[i]) >= threshold.getValue(2) ? 1 : 0;
      }
      assertArrayEquals(quarterTurn(expectedMask, width, height, angle), rotated.getFunction().getReal(), 1e-9);
      assertArrayEquals(new double[width * height], rotated.getFunction().getImaginary(), 0);
      // An ordinary parameter edit must propagate through the same linked chain.
      threshold.setValue(2, .9);
      mask.freeMemory();
    }
  }

  private static Function2D source(int width, int height, boolean centered) {
    double[] real = new double[width * height], imaginary = new double[real.length];
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        real[row * width + col] = .1 + col / (double) width;
        imaginary[row * width + col] = -.25 + row / (double) height;
      }
    }
    return (Function2D) FunctionFactory.createFunction2D(real, imaginary, centered, "two-channel ramp", width, height);
  }

  private static OperatorSystem2D unary(FunctionProducer source, Operation operation) {
    ArrayList<Operation> operations = new ArrayList<>(List.of(operation));
    return new OperatorSystem2D(new CombineOpsRule(new int[] {UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN}, operations),
        new ArrayList<>(List.of(source)), operations);
  }

  /** Integer pixel-center geometry, independent of JAI's resampling/crop path. */
  private static double[] quarterTurn(double[] input, int width, int height, int angle) {
    double[] expected = new double[width * height];
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int sourceCol, sourceRow;
        if (angle == 90) {
          sourceCol = row + (width - height) / 2;
          sourceRow = (width + height) / 2 - 1 - col;
        } else if (angle == -90) {
          sourceCol = (width + height) / 2 - 1 - row;
          sourceRow = col + (height - width) / 2;
        } else {
          sourceCol = width - 1 - col;
          sourceRow = height - 1 - row;
        }
        if (sourceCol >= 0 && sourceCol < width && sourceRow >= 0 && sourceRow < height) {
          expected[row * width + col] = input[sourceRow * width + sourceCol];
        }
      }
    }
    return expected;
  }
}
