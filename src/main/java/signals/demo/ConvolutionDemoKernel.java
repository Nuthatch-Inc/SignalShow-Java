package signals.demo;

import signals.core.BinaryOperation;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.operation.NormalizeOp;

/** Samples the moving filter at the coordinates actually shown by the demo. */
final class ConvolutionDemoKernel {
    final Function kernel;
    private final int width, height, originX, originY;
    private final boolean twoDimensional, centered, convolution, wrap;

    ConvolutionDemoKernel(Function input, Function kernel, boolean convolution,
            boolean wrap, boolean normalize) {
        this.kernel = normalize ? new NormalizeOp(null).create(kernel) : kernel;
        this.convolution = convolution;
        this.wrap = wrap;
        twoDimensional = input instanceof Function2D;
        width = twoDimensional ? ((Function2D) input).getDimensionX() : ((Function1D) input).getDimension();
        height = twoDimensional ? ((Function2D) input).getDimensionY() : 1;
        centered = input.isZeroCentered();
        originX = centered ? width / 2 : 0;
        originY = twoDimensional ? (centered ? height / 2 - 1 : height - 1) : 0;
    }

    Function atOutput(int index) {
        return at(index % width - originX, originY - index / width);
    }

    Function at(int x, int y) {
        double[] real = kernel.getReal(), imaginary = kernel.getImaginary();
        double[] movedReal = new double[real.length], movedImaginary = new double[real.length];
        for (int row = 0; row < height; row++) {
            int inputY = originY - row;
            int filterY = convolution ? y - inputY : inputY - y;
            int kr = originY - filterY;
            if (wrap) kr = Math.floorMod(kr, height);
            else if (kr < 0 || kr >= height) continue;
            for (int col = 0; col < width; col++) {
                int inputX = col - originX;
                int filterX = convolution ? x - inputX : inputX - x;
                int kc = originX + filterX;
                if (wrap) kc = Math.floorMod(kc, width);
                else if (kc < 0 || kc >= width) continue;
                int dst = row * width + col, src = kr * width + kc;
                movedReal[dst] = real[src];
                // Correlation slides the conjugated reference: f(t) conjugate(m(t-x)).
                movedImaginary[dst] = convolution ? imaginary[src] : -imaginary[src];
            }
        }
        return sampled(movedReal, movedImaginary, width, height, centered,
            "translate(" + kernel.getCompactDescriptor() + ")");
    }

    Function complete(Function input, BinaryOperation operation) {
        if (wrap) return operation.create(input, kernel);
        // Zero padding prevents the FFT completion shortcut from introducing the
        // periodic copies that the user disabled with Wrap Around.
        int paddedWidth = width * 2, paddedHeight = twoDimensional ? height * 2 : 1;
        Function full = operation.create(padded(input, paddedWidth, paddedHeight),
            padded(kernel, paddedWidth, paddedHeight));
        double[] fullReal = full.getReal(), fullImaginary = full.getImaginary();
        double[] real = new double[width * height], imaginary = new double[width * height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int dst = row * width + col;
                int src = paddedIndex(col, row, paddedWidth, paddedHeight);
                real[dst] = fullReal[src];
                imaginary[dst] = fullImaginary[src];
            }
        }
        return sampled(real, imaginary, width, height, centered, "output");
    }

    private Function padded(Function input, int paddedWidth, int paddedHeight) {
        double[] real = new double[paddedWidth * paddedHeight], imaginary = new double[real.length];
        double[] sourceReal = input.getReal(), sourceImaginary = input.getImaginary();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int src = row * width + col;
                int dst = paddedIndex(col, row, paddedWidth, paddedHeight);
                real[dst] = sourceReal[src];
                imaginary[dst] = sourceImaginary[src];
            }
        }
        return sampled(real, imaginary, paddedWidth, paddedHeight, false, input.getCompactDescriptor());
    }

    private int paddedIndex(int col, int row, int paddedWidth, int paddedHeight) {
        int x = Math.floorMod(col - originX, paddedWidth);
        int y = Math.floorMod(originY - row, paddedHeight);
        return (paddedHeight - 1 - y) * paddedWidth + x;
    }

    private Function sampled(double[] real, double[] imaginary, int w, int h,
            boolean zeroCentered, String name) {
        return twoDimensional ? FunctionFactory.createFunction2D(real, imaginary, zeroCentered, name, w, h)
            : FunctionFactory.createFunction1D(real, imaginary, zeroCentered, name);
    }
}
