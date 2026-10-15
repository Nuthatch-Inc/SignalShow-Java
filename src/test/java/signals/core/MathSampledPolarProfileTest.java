package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import javax.swing.SwingUtilities;
import signals.gui.Borders;
import signals.gui.ModeSelector;
import signals.gui.ParameterEditor;
import signals.gui.datagenerator.*;
import signals.gui.plot.FunctionTerm1DThumbnailGraphic;
import signals.gui.plot.SeparableFunctionTerm2DPreviewPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.DataFunctionTerm1D;
import signals.functionterm.PolarFunctionTerm2D;
import signals.operation.ScaleOp;
import signals.operation.SquaredMagnitudeOp;

class MathSampledPolarProfileTest extends MathAuditSupport {
    private static final double[] REAL = {1, .2, -.6, 1.8, .4, -1.2, .8, 2};
    private static final double[] IMAG = {-.3, 1.1, .7, -.8, .2, .9, -1.3, .5};

    static Stream<Arguments> cases() {
        return Stream.of(false, true).flatMap(angular -> Stream.of(Constants.CROP_MODE, Constants.SCALE_AND_PAD_MODE)
                .flatMap(mode -> Stream.of(
                        Arguments.of(128, 64, true, angular, mode),
                        Arguments.of(128, 64, false, angular, mode),
                        Arguments.of(64, 128, true, angular, mode),
                        Arguments.of(64, 128, false, angular, mode))));
    }

    @ParameterizedTest
    @MethodSource("cases")
    void sampledProfilesUsePhysicalCoordinatesThroughComplexChains(
            int width, int height, boolean centered, boolean angular, int mode) {
        DataFunctionTerm1D real = data(REAL, mode), imag = data(IMAG, mode);
        RealImagFunction2D source = new RealImagFunction2D(part(real, angular), part(imag, angular),
                "sampled polar profile", width, height, centered);
        ScaleOp scale = new ScaleOp(new ParameterBlock());
        scale.setValue(0, .5); scale.setValue(1, .8);
        OperatorSystem2D scaled = unary(source, scale);
        OperatorSystem2D intensity = unary(scaled, new SquaredMagnitudeOp(new ParameterBlock()));
        int referenceDimension = angular ? (centered ? 360 : 90) : Math.max(width, height) / 2;
        boolean referenceCentered = angular && centered;

        for (int edit = 0; edit < 2; edit++) {
            int center = angular ? 40 + 5 * edit : 8 + 3 * edit;
            int halfWidth = angular ? 25 - 5 * edit : 12 - 2 * edit;
            double amplitude = edit == 0 ? 1.4 : -.7;
            for (DataFunctionTerm1D term : List.of(real, imag)) {
                term.setCenter(center); term.setWidth(halfWidth); term.setAmplitude(amplitude);
            }
            source.setPartA(source.getPartA()); source.setPartB(source.getPartB());
            double[] er = new double[width * height], ei = new double[er.length];
            double[] sr = new double[er.length], si = new double[er.length], power = new double[er.length];
            for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
                int k = row * width + col, px = x(col, width, centered), py = y(row, height, centered);
                double coordinate = angular ? (px == 0 && py == 0 ? 90 : Math.toDegrees(Math.atan2(py, px)))
                        : Math.hypot(px, py);
                er[k] = expected(REAL, coordinate, mode, center, halfWidth, amplitude,
                        referenceDimension, referenceCentered);
                ei[k] = expected(IMAG, coordinate, mode, center, halfWidth, amplitude,
                        referenceDimension, referenceCentered);
                sr[k] = .5 * er[k] - .8 * ei[k]; si[k] = .8 * er[k] + .5 * ei[k];
                power[k] = sr[k] * sr[k] + si[k] * si[k];
            }
            assertImage(source, er, ei, width, height, centered);
            assertImage(scaled.getFunction(), sr, si, width, height, centered);
            assertImage(intensity.getFunction(), power, new double[er.length], width, height, centered);
            if (!angular) {
                // Same radius reached through different triangular-slice entries must agree.
                assertEquals(source.getReal()[index(5, 0, width, height, centered)],
                        source.getReal()[index(4, 3, width, height, centered)], 1e-12);
            }
            // The integer coordinates in the existing 1D preview retain exactly this mapping.
            double[] preview = real.create(referenceDimension, referenceCentered);
            for (int i = 0; i < preview.length; i++) {
                assertEquals(expected(REAL, x(i, referenceDimension, referenceCentered), mode, center,
                        halfWidth, amplitude, referenceDimension, referenceCentered), preview[i], 1e-10);
            }
            assertArrayEquals(REAL, real.getOriginalData()); assertArrayEquals(IMAG, imag.getOriginalData());
        }
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,false", "true,true", "false,true"})
    void editorModesAndCombinedPreviewUseTheSameProfileDomain(boolean centered, boolean angular) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Core core = Core.get(); Borders previousBorders = core.borders;
            DataGeneratorCollections previousGenerators = core.dataGeneratorCollections;
            core.borders = new Borders(); core.dataGeneratorCollections = new DataGeneratorCollections();
            try {
                DataFunctionTerm1D term = data(REAL, Constants.CROP_MODE);
                PolarFunctionTerm2D polar = (PolarFunctionTerm2D) part(term, angular).getTermsList().get(0);
                CreateFunctionTerm2DPolarPanel panel = new CreateFunctionTerm2DPolarPanel(new GUIEventBroadcaster(), true);
                panel.setIndices(128, 64, centered); panel.setListExisting(polar);
                List<CreateAnalyticSubTerm1DPanel> subparts = new ArrayList<>();
                collect(panel, CreateAnalyticSubTerm1DPanel.class, subparts);
                EditAnalyticFunctionTerm1DPanel editor = (EditAnalyticFunctionTerm1DPanel)
                        subparts.get(angular ? 1 : 0).getEditor();
                List<ParameterEditor> fields = new ArrayList<>(); collect(editor, ParameterEditor.class, fields);
                int center = angular ? 40 : 8, halfWidth = angular ? 25 : 12;
                fields.get(0).setText("1.25"); fields.get(1).setText(Integer.toString(center));
                fields.get(2).setText(Integer.toString(2 * halfWidth));
                assertEquals(1.25, term.getAmplitude()); assertEquals(center, term.getCenter());
                assertEquals(halfWidth, term.getWidth());
                List<ModeSelector> selectors = new ArrayList<>(); collect(editor, ModeSelector.class, selectors);
                int referenceDimension = angular ? (centered ? 360 : 90) : 64;
                boolean referenceCentered = angular && centered;
                for (int mode : new int[]{Constants.SCALE_AND_PAD_MODE, Constants.CROP_MODE}) {
                    panel.setSelectedIndex(angular ? 1 : 0);
                    selectors.get(0).setSelectedIndex(mode);
                    panel.setSelectedIndex(2);
                    List<SeparableFunctionTerm2DPreviewPanel> previews = new ArrayList<>();
                    collect(panel, SeparableFunctionTerm2DPreviewPanel.class, previews);
                    FunctionTerm1DThumbnailGraphic graphic = (FunctionTerm1DThumbnailGraphic)
                            readField(previews.get(0), angular ? "part2" : "part1");
                    double[] indices = (double[]) readField(graphic, "indices");
                    graphic.createData();
                    double[] values = (double[]) readField(graphic, "data");
                    assertEquals(referenceDimension, indices.length, "combined preview coordinate count");
                    for (int i = 0; i < indices.length; i++) {
                        assertEquals(x(i, referenceDimension, referenceCentered), indices[i]);
                        assertEquals(expected(REAL, indices[i], mode, center, halfWidth, 1.25,
                                referenceDimension, referenceCentered), values[i], 1e-10);
                    }
                    double[] image = ((PolarFunctionTerm2D) panel.getDataGenerator()).create(128, 64, centered);
                    for (int row = 0; row < 64; row++) for (int col = 0; col < 128; col++) {
                        int px = x(col, 128, centered), py = y(row, 64, centered);
                        double coordinate = angular ? (px == 0 && py == 0 ? 90 : Math.toDegrees(Math.atan2(py, px)))
                                : Math.hypot(px, py);
                        assertEquals(expected(REAL, coordinate, mode, center, halfWidth, 1.25,
                                referenceDimension, referenceCentered), image[row * 128 + col], 1e-10);
                    }
                }
                CreateFunctionTerm2DPolarPanel reopened = new CreateFunctionTerm2DPolarPanel(new GUIEventBroadcaster(), true);
                reopened.setIndices(128, 64, centered); reopened.setListExisting(panel.getDataGenerator());
                assertArrayEquals(((PolarFunctionTerm2D) panel.getDataGenerator()).create(128, 64, centered),
                        ((PolarFunctionTerm2D) reopened.getDataGenerator()).create(128, 64, centered));
            } finally {
                core.borders = previousBorders; core.dataGeneratorCollections = previousGenerators;
            }
        });
    }

    private static Object readField(Object instance, String name) {
        for (Class<?> type = instance.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name); field.setAccessible(true); return field.get(instance);
            } catch (NoSuchFieldException ignored) {
                // Inspect the inherited plot data without replacing its production evaluator.
            } catch (IllegalAccessException e) { throw new AssertionError(e); }
        }
        throw new AssertionError("Missing field " + name);
    }

    private static <T extends Component> void collect(Container root, Class<T> type, List<T> matches) {
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) matches.add(type.cast(child));
            else if (child instanceof Container nested) collect(nested, type, matches);
        }
    }

    private static double expected(double[] samples, double coordinate, int mode, int center,
            int halfWidth, double amplitude, int referenceDimension, boolean referenceCentered) {
        double start, end;
        if (mode == Constants.CROP_MODE) {
            if (coordinate < center - halfWidth || coordinate > center + halfWidth) return 0;
            start = referenceCentered ? -referenceDimension / 2 : 0;
            end = start + referenceDimension - 1;
        } else {
            start = center - halfWidth;
            end = start + 2 * halfWidth - 1;
        }
        if (coordinate < start || coordinate > end) return 0;
        // Piecewise linear interpolation at physical coordinates, zero outside its domain.
        double step = (end - start) / (samples.length - 1);
        for (int i = 0; i < samples.length - 1; i++) {
            double left = start + i * step, right = start + (i + 1) * step;
            if (coordinate <= right) {
                double fraction = (coordinate - left) / (right - left);
                return amplitude * ((1 - fraction) * samples[i] + fraction * samples[i + 1]);
            }
        }
        return amplitude * samples[samples.length - 1];
    }

    private static DataFunctionTerm1D data(double[] samples, int mode) {
        ParameterBlock pb = new DataFunctionTerm1D().getDefaultParamBlock(); pb.addSource(samples.clone());
        DataFunctionTerm1D term = new DataFunctionTerm1D(pb); term.setMode(mode); return term;
    }

    private static FunctionPart2D part(DataFunctionTerm1D data, boolean angular) {
        ConstantFunctionTerm1D one = (ConstantFunctionTerm1D) new ConstantFunctionTerm1D().getDefaultInstance();
        PolarFunctionTerm2D polar = new PolarFunctionTerm2D(new ParameterBlock()
                .addSource(angular ? one : data).addSource(angular ? data : one));
        return new FunctionPart2D(new ArrayList<>(List.of(polar)), CombineTermsRule.getDefaultRule());
    }
}
