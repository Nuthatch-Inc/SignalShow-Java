package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.plot.ProfilePlot;

/** Ordinary rectangular image profiles, including the actual view and its controls. */
class ImageProfileWorkflowTest {
  private DisplayOptions previousDisplay;
  private FunctionCreationOptions previousCreation;
  private Colors previousColors;
  private Borders previousBorders;

  @BeforeEach void setUp() {
    Core core = Core.get();
    previousDisplay = core.displayOptions;
    previousCreation = core.functionCreationOptions;
    core.functionCreationOptions = new FunctionCreationOptions();
    previousColors = core.colors;
    previousBorders = core.borders;
    core.displayOptions = new DisplayOptions();
    core.colors = new Colors();
    core.borders = new Borders();
  }

  @AfterEach void tearDown() {
    Core core = Core.get();
    core.displayOptions = previousDisplay;
    core.functionCreationOptions = previousCreation;
    core.colors = previousColors;
    core.borders = previousBorders;
  }

  @ParameterizedTest @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
  void rectangularProfilesSampleTheRequestedLine(int width, int height, boolean centered) {
    Function2D image = image(width, height, centered);
    for (int slope : new int[] {0, 1, -1}) {
      for (int offset : new int[] {0, 3}) {
        check(image, true, slope, offset);
        check(image, false, slope, offset);
      }
    }
  }

  @ParameterizedTest @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
  void profileViewOpensAndChangesChannelsAndLines(int width, int height, boolean centered) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      ProfilePlot panel = new ProfilePlot(image(width, height, centered));
      for (String channel : new String[] {"Imaginary Part", "Magnitude", "Phase", "Real Part"}) {
        button(panel, channel).doClick();
      }
      button(panel, " x = ").doClick();
      panel.parameterChanged(ProfilePlot.X0_PARAM, "3");
      panel.parameterChanged(ProfilePlot.RUN_PARAM, "1");
      button(panel, " y = ").doClick();
      panel.parameterChanged(ProfilePlot.Y0_PARAM, "3");
      panel.parameterChanged(ProfilePlot.RISE_PARAM, "-1");
      assertNotNull(panel);
    });
  }

  private static Function2D image(int width, int height, boolean centered) {
    double[] real = new double[width * height];
    double[] imag = new double[real.length];
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int x = col - (centered ? width / 2 : 0);
        int y = height - 1 - row - (centered ? height / 2 : 0);
        real[row * width + col] = 1000 * y + x;
        imag[row * width + col] = 2 * x - 3 * y + 7;
      }
    }
    return (Function2D) FunctionFactory.createFunction2D(real, imag, centered, "asymmetric image", width, height);
  }

  private static void check(Function2D image, boolean horizontal, int slope, int offset) {
    Function profile = horizontal ? image.getYProfile(slope, offset) : image.getXProfile(slope, offset);
    int width = image.getDimensionX(), height = image.getDimensionY();
    int xmin = image.isZeroCentered() ? -width / 2 : 0;
    int ymin = image.isZeroCentered() ? -height / 2 : 0;
    int length = horizontal ? width : height;
    assertEquals(length, profile.getReal().length);
    assertEquals(length, profile.getImaginary().length);
    for (int i = 0; i < length; i++) {
      int independent = i + (horizontal ? xmin : ymin);
      int x = horizontal ? independent : slope * independent + offset;
      int y = horizontal ? slope * independent + offset : independent;
      boolean inside = x >= xmin && x < xmin + width && y >= ymin && y < ymin + height;
      assertEquals(inside ? 1000 * y + x : 0, profile.getReal()[i], "real sample " + i);
      assertEquals(inside ? 2 * x - 3 * y + 7 : 0, profile.getImaginary()[i], "imaginary sample " + i);
    }
    assertTrue(profile.getCompactDescriptor().contains((horizontal ? "y = " : "x = ")
        + slope + (horizontal ? "x+" : "y+") + offset + "]"),
        "The profile label must retain the user's mathematical intercept");
  }

  private static JRadioButton button(Container root, String label) {
    for (Component child : root.getComponents()) {
      if (child instanceof JRadioButton && label.equals(((JRadioButton) child).getText())) return (JRadioButton) child;
      if (child instanceof Container) {
        JRadioButton found = button((Container) child, label);
        if (found != null) return found;
      }
    }
    return null;
  }
}
