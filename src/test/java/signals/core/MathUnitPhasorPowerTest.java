package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import static signals.core.CombineOpsRule.FUNCTION;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.operation.ExponentOp;
import signals.operation.SquaredMagnitudeOp;

class MathUnitPhasorPowerTest extends MathAuditSupport {
    @ParameterizedTest
    @CsvSource({"32,1,false", "32,1,true", "32,16,false", "32,16,true", "16,32,false", "16,32,true"})
    void boundedIntegerPowersPreserveUnitMagnitudeAndZeroMasks(int width, int height, boolean centered) {
        double[][] phasors = {{-1,0}, {0,1}, {0,-1}, {.6,.8}, {.6,-.8}, {-.8,.6}, {-.8,-.6}, {1,0}, {0,0}};
        double[] real = new double[width*height], imag = new double[real.length];
        for (int i = 0; i < real.length; i++) {
            real[i] = phasors[i%phasors.length][0]; imag[i] = phasors[i%phasors.length][1];
        }
        Function source = height == 1 ? FunctionFactory.createFunction1D(real.clone(), imag.clone(), centered, "phasors")
                : image(real, imag, width, height, centered);
        ExponentOp operation = (ExponentOp) new ExponentOp().getDefaultInstance();
        OperatorSystem raised = system(height, source, operation);
        OperatorSystem intensity = system(height, raised, (SquaredMagnitudeOp) new SquaredMagnitudeOp().getDefaultInstance());
        for (int exponent : new int[]{2, 256, -256, 512, -512}) {
            operation.setValue(0, exponent); raised.freeMemory();
            Function actual = raised.getFunction(), power = intensity.getFunction();
            assertEquals(centered, actual.isZeroCentered());
            if (height > 1) {
                assertEquals(width, ((Function2D) actual).getDimensionX());
                assertEquals(height, ((Function2D) actual).getDimensionY());
            }
            for (int i = 0; i < real.length; i++) {
                boolean zero = real[i] == 0 && imag[i] == 0;
                // Independent integer-power oracle: repeated multiplication, no log/phase/exponential helper.
                double er = 1, ei = 0;
                for (int n = 0; n < Math.abs(exponent); n++) {
                    double next = er*real[i] - ei*imag[i];
                    ei = er*imag[i] + ei*real[i]; er = next;
                }
                if (exponent < 0 && !zero) { double denominator = er*er + ei*ei; er /= denominator; ei = -ei/denominator; }
                // Preserve Java's existing zero-base convention; singularities are a separate contract.
                if (zero) { er = 0; ei = 0; }
                String context = "power " + exponent + ", sample " + i;
                assertEquals(er, actual.getReal()[i], 1e-9, context + " real");
                assertEquals(ei, actual.getImaginary()[i], 1e-9, context + " imaginary");
                assertEquals(zero ? 0 : 1, power.getReal()[i], 1e-9, context + " intensity");
                assertEquals(0, power.getImaginary()[i], 0);
            }
            assertArrayEquals(real, source.getReal()); assertArrayEquals(imag, source.getImaginary());
        }
    }

    private static OperatorSystem system(int height, FunctionProducer input, Operation operation) {
        ArrayList<Operation> ops = new ArrayList<>(List.of(operation));
        CombineOpsRule rule = new CombineOpsRule(new int[]{operation.getPriority(), FUNCTION}, ops);
        ArrayList<FunctionProducer> inputs = new ArrayList<>(List.of(input));
        return height == 1 ? new OperatorSystem1D(rule, inputs, ops) : new OperatorSystem2D(rule, inputs, ops);
    }
}
