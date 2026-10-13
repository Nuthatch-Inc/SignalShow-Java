package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.demo.*;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.ParameterEditor;

/** Model-created views must reopen with their controls and current source data. */
class FrequencyDemoReopenWorkflowTest {
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

    static Stream<Arguments> filters() {
        return Stream.of(false, true).flatMap(twoD ->
            Stream.of(false, true).map(centered -> Arguments.of(twoD, centered)));
    }

    @ParameterizedTest @MethodSource("filters")
    void filterSettingsSurviveReopenSourceEditsAndDuplication(boolean twoD, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function input = constant(twoD, centered, 1);
            DemoModel model = twoD ? new FilteringModel2D(input) : new FilteringModel1D(input);
            Object original = view(model);
            click(original, "gaussianFilterButton"); click(original, "brFilterButton");
            for (int i = 0; i < 5; i++) parameter(original, i, new double[] {0.7, 0, 4, 3, 4}[i]);
            if (twoD) click(original, "imagButton");
            else {
                ((JTabbedPane) field(original, "spatialPanel")).setSelectedIndex(1);
                ((JTabbedPane) field(original, "frequencyPanel")).setSelectedIndex(2);
            }
            // Duplicate while still editing, before GUI.saveContent is called.
            DemoModel duplicate = model.clone();
            leave(original);
            Object reopened = view(model);
            assertFilterSettings(reopened, twoD, 0.7);
            double gain = 0.7 * (1 - 2 * Math.exp(-Math.PI * Math.pow(4.0 / 3, 4)));
            assertConstant((Function) field(reopened, "reconstructed"), gain, centered);
            leave(reopened);
            Function changed = constant(twoD, centered, 2);
            replaceSource(input, changed);
            reopened = view(model);
            assertFilterSettings(reopened, twoD, 0.7);
            assertConstant((Function) field(reopened, "reconstructed"), 2 * gain, centered);
            Object copied = view(duplicate);
            assertFilterSettings(copied, twoD, 0.7);
            parameter(copied, 0, 1.2);
            leave(copied); leave(reopened);
            Object independent = view(model);
            assertFilterSettings(independent, twoD, 0.7);
            assertConstant((Function) field(independent, "reconstructed"), 2 * gain, centered);
            leave(independent);
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void frequencyLocationChoicesSurviveReopenAndSourceEdits(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function input = constant(true, centered, 1);
            DemoModel model = new FrequencyLocation2DModel(input);
            Object original = view(model);
            ((ParameterEditor) field(original, "innerRadiusEditor")).setText("3");
            ((ParameterEditor) field(original, "outerRadiusEditor")).setText("6");
            click(original, "dcCheckBox"); click(original, "lfCheckBox");
            click(original, "mfCheckBox"); click(original, "absvalCheckBox"); click(original, "imagButton");
            DemoModel duplicate = model.clone();
            leave(original);
            for (int scale = 1; scale <= 2; scale++) {
                Function changed = constant(true, centered, scale);
                replaceSource(input, changed);
                Object reopened = view(model);
                assertLocationSettings(reopened);
                double[][] low = (double[][]) field(reopened, "lpfiltered");
                for (int i = 0; i < low[0].length; i++) {
                    assertEquals(0.4 * scale, low[0][i], 1e-9, "Retained DC choice follows source edits");
                    assertEquals(0.2 * scale, low[1][i], 1e-9);
                }
                leave(reopened);
            }
            Object copied = view(duplicate);
            assertLocationSettings(copied);
            ((ParameterEditor) field(copied, "innerRadiusEditor")).setText("2");
            leave(copied);
            Object independent = view(model);
            assertLocationSettings(independent);
            leave(independent);
        });
    }

    private static void assertFilterSettings(Object view, boolean twoD, double amplitude) {
        assertTrue(((AbstractButton) field(view, "gaussianFilterButton")).isSelected(), "Retain Gaussian shape");
        assertTrue(((AbstractButton) field(view, "brFilterButton")).isSelected(), "Retain band rejection");
        AnalyticFunctionTerm1D term = (AnalyticFunctionTerm1D) field(view, "filterTerm");
        double[] expected = {amplitude, 0, 4, 3, 4};
        for (int i = 0; i < expected.length; i++) assertEquals(expected[i], ((Number) term.getParameter(i)).doubleValue());
        if (twoD) assertTrue(((AbstractButton) field(view, "imagButton")).isSelected());
        else {
            assertEquals(1, ((JTabbedPane) field(view, "spatialPanel")).getSelectedIndex());
            assertEquals(2, ((JTabbedPane) field(view, "frequencyPanel")).getSelectedIndex());
        }
    }

    private static void assertLocationSettings(Object view) {
        assertEquals(3.0, (double) field(view, "innerRadius"), "Retain low cutoff");
        assertEquals(6.0, (double) field(view, "outerRadius"), "Retain mid cutoff");
        for (String name : new String[] {"dcCheckBox", "hfCheckBox", "imagButton"})
            assertTrue(((AbstractButton) field(view, name)).isSelected(), name);
        for (String name : new String[] {"lfCheckBox", "mfCheckBox", "absvalCheckBox", "realButton"})
            assertFalse(((AbstractButton) field(view, name)).isSelected(), name);
    }

    private static void parameter(Object view, int index, double value) {
        JPanel panel = (JPanel) field(view, "filterParamPanel");
        ((ParameterEditor) panel.getComponent(2 * index + 1)).setText(index == 4 ? Integer.toString((int) value) : Double.toString(value));
    }

    private static Function constant(boolean twoD, boolean centered, int scale) {
        double[] real = new double[twoD ? 512 : 128], imaginary = new double[real.length];
        Arrays.fill(real, 0.4 * scale); Arrays.fill(imaginary, 0.2 * scale);
        return twoD ? FunctionFactory.createFunction2D(real, imaginary, centered, "Demo source", 32, 16)
            : FunctionFactory.createFunction1D(real, imaginary, centered, "Demo source");
    }

    private static void replaceSource(Function input, Function changed) {
        if (input instanceof Function2D image) {
            image.setPartA(((Function2D) changed).getPartA());
            image.setPartB(((Function2D) changed).getPartB());
        } else {
            ((Function1D) input).setPartA(((Function1D) changed).getPartA());
            ((Function1D) input).setPartB(((Function1D) changed).getPartB());
        }
    }

    private static void assertConstant(Function function, double gain, boolean centered) {
        assertEquals(centered, function.isZeroCentered());
        for (int i = 0; i < function.getReal().length; i++) {
            assertEquals(0.4 * gain, function.getReal()[i], 1e-9);
            assertEquals(0.2 * gain, function.getImaginary()[i], 1e-9);
        }
    }

    private static void leave(Object view) {
        // The production GUI calls these contracts, in this order, on view replacement.
        if (view instanceof Savable save) save.saveStateToModel();
        if (view instanceof Deconstructable cleanup) cleanup.deconstruct();
    }

    private static Object view(DemoModel model) {
        try {
            Method method = model.getClass().getDeclaredMethod("createView"); method.setAccessible(true);
            return method.invoke(model);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    private static void click(Object object, String name) { ((AbstractButton) field(object, name)).doClick(); }

    private static Object field(Object object, String name) {
        for (Class<?> type = object.getClass(); type != null; type = type.getSuperclass()) {
            try { Field f = type.getDeclaredField(name); f.setAccessible(true); return f.get(object); }
            catch (NoSuchFieldException e) { /* Continue to the shared demo owner. */ }
            catch (IllegalAccessException e) { throw new AssertionError(e); }
        }
        throw new AssertionError("Missing field " + name);
    }
}
