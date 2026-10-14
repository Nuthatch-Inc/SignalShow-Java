package signals.operation;

/** Shared eight-by-eight binary aperture encoding. All input arrays remain read-only. */
final class HologramEncoding {
    private static final int CELL_SIZE = 8;

    private HologramEncoding() {}

    static double[] encode(double[] real, double[] imaginary, int width, int height,
            boolean errorDiffusion, boolean widerApertures) {
        int outputWidth = width * CELL_SIZE;
        double[] pixels = new double[outputWidth * height * CELL_SIZE];
        double peak = 0;
        for (int i = 0; i < real.length; i++) {
            peak = Math.max(peak, Math.hypot(real[i], imaginary[i]));
        }
        if (peak == 0) return pixels;

        double residualReal = 0, residualImaginary = 0;
        for (int i = 0; i < real.length; i++) {
            double targetReal = real[i] / peak + (errorDiffusion ? residualReal : 0);
            double targetImaginary = imaginary[i] / peak + (errorDiffusion ? residualImaginary : 0);
            int apertureHeight = (int) Math.max(0, Math.min(CELL_SIZE,
                    Math.round(Math.hypot(targetReal, targetImaginary) * CELL_SIZE)));

            // Eight periodic positions, with the existing four-column carrier offset.
            int phaseIndex = (int) Math.round(Math.atan2(targetImaginary, targetReal)
                    * CELL_SIZE / (2 * Math.PI));
            int column = Math.floorMod(phaseIndex + CELL_SIZE / 2, CELL_SIZE);
            if (errorDiffusion) {
                double quantizedAngle = phaseIndex * (2 * Math.PI) / CELL_SIZE;
                double quantizedMagnitude = (double) apertureHeight / CELL_SIZE;
                // A complex difference is not the polar form of separate magnitude/phase errors.
                residualReal = targetReal - quantizedMagnitude * Math.cos(quantizedAngle);
                residualImaginary = targetImaginary - quantizedMagnitude * Math.sin(quantizedAngle);
            }

            int cellRow = i / width, cellColumn = i % width;
            for (int y = CELL_SIZE - apertureHeight; y < CELL_SIZE; y++) {
                int start = (cellRow * CELL_SIZE + y) * outputWidth + cellColumn * CELL_SIZE;
                pixels[start + column] = 1;
                if (widerApertures) {
                    pixels[start + (column + 1) % CELL_SIZE] = 1;
                    pixels[start + (column + CELL_SIZE - 1) % CELL_SIZE] = 1;
                }
            }
        }
        return pixels;
    }
}
