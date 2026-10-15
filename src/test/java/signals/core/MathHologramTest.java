package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.Random;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.operation.HologramEncoderOp;
import signals.operation.PhaseDetourHologramOp;
import signals.operation.ScaleOp;

/** Verify the binary cells through their optical first order, not a copy of the quantizer. */
class MathHologramTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"16,8,true", "16,8,false", "8,16,true", "8,16,false"})
    void encodingLeavesAllSourceChannelsIntactAndCanBeRepeated(int w, int h, boolean centered) {
        double[] real = new double[w*h], imag = new double[w*h];
        for (int k=0; k<real.length; k++) {
            real[k] = 1 + .3*Math.cos(.21*k); imag[k] = .7*Math.sin(.17*k);
        }
        for (boolean diffuse : new boolean[]{false,true}) for (boolean wide : new boolean[]{false,true}) {
            Function2D source = image(real, imag, w, h, centered);
            double[] magnitude = source.getMagnitude().clone(), phase = source.getPhase().clone();
            HologramEncoderOp op = encoder(diffuse, wide);
            Function first = op.create(source);
            assertArrayEquals(magnitude, source.getMagnitude(), 0, "source magnitude after encoding");
            assertArrayEquals(phase, source.getPhase(), 0, "source phase after encoding");
            assertImage(source, real, imag, w, h, centered);
            assertArrayEquals(first.getReal(), op.create(source).getReal(), 0, "second use of same source");
            assertImage(first, referenceCells(real, imag, w, h, diffuse, wide), new double[w*h*64], w*8, h*8, true);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans={false,true})
    void eightPhaseDirectionsAndAuthoredHalfCycleHaveCorrectOpticalFirstOrder(boolean wide) {
        int w=16,h=8;
        double[] directions={-Math.PI,-3*Math.PI/4,-Math.PI/2,-Math.PI/4,0,Math.PI/4,Math.PI/2,3*Math.PI/4,
                Math.PI,.3,-.3,1,-1,2,-2,2.9};
        double[] magnitude=new double[w*h],phase=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            magnitude[row*w+col]=(row+1)/8.0; phase[row*w+col]=directions[col];
        }
        Function source=FunctionFactory.createMagPhaseFunction2D(magnitude.clone(),phase.clone(),true,"phase directions",w,h);
        Function output=encoder(false,wide).create(source);
        double[] pixels=output.getReal();
        double apertureGain=wide ? 1+2*Math.cos(Math.PI/4) : 1;
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            double re=0,im=0;
            for(int yy=0;yy<8;yy++) for(int xx=0;xx<8;xx++) {
                double pixel=pixels[(row*8+yy)*w*8+col*8+xx];
                assertTrue(pixel==0 || pixel==1, "binary cell");
                re+=pixel*Math.cos(2*Math.PI*xx/8)/64;
                im+=pixel*Math.sin(2*Math.PI*xx/8)/64;
            }
            assertEquals((row+1)*apertureGain/64,Math.hypot(re,im),1e-12,"aperture amplitude");
            // The fixed four-column carrier offset contributes a global pi phase.
            double phaseError=Math.atan2(Math.sin(Math.atan2(im,re)-directions[col]-Math.PI),
                    Math.cos(Math.atan2(im,re)-directions[col]-Math.PI));
            assertTrue(Math.abs(phaseError)<=Math.PI/8+1e-12,"phase error at column "+col+": "+phaseError);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans={false,true})
    void diffusionCarriesComplexResidualIntoTheNextCell(boolean wide) {
        int w=16,h=8;
        double[] real=new double[w*h],imag=new double[w*h];
        real[0]=Math.cos(.3);imag[0]=Math.sin(.3);real[1]=.5;
        for(int k=2;k<real.length;k++) {
            double amplitude=.2+.8*(1+Math.sin(.37*k))/2,angle=.29*k;
            real[k]=amplitude*Math.cos(angle);imag[k]=amplitude*Math.sin(angle);
        }
        Function output=encoder(true,wide).create(image(real,imag,w,h,true));
        assertImage(output,referenceCells(real,imag,w,h,true,wide),new double[w*h*64],w*8,h*8,true);
        // First cell quantizes to 1+0i. Its nonzero phase error must reach cell 2.
        assertEquals(1,output.getReal()[7*w*8+8+5],0,"cell 2 carries the imaginary residual to column 5");
    }

    @ParameterizedTest
    @CsvSource({"16,8,true,false", "8,16,true,false", "16,8,false,false", "8,16,false,false",
        "16,8,true,true", "8,16,true,true", "16,8,false,true", "8,16,false,true"})
    void phaseDetourUsesTheSameVerifiedCellsAfterItsInverseTransform(int w,int h,boolean centered,boolean randomPhase) {
        double[] real=new double[w*h], imag=new double[w*h], objectReal=new double[w*h], objectImag=new double[w*h];
        // The existing Java operation uses the Random Phase generator's fixed default seed 1.
        Random random = new Random(1);
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            double px=x(col,w,centered),py=y(row,h,centered);
            int k=row*w+col;
            real[k]=Math.exp(-(px-1.7)*(px-1.7)/17-(py+.8)*(py+.8)/6);
            imag[k]=.4*Math.cos(.2*k); // The documented object amplitude is the signed real channel.
            double phase=randomPhase ? 2*Math.PI*random.nextDouble()-Math.PI : 0;
            objectReal[k]=real[k]*Math.cos(phase); objectImag[k]=real[k]*Math.sin(phase);
        }
        // Independent physical-coordinate DFT: retain Java's inverse sign and centered output,
        // while respecting the source's actual origin. No FFT or production shift helper here.
        double[] spectrumReal=new double[w*h],spectrumImag=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            int k=row*w+col;
            for(int sr=0;sr<h;sr++) for(int sc=0;sc<w;sc++) {
                double angle=2*Math.PI*(x(col,w,true)*(double)x(sc,w,centered)/w+y(row,h,true)*(double)y(sr,h,centered)/h);
                double a=objectReal[sr*w+sc], b=objectImag[sr*w+sc];
                spectrumReal[k]+=a*Math.cos(angle)-b*Math.sin(angle);
                spectrumImag[k]+=a*Math.sin(angle)+b*Math.cos(angle);
            }
        }
        for(boolean diffuse:new boolean[]{false,true}) for(boolean wide:new boolean[]{false,true}) {
            PhaseDetourHologramOp op=new PhaseDetourHologramOp(new ParameterBlock());
            op.setRandomPhase(randomPhase);op.setErrorDiffusion(diffuse);op.setWiderApertures(wide);
            Function2D source=image(real,imag,w,h,centered);
            assertImage(op.create(source),referenceCells(spectrumReal,spectrumImag,w,h,diffuse,wide),new double[w*h*64],w*8,h*8,true);
            assertImage(source,real,imag,w,h,centered);
        }
    }

    @ParameterizedTest
    @CsvSource({"16,8,true", "8,16,true", "16,8,false", "8,16,false"})
    void originImpulseProducesIdenticalZeroPhaseHologramCells(int w,int h,boolean centered) {
        double[] real=new double[w*h], imag=new double[w*h];
        real[index(0,0,w,h,centered)]=1;
        Function2D source=image(real,imag,w,h,centered);
        PhaseDetourHologramOp op=new PhaseDetourHologramOp(new ParameterBlock());
        double[] actual=op.create(source).getReal();
        // The unnormalized transform of delta(x,y) is exactly 1 at every frequency.
        // Every cell therefore has full height and the zero-phase carrier column 4.
        for(int row=0;row<h*8;row++) for(int col=0;col<w*8;col++)
            assertEquals(col%8==4 ? 1 : 0,actual[row*w*8+col],0,"origin impulse cell at "+col+","+row);
        assertImage(source,real,imag,w,h,centered);
    }

    @ParameterizedTest
    @ValueSource(booleans={false,true})
    void editedComplexScaleFeedsTheEncoderWithoutChangingTheSource(boolean centered) {
        int w=16,h=8;
        double[] real=new double[w*h],imag=new double[w*h];
        for(int k=0;k<real.length;k++) {real[k]=.8+.4*Math.cos(.21*k);imag[k]=.6*Math.sin(.17*k);}
        Function2D source=image(real,imag,w,h,centered);
        ScaleOp scale=new ScaleOp(new ParameterBlock());
        OperatorSystem2D scaled=unary(source,scale),encoded=unary(scaled,encoder(true,false));
        for(double[] factor:new double[][]{{.7,.3},{-.6,.8},{1,0}}) {
            scale.setValue(0,factor[0]);scale.setValue(1,factor[1]);scaled.freeMemory();
            double[] rr=new double[w*h],ii=new double[w*h];
            for(int k=0;k<rr.length;k++) {
                rr[k]=real[k]*factor[0]-imag[k]*factor[1];ii[k]=imag[k]*factor[0]+real[k]*factor[1];
            }
            double[] beforeMagnitude=scaled.getFunction().getMagnitude().clone();
            assertImage(encoded.getFunction(),referenceCells(rr,ii,w,h,true,false),new double[w*h*64],w*8,h*8,true);
            assertArrayEquals(beforeMagnitude,scaled.getFunction().getMagnitude(),0);
            assertImage(source,real,imag,w,h,centered);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans={false,true})
    void blankImageEncodesAsBlank(boolean diffuse) {
        for(boolean wide:new boolean[]{false,true})
            assertImage(encoder(diffuse,wide).create(image(new double[128],new double[128],16,8,true)),
                    new double[8192],new double[8192],128,64,true);
    }

    private static HologramEncoderOp encoder(boolean diffuse, boolean wide) {
        HologramEncoderOp op=new HologramEncoderOp(new ParameterBlock());
        op.setErrorDiffusion(diffuse);op.setWiderApertures(wide);return op;
    }

    /** Enumerate the eight physical phase directions and nine allowed aperture heights. */
    private static double[] referenceCells(double[] real,double[] imag,int w,int h,boolean diffuse,boolean wide) {
        double peak=0;for(int k=0;k<real.length;k++) peak=Math.max(peak,Math.hypot(real[k],imag[k]));
        double[] pixels=new double[w*h*64];
        if(peak==0)return pixels;
        double residualReal=0,residualImag=0;
        for(int k=0;k<real.length;k++) {
            double re=real[k]/peak+(diffuse?residualReal:0),im=imag[k]/peak+(diffuse?residualImag:0);
            double magnitude=Math.hypot(re,im),bestDistance=Double.POSITIVE_INFINITY;int height=0;
            for(int candidate=0;candidate<=8;candidate++) {
                double distance=Math.abs(magnitude-candidate/8.0);
                if(distance<bestDistance) {bestDistance=distance;height=candidate;}
            }
            double bestProjection=-Double.MAX_VALUE;int column=4;
            if(magnitude>1e-14) for(int candidate=0;candidate<8;candidate++) {
                double angle=2*Math.PI*(candidate-4)/8;
                double projection=re*Math.cos(angle)+im*Math.sin(angle);
                if(projection>bestProjection) {bestProjection=projection;column=candidate;}
            }
            double chosenAngle=2*Math.PI*(column-4)/8;
            residualReal=re-height*Math.cos(chosenAngle)/8;
            residualImag=im-height*Math.sin(chosenAngle)/8;
            for(int yy=8-height;yy<8;yy++) for(int xx=0;xx<8;xx++) {
                if(xx==column || (wide && (xx==(column+1)%8 || xx==(column+7)%8)))
                    pixels[((k/w)*8+yy)*w*8+(k%w)*8+xx]=1;
            }
        }
        return pixels;
    }
}
