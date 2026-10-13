package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.HorizontalThumbnailList;
import signals.gui.VerticalThumbnailList;
import signals.gui.datagenerator.*;
import signals.operation.ThresholdOp;

/** Exercise actual calculator buttons with the ordinary current library. */
class OperationInsertionWorkflowTest {
  private FunctionCreationOptions previousCreation;
  private DisplayOptions previousDisplay;
  private Colors previousColors;
  private Borders previousBorders;

  @BeforeEach
  void setUp() {
    Core core = Core.get();
    previousCreation = core.functionCreationOptions;
    previousDisplay = core.displayOptions;
    previousColors = core.colors;
    previousBorders = core.borders;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
    core.borders = new Borders();
  }

  @AfterEach
  void tearDown() {
    Core core = Core.get();
    core.functionCreationOptions = previousCreation;
    core.displayOptions = previousDisplay;
    core.colors = previousColors;
    core.borders = previousBorders;
  }

  @ParameterizedTest
  @CsvSource({"false,New Unary Operation", "true,New Unary Operation",
      "false,New Binary Operation", "true,New Binary Operation",
      "false,New Term", "true,New Term"})
  void addingToAnExistingChainNeverSelectsThatChainAsInput(boolean image, String button) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Function source = source(image);
      OperatorSystem chain = chain(image, source);
      VerticalThumbnailList library = library(source, chain);
      GUIEventBroadcaster events = new GUIEventBroadcaster();
      OperationCalculator calculator = image ? new OperationCalculator2D(events) {
        @Override protected VerticalThumbnailList getLibrary() { return library; }
      } : new OperationCalculator1D(events) {
        @Override protected VerticalThumbnailList getLibrary() { return library; }
      };
      calculator.setSystem(chain);
      calculator.setList(chain.getInputList(), chain.getOpList(), chain.getCombineOpsRule());
      findButton(calculator, button).doClick();
      assertEquals(2, calculator.getFunctionList().size());
      for (FunctionProducer input : calculator.getFunctionList()) {
        assertSame(source, input, "New operands must come from eligible sources, not the current chain");
      }
      if (!button.equals("New Binary Operation")) {
        // New Term/Unary append a term; finish the expression with the ordinary Plus button.
        findList(calculator).setSelected(4); // closing parenthesis of Threshold(source)
        findButton(calculator, "New Binary Operation").doClick();
      }
      chain.setInputList(calculator.getFunctionList());
      chain.setOpList(calculator.getOperationList());
      chain.setCombineOpsRule(new CombineOpsRule(calculator.getCodeList(), chain.getOpList()));
      Function result = assertDoesNotThrow(chain::getFunction);
      assertNotNull(result);
      assertEquals(source.getReal().length, result.getReal().length);
      assertTrue(Arrays.stream(result.getReal()).allMatch(Double::isFinite));
      assertTrue(Arrays.stream(result.getImaginary()).allMatch(Double::isFinite));
    });
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void returningToAnEarlierChainExcludesItsDownstreamConsumers(boolean image) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Function source = source(image);
      OperatorSystem first = chain(image, source);
      OperatorSystem second = chain(image, first);
      OperatorSystem third = chain(image, second);
      OperatorSystem independent = chain(image, source);
      VerticalThumbnailList library = library(source, first, second, third, independent);
      VerticalThumbnailList eligible = image ? library.getFunction2DList(first) : library.getFunction1DList(first);
      assertEquals(2, eligible.getListSize());
      assertSame(source, eligible.getItemAt(0));
      assertSame(independent, eligible.getItemAt(1));
      assertNotNull(third.getFunction(), "Filtering must not alter the existing chain");
    });
  }

  private static Function source(boolean image) {
    double[] real = new double[256];
    double[] imaginary = new double[256];
    for (int i = 0; i < real.length; i++) {
      real[i] = Math.cos(2 * Math.PI * i / 32);
      imaginary[i] = Math.sin(2 * Math.PI * i / 16);
    }
    return image ? FunctionFactory.createFunction2D(real, imaginary, true, "source", 32, 8)
        : FunctionFactory.createFunction1D(real, imaginary, true, "source");
  }

  private static OperatorSystem chain(boolean image, FunctionProducer source) {
    ArrayList<Operation> operations = new ArrayList<>(Arrays.asList((ThresholdOp) new ThresholdOp().getDefaultInstance()));
    ArrayList<FunctionProducer> inputs = new ArrayList<>(Arrays.asList(source));
    CombineOpsRule rule = new CombineOpsRule(new int[] {UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN}, operations);
    return image ? new OperatorSystem2D(rule, inputs, operations) : new OperatorSystem1D(rule, inputs, operations);
  }

  private static VerticalThumbnailList library(FunctionProducer... inputs) {
    VerticalThumbnailList library = new VerticalThumbnailList();
    library.setSelecting(false);
    for (FunctionProducer input : inputs) library.addItem(input);
    return library;
  }

  private static HorizontalThumbnailList findList(Container container) {
    for (Component child : container.getComponents()) {
      if (child instanceof HorizontalThumbnailList) return (HorizontalThumbnailList) child;
      if (child instanceof Container) {
        HorizontalThumbnailList match = findList((Container) child);
        if (match != null) return match;
      }
    }
    return null;
  }

  private static JButton findButton(Container container, String tooltip) {
    for (Component child : container.getComponents()) {
      if (child instanceof JButton && tooltip.equals(((JButton) child).getToolTipText())) return (JButton) child;
      if (child instanceof Container) {
        JButton match = findButton((Container) child, tooltip);
        if (match != null) return match;
      }
    }
    return null;
  }
}
