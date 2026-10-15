package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.geom.Line2D;
import java.awt.image.RenderedImage;
import java.lang.reflect.Field;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.ParameterEditor;
import signals.gui.plot.ImageProfileCutGraphic;
import signals.gui.plot.PlotCursorPanel;
import signals.gui.plot.PlotGraphic;
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
  void profileViewDisplaysTheSelectedChannelAndEditedLine(int width, int height, boolean centered) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      ProfilePlot panel = new ProfilePlot(image(width, height, centered));
      ImageProfileCutGraphic graphic = field(panel, "graphic");
      try {
        String[] channels = {"Real Part", "Imaginary Part", "Magnitude", "Phase"};
        for (int channel = 0; channel < channels.length; channel++) {
          button(panel, channels[channel]).doClick(0);
          double[] displayed = field(graphic, "data");
          for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
            int x = col - (centered ? width / 2 : 0);
            int y = height - 1 - row - (centered ? height / 2 : 0);
            assertEquals(component(channel, 1000 * y + x, 2 * x - 3 * y + 7),
                displayed[row * width + col], 1e-8, "image channel at " + x + "," + y);
          }
          for (boolean horizontal : new boolean[]{true, false}) {
            for (int slope : new int[]{0, 1, -1}) for (int offset : new int[]{0, 3}) {
              editLine(panel, horizontal, slope, offset);
              PlotCursorPanel plot = field(panel, "plotPanel");
              assertEquals(1, plot.getNumberPlots());
              PlotGraphic.DataSequence sequence = plot.getGraphic().getDataSequences().get(0);
              int xmin = centered ? -width / 2 : 0, ymin = centered ? -height / 2 : 0;
              int length = horizontal ? width : height;
              assertEquals(length, sequence.getYData().length);
              for (int i = 0; i < length; i++) {
                int independent = i + (horizontal ? xmin : ymin);
                int x = horizontal ? independent : slope * independent + offset;
                int y = horizontal ? slope * independent + offset : independent;
                boolean inside = x >= xmin && x < xmin + width && y >= ymin && y < ymin + height;
                double expected = inside ? component(channel, 1000 * y + x, 2 * x - 3 * y + 7) : 0;
                assertEquals(independent, sequence.getXData()[i], 0, "profile coordinate");
                assertEquals(expected, sequence.getYData()[i], 1e-8, "profile channel sample " + i);
              }
            }
          }
        }
      } finally { graphic.deconstruct(); }
    });
  }

  @ParameterizedTest @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
  void profileCutOverlayPassesThroughTheSelectedImagePixels(int width, int height, boolean centered) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      ProfilePlot panel = new ProfilePlot(image(width, height, centered));
      ImageProfileCutGraphic graphic = field(panel, "graphic");
      try {
        for (boolean horizontal : new boolean[]{true, false}) {
          for (int slope : new int[]{0, 1, -1}) for (int offset : new int[]{0, 3}) {
            editLine(panel, horizontal, slope, offset);
            Line2D line = field(graphic, "line");
            RenderedImage rendered = field(graphic, "image");
            int xmin = centered ? -width / 2 : 0, ymin = centered ? -height / 2 : 0;
            int length = horizontal ? width : height;
            int checked = 0;
            for (int i = 0; i < length; i++) {
              int independent = i + (horizontal ? xmin : ymin);
              int x = horizontal ? independent : slope * independent + offset;
              int y = horizontal ? slope * independent + offset : independent;
              if (x < xmin || x >= xmin + width || y < ymin || y >= ymin + height) continue;
              // Locate the selected source pixel in the actual rendered image. Allow
              // a pixel for the existing edge-versus-center drawing convention.
              double px = rendered.getMinX() + (x - xmin + .5) * rendered.getWidth() / width;
              double py = rendered.getMinY() + (height - 1 - (y - ymin) + .5) * rendered.getHeight() / height;
              assertTrue(line.ptSegDist(px, py) <= 1.5,
                  "cut misses selected pixel " + x + "," + y + " by " + line.ptSegDist(px, py)
                  + " display pixels; horizontal=" + horizontal + ", slope=" + slope + ", offset=" + offset);
              checked++;
            }
            assertTrue(checked > 0);
          }
        }
      } finally { graphic.deconstruct(); }
    });
  }

  private static void editLine(ProfilePlot panel, boolean horizontal, int slope, int offset) {
    button(panel, horizontal ? " y = " : " x = ").doClick(0);
    ParameterEditor slopeEditor = field(panel, horizontal ? "riseEditor" : "runEditor");
    ParameterEditor offsetEditor = field(panel, horizontal ? "y0Editor" : "x0Editor");
    assertTrue(slopeEditor.isEnabled()); assertTrue(offsetEditor.isEnabled());
    slopeEditor.setText(Integer.toString(slope));
    offsetEditor.setText(Integer.toString(offset));
  }

  private static double component(int channel, double real, double imaginary) {
    if (channel == 0) return real;
    if (channel == 1) return imaginary;
    if (channel == 2) return Math.hypot(real, imaginary);
    double phase = Math.atan2(imaginary, real);
    return phase == Math.PI ? -Math.PI : phase; // Existing Java negative-axis convention.
  }

  @SuppressWarnings("unchecked")
  private static <T> T field(Object owner, String name) {
    for (Class<?> type = owner.getClass(); type != null; type = type.getSuperclass()) {
      try {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(owner);
      } catch (NoSuchFieldException missing) {
        // Inspect the component's superclass fields too.
      } catch (IllegalAccessException failure) { throw new AssertionError(failure); }
    }
    throw new AssertionError("Missing field " + name);
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
