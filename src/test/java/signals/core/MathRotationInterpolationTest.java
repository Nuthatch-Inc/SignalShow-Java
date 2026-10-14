package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.RotateOp2D;
import signals.operation.SquaredMagnitudeOp;

class MathRotationInterpolationTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"64,32,true,0","64,32,false,0","32,64,true,0","32,64,false,0",
            "64,32,true,1","64,32,false,1","32,64,true,1","32,64,false,1"})
    void ordinaryAnglesPreserveComplexAffineProfilesAndZeroBackground(int w,int h,boolean centered,int interpolation) {
        double[] real=new double[w*h],imag=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            real[row*w+col]=real(col,row);imag[row*w+col]=imag(col,row);
        }
        Function2D source=image(real,imag,w,h,centered);
        RotateOp2D rotate=new RotateOp2D(new ParameterBlock());rotate.setInterpolation(interpolation);
        OperatorSystem2D rotated=unary(source,rotate),intensity=unary(rotated,new SquaredMagnitudeOp(new ParameterBlock()));
        // Rotate is an image-center operation in Java, independently of the signal origin.
        double cx=(w-1)/2.0,cy=(h-1)/2.0;
        for(int angle:new int[]{30,-45,135}) {
            rotate.setValue(0,angle);rotated.freeMemory();intensity.freeMemory();
            Function2D result=(Function2D)rotated.getFunction();Function power=intensity.getFunction();
            assertEquals(w,result.getDimensionX());assertEquals(h,result.getDimensionY());assertEquals(centered,result.isZeroCentered());
            double cos=Math.cos(Math.toRadians(angle)),sin=Math.sin(Math.toRadians(angle));int interior=0,background=0;
            for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
                double sx=cx+cos*(col-cx)+sin*(row-cy),sy=cy-sin*(col-cx)+cos*(row-cy);
                int k=row*w+col;
                if(sx>=2 && sx<w-3 && sy>=2 && sy<h-3) {
                    if(interpolation==Interpolation.INTERP_NEAREST) {
                        // Float-angle transforms may choose either neighbor at an exact half-pixel tie.
                        // These ties do not establish a wrong geometric result.
                        if(Math.abs(sx-Math.floor(sx)-.5)<1e-5 || Math.abs(sy-Math.floor(sy)-.5)<1e-5) continue;
                        sx=Math.floor(sx+.5);sy=Math.floor(sy+.5);
                    }
                    // Linear interpolation reproduces affine profiles; JAI quantizes subpixel weights.
                    double er=real(sx,sy),ei=imag(sx,sy),tolerance=interpolation==Interpolation.INTERP_NEAREST?1e-10:.001;
                    assertEquals(er,result.getReal()[k],tolerance,"real at "+col+","+row+" angle "+angle);
                    assertEquals(ei,result.getImaginary()[k],tolerance,"imag at "+col+","+row+" angle "+angle);
                    assertEquals(er*er+ei*ei,power.getReal()[k],.006);interior++;
                } else if(sx < -2 || sx>w+1 || sy < -2 || sy>h+1) {
                    assertEquals(0,result.getReal()[k],0);assertEquals(0,result.getImaginary()[k],0);background++;
                }
            }
            assertTrue(interior>w*h/5,"substantial rotated image interior checked");
            assertTrue(background>w*h/10,"uncovered canvas checked");
            assertArrayEquals(new double[w*h],power.getImaginary(),0);
            assertImage(source,real,imag,w,h,centered);
        }
    }
    private static double real(double x,double y) {return .3+.02*x-.03*y;}
    private static double imag(double x,double y) {return -.5+.01*x+.04*y;}
}
