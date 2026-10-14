package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.operation.*;

class MathArithmeticPrecedenceTest extends MathAuditSupport {
    static Stream<Arguments> cases() {
        return Stream.of("a+b/c","a-b/c","a/b*c","a/b/c","(a+b)/c","a/(b*c)",
                "a*b+c","a+b*c","a-b-c","a/(b+c)").flatMap(expression->Stream.of(
                Arguments.of(expression,false,true),Arguments.of(expression,false,false),
                Arguments.of(expression,true,true),Arguments.of(expression,true,false)));
    }

    @ParameterizedTest
    @MethodSource("cases")
    void calculatorPrioritiesRespectStandardComplexArithmetic(String expression,boolean image,boolean centered) {
        int n=image?64*32:64;
        double[][] real=new double[3][n],imag=new double[3][n];
        ArrayList<FunctionProducer> inputs=new ArrayList<>();
        for(int channel=0;channel<3;channel++) {
            for(int k=0;k<n;k++) {
                real[channel][k]=1.2+.4*channel+.3*Math.cos(.13*k+channel);
                imag[channel][k]=-.7+.5*channel+.2*Math.sin(.21*k-channel);
            }
            inputs.add(image?image(real[channel],imag[channel],64,32,centered):
                    FunctionFactory.createFunction1D(real[channel].clone(),imag[channel].clone(),centered,"operand"+channel));
        }
        ArrayList<Operation> ops=new ArrayList<>();int[] tokens=new int[expression.length()];
        for(int i=0;i<tokens.length;i++) {
            char token=expression.charAt(i);
            if(token=='(') tokens[i]=CombineOpsRule.LEFT_PAREN;
            else if(token==')') tokens[i]=CombineOpsRule.RIGHT_PAREN;
            else if(token>='a' && token<='c') tokens[i]=CombineOpsRule.FUNCTION;
            else {
                Operation prototype=switch(token) {case '+'->new PlusOp();case '-'->new MinusOp();case '*'->new TimesOp();case '/'->new DivideOp();default->throw new AssertionError(token);};
                Operation op=(Operation)prototype.getDefaultInstance();ops.add(op);
                // Use the same priority metadata as the actual calculator buttons.
                tokens[i]=op.getPriority();
            }
        }
        CombineOpsRule rule=new CombineOpsRule(tokens,ops);
        OperatorSystem system=image?new OperatorSystem2D(rule,inputs,ops):new OperatorSystem1D(rule,inputs,ops);
        Function result=system.getFunction();
        double[] er=new double[n],ei=new double[n],power=new double[n];
        for(int k=0;k<n;k++) {
            Complex a=new Complex(real[0][k],imag[0][k]),b=new Complex(real[1][k],imag[1][k]),c=new Complex(real[2][k],imag[2][k]);
            Complex expected=switch(expression) {
                case "a+b/c"->a.plus(b.divide(c));case "a-b/c"->a.minus(b.divide(c));
                case "a/b*c"->a.divide(b).times(c);case "a/b/c"->a.divide(b).divide(c);
                case "(a+b)/c"->a.plus(b).divide(c);case "a/(b*c)"->a.divide(b.times(c));
                case "a*b+c"->a.times(b).plus(c);case "a+b*c"->a.plus(b.times(c));
                case "a-b-c"->a.minus(b).minus(c);case "a/(b+c)"->a.divide(b.plus(c));
                default->throw new AssertionError(expression);
            };
            er[k]=expected.real;ei[k]=expected.imag;power[k]=er[k]*er[k]+ei[k]*ei[k];
        }
        assertEquals(centered,result.isZeroCentered());
        if(image) assertImage(result,er,ei,64,32,centered);
        else {assertInstanceOf(Function1D.class,result);assertArrayEquals(er,result.getReal(),1e-8);assertArrayEquals(ei,result.getImaginary(),1e-8);}
        SquaredMagnitudeOp intensity=new SquaredMagnitudeOp(new ParameterBlock());
        ArrayList<Operation> outerOps=new ArrayList<>(List.of(intensity));
        CombineOpsRule outerRule=new CombineOpsRule(new int[]{intensity.getPriority(),CombineOpsRule.FUNCTION},outerOps);
        ArrayList<FunctionProducer> outerInputs=new ArrayList<>(List.of(system));
        OperatorSystem outer=image?new OperatorSystem2D(outerRule,outerInputs,outerOps):new OperatorSystem1D(outerRule,outerInputs,outerOps);
        assertArrayEquals(power,outer.getFunction().getReal(),1e-8);
        assertArrayEquals(new double[n],outer.getFunction().getImaginary(),0);
        for(int i=0;i<3;i++) {
            assertArrayEquals(real[i],inputs.get(i).getFunction().getReal(),0);
            assertArrayEquals(imag[i],inputs.get(i).getFunction().getImaginary(),0);
        }
    }

    // Multiplication/division use polar arithmetic, independently of the production Cartesian formula.
    private record Complex(double real,double imag) {
        Complex plus(Complex b){return new Complex(real+b.real,imag+b.imag);}
        Complex minus(Complex b){return new Complex(real-b.real,imag-b.imag);}
        Complex times(Complex b){return polar(Math.hypot(real,imag)*Math.hypot(b.real,b.imag),Math.atan2(imag,real)+Math.atan2(b.imag,b.real));}
        Complex divide(Complex b){return polar(Math.hypot(real,imag)/Math.hypot(b.real,b.imag),Math.atan2(imag,real)-Math.atan2(b.imag,b.real));}
        private static Complex polar(double radius,double phase){return new Complex(radius*Math.cos(phase),radius*Math.sin(phase));}
    }
}
