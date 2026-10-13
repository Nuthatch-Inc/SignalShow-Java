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
