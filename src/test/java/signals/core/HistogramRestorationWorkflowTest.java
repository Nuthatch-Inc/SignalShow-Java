package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.gui.Borders;
import signals.gui.datagenerator.*;
import signals.gui.plot.HistogramCursorPanel;
import signals.gui.plot.FunctionOverviewPanel;
import signals.gui.plot.PlotGraphic;

/** Returning from another document must restore the selected function's own histogram. */
class HistogramRestorationWorkflowTest extends MathAuditSupport {
    private Borders borders;
    @BeforeEach void prepareBorders() { borders=Core.get().borders; Core.get().borders=new Borders(); }
    @AfterEach void restoreBorders() { Core.get().borders=borders; }

    static Stream<Arguments> documents() {
        return Stream.of(new int[]{64,0},new int[]{128,64},new int[]{64,128}).flatMap(size ->
            Stream.of(false,true).flatMap(polar -> Stream.of(false,true).map(centered ->
                Arguments.of(size[0],size[1],polar,centered))));
    }

    @ParameterizedTest
    @MethodSource("documents")
    void returningToHistogramRestoresItsButtonAndBothChannelDistributions(
            int width,int height,boolean polar,boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int length=width*Math.max(height,1);
            double[] a=new double[length],b=new double[length],real=new double[length],imag=new double[length];
            for(int i=0;i<length;i++) {
                a[i]=polar?1.3+.4*Math.cos(.17*i):-2+.25*(i%11);
                b[i]=polar?.2+.7*Math.sin(.13*i):3-.4*(i%7);
                real[i]=polar?a[i]*Math.cos(b[i]):a[i]; imag[i]=polar?a[i]*Math.sin(b[i]):b[i];
            }
            Function source=height==0
                ? signal(a,b,polar,centered)
                : (polar?FunctionFactory.createMagPhaseFunction2D(a,b,centered,"first",width,height):FunctionFactory.createFunction2D(a,b,centered,"first",width,height));
            double[] otherReal=real.clone(),otherImag=imag.clone();
            for(int i=0;i<length;i++) { otherReal[i]=6-1.8*real[i]; otherImag[i]=3+2.2*imag[i]; }
            Function other=height==0?FunctionFactory.createFunction1D(otherReal,otherImag,centered,"other")
                :FunctionFactory.createFunction2D(otherReal,otherImag,centered,"other",width,height);
            Slot slot=new Slot(); ArrayList<FunctionToolBar> opened=new ArrayList<>();
            try {
                FunctionToolBar first=toolbar(source,slot); opened.add(first);
                button(first,"Histogram").doClick(0);
                assertEquals(DataGeneratorToolBar.HISTOGRAM,source.getShowState());
                assertHistogram(slot.content,real,imag);
                FunctionToolBar second=toolbar(other,slot); opened.add(second);
                button(second,"Histogram").doClick(0);
                assertHistogram(slot.content,otherReal,otherImag);
                Component otherView=slot.content;
                // Function.show() reconstructs this toolbar and calls this exact saved-state dispatcher.
                FunctionToolBar returning=toolbar(source,slot); opened.add(returning);
                returning.selectButton(source.getShowState());
                assertNotSame(otherView,slot.content,"Returning to the first document must replace the other document's view");
                assertTrue(button(returning,"Histogram").isSelected());
                assertHistogram(slot.content,real,imag);
                // Ordinary Plot/FFT dispatch continues to work through the shared toolbar owner.
                for(String state:new String[]{DataGeneratorToolBar.PLOT,DataGeneratorToolBar.FFT}) {
                    returning.selectButton(state);
                    assertInstanceOf(FunctionOverviewPanel.class,slot.content);
                    assertEquals(state,source.getShowState());
                    assertTrue(button(returning,state.equals(DataGeneratorToolBar.PLOT)?"Plot":"FFT").isSelected());
                }
                returning.selectButton(DataGeneratorToolBar.HISTOGRAM);
                assertHistogram(slot.content,real,imag);
                assertArrayEquals(real,source.getReal(),1e-12); assertArrayEquals(imag,source.getImaginary(),1e-12);
            } finally { for(FunctionToolBar bar:opened) bar.deconstruct(); slot.content=null; }
        });
    }

    private static Function signal(double[] a,double[] b,boolean polar,boolean centered) {
        Function1D samples=(Function1D)FunctionFactory.createFunction1D(a,b,centered,"first");
        return polar?new MagPhaseFunction1D(samples.getPartA(),samples.getPartB(),"first",a.length,centered):samples;
    }

    private static void assertHistogram(Component component,double[] real,double[] imag) {
        HistogramCursorPanel histogram=assertInstanceOf(HistogramCursorPanel.class,component);
        assertEquals(2,histogram.getNumberPlots());
        double minimum=Math.min(Arrays.stream(real).min().orElseThrow(),Arrays.stream(imag).min().orElseThrow());
        double maximum=Math.max(Arrays.stream(real).max().orElseThrow(),Arrays.stream(imag).max().orElseThrow());
        double step=(maximum-minimum)/30;
        double[][] channels={real,imag};
        for(int channel=0;channel<2;channel++) {
            PlotGraphic.DataSequence sequence=histogram.getGraphic().getDataSequences().get(channel);
            double[] counts=new double[30];
            for(double value:channels[channel]) counts[Math.min(29,(int)Math.floor((value-minimum)/step))]++;
            for(int bin=0;bin<30;bin++) assertEquals(minimum+bin*step,sequence.getXData()[bin],1e-12);
            assertArrayEquals(counts,sequence.getYData(),0,"current document histogram channel "+channel);
        }
    }

    private static FunctionToolBar toolbar(Function function,Slot slot) {
        if(function instanceof MagPhaseFunction1D) return new MagPhaseFunction1DToolBar(function) {
            @Override public void setContent(Component c,String tab) { content=slot.content=c; openTab=tab; setShowableState(tab); }
        };
        if(function instanceof Function1D) return new ReImFunction1DToolBar(function) {
            @Override public void setContent(Component c,String tab) { content=slot.content=c; openTab=tab; setShowableState(tab); }
        };
        if(function instanceof MagPhaseFunction2D) return new MagPhaseFunction2DToolBar(function) {
            @Override public void setContent(Component c,String tab) { content=slot.content=c; openTab=tab; setShowableState(tab); }
        };
        return new ReImFunction2DToolBar(function) {
            @Override public void setContent(Component c,String tab) { content=slot.content=c; openTab=tab; setShowableState(tab); }
        };
    }

    private static JToggleButton button(FunctionToolBar toolbar,String text) {
        for(Component component:toolbar.getComponents())
            if(component instanceof JToggleButton button && text.equals(button.getText())) return button;
        throw new AssertionError("Missing button "+text);
    }
    private static class Slot { Component content; }
}
