package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.Component;
import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.functionterm.ConstantFunctionTerm2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.datagenerator.*;
import signals.operation.PlusOp;

/** Ordinary image-size/origin edits must also govern the next image's creation. */
class CreationDefaultsWorkflowTest {
    private FunctionCreationOptions creation;
    private DisplayOptions display;
    private Colors colors;
    private Borders borders;
    private DataGeneratorCollections generators;

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
    }

    static Stream<Arguments> imageEdits() {
        return Stream.of(false, true).flatMap(polar -> Stream.of(false, true).flatMap(centered ->
            Stream.of(new int[]{128, 64}, new int[]{64, 128}).map(size ->
                Arguments.of(polar, centered, size[0], size[1]))));
    }

    @ParameterizedTest(name = "polar={0}, centered={1}, size={2}x{3}")
    @MethodSource("imageEdits")
    void rectangularEditsPreserveNextImageShapeAndKeepOneDOriginIndependent(
            boolean polar, boolean centered, int width, int height) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            FunctionCreationOptions options = Core.getFunctionCreationOptions();
            options.setZeroCentered1D(!centered);
            options.setZeroCentered2D(!centered);
            Function2D source = polar
                ? new MagPhaseFunction2D(part(3), part(.6), "Original", 256, 256, !centered)
                : new RealImagFunction2D(part(2), part(-3), "Original", 256, 256, !centered);
            source.getReal(); source.getImaginary(); // Also exercise cached-source invalidation.
            int[] refreshes = {0};
            // Replace only the JFrame destination and library repaint, preserving the
            // actual controls, listeners, editor events and function publication.
            Function2DToolBar toolbar = polar ? new MagPhaseFunction2DToolBar(source) {
                @Override public void setContent(Component c, String tab) {}
                @Override protected void refreshLibrary() { refreshes[0]++; }
            } : new ReImFunction2DToolBar(source) {
                @Override public void setContent(Component c, String tab) {}
                @Override protected void refreshLibrary() { refreshes[0]++; }
            };
            try {
                toolbar.editPartA();
                ((JComboBox<?>) field(toolbar, "widthSelector")).setSelectedItem(width);
                ((JComboBox<?>) field(toolbar, "heightSelector")).setSelectedItem(height);
                ((JCheckBox) field(toolbar, "zeroCenteredCheckBox")).doClick();
                assertTrue(refreshes[0] > 0);
                assertEquals(width, source.getDimensionX());
                assertEquals(height, source.getDimensionY());
                assertEquals(centered, source.isZeroCentered());
                Function2D nextCartesian = (Function2D) FunctionFactory.createZeroFunction2D();
                Function2D nextPolar = (Function2D) FunctionFactory.createMagPhaseZeroFunction2D();
                assertAll("Creation settings and both menu callers retain their own axes and origin",
                    () -> assertEquals(width, nextCartesian.getDimensionX(), "next real/imaginary width"),
                    () -> assertEquals(height, nextCartesian.getDimensionY(), "next real/imaginary height"),
                    () -> assertEquals(centered, nextCartesian.isZeroCentered(), "next real/imaginary origin"),
                    () -> assertEquals(width, nextPolar.getDimensionX(), "next magnitude/phase width"),
                    () -> assertEquals(height, nextPolar.getDimensionY(), "next magnitude/phase height"),
                    () -> assertEquals(centered, nextPolar.isZeroCentered(), "next magnitude/phase origin"),
                    () -> assertEquals(!centered, FunctionFactory.createZeroFunction1D().isZeroCentered(),
                        "2D editing must not change the next 1D function's origin"),
                    () -> assertEquals(Math.sqrt(height), options.getDefaultWidthY2D(), "default vertical generator width"));
                double ar = polar ? 3 * Math.cos(.6) : 2, ai = polar ? 3 * Math.sin(.6) : -3;
                assertConstant(source, ar, ai, width, height, centered);
                for (Function2D next : new Function2D[]{nextCartesian, nextPolar}) {
                    boolean nextIsPolar = next instanceof MagPhaseFunction2D;
                    next.setPartA(part(nextIsPolar ? 5 : -2));
                    next.setPartB(part(nextIsPolar ? .4 : .75));
                    double br = nextIsPolar ? 5 * Math.cos(.4) : -2;
                    double bi = nextIsPolar ? 5 * Math.sin(.4) : .75;
                    ArrayList<Operation> operations = new ArrayList<>(Arrays.asList(new PlusOp(new ParameterBlock())));
                    OperatorSystem2D chain = new OperatorSystem2D(
                        new CombineOpsRule(new int[]{FUNCTION, BINARY_OP_TIER_1, FUNCTION}, operations),
                        new ArrayList<>(Arrays.asList(source, next)), operations);
                    assertNotNull(chain.getFunction(), "Normally created same-size images must combine");
                    assertConstant((Function2D) chain.getFunction(), ar + br, ai + bi, width, height, centered);
                }
            } finally {
                if (field(toolbar, "editor") instanceof CreateFunctionPart2DPanel editor) editor.deconstruct();
                toolbar.deconstruct();
            }
        });
    }

    private static FunctionPart2D part(double amplitude) {
        ConstantFunctionTerm2D term = (ConstantFunctionTerm2D) new ConstantFunctionTerm2D().getDefaultInstance();
        term.setAmplitude(amplitude);
        return new FunctionPart2D(new ArrayList<>(Arrays.asList(term)), CombineTermsRule.getDefaultRule());
    }

    private static void assertConstant(Function2D value, double real, double imaginary, int w, int h, boolean centered) {
        assertEquals(w, value.getDimensionX()); assertEquals(h, value.getDimensionY());
        assertEquals(centered, value.isZeroCentered());
        double[] a = value.getReal(), b = value.getImaginary();
        assertEquals(w * h, a.length); assertEquals(w * h, b.length);
        for (int i = 0; i < a.length; i++) {
            assertEquals(real, a[i], 1e-12, "real " + i);
            assertEquals(imaginary, b[i], 1e-12, "imaginary " + i);
        }
    }

    private static Object field(Object owner, String name) {
        for (Class<?> type = owner.getClass(); type != null; type = type.getSuperclass()) {
            try { Field f = type.getDeclaredField(name); f.setAccessible(true); return f.get(owner); }
            catch (NoSuchFieldException ignored) {}
            catch (ReflectiveOperationException e) { throw new AssertionError(e); }
        }
        throw new AssertionError("Missing field " + name);
    }
}
