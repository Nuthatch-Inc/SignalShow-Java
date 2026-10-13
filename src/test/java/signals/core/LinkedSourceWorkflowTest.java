package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import signals.gui.Colors;
import signals.operation.PlusOp;
import signals.operation.RealOp;
import signals.operation.ThresholdOp;

/** Live source edits through real operation chains, with independent sample expectations. */
class LinkedSourceWorkflowTest {
  private FunctionCreationOptions oldCreation;
  private DisplayOptions oldDisplay;
  private Colors oldColors;

  @BeforeEach void setUp() {
    Core core = Core.get();
    oldCreation = core.functionCreationOptions;
    oldDisplay = core.displayOptions;
    oldColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach void tearDown() {
    Core core = Core.get();
    core.functionCreationOptions = oldCreation;
    core.displayOptions = oldDisplay;
    core.colors = oldColors;
  }

  @ParameterizedTest @ValueSource(booleans = {false, true})
  void sourceEditsUpdateLinkedResultsOnceAndKeepThemReusable(boolean image) {
    Function a = source(image, 0.1);
    Function b = source(image, 0.2);
    CountingPlus plus = new CountingPlus();
    CountingThreshold threshold = new CountingThreshold();
    OperatorSystem sum = system(image, new FunctionProducer[] {a, b}, plus,
        FUNCTION, BINARY_OP_TIER_1, FUNCTION);
    OperatorSystem mask = system(image, new FunctionProducer[] {sum}, threshold,
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN);
    OperatorSystem real = system(image, new FunctionProducer[] {mask}, new RealOp(new ParameterBlock()),
        UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN);
    assertResult(real.getFunction(), image, 0.3);

    // Same setters used when the source editor publishes changed real/imaginary parts.
    for (double amplitude : new double[] {0.5, 0.8}) {
      replaceParts(a, source(image, amplitude));
      plus.calls = 0;
      threshold.calls = 0;
      Function output = real.getFunction();
      assertResult(output, image, amplitude + 0.2);
      Function repeat = real.getFunction();
      Function repeatAgain = real.getFunction();
      assertAll(
          () -> assertSame(output, repeat, "Reading unchanged data must reuse the updated result"),
          () -> assertSame(output, repeatAgain, "Plot/channel consumers must not rebuild the chain"),
          () -> assertEquals(1, plus.calls, "One source edit requires one addition, not repeated allocations"),
          () -> assertEquals(1, threshold.calls, "One source edit requires one threshold pass"));
    }

    // Replacing an operand through the public system setter also invalidates consumers.
    sum.setInputList(new ArrayList<>(Arrays.asList(a, source(image, -0.4))));
    Function replaced = real.getFunction();
    assertResult(replaced, image, 0.4);
    assertSame(replaced, real.getFunction());
    // Cache eviction may rebuild data but must not leave consumers permanently dirty.
    sum.freeMemory();
    Function regenerated = real.getFunction();
    assertResult(regenerated, image, 0.4);
    assertSame(regenerated, real.getFunction());
  }

  private static void replaceParts(Function target, Function replacement) {
    if (target instanceof Function2D) {
      ((Function2D) target).setPartA(((Function2D) replacement).getPartA());
      ((Function2D) target).setPartB(((Function2D) replacement).getPartB());
    } else {
      ((Function1D) target).setPartA(((Function1D) replacement).getPartA());
      ((Function1D) target).setPartB(((Function1D) replacement).getPartB());
    }
  }

  private static Function source(boolean image, double amplitude) {
    int length = image ? 128 * 64 : 128;
    double[] real = new double[length];
    double[] imaginary = new double[length];
    for (int i = 0; i < length; i++) {
      real[i] = amplitude * ((i % 128) - 32) / 64.0;
      imaginary[i] = amplitude * ((i / 128) + 1) / 64.0;
    }
    return image ? FunctionFactory.createFunction2D(real, imaginary, true, "editable source", 128, 64)
        : FunctionFactory.createFunction1D(real, imaginary, true, "editable source");
  }

  private static void assertResult(Function result, boolean image, double sumAmplitude) {
    int length = image ? 128 * 64 : 128;
    assertEquals(length, result.getReal().length);
    for (int i = 0; i < length; i++) {
      double expected = sumAmplitude * ((i % 128) - 32) / 64.0 >= 0.5 ? 1 : 0;
      assertEquals(expected, result.getReal()[i], "sample " + i);
      assertEquals(0, result.getImaginary()[i]);
    }
    if (image) {
      assertEquals(128, ((Function2D) result).getDimensionX());
      assertEquals(64, ((Function2D) result).getDimensionY());
    }
  }

  private static OperatorSystem system(boolean image, FunctionProducer[] inputs, Operation operation, int... code) {
    ArrayList<Operation> operations = new ArrayList<>(Arrays.asList(operation));
    CombineOpsRule rule = new CombineOpsRule(code, operations);
    ArrayList<FunctionProducer> sources = new ArrayList<>(Arrays.asList(inputs));
    return image ? new OperatorSystem2D(rule, sources, operations)
        : new OperatorSystem1D(rule, sources, operations);
  }

  private static class CountingPlus extends PlusOp {
    int calls;
    CountingPlus() { super(new ParameterBlock()); }
    @Override public Function create(Function a, Function b) {
      calls++;
      return super.create(a, b);
    }
  }

  private static class CountingThreshold extends ThresholdOp {
    int calls;
    CountingThreshold() { super(new ParameterBlock()); }
    @Override public Function create(Function input) {
      calls++;
      return super.create(input);
    }
  }
}
