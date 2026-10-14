package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.functionterm.*;
import signals.operation.SquaredMagnitudeOp;

/** Ordinary generators at visible scales; tolerance intentionally ignores tiny approximation errors. */
class MathAnalyticGeneratorsTest extends MathAuditSupport {
    static Stream<Arguments> cases() {
        return Stream.of("Besinc","Bessel0","Bessel1","Bessel2","Bessel5","Lorentzian","Monomial","AbsMonomial",
                "SincSquared","SquareWave","Stairstep","Step","StepExponential","Signum","Comb","DoubleSlit","Constant","Zero")
                .flatMap(kind->Stream.of(Arguments.of(kind,64,32,true),Arguments.of(kind,64,32,false),
                        Arguments.of(kind,32,64,true),Arguments.of(kind,32,64,false)));
    }
    @ParameterizedTest
    @MethodSource("cases")
    void ordinaryProfilesKeepTheirMeaningInBothComplexImageChannels(String kind,int w,int h,boolean centered) throws Exception {
        // Order 5's legacy recurrence has about 4.6e-5 error near zero on this grid.
        // Keep this a major-error check; numerical refinement is explicitly deferred.
        double tolerance=kind.equals("Bessel5")?1e-4:1e-5;
        double cx=(centered?0:w/2.0)+3,cy=(centered?0:h/2.0)-2,width=8;
        FunctionTerm1D xt=term(kind,1.4,cx,width),yt=term(kind,-.8,cy,width);
        ConstantFunctionTerm1D one=(ConstantFunctionTerm1D)new ConstantFunctionTerm1D().getDefaultInstance();
        RealImagFunction2D source=new RealImagFunction2D(
                part(new XYFunctionTerm2D(new ParameterBlock().addSource(xt).addSource(one))),
                part(new XYFunctionTerm2D(new ParameterBlock().addSource(one).addSource(yt))),"analytic channels",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double amplitude:new double[]{1.4,-1.2}) {
            if(xt instanceof AnalyticFunctionTerm1D analytic&&analytic.hasAmplitude())analytic.setAmplitude(amplitude);
            source.setPartA(source.getPartA());
            double[] real=new double[w*h],imag=new double[w*h],power=new double[w*h];
            double[] xp=new double[w],yp=new double[h];
            for(int col=0;col<w;col++)xp[col]=amplitude*basis(kind,x(col,w,centered)-cx,width);
            for(int row=0;row<h;row++)yp[row]=-.8*basis(kind,y(row,h,centered)-cy,width);
            assertArrayEquals(xp,xt.create(w,centered),tolerance,"1D profile");
            for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
                int k=row*w+col;real[k]=xp[col];imag[k]=yp[row];power[k]=real[k]*real[k]+imag[k]*imag[k];
            }
            assertEquals(w,source.getDimensionX());assertEquals(h,source.getDimensionY());assertEquals(centered,source.isZeroCentered());
            assertArrayEquals(real,source.getReal(),tolerance);assertArrayEquals(imag,source.getImaginary(),tolerance);
            assertArrayEquals(power,intensity.getFunction().getReal(),1e-5);
            assertArrayEquals(new double[w*h],intensity.getFunction().getImaginary(),1e-5);
        }
    }
    private static FunctionTerm1D term(String kind,double amplitude,double center,double width) throws Exception {
        String name=kind.startsWith("Bessel")?"Bessel":kind;
        DataGenerator prototype=(DataGenerator)Class.forName("signals.functionterm."+name+"FunctionTerm1D").getDeclaredConstructor().newInstance();
        FunctionTerm1D result=(FunctionTerm1D)prototype.getDefaultInstance();
        if(result instanceof AnalyticFunctionTerm1D term) {
            if(term.hasAmplitude())term.setAmplitude(amplitude);
            if(term.hasCenter())term.setCenter(center);
            if(term.hasWidth())term.setWidth(width);
        }
        if(kind.startsWith("Bessel"))result.setParameter(Integer.parseInt(kind.substring(6)),3);
        if(kind.equals("Monomial")||kind.equals("AbsMonomial"))result.setParameter(3,3);
        if(kind.equals("SquareWave"))result.setParameter(21.0,3);
        if(result instanceof DoubleSlitFunctionTerm1D slits)slits.setSlitWidth(4);
        return result;
    }
    private static double basis(String kind,double offset,double width) {
        double u=offset/width;
        if(kind.startsWith("Bessel"))return bessel(Integer.parseInt(kind.substring(6)),Math.PI*Math.abs(u));
        return switch(kind) {
            case "Besinc" -> u==0?1:2*bessel(1,Math.PI*Math.abs(u))/(Math.PI*Math.abs(u));
            case "Lorentzian" -> 1/(1+4*Math.PI*Math.PI*u*u);
            case "Monomial" -> u*u*u;
            case "AbsMonomial" -> Math.abs(u*u*u);
            case "SincSquared" -> u==0?1:Math.pow(Math.sin(Math.PI*u)/(Math.PI*u),2);
            case "SquareWave" -> Math.signum(Math.cos(2*Math.PI*u+21*Math.PI/180));
            case "Stairstep" -> Math.floor((u+1)/2);
            case "Step" -> offset==0?.5:offset>0?1:0;
            case "StepExponential" -> offset==0?.5:offset>0?Math.exp(-u):0;
            case "Signum" -> Math.signum(offset);
            case "Comb" -> offset%width==0?1:0;
            case "DoubleSlit" -> Math.abs(offset-width)<=2||Math.abs(offset+width)<=2?1:0;
            case "Constant" -> 1;
            case "Zero" -> 0;
            default -> throw new AssertionError(kind);
        };
    }
    private static double bessel(int order,double z) {
        // Independent integral representation (NIST DLMF 10.9.2), not the production recurrence.
        int steps=512;double sum=0;
        for(int k=0;k<=steps;k++) {
            double angle=Math.PI*k/steps;
            sum+=(k==0||k==steps?.5:1)*Math.cos(z*Math.sin(angle)-order*angle)/steps;
        }
        return sum;
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
}
