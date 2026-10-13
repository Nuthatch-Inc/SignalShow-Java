package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import signals.functionterm.ImageFunctionTerm2D;
import signals.functionterm.ZeroFunctionTerm2D;
import signals.gui.Colors;
import signals.gui.plot.FunctionTerm2DThumbnailGraphic;

/** Bounded creation/rendering coverage, separate from mathematical certification. */
class Generator2DWorkflowTest {
  private FunctionCreationOptions previousCreationOptions;
  private DisplayOptions previousDisplayOptions;
  private Colors previousColors;

  @BeforeEach
  void initializeFunctionServices() {
    Core core = Core.get();
    previousCreationOptions = core.functionCreationOptions;
    previousDisplayOptions = core.displayOptions;
    previousColors = core.colors;
    core.functionCreationOptions = new FunctionCreationOptions();
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
  }

  @AfterEach
  void restoreFunctionServices() {
    Core core = Core.get();
    core.functionCreationOptions = previousCreationOptions;
    core.displayOptions = previousDisplayOptions;
    core.colors = previousColors;
  }

  static Stream<Arguments> registeredGenerators() {
    DataGeneratorCollections registry = new DataGeneratorCollections();
    return Stream.of(registry.getImage2DList(), registry.getNoise2DList(), registry.getAnalytic2DList())
        .flatMap(List::stream).flatMap(type -> Stream.of(
            Arguments.of(type, 256, 256, true), Arguments.of(type, 256, 256, false),
            Arguments.of(type, 64, 32, true), Arguments.of(type, 64, 32, false),
            Arguments.of(type, 32, 64, true), Arguments.of(type, 32, 64, false)));
  }

  @ParameterizedTest(name = "{0}: {1}x{2}, centered={3}")
  @MethodSource("registeredGenerators")
  void registered2DGeneratorsProduceChannelsAndThumbnail(Class<? extends DataGenerator> type,
      int width, int height, boolean centered) throws Exception {
    FunctionTerm2D term = (FunctionTerm2D) type.getDeclaredConstructor().newInstance().getDefaultInstance();
    // Match the image editor's initial fit-to-document parameters.
    if (term instanceof ImageFunctionTerm2D image) {
      image.setWidth(width);
      image.setHeight(height);
      if (!centered) {
        image.setCenterX(width / 2.0);
        image.setCenterY(height / 2.0);
      }
    }
    FunctionTerm2D zero = (FunctionTerm2D) new ZeroFunctionTerm2D().getDefaultInstance();
    Function function = new RealImagFunction2D(part(term), part(zero), "generator audit", width, height, centered);
    for (double[] channel : List.of(function.getReal(), function.getImaginary(),
        function.getMagnitude(), function.getPhase())) {
      assertEquals(width * height, channel.length);
      for (double sample : channel) assertTrue(Double.isFinite(sample), "non-finite default sample");
    }
    // Force raster evaluation through the production thumbnail renderer.
    var thumbnail = new FunctionTerm2DThumbnailGraphic(term, width, height, centered)
        .getScaledImage(new Dimension(40, 40));
    assertNotNull(thumbnail.getData());
    assertEquals(40 * width / Math.max(width, height), thumbnail.getWidth());
    assertEquals(40 * height / Math.max(width, height), thumbnail.getHeight());
  }

  private static FunctionPart2D part(FunctionTerm2D term) {
    return new FunctionPart2D(new ArrayList<>(List.of(term)), CombineTermsRule.getDefaultRule());
  }
}
