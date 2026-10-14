package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import signals.gui.Colors;
import signals.operation.ExponentOp;
import signals.operation.InvertOp;

class ComplexPowerWorkflowTest {
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;
    @BeforeEach void setUp() {
        Core c=Core.get();oldCreation=c.functionCreationOptions;oldDisplay=c.displayOptions;oldColors=c.colors;
        c.functionCreationOptions=new FunctionCreationOptions();c.displayOptions=new DisplayOptions();c.colors=new Colors();
    }
    @AfterEach void tearDown() {
        Core c=Core.get();c.functionCreationOptions=oldCreation;c.displayOptions=oldDisplay;c.colors=oldColors;
    }
    static Stream<Arguments> ordinaryPowers() {
        ArrayList<Arguments> args=new ArrayList<>();
        for(boolean image:new boolean[]{false,true}) for(boolean centered:new boolean[]{false,true})
            for(double[] p:new double[][]{{2,0},{.5,0},{.5,.25}}) args.add(Arguments.of(image,centered,p[0],p[1]));
        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("ordinaryPowers")
    void powerRetainsComplexPhaseInBothDimensions(boolean image,boolean centered,double a,double b) {
        Fixture fixture=source(image,centered,1.2);
        ExponentOp op=new ExponentOp(new ParameterBlock());op.setValue(0,a);op.setValue(1,b);
        Function result=op.create(fixture.function);
        check(result,fixture,a,b,false);
        assertEquals(centered,result.isZeroCentered());
        if(image) {
            assertEquals(128,((Function2D)result).getDimensionX());assertEquals(64,((Function2D)result).getDimensionY());
        }
        // Squaring also has an independent Cartesian expression.
        if(a==2&&b==0) {
            double[] re=fixture.function.getReal(),im=fixture.function.getImaginary();
            for(int i=0;i<re.length;i++) {
                assertEquals(re[i]*re[i]-im[i]*im[i],result.getReal()[i],1e-9);
                assertEquals(2*re[i]*im[i],result.getImaginary()[i],1e-9);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans={true,false})
    void sourceAndPowerEditsReachTheLinkedReciprocal(boolean centered) {
        Fixture fixture=source(true,centered,1.2);
        Function2D input=(Function2D)fixture.function;
        ExponentOp op=new ExponentOp(new ParameterBlock());
        OperatorSystem2D power=unary(input,op),inverse=unary(power,new InvertOp(new ParameterBlock()));
        for(double[] p:new double[][]{{2,0},{.5,0},{.5,.25}}) {
            op.setValue(0,p[0]);op.setValue(1,p[1]);power.freeMemory();
            check(power.getFunction(),fixture,p[0],p[1],false);
            check(inverse.getFunction(),fixture,p[0],p[1],true);
            Fixture edited=source(true,centered,.8);
            Function2D replacement=(Function2D)edited.function;
            input.setPartA(replacement.getPartA());input.setPartB(replacement.getPartB());
            fixture=new Fixture(input,edited.radius,edited.phase);
            check(power.getFunction(),fixture,p[0],p[1],false);
            check(inverse.getFunction(),fixture,p[0],p[1],true);
        }
    }

    private static void check(Function actual,Fixture f,double a,double b,boolean inverse) {
        double[] expectedReal=new double[f.radius.length],expectedImaginary=new double[f.radius.length];
        for(int i=0;i<f.radius.length;i++) {
            // Known authored polar data, independent of the production phase/log helpers.
            double magnitude=Math.pow(f.radius[i],a)*Math.exp(-b*f.phase[i]);
            double angle=a*f.phase[i]+b*Math.log(f.radius[i]);
            if(inverse){magnitude=1/magnitude;angle=-angle;}
            expectedReal[i]=magnitude*Math.cos(angle);expectedImaginary[i]=magnitude*Math.sin(angle);
        }
        assertArrayEquals(expectedReal,actual.getReal(),1e-9);
        assertArrayEquals(expectedImaginary,actual.getImaginary(),1e-9);
    }
    private static Fixture source(boolean image,boolean centered,double amplitude) {
        int w=128,h=image?64:1;double[] radius=new double[w*h],phase=new double[w*h],re=new double[w*h],im=new double[w*h];
        for(int row=0;row<h;row++) for(int col=0;col<w;col++) {
            int i=row*w+col;
            radius[i]=amplitude+.2*Math.cos(2*Math.PI*col/w);
            phase[i]=-2.4+4.6*col/(w-1.0)+.1*row/Math.max(1,h-1);
            re[i]=radius[i]*Math.cos(phase[i]);im[i]=radius[i]*Math.sin(phase[i]);
        }
        Function f=image?FunctionFactory.createFunction2D(re,im,centered,"complex image",w,h)
            :FunctionFactory.createFunction1D(re,im,centered,"complex signal");
        return new Fixture(f,radius,phase);
    }
    private static OperatorSystem2D unary(FunctionProducer source,Operation op) {
        ArrayList<Operation> ops=new ArrayList<>(List.of(op));
        return new OperatorSystem2D(new CombineOpsRule(new int[]{CombineOpsRule.UNARY_OP_TIER_2,
            CombineOpsRule.LEFT_PAREN,CombineOpsRule.FUNCTION,CombineOpsRule.RIGHT_PAREN},ops),new ArrayList<>(List.of(source)),ops);
    }
    private record Fixture(Function function,double[] radius,double[] phase) {}
}
