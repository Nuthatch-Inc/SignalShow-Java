package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.gui.Borders;
import signals.gui.datagenerator.EditImageFunctionTermPanel;
import signals.gui.datagenerator.ReImFunction2DToolBar;
import signals.gui.plot.DisplayCursorPanel;
import signals.gui.plot.Function2DOverviewPanel;
import signals.operation.SquaredMagnitudeOp;

/** Ordinary channel edits must reach both the FFT view and an existing operation chain. */
class FourierViewWorkflowTest extends MathAuditSupport {
    private Borders borders;
    private DataGeneratorCollections generators;
    @BeforeEach void setupEditorServices() {
        borders=Core.get().borders; generators=Core.get().dataGeneratorCollections;
        Core.get().borders=new Borders(); Core.get().dataGeneratorCollections=new DataGeneratorCollections();
    }
    @AfterEach void restoreEditorServices() {
        Core.get().borders=borders; Core.get().dataGeneratorCollections=generators;
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "64,128,true", "128,64,false", "64,128,false"})
    void channelEditsAndViewRevisitsKeepFourierCoefficientsAndLinkedIntensityCurrent(
            int width,int height,boolean centered) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            double[] originalReal=new double[width*height],originalImag=new double[width*height];
            for(int row=0;row<height;row++) for(int col=0;col<width;col++) {
                double angle=2*Math.PI*(2.0*x(col,width,centered)/width+3.0*y(row,height,centered)/height);
                originalReal[row*width+col]=.25+.8*Math.cos(angle)-.2*Math.sin(angle);
                originalImag[row*width+col]=-.5+.7*Math.sin(angle)+.3*Math.cos(angle);
            }
            Function2D source=image(originalReal,originalImag,width,height,centered);
            OperatorSystem2D intensity=unary(source,(SquaredMagnitudeOp)new SquaredMagnitudeOp().getDefaultInstance());
            intensity.getFunction().getReal(); // Materialize the linked result before any edits.
            Toolbar toolbar=new Toolbar(source);
            try {
                double[][] edits={{1,1},{2,1},{2,-.75}};
                for(int step=0;step<edits.length;step++) {
                    double a=edits[step][0],b=edits[step][1];
                    if(step>0) {
                        press(toolbar,step==1?"Edit Real Part":"Edit Imaginary Part");
                        EditImageFunctionTermPanel editor=find((Container)toolbar.visible(),EditImageFunctionTermPanel.class);
                        assertNotNull(editor); editor.setAmplitude(step==1?a:b); // Writes the actual parameter text field.
                        assertTrue(toolbar.refreshes>0,"The real toolbar must receive and publish the edit event");
                    }
                    double[] real=originalReal.clone(),imag=originalImag.clone();
                    for(int i=0;i<real.length;i++) { real[i]*=a; imag[i]*=b; }
                    assertImage(source,real,imag,width,height,centered);
                    Function result=intensity.getFunction();
                    for(int i=0;i<real.length;i++) {
                        assertEquals(real[i]*real[i]+imag[i]*imag[i],result.getReal()[i],1e-10,"linked intensity");
                        assertEquals(0,result.getImaginary()[i],0);
                    }
                    press(toolbar,"FFT");
                    double[] spectrumReal=new double[real.length],spectrumImag=new double[real.length];
                    double scale=Math.sqrt(width*height);
                    int dc=index(0,0,width,height,centered);
                    int positive=index(2,3,width,height,centered),negative=index(-2,-3,width,height,centered);
                    // Closed-form coefficients of cos/sin modes under the view's unitary forward FFT.
                    spectrumReal[dc]=.25*a*scale; spectrumImag[dc]=-.5*b*scale;
                    spectrumReal[positive]=(.4*a+.35*b)*scale; spectrumImag[positive]=(.1*a+.15*b)*scale;
                    spectrumReal[negative]=(.4*a-.35*b)*scale; spectrumImag[negative]=(-.1*a+.15*b)*scale;
                    assertOverview(toolbar.visible(),spectrumReal,spectrumImag);
                    press(toolbar,"Plot"); assertOverview(toolbar.visible(),real,imag);
                    press(toolbar,"FFT"); assertOverview(toolbar.visible(),spectrumReal,spectrumImag);
                }
            } finally { toolbar.close(); }
        });
    }

    private static void assertOverview(Component view,double[] real,double[] imag) {
        assertInstanceOf(Function2DOverviewPanel.class,view);
        DisplayCursorPanel[] panels=field(view,"plots");
        for(int channel=0;channel<4;channel++) {
            Object graphic=field(panels[channel],"graphic");
            double[] actual=field(graphic,"data"); assertEquals(real.length,actual.length);
            for(int i=0;i<real.length;i++) {
                double expected;
                if(channel==0) expected=real[i];
                else if(channel==1) expected=imag[i];
                else if(channel==2) expected=Math.hypot(real[i],imag[i]);
                else { expected=Math.atan2(imag[i],real[i]); if(expected==Math.PI) expected=-Math.PI; }
                assertEquals(expected,actual[i],1e-8,"visible channel "+channel+" sample "+i);
            }
        }
    }

    private static void press(Toolbar toolbar,String text) {
        for(Component c:toolbar.getComponents()) if(c instanceof JToggleButton button && text.equals(button.getText())) {
            button.doClick(0); return;
        }
        fail("Missing toolbar button "+text);
    }

    private static <T> T find(Container parent,Class<T> type) {
        if(type.isInstance(parent)) return type.cast(parent);
        for(Component c:parent.getComponents()) if(c instanceof Container container) {
            T result=find(container,type); if(result!=null) return result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object owner,String name) {
        for(Class<?> type=owner.getClass();type!=null;type=type.getSuperclass()) {
            try { Field field=type.getDeclaredField(name); field.setAccessible(true); return (T)field.get(owner); }
            catch(NoSuchFieldException missing) { /* Read superclass fields too. */ }
            catch(IllegalAccessException failure) { throw new AssertionError(failure); }
        }
        throw new AssertionError("Missing field "+name);
    }

    /** Only supplies the JFrame destination and library repaint; all math, events and views are real. */
    private static class Toolbar extends ReImFunction2DToolBar {
        int refreshes;
        Toolbar(Function source) { super(source); }
        @Override public void setContent(Component c,String tab) {
            if(content instanceof Deconstructable previous) previous.deconstruct();
            content=c; openTab=tab; setShowableState(tab);
        }
        @Override protected void refreshLibrary() { refreshes++; }
        Component visible() { return content; }
        void close() { if(content instanceof Deconstructable previous) previous.deconstruct(); deconstruct(); }
    }
}
