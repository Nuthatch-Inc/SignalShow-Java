package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.*;

class MathCoordinates1DTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"32,true", "32,false", "64,true", "64,false"})
    void reverseReflectsPhysicalCoordinatesAndPreservesTheOrigin(int n,boolean centered) {
        double[][] samples=samples(n,centered);
        Function source=FunctionFactory.createFunction1D(samples[0].clone(),samples[1].clone(),centered,"asymmetric polynomial");
        OperatorSystem1D reversed=chain(source,new ReverseOp1D(new ParameterBlock()));
        double[][] expected=new double[2][n];
        for(int col=0;col<n;col++) {
            int sourceCol=Math.floorMod(-x(col,n,centered)+(centered?n/2:0),n);
            for(int channel=0;channel<2;channel++) expected[channel][col]=samples[channel][sourceCol];
        }
        assertSignal(reversed.getFunction(),expected,centered);
        assertSignal(chain(reversed,new ReverseOp1D(new ParameterBlock())).getFunction(),samples,centered);
        assertSignal(source,samples,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,true", "32,false", "64,true", "64,false"})
    void ordinaryTranslationEditsFeedComplexScaleWithAndWithoutWrap(int n,boolean centered) {
        double[][] samples=samples(n,centered);
        Function source=FunctionFactory.createFunction1D(samples[0].clone(),samples[1].clone(),centered,"translate fixture");
        TranslateOp1D op=new TranslateOp1D(new ParameterBlock());
        OperatorSystem1D translated=chain(source,op);
        ScaleOp scale=new ScaleOp(new ParameterBlock());scale.setValue(0,.6);scale.setValue(1,-.4);
        OperatorSystem1D scaled=chain(translated,scale);
        for(boolean wrap:new boolean[]{false,true}) for(int shift:new int[]{3,-5,7}) {
            op.setWrapAround(wrap);op.setShiftAmount(shift);translated.freeMemory();scaled.freeMemory();
            double[][] expected=new double[2][n],product=new double[2][n];
            for(int col=0;col<n;col++) {
                int sourceCol=col-shift;
                if(wrap) sourceCol=Math.floorMod(sourceCol,n);
                if(sourceCol>=0 && sourceCol<n) for(int ch=0;ch<2;ch++) expected[ch][col]=samples[ch][sourceCol];
                product[0][col]=.6*expected[0][col]+.4*expected[1][col];
                product[1][col]=.6*expected[1][col]-.4*expected[0][col];
            }
            assertSignal(translated.getFunction(),expected,centered);
            assertSignal(scaled.getFunction(),product,centered);
            assertSignal(source,samples,centered);
        }
    }

    @ParameterizedTest
    @CsvSource({"32,true", "32,false", "64,true", "64,false"})
    void derivativeUsesIncreasingXAndIntegralRecoversBothChannelsUpToConstant(int n,boolean centered) {
        double[][] samples=samples(n,centered),expected=new double[2][n],recovered=new double[2][n];
        Function source=FunctionFactory.createFunction1D(samples[0].clone(),samples[1].clone(),centered,"derivative fixture");
        for(int col=1;col<n;col++) {
            expected[0][col]=.5*x(col,n,centered)+.25;
            expected[1][col]=-.7;
            for(int ch=0;ch<2;ch++) recovered[ch][col]=samples[ch][col]-samples[ch][0];
        }
        OperatorSystem1D derivative=chain(source,new DerivativeOp1D(new ParameterBlock()));
        assertSignal(derivative.getFunction(),expected,centered);
        assertSignal(chain(derivative,new IntegralOp1D(new ParameterBlock())).getFunction(),recovered,centered);
        assertSignal(source,samples,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,true", "32,false", "64,true", "64,false"})
    void integralMatchesClosedFormPolynomialSumsAndDerivativeRecoversSamples(int n,boolean centered) {
        double[][] samples=samples(n,centered),expected=new double[2][n],recovered={samples[0].clone(),samples[1].clone()};
        Function source=FunctionFactory.createFunction1D(samples[0].clone(),samples[1].clone(),centered,"integral fixture");
        double start=x(0,n,centered);
        for(int col=0;col<n;col++) {
            double count=col+1,sumIndex=col*count/2,sumSquare=col*count*(2*col+1)/6;
            // Closed-form sums of x and x squared over the displayed coordinate interval.
            double sumX=count*start+sumIndex,sumX2=count*start*start+2*start*sumIndex+sumSquare;
            expected[0][col]=.25*sumX2+.5*sumX-count;
            expected[1][col]=-.7*sumX+2*count;
        }
        recovered[0][0]=0;recovered[1][0]=0; // The established derivative uses zero at its left boundary.
        OperatorSystem1D integral=chain(source,new IntegralOp1D(new ParameterBlock()));
        assertSignal(integral.getFunction(),expected,centered);
        assertSignal(chain(integral,new DerivativeOp1D(new ParameterBlock())).getFunction(),recovered,centered);
        assertSignal(source,samples,centered);
    }

    private static double[][] samples(int n,boolean centered) {
        double[][] values=new double[2][n];
        for(int col=0;col<n;col++) {double x=x(col,n,centered);values[0][col]=.25*x*x+.5*x-1;values[1][col]=-.7*x+2;}
        return values;
    }
    private static OperatorSystem1D chain(FunctionProducer source,UnaryOperation operation) {
        ArrayList<Operation> ops=new ArrayList<>(List.of(operation));
        return new OperatorSystem1D(new CombineOpsRule(new int[]{operation.getPriority(),CombineOpsRule.FUNCTION},ops),
                new ArrayList<>(List.of(source)),ops);
    }
    private static void assertSignal(Function actual,double[][] expected,boolean centered) {
        assertInstanceOf(Function1D.class,actual);assertEquals(centered,actual.isZeroCentered());
        assertArrayEquals(expected[0],actual.getReal(),1e-8,"real channel");
        assertArrayEquals(expected[1],actual.getImaginary(),1e-8,"imaginary channel");
    }
}
