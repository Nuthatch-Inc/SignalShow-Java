package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.DataFunctionTerm2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.DataFunctionTerm2DEditor;
import signals.gui.datagenerator.EditImageFunctionTermPanel;
import signals.gui.datagenerator.GUIEvent;
import signals.gui.datagenerator.GUIEventBroadcaster;
import signals.gui.datagenerator.GUIEventListener;
import signals.gui.plot.InteractiveImageGraphic;
import signals.operation.ThresholdOp;

/** Normal imported-image edits through the actual Swing fields and linked output. */
class ImportedImageEditingWorkflowTest {
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;
    private Borders oldBorders;
    // The production broadcaster holds weak references; this fixture owns its listener.
    private GUIEventListener editListener;

    @BeforeEach void setUp() {
        Core c = Core.get();
        oldCreation = c.functionCreationOptions; oldDisplay = c.displayOptions;
        oldColors = c.colors; oldBorders = c.borders;
        c.functionCreationOptions = new FunctionCreationOptions();
        c.displayOptions = new DisplayOptions(); c.colors = new Colors(); c.borders = new Borders();
    }
    @AfterEach void tearDown() {
        editListener = null;
        Core c = Core.get();
        c.functionCreationOptions = oldCreation; c.displayOptions = oldDisplay;
        c.colors = oldColors; c.borders = oldBorders;
    }

    @ParameterizedTest @CsvSource({"amplitude,true", "amplitude,false", "translate,true",
        "translate,false", "resize,true", "resize,false", "crop,true", "crop,false"})
    void editedSamplesAndLinkedThresholdFollowDisplayedParameters(String edit, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int w = 128, h = 64;
            double[] input = new double[w * h];
            for (int y = 0; y < h; y++) for (int x = 0; x < w; x++)
                input[y * w + x] = edit.equals("resize") ? 1 : (x + 2 * y) / 256.0;
            Function2D source = (Function2D) FunctionFactory.createFunction2D(input.clone(),
                new double[input.length], centered, "Imported", w, h);
            DataFunctionTerm2D term = (DataFunctionTerm2D) source.getPartA().getTermsList().get(0);
            assertArrayEquals(input, source.getReal());
            GUIEventBroadcaster bus = new GUIEventBroadcaster();
            editListener = event -> source.setPartA(source.getPartA());
            bus.addGUIEventListener(editListener);
            EditImageFunctionTermPanel panel = new EditImageFunctionTermPanel(bus, GUIEvent.Descriptor.MODIFIED);
            panel.setIndices(w, h, centered);
            panel.setDefaultWidthOff();
            panel.setFunctionTerm(term);
            ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
            ArrayList<Operation> ops = new ArrayList<>(Arrays.asList(threshold));
            OperatorSystem2D chain = new OperatorSystem2D(new CombineOpsRule(
                new int[] {UNARY_OP_TIER_2, LEFT_PAREN, FUNCTION, RIGHT_PAREN}, ops),
                new ArrayList<>(Arrays.asList(source)), ops);
            chain.getFunction().getReal(); // Materialize the old result before editing.
            if (edit.equals("amplitude")) panel.setAmplitude(2);
            if (edit.equals("translate")) {
                panel.setCenterX((centered ? 0 : w / 2) + 16);
                panel.setCenterY((centered ? 0 : h / 2) + 8);
            }
            if (edit.equals("resize") || edit.equals("crop")) { panel.setWidth(64); panel.setHeight(32); }
            if (edit.equals("crop")) panel.setMode(Constants.CROP_MODE);
            double[] result = source.getReal();
            Function operated = chain.getFunction();
            for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
                int i = y * w + x;
                double expected;
                if (edit.equals("amplitude")) expected = 2 * input[i];
                else if (edit.equals("translate")) expected = x >= 16 && y < h - 8 ? input[(y + 8) * w + x - 16] : 0;
                else expected = x >= 32 && x < 96 && y >= 16 && y < 48 ? (edit.equals("crop") ? input[i] : 1) : 0;
                assertEquals(expected, result[i], 1e-12, edit + " pixel " + x + "," + y);
                assertEquals(expected >= 0.5 ? 1 : 0, operated.getReal()[i], 0);
                assertEquals(0, operated.getImaginary()[i], 0);
            }
            assertArrayEquals(input, term.getOriginalData(), "Edits must not modify loaded pixels");
        });
    }

    @Test void selectingAndReplacingLoadedDataRefreshesTheVisiblePreview() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                EditImageFunctionTermPanel panel = new EditImageFunctionTermPanel(new GUIEventBroadcaster(), GUIEvent.Descriptor.MODIFIED);
                panel.setIndices(128, 64, true);
                panel.setDefaultWidthOff();
                for (double amplitude : new double[] {0.25, 0.75}) {
                    double[] input = new double[128 * 64]; Arrays.fill(input, amplitude);
                    Function2D source = (Function2D) FunctionFactory.createFunction2D(input, new double[input.length], true, "Imported", 128, 64);
                    DataFunctionTerm2D term = (DataFunctionTerm2D) source.getPartA().getTermsList().get(0);
                    panel.setFunctionTerm(term);
                    assertInstanceOf(DataFunctionTerm2DEditor.class, panel.getEditor());
                    Field displayField = DataFunctionTerm2DEditor.class.getDeclaredField("display");
                    displayField.setAccessible(true);
                    Object display = displayField.get(panel.getEditor());
                    Field dataField = InteractiveImageGraphic.class.getDeclaredField("data");
                    dataField.setAccessible(true);
                    assertArrayEquals(input, (double[]) dataField.get(display), "Preview must follow the selected loaded term immediately");
                }
            } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
        });
    }
}
