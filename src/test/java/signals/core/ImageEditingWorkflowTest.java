package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import signals.functionterm.GradientFunctionTerm2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.EditImageFunctionTermPanel;
import signals.gui.datagenerator.GUIEvent;
import signals.gui.datagenerator.GUIEventBroadcaster;

/** Reproduces image edits through the real parameter controls and preview renderer. */
class ImageEditingWorkflowTest {
  private DisplayOptions previousDisplayOptions;
  private Colors previousColors;
  private Borders previousBorders;

  @BeforeEach
  void initializeEditorServices() {
    Core core = Core.get();
    previousDisplayOptions = core.displayOptions;
    previousColors = core.colors;
    previousBorders = core.borders;
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
    core.borders = new Borders();
  }

  @AfterEach
  void restoreEditorServices() {
    Core core = Core.get();
    core.displayOptions = previousDisplayOptions;
    core.colors = previousColors;
    core.borders = previousBorders;
  }

  @ParameterizedTest(name = "image edit {0}, centered={1}")
  @CsvSource({"zero width,true", "zero height,true", "outside x,true", "outside y,true",
      "zero width,false", "zero height,false", "outside x,false", "outside y,false"})
  void emptyOrOffCanvasImageEditsProduceBlankImageAndRecover(String edit, boolean centered) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      GradientFunctionTerm2D image = (GradientFunctionTerm2D) new GradientFunctionTerm2D().getDefaultInstance();
      EditImageFunctionTermPanel panel = new EditImageFunctionTermPanel(new GUIEventBroadcaster(), GUIEvent.Descriptor.MODIFIED);
      panel.setIndices(128, 64, centered);
      panel.setFunctionTerm(image);
      panel.setDefaultWidthOn();
      double originalX = image.getCenterX();
      double originalY = image.getCenterY();
      switch (edit) {
        case "zero width": panel.setWidth(0); break;
        case "zero height": panel.setHeight(0); break;
        case "outside x": panel.setCenterX(256); break;
        case "outside y": panel.setCenterY(256); break;
        default: fail("Unknown fixture: " + edit);
      }
      assertArrayEquals(new double[128 * 64], image.create(128, 64, centered));
      var preview = new signals.gui.plot.FunctionTerm2DThumbnailGraphic(image, 128, 64, centered)
          .getScaledImage(new java.awt.Dimension(40, 40));
      assertNotNull(preview.getData());
      panel.setWidth(128);
      panel.setHeight(64);
      panel.setCenterX(originalX);
      panel.setCenterY(originalY);
      double[] recovered = image.create(128, 64, centered);
      assertEquals(128 * 64, recovered.length);
      assertTrue(java.util.Arrays.stream(recovered).anyMatch(v -> v > 0));
    });
  }
}
