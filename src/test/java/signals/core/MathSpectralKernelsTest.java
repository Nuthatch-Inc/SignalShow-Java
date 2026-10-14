package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.CepstrumOp;
import signals.operation.ComplementFilterOp;

/** Direct physical-coordinate sums, with ordinary asymmetric complex signals. */
class MathSpectralKernelsTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"32,1,true", "32,1,false", "64,1,true", "64,1,false", "16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void magnitudeCepstrumUsesInverseTransformAndPreservesComplexQuefrency(int w,int h,boolean centered) {
        double[][] input=fixture(w,h,centered),spectrum=dft(input,w,h,centered,false,1);
        double[][] log=new double[2][w*h];
        for(int k=0;k<w*h;k++) log[0][k]=Math.log(Math.hypot(spectrum[0][k],spectrum[1][k]));
        double[][] expected=dft(log,w,h,centered,true,w*h);
        assertTrue(java.util.Arrays.stream(expected[1]).anyMatch(v->Math.abs(v)>.01),"fixture distinguishes transform direction");
        Function source=source(input,w,h,centered);
        assertSamples(new CepstrumOp(new ParameterBlock()).create(source),expected,w,h,centered);
        assertSamples(source,input,w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,1,true", "32,1,false", "64,1,true", "64,1,false", "16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void inverseCepstrumReconstructsTheMagnitudeSpectrumWithZeroPhase(int w,int h,boolean centered) {
        double[][] spectrum=dft(fixture(w,h,centered),w,h,centered,false,1);
        double[][] log=new double[2][w*h],magnitude=new double[2][w*h];
        for(int k=0;k<w*h;k++) {magnitude[0][k]=Math.hypot(spectrum[0][k],spectrum[1][k]);log[0][k]=Math.log(magnitude[0][k]);}
        double[][] cepstrum=dft(log,w,h,centered,true,w*h);
        CepstrumOp op=new CepstrumOp(new ParameterBlock());op.setSelected(true);
        Function source=source(cepstrum,w,h,centered);
        assertSamples(op.create(source),dft(magnitude,w,h,centered,true,w*h),w,h,centered);
        assertSamples(source,cepstrum,w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,1,true", "32,1,false", "64,1,true", "64,1,false", "16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void complementKernelMatchesTheFiniteInverseFilterSeries(int w,int h,boolean centered) {
        double[][] input=fixture(w,h,centered),spectrum=dft(input,w,h,centered,false,Math.sqrt(w*h));
        double peak=0;for(int k=0;k<w*h;k++)peak=Math.max(peak,Math.hypot(spectrum[0][k],spectrum[1][k]));
        Function source=source(input,w,h,centered);
        ComplementFilterOp op=new ComplementFilterOp(new ParameterBlock());
        for(int order:new int[]{1,2,5}) {
            double[][] filter=new double[2][w*h];
            for(int k=0;k<w*h;k++) {
                double mag=Math.hypot(spectrum[0][k],spectrum[1][k]),gain=0;
                for(int power=0;power<=order;power++)gain+=Math.pow(1-mag/peak,power);
                filter[0][k]=gain*spectrum[0][k]/mag;filter[1][k]=-gain*spectrum[1][k]/mag;
            }
            op.setValue(0,order);
            assertSamples(op.create(source),dft(filter,w,h,centered,true,Math.sqrt(w*h)),w,h,centered);
        }
        assertSamples(source,input,w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void forwardAndInverseCepstrumChainKeepsTheOriginalSpectrumMagnitude(int w,int h,boolean centered) {
        double[][] input=fixture(w,h,centered),spectrum=dft(input,w,h,centered,false,1);
        double[][] magnitude=new double[2][w*h];
        for(int k=0;k<w*h;k++)magnitude[0][k]=Math.hypot(spectrum[0][k],spectrum[1][k]);
        CepstrumOp inverse=new CepstrumOp(new ParameterBlock());inverse.setSelected(true);
        OperatorSystem2D chain=unary(unary(source(input,w,h,centered),new CepstrumOp(new ParameterBlock())),inverse);
        assertSamples(chain.getFunction(),dft(magnitude,w,h,centered,true,w*h),w,h,centered);
    }

    private static double[][] fixture(int w,int h,boolean centered) {
        double[][] input=new double[2][w*h];
        int origin=index(0,0,w,h,centered),a=index(2,h==1?0:1,w,h,centered),b=index(-1,h==1?0:2,w,h,centered);
        input[0][origin]=2;input[1][origin]=.7;
        input[0][a]=.5;input[1][a]=-.3;input[0][b]=-.2;input[1][b]=.4;
        return input;
    }
    private static Function source(double[][] a,int w,int h,boolean centered) {
        return h==1?FunctionFactory.createFunction1D(a[0].clone(),a[1].clone(),centered,"kernel fixture"):image(a[0],a[1],w,h,centered);
    }
    private static void assertSamples(Function actual,double[][] a,int w,int h,boolean centered) {
        int largest=0;for(int k=1;k<a[1].length;k++)if(Math.abs(a[1][k])>Math.abs(a[1][largest]))largest=k;
        assertEquals(a[1][largest],actual.getImaginary()[largest],1e-8,"largest imaginary sample");
        if(h>1)assertImage(actual,a[0],a[1],w,h,centered);
        else {assertInstanceOf(Function1D.class,actual);assertEquals(centered,actual.isZeroCentered());
            assertArrayEquals(a[0],actual.getReal(),1e-8);assertArrayEquals(a[1],actual.getImaginary(),1e-8);}
    }
    private static double[][] dft(double[][] a,int w,int h,boolean centered,boolean inverse,double divisor) {
        double[][] out=new double[2][w*h];
        for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
            int k=row*w+col;double u=x(col,w,centered),v=y(row,h,centered);
            for(int sr=0;sr<h;sr++)for(int sc=0;sc<w;sc++) {
                int j=sr*w+sc;
                double angle=(inverse?2:-2)*Math.PI*(u*x(sc,w,centered)/w+v*y(sr,h,centered)/h);
                out[0][k]+=(a[0][j]*Math.cos(angle)-a[1][j]*Math.sin(angle))/divisor;
                out[1][k]+=(a[0][j]*Math.sin(angle)+a[1][j]*Math.cos(angle))/divisor;
            }
        }
        return out;
    }
}
