package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.demo.FilteringDemo;
import signals.demo.FilteringDemo1D;
import signals.demo.FilteringDemo2D;
import signals.demo.FrequencyLocation2D;
import signals.functionterm.AnalyticFunctionTerm1D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.ParameterEditor;
import signals.gui.plot.ImageDisplayMath;

/** Ordinary frequency choices checked with signals whose mode gains are known. */
class FrequencyDemoWorkflowTest {
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

    static Stream<Arguments> filters() {
        return Stream.of(false, true).flatMap(twoD -> Stream.of(false, true).flatMap(centered ->
            Stream.of(false, true).map(gaussian -> Arguments.of(twoD, centered, gaussian))));
    }

    @ParameterizedTest(name = "filter 2D={0}, centered={1}, Gaussian={2}")
    @MethodSource("filters")
    void lowAndHighPassFollowWidthEdits(boolean twoD, boolean centered, boolean gaussian) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int width = twoD ? 32 : 128, height = twoD ? 16 : 1;
            double[][] samples = modes(width, height, centered, frequency -> 1);
            Function input = twoD ? FunctionFactory.createFunction2D(samples[0], samples[1], centered, "Modes", width, height)
                : FunctionFactory.createFunction1D(samples[0], samples[1], centered, "Modes");
            FilteringDemo demo = twoD ? new FilteringDemo2D(input) : new FilteringDemo1D(input);
            click(demo, gaussian ? "gaussianFilterButton" : "idealFilterButton");
            for (boolean high : new boolean[] {false, true}) {
                click(demo, high ? "hpFilterButton" : "lpFilterButton");
                for (double cutoff : new double[] {3, 6}) {
                    AnalyticFunctionTerm1D term = (AnalyticFunctionTerm1D) field(demo, "filterTerm");
                    JPanel params = (JPanel) field(demo, "filterParamPanel");
                    ((ParameterEditor) params.getComponent(2 * term.getWidthIndex() + 1)).setText(Double.toString(cutoff));
                    assertEquals(cutoff, term.getWidth(), "Visible width edit reaches the filter");
                    DoubleUnaryOperator gain = frequency -> {
                        double low = gaussian ? Math.exp(-Math.PI * frequency * frequency / (cutoff * cutoff))
                            : (frequency < cutoff ? 1 : 0);
                        return high ? 1 - low : low;
                    };
                    if (twoD) {
                        ImageDisplayMath image = (ImageDisplayMath) field(field(demo, "frequencyPanel"), "imageMath");
                        assertEquals(0, image.getMinValue(), 1e-9, "RGB frequency preview minimum");
                        assertEquals(1, image.getMaxValue(), 1e-9, "RGB frequency preview maximum");
                    }
                    Function result = (Function) field(demo, "reconstructed");
                    double[][] expected = modes(width, height, centered, gain);
                    assertAll("Filtered modes follow the selected shape, width and pass type",
                        () -> assertArrayEquals(expected[0], result.getReal(), 1e-9, "real"),
                        () -> assertArrayEquals(expected[1], result.getImaginary(), 1e-9, "imaginary"),
                        () -> assertEquals(centered, result.isZeroCentered(), "Output retains spatial origin"));
                }
            }
            assertArrayEquals(samples[0], input.getReal());
            assertArrayEquals(samples[1], input.getImaginary());
        });
    }

    static Stream<Arguments> bandFilters() {
        return Stream.of(false, true).flatMap(centered ->
            Stream.of(false, true).map(gaussian -> Arguments.of(centered, gaussian)));
    }

    @ParameterizedTest(name = "2D bands centered={0}, Gaussian={1}")
    @MethodSource("bandFilters")
    void bandPassAndRejectFollowNormalParameterEdits(boolean centered, boolean gaussian) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int width = 32, height = 16;
            double[][] samples = modes(width, height, centered, frequency -> 1);
            Function input = FunctionFactory.createFunction2D(samples[0], samples[1], centered,
                "Band filtering", width, height);
            FilteringDemo2D demo = new FilteringDemo2D(input);
            click(demo, gaussian ? "gaussianFilterButton" : "idealFilterButton");
            // Amplitude, center, separation and slit width; all ordinary values.
            for (double[] parameters : new double[][] {{1, 0, 4, 3}, {1, 1.25, 5, 2.5}, {0.7, 1.25, 5, 4}}) {
                double amplitude = parameters[0], center = parameters[1];
                double separation = parameters[2], slitWidth = parameters[3];
                double[][] passed = null;
                for (boolean reject : new boolean[] {false, true}) {
                    click(demo, reject ? "brFilterButton" : "bpFilterButton");
                    JPanel panel = (JPanel) field(demo, "filterParamPanel");
                    for (int parameter = 0; parameter < parameters.length; parameter++) {
                        ((ParameterEditor) panel.getComponent(2 * parameter + 1))
                            .setText(Double.toString(parameters[parameter]));
                    }
                    AnalyticFunctionTerm1D term = (AnalyticFunctionTerm1D) field(demo, "filterTerm");
                    for (int parameter = 0; parameter < parameters.length; parameter++) {
                        assertEquals(parameters[parameter], ((Number) term.getParameter(parameter)).doubleValue(),
                            "Control edit reaches the active filter");
                    }
                    DoubleUnaryOperator gain = frequency -> {
                        double leftDistance = frequency - (center - separation);
                        double rightDistance = frequency - (center + separation);
                        double pass = gaussian
                            ? amplitude * (Math.exp(-Math.PI * Math.pow(leftDistance / slitWidth, 2))
                                + Math.exp(-Math.PI * Math.pow(rightDistance / slitWidth, 2)))
                            : (Math.min(Math.abs(leftDistance), Math.abs(rightDistance)) <= slitWidth / 2 ? amplitude : 0);
                        return reject ? amplitude - pass : pass;
                    };
                    double[][] expected = modes(width, height, centered, gain);
                    Function result = (Function) field(demo, "reconstructed");
                    assertArrayEquals(expected[0], result.getReal(), 1e-9, "Band-filtered real image");
                    assertArrayEquals(expected[1], result.getImaginary(), 1e-9, "Band-filtered imaginary image");
                    assertEquals(centered, result.isZeroCentered());
                    assertEquals(width, ((Function2D) result).getDimensionX());
                    assertEquals(height, ((Function2D) result).getDimensionY());
                    if (!reject) {
                        passed = new double[][] {result.getReal().clone(), result.getImaginary().clone()};
                    } else {
                        for (int i = 0; i < width * height; i++) {
                            assertEquals(amplitude * samples[0][i], passed[0][i] + result.getReal()[i], 1e-9,
                                "Pass and reject reconstruct the scaled real input");
                            assertEquals(amplitude * samples[1][i], passed[1][i] + result.getImaginary()[i], 1e-9,
                                "Pass and reject reconstruct the scaled imaginary input");
                        }
                    }
                    // Existing scale controls display three decimals; sample checks above stay strict.
                    for (int channel = 0; channel < 2; channel++) {
                        click(demo, channel == 0 ? "realButton" : "imagButton");
                        ImageDisplayMath image = (ImageDisplayMath) field(field(demo, "spatialPanel"), "imageMath");
                        assertEquals(Arrays.stream(expected[channel]).min().orElseThrow(), image.getMinValue(), 0.0005,
                            "Displayed channel minimum follows the reconstruction");
                        assertEquals(Arrays.stream(expected[channel]).max().orElseThrow(), image.getMaxValue(), 0.0005,
                            "Displayed channel maximum follows the reconstruction");
                    }
                }
            }
            assertArrayEquals(samples[0], input.getReal(), "Source real samples remain unchanged");
            assertArrayEquals(samples[1], input.getImaginary(), "Source imaginary samples remain unchanged");
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void frequencyBandsAndDcAreIndependentlySelectable(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int width = 32, height = 16;
            double[][] input = modes(width, height, centered, f -> 1);
            FrequencyLocation2D demo = new FrequencyLocation2D(
                FunctionFactory.createFunction2D(input[0], input[1], centered, "Frequency bands", width, height));
            ((ParameterEditor) field(demo, "innerRadiusEditor")).setText("3");
            ((ParameterEditor) field(demo, "outerRadiusEditor")).setText("6");
            assertParts(demo, "lpfiltered", modes(width, height, centered, f -> f > 0 && f < 3 ? 1 : 0));
            assertParts(demo, "bandfiltered", modes(width, height, centered, f -> f > 3 && f < 6 ? 1 : 0));
            assertParts(demo, "hpfiltered", modes(width, height, centered, f -> f > 6 ? 1 : 0));
            click(demo, "dcCheckBox");
            assertParts(demo, "lpfiltered", modes(width, height, centered, f -> f < 3 ? 1 : 0));
            click(demo, "lfCheckBox");
            click(demo, "mfCheckBox");
            click(demo, "hfCheckBox");
            assertParts(demo, "lpfiltered", modes(width, height, centered, f -> f == 0 ? 1 : 0));
            click(demo, "imagButton");
            ImageDisplayMath image = (ImageDisplayMath) field(field(demo, "spatialDisplayPanel"), "imageMath");
            assertEquals(0, image.getMinValue(), 1e-9);
            assertEquals(0.2, image.getMaxValue(), 1e-9, "DC-only imaginary reconstruction is visible in blue");
            assertEquals(centered, field(field(demo, "spatialDisplayPanel"), "zeroCentered"));
            // Restoring low frequencies and excluding DC must recover the first band.
            click(demo, "lfCheckBox");
            click(demo, "dcCheckBox");
            assertParts(demo, "lpfiltered", modes(width, height, centered, f -> f > 0 && f < 3 ? 1 : 0));
        });
    }

    // Fourier modes are eigenfunctions of radial filtering. Their gains give an
    // oracle without calling a production transform or building a sampled mask.
    private static double[][] modes(int width, int height, boolean centered, DoubleUnaryOperator gain) {
        double[][] values = new double[2][width * height];
        int[] kx = {1, 4, 7}, ky = {0, height == 1 ? 0 : 2, 0};
        double[] realAmplitude = {1, 0.5, 0.3}, imaginaryAmplitude = {0.35, 0.2, -0.1};
        int ox = centered ? width / 2 : 0, oy = height == 1 ? 0 : (centered ? height / 2 - 1 : height - 1);
        for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
            int i = row * width + col;
            values[0][i] = 0.4 * gain.applyAsDouble(0);
            values[1][i] = 0.2 * gain.applyAsDouble(0);
            for (int j = 0; j < 3; j++) {
                double phase = 2 * Math.PI * (kx[j] * (col - ox) / (double) width + ky[j] * (oy - row) / (double) height);
                double scale = gain.applyAsDouble(Math.hypot(kx[j], ky[j]));
                values[0][i] += realAmplitude[j] * scale * Math.cos(phase);
                values[1][i] += imaginaryAmplitude[j] * scale * Math.sin(phase);
            }
        }
        return values;
    }

    private static void assertParts(Object demo, String name, double[][] expected) {
        double[][] actual = (double[][]) field(demo, name);
        assertArrayEquals(expected[0], actual[0], 1e-9, name + " real");
        assertArrayEquals(expected[1], actual[1], 1e-9, name + " imaginary");
    }

    private static void click(Object demo, String name) { ((AbstractButton) field(demo, name)).doClick(); }

    private static Object field(Object target, String name) {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name); field.setAccessible(true); return field.get(target);
            } catch (NoSuchFieldException e) { /* look in the shared demo base */ }
            catch (IllegalAccessException e) { throw new AssertionError(e); }
        }
        throw new AssertionError("Missing field " + name);
    }
}
