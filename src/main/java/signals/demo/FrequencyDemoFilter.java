package signals.demo;

import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.functionterm.AnalyticFunctionTerm1D;

/** Samples a filter on signed FFT frequencies, including the wrapped negative bins. */
final class FrequencyDemoFilter {
    private FrequencyDemoFilter() {}

    static Function create(Function spectrum, AnalyticFunctionTerm1D term) {
        boolean twoD = spectrum instanceof Function2D;
        int width = twoD ? ((Function2D) spectrum).getDimensionX() : ((Function1D) spectrum).getDimension();
        int height = twoD ? ((Function2D) spectrum).getDimensionY() : 1;
        boolean centered = spectrum.isZeroCentered();
        double[] frequencies = new double[width * height];
        for (int row = 0; row < height; row++) {
            int y = twoD ? frequency(height - 1 - row, height, centered) : 0;
            for (int col = 0; col < width; col++) {
                int x = frequency(col, width, centered);
                frequencies[row * width + col] = twoD ? Math.hypot(x, y) : x;
            }
        }
        double[] real = term.create(frequencies), imaginary = new double[real.length];
        return twoD ? FunctionFactory.createFunction2D(real, imaginary, centered, "filter", width, height)
            : FunctionFactory.createFunction1D(real, imaginary, centered, "Filter");
    }

    private static int frequency(int index, int size, boolean centered) {
        if (centered) return index - size / 2;
        return index < size / 2 ? index : index - size;
    }
}
