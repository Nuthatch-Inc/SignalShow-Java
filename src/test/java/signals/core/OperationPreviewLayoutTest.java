package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import signals.gui.Borders;
import signals.gui.ParameterEditor;
import signals.gui.operation.OperationOptionsPanel;
import signals.operation.ThresholdOp;
import signals.operation.RotateOp2D;
import signals.gui.Colors;
import signals.gui.datagenerator.OperationPreviewPanel;
import signals.gui.plot.ImageDisplayPanel;

class OperationPreviewLayoutTest {
  private DisplayOptions previousDisplayOptions;
  private Colors previousColors;
  private Borders previousBorders;

  @BeforeEach
  void initializePreviewServices() {
    Core core = Core.get();
    previousDisplayOptions = core.displayOptions;
    previousColors = core.colors;
    previousBorders = core.borders;
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
    core.borders = new Borders();
  }

  @AfterEach
  void restorePreviewServices() {
    Core core = Core.get();
    core.displayOptions = previousDisplayOptions;
    core.colors = previousColors;
    core.borders = previousBorders;
  }

  @Test
  void twoImagePreviewRequestsRoomForImagesAxesAndScaleControlsBeforeFirstLayout() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      OperationPreviewPanel preview = new OperationPreviewPanel(true);
      List<ImageDisplayPanel> images = new ArrayList<>();
      collectImages(preview, images);
      assertEquals(2, images.size());
      Dimension preferred = preview.getPreferredSize();
      for (ImageDisplayPanel image : images) {
        assertTrue(preferred.width >= image.getPreferredSize().width,
            "Preview must reserve space for the image, axes and scale controls before it has a parent size");
      }
      preview.setSize(preferred);
      layoutTree(preview);
      assertEquals(preferred, preview.getPreferredSize(),
          "Preferred size must not shrink each time the parent lays out the preview");
      for (ImageDisplayPanel image : images) {
        assertTrue(image.getWidth() >= image.getPreferredSize().width);
        assertTrue(image.getHeight() >= image.getPreferredSize().height);
      }
    });
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void operationControlsFitAbovePreviewWithoutTakingTheDocumentationColumn(boolean rotate) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Operation operation = rotate ? new RotateOp2D(new ParameterBlock()) : new ThresholdOp(new ParameterBlock());
      OperationOptionsPanel controls = operation.getOptionsInterface();
      int previewWidth = new OperationPreviewPanel(true).getPreferredSize().width;
      assertTrue(controls.getPreferredSize().width <= previewWidth,
          "Operation controls must fit the image-preview column, leaving room for the selector and help");
      controls.setSize(previewWidth, controls.getPreferredSize().height);
      layoutTree(controls);
      List<ParameterEditor> editors = new ArrayList<>();
      collectEditors(controls, editors);
      assertEquals(rotate ? 1 : 3, editors.size());
      Rectangle area = new Rectangle(0, 0, controls.getWidth(), controls.getHeight());
      for (ParameterEditor editor : editors) {
        Rectangle bounds = SwingUtilities.convertRectangle(editor.getParent(), editor.getBounds(), controls);
        assertTrue(bounds.width > 0 && bounds.height > 0 && area.contains(bounds),
            "Every existing numeric field must remain visible within the parameter area");
      }
    });
  }

  private static void collectEditors(Container parent, List<ParameterEditor> editors) {
    for (Component child : parent.getComponents()) {
      if (child instanceof ParameterEditor editor) editors.add(editor);
      else if (child instanceof Container container) collectEditors(container, editors);
    }
  }

  private static void collectImages(Container parent, List<ImageDisplayPanel> images) {
    for (Component child : parent.getComponents()) {
      if (child instanceof ImageDisplayPanel image) images.add(image);
      else if (child instanceof Container container) collectImages(container, images);
    }
  }

  private static void layoutTree(Container parent) {
    parent.doLayout();
    for (Component child : parent.getComponents()) {
      if (child instanceof Container container) layoutTree(container);
    }
  }
}
