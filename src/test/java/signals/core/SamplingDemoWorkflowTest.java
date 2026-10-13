package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.demo.Sampling1D;
import signals.demo.SamplingModel1D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.ParameterEditor;

/** Ordinary sampling controls, band-limited reconstruction and document revisits. */
class SamplingDemoWorkflowTest {
    private FunctionCreationOptions creation;
    private DisplayOptions display;
    private Colors colors;
    private Borders borders;

    @BeforeEach void initialize() {
        Core c = Core.get();
        creation = c.functionCreationOptions; display = c.displayOptions;
        colors = c.colors; borders = c.borders;
        c.functionCreationOptions = new FunctionCreationOptions();
        c.displayOptions = new DisplayOptions(); c.colors = new Colors(); c.borders = new Borders();
    }

    @AfterEach void restore() {
        Core c = Core.get();
        c.functionCreationOptions = creation; c.displayOptions = display;
        c.colors = colors; c.borders = borders;
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void defaultReconstructionPreservesTheSource(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Core.getFunctionCreationOptions().setZeroCentered1D(!centered);
            Function1D input = source(centered, 1, true);
            Sampling1D demo = new Sampling1D(input);
            reconstruct(demo).doClick();
            assertOutput(demo, input);
        });
    }

    static Stream<Arguments> origins() {
        return Stream.of(false, true).flatMap(centered ->
            Stream.of(false, true).map(defaultCentered -> Arguments.of(centered, defaultCentered)));
    }

    @ParameterizedTest @MethodSource("origins")
    void samplingAndWidthEditsFollowBothComplexChannels(boolean centered, boolean defaultCentered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Core.getFunctionCreationOptions().setZeroCentered1D(defaultCentered);
            Function1D input = source(centered, 1, true);
            double[] originalReal = input.getReal().clone(), originalImaginary = input.getImaginary().clone();
            Sampling1D demo = new Sampling1D(input);
            for (int period : new int[] {2, 4, 1}) {
                period(demo).setText(Integer.toString(period));
                assertEquals(128.0 / period, ((signals.functionterm.AnalyticFunctionTerm1D)
                    field(demo, "rect")).getWidth() * 2, "Period updates the displayed filter width");
                Function sampled = plotted(demo, "spatialPanel", 0);
                for (int i = 0; i < 128; i++) {
                    int coordinate = centered ? i - 64 : i;
                    assertEquals(coordinate % period == 0 ? originalReal[i] : 0, sampled.getReal()[i], 1e-12);
                    assertEquals(coordinate % period == 0 ? originalImaginary[i] : 0, sampled.getImaginary()[i], 1e-12);
                }
                if (!reconstruct(demo).isSelected()) reconstruct(demo).doClick();
                assertOutput(demo, input);
                for (int width : new int[] {24, 8, 20}) {
                    width(demo).setText(Integer.toString(width));
                    assertOutput(demo, source(centered, 1, width > 10));
                }
                reconstruct(demo).doClick();
                assertEquals(2, sources(demo, "spatialPanel").size(), "Turning reconstruction off removes its plot");
                assertFalse(width(demo).isEnabled());
            }
            assertArrayEquals(originalReal, input.getReal(), "Source real data remain unchanged");
            assertArrayEquals(originalImaginary, input.getImaginary(), "Source imaginary data remain unchanged");
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void settingsSurviveReopenSourceEditsAndIndependentDuplication(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function1D input = source(centered, 1, true);
            SamplingModel1D model = new SamplingModel1D(input);
            Sampling1D original = view(model);
            period(original).setText("4"); width(original).setText("20");
            reconstruct(original).doClick();
            ((JTabbedPane) field(original, "spatialPanel")).setSelectedIndex(1);
            ((JTabbedPane) field(original, "frequencyPanel")).setSelectedIndex(2);
            SamplingModel1D duplicate = model.clone();
            leave(original);
            Sampling1D reopened = view(model);
            assertSettings(reopened, 4, 20); assertOutput(reopened, input); leave(reopened);
            Function1D changed = source(centered, 2, true);
            input.setPartA(changed.getPartA()); input.setPartB(changed.getPartB());
            Sampling1D edited = view(model);
            assertSettings(edited, 4, 20); assertOutput(edited, changed);
            Sampling1D copied = view(duplicate);
            assertSettings(copied, 4, 20); assertOutput(copied, changed);
            period(copied).setText("2"); leave(copied); leave(edited);
            Sampling1D independent = view(model);
            assertSettings(independent, 4, 20); assertOutput(independent, changed); leave(independent);
        });
    }

    private static Function1D source(boolean centered, double scale, boolean higherMode) {
        double[] real = new double[128], imaginary = new double[128];
        for (int i = 0; i < 128; i++) {
            double angle = 2 * Math.PI * (centered ? i - 64 : i) / 128;
            real[i] = scale * (0.4 + 0.6 * Math.cos(3 * angle));
            imaginary[i] = scale * (-0.2 + (higherMode ? 0.3 * Math.sin(5 * angle) : 0));
        }
        return (Function1D) FunctionFactory.createFunction1D(real, imaginary, centered, "Sampling modes");
    }

    private static void assertOutput(Sampling1D demo, Function expected) {
        Function output = plotted(demo, "spatialPanel", 2);
        assertAll("Reconstruction follows the input and selected passband",
            () -> assertArrayEquals(expected.getReal(), output.getReal(), 1e-9, "Real reconstruction"),
            () -> assertArrayEquals(expected.getImaginary(), output.getImaginary(), 1e-9, "Imaginary reconstruction"),
            () -> assertEquals(expected.isZeroCentered(), output.isZeroCentered(), "Origin retained"));
    }

    private static void assertSettings(Sampling1D demo, int period, double width) {
        assertEquals(period, ((signals.operation.SampleOp1D) field(demo, "samplingOp")).getSamplingPeriod(), "Retain period");
        assertEquals(width, ((signals.functionterm.AnalyticFunctionTerm1D) field(demo, "rect")).getWidth() * 2);
        assertTrue(reconstruct(demo).isSelected(), "Retain reconstruction");
        assertTrue(width(demo).isEnabled());
        assertEquals(1, ((JTabbedPane) field(demo, "spatialPanel")).getSelectedIndex());
        assertEquals(2, ((JTabbedPane) field(demo, "frequencyPanel")).getSelectedIndex());
    }

    private static ParameterEditor period(Sampling1D demo) { return editorForLabel(demo, "sampling period (pixels):"); }
    private static ParameterEditor width(Sampling1D demo) { return (ParameterEditor) field(demo, "filterspinner"); }
    private static JCheckBox reconstruct(Sampling1D demo) { return (JCheckBox) field(demo, "reconstructCB"); }
    private static ParameterEditor editorForLabel(Container root, String text) {
        for (Component child : root.getComponents()) {
            if (child instanceof JLabel label && text.equals(label.getText())) return (ParameterEditor) label.getLabelFor();
            if (child instanceof Container nested) {
                ParameterEditor editor = editorForLabel(nested, text);
                if (editor != null) return editor;
            }
        }
        return null;
    }
    private static List<?> sources(Sampling1D demo, String panel) {
        Object[] plots = (Object[]) field(field(demo, panel), "plots");
        return (List<?>) field(plots[0], "sourceData");
    }
    private static Function plotted(Sampling1D demo, String panel, int index) {
        return ((SourceData) sources(demo, panel).get(index)).getFunction();
    }
    private static Sampling1D view(SamplingModel1D model) {
        try {
            Method method = model.getClass().getDeclaredMethod("createView"); method.setAccessible(true);
            return (Sampling1D) method.invoke(model);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }
    private static void leave(Object view) {
        if (view instanceof Savable save) save.saveStateToModel();
        if (view instanceof Deconstructable cleanup) cleanup.deconstruct();
    }
    private static Object field(Object object, String name) {
        for (Class<?> type = object.getClass(); type != null; type = type.getSuperclass()) {
            try { Field f = type.getDeclaredField(name); f.setAccessible(true); return f.get(object); }
            catch (NoSuchFieldException e) { /* Continue to the owner. */ }
            catch (IllegalAccessException e) { throw new AssertionError(e); }
        }
        throw new AssertionError("Missing field " + name);
    }
}
