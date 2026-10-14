package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.ConstantFunctionTerm2D;
import signals.functionterm.GaussianFunctionTerm1D;
import signals.functionterm.PolarFunctionTerm2D;
import signals.gui.Colors;
import signals.operation.DivideOp;
import signals.operation.MinusOp;

class PolarImageWorkflowTest {
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;
    @BeforeEach void setUp() {
        Core c=Core.get(); oldCreation=c.functionCreationOptions; oldDisplay=c.displayOptions; oldColors=c.colors;
        c.functionCreationOptions=new FunctionCreationOptions(); c.displayOptions=new DisplayOptions(); c.colors=new Colors();
    }
    @AfterEach void tearDown() {
        Core c=Core.get(); c.functionCreationOptions=oldCreation; c.displayOptions=oldDisplay; c.colors=oldColors;
    }

    @ParameterizedTest
    @CsvSource({"128,64,true", "128,64,false", "64,128,true", "64,128,false", "128,128,true", "128,128,false"})
    void radialAndAngularFactorsUseTheSamePixelCoordinates(int w,int h,boolean centered) {
        Function2D source=source(w,h,centered);
        assertArrayEquals(expected(w,h,centered,1.5,16,18,90,60),source.getReal(),1e-9);
        assertArrayEquals(expected(w,h,centered,.4,7,24,180,-25),source.getImaginary(),1e-9);
    }

    @ParameterizedTest
    @CsvSource({"128,64", "64,128"})
    void originAndAngularEditsPropagateThroughComplexDivisionAndSubtraction(int w,int h) {
        Function2D source=source(w,h,true);
        Function base=new RealImagFunction2D(constantPart(1.25),constantPart(-.75),"base",w,h,true);
        OperatorSystem2D divide=binary(source,base,new DivideOp(new ParameterBlock()));
        OperatorSystem2D minus=binary(divide,base,new MinusOp(new ParameterBlock()));
        double phase=60;
        for (boolean centered:new boolean[]{true,false,true}) {
            source.setZeroCentered(centered);base.setZeroCentered(centered);
            verifyChain(source,divide,minus,w,h,centered,phase);
            phase+=30;
            PolarFunctionTerm2D polar=(PolarFunctionTerm2D) source.getPartA().getTermsList().get(0);
            ((CosineFunctionTerm1D)polar.getFunctionTerm1DB()).setInitialPhase(phase);
            source.setPartA(source.getPartA());
            verifyChain(source,divide,minus,w,h,centered,phase);
        }
    }

    private static void verifyChain(Function2D source,OperatorSystem2D divide,OperatorSystem2D minus,
            int w,int h,boolean centered,double phase) {
        double[] ar=expected(w,h,centered,1.5,16,18,90,phase),ai=expected(w,h,centered,.4,7,24,180,-25);
        double[] qr=new double[w*h],qi=new double[w*h],mr=new double[w*h],mi=new double[w*h];
        for(int i=0;i<qr.length;i++) {
            qr[i]=(ar[i]*1.25-ai[i]*.75)/(1.25*1.25+.75*.75);
            qi[i]=(ai[i]*1.25+ar[i]*.75)/(1.25*1.25+.75*.75);
            mr[i]=qr[i]-1.25;mi[i]=qi[i]+.75;
        }
        assertArrayEquals(ar,source.getReal(),1e-9);assertArrayEquals(ai,source.getImaginary(),1e-9);
        assertArrayEquals(qr,divide.getFunction().getReal(),1e-9);assertArrayEquals(qi,divide.getFunction().getImaginary(),1e-9);
        assertArrayEquals(mr,minus.getFunction().getReal(),1e-9);assertArrayEquals(mi,minus.getFunction().getImaginary(),1e-9);
        assertEquals(centered,minus.getFunction().isZeroCentered());
    }

    private static Function2D source(int w,int h,boolean centered) {
        return new RealImagFunction2D(part(1.5,16,18,90,60),part(.4,7,24,180,-25),"polar",w,h,centered);
    }
    private static FunctionPart2D constantPart(double amplitude) {
        ConstantFunctionTerm2D term=(ConstantFunctionTerm2D)new ConstantFunctionTerm2D().getDefaultInstance();
        term.setAmplitude(amplitude);
        return new FunctionPart2D(new ArrayList<>(List.of(term)),new CombineTermsRule(new int[]{1}));
    }
    private static FunctionPart2D part(double amplitude,double center,double width,double period,double phase) {
        GaussianFunctionTerm1D radial=(GaussianFunctionTerm1D)new GaussianFunctionTerm1D().getDefaultInstance();
        radial.setAmplitude(amplitude);radial.setCenter(center);radial.setWidth(width);
        CosineFunctionTerm1D angular=(CosineFunctionTerm1D)new CosineFunctionTerm1D().getDefaultInstance();
        angular.setWidth(period);angular.setInitialPhase(phase);
        PolarFunctionTerm2D polar=new PolarFunctionTerm2D(new ParameterBlock().addSource(radial).addSource(angular));
        return new FunctionPart2D(new ArrayList<>(List.of(polar)),new CombineTermsRule(new int[]{1}));
    }
    private static OperatorSystem2D binary(FunctionProducer a,FunctionProducer b,Operation op) {
        ArrayList<Operation> ops=new ArrayList<>(List.of(op));
        return new OperatorSystem2D(new CombineOpsRule(new int[]{CombineOpsRule.FUNCTION,op.getPriority(),CombineOpsRule.FUNCTION},ops),new ArrayList<>(List.of(a,b)),ops);
    }
    /** Independent geometry: bottom-left or centered coordinates, same for radius and angle. */
    private static double[] expected(int w,int h,boolean centered,double amplitude,double center,double width,double period,double phase) {
        double[] result=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            double x=centered?col-w/2:col,y=centered?h/2-1-row:h-1-row;
            // Retain the existing90-degree convention where polar angle is undefined.
            double angle=x==0&&y==0?90:Math.toDegrees(Math.atan2(y,x));
            double radius=Math.hypot(x,y);
            result[row*w+col]=amplitude*Math.exp(-Math.PI*Math.pow((radius-center)/width,2))*Math.cos(2*Math.PI*angle/period+Math.toRadians(phase));
        }
        return result;
    }
}
