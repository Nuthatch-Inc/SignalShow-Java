package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.gui.ParameterEditor;
import signals.gui.plot.HistogramCursorPanel;
import signals.gui.plot.PlotGraphic;

class HistogramViewWorkflowTest {
  private DisplayOptions display;
  private FunctionCreationOptions creation;
  private Colors colors;
  private Borders borders;
  @BeforeEach void setUp() {
    Core c=Core.get(); display=c.displayOptions; creation=c.functionCreationOptions; colors=c.colors; borders=c.borders;
    c.displayOptions=new DisplayOptions(); c.functionCreationOptions=new FunctionCreationOptions(); c.colors=new Colors(); c.borders=new Borders();
  }
  @AfterEach void tearDown() {
    Core c=Core.get(); c.displayOptions=display; c.functionCreationOptions=creation; c.colors=colors; c.borders=borders;
  }

  @Test void changingBinCountKeepsTheBaselineAndLastBarVisible() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      HistogramCursorPanel panel=new HistogramCursorPanel(new Dimension(800,450));
      panel.showToolBar();
      panel.setPlot(image(false));
      PlotGraphic plot=find(panel,PlotGraphic.class);
      plot.setSize(800,450);
      for (int bins : new int[] {30,16,40}) {
        find(panel,ParameterEditor.class).setText(Integer.toString(bins));
        var graphics=new BufferedImage(800,450,BufferedImage.TYPE_INT_RGB).createGraphics();
        try { plot.paint(graphics); } finally { graphics.dispose(); }
        assertTrue(plot.getYExtrema()[0] < 0, "Histogram baseline must be inside the plot");
        assertTrue(plot.getXExtrema()[1] >= 1 - 1e-12, "The entire final bin must fit: " + plot.getXExtrema()[1]);
        // Zero's position plus the 20-pixel tick label must fit in the actual component.
        double[] y=plot.getYExtrema();
        double baseline=20+409*y[1]/(y[1]-y[0]);
        assertTrue(baseline+20 < 450, "Leave room below zero for tick labels");
        assertEquals(2,panel.getNumberPlots());
        for (PlotGraphic.DataSequence sequence : plot.getDataSequences()) {
          assertEquals(bins,sequence.getYData().length);
          assertEquals(8192,Arrays.stream(sequence.getYData()).sum());
        }
      }
    });
  }

  @Test void nonnegativeImaginaryDataRetainsItsHistogram() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      HistogramCursorPanel panel=new HistogramCursorPanel(new Dimension(800,450));
      panel.setPlot(image(true));
      assertEquals(2,panel.getNumberPlots(),"A channel containing both zero and nonzero values is present");
    });
  }

  @Test void purelyRealDataStillShowsOneHistogram() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Function function=image(false);
      Function real=FunctionFactory.createFunction2D(function.getReal(),new double[8192],true,"real image",128,64);
      HistogramCursorPanel panel=new HistogramCursorPanel(new Dimension(800,450));
      panel.setPlot(real);
      assertEquals(1,panel.getNumberPlots());
    });
  }

  private static Function image(boolean nonnegativeImaginary) {
    double[] real=new double[8192],imag=new double[8192];
    for(int i=0;i<real.length;i++) {
      real[i]=2.0*i/(real.length-1)-1;
      imag[i]=nonnegativeImaginary ? (i%17)/16.0 : 1-2.0*i/(imag.length-1);
    }
    return FunctionFactory.createFunction2D(real,imag,true,"histogram image",128,64);
  }

  private static <T> T find(Container root,Class<T> type) {
    for(Component child:root.getComponents()) {
      if(type.isInstance(child)) return type.cast(child);
      if(child instanceof Container) {
        T found=find((Container)child,type); if(found!=null) return found;
      }
    }
    return null;
  }
}
