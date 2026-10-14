package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.XYFunctionTerm2D;
import signals.operation.SquaredMagnitudeOp;

/** Independent ordinary grouping expectations through real 1D/2D channel creation. */
class MathTermGroupingTest extends MathAuditSupport {
    static Stream<Arguments> cases() {
        return Stream.of("a+(b)*c", "a-(b)*c", "a+(b*c)*d", "a-(b-c)*d",
                "a*(b+(c*d)+e)", "a-(b-(c-d)+e)").flatMap(expression -> Stream.of(
                Arguments.of(expression, false, false), Arguments.of(expression, false, true),
                Arguments.of(expression, true, false), Arguments.of(expression, true, true)));
    }

    @ParameterizedTest @MethodSource("cases")
    void groupedCreationKeepsBothChannelsAndDownstreamIntensity(String expression, boolean image, boolean centered) {
        double[] a = {2, 3, 4, 5, 6}, b = {1, -2, 0.5, 3, -1};
        int[] tokens = expression.chars().map(c -> switch (c) {
            case '+' -> CombineTermsRule.ADD; case '-' -> CombineTermsRule.SUBTRACT;
            case '*' -> CombineTermsRule.MULTIPLY; case '(' -> CombineTermsRule.LEFT_PAREN;
            case ')' -> CombineTermsRule.RIGHT_PAREN; default -> CombineTermsRule.FUNCTION_TERM;
        }).toArray();
        int count = (int) expression.chars().filter(c -> c >= 'a' && c <= 'e').count();
        Function source = image
                ? new RealImagFunction2D(part2D(a, count, tokens), part2D(b, count, tokens), "grouped image", 64, 32, centered)
                : new RealImagFunction1D(part1D(a, count, tokens), part1D(b, count, tokens), "grouped signal", 64, centered);
        double real = expected(expression, a), imag = expected(expression, b);
        assertSamples(source, image, centered, real, imag);
        assertSamples(source.clone(), image, centered, real, imag);
        SquaredMagnitudeOp intensity = new SquaredMagnitudeOp(new ParameterBlock());
        ArrayList<Operation> ops = new ArrayList<>(List.of(intensity));
        CombineOpsRule rule = new CombineOpsRule(new int[]{intensity.getPriority(), CombineOpsRule.FUNCTION}, ops);
        OperatorSystem downstream = image ? new OperatorSystem2D(rule, new ArrayList<>(List.of(source)), ops)
                : new OperatorSystem1D(rule, new ArrayList<>(List.of(source)), ops);
        assertSamples(downstream.getFunction(), image, centered, real * real + imag * imag, 0);
    }

    private static double expected(String expression, double[] v) {
        double a = v[0], b = v[1], c = v[2], d = v[3], e = v[4];
        return switch (expression) {
            case "a+(b)*c" -> a + b*c; case "a-(b)*c" -> a - b*c;
            case "a+(b*c)*d" -> a + b*c*d; case "a-(b-c)*d" -> a - (b-c)*d;
            case "a*(b+(c*d)+e)" -> a * (b+c*d+e); case "a-(b-(c-d)+e)" -> a-(b-(c-d)+e);
            default -> throw new AssertionError(expression);
        };
    }

    private static ConstantFunctionTerm1D constant(double value) {
        return new ConstantFunctionTerm1D(new ParameterBlock().add(value));
    }

    private static FunctionPart1D part1D(double[] values, int count, int[] tokens) {
        ArrayList<FunctionTerm1D> terms = new ArrayList<>();
        for (int i = 0; i < count; i++) terms.add(constant(values[i]));
        return new FunctionPart1D(terms, new CombineTermsRule(tokens));
    }

    private static FunctionPart2D part2D(double[] values, int count, int[] tokens) {
        ArrayList<FunctionTerm2D> terms = new ArrayList<>();
        for (int i = 0; i < count; i++) terms.add(new XYFunctionTerm2D(
                new ParameterBlock().addSource(constant(values[i])).addSource(constant(1))));
        return new FunctionPart2D(terms, new CombineTermsRule(tokens));
    }

    private static void assertSamples(Function result, boolean image, boolean centered, double real, double imag) {
        double[] er = new double[image ? 64*32 : 64], ei = new double[er.length];
        Arrays.fill(er, real); Arrays.fill(ei, imag);
        if (image) assertImage(result, er, ei, 64, 32, centered);
        else {
            assertInstanceOf(Function1D.class, result); assertEquals(centered, result.isZeroCentered());
            assertArrayEquals(er, result.getReal(), 1e-8); assertArrayEquals(ei, result.getImaginary(), 1e-8);
        }
    }
}
