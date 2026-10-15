package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.FUNCTION;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.SineFunctionTerm1D;
import signals.functionterm.XYFunctionTerm2D;
import signals.operation.DivideOp;
import signals.operation.ScaleOp;
import signals.operation.SquaredMagnitudeOp;

class MathDivisionZeroCrossingTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"32,1,false", "32,1,true", "32,16,false", "32,16,true", "16,32,false", "16,32,true"})
    void scaledComplexSinusoidKeepsItsRatioAcrossCommonZeros(int width, int height, boolean centered) {
        SineFunctionTerm1D real = sine(width, centered), imag = sine(width, centered);
        Function source;
        if (height == 1) {
            source = new RealImagFunction1D(
                    new FunctionPart1D(new ArrayList<>(List.of(real)), CombineTermsRule.getDefaultRule()),
                    new FunctionPart1D(new ArrayList<>(List.of(imag)), CombineTermsRule.getDefaultRule()),
                    "complex sinusoid", width, centered);
        } else {
            source = new RealImagFunction2D(part(real), part(imag), "complex sinusoid", width, height, centered);
        }
        ScaleOp scale = (ScaleOp) new ScaleOp().getDefaultInstance();
        OperatorSystem numerator = system(height, List.of(source), scale, scale.getPriority(), FUNCTION);
        DivideOp divide = (DivideOp) new DivideOp().getDefaultInstance();
        OperatorSystem quotient = system(height, List.of(numerator, source), divide, FUNCTION, divide.getPriority(), FUNCTION);
        SquaredMagnitudeOp intensity = (SquaredMagnitudeOp) new SquaredMagnitudeOp().getDefaultInstance();
        OperatorSystem power = system(height, List.of(quotient), intensity, intensity.getPriority(), FUNCTION);

        for (double[] coefficient : new double[][]{{2, 3}, {-.5, 1.25}}) {
            scale.setValue(0, coefficient[0]); scale.setValue(1, coefficient[1]); numerator.freeMemory();
            // Publish ordinary source edits with populated linked caches on the second pass.
            for (double[] amplitude : new double[][]{{1, 1}, {.75, -.5}}) {
                real.setAmplitude(amplitude[0]); imag.setAmplitude(amplitude[1]);
                if (source instanceof Function1D f) {
                    f.setPartA(f.getPartA()); f.setPartB(f.getPartB());
                } else {
                    Function2D f = (Function2D) source;
                    f.setPartA(f.getPartA()); f.setPartB(f.getPartB());
                }
                double[] inputReal = source.getReal().clone(), inputImag = source.getImaginary().clone();
                assertEquals(0, inputReal[7], 0); assertEquals(0, inputImag[7], 0);
                Function result = quotient.getFunction(), magnitudeSquared = power.getFunction();
                assertEquals(centered, result.isZeroCentered());
                if (height > 1) {
                    assertEquals(width, ((Function2D) result).getDimensionX());
                    assertEquals(height, ((Function2D) result).getDimensionY());
                }
                for (int i = 0; i < width * height; i++) {
                    // Algebraic limit: (c * B(t)) / B(t) = c, including a removable zero.
                    assertEquals(coefficient[0], result.getReal()[i], 1e-10, "real sample " + i);
                    assertEquals(coefficient[1], result.getImaginary()[i], 1e-10, "imaginary sample " + i);
                    assertEquals(coefficient[0]*coefficient[0] + coefficient[1]*coefficient[1],
                            magnitudeSquared.getReal()[i], 1e-10, "intensity sample " + i);
                    assertEquals(0, magnitudeSquared.getImaginary()[i]);
                }
                assertArrayEquals(inputReal, source.getReal()); assertArrayEquals(inputImag, source.getImaginary());
            }
        }
    }

    private static SineFunctionTerm1D sine(int width, boolean centered) {
        SineFunctionTerm1D term = (SineFunctionTerm1D) new SineFunctionTerm1D().getDefaultInstance();
        term.setWidth(19); term.setCenter(7 - (centered ? width / 2 : 0));
        return term;
    }

    private static FunctionPart2D part(SineFunctionTerm1D term) {
        ConstantFunctionTerm1D y = (ConstantFunctionTerm1D) new ConstantFunctionTerm1D().getDefaultInstance();
        XYFunctionTerm2D xy = new XYFunctionTerm2D(new ParameterBlock().addSource(term).addSource(y));
        return new FunctionPart2D(new ArrayList<>(List.of(xy)), CombineTermsRule.getDefaultRule());
    }

    private static OperatorSystem system(int height, List<FunctionProducer> inputs, Operation operation, int... codes) {
        ArrayList<Operation> ops = new ArrayList<>(List.of(operation));
        CombineOpsRule rule = new CombineOpsRule(codes, ops);
        return height == 1 ? new OperatorSystem1D(rule, new ArrayList<>(inputs), ops)
                : new OperatorSystem2D(rule, new ArrayList<>(inputs), ops);
    }
}
