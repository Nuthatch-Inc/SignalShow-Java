package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import signals.demo.AnimatedPane;
import signals.demo.Convolve1D;
import signals.demo.Convolve2D;
import signals.gui.Borders;
import signals.gui.Colors;
import signals.operation.ConvolveOp;

/** Ordinary leaving/completing a playing demonstration must release its timer. */
class DemoLifecycleWorkflowTest {
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;
    private Borders oldBorders;

    @BeforeEach void setUp() {
        Core c = Core.get();
        oldCreation = c.functionCreationOptions; oldDisplay = c.displayOptions;
        oldColors = c.colors; oldBorders = c.borders;
        c.functionCreationOptions = new FunctionCreationOptions();
        c.displayOptions = new DisplayOptions(); c.colors = new Colors(); c.borders = new Borders();
    }

    @AfterEach void tearDown() {
        Core c = Core.get();
        c.functionCreationOptions = oldCreation; c.displayOptions = oldDisplay;
        c.colors = oldColors; c.borders = oldBorders;
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void switchingAwayFromAPlayingDemoStopsItsTimer(boolean twoDimensional) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AnimatedPane demo = demo(twoDimensional);
            Timer timer = timer(demo);
            try {
                demo.play();
                assertTrue(timer.isRunning());
                // GUI.removeContent/setContent invoke this contract on the departing view.
                if (demo instanceof Deconstructable cleanup) cleanup.deconstruct();
                assertFalse(timer.isRunning(), "A removed view must not retain live animation work");
                demo.play();
                assertTrue(timer.isRunning(), "An explicitly reopened view can still play");
                demo.stop();
                assertFalse(timer.isRunning());
            } finally {
                timer.stop();
            }
        });
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void reachingTheLastFrameStopsTheTimer(boolean twoDimensional) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AnimatedPane demo = demo(twoDimensional);
            Timer timer = timer(demo);
            try {
                for (int i = 0; i < 80 && !demo.lastStep(); i++) demo.step();
                assertTrue(demo.lastStep(), "The ordinary finite demo reaches its end");
                demo.play();
                // Execute the real timer callback deterministically on the EDT.
                for (var listener : timer.getActionListeners())
                    listener.actionPerformed(new ActionEvent(timer, ActionEvent.ACTION_PERFORMED, "tick"));
                assertFalse(timer.isRunning(), "A completed demo must release the repeating timer");
            } finally {
                timer.stop();
            }
        });
    }

    private static AnimatedPane demo(boolean twoDimensional) {
        int size = twoDimensional ? 64 : 16;
        double[] real = new double[size], imaginary = new double[size];
        for (int i = 0; i < size; i++) real[i] = (i % 7) / 6.0;
        Function input = twoDimensional
            ? FunctionFactory.createFunction2D(real, imaginary, true, "demo input", 8, 8)
            : FunctionFactory.createFunction1D(real, imaginary, true, "demo input");
        Function kernel = input.clone();
        var op = new ConvolveOp(null);
        return twoDimensional ? new Convolve2D(input, kernel, true, op, true, false)
            : new Convolve1D(input, kernel, true, op, 0, true, false);
    }

    private static Timer timer(AnimatedPane pane) {
        try {
            var field = AnimatedPane.class.getDeclaredField("timer");
            field.setAccessible(true);
            return (Timer) field.get(pane);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
