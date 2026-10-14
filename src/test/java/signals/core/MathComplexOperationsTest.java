package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.*;

class MathComplexOperationsTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"false,true", "false,false", "true,true", "true,false"})
    void pointwiseOperatorsUseBothChannelsAndExplicitExtractionReturnsRealValues(boolean image,boolean centered) {
        int w=32,h=image?16:1;
        double[] real=new double[w*h],imag=new double[w*h];
        for(int i=0;i<real.length;i++) {
            // Nonzero values in all quadrants, ordinary amplitudes, no branch-cut or overflow cases.
            real[i]=(i%2==0?1:-1)*(.4+.1*(i%7));
            imag[i]=(i%3==0?1:-1)*(.3+.15*(i%5));
        }
        Function input=source(real,imag,w,h,image,centered);
        for(String kind:List.of("scale","offset","conjugate","negative","reciprocal","magnitude","intensity","phase","real","absolute","sign","mean","clip","threshold","square","complex-power")) {
            UnaryOperation op=operation(kind);
            double[] er=new double[real.length],ei=new double[real.length];
            double mr=0,mi=0;for(int i=0;i<real.length;i++){mr+=real[i]/real.length;mi+=imag[i]/imag.length;}
            for(int i=0;i<real.length;i++) {
                double a=real[i],b=imag[i],radius=Math.hypot(a,b),angle=Math.atan2(b,a);
                switch(kind) {
                    case "scale" -> {er[i]=.75*a+.5*b;ei[i]=.75*b-.5*a;}
                    case "offset" -> {er[i]=a-.6;ei[i]=b+1.1;}
                    case "conjugate" -> {er[i]=a;ei[i]=-b;}
                    case "negative" -> {er[i]=-a;ei[i]=-b;}
                    case "reciprocal" -> {er[i]=a/(a*a+b*b);ei[i]=-b/(a*a+b*b);}
                    case "magnitude" -> er[i]=radius;
                    case "intensity" -> er[i]=a*a+b*b;
                    case "phase" -> er[i]=angle;
                    case "real" -> er[i]=a;
                    case "absolute" -> {er[i]=Math.abs(a);ei[i]=Math.abs(b);}
                    case "sign" -> {er[i]=Math.signum(a);ei[i]=Math.signum(b);}
                    case "mean" -> {er[i]=a-mr;ei[i]=b-mi;}
                    case "clip" -> {er[i]=Math.min(.65,Math.max(-.55,a));ei[i]=Math.min(.65,Math.max(-.55,b));}
                    case "threshold" -> {er[i]=a>=.2?2:-1;ei[i]=b>=.2?2:-1;}
                    case "square" -> {er[i]=a*a-b*b;ei[i]=2*a*b;}
                    case "complex-power" -> {
                        double magnitude=Math.pow(radius,.5)*Math.exp(-.25*angle),phase=.5*angle+.25*Math.log(radius);
                        er[i]=magnitude*Math.cos(phase);ei[i]=magnitude*Math.sin(phase);
                    }
                }
            }
            Function result=op.create(input);
            assertSamples(kind,result,er,ei,w,h,image,centered);
            result.freeMemory();assertSamples(kind+" regenerated",result,er,ei,w,h,image,centered);
            assertSamples("unchanged input",input,real,imag,w,h,image,centered);
        }
        // Preserve the documented componentwise square root on ordinary nonnegative channels.
        Function positive=new AbsoluteValueOp(new ParameterBlock()).create(input);
        Function root=new SquareRootOp(new ParameterBlock()).create(positive);
        for(int i=0;i<real.length;i++) {
            assertEquals(Math.sqrt(Math.abs(real[i])),root.getReal()[i],1e-9);
            assertEquals(Math.sqrt(Math.abs(imag[i])),root.getImaginary()[i],1e-9);
        }
    }

    @ParameterizedTest
    @CsvSource({"32,16,true", "32,16,false", "16,32,true", "16,32,false"})
    void groupedArithmeticAndEditedUnaryChainRetainComplexValues(int w,int h,boolean centered) {
        double[] ar=new double[w*h],ai=new double[w*h],br=new double[w*h],bi=new double[w*h],cr=new double[w*h],ci=new double[w*h];
        for(int i=0;i<ar.length;i++) {
            ar[i]=.4+.05*(i%11);ai[i]=-.7+.1*(i%7);
            br[i]=1.2-.1*(i%9);bi[i]=.2+.07*(i%5);
            cr[i]=1.4+.1*(i%3);ci[i]=-.3+.08*(i%7);
        }
        Function2D a=image(ar,ai,w,h,centered),b=image(br,bi,w,h,centered),c=image(cr,ci,w,h,centered);
        ArrayList<Operation> ops=new ArrayList<>(List.of(new MinusOp(new ParameterBlock()),new DivideOp(new ParameterBlock())));
        int[] grouped={LEFT_PAREN,FUNCTION,BINARY_OP_TIER_1,FUNCTION,RIGHT_PAREN,BINARY_OP_TIER_2,FUNCTION};
        OperatorSystem2D divided=new OperatorSystem2D(new CombineOpsRule(grouped,ops),new ArrayList<>(List.of(a,b,c)),ops);
        ScaleOp scale=new ScaleOp(new ParameterBlock());
        OperatorSystem2D scaled=unary(divided,scale),conjugated=unary(scaled,new ConjugateOp(new ParameterBlock()));
        OperatorSystem2D intensity=unary(conjugated,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double[] factor:new double[][]{{.75,-.5},{-1.25,.4},{.2,1.1}}) {
            scale.setValue(0,factor[0]);scale.setValue(1,factor[1]);scaled.freeMemory();
            double[] qr=new double[w*h],qi=new double[w*h],er=new double[w*h],ei=new double[w*h],power=new double[w*h];
            for(int i=0;i<qr.length;i++) {
                double rr=ar[i]-br[i],ri=ai[i]-bi[i],den=cr[i]*cr[i]+ci[i]*ci[i];
                qr[i]=(rr*cr[i]+ri*ci[i])/den;qi[i]=(ri*cr[i]-rr*ci[i])/den;
                er[i]=qr[i]*factor[0]-qi[i]*factor[1];ei[i]=-(qi[i]*factor[0]+qr[i]*factor[1]);
                power[i]=er[i]*er[i]+ei[i]*ei[i];
            }
            assertImage(divided.getFunction(),qr,qi,w,h,centered);
            assertImage(conjugated.getFunction(),er,ei,w,h,centered);
            assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
        }
    }

    private static UnaryOperation operation(String kind) {
        ParameterBlock pb=new ParameterBlock();
        return switch(kind) {
            case "scale" -> {ScaleOp op=new ScaleOp(pb);op.setValue(0,.75);op.setValue(1,-.5);yield op;}
            case "offset" -> {OffsetOp op=new OffsetOp(pb);op.setValue(0,-.6);op.setValue(1,1.1);yield op;}
            case "conjugate" -> new ConjugateOp(pb);
            case "negative" -> new NegateOp(pb);
            case "reciprocal" -> new InvertOp(pb);
            case "magnitude" -> new MagnitudeOp(pb);
            case "intensity" -> new SquaredMagnitudeOp(pb);
            case "phase" -> new PhaseOp(pb);
            case "real" -> new RealOp(pb);
            case "absolute" -> new AbsoluteValueOp(pb);
            case "sign" -> new SignumOp(pb);
            case "mean" -> new SubtractMeanOp(pb);
            case "clip" -> {ClipOp op=new ClipOp(pb);op.setValue(0,-.55);op.setValue(1,.65);yield op;}
            case "threshold" -> {ThresholdOp op=new ThresholdOp(pb);op.setValue(0,2);op.setValue(1,-1);op.setValue(2,.2);yield op;}
            case "square", "complex-power" -> {ExponentOp op=new ExponentOp(pb);op.setValue(0,kind.equals("square")?2:.5);op.setValue(1,kind.equals("square")?0:.25);yield op;}
            default -> throw new AssertionError(kind);
        };
    }
    private static Function source(double[] real,double[] imag,int w,int h,boolean image,boolean centered) {
        return image?image(real,imag,w,h,centered):FunctionFactory.createFunction1D(real.clone(),imag.clone(),centered,"complex fixture");
    }
    private static void assertSamples(String context,Function actual,double[] real,double[] imag,int w,int h,boolean image,boolean centered) {
        assertArrayEquals(real,actual.getReal(),1e-8,context+" real");
        assertArrayEquals(imag,actual.getImaginary(),1e-8,context+" imaginary");
        assertEquals(centered,actual.isZeroCentered());
        if(image) {assertEquals(w,((Function2D)actual).getDimensionX());assertEquals(h,((Function2D)actual).getDimensionY());}
    }
}
