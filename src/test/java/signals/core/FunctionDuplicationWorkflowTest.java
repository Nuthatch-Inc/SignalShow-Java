package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.*;
import signals.gui.Colors;

class FunctionDuplicationWorkflowTest {
  private FunctionCreationOptions oldCreation;
  private DisplayOptions oldDisplay;
  private Colors oldColors;

  @BeforeEach void setUp() {
    Core core = Core.get();
    oldCreation = core.functionCreationOptions;
    oldDisplay = core.displayOptions;
    oldColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach void tearDown() {
    Core core = Core.get();
    core.functionCreationOptions = oldCreation;
    core.displayOptions = oldDisplay;
    core.colors = oldColors;
  }

  @ParameterizedTest @CsvSource({"false,false", "false,true", "true,false", "true,true"})
  void duplicateRetainsItsDefinitionAndEditsDoNotChangeOriginal(boolean image, boolean magPhase) {
    GaussianFunctionTerm1D gaussian = (GaussianFunctionTerm1D) new GaussianFunctionTerm1D().getDefaultInstance();
    gaussian.setWidth(24);
    Function original;
    if (image) {
      FunctionPart2D a = part2D(new PolarFunctionTerm2D(new ParameterBlock().addSource(gaussian).addSource(constant(1))));
      FunctionPart2D b = part2D(new XYFunctionTerm2D(new ParameterBlock().addSource(constant(0.25)).addSource(constant(1))));
      original = magPhase ? new MagPhaseFunction2D(a, b, "original", 128, 64, true)
          : new RealImagFunction2D(a, b, "original", 128, 64, true);
    } else {
      FunctionPart1D a = part1D(gaussian), b = part1D(constant(0.25));
      original = magPhase ? new MagPhaseFunction1D(a, b, "original", 128, true)
          : new RealImagFunction1D(a, b, "original", 128, true);
    }
    original.newState = false;
    double[] real = original.getReal().clone(), imag = original.getImaginary().clone();
    Function copy = original.clone();
    assertArrayEquals(real, copy.getReal());
    assertArrayEquals(imag, copy.getImaginary());
    AnalyticFunctionTerm1D edited = firstAnalytic(copy);
    edited.setAmplitude(2);
    publishPartA(copy);
    original.freeMemory(); // A stale original cache must not conceal shared mutable terms.
    assertArrayEquals(real, original.getReal(), "Editing a duplicate must leave the original unchanged");
    assertArrayEquals(imag, original.getImaginary());
    for (int i = 0; i < real.length; i++) {
      assertEquals(2 * real[i], copy.getReal()[i], 1e-12);
      assertEquals((magPhase ? 2 : 1) * imag[i], copy.getImaginary()[i], 1e-12);
    }
    assertFalse(copy.newState, "A duplicate opens its existing definition, not the new-function chooser");
    firstAnalytic(original).setWidth(32);
    publishPartA(original);
    copy.freeMemory();
    assertEquals(24, edited.getWidth(), "Later original edits must also stay independent");
  }

  @ParameterizedTest
  @CsvSource({"1D,false,false", "1D,false,true", "1D,true,false", "1D,true,true",
      "Cartesian,false,false", "Cartesian,false,true", "Cartesian,true,false", "Cartesian,true,true",
      "Polar,false,false", "Polar,false,true", "Polar,true,false", "Polar,true,true"})
  void sampledProfileCopiesKeepTheirModeAndLinkedResults(String geometry, boolean magPhase, boolean centered) {
    DataFunctionTerm1D real = sampled(new double[]{1,.2,-.6,1.8,.4,-1.2,.8,2}, "real-profile.txt");
    DataFunctionTerm1D imag = sampled(new double[]{-.3,1.1,.7,-.8,.2,.9,-1.3,.5}, "imag-profile.txt");
    boolean image = !geometry.equals("1D");
    Function original;
    if (image) {
      SeparableFunctionTerm2D a = sampledImageTerm(real, geometry), b = sampledImageTerm(imag, geometry);
      original = magPhase ? new MagPhaseFunction2D(part2D(a), part2D(b), "sampled", 128, 64, centered)
          : new RealImagFunction2D(part2D(a), part2D(b), "sampled", 128, 64, centered);
    } else {
      original = magPhase ? new MagPhaseFunction1D(part1D(real), part1D(imag), "sampled", 128, centered)
          : new RealImagFunction1D(part1D(real), part1D(imag), "sampled", 128, centered);
    }
    double[] er = original.getReal().clone(), ei = original.getImaginary().clone();
    Function copy = original.clone();
    assertArrayEquals(er, copy.getReal(), "Duplicating must retain the sampled shape");
    assertArrayEquals(ei, copy.getImaginary(), "Duplicating must retain the second channel");
    DataFunctionTerm1D copied = (DataFunctionTerm1D) firstAnalytic(copy);
    assertEquals(Constants.SCALE_AND_PAD_MODE, copied.getMode());
    assertEquals("real-profile.txt", copied.getEquation(new String[]{"x"}));
    assertNotSame(real.getOriginalData(), copied.getOriginalData());
    signals.operation.SquaredMagnitudeOp power = new signals.operation.SquaredMagnitudeOp(new ParameterBlock());
    ArrayList<Operation> ops = new ArrayList<>(java.util.List.of(power));
    CombineOpsRule rule = new CombineOpsRule(new int[]{power.getPriority(), CombineOpsRule.FUNCTION}, ops);
    OperatorSystem downstream = image ? new OperatorSystem2D(rule, new ArrayList<>(java.util.List.of(copy)), ops)
        : new OperatorSystem1D(rule, new ArrayList<>(java.util.List.of(copy)), ops);
    assertNotNull(downstream.getFunction());
    copied.setAmplitude(2 * real.getAmplitude()); publishPartA(copy);
    original.freeMemory();
    assertArrayEquals(er, original.getReal()); assertArrayEquals(ei, original.getImaginary());
    Function result = downstream.getFunction();
    for (int i = 0; i < er.length; i++) {
      double re = 2 * er[i], im = (magPhase ? 2 : 1) * ei[i];
      assertEquals(re, copy.getReal()[i], 1e-10); assertEquals(im, copy.getImaginary()[i], 1e-10);
      assertEquals(re * re + im * im, result.getReal()[i], 1e-9);
      assertEquals(0, result.getImaginary()[i]);
    }
    copied.setMode(Constants.CROP_MODE); copied.getOriginalData()[0] = 99; publishPartA(copy);
    original.freeMemory();
    assertEquals(Constants.SCALE_AND_PAD_MODE, real.getMode());
    assertEquals(1, real.getOriginalData()[0]);
    assertArrayEquals(er, original.getReal()); assertArrayEquals(ei, original.getImaginary());
  }

  private static DataFunctionTerm1D sampled(double[] values, String descriptor) {
    ParameterBlock pb = new DataFunctionTerm1D().getDefaultParamBlock(); pb.addSource(values);
    DataFunctionTerm1D term = new DataFunctionTerm1D(pb);
    term.setMode(Constants.SCALE_AND_PAD_MODE); term.setCenter(8); term.setWidth(12);
    term.setAmplitude(1.2); term.setDescriptor(descriptor); return term;
  }

  private static SeparableFunctionTerm2D sampledImageTerm(DataFunctionTerm1D data, String geometry) {
    ParameterBlock pb = new ParameterBlock().addSource(data).addSource(constant(1));
    return geometry.equals("Polar") ? new PolarFunctionTerm2D(pb) : new XYFunctionTerm2D(pb);
  }

  @Test void complexCopyRetainsLinkedChannelEditingAndIndependentSharedChildren() {
    FunctionTerm1D angle = constant(1);
    FunctionTerm1D cosine = (FunctionTerm1D) new ChirpFunctionTerm1D().getDefaultInstance();
    FunctionTerm1D sine = (FunctionTerm1D) new SineChirpFunctionTerm1D().getDefaultInstance();
    RealImagFunction2D original = new RealImagFunction2D(
        part2D(new PolarFunctionTerm2D(new ParameterBlock().addSource(cosine).addSource(angle))),
        part2D(new PolarFunctionTerm2D(new ParameterBlock().addSource(sine).addSource(angle))),
        "complex chirp", 256, 256, true);
    original.setType(Constants.FunctionType.COMPLEX);
    original.newState = false;
    Function2D copy = original.clone();
    assertEquals(Constants.FunctionType.COMPLEX, copy.getType());
    assertFalse(copy.newState);
    SeparableFunctionTerm2D a = (SeparableFunctionTerm2D) copy.getPartA().getTermsList().get(0);
    SeparableFunctionTerm2D b = (SeparableFunctionTerm2D) copy.getPartB().getTermsList().get(0);
    assertSame(a.getFunctionTerm1DB(), b.getFunctionTerm1DB(), "Preserve deliberate sharing inside this copy");
    assertNotSame(angle, a.getFunctionTerm1DB(), "Sharing must not cross document ownership");
    assertArrayEquals(original.getReal(), copy.getReal());
    assertArrayEquals(original.getImaginary(), copy.getImaginary());
  }

  @Test void copyUsesTheCurrentSeparableChildrenAfterGeneratorReplacement() {
    XYFunctionTerm2D term = new XYFunctionTerm2D(new ParameterBlock().addSource(constant(1)).addSource(constant(1)));
    term.setFunctionTerm1DA((FunctionTerm1D) new GaussianFunctionTerm1D().getDefaultInstance());
    RealImagFunction2D original = new RealImagFunction2D(part2D(term), part2D(new ZeroFunctionTerm2D(new ParameterBlock())),
        "replaced generator", 128, 64, true);
    Function2D copy = original.clone();
    assertArrayEquals(original.getReal(), copy.getReal());
    SeparableFunctionTerm2D copiedTerm = (SeparableFunctionTerm2D) copy.getPartA().getTermsList().get(0);
    assertNotSame(term.getFunctionTerm1DA(), copiedTerm.getFunctionTerm1DA());
    assertInstanceOf(GaussianFunctionTerm1D.class, copiedTerm.getFunctionTerm1DA());
  }

  @Test void sampledImageCopyRetainsScalingModeAndOwnsItsData() {
    double[] samples = new double[128 * 64];
    for (int i = 0; i < samples.length; i++) samples[i] = i % 128;
    Function2D original = (Function2D) FunctionFactory.createFunction2D(samples, new double[samples.length], true, "image", 128, 64);
    DataFunctionTerm2D term = (DataFunctionTerm2D) original.getPartA().getTermsList().get(0);
    term.setMode(Constants.SCALE_AND_WRAP_MODE);
    Function2D copy = (Function2D) original.clone();
    DataFunctionTerm2D copiedTerm = (DataFunctionTerm2D) copy.getPartA().getTermsList().get(0);
    assertEquals(Constants.SCALE_AND_WRAP_MODE, copiedTerm.getMode());
    assertNotSame(term.getOriginalData(), copiedTerm.getOriginalData());
    assertArrayEquals(original.getReal(), copy.getReal());
    copiedTerm.setMode(Constants.CROP_MODE);
    assertEquals(Constants.SCALE_AND_WRAP_MODE, term.getMode());
  }

  @Test void noiseCopyRetainsItsSeedAndSettingsWithoutSharingLaterEdits() {
    UniformNoiseFunctionTerm2D noise = (UniformNoiseFunctionTerm2D) new UniformNoiseFunctionTerm2D().getDefaultInstance();
    noise.getFunctionTerm1D().setSeed(23);
    noise.getFunctionTerm1D().setAmplitude(2);
    RealImagFunction2D original = new RealImagFunction2D(part2D(noise), part2D(new ZeroFunctionTerm2D(new ParameterBlock())),
        "noise", 128, 64, true);
    Function2D copy = original.clone();
    NoiseFunctionTerm2D copied = (NoiseFunctionTerm2D) copy.getPartA().getTermsList().get(0);
    assertArrayEquals(original.getReal(), copy.getReal());
    copied.getFunctionTerm1D().setSeed(31);
    publishPartA(copy);
    assertEquals(23, noise.getFunctionTerm1D().getSeed());
    assertFalse(Arrays.equals(original.getReal(), copy.getReal()));
  }

  private static ConstantFunctionTerm1D constant(double amplitude) {
    ConstantFunctionTerm1D term = (ConstantFunctionTerm1D) new ConstantFunctionTerm1D().getDefaultInstance();
    term.setAmplitude(amplitude);
    return term;
  }

  private static FunctionPart1D part1D(FunctionTerm1D term) {
    return new FunctionPart1D(new ArrayList<>(Arrays.asList(term)), CombineTermsRule.getDefaultRule());
  }

  private static FunctionPart2D part2D(FunctionTerm2D term) {
    return new FunctionPart2D(new ArrayList<>(Arrays.asList(term)), CombineTermsRule.getDefaultRule());
  }

  private static AnalyticFunctionTerm1D firstAnalytic(Function function) {
    if (function instanceof Function1D) return (AnalyticFunctionTerm1D) ((Function1D) function).getPartA().getTermsList().get(0);
    return (AnalyticFunctionTerm1D) ((SeparableFunctionTerm2D) ((Function2D) function).getPartA().getTermsList().get(0)).getFunctionTerm1DA();
  }

  private static void publishPartA(Function function) {
    if (function instanceof Function1D) ((Function1D) function).setPartA(((Function1D) function).getPartA());
    else ((Function2D) function).setPartA(((Function2D) function).getPartA());
  }
}
