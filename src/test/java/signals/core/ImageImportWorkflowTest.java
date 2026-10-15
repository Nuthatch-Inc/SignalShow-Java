package signals.core;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.Raster;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import signals.functionterm.DataFunctionTerm2D;
import signals.io.ImageImport;
import signals.operation.ArrayUtilities;
import signals.operation.RealOp;
import signals.operation.ThresholdOp;

/** Ordinary file decoding, centering and operation use, with known source pixels. */
class ImageImportWorkflowTest {
    @TempDir Path directory;
    private FunctionCreationOptions previousCreation;

    @BeforeEach void setUp() {
        previousCreation = Core.get().functionCreationOptions;
        Core.get().functionCreationOptions = new FunctionCreationOptions();
    }

    @AfterEach void tearDown() {
        Core.get().functionCreationOptions = previousCreation;
    }

    @ParameterizedTest @CsvSource({
        "jpg,192,128,256,gray", "jpg,128,128,128,gray",
        "png,192,128,256,rgb", "tif,128,128,128,gray", "bmp,160,96,256,rgb",
        "tif,128,64,128,gray16", "png,64,128,128,gray16"
    })
    void loadOrdinaryImageAndUseItInARealThresholdChain(String format, int width,
            int height, int side, String color) throws Exception {
        boolean gray = color.startsWith("gray");
        boolean wide = color.equals("gray16");
        boolean jpeg = format.equals("jpg");
        BufferedImage original = new BufferedImage(width, height,
            wide ? BufferedImage.TYPE_USHORT_GRAY : gray ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR);
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            int red = jpeg ? (x < width / 2 ? 0 : 255) : pixel(x, y, wide);
            if (gray) original.getRaster().setSample(x, y, 0, red);
            else original.setRGB(x, y, (red << 16) | (17 << 8) | 231);
        }
        Path file = directory.resolve("ordinary image." + format);
        assertTrue(ImageIO.write(original, format, file.toFile()));
        DataFunctionTerm2D imported = ImageImport.read(file.toFile());
        assertEquals(side, imported.getXDimension());
        assertEquals(side, imported.getYDimension());
        double[] samples = imported.create(side, side, true);
        assertEquals(side * side, samples.length);
        Function source = FunctionFactory.createFunction2D(samples, new double[samples.length],
            true, "Imported image", side, side);
        ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance();
        double cutoff = wide ? 32767.5 : 127.5;
        threshold.setValue(2, cutoff);
        Function output = threshold.create(new RealOp().create(source));
        int offsetX = (side - width) / 2, offsetY = (side - height) / 2;
        for (int y = 0; y < side; y++) for (int x = 0; x < side; x++) {
            int sx = x - offsetX, sy = y - offsetY;
            boolean inside = sx >= 0 && sx < width && sy >= 0 && sy < height;
            double expected = inside ? (jpeg ? (sx < width / 2 ? 0 : 255) : pixel(sx, sy, wide)) : 0;
            int i = y * side + x;
            assertEquals(expected, samples[i], jpeg ? 2 : 0, "source pixel " + x + "," + y);
            assertEquals(expected >= cutoff ? 1 : 0, output.getReal()[i], 0);
            assertEquals(0, output.getImaginary()[i], 0);
        }
    }

    @ParameterizedTest @CsvSource({"128,64", "64,128"})
    void fractionalTiffSamplesSurviveImportAndThreshold(int width, int height) throws Exception {
        var model = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
        var sampleModel = new ComponentSampleModel(DataBuffer.TYPE_FLOAT, width, height, 1, width, new int[]{0});
        var raster = Raster.createWritableRaster(sampleModel, new DataBufferFloat(width * height), null);
        BufferedImage original = new BufferedImage(model, raster, false, null);
        for (int row = 0; row < height; row++) for (int col = 0; col < width; col++)
            raster.setSample(col, row, 0, fractionalPixel(col, row));
        Path file = directory.resolve("fractional scientific image.tif");
        assertTrue(ImageIO.write(original, "TIFF", file.toFile()));
        // Establish that the fixture's file and decoder preserve the samples before app import.
        BufferedImage decoded = ImageIO.read(file.toFile());
        assertEquals(DataBuffer.TYPE_FLOAT, decoded.getRaster().getDataBuffer().getDataType());
        DataFunctionTerm2D imported = ImageImport.read(file.toFile());
        int side = Math.max(width, height), offsetX = (side - width) / 2, offsetY = (side - height) / 2;
        double[] samples = imported.create(side, side, true);
        Function source = FunctionFactory.createFunction2D(samples, new double[samples.length], true,
                "fractional TIFF", side, side);
        ThresholdOp threshold = (ThresholdOp) new ThresholdOp().getDefaultInstance(); threshold.setValue(2, .5);
        Function output = threshold.create(new RealOp().create(source));
        for (int row = 0; row < side; row++) for (int col = 0; col < side; col++) {
            int sx = col - offsetX, sy = row - offsetY, index = row * side + col;
            boolean inside = sx >= 0 && sx < width && sy >= 0 && sy < height;
            double expected = inside ? fractionalPixel(sx, sy) : 0;
            if (inside) assertEquals(expected, decoded.getRaster().getSampleDouble(sx, sy, 0), 0);
            assertEquals(expected, samples[index], 0, "imported fractional pixel " + col + "," + row);
            assertEquals(expected >= .5 ? 1 : 0, output.getReal()[index], 0);
            assertEquals(0, output.getImaginary()[index], 0);
        }
    }

    private static int pixel(int x, int y, boolean wide) {
        return wide ? (x * 257 + y * 509) % 65536 : (x * 3 + y * 5) % 256;
    }

    private static double fractionalPixel(int x, int y) {
        return ((x * 3 + y * 5) % 128 - 32) / 64.0;
    }

    @Test void ordinaryRectangularTextSamplesAlsoPadWithoutClipping() {
        ArrayList<Double> data = new ArrayList<>();
        for (int i = 0; i < 192 * 128; i++) data.add((double) i);
        double[] padded = ArrayUtilities.centerAndPad2D(192, 128, data);
        assertEquals(256 * 256, padded.length);
        for (int y = 0; y < 128; y++) for (int x = 0; x < 192; x++)
            assertEquals(y * 192 + x, padded[(y + 64) * 256 + x + 32], 0);
    }
}
