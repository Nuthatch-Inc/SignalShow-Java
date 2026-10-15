package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Dimension;
import java.awt.image.Raster;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.gui.Borders;
import signals.gui.plot.ImageDisplayPanel;
import signals.gui.plot.HistogramCursorPanel;
import signals.gui.plot.PlotGraphic;
import signals.operation.OffsetOp;

class NegativeImageDisplayWorkflowTest extends MathAuditSupport {
    private Borders previousBorders;
    @BeforeEach void prepareBorders() { previousBorders = Core.get().borders; Core.get().borders = new Borders(); }
    @AfterEach void restoreBorders() { Core.get().borders = previousBorders; }

    @ParameterizedTest
    @CsvSource({"128,64,false", "128,64,true", "64,128,false", "64,128,true"})
    void offsetImageUsesItsActualNegativeRangeInBothChannelViews(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source = negativeImage(width, height, centered);
            OffsetOp offset = (OffsetOp) new OffsetOp().getDefaultInstance();
            OperatorSystem2D chain = unary(source, offset);
            DisplayProbe panel = new DisplayProbe(width, height);
            try {
                panel.setIndices(width, height, centered);
                for (double shift : new double[]{-1, -3}) {
                    offset.setValue(0, shift); offset.setValue(1, shift - 1); chain.freeMemory();
                    Function result = chain.getFunction();
                    for (double[] channel : new double[][]{result.getReal(), result.getImaginary()}) {
                        panel.display(channel);
                        double min = Arrays.stream(channel).min().orElseThrow(), max = Arrays.stream(channel).max().orElseThrow();
                        Raster pixels = panel.pixels();
                        assertEquals(255, pixels.getSample(width-1, height-1, 0), "Brightest sample should span the grayscale range");
                        assertEquals(min, panel.min(), 1e-12); assertEquals(max, panel.max(), 1e-12);
                        assertEquals(min, Double.parseDouble(panel.minText()), 1e-12);
                        assertEquals(max, Double.parseDouble(panel.maxText()), 1e-12);
                        for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
                            double expected = 255 * (channel[row*width+col] - min) / (max - min);
                            assertEquals(expected, pixels.getSample(col, row, 0), 1.01, "pixel " + col + "," + row);
                        }
                    }
                }
            } finally { panel.deconstruct(); }
        });
    }

    @ParameterizedTest
    @CsvSource({"128,64,false", "128,64,true", "64,128,false", "64,128,true"})
    void negativeComplexHistogramAndLinePlotsUseObservedExtrema(int width, int height, boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Function2D source = negativeImage(width, height, centered);
            OffsetOp offset = (OffsetOp) new OffsetOp().getDefaultInstance();
            offset.setValue(0, -1); offset.setValue(1, -2);
            Function result = unary(source, offset).getFunction();
            HistogramCursorPanel histogram = new HistogramCursorPanel(new Dimension(800,450));
            histogram.setPlot(result);
            double min = -10, max = -3, step = (max-min)/30;
            assertEquals(2, histogram.getNumberPlots());
            double[][] channels = {result.getReal(), result.getImaginary()};
            for (int c = 0; c < 2; c++) {
                PlotGraphic.DataSequence sequence = histogram.getGraphic().getDataSequences().get(c);
                double[] counts = new double[30];
                for (double value : channels[c]) counts[Math.min(29, (int)Math.floor((value-min)/step))]++;
                for (int b = 0; b < 30; b++) assertEquals(min + b*step, sequence.getXData()[b], 1e-12, "bin edge " + b);
                assertArrayEquals(counts, sequence.getYData(), 0);
            }
            PlotGraphic line = new PlotGraphic(new Dimension(800,450));
            double[] x = new double[width]; for (int i = 0; i < width; i++) x[i] = i;
            line.add(x, Arrays.copyOf(result.getReal(), width), "negative row");
            line.autofit();
            assertArrayEquals(new double[]{-7,-3}, line.getYExtrema(), 1e-12);
        });
    }

    private static Function2D negativeImage(int width, int height, boolean centered) {
        double[] real = new double[width*height], imag = new double[real.length];
        for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
            real[row*width+col] = -6 + 4.0*col/(width-1);
            imag[row*width+col] = -8 + 4.0*row/(height-1);
        }
        return image(real, imag, width, height, centered);
    }

    private static class DisplayProbe extends ImageDisplayPanel {
        DisplayProbe(int width, int height) { super(new Dimension(width,height)); }
        Raster pixels() { return imageMath.getScaledImage().getData(); }
        double min() { return imageMath.getMinValue(); }
        double max() { return imageMath.getMaxValue(); }
        String minText() { return ((javax.swing.JSpinner.DefaultEditor) minEditor.getEditor()).getTextField().getText(); }
        String maxText() { return ((javax.swing.JSpinner.DefaultEditor) maxEditor.getEditor()).getTextField().getText(); }
    }
}
