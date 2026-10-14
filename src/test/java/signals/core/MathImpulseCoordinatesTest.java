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
import signals.operation.ScaleOp;

class MathImpulseCoordinatesTest extends MathAuditSupport {
    static Stream<Arguments> polarCases() {
        return Stream.of("Delta","DeltaPairEven","DeltaPairOdd").flatMap(kind->Stream.of(false,true).flatMap(angular->Stream.of(
                Arguments.of(kind,angular,32,16,true),Arguments.of(kind,angular,32,16,false),
                Arguments.of(kind,angular,16,32,true),Arguments.of(kind,angular,16,32,false))));
    }
    @ParameterizedTest
    @MethodSource("polarCases")
    void impulsesUsePhysicalRadiusOrAngleInComplexImageChains(String kind,boolean angular,int w,int h,boolean centered) throws Exception {
        double center=angular?90:kind.equals("Delta")?5:8,width=angular?90:3;
        AnalyticFunctionTerm1D realImpulse=impulse(kind,1.4,center,width),imagImpulse=impulse(kind,-.8,center,width);
        FunctionPart2D real=part(polar(realImpulse,angular)),imag=part(polar(imagImpulse,angular));
        RealImagFunction2D source=new RealImagFunction2D(real,imag,"polar impulses",w,h,centered);
        double[] er=new double[w*h],ei=new double[w*h],scaledR=new double[w*h],scaledI=new double[w*h];
        for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
            int k=row*w+col,px=x(col,w,centered),py=y(row,h,centered);
            double coordinate=angular?(px==0&&py==0?90:Math.toDegrees(Math.atan2(py,px))):Math.hypot(px,py);
            double pulse=value(kind,coordinate,center,width);
            er[k]=1.4*pulse;ei[k]=-.8*pulse;
            scaledR[k]=.5*er[k]-.8*ei[k];scaledI[k]=.8*er[k]+.5*ei[k];
        }
        assertTrue(java.util.Arrays.stream(er).anyMatch(v->v!=0),"fixture has visible impulses");
        assertImage(source,er,ei,w,h,centered);
        ScaleOp scale=new ScaleOp(new ParameterBlock());scale.setValue(0,.5);scale.setValue(1,.8);
        assertImage(unary(source,scale).getFunction(),scaledR,scaledI,w,h,centered);
    }
    static Stream<Arguments> linearCases() {
        return Stream.of("Delta","DeltaPairEven","DeltaPairOdd").flatMap(kind->Stream.of(
                Arguments.of(kind,64,true),Arguments.of(kind,64,false),Arguments.of(kind,128,true),Arguments.of(kind,128,false)));
    }
    @ParameterizedTest
    @MethodSource("linearCases")
    void ordinaryUnitSpacedSignalImpulsesKeepTheirLocations(String kind,int n,boolean centered) throws Exception {
        double center=8,width=3;
        double[] expected=new double[n];for(int k=0;k<n;k++)expected[k]=1.4*value(kind,x(k,n,centered),center,width);
        assertArrayEquals(expected,impulse(kind,1.4,center,width).create(n,centered),0);
    }
    private static double value(String kind,double coordinate,double center,double width) {
        if(kind.equals("Delta"))return coordinate==center?1:0;
        if(coordinate==center-width)return 1;
        if(coordinate==center+width)return kind.equals("DeltaPairOdd")?-1:1;
        return 0;
    }
    private static AnalyticFunctionTerm1D impulse(String kind,double amplitude,double center,double width) throws Exception {
        DataGenerator prototype=(DataGenerator)Class.forName("signals.functionterm."+kind+"FunctionTerm1D").getDeclaredConstructor().newInstance();
        AnalyticFunctionTerm1D term=(AnalyticFunctionTerm1D)prototype.getDefaultInstance();
        term.setAmplitude(amplitude);term.setCenter(center);if(term.hasWidth())term.setWidth(width);return term;
    }
    private static PolarFunctionTerm2D polar(AnalyticFunctionTerm1D impulse,boolean angular) {
        ConstantFunctionTerm1D one=(ConstantFunctionTerm1D)new ConstantFunctionTerm1D().getDefaultInstance();
        return new PolarFunctionTerm2D(new ParameterBlock().addSource(angular?one:impulse).addSource(angular?impulse:one));
    }
    private static FunctionPart2D part(FunctionTerm2D term) {
        return new FunctionPart2D(new ArrayList<>(List.of(term)),CombineTermsRule.getDefaultRule());
    }
}
