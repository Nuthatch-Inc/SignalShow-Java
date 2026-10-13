package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.*;
import signals.operation.TimesOp;

class OperationReopenWorkflowTest {
  private FunctionCreationOptions oldCreation;
  private DisplayOptions oldDisplay;
  private Colors oldColors;
  private Borders oldBorders;
  private DataGeneratorCollections oldGenerators;
  private static DefaultListModel<FunctionProducer> library;

  @BeforeEach void setUp() {
    Core core = Core.get();
    oldCreation = core.functionCreationOptions;
    oldDisplay = core.displayOptions;
    oldColors = core.colors;
    oldBorders = core.borders;
    oldGenerators = core.dataGeneratorCollections;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
    core.borders = new Borders();
    core.dataGeneratorCollections = new DataGeneratorCollections();
  }

  @AfterEach void tearDown() {
    Core core = Core.get();
    core.functionCreationOptions = oldCreation;
    core.displayOptions = oldDisplay;
    core.colors = oldColors;
    core.borders = oldBorders;
    core.dataGeneratorCollections = oldGenerators;
    library = null;
  }

  @ParameterizedTest @ValueSource(booleans = {false, true})
  void reopeningABinaryExpressionImmediatelyShowsTheSelectedInputPicker(boolean image) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      double[] samples = new double[256];
      Arrays.fill(samples, 1);
      Function a = image ? FunctionFactory.createFunction2D(samples, new double[256], true, "A", 32, 8)
          : FunctionFactory.createFunction1D(samples, new double[256], true, "A");
      Function b = a.clone();
      library = new DefaultListModel<>();
      library.addElement(a);
      library.addElement(b);
      ArrayList<Operation> operations = new ArrayList<>(Arrays.asList(new TimesOp(new ParameterBlock())));
      ArrayList<FunctionProducer> inputs = new ArrayList<>(Arrays.asList(a, b));
      CombineOpsRule rule = new CombineOpsRule(new int[] {FUNCTION, BINARY_OP_TIER_2, FUNCTION}, operations);
      OperatorSystem system = image ? new OperatorSystem2D(rule, inputs, operations)
          : new OperatorSystem1D(rule, inputs, operations);

      // Keep the actual editor, calculator and CardLayout. Only the desktop-owned library
      // and its repaint callback are replaced so opening can be tested without a JFrame.
      CreateOperationSystemPanel panel = image ? new CreateOperationSystem2DPanel(system) {
        @Override public void createFunctionSelectorPanel() { functionSelectorPanel = new FixtureSelector(); }
        @Override public void saveStateToModel() { }
      } : new CreateOperationSystem1DPanel(system) {
        @Override public void createFunctionSelectorPanel() { functionSelectorPanel = new FixtureSelector(); }
        @Override public void saveStateToModel() { }
      };
      FunctionSelectorPanel picker = find(panel, FunctionSelectorPanel.class);
      assertNotNull(picker);
      for (Component c = picker; c != panel; c = c.getParent()) {
        assertTrue(c.isVisible(), "Selected input picker must be visible immediately after opening");
      }
      assertSame(b, picker.getFunction(), "Initial selection must match the highlighted final input");
      assertEquals(2, picker.getComponentCount());
      assertArrayEquals(samples, panel.getFunction().getReal());
    });
  }

  private static class FixtureSelector extends FunctionSelectorPanel {
    FixtureSelector() { super(new GUIEventBroadcaster(), null); }
    @Override public ListModel getFunctionListModel() { return library; }
    @Override public Dimension getButtonSize() { return new Dimension(170, 170); }
  }

  private static <T> T find(Container parent, Class<T> type) {
    for (Component child : parent.getComponents()) {
      if (type.isInstance(child)) return type.cast(child);
      if (child instanceof Container) {
        T result = find((Container) child, type);
        if (result != null) return result;
      }
    }
    return null;
  }
}
