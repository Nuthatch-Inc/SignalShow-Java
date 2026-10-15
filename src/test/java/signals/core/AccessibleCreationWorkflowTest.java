package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.ConstantFunctionTerm2D;
import signals.gui.Borders;
import signals.gui.ParameterEditor;
import signals.gui.datagenerator.*;
import signals.operation.SquaredMagnitudeOp;

/** Accessible edits use the same document listeners and publication as visible text edits. */
class AccessibleCreationWorkflowTest extends MathAuditSupport {
    private Borders borders;
    private DataGeneratorCollections generators;
    @BeforeEach void initializeEditor() {
        borders = Core.get().borders; generators = Core.get().dataGeneratorCollections;
        Core.get().borders = new Borders(); Core.get().dataGeneratorCollections = new DataGeneratorCollections();
    }
    @AfterEach void restoreEditor() {
        Core.get().borders = borders; Core.get().dataGeneratorCollections = generators;
    }

    @ParameterizedTest @CsvSource({"false,true,128,64", "false,false,64,128", "true,true,64,128", "true,false,128,64"})
    void labelledAccessibleEditsPublishBothChannelsAndUpdateAnExistingChain(
            boolean polar, boolean centered, int width, int height) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source = polar
                ? new MagPhaseFunction2D(part(2), part(.6), "Original", width, height, centered)
                : new RealImagFunction2D(part(2), part(.6), "Original", width, height, centered);
            OperatorSystem2D intensity = unary(source, (SquaredMagnitudeOp) new SquaredMagnitudeOp().getDefaultInstance());
            intensity.getFunction().getReal();
            Component[] visible = {null};
            int[] refreshes = {0};
            // Only the JFrame destination and library repaint are supplied by the fixture.
            Function2DToolBar toolbar = polar ? new MagPhaseFunction2DToolBar(source) {
                @Override public void setContent(Component c, String tab) { replaceView(visible, c); }
                @Override protected void refreshLibrary() { refreshes[0]++; }
            } : new ReImFunction2DToolBar(source) {
                @Override public void setContent(Component c, String tab) { replaceView(visible, c); }
                @Override protected void refreshLibrary() { refreshes[0]++; }
            };
            try {
                toolbar.editPartA();
                JTextField name = field(toolbar, "nameTextField");
                accessibleEdit(name, "Accessible image");
                assertEquals("Accessible image", source.getDescriptor());
                accessibleEdit(labelledText((Container) visible[0], "Amplitude:"), "3.25");
                assertResult(source, intensity, polar, 3.25, .6);
                toolbar.editPartB();
                accessibleEdit(labelledText((Container) visible[0], "Amplitude:"), "1.1");
                assertResult(source, intensity, polar, 3.25, 1.1);
                assertTrue(refreshes[0] > 0, "Accessible edits must publish through the real event listener");
                toolbar.editPartA();
                assertEquals("3.25", labelledText((Container) visible[0], "Amplitude:").getText());
                assertResult(source, intensity, polar, 3.25, 1.1);
                assertEquals("Accessible image", ((JTextField) field(toolbar, "nameTextField")).getText());
                // The edit path above already works; the missing label associations are a separate defect.
                assertNamed(field(toolbar, "nameTextField"), "Function Name:");
                assertNamed(field(toolbar, "widthSelector"), "Width:");
                assertNamed(field(toolbar, "heightSelector"), "Height:");
                assertNamed(labelledText((Container) visible[0], "Amplitude:"), "Amplitude:");
            } finally {
                replaceView(visible, null);
                toolbar.deconstruct();
            }
        });
    }

    private static void accessibleEdit(JTextField field, String value) {
        AccessibleContext context = field.getAccessibleContext();
        assertTrue(context.getAccessibleStateSet().contains(AccessibleState.EDITABLE));
        assertTrue(context.getAccessibleStateSet().contains(AccessibleState.FOCUSABLE));
        assertNotNull(context.getAccessibleEditableText());
        context.getAccessibleEditableText().setTextContents(value);
        assertEquals(value, field.getText());
    }
    private static void assertNamed(JComponent control, String expected) {
        String name = control.getAccessibleContext().getAccessibleName();
        assertNotNull(name, "Missing accessible name: " + expected);
        assertEquals(expected, name.trim());
    }
    private static void assertResult(Function2D source, OperatorSystem2D chain, boolean polar, double a, double b) {
        double real = polar ? a * Math.cos(b) : a, imag = polar ? a * Math.sin(b) : b;
        Function result = chain.getFunction();
        for (int i = 0; i < source.getReal().length; i++) {
            assertEquals(real, source.getReal()[i], 1e-12);
            assertEquals(imag, source.getImaginary()[i], 1e-12);
            assertEquals(real * real + imag * imag, result.getReal()[i], 1e-11);
            assertEquals(0, result.getImaginary()[i], 0);
        }
    }
    private static FunctionPart2D part(double amplitude) {
        ConstantFunctionTerm2D term = (ConstantFunctionTerm2D) new ConstantFunctionTerm2D().getDefaultInstance();
        term.setAmplitude(amplitude);
        return new FunctionPart2D(new ArrayList<>(List.of(term)), CombineTermsRule.getDefaultRule());
    }
    private static JTextField labelledText(Container container, String title) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel label && label.getText() != null && title.equals(label.getText().trim())) {
                Component target = label.getLabelFor();
                if (target instanceof ParameterEditor spinner)
                    return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
                return assertInstanceOf(JTextField.class, target);
            }
            if (c instanceof Container child) {
                JTextField result = labelledText(child, title); if (result != null) return result;
            }
        }
        return null;
    }
    private static void replaceView(Component[] visible, Component next) {
        if (visible[0] instanceof Deconstructable prior) prior.deconstruct();
        visible[0] = next;
    }
    @SuppressWarnings("unchecked")
    private static <T> T field(Object owner, String name) {
        for (Class<?> type = owner.getClass(); type != null; type = type.getSuperclass()) {
            try { Field field = type.getDeclaredField(name); field.setAccessible(true); return (T) field.get(owner); }
            catch (NoSuchFieldException missing) { /* Search parent classes. */ }
            catch (IllegalAccessException failure) { throw new AssertionError(failure); }
        }
        throw new AssertionError("Missing field " + name);
    }
}
