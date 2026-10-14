package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.*;

/** Direct sums use physical coordinates, not the FFT or test copies of its orchestration. */
class MathSpectralOperationsTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void actualFourierOperationMatchesDirectSumsInBothDirections(int w,int h,boolean centered) {
        double[][] a=fixture(w,h,centered,0);
        Function2D source=image(a[0],a[1],w,h,centered);
        FourierTransformOp op=new FourierTransformOp(new ParameterBlock());
        for(boolean inverse:new boolean[]{false,true}) for(int norm:new int[]{Transforms.NORMALIZE_NONE,Transforms.NORMALIZE_N,Transforms.NORMALIZE_ROOT_N}) {
            op.setInverse(inverse);op.setNormalization(norm);
            double divisor=norm==Transforms.NORMALIZE_NONE?1:norm==Transforms.NORMALIZE_N?w*h:Math.sqrt(w*h);
            double[][] expected=dft(a,w,h,centered,inverse,divisor);
            assertImage(op.create(source),expected[0],expected[1],w,h,centered);
        }
        assertImage(source,a[0],a[1],w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
    void originImpulseHasConstantSpectrumAndComplexPlaneWaveHasCorrectFrequency(int w,int h,boolean centered) {
        double[] real=new double[w*h],imag=new double[w*h];
        int origin=index(0,0,w,h,centered);real[origin]=2;imag[origin]=-3;
        FourierTransformOp op=new FourierTransformOp(new ParameterBlock());op.setNormalization(Transforms.NORMALIZE_NONE);
        Function result=op.create(image(real,imag,w,h,centered));
        for(int k=0;k<real.length;k++) { assertEquals(2,result.getReal()[k],1e-8); assertEquals(-3,result.getImaginary()[k],1e-8); }
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            double angle=2*Math.PI*(3.0*x(col,w,centered)/w-2.0*y(row,h,centered)/h);
            real[row*w+col]=Math.cos(angle);imag[row*w+col]=Math.sin(angle);
        }
        result=op.create(image(real,imag,w,h,centered));
        int peak=index(3,-2,w,h,centered);
        for(int k=0;k<real.length;k++) { assertEquals(k==peak?w*h:0,result.getReal()[k],1e-8);assertEquals(0,result.getImaginary()[k],1e-8); }
    }

    @ParameterizedTest
    @CsvSource({"16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void productionConvolutionAndCorrelationMatchDirectSpatialSums(int w,int h,boolean centered) {
        double[][] a=fixture(w,h,centered,0),b=fixture(w,h,centered,1);
        Function2D left=image(a[0],a[1],w,h,centered),right=image(b[0],b[1],w,h,centered);
        double[][] expected=spatialSum(a,b,w,h,centered,false);
        assertImage(new ConvolveOp(new ParameterBlock()).create(left,right),expected[0],expected[1],w,h,centered);
        expected=spatialSum(a,b,w,h,centered,true);
        assertImage(new CorrelateOp(new ParameterBlock()).create(left,right),expected[0],expected[1],w,h,centered);
        expected=spatialSum(a,a,w,h,centered,true);
        Function autocorrelation=new AutoCorrelateOp(new ParameterBlock()).create(left);
        assertImage(autocorrelation,expected[0],expected[1],w,h,centered);
        double energy=0;for(int k=0;k<w*h;k++)energy+=a[0][k]*a[0][k]+a[1][k]*a[1][k];
        int origin=index(0,0,w,h,centered);
        assertEquals(energy,autocorrelation.getReal()[origin],1e-8);
        assertEquals(0,autocorrelation.getImaginary()[origin],1e-8);
        assertImage(left,a[0],a[1],w,h,centered);assertImage(right,b[0],b[1],w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,true", "32,false", "64,true", "64,false"})
    void oneDimensionalProductionTransformsAndSpatialOperationsMatchDirectSums(int n,boolean centered) {
        double[][] a=fixture(n,1,centered,0),b=fixture(n,1,centered,1);
        Function left=FunctionFactory.createFunction1D(a[0].clone(),a[1].clone(),centered,"left");
        Function right=FunctionFactory.createFunction1D(b[0].clone(),b[1].clone(),centered,"right");
        FourierTransformOp fft=new FourierTransformOp(new ParameterBlock());
        for(boolean inverse:new boolean[]{false,true}) for(int norm:new int[]{Transforms.NORMALIZE_NONE,Transforms.NORMALIZE_N,Transforms.NORMALIZE_ROOT_N}) {
            fft.setInverse(inverse);fft.setNormalization(norm);
            double divisor=norm==Transforms.NORMALIZE_NONE?1:norm==Transforms.NORMALIZE_N?n:Math.sqrt(n);
            assertSignal(fft.create(left),dft(a,n,1,centered,inverse,divisor),centered);
        }
        assertSignal(new ConvolveOp(new ParameterBlock()).create(left,right),spatialSum(a,b,n,1,centered,false),centered);
        assertSignal(new CorrelateOp(new ParameterBlock()).create(left,right),spatialSum(a,b,n,1,centered,true),centered);
        assertSignal(new AutoCorrelateOp(new ParameterBlock()).create(left),spatialSum(a,a,n,1,centered,true),centered);
        assertSignal(left,a,centered);assertSignal(right,b,centered);
    }
    private static void assertSignal(Function actual,double[][] expected,boolean centered) {
        assertInstanceOf(Function1D.class,actual);assertEquals(centered,actual.isZeroCentered());
        assertArrayEquals(expected[0],actual.getReal(),1e-8);
        assertArrayEquals(expected[1],actual.getImaginary(),1e-8);
    }

    private static double[][] fixture(int w,int h,boolean centered,int variant) {
        double[][] a=new double[2][w*h];
        for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
            double px=x(col,w,centered),py=y(row,h,centered);
            a[0][row*w+col]=.8+.03*px+.02*py+Math.cos(2*Math.PI*(px/w+2*py/h)+variant);
            a[1][row*w+col]=-.4+.04*px-.05*py+.3*Math.sin(2*Math.PI*(2*px/w-py/h)+variant);
        }
        return a;
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
    private static double[][] spatialSum(double[][] a,double[][] b,int w,int h,boolean centered,boolean correlation) {
        double[][] out=new double[2][w*h];
        for(int row=0;row<h;row++)for(int col=0;col<w;col++) {
            int k=row*w+col,px=x(col,w,centered),py=y(row,h,centered);
            for(int sr=0;sr<h;sr++)for(int sc=0;sc<w;sc++) {
                int j=sr*w+sc,sx=x(sc,w,centered),sy=y(sr,h,centered);
                int q=index(correlation?sx-px:px-sx,correlation?sy-py:py-sy,w,h,centered);
                double br=b[0][q],bi=correlation?-b[1][q]:b[1][q];
                out[0][k]+=a[0][j]*br-a[1][j]*bi;
                out[1][k]+=a[0][j]*bi+a[1][j]*br;
            }
        }
        return out;
    }
}
