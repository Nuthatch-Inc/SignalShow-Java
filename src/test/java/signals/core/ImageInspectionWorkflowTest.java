package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.gui.Borders;
import signals.gui.plot.DisplayCursorPanel;
import signals.gui.plot.Function2DOverviewPanel;
import signals.gui.plot.ImageCursorPanel;
import signals.operation.ScaleOp;

/** Verify values through the enlarged image's actual channel, cursor and display controls. */
class ImageInspectionWorkflowTest extends MathAuditSupport {
    private Borders borders;
    @BeforeEach void prepareBorders() { borders = Core.get().borders; Core.get().borders = new Borders(); }
    @AfterEach void restoreBorders() { Core.get().borders = borders; }

    @ParameterizedTest
    @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
    void cursorFollowsChannelSwitchesInTheReusedEnlargedImage(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2DOverviewPanel view = new Function2DOverviewPanel(fixture(width, height, centered));
            ImageCursorPanel cursor = field(view, "largePlotPanel");
            try {
                for (int channel : new int[]{0, 1, 2, 3, 0}) {
                    enlarge(view, channel);
                    enableCursor(cursor);
                    double real = 3 + 3 / 128.0 + 4 / 256.0;
                    double imag = 4 + 3 / 64.0 - 4 / 128.0;
                    double expected = channel == 0 ? real : channel == 1 ? imag : channel == 2
                        ? Math.hypot(real, imag) : Math.atan2(imag, real);
                    assertCursor(cursor, width, height, centered, 3, 4, expected, channel == 3);
                    view.updatePlots();
                }
            } finally { cursor.deconstruct(); view.deconstruct(); }
        });
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
    void cursorFollowsMagnitudeOptionsAndTheirReturnToThumbnails(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2DOverviewPanel view = new Function2DOverviewPanel(fixture(width, height, centered));
            ImageCursorPanel cursor = field(view, "largePlotPanel");
            try {
                enlarge(view, 2); enableCursor(cursor);
                double real = 3 + 3 / 128.0 + 4 / 256.0, imag = 4 + 3 / 64.0 - 4 / 128.0;
                double magnitude = Math.hypot(real, imag), intensity = real * real + imag * imag;
                assertCursor(cursor, width, height, centered, 3, 4, magnitude, false);
                checkBox(cursor, "Squared").doClick(0);
                assertCursor(cursor, width, height, centered, 3, 4, intensity, false);
                checkBox(cursor, "Logarithm").doClick(0);
                assertCursor(cursor, width, height, centered, 3, 4, Math.log10(intensity), false);
                view.updatePlots(); enlarge(view, 2); enableCursor(cursor);
                assertTrue(checkBox(cursor, "Squared").isSelected());
                assertTrue(checkBox(cursor, "Logarithm").isSelected());
                assertCursor(cursor, width, height, centered, 3, 4, Math.log10(intensity), false);
                checkBox(cursor, "Squared").doClick(0);
                assertCursor(cursor, width, height, centered, 3, 4, Math.log10(magnitude), false);
                checkBox(cursor, "Logarithm").doClick(0);
                assertCursor(cursor, width, height, centered, 3, 4, magnitude, false);
            } finally { cursor.deconstruct(); view.deconstruct(); }
        });
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
    void cursorFollowsOrdinaryUnwrappedPhaseChanges(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            double[] real = new double[width * height], imag = new double[real.length];
            for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
                double phase = .12 * col + .08 * row;
                real[row * width + col] = Math.cos(phase); imag[row * width + col] = Math.sin(phase);
            }
            Function2DOverviewPanel view = new Function2DOverviewPanel(image(real, imag, width, height, centered));
            ImageCursorPanel cursor = field(view, "largePlotPanel");
            try {
                enlarge(view, 3); enableCursor(cursor);
                int col = 20, row = 15;
                int x = col - (centered ? width / 2 : 0), y = height - 1 - row - (centered ? height / 2 : 0);
                double phase = .12 * col + .08 * row;
                assertCursor(cursor, width, height, centered, x, y, phase - 2 * Math.PI, true);
                checkBox(cursor, "Unwrap").doClick(0);
                assertCursor(cursor, width, height, centered, x, y, phase, true);
                checkBox(cursor, "Unwrap").doClick(0);
                assertCursor(cursor, width, height, centered, x, y, phase - 2 * Math.PI, true);
            } finally { cursor.deconstruct(); view.deconstruct(); }
        });
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
    void cursorFollowsEditedOperationResults(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source = fixture(width, height, centered);
            ScaleOp scale = (ScaleOp) new ScaleOp().getDefaultInstance();
            OperatorSystem2D system = unary(source, scale);
            Function2DOverviewPanel view = new Function2DOverviewPanel(system.getFunction());
            ImageCursorPanel cursor = field(view, "largePlotPanel");
            try {
                enlarge(view, 0); enableCursor(cursor);
                double real = 3 + 3 / 128.0 + 4 / 256.0, imag = 4 + 3 / 64.0 - 4 / 128.0;
                assertCursor(cursor, width, height, centered, 3, 4, real, false);
                scale.setValue(0, 1.25); scale.setValue(1, -.5); system.freeMemory();
                view.setFunction(system.getFunction()); enableCursor(cursor);
                assertCursor(cursor, width, height, centered, 3, 4, 1.25 * real + .5 * imag, false);
            } finally { cursor.deconstruct(); view.deconstruct(); }
        });
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
    void cursorFollowsLargerImagesWithoutUsingThePreviousImageBuffer(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2DOverviewPanel view = new Function2DOverviewPanel(fixture(width, height, centered));
            ImageCursorPanel cursor = field(view, "largePlotPanel");
            try {
                enlarge(view, 0); enableCursor(cursor);
                double real = 3 + 3 / 128.0 + 4 / 256.0;
                assertCursor(cursor, width, height, centered, 3, 4, real, false);
                view.setFunction(fixture(width * 2, height * 2, centered)); enableCursor(cursor);
                assertCursor(cursor, width * 2, height * 2, centered, 3, 4, real, false);
            } finally { cursor.deconstruct(); view.deconstruct(); }
        });
    }

    private static void enlarge(Function2DOverviewPanel view, int channel) {
        DisplayCursorPanel[] plots = field(view, "plots");
        plots[channel].dispatchEvent(new MouseEvent(plots[channel], MouseEvent.MOUSE_PRESSED,
            System.currentTimeMillis(), 0, 10, 10, 1, false));
    }

    private static void enableCursor(ImageCursorPanel cursor) {
        JPanel toolbar = field(cursor, "toolBar");
        ((JButton) toolbar.getComponent(0)).doClick(0);
    }

    private static void assertCursor(ImageCursorPanel cursor, int width, int height, boolean centered,
            int x, int y, double expected, boolean phase) {
        int dimension = Math.max(width, height);
        cursor.setCursorLocationX(x + (centered ? dimension / 2 : 0));
        cursor.setCursorLocationY(y + (centered ? dimension / 2 : 0));
        Object graphic = field(cursor, "graphic");
        double[] displayed = field(graphic, "data");
        assertEquals(expected, displayed[index(x, y, width, height, centered)], 1e-8, "visible image sample");
        JLabel label = field(cursor, "label");
        NumberFormat format = Core.getDisplayOptions().getFormat();
        String value = format.format(phase ? expected / Math.PI : expected) + (phase ? "π)" : "");
        assertTrue(label.getText().endsWith(": (" + (double) x + ", " + (double) y + " ): " + value),
            "Cursor should report " + value + " for visible sample at " + x + "," + y + "; was " + label.getText());
    }

    private static Function2D fixture(int width, int height, boolean centered) {
        double[] real = new double[width * height], imag = new double[real.length];
        for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
            int x = x(col, width, centered), y = y(row, height, centered);
            real[row * width + col] = 3 + x / 128.0 + y / 256.0;
            imag[row * width + col] = 4 + x / 64.0 - y / 128.0;
        }
        return image(real, imag, width, height, centered);
    }

    private static JCheckBox checkBox(Container root, String text) {
        for (Component child : root.getComponents()) {
            if (child instanceof JCheckBox box && text.equals(box.getText())) return box;
            if (child instanceof Container container) {
                JCheckBox found = checkBox(container, text); if (found != null) return found;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object owner, String name) {
        for (Class<?> type = owner.getClass(); type != null; type = type.getSuperclass()) {
            try { Field field = type.getDeclaredField(name); field.setAccessible(true); return (T) field.get(owner); }
            catch (NoSuchFieldException missing) { /* Read the component's superclass too. */ }
            catch (IllegalAccessException failure) { throw new AssertionError(failure); }
        }
        throw new AssertionError("Missing field " + name);
    }
}
