package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineTermsRule.*;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import signals.functionterm.*;
import signals.gui.*;
import signals.gui.datagenerator.*;

/** Ordinary two-term authoring through the real Swing selectors and parameter fields. */
class TwoTermImageWorkflowTest {
  private FunctionCreationOptions previousCreation;
  private DisplayOptions previousDisplay;
  private Colors previousColors;
  private Borders previousBorders;
  private DataGeneratorCollections previousGenerators;

  @BeforeEach void setUp() {
    Core core = Core.get();
    previousCreation = core.functionCreationOptions;
    previousDisplay = core.displayOptions;
    previousColors = core.colors;
    previousBorders = core.borders;
    previousGenerators = core.dataGeneratorCollections;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
    core.borders = new Borders();
    core.dataGeneratorCollections = new DataGeneratorCollections();
  }

  @AfterEach void tearDown() {
    Core core = Core.get();
    core.functionCreationOptions = previousCreation;
    core.displayOptions = previousDisplay;
    core.colors = previousColors;
    core.borders = previousBorders;
    core.dataGeneratorCollections = previousGenerators;
  }

  @ParameterizedTest @ValueSource(strings = {"Add", "Subtract", "Multiply"})
  void circleAndGaussianKeepTheirOperatorParametersAndOutput(String operation) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      CreateFunctionPart2DPanel panel = panel();
      panel.setPolarTerm();
      select(panel, RectangleFunctionTerm1D.class);
      width(panel, "64");
      // Retain the previous editor just as Swing focus/selection can, avoiding reliance on GC.
      CreateAnalyticSubTerm1DPanel previousEditor = find(panel, CreateAnalyticSubTerm1DPanel.class);
      click(panel, operation);
      HorizontalThumbnailList list = find(panel.getCalculator(), HorizontalThumbnailList.class);
      CalculatorListItem symbol = (CalculatorListItem) list.getItemAt(2);
      ThumbnailGraphic originalSymbol = symbol.getGraphic();
      panel.setXYTerm();
      select(panel, GaussianFunctionTerm1D.class);
      width(panel, "24");
      int code = operation.equals("Add") ? ADD : operation.equals("Subtract") ? SUBTRACT : MULTIPLY;
      assertSame(originalSymbol, symbol.getGraphic(), "The arithmetic symbol must remain an icon");
      assertNull(symbol.getValue(), "An arithmetic symbol must never become an image term");
      assertArrayEquals(new int[] {FUNCTION_TERM, code, FUNCTION_TERM}, panel.getCalculator().getCodeList());
      assertEquals(2, panel.getCalculator().getFunctionTerm2DList().size());
      checkCircleAndGaussian(panel, code, 32, 24);
      list.setSelected(1);
      checkCircleAndGaussian(panel, code, 32, 24);
      width(panel, "80");
      list.setSelected(3);
      checkCircleAndGaussian(panel, code, 40, 24);
      assertNotNull(previousEditor);
    });
  }

  @Test void twoCartesianTermsCanBeSelectedAndEditedIndependently() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      CreateFunctionPart2DPanel panel = panel();
      panel.setXYTerm();
      select(panel, GaussianFunctionTerm1D.class);
      width(panel, "24");
      CreateAnalyticSubTerm1DPanel previousEditor = find(panel, CreateAnalyticSubTerm1DPanel.class);
      click(panel, "Add");
      panel.setXYTerm();
      select(panel, RectangleFunctionTerm1D.class);
      width(panel, "64");
      HorizontalThumbnailList list = find(panel.getCalculator(), HorizontalThumbnailList.class);
      list.setSelected(1);
      width(panel, "32");
      list.setSelected(3);
      List<FunctionTerm2D> terms = panel.getCalculator().getFunctionTerm2DList();
      assertEquals(2, terms.size());
      AnalyticFunctionTerm1D gaussian = (AnalyticFunctionTerm1D) ((XYFunctionTerm2D) terms.get(0)).getFunctionTerm1DA();
      AnalyticFunctionTerm1D rectangle = (AnalyticFunctionTerm1D) ((XYFunctionTerm2D) terms.get(1)).getFunctionTerm1DA();
      assertInstanceOf(GaussianFunctionTerm1D.class, gaussian);
      assertInstanceOf(RectangleFunctionTerm1D.class, rectangle);
      assertEquals(32, gaussian.getWidth());
      assertEquals(32, rectangle.getWidth());
      assertNull(((CalculatorListItem) list.getItemAt(2)).getValue());
      assertNotNull(previousEditor);
    });
  }

  @Test void resizingAndReopeningAPolarEditorPreservesTheEnteredWidth() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      CreateFunctionPart2DPanel panel = panel();
      panel.setPolarTerm();
      select(panel, RectangleFunctionTerm1D.class);
      width(panel, "64");
      panel.setIndices(512, 256, true);
      FunctionPart2D part = panel.getFunctionPart2D();
      assertEquals(32, ((AnalyticFunctionTerm1D) ((PolarFunctionTerm2D) part.getTermsList().get(0)).getFunctionTerm1DA()).getWidth());
      CreateFunctionPart2DPanel reopened = panel();
      reopened.setFunctionPart2D(part);
      assertEquals(32, ((AnalyticFunctionTerm1D) ((PolarFunctionTerm2D) reopened.getFunctionPart2D().getTermsList().get(0)).getFunctionTerm1DA()).getWidth());
    });
  }

  @ParameterizedTest @ValueSource(booleans = {false, true})
  void ordinaryGroupingEditsAndTermRemovalKeepTheCalculatorConsistent(boolean grouped) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      CreateFunctionPart2DPanel panel = panel();
      if (grouped) click(panel, "Open Parenthesis");
      addConstant(panel, 2);
      click(panel, "Add");
      addConstant(panel, 3);
      if (grouped) click(panel, "Closed Parenthesis");
      click(panel, "Multiply");
      addConstant(panel, 4);
      assertConstantImage(panel, grouped ? 20 : 14);

      HorizontalThumbnailList list = find(panel.getCalculator(), HorizontalThumbnailList.class);
      list.setSelected(grouped ? 2 : 1);
      amplitude(panel, "5");
      assertConstantImage(panel, grouped ? 32 : 17);
      list.setSelected(list.getListSize() - 1);
      click(panel, "Delete"); // Remove C, then the now-unneeded multiplication token.
      list.setSelected(list.getListSize() - 1);
      click(panel, "Delete");
      assertConstantImage(panel, 8);
      list.setSelected(list.getListSize() - 1);
      click(panel, "Multiply");
      addConstant(panel, 6);
      assertConstantImage(panel, grouped ? 48 : 23);

      FunctionPart2D authored = panel.getFunctionPart2D();
      CreateFunctionPart2DPanel reopened = panel();
      reopened.setFunctionPart2D(authored);
      assertArrayEquals(panel.getCalculator().getCodeList(), reopened.getCalculator().getCodeList());
      assertConstantImage(reopened, grouped ? 48 : 23);
      assertEquals(3, reopened.getCalculator().getFunctionTerm2DList().size());
    });
  }

  private static void addConstant(CreateFunctionPart2DPanel panel, double value) {
    panel.setXYTerm();
    select(panel, ConstantFunctionTerm1D.class);
    amplitude(panel, Double.toString(value));
  }

  private static void amplitude(Container panel, String value) {
    EditAnalyticFunctionTerm1DPanel editor = (EditAnalyticFunctionTerm1DPanel)
        find(panel, CreateAnalyticSubTerm1DPanel.class).getEditor();
    List<ParameterEditor> fields = new ArrayList<>();
    collect(editor, ParameterEditor.class, fields);
    fields.get(0).setText(value);
  }

  private static void assertConstantImage(CreateFunctionPart2DPanel panel, double expected) {
    double[] samples = panel.getFunctionPart2D().create(256, 256, true);
    assertEquals(65536, samples.length);
    for (double sample : samples) assertEquals(expected, sample, 1e-12);
  }

  private static void checkCircleAndGaussian(CreateFunctionPart2DPanel panel, int code, double radius, double gaussianWidth) {
    List<FunctionTerm2D> terms = panel.getCalculator().getFunctionTerm2DList();
    PolarFunctionTerm2D circle = assertInstanceOf(PolarFunctionTerm2D.class, terms.get(0));
    XYFunctionTerm2D stripe = assertInstanceOf(XYFunctionTerm2D.class, terms.get(1));
    assertEquals(radius, ((AnalyticFunctionTerm1D) circle.getFunctionTerm1DA()).getWidth());
    assertEquals(gaussianWidth, ((AnalyticFunctionTerm1D) stripe.getFunctionTerm1DA()).getWidth());
    double[] result = panel.getFunctionPart2D().create(256, 256, true);
    // Independent values at ordinary interior/exterior points, away from circle boundaries.
    for (int x : new int[] {-48, -16, 0, 16, 48}) {
      double a = Math.abs(x) < radius ? 1 : 0;
      double b = Math.exp(-Math.PI * x * x / (gaussianWidth * gaussianWidth));
      double expected = code == ADD ? a + b : code == SUBTRACT ? a - b : a * b;
      assertEquals(expected, result[127 * 256 + 128 + x], 1e-12, "sample x=" + x);
    }
  }

  private static CreateFunctionPart2DPanel panel() {
    CreateFunctionPart2DPanel panel = new CreateFunctionPart2DPanel(new GUIEventBroadcaster(), true);
    panel.setIndices(256, 256, true);
    return panel;
  }

  private static void select(Container panel, Class<? extends DataGenerator> type) {
    JList<?> list = find(panel, DataGeneratorSelectorPanel.class).getList();
    for (int i = 0; i < list.getModel().getSize(); i++) {
      if (((TextListItem) list.getModel().getElementAt(i)).getValue() == type) {
        list.setSelectedIndex(i);
        return;
      }
    }
    fail("Missing generator " + type);
  }

  private static void width(Container panel, String value) {
    EditAnalyticFunctionTerm1DPanel editor = (EditAnalyticFunctionTerm1DPanel) find(panel, CreateAnalyticSubTerm1DPanel.class).getEditor();
    List<ParameterEditor> fields = new ArrayList<>();
    collect(editor, ParameterEditor.class, fields);
    fields.get(2).setText(value);
  }

  private static void click(Container panel, String tooltip) {
    List<JButton> buttons = new ArrayList<>();
    collect(panel, JButton.class, buttons);
    buttons.stream().filter(b -> tooltip.equals(b.getToolTipText())).findFirst().orElseThrow().doClick();
  }

  private static <T extends Component> T find(Container root, Class<T> type) {
    List<T> matches = new ArrayList<>();
    collect(root, type, matches);
    return matches.stream().findFirst().orElseThrow(() -> new AssertionError("Missing " + type));
  }

  private static <T extends Component> void collect(Container root, Class<T> type, List<T> matches) {
    for (Component child : root.getComponents()) {
      if (type.isInstance(child)) matches.add(type.cast(child));
      else if (child instanceof Container) collect((Container) child, type, matches);
    }
  }
}
