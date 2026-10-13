package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;

import javax.media.jai.Interpolation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import signals.gui.Colors;
import signals.operation.*;

class OperationDuplicationTest {
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
    Core core = Core.get();
    core.functionCreationOptions = oldCreation;
    core.displayOptions = oldDisplay;
    core.colors = oldColors;
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void duplicatedChainPreservesSettingsButOwnsLaterParameterAndOperandEdits(boolean image) {
    Function input = function(image, new double[] {0, .2, .4, .6, .8, 1, .2, .6});
    Function mask = function(image, new double[] {1, 1, 1, 1, 1, 1, 1, 1});
    ThresholdOp threshold = new ThresholdOp(new ParameterBlock());
    threshold.setValue(2, .25);
    OperatorSystem original = system(image, new FunctionProducer[] {input, mask},
        new Operation[] {new RealOp(new ParameterBlock()), threshold, new TimesOp(new ParameterBlock())},
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, UNARY_OP_TIER_2, RIGHT_PAREN, BINARY_OP_TIER_2, FUNCTION);
    double[] before = {0, 0, 1, 1, 1, 1, 0, 1};
    assertArrayEquals(before, original.getFunction().getReal());
    OperatorSystem copy = original.clone();
    assertArrayEquals(before, copy.getFunction().getReal(), "copy must preserve configured settings");
    assertSame(input, copy.getInputList().get(0), "input documents remain linked");
    ThresholdOp copiedThreshold = (ThresholdOp) copy.getOpList().get(1);
    copiedThreshold.setValue(2, .75);
    copy.freeMemory();
    original.freeMemory();
    assertArrayEquals(new double[] {0, 0, 0, 0, 1, 1, 0, 0}, copy.getFunction().getReal());
    assertArrayEquals(before, original.getFunction().getReal(), "editing a copy must not change its original");
    threshold.setValue(0, 2);
    original.freeMemory();
    copy.freeMemory();
    assertEquals(2, original.getFunction().getReal()[4]);
    assertEquals(1, copy.getFunction().getReal()[4], "independence works in both directions");
    Function replacement = function(image, new double[] {1, 1, 1, 1, 1, 1, 1, 1});
    copy.getInputList().set(0, replacement);
    copy.freeMemory();
    assertArrayEquals(replacement.getReal(), copy.getFunction().getReal());
    assertSame(input, original.getInputList().get(0), "operand list edits must be independent");
  }

  @Test
  void duplicatePreservesRotationAndInterpolationWithoutSharingLaterEdits() {
    Function input = function(true, new double[] {0, 1, 2, 3, 4, 5, 6, 7});
    RotateOp2D rotation = new RotateOp2D(new ParameterBlock());
    rotation.setValue(0, 37);
    rotation.setInterpolation(Interpolation.INTERP_NEAREST);
    OperatorSystem original = system(true, new FunctionProducer[] {input}, new Operation[] {rotation},
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN);
    OperatorSystem copy = original.clone();
    assertArrayEquals(original.getFunction().getReal(), copy.getFunction().getReal());
    RotateOp2D copiedRotation = (RotateOp2D) copy.getOpList().get(0);
    assertEquals(37, copiedRotation.getValue(0));
    assertEquals(Interpolation.INTERP_NEAREST, copiedRotation.getInterpolation());
    copiedRotation.setValue(0, 90);
    copiedRotation.setInterpolation(Interpolation.INTERP_BILINEAR);
    assertEquals(37, rotation.getValue(0));
    assertEquals(Interpolation.INTERP_NEAREST, rotation.getInterpolation());
  }

  @Test
  void duplicatePreservesTransformFlagsAndIndependentParameterBlock() {
    Function input = function(false, new double[] {0, 1, 2, 3, 4, 5, 6, 7});
    FourierTransformOp fft = new FourierTransformOp(new ParameterBlock().add(17));
    fft.setSelected(true);
    fft.setNormalization(Transforms.NORMALIZE_NONE);
    OperatorSystem original = system(false, new FunctionProducer[] {input}, new Operation[] {fft},
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN);
    OperatorSystem copy = original.clone();
    assertArrayEquals(original.getFunction().getReal(), copy.getFunction().getReal());
    assertArrayEquals(original.getFunction().getImaginary(), copy.getFunction().getImaginary());
    FourierTransformOp copiedFft = (FourierTransformOp) copy.getOpList().get(0);
    assertTrue(copiedFft.isSelected());
    assertEquals(Transforms.NORMALIZE_NONE, copiedFft.getNormalization());
    copiedFft.setSelected(false);
    copiedFft.setParameter(23, 0);
    assertTrue(fft.isSelected());
    assertEquals(17, fft.getParameter(0));
  }

  private static Function function(boolean image, double[] real) {
    return image ? FunctionFactory.createFunction2D(real, new double[real.length], true, "input", 4, 2)
        : FunctionFactory.createFunction1D(real, new double[real.length], true, "input");
  }

  private static OperatorSystem system(boolean image, FunctionProducer[] sources, Operation[] operations, int... rule) {
    ArrayList<Operation> ops = new ArrayList<>(Arrays.asList(operations));
    ArrayList<FunctionProducer> inputs = new ArrayList<>(Arrays.asList(sources));
    CombineOpsRule expression = new CombineOpsRule(rule, ops);
    return image ? new OperatorSystem2D(expression, inputs, ops) : new OperatorSystem1D(expression, inputs, ops);
  }
}
