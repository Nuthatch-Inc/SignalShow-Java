package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.renderable.ParameterBlock;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.CreateBinaryOperation2DPanel;
import signals.gui.datagenerator.DataGeneratorSelectorPanel;
import signals.gui.datagenerator.GUIEventBroadcaster;
import signals.operation.MinusOp;
import signals.operation.PlusOp;

/** Exercises the same search field, selection events and owning editor as the UI. */
class DataGeneratorSelectionTest {
  private Colors previousColors;
  private Borders previousBorders;
  private DataGeneratorCollections previousCollections;

  @BeforeEach
  void initializeSelectorServices() {
    Core core = Core.get();
    previousColors = core.colors;
    previousBorders = core.borders;
    previousCollections = core.dataGeneratorCollections;
    core.colors = new Colors();
    core.borders = new Borders();
    core.dataGeneratorCollections = new DataGeneratorCollections();
  }

  @AfterEach
  void restoreSelectorServices() {
    Core core = Core.get();
    core.colors = previousColors;
    core.borders = previousBorders;
    core.dataGeneratorCollections = previousCollections;
  }

  @ParameterizedTest
  @CsvSource({"plus,PlusOp", "minus,MinusOp", "times,TimesOp", "divide,DivideOp",
      "convolve,ConvolveOp", "correlate,CorrelateOp", "PL,PlusOp"})
  void typedSearchSelectsMatching2DOperation(String query, String operationClass) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      CreateBinaryOperation2DPanel panel = panel();
      DataGeneratorSelectorPanel selector = find(panel, DataGeneratorSelectorPanel.class);
      // setText on the real input document invokes the production search listener.
      find(selector, JTextField.class).setText(query);
      assertEquals(1, selector.getList().getModel().getSize());
      assertEquals(operationClass, selector.getSelectedItem().getClass().getSimpleName());
      assertEquals(operationClass, panel.getDataGenerator().getClass().getSimpleName());
    });
  }

  @Test
  void unmatchedSearchCanBeClearedAndAnotherOperationSelected() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      CreateBinaryOperation2DPanel panel = panel();
      DataGeneratorSelectorPanel selector = find(panel, DataGeneratorSelectorPanel.class);
      JTextField search = find(selector, JTextField.class);
      search.setText("not an operation");
      assertEquals(0, selector.getList().getModel().getSize());
      assertInstanceOf(PlusOp.class, panel.getDataGenerator());
      search.setText("");
      assertEquals(6, selector.getList().getModel().getSize());
      search.setText("minus");
      assertEquals(1, selector.getList().getModel().getSize());
      assertInstanceOf(MinusOp.class, panel.getDataGenerator());
    });
  }

  private CreateBinaryOperation2DPanel panel() {
    CreateBinaryOperation2DPanel panel = new CreateBinaryOperation2DPanel(new GUIEventBroadcaster());
    panel.setListNew(new PlusOp(new ParameterBlock()));
    return panel;
  }

  private static <T extends Component> T find(Container root, Class<T> type) {
    for (Component child : root.getComponents()) {
      if (type.isInstance(child)) return type.cast(child);
      if (child instanceof Container) {
        T match = find((Container) child, type);
        if (match != null) return match;
      }
    }
    return null;
  }
}
