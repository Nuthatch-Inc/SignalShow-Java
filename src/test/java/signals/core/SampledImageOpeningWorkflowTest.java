package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.DataFunctionTerm2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.*;
import signals.io.ComplexIO;
import signals.operation.OffsetOp;

/** Loaded files and operation outputs must open their existing samples on the first visit. */
class SampledImageOpeningWorkflowTest {
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;
    private Borders oldBorders;
    private DataGeneratorCollections oldGenerators;
    @TempDir Path temporary;

    @BeforeEach void setUp() {
        Core c = Core.get();
        oldCreation = c.functionCreationOptions; oldDisplay = c.displayOptions;
        oldColors = c.colors; oldBorders = c.borders; oldGenerators = c.dataGeneratorCollections;
        c.functionCreationOptions = new FunctionCreationOptions(); c.displayOptions = new DisplayOptions();
        c.colors = new Colors(); c.borders = new Borders(); c.dataGeneratorCollections = new DataGeneratorCollections();
    }
    @AfterEach void tearDown() {
        Core c = Core.get();
        c.functionCreationOptions = oldCreation; c.displayOptions = oldDisplay;
        c.colors = oldColors; c.borders = oldBorders; c.dataGeneratorCollections = oldGenerators;
    }

    @ParameterizedTest
    @CsvSource({"loaded,true", "loaded,false", "result,true", "result,false", "polar,true", "polar,false"})
    void firstEditorVisitRetainsBothSampledChannels(String caller, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int width = 64, height = 128;
            double[] a = new double[width * height], b = new double[a.length];
            for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
                int i = row * width + col;
                a[i] = 1 + col / 64.0; b[i] = row / 128.0;
            }
            Function2D function;
            if (caller.equals("polar")) {
                function = (Function2D) FunctionFactory.createMagPhaseFunction2D(a, b, centered, "polar", width, height);
            } else {
                function = (Function2D) FunctionFactory.createFunction2D(a, b, centered, "sampled", width, height);
                if (caller.equals("loaded")) {
                    try {
                        Path file = temporary.resolve("normal-image.txt");
                        ComplexIO.write(file.toFile(), function);
                        function = (Function2D) ComplexIO.read(file.toFile());
                    } catch (IOException e) { throw new AssertionError(e); }
                } else {
                    OffsetOp offset = (OffsetOp) new OffsetOp().getDefaultInstance();
                    offset.setValue(0, .5); offset.setValue(1, -.25);
                    function = (Function2D) offset.create(function);
                    a = a.clone(); b = b.clone();
                    for (int i = 0; i < a.length; i++) { a[i] += .5; b[i] -= .25; }
                }
            }
            final Component[] visible = new Component[1];
            // Use the same toolbar/state selection as Function.show(). Replace only the
            // JFrame content destination, leaving the editor/calculator/selection path intact.
            Function2DToolBar toolbar = caller.equals("polar")
                ? new MagPhaseFunction2DToolBar(function, function.newState) {
                    @Override public void setContent(Component c, String tab) { visible[0] = c; }
                } : new ReImFunction2DToolBar(function, function.newState) {
                    @Override public void setContent(Component c, String tab) { visible[0] = c; }
                };
            try {
                toolbar.editPartA();
                checkEditor(visible[0], function.getPartA(), a, width, height, centered);
                find((Container) visible[0], CreateFunctionPart2DPanel.class).deconstruct();
                toolbar.editPartB();
                checkEditor(visible[0], function.getPartB(), b, width, height, centered);
            } finally {
                if (visible[0] != null) find((Container) visible[0], CreateFunctionPart2DPanel.class).deconstruct();
                toolbar.deconstruct();
            }
        });
    }

    private static void checkEditor(Component visible, FunctionPart2D original, double[] expected,
            int width, int height, boolean centered) {
        CreateFunctionPart2DPanel editor = find((Container) visible, CreateFunctionPart2DPanel.class);
        FunctionCalculator2D calculator = find(editor, FunctionCalculator2D.class);
        assertEquals(1, calculator.getFunctionTerm2DList().size(),
            "First opening must show the existing data term instead of an empty creation chooser");
        assertInstanceOf(DataFunctionTerm2D.class, calculator.getFunctionTerm2DList().get(0));
        assertSame(original.getTermsList().get(0), calculator.getFunctionTerm2DList().get(0));
        assertArrayEquals(expected, editor.getFunctionPart2D().create(width, height, centered), 1e-12,
            "The visible editor must retain the actual saved/output samples");
    }

    private static <T> T find(Container parent, Class<T> type) {
        if (type.isInstance(parent)) return type.cast(parent);
        for (Component child : parent.getComponents()) {
            if (child instanceof Container) {
                T result = find((Container) child, type);
                if (result != null) return result;
            }
        }
        return null;
    }
}
