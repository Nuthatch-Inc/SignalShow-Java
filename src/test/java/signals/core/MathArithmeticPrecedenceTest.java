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
                "a*b+c","a+b*c","a-b-c","a/(b+c)",
                "a+(b)*c","a-(b)*c","a+(b*c)*d","a-(b-c)*d","a/(b/c)*d",
                "a*(b+(c*d)+e)","a-(b-(c-d)+e)","a/((b)/c)","a+((b*c))*d",
                "a+r(b)*c","r(a+r(b)*c)","m(a+j(b)*c)","r(j(a+b)*c)",
                "rj(a+b)*c","a+rj(b)*c","mj(a+b)*c","rj(a+b*c)","a+bj*c","a+bjr*c").flatMap(expression->Stream.of(
                Arguments.of(expression,false,true),Arguments.of(expression,false,false),
                Arguments.of(expression,true,true),Arguments.of(expression,true,false)));
    }

    @ParameterizedTest
    @MethodSource("cases")
    void calculatorPrioritiesRespectStandardComplexArithmetic(String expression,boolean image,boolean centered) {
        int n=image?64*32:64;
        int count=(int)expression.chars().filter(c->c>='a' && c<='e').count();
        double[][] real=new double[count][n],imag=new double[count][n];
        ArrayList<FunctionProducer> inputs=new ArrayList<>();
        for(int channel=0;channel<count;channel++) {
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
            else if(token>='a' && token<='e') tokens[i]=CombineOpsRule.FUNCTION;
            else {
                Operation prototype=switch(token) {case '+'->new PlusOp();case '-'->new MinusOp();case '*'->new TimesOp();case '/'->new DivideOp();case 'r'->new RealOp();case 'j'->new ConjugateOp();case 'm'->new MagnitudeOp();default->throw new AssertionError(token);};
                Operation op=(Operation)prototype.getDefaultInstance();ops.add(op);
                // Use the same priority metadata as the actual calculator buttons.
                tokens[i]=op.getPriority();
            }
        }
        CombineOpsRule rule=new CombineOpsRule(tokens,ops);
        OperatorSystem system=image?new OperatorSystem2D(rule,inputs,ops):new OperatorSystem1D(rule,inputs,ops);
        Function result=system.getFunction();
        assertNotNull(result, "A complete ordinary expression must produce a result: " + expression);
        double[] er=new double[n],ei=new double[n],power=new double[n];
        for(int k=0;k<n;k++) {
            Complex a=new Complex(real[0][k],imag[0][k]),b=new Complex(real[1][k],imag[1][k]),c=new Complex(real[2][k],imag[2][k]);
            Complex d=count>3?new Complex(real[3][k],imag[3][k]):null;
            Complex e=count>4?new Complex(real[4][k],imag[4][k]):null;
            Complex expected=switch(expression) {
                case "a+b/c"->a.plus(b.divide(c));case "a-b/c"->a.minus(b.divide(c));
                case "a/b*c"->a.divide(b).times(c);case "a/b/c"->a.divide(b).divide(c);
                case "(a+b)/c"->a.plus(b).divide(c);case "a/(b*c)"->a.divide(b.times(c));
                case "a*b+c"->a.times(b).plus(c);case "a+b*c"->a.plus(b.times(c));
                case "a-b-c"->a.minus(b).minus(c);case "a/(b+c)"->a.divide(b.plus(c));
                case "a+(b)*c"->a.plus(b.times(c));case "a-(b)*c"->a.minus(b.times(c));
                case "a+(b*c)*d","a+((b*c))*d"->a.plus(b.times(c).times(d));
                case "a-(b-c)*d"->a.minus(b.minus(c).times(d));
                case "a/(b/c)*d"->a.divide(b.divide(c)).times(d);
                case "a*(b+(c*d)+e)"->a.times(b.plus(c.times(d)).plus(e));
                case "a-(b-(c-d)+e)"->a.minus(b.minus(c.minus(d)).plus(e));
                case "a/((b)/c)"->a.divide(b.divide(c));
                case "a+r(b)*c"->a.plus(b.realPart().times(c));
                case "r(a+r(b)*c)"->a.plus(b.realPart().times(c)).realPart();
                case "m(a+j(b)*c)"->a.plus(b.conjugate().times(c)).magnitude();
                case "r(j(a+b)*c)"->a.plus(b).conjugate().times(c).realPart();
                case "rj(a+b)*c"->a.plus(b).conjugate().realPart().times(c);
                case "a+rj(b)*c"->a.plus(b.conjugate().realPart().times(c));
                case "mj(a+b)*c"->a.plus(b).conjugate().magnitude().times(c);
                case "rj(a+b*c)"->a.plus(b.times(c)).conjugate().realPart();
                case "a+bj*c"->a.plus(b.conjugate().times(c));
                case "a+bjr*c"->a.plus(b.conjugate().realPart().times(c));
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
        for(int i=0;i<count;i++) {
            assertArrayEquals(real[i],inputs.get(i).getFunction().getReal(),0);
            assertArrayEquals(imag[i],inputs.get(i).getFunction().getImaginary(),0);
        }
    }

    // Multiplication/division use polar arithmetic, independently of the production Cartesian formula.
    private record Complex(double real,double imag) {
        Complex realPart(){return new Complex(real,0);}
        Complex conjugate(){return new Complex(real,-imag);}
        Complex magnitude(){return new Complex(Math.hypot(real,imag),0);}
        Complex plus(Complex b){return new Complex(real+b.real,imag+b.imag);}
        Complex minus(Complex b){return new Complex(real-b.real,imag-b.imag);}
        Complex times(Complex b){return polar(Math.hypot(real,imag)*Math.hypot(b.real,b.imag),Math.atan2(imag,real)+Math.atan2(b.imag,b.real));}
        Complex divide(Complex b){return polar(Math.hypot(real,imag)/Math.hypot(b.real,b.imag),Math.atan2(imag,real)-Math.atan2(b.imag,b.real));}
        private static Complex polar(double radius,double phase){return new Complex(radius*Math.cos(phase),radius*Math.sin(phase));}
    }
}
