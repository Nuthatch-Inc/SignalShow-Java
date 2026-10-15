package signals.gui.datagenerator;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.AbstractButton;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import signals.core.*;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.HorizontalThumbnailList;
import signals.gui.VerticalThumbnailList;
import javax.swing.JList;
import signals.gui.TextListItem;
import signals.gui.ParameterEditor;
import signals.operation.SquaredMagnitudeOp;
import signals.operation.TimesOp;
import signals.operation.PlusOp;
import signals.operation.MinusOp;
import signals.operation.DivideOp;
import signals.operation.RealOp;
import signals.operation.ConjugateOp;
import signals.operation.ThresholdOp;
import signals.operation.TranslateOp2D;
import signals.operation.DerivativeOp2D;
import signals.operation.FourierTransformOp;
import signals.operation.HologramEncoderOp;
import signals.operation.PhaseDetourHologramOp;
import signals.gui.plot.ImageDisplayPanel;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import signals.functionterm.*;
import java.awt.image.renderable.ParameterBlock;

/** Actual Swing actions and model publication, with only the desktop library supplied. */
class OperationEditingWorkflowTest {
    private final Map<Field, Object> previousServices = new LinkedHashMap<>();
    private static VerticalThumbnailList library;

    @BeforeEach void setUp() throws Exception {
        service("functionCreationOptions", new FunctionCreationOptions());
        service("displayOptions", new DisplayOptions());
        service("colors", new Colors());
        service("borders", new Borders());
        service("dataGeneratorCollections", new DataGeneratorCollections());
    }

    private void service(String name, Object value) throws Exception {
        Field field = Core.class.getDeclaredField(name);
        field.setAccessible(true);
        previousServices.put(field, field.get(Core.get()));
        field.set(Core.get(), value);
    }

    @AfterEach void tearDown() throws Exception {
        for (Map.Entry<Field, Object> entry : previousServices.entrySet())
            entry.getKey().set(Core.get(), entry.getValue());
        library = null;
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void clearingAndRebuildingKeepsTheLibraryChainAndDownstreamResult(boolean image) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function a = source(image, 1), b = source(image, 2);
            Operation multiply = (Operation) new TimesOp().getDefaultInstance();
            OperatorSystem chain = chain(image, List.of(a, b), List.of(multiply),
                    FUNCTION, multiply.getPriority(), FUNCTION);
            Operation intensity = (Operation) new SquaredMagnitudeOp().getDefaultInstance();
            OperatorSystem downstream = chain(image, List.of(chain), List.of(intensity),
                    intensity.getPriority(), FUNCTION);
            prepareLibrary(a, b, chain, downstream);
            assertNotNull(downstream.getFunction()); // populate both caches before editing
            CreateOperationSystemPanel panel = editor(image, chain);
            try {
                button(panel, "Clear").doClick();
                assertNull(chain.getFunction(), "Clear must publish to the library's existing chain");
                assertNull(downstream.getFunction(), "A cleared input must invalidate linked results");
                assertFalse(panel.saveButton.isEnabled());

                button(panel.calculator, "New Term").doClick();
                pick(panel, b);
                assertChannels(chain.getFunction(), b.getReal(), b.getImaginary());
                assertSame(chain, panel.system, "Clear must retain the edited system's identity");
                button(panel.calculator, "New Binary Operation").doClick();
                find(panel.calculator, HorizontalThumbnailList.class).setSelected(3);
                pick(panel, a);
                double[] real = new double[a.getReal().length], imag = new double[real.length];
                double[] power = new double[real.length];
                for (int i = 0; i < real.length; i++) {
                    real[i] = a.getReal()[i] + b.getReal()[i];
                    imag[i] = a.getImaginary()[i] + b.getImaginary()[i];
                    power[i] = real[i] * real[i] + imag[i] * imag[i];
                }
                assertChannels(chain.getFunction(), real, imag);
                assertChannels(panel.getFunction(), real, imag);
                assertChannels(downstream.getFunction(), power, new double[power.length]);
                assertTrue(panel.saveButton.isEnabled());
                assertSame(chain, library.getItemAt(2));
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(image, chain);
            try {
                assertEquals(List.of(b, a), reopened.calculator.getFunctionList());
                assertArrayEquals(new int[] {FUNCTION, BINARY_OP_TIER_1, FUNCTION},
                        reopened.calculator.getCodeList());
                assertChannels(reopened.getFunction(), chain.getFunction().getReal(), chain.getFunction().getImaginary());
            } finally { reopened.deconstruct(); }
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void groupingAnInnerProductKeepsTheSurroundingArithmetic(boolean image) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function a = source(image, 1), b = source(image, 2), c = source(image, 3), d = source(image, 4);
            Operation plus = (Operation) new PlusOp().getDefaultInstance();
            Operation times = (Operation) new TimesOp().getDefaultInstance();
            OperatorSystem chain = chain(image, List.of(a, b, c, d), List.of(plus, times, times),
                    FUNCTION, plus.getPriority(), FUNCTION, times.getPriority(), FUNCTION, times.getPriority(), FUNCTION);
            prepareLibrary(a, b, c, d, chain);
            double[] real = new double[512], imag = new double[512];
            for (int i = 0; i < real.length; i++) {
                double bcReal = b.getReal()[i] * c.getReal()[i] - b.getImaginary()[i] * c.getImaginary()[i];
                double bcImag = b.getReal()[i] * c.getImaginary()[i] + b.getImaginary()[i] * c.getReal()[i];
                real[i] = a.getReal()[i] + bcReal * d.getReal()[i] - bcImag * d.getImaginary()[i];
                imag[i] = a.getImaginary()[i] + bcReal * d.getImaginary()[i] + bcImag * d.getReal()[i];
            }
            assertChannels(chain.getFunction(), real, imag); // A + B*C*D
            CreateOperationSystemPanel panel = editor(image, chain);
            try {
                HorizontalThumbnailList list = find(panel.calculator, HorizontalThumbnailList.class);
                list.setSelected(2); // insert '(' after Plus
                button(panel.calculator, "Open Parenthesis").doClick();
                list.setSelected(6); // insert ')' after C
                button(panel.calculator, "Closed Parenthesis").doClick();
                assertArrayEquals(new int[] {FUNCTION, BINARY_OP_TIER_1, LEFT_PAREN,
                        FUNCTION, BINARY_OP_TIER_2, FUNCTION, RIGHT_PAREN, BINARY_OP_TIER_2, FUNCTION},
                        panel.calculator.getCodeList());
                assertChannels(chain.getFunction(), real, imag); // A + (B*C)*D is the same value
                assertChannels(panel.getFunction(), real, imag);
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(image, chain);
            try { assertChannels(reopened.getFunction(), real, imag); }
            finally { reopened.deconstruct(); }
        });
    }

    @ParameterizedTest @CsvSource({"false,+", "true,+", "false,-", "true,-", "false,*", "true,*", "false,/", "true,/"})
    void selectingBinaryOperationsAndReplacingInputsPublishesTheChosenResult(boolean image, char operator) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function a = source(image, 1), b = source(image, 2), c = source(image, 3);
            OperatorSystem chain = image ? new OperatorSystem2D() : new OperatorSystem1D();
            prepareLibrary(a, b, c, chain);
            CreateOperationSystemPanel panel = editor(image, chain);
            try {
                button(panel.calculator, "New Binary Operation").doClick(); // insert the default operands
                HorizontalThumbnailList list = find(panel.calculator, HorizontalThumbnailList.class);
                list.setSelected(1); pick(panel, a);
                list.setSelected(3); pick(panel, b);
                list.setSelected(2);
                selectOperation(panel, switch (operator) {
                    case '+' -> PlusOp.class; case '-' -> MinusOp.class;
                    case '*' -> TimesOp.class; case '/' -> DivideOp.class;
                    default -> throw new AssertionError(operator);
                });
                assertBinary(chain.getFunction(), a, b, operator);
                list.setSelected(3); pick(panel, c);
                assertBinary(chain.getFunction(), a, c, operator);
                assertBinary(panel.getFunction(), a, c, operator);
                assertTrue(panel.saveButton.isEnabled());
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(image, chain);
            try { assertBinary(reopened.getFunction(), a, c, operator); }
            finally { reopened.deconstruct(); }
        });
    }

    @ParameterizedTest @CsvSource({"false,real", "true,real", "false,conjugate", "true,conjugate", "false,intensity", "true,intensity"})
    void nestedThresholdSelectionAndTypingUpdateSavedAndLinkedResults(boolean image, String outer) throws Exception {
        thresholdEditingWorkflow(image, outer, false);
    }

    @ParameterizedTest @CsvSource({"false,real", "true,real", "false,conjugate", "true,conjugate", "false,intensity", "true,intensity"})
    void accessibleThresholdEditsUpdateSavedAndLinkedResults(boolean image, String outer) throws Exception {
        thresholdEditingWorkflow(image, outer, true);
    }

    private void thresholdEditingWorkflow(boolean image, String outer, boolean accessible) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function source = source(image, 1);
            double[] originalReal = source.getReal().clone(), originalImag = source.getImaginary().clone();
            OperatorSystem chain = image ? new OperatorSystem2D() : new OperatorSystem1D();
            Operation intensity = (Operation) new SquaredMagnitudeOp().getDefaultInstance();
            OperatorSystem downstream = chain(image, List.of(chain), List.of(intensity), intensity.getPriority(), FUNCTION);
            prepareLibrary(source, chain, downstream);
            CreateOperationSystemPanel panel = editor(image, chain);
            try {
                button(panel.calculator, "New Unary Operation").doClick();
                selectOperation(panel, switch (outer) {
                    case "real" -> RealOp.class; case "conjugate" -> ConjugateOp.class;
                    case "intensity" -> SquaredMagnitudeOp.class; default -> throw new AssertionError(outer);
                });
                HorizontalThumbnailList list = find(panel.calculator, HorizontalThumbnailList.class);
                list.setSelected(2); // after the outer opening parenthesis
                button(panel.calculator, "New Unary Operation").doClick();
                selectOperation(panel, ThresholdOp.class);
                list.setSelected(3); button(panel.calculator, "Open Parenthesis").doClick();
                list.setSelected(5); button(panel.calculator, "Closed Parenthesis").doClick();
                list.setSelected(3); // Threshold inside the outer operation
                List<ParameterEditor> fields = new ArrayList<>();
                collect(panel.operationPanel.getEditor(), ParameterEditor.class, fields);
                assertEquals(3, fields.size());
                editParameter(fields.get(0), "3", accessible);
                editParameter(fields.get(1), "-1", accessible);
                editParameter(fields.get(2), "0.75", accessible);
                assertThresholdChain(chain, downstream, source, outer, 0.75);
                // Keep the populated downstream cache while changing an ordinary parameter.
                editParameter(fields.get(2), "1.25", accessible);
                assertThresholdChain(chain, downstream, source, outer, 1.25);
                ThresholdOp edited = assertInstanceOf(ThresholdOp.class, chain.getOpList().get(1));
                assertEquals(3, edited.getValue(0)); assertEquals(-1, edited.getValue(1)); assertEquals(1.25, edited.getValue(2));
                assertArrayEquals(originalReal, source.getReal()); assertArrayEquals(originalImag, source.getImaginary());
                if (accessible) for (int i = 0; i < fields.size(); i++) {
                    ParameterEditor field = fields.get(i);
                    javax.swing.JTextField text = ((javax.swing.JSpinner.DefaultEditor) field.getEditor()).getTextField();
                    assertEquals(edited.getParamName(i), field.getAccessibleContext().getAccessibleName());
                    assertEquals(edited.getParamName(i), text.getAccessibleContext().getAccessibleName());
                }
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(image, chain);
            try {
                assertThresholdChain(chain, downstream, source, outer, 1.25);
                assertChannels(reopened.getFunction(), chain.getFunction().getReal(), chain.getFunction().getImaginary());
                assertArrayEquals(new int[] {UNARY_OP_TIER_2, LEFT_PAREN, UNARY_OP_TIER_2, LEFT_PAREN,
                        FUNCTION, RIGHT_PAREN, RIGHT_PAREN}, reopened.calculator.getCodeList());
            } finally { reopened.deconstruct(); }
        });
    }

    @ParameterizedTest @CsvSource({"false,real", "true,real", "false,conjugate", "true,conjugate", "false,intensity", "true,intensity"})
    void prependingAUnaryOperationToAnExistingChainNestsItAroundTheNextOperand(boolean image, String outer) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function source = source(image, 1);
            ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
            threshold.setValue(0, 3); threshold.setValue(1, -1); threshold.setValue(2, 0.75);
            OperatorSystem chain = chain(image, List.of(source), List.of(threshold),
                    threshold.getPriority(), LEFT_PAREN, FUNCTION, RIGHT_PAREN);
            Operation intensity = (Operation) new SquaredMagnitudeOp().getDefaultInstance();
            OperatorSystem downstream = chain(image, List.of(chain), List.of(intensity), intensity.getPriority(), FUNCTION);
            prepareLibrary(source, chain, downstream);
            assertNotNull(downstream.getFunction());
            CreateOperationSystemPanel panel = editor(image, chain);
            try {
                find(panel.calculator, HorizontalThumbnailList.class).setSelected(0);
                button(panel.calculator, "New Unary Operation").doClick();
                selectOperation(panel, switch (outer) {
                    case "real" -> RealOp.class; case "conjugate" -> ConjugateOp.class;
                    case "intensity" -> SquaredMagnitudeOp.class; default -> throw new AssertionError(outer);
                });
                assertArrayEquals(new int[] {UNARY_OP_TIER_2, UNARY_OP_TIER_2, LEFT_PAREN,
                        FUNCTION, RIGHT_PAREN}, panel.calculator.getCodeList());
                assertThresholdChain(chain, downstream, source, outer, 0.75);
                assertTrue(panel.saveButton.isEnabled());
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(image, chain);
            try { assertThresholdChain(chain, downstream, source, outer, 0.75); }
            finally { reopened.deconstruct(); }
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void gaussianTranslationControlsUpdateWrappingAndLinkedIntensity(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source = gaussianSource(centered);
            TranslateOp2D op = (TranslateOp2D) new TranslateOp2D().getDefaultInstance();
            OperatorSystem chain = chain(true, List.of(source), List.of(op), op.getPriority(), FUNCTION);
            OperatorSystem power = intensityOf(chain);
            prepareLibrary(source, chain, power);
            double[][] expectedSource = gaussianSamples();
            assertChannels(source, expectedSource[0], expectedSource[1]);
            CreateOperationSystemPanel panel = editor(true, chain);
            try {
                find(panel.calculator, HorizontalThumbnailList.class).setSelected(1);
                List<ParameterEditor> fields = new ArrayList<>();
                collect(panel.operationPanel.getEditor(), ParameterEditor.class, fields);
                assertEquals(2, fields.size());
                fields.get(0).setText("3"); fields.get(1).setText("-2");
                JCheckBox wrap = option(panel, JCheckBox.class, "wrap around");
                for (boolean wrapping : new boolean[]{false, true, false}) {
                    if (wrap.isSelected() != wrapping) wrap.doClick();
                    assertEquals(wrapping, op.isWrapAround());
                    double[][] expected = new double[2][32*16];
                    for (int row=0;row<16;row++) for (int col=0;col<32;col++) {
                        int sx=col-3, sy=row-2;
                        if (wrapping || sx>=0 && sx<32 && sy>=0 && sy<16) {
                            int index=Math.floorMod(sy,16)*32+Math.floorMod(sx,32);
                            for(int channel=0;channel<2;channel++) expected[channel][row*32+col]=expectedSource[channel][index];
                        }
                    }
                    assertWithIntensity(chain,power,expected);
                }
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(true, chain);
            try {
                find(reopened.calculator, HorizontalThumbnailList.class).setSelected(1);
                assertFalse(option(reopened,JCheckBox.class,"wrap around").isSelected());
                assertEquals(3,op.getValue(0)); assertEquals(-2,op.getValue(1));
                assertChannels(reopened.getFunction(),chain.getFunction().getReal(),chain.getFunction().getImaginary());
            } finally { reopened.deconstruct(); }
            assertChannels(source,expectedSource[0],expectedSource[1]);
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void gaussianDerivativeDirectionControlsUpdateLinkedIntensity(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source=gaussianSource(centered);
            DerivativeOp2D op=(DerivativeOp2D)new DerivativeOp2D().getDefaultInstance();
            OperatorSystem chain=chain(true,List.of(source),List.of(op),op.getPriority(),FUNCTION);
            OperatorSystem power=intensityOf(chain);
            prepareLibrary(source,chain,power);
            double[][] input=gaussianSamples(),last=null;
            CreateOperationSystemPanel panel=editor(true,chain);
            try {
                find(panel.calculator,HorizontalThumbnailList.class).setSelected(1);
                for(boolean xDirection:new boolean[]{false,true,false}) {
                    option(panel,JRadioButton.class,xDirection?"X-Direction":"Y-Direction").doClick();
                    assertEquals(xDirection,op.isXDirection());
                    double[][] expected=new double[2][32*16];
                    for(int row=0;row<16;row++) for(int col=0;col<32;col++) {
                        if(xDirection?col>0:row<15) {
                            int index=row*32+col,previous=xDirection?index-1:index+32;
                            for(int c=0;c<2;c++)expected[c][index]=input[c][index]-input[c][previous];
                        }
                    }
                    assertWithIntensity(chain,power,expected); last=expected;
                }
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened=editor(true,chain);
            try {
                find(reopened.calculator,HorizontalThumbnailList.class).setSelected(1);
                assertTrue(option(reopened,JRadioButton.class,"Y-Direction").isSelected());
                assertWithIntensity(chain,power,last);
            } finally { reopened.deconstruct(); }
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void gaussianFftOptionControlsUpdateDirectionNormalizationAndLinkedIntensity(boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source=gaussianSource(centered);
            FourierTransformOp op=(FourierTransformOp)new FourierTransformOp().getDefaultInstance();
            OperatorSystem chain=chain(true,List.of(source),List.of(op),op.getPriority(),FUNCTION);
            OperatorSystem power=intensityOf(chain);
            prepareLibrary(source,chain,power);
            double[][] input=gaussianSamples();
            CreateOperationSystemPanel panel=editor(true,chain);
            try {
                find(panel.calculator,HorizontalThumbnailList.class).setSelected(1);
                for(boolean inverse:new boolean[]{false,true}) {
                    JCheckBox checkbox=option(panel,JCheckBox.class,"inverse");
                    if(checkbox.isSelected()!=inverse)checkbox.doClick();
                    assertEquals(inverse,op.isInverse());
                    double[][] unscaled=directGaussianDft(input,centered,inverse);
                    String[] labels={"No Normalization","Divide by N","Divide by \u221aN"};
                    double[] divisors={1,512,Math.sqrt(512)};
                    for(int n=0;n<labels.length;n++) {
                        option(panel,JRadioButton.class,labels[n]).doClick();
                        double[][] expected={unscaled[0].clone(),unscaled[1].clone()};
                        for(double[] channel:expected) for(int i=0;i<channel.length;i++)channel[i]/=divisors[n];
                        assertWithIntensity(chain,power,expected);
                    }
                }
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened=editor(true,chain);
            try {
                find(reopened.calculator,HorizontalThumbnailList.class).setSelected(1);
                assertTrue(option(reopened,JCheckBox.class,"inverse").isSelected());
                assertTrue(option(reopened,JRadioButton.class,"Divide by \u221aN").isSelected());
                assertChannels(reopened.getFunction(),chain.getFunction().getReal(),chain.getFunction().getImaginary());
            } finally { reopened.deconstruct(); }
            assertChannels(source,input[0],input[1]);
        });
    }

    @ParameterizedTest @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    void hologramOptionEditsUpdatePreviewPublicationAndCachedIntensity(boolean phaseDetour, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source = gaussianSource(centered);
            double[][] original = {source.getReal().clone(), source.getImaginary().clone()};
            UnaryOperation op = hologram(phaseDetour, false, false, false);
            OperatorSystem chain = chain(true, List.of(source), List.of(op), op.getPriority(), FUNCTION);
            OperatorSystem intensity = intensityOf(chain);
            prepareLibrary(source, chain, intensity);
            double[] previous = chain.getFunction().getReal().clone();
            intensity.getFunction().getReal();
            // Each row is diffusion, wide apertures, random phase. Revisit ordinary settings.
            boolean[][] settings = phaseDetour
                ? new boolean[][]{{false,false,true},{true,false,true},{true,true,true},{true,true,false},{false,false,false},{true,true,true}}
                : new boolean[][]{{false,true,false},{true,true,false},{true,false,false},{false,false,false},{true,true,false}};
            CreateOperationSystemPanel panel = editor(true, chain);
            Function2D expected = null;
            try {
                find(panel.calculator, HorizontalThumbnailList.class).setSelected(1);
                for (boolean[] state : settings) {
                    setOption(panel, "Error Diffusion", state[0]);
                    setOption(panel, "3-Pixel-Wide Apertures", state[1]);
                    if (phaseDetour) setOption(panel, "Random Phase", state[2]);
                    // Fresh operation supplies the intended option configuration, independent
                    // of editor state/caches. MathHologramTest separately checks its mathematics.
                    expected = (Function2D) hologram(phaseDetour, state[0], state[1], state[2]).create(source);
                    assertFalse(java.util.Arrays.equals(previous, expected.getReal()), "Fixture must distinguish successive option states");
                    assertWithIntensity(chain, intensity, new double[][]{expected.getReal(), expected.getImaginary()});
                    assertChannels(panel.getFunction(), expected.getReal(), expected.getImaginary());
                    assertEquals(256, ((Function2D) chain.getFunction()).getDimensionX());
                    assertEquals(128, ((Function2D) chain.getFunction()).getDimensionY());
                    assertTrue(chain.getFunction().isZeroCentered());
                    assertPreview(panel.systemPreviewPanel, expected);
                    assertTrue(panel.saveButton.isEnabled());
                    previous = expected.getReal().clone();
                }
            } finally { panel.deconstruct(); }
            CreateOperationSystemPanel reopened = editor(true, chain);
            try {
                find(reopened.calculator, HorizontalThumbnailList.class).setSelected(1);
                assertTrue(option(reopened, JCheckBox.class, "Error Diffusion").isSelected());
                assertTrue(option(reopened, JCheckBox.class, "3-Pixel-Wide Apertures").isSelected());
                if (phaseDetour) assertTrue(option(reopened, JCheckBox.class, "Random Phase").isSelected());
                assertWithIntensity(chain, intensity, new double[][]{expected.getReal(), expected.getImaginary()});
                assertPreview(reopened.systemPreviewPanel, expected);
                assertChannels(source, original[0], original[1]);
            } finally { reopened.deconstruct(); }
        });
    }

    private static UnaryOperation hologram(boolean phaseDetour, boolean diffusion, boolean wide, boolean random) {
        if (phaseDetour) {
            PhaseDetourHologramOp op = new PhaseDetourHologramOp(new ParameterBlock());
            op.setErrorDiffusion(diffusion); op.setWiderApertures(wide); op.setRandomPhase(random);
            return op;
        }
        HologramEncoderOp op = new HologramEncoderOp(new ParameterBlock());
        op.setErrorDiffusion(diffusion); op.setWiderApertures(wide);
        return op;
    }

    private static void setOption(CreateOperationSystemPanel panel, String name, boolean selected) {
        JCheckBox checkbox = option(panel, JCheckBox.class, name);
        if (checkbox.isSelected() != selected) checkbox.doClick(0);
    }

    private static void assertPreview(OperationPreviewPanel preview, Function2D expected) {
        String[] fields = {"realImagePanel", "imagImagePanel"};
        double[][] channels = {expected.getReal(), expected.getImaginary()};
        for (int channel = 0; channel < 2; channel++) {
            ImageDisplayPanel reference = new ImageDisplayPanel(new Dimension(200, 200));
            try {
                reference.setIndices(expected.getDimensionX(), expected.getDimensionY(), expected.isZeroCentered());
                reference.display(channels[channel]);
                ImageDisplayPanel actual = readField(preview, fields[channel]);
                RenderedImage expectedImage = readField(reference, "image"), actualImage = readField(actual, "image");
                assertEquals(expectedImage.getWidth(), actualImage.getWidth());
                assertEquals(expectedImage.getHeight(), actualImage.getHeight());
                Raster a = actualImage.getData(), b = expectedImage.getData();
                assertArrayEquals(b.getPixels(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), (int[]) null),
                        a.getPixels(a.getMinX(), a.getMinY(), a.getWidth(), a.getHeight(), (int[]) null),
                        "actual preview pixels channel " + channel);
            } finally { reference.deconstruct(); }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readField(Object owner, String name) {
        try { Field field = owner.getClass().getDeclaredField(name); field.setAccessible(true); return (T) field.get(owner); }
        catch (ReflectiveOperationException failure) { throw new AssertionError(failure); }
    }

    private static Function2D gaussianSource(boolean centered) {
        GaussianFunctionTerm1D x=(GaussianFunctionTerm1D)new GaussianFunctionTerm1D().getDefaultInstance();
        GaussianFunctionTerm1D y=(GaussianFunctionTerm1D)new GaussianFunctionTerm1D().getDefaultInstance();
        x.setAmplitude(2);x.setWidth(5);x.setCenter((centered?0:16)+3);
        y.setWidth(3);y.setCenter((centered?0:8)-2);
        XYFunctionTerm2D magnitude=new XYFunctionTerm2D(new ParameterBlock().addSource(x).addSource(y));
        ConstantFunctionTerm2D phase=(ConstantFunctionTerm2D)new ConstantFunctionTerm2D().getDefaultInstance();
        phase.setAmplitude(.7);
        FunctionPart2D a=new FunctionPart2D(new ArrayList<>(List.of(magnitude)),CombineTermsRule.getDefaultRule());
        FunctionPart2D b=new FunctionPart2D(new ArrayList<>(List.of(phase)),CombineTermsRule.getDefaultRule());
        return new MagPhaseFunction2D(a,b,"complex Gaussian",32,16,centered);
    }

    private static double[][] gaussianSamples() {
        double[][] result=new double[2][512];
        for(int row=0;row<16;row++)for(int col=0;col<32;col++) {
            double dx=(col-19)/5.0,dy=(9-row)/3.0;
            double magnitude=2*Math.exp(-Math.PI*(dx*dx+dy*dy));
            result[0][row*32+col]=magnitude*Math.cos(.7);
            result[1][row*32+col]=magnitude*Math.sin(.7);
        }
        return result;
    }

    private static double[][] directGaussianDft(double[][] input,boolean centered,boolean inverse) {
        double[][] result=new double[2][512];
        for(int fr=0;fr<16;fr++)for(int fc=0;fc<32;fc++) {
            int fx=fc-(centered?16:0),fy=15-fr-(centered?8:0),out=fr*32+fc;
            for(int row=0;row<16;row++)for(int col=0;col<32;col++) {
                int x=col-(centered?16:0),y=15-row-(centered?8:0),in=row*32+col;
                double angle=(inverse?1:-1)*2*Math.PI*(x*fx/32.0+y*fy/16.0);
                result[0][out]+=input[0][in]*Math.cos(angle)-input[1][in]*Math.sin(angle);
                result[1][out]+=input[1][in]*Math.cos(angle)+input[0][in]*Math.sin(angle);
            }
        }
        return result;
    }

    private static OperatorSystem intensityOf(OperatorSystem source) {
        Operation op=(Operation)new SquaredMagnitudeOp().getDefaultInstance();
        return chain(true,List.of(source),List.of(op),op.getPriority(),FUNCTION);
    }

    private static void assertWithIntensity(OperatorSystem chain,OperatorSystem intensity,double[][] expected) {
        assertChannels(chain.getFunction(),expected[0],expected[1]);
        double[] power=new double[expected[0].length];
        for(int i=0;i<power.length;i++)power[i]=expected[0][i]*expected[0][i]+expected[1][i]*expected[1][i];
        assertChannels(intensity.getFunction(),power,new double[power.length]);
    }

    private static <T extends AbstractButton> T option(CreateOperationSystemPanel panel,Class<T> type,String label) {
        List<T> controls=new ArrayList<>();collect(panel.operationPanel.getEditor(),type,controls);
        return controls.stream().filter(c->label.equals(c.getText())).findFirst().orElseThrow(()->new AssertionError("Missing option "+label));
    }

    private static void assertBinary(Function result, Function a, Function b, char operator) {
        double[] real = new double[a.getReal().length], imag = new double[real.length];
        for (int i = 0; i < real.length; i++) {
            double ar = a.getReal()[i], ai = a.getImaginary()[i], br = b.getReal()[i], bi = b.getImaginary()[i];
            if (operator == '+' || operator == '-') {
                int sign = operator == '+' ? 1 : -1;
                real[i] = ar + sign*br; imag[i] = ai + sign*bi;
            } else {
                double r = operator == '*' ? Math.hypot(ar, ai)*Math.hypot(br, bi) : Math.hypot(ar, ai)/Math.hypot(br, bi);
                double phase = Math.atan2(ai, ar) + (operator == '*' ? 1 : -1)*Math.atan2(bi, br);
                real[i] = r*Math.cos(phase); imag[i] = r*Math.sin(phase);
            }
        }
        assertChannels(result, real, imag);
    }

    private static void editParameter(ParameterEditor editor, String value, boolean accessible) {
        if (!accessible) { editor.setText(value); return; }
        javax.swing.JTextField field = ((javax.swing.JSpinner.DefaultEditor) editor.getEditor()).getTextField();
        javax.accessibility.AccessibleContext context = field.getAccessibleContext();
        assertTrue(context.getAccessibleStateSet().contains(javax.accessibility.AccessibleState.EDITABLE));
        assertNotNull(context.getAccessibleEditableText());
        context.getAccessibleEditableText().setTextContents(value);
        assertEquals(value, field.getText());
    }

    private static void assertThresholdChain(OperatorSystem chain, OperatorSystem downstream, Function source, String outer, double level) {
        double[] real = new double[source.getReal().length], imag = new double[real.length], power = new double[real.length];
        for (int i = 0; i < real.length; i++) {
            double r = source.getReal()[i] < level ? -1 : 3, im = source.getImaginary()[i] < level ? -1 : 3;
            real[i] = outer.equals("intensity") ? r*r+im*im : r;
            imag[i] = outer.equals("conjugate") ? -im : 0;
            power[i] = real[i]*real[i]+imag[i]*imag[i];
        }
        assertChannels(chain.getFunction(), real, imag);
        assertChannels(downstream.getFunction(), power, new double[real.length]);
    }

    private static void selectOperation(CreateOperationSystemPanel panel, Class<? extends Operation> type) {
        JList<?> list = panel.operationPanel.selector.getList();
        for (int i = 0; i < list.getModel().getSize(); i++) {
            if (((TextListItem) list.getModel().getElementAt(i)).getValue() == type) {
                list.setSelectedIndex(i); return;
            }
        }
        fail("Missing operation " + type);
    }

    private static <T> void collect(Container parent, Class<T> type, List<T> matches) {
        for (Component child : parent.getComponents()) {
            if (type.isInstance(child)) matches.add(type.cast(child));
            else if (child instanceof Container) collect((Container) child, type, matches);
        }
    }

    private static CreateOperationSystemPanel editor(boolean image, OperatorSystem system) {
        // Preserve the real event bus, pickers, operation editors, preview, and saveStateToModel.
        // No JFrame, desktop input, or replacement calculation/publication implementation.
        return image ? new CreateOperationSystem2DPanel(system) {
            @Override public void createFunctionSelectorPanel() {
                functionSelectorPanel = new Function2DSelectorPanel(broadcaster, this.system) {
                    @Override public ListModel getFunctionListModel() {
                        return library.getFunction2DList(excludeSystem).getModel();
                    }
                };
            }
            @Override public void createCalculator() {
                calculator = new OperationCalculator2D(broadcaster) {
                    @Override protected VerticalThumbnailList getLibrary() { return library; }
                };
            }
            @Override protected void refreshLibrary() { library.refresh(); }
        } : new CreateOperationSystem1DPanel(system) {
            @Override public void createFunctionSelectorPanel() {
                functionSelectorPanel = new Function1DSelectorPanel(broadcaster, this.system) {
                    @Override public ListModel getFunctionListModel() {
                        return library.getFunction1DList(excludeSystem).getModel();
                    }
                };
            }
            @Override public void createCalculator() {
                calculator = new OperationCalculator1D(broadcaster) {
                    @Override protected VerticalThumbnailList getLibrary() { return library; }
                };
            }
            @Override protected void refreshLibrary() { library.refresh(); }
        };
    }

    private static void prepareLibrary(FunctionProducer... sources) {
        library = new VerticalThumbnailList();
        library.setSelecting(false);
        for (FunctionProducer source : sources) library.addItem(source);
    }

    private static Function source(boolean image, int variant) {
        double[] real = new double[512], imag = new double[512];
        for (int i = 0; i < real.length; i++) {
            real[i] = variant + Math.cos(2 * Math.PI * i / 32);
            imag[i] = 0.5 * variant + Math.sin(2 * Math.PI * i / 16);
        }
        return image ? FunctionFactory.createFunction2D(real, imag, true, "source " + variant, 32, 16)
                : FunctionFactory.createFunction1D(real, imag, true, "source " + variant);
    }

    private static OperatorSystem chain(boolean image, List<FunctionProducer> inputs, List<Operation> operations, int... tokens) {
        ArrayList<Operation> ops = new ArrayList<>(operations);
        CombineOpsRule rule = new CombineOpsRule(tokens, ops);
        return image ? new OperatorSystem2D(rule, new ArrayList<>(inputs), ops)
                : new OperatorSystem1D(rule, new ArrayList<>(inputs), ops);
    }

    private static void pick(CreateOperationSystemPanel panel, FunctionProducer source) {
        FunctionSelectorPanel.FunctionButton target = panel.functionSelectorPanel.buttons.stream()
                .filter(b -> b.function == source).findFirst().orElseThrow();
        target.dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false, MouseEvent.BUTTON1));
    }

    private static void assertChannels(Function result, double[] real, double[] imag) {
        assertNotNull(result);
        assertArrayEquals(real, result.getReal(), 1e-8, "real channel");
        assertArrayEquals(imag, result.getImaginary(), 1e-8, "imaginary channel");
    }

    private static JButton button(Container parent, String name) {
        for (Component child : parent.getComponents()) {
            if (child instanceof JButton && (name.equals(((JButton) child).getText())
                    || name.equals(((JButton) child).getToolTipText()))) return (JButton) child;
            if (child instanceof Container) {
                JButton found = button((Container) child, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static <T> T find(Container parent, Class<T> type) {
        for (Component child : parent.getComponents()) {
            if (type.isInstance(child)) return type.cast(child);
            if (child instanceof Container) {
                T found = find((Container) child, type);
                if (found != null) return found;
            }
        }
        return null;
    }
}
