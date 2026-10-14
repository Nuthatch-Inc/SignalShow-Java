package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import signals.functionterm.*;
import signals.operation.SquaredMagnitudeOp;

class MathSampledImagesTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"64,32,true","64,32,false","32,64,true","32,64,false"})
    void nonconstantDataImagesRetainScaleWhenMovedAcrossCanvas(int w,int h,boolean centered) {
        double[] real=new double[w*h],imag=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {real[row*w+col]=ramp(col,row,true);imag[row*w+col]=ramp(col,row,false);}
        Function2D source=image(real,imag,w,h,centered);
        DataFunctionTerm2D a=(DataFunctionTerm2D)source.getPartA().getTermsList().get(0),b=(DataFunctionTerm2D)source.getPartB().getTermsList().get(0);
        a.setAmplitude(1.4);b.setAmplitude(-.7);
        OperatorSystem2D power=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double scale:new double[]{.5,.75}) for(int direction:new int[]{0,1,-1}) {
            int width=(int)(w*scale),height=(int)(h*scale),dx=direction*(w/2-6),dy=direction*(h/2-6);
            a.setWidth(width);b.setWidth(width);a.setHeight(height);b.setHeight(height);
            a.setCenterX((centered?0:w/2)+dx);a.setCenterY((centered?0:h/2)+dy);
            b.setCenterX((centered?0:w/2)-dx);b.setCenterY((centered?0:h/2)-dy);
            source.setPartA(source.getPartA());source.setPartB(source.getPartB());
            double[] actualR=source.getReal(),actualI=source.getImaginary(),actualPower=power.getFunction().getReal();
            for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
                int k=row*w+col;
                double er=sample(col,row,w,h,width,height,dx,dy,true),ei=sample(col,row,w,h,width,height,-dx,-dy,false);
                assertEquals(er,actualR[k],.001,"real at "+col+","+row);assertEquals(ei,actualI[k],.001,"imag at "+col+","+row);
                assertEquals(er*er+ei*ei,actualPower[k],.01);
            }
            assertArrayEquals(new double[w*h],power.getFunction().getImaginary(),0);
            assertArrayEquals(real,a.getOriginalData(),0);assertArrayEquals(imag,b.getOriginalData(),0);
        }
    }

    static Stream<Arguments> resources() {
        return Stream.of("A","Boat","E","Gradient","Lena","MRIPhase1","SARPhase1","StepWedge")
                .flatMap(name->Stream.of(Arguments.of(name,true),Arguments.of(name,false)));
    }
    @ParameterizedTest
    @MethodSource("resources")
    void packagedImagesMatchIndependentlyDecodedPixelsAndComplexIntensity(String name,boolean centered) throws Exception {
        PredefinedImageFunctionTerm2D a=predefined(name),b=predefined(name);
        BufferedImage decoded=ImageIO.read(getClass().getResource(a.getFilename(0)));
        checkResource(a,b,decoded,centered);
    }

    @ParameterizedTest
    @CsvSource({"128,true","128,false","256,true","256,false","512,true","512,false"})
    void barChartsSelectTheSuppliedResolutionAfterAWidthEdit(int dimension,boolean centered) throws Exception {
        BarChartFunctionTerm2D a=(BarChartFunctionTerm2D)new BarChartFunctionTerm2D().getDefaultInstance();
        BarChartFunctionTerm2D b=(BarChartFunctionTerm2D)new BarChartFunctionTerm2D().getDefaultInstance();
        // First create at 128 to populate the real cached-image path, then edit the size.
        a.setWidth(128);a.setHeight(128);a.create(128,128,true);
        b.setWidth(128);b.setHeight(128);b.create(128,128,true);
        BufferedImage decoded=ImageIO.read(getClass().getResource("/images/barchart"+dimension+".png"));
        checkResource(a,b,decoded,centered);
    }


    @ParameterizedTest
    @CsvSource({"MRIPhase1,true","MRIPhase1,false","SARPhase1,true","SARPhase1,false"})
    void phaseImagesComposeUnitCorrectComplexPhasors(String name,boolean centered) throws Exception {
        PredefinedImageFunctionTerm2D phase=predefined(name);
        BufferedImage decoded=ImageIO.read(getClass().getResource(phase.getFilename(0)));
        int w=decoded.getWidth(),h=decoded.getHeight();
        phase.setWidth(w);phase.setHeight(h);phase.setCenterX(centered?0:w/2.0);phase.setCenterY(centered?0:h/2.0);
        double[] magnitude=new double[w*h];java.util.Arrays.fill(magnitude,1.3);
        Function2D source=new MagPhaseFunction2D(image(magnitude,new double[w*h],w,h,centered).getPartA(),part(phase),"phase image",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double scale:new double[]{1,-.8}) {
            phase.setAmplitude(scale);source.setPartB(source.getPartB());
            double[] er=new double[w*h],ei=new double[w*h];
            for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
                double value=((decoded.getRGB(col,row)>>>16)&255)/255.0;
                double radians=scale*Math.PI*(2*value-1);int k=row*w+col;
                er[k]=1.3*Math.cos(radians);ei[k]=1.3*Math.sin(radians);
            }
            assertImage(source,er,ei,w,h,centered);
            double[] power=new double[w*h];java.util.Arrays.fill(power,1.3*1.3);
            assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
        }
    }

    private void checkResource(PredefinedImageFunctionTerm2D a,PredefinedImageFunctionTerm2D b,BufferedImage decoded,boolean centered) {
        int w=decoded.getWidth(),h=decoded.getHeight();
        for(PredefinedImageFunctionTerm2D term:List.of(a,b)) {
            term.setWidth(w);term.setHeight(h);term.setCenterX(centered?0:w/2.0);term.setCenterY(centered?0:h/2.0);
        }
        Function2D source=new RealImagFunction2D(part(a),part(b),"packaged pixels",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double amplitude:new double[]{1.4,-.6}) {
            a.setAmplitude(amplitude);b.setAmplitude(-.7);source.setPartA(source.getPartA());source.setPartB(source.getPartB());
            double[] er=new double[w*h],ei=new double[w*h],power=new double[w*h];
            for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
                // PNG's decoded red sample is the existing scalar-image channel convention.
                double value=((decoded.getRGB(col,row)>>>16)&255)/255.0;int k=row*w+col;
                if(a instanceof PhaseImageFunctionTerm2D) value=Math.PI*(2*value-1);
                er[k]=amplitude*value;ei[k]=-.7*value;power[k]=er[k]*er[k]+ei[k]*ei[k];
            }
            assertImage(source,er,ei,w,h,centered);assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
        }
    }
    private static double sample(int col,int row,int w,int h,int width,int height,int dx,int dy,boolean real) {
        int localX=col-(w/2+dx-width/2),localY=row-(h/2-dy-height/2);
        if(localX<0 || localX>=width || localY<0 || localY>=height) return 0;
        double sx=(localX+.5)*w/width-.5,sy=(localY+.5)*h/height-.5;
        return (real?1.4:-.7)*ramp(sx,sy,real);
    }
    private static double ramp(double col,double row,boolean real) {return real?.2+.01*col+.02*row:.3+.03*col-.01*row;}
    private static PredefinedImageFunctionTerm2D predefined(String name) throws Exception {
        return (PredefinedImageFunctionTerm2D)((DataGenerator)Class.forName("signals.functionterm."+name+"FunctionTerm2D").getDeclaredConstructor().newInstance()).getDefaultInstance();
    }
    private static FunctionPart2D part(FunctionTerm2D term) {return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());}
}
