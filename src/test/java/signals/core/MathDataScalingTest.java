package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.DataFunctionTerm1D;
import signals.operation.SquaredMagnitudeOp;

class MathDataScalingTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"32,true,0","32,false,0","64,true,0","64,false,0",
            "32,true,1","32,false,1","64,true,1","64,false,1"})
    void ordinaryWidthAndCenterEditsPreserveSampledGeometryAndComplexChains(int n,boolean centered,int mode) {
        double[] originalReal=new double[n],originalImag=new double[n];
        for(int col=0;col<n;col++) {originalReal[col]=.2+.05*col;originalImag[col]=.4-.02*col;}
        Function1D source=(Function1D)FunctionFactory.createFunction1D(originalReal.clone(),originalImag.clone(),centered,"sampled ramps");
        DataFunctionTerm1D real=(DataFunctionTerm1D)source.getPartA().getTermsList().get(0);
        DataFunctionTerm1D imag=(DataFunctionTerm1D)source.getPartB().getTermsList().get(0);
        real.setMode(mode);imag.setMode(mode);real.setAmplitude(1.4);imag.setAmplitude(-.7);
        SquaredMagnitudeOp power=new SquaredMagnitudeOp(new ParameterBlock());
        ArrayList<Operation> ops=new ArrayList<>(List.of(power));
        OperatorSystem1D intensity=new OperatorSystem1D(new CombineOpsRule(new int[]{power.getPriority(),CombineOpsRule.FUNCTION},ops),new ArrayList<>(List.of(source)),ops);
        for(int halfWidth:new int[]{12,10}) for(int shift:new int[]{0,4,n/2-6,-n/2+6}) {
            int cx=(centered?0:n/2)+shift,ci=(centered?0:n/2)-shift;
            real.setWidth(halfWidth);imag.setWidth(halfWidth);real.setCenter(cx);imag.setCenter(ci);
            source.setPartA(source.getPartA());source.setPartB(source.getPartB());
            double[] er=new double[n],ei=new double[n],ep=new double[n];
            for(int col=0;col<n;col++) {
                er[col]=expected(col,n,centered,cx,halfWidth,mode,true);
                ei[col]=expected(col,n,centered,ci,halfWidth,mode,false);
                ep[col]=er[col]*er[col]+ei[col]*ei[col];
            }
            assertArrayEquals(er,source.getReal(),1e-10,"real, half-width "+halfWidth+", center shift "+shift);
            assertArrayEquals(ei,source.getImaginary(),1e-10,"imaginary, half-width "+halfWidth+", center shift "+shift);
            assertArrayEquals(ep,intensity.getFunction().getReal(),1e-9);
            assertArrayEquals(new double[n],intensity.getFunction().getImaginary(),0);
            assertArrayEquals(originalReal,real.getOriginalData(),0);assertArrayEquals(originalImag,imag.getOriginalData(),0);
        }
    }

    private static double expected(int col,int n,boolean centered,int center,int halfWidth,int mode,boolean real) {
        int coordinate=x(col,n,centered);double sourceIndex;
        if(mode==Constants.CROP_MODE) {
            if(Math.abs(coordinate-center)>halfWidth) return 0;
            sourceIndex=col; // Cropping masks samples without translating or rescaling them.
        } else {
            int local=coordinate-(center-halfWidth);
            if(local<0 || local>=2*halfWidth) return 0;
            // Scale the complete source to the authored width, then clip at the viewport.
            sourceIndex=local*(n-1.0)/(2*halfWidth-1);
        }
        return real?1.4*(.2+.05*sourceIndex):-.7*(.4-.02*sourceIndex);
    }
}
