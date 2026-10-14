package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.*;

class MathCoordinates2DTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"32,16,true", "32,16,false", "16,32,true", "16,32,false"})
    void reversalNegatesPhysicalCoordinatesAndKeepsTheOrigin(int w, int h, boolean centered) {
        double[] real = new double[w*h], imag = new double[w*h];
        for (int row=0; row<h; row++) for (int col=0; col<w; col++) {
            int k=row*w+col;
            real[k]=2*x(col,w,centered)+3*y(row,h,centered)+5;
            imag[k]=-x(col,w,centered)+4*y(row,h,centered)-2;
        }
        Function2D source=image(real,imag,w,h,centered);
        ReverseOp2D op=new ReverseOp2D(new ParameterBlock());
        OperatorSystem2D chain=unary(source,op);
        for (boolean horizontal:new boolean[]{true,false,true}) {
            op.setXDirection(horizontal); chain.freeMemory();
            double[] er=new double[w*h], ei=new double[w*h];
            for (int row=0; row<h; row++) for (int col=0; col<w; col++) {
                int px=x(col,w,centered), py=y(row,h,centered);
                int from=index(horizontal?-px:px,horizontal?py:-py,w,h,centered);
                er[row*w+col]=real[from]; ei[row*w+col]=imag[from];
            }
            Function result=chain.getFunction();
            assertImage(result,er,ei,w,h,centered);
            int origin=index(0,0,w,h,centered);
            assertEquals(real[origin],result.getReal()[origin],0,"reversal must fix coordinate zero");
            assertImage(op.create(result),real,imag,w,h,centered);
        }
        assertImage(source,real,imag,w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false"})
    void originImpulseStaysFixedAndDoubleFourierAgreesWithCoordinateReversal(int w,int h,boolean centered) {
        double[] real=new double[w*h],imag=new double[w*h];
        int origin=index(0,0,w,h,centered);real[origin]=2;imag[origin]=-3;
        ReverseOp2D reverse=new ReverseOp2D(new ParameterBlock());
        for(boolean horizontal:new boolean[]{true,false}) {
            reverse.setXDirection(horizontal);
            assertImage(reverse.create(image(real,imag,w,h,centered)),real,imag,w,h,centered);
        }
        int feature=index(3,5,w,h,centered);real[feature]=-.75;imag[feature]=1.25;
        Function source=image(real,imag,w,h,centered);
        FourierTransformOp fft=new FourierTransformOp(new ParameterBlock());
        Function doubleFourier=fft.create(fft.create(source));
        reverse.setXDirection(true);Function reversed=reverse.create(source);
        reverse.setXDirection(false);reversed=reverse.create(reversed);
        double[] er=new double[w*h],ei=new double[w*h];
        er[origin]=2;ei[origin]=-3;
        int reflected=index(-3,-5,w,h,centered);er[reflected]=-.75;ei[reflected]=1.25;
        assertImage(doubleFourier,er,ei,w,h,centered);
        assertImage(reversed,er,ei,w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,16,true", "32,16,false", "16,32,true", "16,32,false"})
    void translationsMoveBothChannelsInTheSelectedPhysicalDirection(int w,int h,boolean centered) {
        double[] real=new double[w*h], imag=new double[w*h];
        for(int i=0;i<real.length;i++) { real[i]=i+.25; imag[i]=1-i*.5; }
        Function2D source=image(real,imag,w,h,centered);
        TranslateOp2D op=new TranslateOp2D(new ParameterBlock());
        for (boolean wrap:new boolean[]{false,true}) for (int[] shift:new int[][]{{3,0},{0,2},{-2,3},{3,-2}}) {
            op.setShiftAmountX(shift[0]); op.setShiftAmountY(shift[1]); op.setWrapAround(wrap);
            double[] er=new double[w*h],ei=new double[w*h];
            for (int row=0;row<h;row++) for(int col=0;col<w;col++) {
                int fromCol=col-shift[0],fromRow=row+shift[1];
                if(wrap || fromCol>=0&&fromCol<w&&fromRow>=0&&fromRow<h) {
                    int from=Math.floorMod(fromRow,h)*w+Math.floorMod(fromCol,w);
                    er[row*w+col]=real[from];ei[row*w+col]=imag[from];
                }
            }
            assertImage(op.create(source),er,ei,w,h,centered);
        }
        assertImage(source,real,imag,w,h,centered);
    }

    @ParameterizedTest
    @CsvSource({"32,16,true", "32,16,false", "16,32,true", "16,32,false"})
    void directionalCalculusUsesIncreasingXAndYForBothChannels(int w,int h,boolean centered) {
        double[] real=new double[w*h],imag=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            real[row*w+col]=5+2*x(col,w,centered)+3*y(row,h,centered);
            imag[row*w+col]=-2-x(col,w,centered)+4*y(row,h,centered);
        }
        Function2D source=image(real,imag,w,h,centered);
        for(boolean horizontal:new boolean[]{true,false}) {
            DerivativeOp2D derivative=new DerivativeOp2D(new ParameterBlock());
            IntegralOp2D integral=new IntegralOp2D(new ParameterBlock());
            derivative.setXDirection(horizontal);integral.setXDirection(horizontal);
            double[] dr=new double[w*h],di=new double[w*h],ir=new double[w*h],ii=new double[w*h];
            for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
                int k=row*w+col;
                if(horizontal?col>0:row<h-1) { dr[k]=horizontal?2:3;di[k]=horizontal?-1:4; }
                if(horizontal) for(int c=0;c<=col;c++) { ir[k]+=real[row*w+c];ii[k]+=imag[row*w+c]; }
                else for(int r=h-1;r>=row;r--) { ir[k]+=real[r*w+col];ii[k]+=imag[r*w+col]; }
            }
            assertImage(derivative.create(source),dr,di,w,h,centered);
            assertImage(integral.create(source),ir,ii,w,h,centered);
        }
    }
}
