package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.swing.SwingUtilities;
import javax.swing.JRadioButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.demo.AnimatedPane;
import signals.demo.Convolve1D;
import signals.demo.Convolve2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.plot.ImageDisplayMath;
import signals.operation.ConvolveOp;
import signals.operation.CorrelateOp;

/** Demonstration frames, restart, and completion checked against direct spatial sums. */
class DemoCalculationWorkflowTest {
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;
    private Borders oldBorders;

    @BeforeEach void setUp() {
        Core c = Core.get();
        oldCreation = c.functionCreationOptions; oldDisplay = c.displayOptions;
        oldColors = c.colors; oldBorders = c.borders;
        c.functionCreationOptions = new FunctionCreationOptions();
        c.displayOptions = new DisplayOptions(); c.colors = new Colors(); c.borders = new Borders();
    }

    @AfterEach void tearDown() {
        Core c = Core.get();
        c.functionCreationOptions = oldCreation; c.displayOptions = oldDisplay;
        c.colors = oldColors; c.borders = oldBorders;
    }

    static Stream<Arguments> demonstrations() {
        return Stream.of(false, true).flatMap(twoD -> Stream.of(false, true).flatMap(centered ->
            Stream.of(false, true).flatMap(correlation -> Stream.of(
                Arguments.of(twoD, centered, correlation, true, false),
                Arguments.of(twoD, centered, correlation, true, true),
                Arguments.of(twoD, centered, correlation, false, false)))));
    }

    @ParameterizedTest(name = "2D={0}, centered={1}, correlation={2}, wrap={3}, normalize={4}")
    @MethodSource("demonstrations")
    void animationRestartAndFinalResultAgree(boolean twoD, boolean centered, boolean correlation,
            boolean wrap, boolean normalize) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int width = twoD ? 32 : 128, height = twoD ? 16 : 1;
            int count = width * height;
            int ox = centered ? width / 2 : 0;
            int oy = twoD ? (centered ? height / 2 - 1 : height - 1) : 0;
            double[][] source = new double[2][count], kernel = new double[2][count];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int i = row * width + col;
                    double x = col - width * 0.6, y = twoD ? row - height * 0.4 : 0;
                    source[0][i] = 0.2 + Math.exp(-(x * x + y * y) / 20);
                    source[1][i] = 0.15 * Math.sin(2 * Math.PI * col / width);
                    double kx = col - ox - 3, ky = twoD ? oy - row - 2 : 0;
                    kernel[0][i] = Math.exp(-(kx * kx + ky * ky) / 4);
                    // Normalization uses the ordinary real smoothing filter; the other
                    // cases also check the complex reference's conjugation.
                    kernel[1][i] = normalize ? 0 : 0.25 * kernel[0][i];
                }
            }
            Function input = sampled(source, twoD, centered, width, height, "Gaussian with offset");
            Function reference = sampled(kernel, twoD, centered, width, height, "Offset Gaussian filter");
            BinaryOperation op = correlation ? new CorrelateOp(null) : new ConvolveOp(null);
            AnimatedPane demo = twoD
                ? new Convolve2D(input, reference, !correlation, op, wrap, normalize)
                : new Convolve1D(input, reference, !correlation, op, 0, wrap, normalize);
            double[][] expected = directSum(source, kernel, width, height, ox, oy,
                correlation, wrap, normalize);
            try {
                // Ordinary Step, return to beginning, then run the complete animation.
                for (int i = 0; i < 7; i++) demo.step();
                double[] partial = samples(demo, "outputReal").clone();
                demo.stepBack();
                assertEquals(0, samples(demo, "outputReal")[4], "Going back removes the later sample");
                demo.step();
                assertArrayEquals(partial, samples(demo, "outputReal"), "Stepping forward restores the same frame");
                demo.toBegin();
                assertTrue(demo.firstStep());
                assertArrayEquals(new double[count], samples(demo, "outputReal"), "Restart clears output");
                int steps = 0;
                while (!demo.lastStep() && steps < count + 4) {
                    demo.step();
                    steps++;
                }
                assertTrue(demo.lastStep(), "Playback reaches a finite end");
                System.out.printf("2D=%s centered=%s correlation=%s wrap=%s normalize=%s steps=%d/%d maxErrors=%.6g,%.6g last=%.6g/%.6g%n",
                    twoD, centered, correlation, wrap, normalize, steps, count + 2,
                    maxError(expected[0], samples(demo, "outputReal")),
                    maxError(expected[1], samples(demo, "outputImag")),
                    samples(demo, "outputReal")[count - 1], expected[0][count - 1]);
                assertEquals(count + 2, steps, "Every position is visited, including the last pixel");
                assertAll("Every output sample must be computed at the correct position",
                    () -> assertArrayEquals(expected[0], samples(demo, "outputReal"), 1e-9, "animated real"),
                    () -> assertArrayEquals(expected[1], samples(demo, "outputImag"), 1e-9, "animated imaginary"));
                demo.toEnd();
                Function finalOutput = (Function) field(demo, "output");
                assertArrayEquals(expected[0], finalOutput.getReal(), 1e-9, "final real");
                assertArrayEquals(expected[1], finalOutput.getImaginary(), 1e-9, "final imaginary");
                assertArrayEquals(source[0], input.getReal(), "Input remains unchanged");
                assertArrayEquals(kernel[0], reference.getReal(), "Filter remains unchanged");
                assertArrayEquals(kernel[1], reference.getImaginary(), "Reference phase remains unchanged");
            } finally {
                demo.deconstruct();
            }
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void imaginaryPreviewOfRealImagesStaysZeroAndUsesTheirOrigin(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            double[] real = new double[32 * 16];
            Arrays.fill(real, 2);
            Function input = FunctionFactory.createFunction2D(real, new double[real.length], centered, "Constant", 32, 16);
            Convolve2D demo = new Convolve2D(input, input, true, new ConvolveOp(null), true, true);
            try {
                ((JRadioButton) field(demo, "imagButton")).doClick();
                Object display = field(demo, "display");
                ImageDisplayMath math = (ImageDisplayMath) field(display, "imageMath");
                assertEquals(-0.01, math.getMinValue(), 1e-9);
                assertEquals(0.01, math.getMaxValue(), 1e-9, "Real filter must not appear in the imaginary preview");
                assertEquals(centered, field(display, "zeroCentered"));
                assertEquals(centered, field(field(demo, "outputDisplay"), "zeroCentered"));
            } finally { demo.deconstruct(); }
        });
    }

    private static Function sampled(double[][] data, boolean twoD, boolean centered,
            int width, int height, String name) {
        return twoD ? FunctionFactory.createFunction2D(data[0], data[1], centered, name, width, height)
            : FunctionFactory.createFunction1D(data[0], data[1], centered, name);
    }

    // Independent definition in stored image coordinates. No production FFT,
    // reverse, translation, normalization, or demo helper is used by this oracle.
    private static double[][] directSum(double[][] a, double[][] b, int width, int height,
            int ox, int oy, boolean correlation, boolean wrap, boolean normalize) {
        double[][] result = new double[2][width * height];
        double scale = normalize ? 1 / Arrays.stream(b[0]).sum() : 1;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int out = row * width + col;
                for (int ar = 0; ar < height; ar++) {
                    int br = oy + (correlation ? ar - row : row - ar);
                    if (wrap) br = Math.floorMod(br, height);
                    else if (br < 0 || br >= height) continue;
                    for (int ac = 0; ac < width; ac++) {
                        int bc = ox + (correlation ? ac - col : col - ac);
                        if (wrap) bc = Math.floorMod(bc, width);
                        else if (bc < 0 || bc >= width) continue;
                        int ai = ar * width + ac, bi = br * width + bc;
                        double realB = b[0][bi] * scale;
                        double imagB = b[1][bi] * (correlation ? -1 : 1);
                        result[0][out] += a[0][ai] * realB - a[1][ai] * imagB;
                        result[1][out] += a[1][ai] * realB + a[0][ai] * imagB;
                    }
                }
            }
        }
        return result;
    }

    private static double maxError(double[] expected, double[] actual) {
        double error = 0;
        for (int i = 0; i < expected.length; i++) error = Math.max(error, Math.abs(expected[i] - actual[i]));
        return error;
    }

    private static Object field(Object target, String name) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    private static double[] samples(Object target, String name) { return (double[]) field(target, name); }
}
