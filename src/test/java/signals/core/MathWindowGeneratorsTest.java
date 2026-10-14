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

class MathWindowGeneratorsTest extends MathAuditSupport {
    static Stream<Arguments> cases() {
        return Stream.of("Hamming","Hanning","Welch","Parzen").flatMap(kind->Stream.of(
                Arguments.of(kind,128,64,true),Arguments.of(kind,128,64,false),
                Arguments.of(kind,64,128,true),Arguments.of(kind,64,128,false)));
    }
    @ParameterizedTest
    @MethodSource("cases")
    void windowSamplesAndEditedComplexImageChainMatchIndependentBasis(String kind,int w,int h,boolean centered) throws Exception {
        AnalyticFunctionTerm1D xWindow=window(kind),yWindow=window(kind);
        xWindow.setAmplitude(1.4);yWindow.setAmplitude(-.8);
        double cx=centered?3:w/2.0,cy=centered?-2:h/2.0;
        xWindow.setCenter(cx);yWindow.setCenter(cy);yWindow.setWidth(12);
        ConstantFunctionTerm1D one=(ConstantFunctionTerm1D)new ConstantFunctionTerm1D().getDefaultInstance();
        XYFunctionTerm2D realTerm=new XYFunctionTerm2D(new ParameterBlock().addSource(xWindow).addSource(one));
        XYFunctionTerm2D imagTerm=new XYFunctionTerm2D(new ParameterBlock().addSource(one).addSource(yWindow));
        RealImagFunction2D source=new RealImagFunction2D(part(realTerm),part(imagTerm),"window channels",w,h,centered);
        OperatorSystem2D intensity=unary(source,new SquaredMagnitudeOp(new ParameterBlock()));
        for(double width:new double[]{16,9}) {
            xWindow.setWidth(width);source.setPartA(source.getPartA());
            double[] real=new double[w*h],imag=new double[w*h],power=new double[w*h];
            for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
                int k=row*w+col;
                real[k]=1.4*basis(kind,(x(col,w,centered)-cx)/width);
                imag[k]=-.8*basis(kind,(y(row,h,centered)-cy)/12);
                power[k]=real[k]*real[k]+imag[k]*imag[k];
            }
            assertImage(source,real,imag,w,h,centered);
            assertImage(intensity.getFunction(),power,new double[w*h],w,h,centered);
            double[] profile=xWindow.create(w,centered);
            for(int col=0;col<w;col++)assertEquals(real[col],profile[col],1e-8);
        }
    }
    private static double basis(String kind,double u) {
        if(Math.abs(u)>1)return 0;
        // Hann/Hamming as squared cosines; Welch as a beta polynomial.
        double cosine=Math.cos(Math.PI*u/2),t=(u+1)/2;
        if(kind.equals("Hanning"))return cosine*cosine;
        if(kind.equals("Hamming"))return .08+.92*cosine*cosine;
        if(kind.equals("Welch"))return 4*t*(1-t);
        // Parzen is a scaled cubic B-spline (four-box convolution).
        int[] coefficients={1,-4,6,-4,1};double spline=0;
        for(int k=0;k<5;k++)spline+=coefficients[k]*Math.pow(Math.max(0,2*u+2-k),3)/4;
        return spline;
    }
    private static AnalyticFunctionTerm1D window(String name) throws Exception {
        DataGenerator prototype=(DataGenerator)Class.forName("signals.functionterm."+name+"WindowFunctionTerm1D").getDeclaredConstructor().newInstance();
        return (AnalyticFunctionTerm1D)prototype.getDefaultInstance();
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
}
