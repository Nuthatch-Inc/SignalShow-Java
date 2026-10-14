package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.*;
import signals.operation.*;

class MathGenerators2DTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
    void separableImageFamiliesUseCorrectCoordinatesCentersAndAmplitudes(int w,int h,boolean centered) {
        for(String kind:List.of("gaussian","sine","cosine","line","triangle","rectangle","sinc","chirp")) {
            AnalyticFunctionTerm1D horizontal=term(kind,1.7,3,11,25);
            AnalyticFunctionTerm1D vertical=term("gaussian",.8,-2,9,0);
            Function2D source=new RealImagFunction2D(part(xy(horizontal,vertical)),
                    part(xy(term("sine",.6,-4,17,-35),term("gaussian",1.2,5,13,0))),"Cartesian fixture",w,h,centered);
            double[] real=new double[w*h],imag=new double[w*h];
            for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
                double px=x(col,w,centered),py=y(row,h,centered);int i=row*w+col;
                real[i]=value(kind,px,1.7,3,11,25)*value("gaussian",py,.8,-2,9,0);
                imag[i]=value("sine",px,.6,-4,17,-35)*value("gaussian",py,1.2,5,13,0);
            }
            assertImage(source,real,imag,w,h,centered);
            Function magnitude=new MagnitudeOp(new ParameterBlock()).create(source);
            for(int i=0;i<real.length;i++)assertEquals(Math.hypot(real[i],imag[i]),magnitude.getReal()[i],1e-9,kind+" magnitude");
            assertArrayEquals(new double[w*h],magnitude.getImaginary(),0);
            source.freeMemory();assertImage(source,real,imag,w,h,centered);
        }
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
    void polarDefinitionsAndPhaseEditsProduceBothCartesianChannels(int w,int h,boolean centered) {
        AnalyticFunctionTerm1D phaseX=term("cosine",2.4,3,24,25);
        FunctionPart2D magnitudePart=part(xy(term("gaussian",1.5,-2,17,0),term("gaussian",.8,4,13,0)));
        FunctionPart2D phasePart=part(xy(phaseX,term("sine",1,-3,18,-35)));
        Function2D source=new MagPhaseFunction2D(magnitudePart,phasePart,"polar definition",w,h,centered);
        ScaleOp rotatePhase=new ScaleOp(new ParameterBlock());rotatePhase.setValue(0,0);rotatePhase.setValue(1,1);
        OperatorSystem2D rotated=unary(source,rotatePhase),realOnly=unary(rotated,new RealOp(new ParameterBlock()));
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double degrees:new double[]{25,-40,70}) {
            ((CosineFunctionTerm1D)phaseX).setInitialPhase(degrees);source.setPartB(phasePart);
            double[] real=new double[w*h],imag=new double[w*h],rotatedReal=new double[w*h],power=new double[w*h];
            for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
                double px=x(col,w,centered),py=y(row,h,centered);int i=row*w+col;
                double magnitude=value("gaussian",px,1.5,-2,17,0)*value("gaussian",py,.8,4,13,0);
                double phase=value("cosine",px,2.4,3,24,degrees)*value("sine",py,1,-3,18,-35);
                real[i]=magnitude*Math.cos(phase);imag[i]=magnitude*Math.sin(phase);
                rotatedReal[i]=-imag[i];power[i]=magnitude*magnitude;
            }
            assertImage(source,real,imag,w,h,centered);
            assertImage(rotated.getFunction(),rotatedReal,real,w,h,centered);
            assertImage(realOnly.getFunction(),rotatedReal,new double[w*h],w,h,centered);
            assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
        }
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
    void circularAndAnnularAperturesHonorTheirAuthoredCentersAndRadii(int w,int h,boolean centered) {
        CylinderFunctionTerm2D disk=(CylinderFunctionTerm2D)new CylinderFunctionTerm2D().getDefaultInstance();
        CassegrainFunctionTerm2D ring=(CassegrainFunctionTerm2D)new CassegrainFunctionTerm2D().getDefaultInstance();
        disk.setAmplitude(1.7);disk.setWidth(9);
        ring.setAmplitude(-.6);ring.setWidth(13);ring.setHeight(5);
        Function2D source=new RealImagFunction2D(part(disk),part(ring),"apertures",w,h,centered);
        for(int[] center:new int[][]{{0,0},{3,5},{-4,-2}}) {
            int cx=center[0]+(centered?0:20),cy=center[1]+(centered?0:20);
            disk.setCenterX(cx);disk.setCenterY(cy);ring.setCenterX(cx);ring.setCenterY(cy);
            source.setPartA(source.getPartA());source.setPartB(source.getPartB());
            double[] real=new double[w*h],imag=new double[w*h];
            for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
                double radius=Math.hypot(x(col,w,centered)-cx,y(row,h,centered)-cy);int i=row*w+col;
                real[i]=radius<=9?1.7:0;imag[i]=radius>=5&&radius<=13?-.6:0;
            }
            assertImage(source,real,imag,w,h,centered);
            Function power=new SquaredMagnitudeOp(new ParameterBlock()).create(source);
            for(int i=0;i<real.length;i++)assertEquals(real[i]*real[i]+imag[i]*imag[i],power.getReal()[i],1e-9);
        }
    }

    private static AnalyticFunctionTerm1D term(String kind,double amplitude,double center,double width,double phase) {
        AnalyticFunctionTerm1D prototype=switch(kind) {
            case "gaussian" -> new GaussianFunctionTerm1D();
            case "sine" -> new SineFunctionTerm1D();
            case "cosine" -> new CosineFunctionTerm1D();
            case "line" -> new LineFunctionTerm1D();
            case "triangle" -> new TriangleFunctionTerm1D();
            case "rectangle" -> new RectangleFunctionTerm1D();
            case "sinc" -> new SincFunctionTerm1D();
            case "chirp" -> new ChirpFunctionTerm1D();
            default -> throw new AssertionError(kind);
        };
        AnalyticFunctionTerm1D result=(AnalyticFunctionTerm1D)prototype.getDefaultInstance();
        result.setAmplitude(amplitude);result.setCenter(center);result.setWidth(width);
        if(result instanceof CosineFunctionTerm1D sinusoid)sinusoid.setInitialPhase(phase);
        if(result instanceof ChirpFunctionTerm1D chirp){chirp.setInitialPhase(phase);chirp.setOrder(2);}
        return result;
    }
    private static double value(String kind,double coordinate,double amplitude,double center,double width,double phase) {
        double t=(coordinate-center)/width;
        return amplitude*switch(kind) {
            case "gaussian" -> Math.exp(-Math.PI*t*t);
            case "sine" -> Math.sin(2*Math.PI*t+Math.toRadians(phase));
            case "cosine" -> Math.cos(2*Math.PI*t+Math.toRadians(phase));
            case "line" -> t;
            case "triangle" -> Math.max(0,1-Math.abs(t));
            case "rectangle" -> Math.abs(t)<1?1:Math.abs(t)==1?.5:0;
            case "sinc" -> t==0?1:Math.sin(Math.PI*t)/(Math.PI*t);
            case "chirp" -> Math.cos(Math.PI*t*t+Math.toRadians(phase));
            default -> throw new AssertionError(kind);
        };
    }
    private static XYFunctionTerm2D xy(FunctionTerm1D horizontal,FunctionTerm1D vertical) {
        return new XYFunctionTerm2D(new ParameterBlock().addSource(horizontal).addSource(vertical));
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
}
