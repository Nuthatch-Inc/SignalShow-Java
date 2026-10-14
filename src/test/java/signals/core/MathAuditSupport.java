package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import signals.gui.Colors;

/** Test setup and geometry only; expected arithmetic never calls production math helpers. */
abstract class MathAuditSupport {
    private FunctionCreationOptions creation;
    private DisplayOptions display;
    private Colors colors;

    @BeforeEach void initializeServices() {
        Core c = Core.get();
        creation = c.functionCreationOptions; display = c.displayOptions; colors = c.colors;
        c.functionCreationOptions = new FunctionCreationOptions();
        c.displayOptions = new DisplayOptions(); c.colors = new Colors();
    }
    @AfterEach void restoreServices() {
        Core c = Core.get();
        c.functionCreationOptions = creation; c.displayOptions = display; c.colors = colors;
    }
    static int x(int col, int width, boolean centered) { return col - (centered ? width / 2 : 0); }
    static int y(int row, int height, boolean centered) { return height - 1 - row - (centered ? height / 2 : 0); }
    static int index(int x, int y, int width, int height, boolean centered) {
        int col = Math.floorMod(x + (centered ? width / 2 : 0), width);
        int bottomRow = Math.floorMod(y + (centered ? height / 2 : 0), height);
        return (height - 1 - bottomRow) * width + col;
    }
    static Function2D image(double[] real, double[] imag, int width, int height, boolean centered) {
        return (Function2D) FunctionFactory.createFunction2D(real.clone(), imag.clone(), centered, "math fixture", width, height);
    }
    static void assertImage(Function actual, double[] real, double[] imag, int width, int height, boolean centered) {
        Function2D result = assertInstanceOf(Function2D.class, actual);
        assertEquals(width, result.getDimensionX()); assertEquals(height, result.getDimensionY());
        assertEquals(centered, result.isZeroCentered());
        assertArrayEquals(real, result.getReal(), 1e-8, "real channel");
        assertArrayEquals(imag, result.getImaginary(), 1e-8, "imaginary channel");
    }
    static OperatorSystem2D unary(FunctionProducer source, UnaryOperation operation) {
        ArrayList<Operation> ops = new ArrayList<>(List.of(operation));
        return new OperatorSystem2D(new CombineOpsRule(new int[]{operation.getPriority(), CombineOpsRule.FUNCTION}, ops),
                new ArrayList<>(List.of(source)), ops);
    }
}
