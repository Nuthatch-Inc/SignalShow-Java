package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.Field;
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
import signals.gui.HorizontalThumbnailList;
import signals.gui.ThumbnailListRenderer;
import signals.gui.datagenerator.*;
import signals.operation.TimesOp;

/** Retired controls can remain alive in native accessibility; they must release documents. */
class EditorLifecycleWorkflowTest {
    private FunctionCreationOptions creation;
    private DisplayOptions display;
    private Colors colors;
    private Borders borders;
    private DataGeneratorCollections generators;
    private static DefaultListModel<FunctionProducer> library;

    @BeforeEach void initialize() {
        Core c = Core.get();
        creation = c.functionCreationOptions; display = c.displayOptions;
        colors = c.colors; borders = c.borders; generators = c.dataGeneratorCollections;
        c.functionCreationOptions = new FunctionCreationOptions(); c.displayOptions = new DisplayOptions();
        c.colors = new Colors(); c.borders = new Borders(); c.dataGeneratorCollections = new DataGeneratorCollections();
    }

    @AfterEach void restore() {
        Core c = Core.get();
        c.functionCreationOptions = creation; c.displayOptions = display;
        c.colors = colors; c.borders = borders; c.dataGeneratorCollections = generators;
        library = null;
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void retiredFunctionToolbarReleasesItsDocumentWithoutChangingIt(boolean image) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function source = source(image, "A", 2);
            FunctionToolBar toolbar = toolbar(source, image);
            assertSame(source, toolbar.getFunction());
            retire(toolbar);
            assertNull(toolbar.getFunction(), "A retained toolbar must not own the closed document");
            assertSame(source, toolbar(source, image).getFunction(), "Reopening gets a new live toolbar");
            assertEquals(2, source.getReal()[0]);
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void retiredOperationToolbarReleasesItsSystem(boolean image) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            OperatorSystem system = system(image);
            OperatorSystemToolBar toolbar = image ? new OperatorSystemToolBar2D(system)
                : new OperatorSystemToolBar1D(system);
            retire(toolbar);
            assertNull(field(toolbar, "system"), "Toolbar actions must not retain an old operation graph");
            assertProduct(system.getFunction());
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void retiringOperationEditorReleasesInputsAndCanReopen(boolean image) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            OperatorSystem system = system(image);
            CreateOperationSystemPanel panel = editor(system, image);
            assertProduct(panel.getFunction());
            OperationCalculator calculator = find(panel, OperationCalculator.class);
            HorizontalThumbnailList list = find(calculator, HorizontalThumbnailList.class);
            ThumbnailListRenderer renderer = (ThumbnailListRenderer) list.getDefaultRenderer(Object.class);
            // Exercise the real retained renderer, not just the calculator's model list.
            renderer.getTableCellRendererComponent(list, list.getItemAt(1), true, false, 0, 1);
            FunctionSelectorPanel picker = find(panel, FunctionSelectorPanel.class);
            retire(panel);
            assertNull(field(panel, "system"));
            assertNull(field(calculator, "system"));
            assertEquals(0, list.getListSize(), "Retired expression cells must release their input/thumbnail models");
            assertEquals(0, list.getColumnCount());
            assertNull(field(renderer, "thumbnailProducer"));
            assertNull(picker.getFunction(), "The retired input picker must release its selected document");
            assertEquals(0, picker.getComponentCount());
            retire(panel); // Cleanup can be called again without editing the model or firing commands.
            assertProduct(system.getFunction());
            CreateOperationSystemPanel reopened = editor(system, image);
            assertProduct(reopened.getFunction());
            assertSame(library.get(1), find(reopened, FunctionSelectorPanel.class).getFunction());
            retire(reopened);
        });
    }

    private static FunctionToolBar toolbar(Function source, boolean image) {
        return image ? new ReImFunction2DToolBar(source) : new ReImFunction1DToolBar(source);
    }

    private static Function source(boolean image, String name, double value) {
        double[] real = new double[256], imag = new double[256];
        Arrays.fill(real, value); Arrays.fill(imag, .5);
        return image ? FunctionFactory.createFunction2D(real, imag, true, name, 32, 8)
            : FunctionFactory.createFunction1D(real, imag, true, name);
    }

    private static OperatorSystem system(boolean image) {
        library = new DefaultListModel<>();
        library.addElement(source(image, "A", 2)); library.addElement(source(image, "B", 3));
        var inputs = new ArrayList<FunctionProducer>(Arrays.asList(library.get(0), library.get(1)));
        var operations = new ArrayList<Operation>(Arrays.asList(new TimesOp(null)));
        var rule = new CombineOpsRule(new int[] {FUNCTION, BINARY_OP_TIER_2, FUNCTION}, operations);
        return image ? new OperatorSystem2D(rule, inputs, operations) : new OperatorSystem1D(rule, inputs, operations);
    }

    private static void assertProduct(Function output) {
        assertNotNull(output);
        for (int i = 0; i < 256; i++) {
            assertEquals(5.75, output.getReal()[i], 1e-12);
            assertEquals(2.5, output.getImaginary()[i], 1e-12);
        }
    }

    private static CreateOperationSystemPanel editor(OperatorSystem system, boolean image) {
        // Only the desktop library and its repaint callback need a fixture in a headless test.
        return image ? new CreateOperationSystem2DPanel(system) {
            @Override public void createFunctionSelectorPanel() { functionSelectorPanel = new FixtureSelector(); }
            @Override public void saveStateToModel() { }
        } : new CreateOperationSystem1DPanel(system) {
            @Override public void createFunctionSelectorPanel() { functionSelectorPanel = new FixtureSelector(); }
            @Override public void saveStateToModel() { }
        };
    }

    private static class FixtureSelector extends FunctionSelectorPanel {
        FixtureSelector() { super(new GUIEventBroadcaster(), null); }
        @Override public ListModel getFunctionListModel() { return library; }
        @Override public Dimension getButtonSize() { return new Dimension(170, 170); }
    }

    private static void retire(Object component) {
        if (component instanceof Deconstructable cleanup) cleanup.deconstruct();
    }

    private static Object field(Object object, String name) {
        for (Class<?> type = object.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field f = type.getDeclaredField(name); f.setAccessible(true); return f.get(object);
            } catch (NoSuchFieldException e) { /* Search the actual owner in the superclass. */
            } catch (IllegalAccessException e) { throw new AssertionError(e); }
        }
        throw new AssertionError("Missing field " + name);
    }

    private static <T> T find(Container parent, Class<T> type) {
        for (Component child : parent.getComponents()) {
            if (type.isInstance(child)) return type.cast(child);
            if (child instanceof Container container) {
                T found = find(container, type); if (found != null) return found;
            }
        }
        return null;
    }
}
