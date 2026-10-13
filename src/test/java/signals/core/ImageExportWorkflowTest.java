package signals.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import signals.core.Constants.Part;
import signals.gui.Colors;
import signals.io.ImageWriter;
import signals.io.ImageWriter.ImageType;
import signals.io.Utils;

/** Actual channel exports, including names and decoded pixels, at ordinary values. */
class ImageExportWorkflowTest {
    @TempDir Path directory;
    private FunctionCreationOptions oldCreation;
    private DisplayOptions oldDisplay;
    private Colors oldColors;

    @BeforeEach void setUp() {
        Core core = Core.get();
        oldCreation = core.functionCreationOptions;
        oldDisplay = core.displayOptions;
        oldColors = core.colors;
        core.functionCreationOptions = new FunctionCreationOptions();
        core.displayOptions = new DisplayOptions();
        core.colors = new Colors();
    }

    @AfterEach void tearDown() {
        Core core = Core.get();
        core.functionCreationOptions = oldCreation;
        core.displayOptions = oldDisplay;
        core.colors = oldColors;
    }

    @ParameterizedTest @CsvSource({"PNG,-4", "TIFF,-4", "PNG,1", "TIFF,1"})
    void realAndImaginaryImageExportsUseTheActualSharedRange(ImageType type, double min) throws Exception {
        double[] real = new double[32], imaginary = new double[32];
        for (int i = 0; i < real.length; i++) {
            real[i] = min + i % 2;
            imaginary[i] = min + 2 + i % 2;
        }
        Function2D function = function(real, imaginary);
        Part[] parts = {Part.REAL_PART, Part.IMAGINARY_PART};
        double[] range = ImageWriter.calculateSharedMinMax(function, parts);
        assertArrayEquals(new double[] {min, min + 3}, range);
        for (Part part : parts) {
            Path output = directory.resolve("signed image_" + part + ImageWriter.typeToExtension(type));
            assertTrue(ImageWriter.writeImage(output.toFile(), function, part, type, range[0], range[1]));
            var image = ImageIO.read(output.toFile());
            assertNotNull(image);
            assertEquals(8, image.getWidth());
            assertEquals(4, image.getHeight());
            for (int i = 0; i < real.length; i++) {
                int expected = 85 * (i % 2 + (part == Part.REAL_PART ? 0 : 2));
                assertEquals(expected, image.getRaster().getSample(i % 8, i / 8, 0));
            }
        }
        assertArrayEquals(real, function.getReal());
        assertArrayEquals(imaginary, function.getImaginary());
    }

    @ParameterizedTest @EnumSource(value = ImageType.class, names = {"TEXT", "CSV"})
    void numericExportsKeepTheDialogFilenameAndExactSamples(ImageType type) throws Exception {
        double[] real = new double[32], imaginary = new double[32];
        for (int i = 0; i < real.length; i++) {
            real[i] = (i - 17) / 4.0;
            imaginary[i] = (11 - i) / 8.0;
        }
        Function2D function = function(real, imaginary);
        for (Part part : new Part[] {Part.REAL_PART, Part.IMAGINARY_PART}) {
            Path output = directory.resolve("complex export_" + part + ImageWriter.typeToExtension(type));
            boolean saved = type == ImageType.TEXT
                ? ImageWriter.writeText(output.toFile(), function, part)
                : ImageWriter.writeCSV(output.toFile(), function, part);
            assertTrue(saved);
            assertTrue(Files.exists(output), "The dialog already supplies the extension");
            assertFalse(Files.exists(Path.of(output + ImageWriter.typeToExtension(type))));
            List<String> lines = Files.readAllLines(output);
            assertEquals(type == ImageType.TEXT ? "8 4" : "# Dimensions: 8 x 4",
                lines.get(type == ImageType.TEXT ? 0 : 1));
            double[] actual = lines.stream().skip(type == ImageType.TEXT ? 1 : 5)
                .flatMap(line -> Arrays.stream(line.trim().split(type == ImageType.TEXT ? "\\s+" : ",")))
                .mapToDouble(Double::parseDouble).toArray();
            assertArrayEquals(part == Part.REAL_PART ? real : imaginary, actual);
        }
    }

    @Test void chooserAndFilenameExtensionsResolveToTheSameFormat() {
        for (ImageType type : ImageType.values()) {
            String extension = ImageWriter.typeToExtension(type);
            String path = "ordinary image" + extension;
            assertEquals(type, ImageWriter.extensionToType(extension));
            assertEquals(type, ImageWriter.extensionToType(Utils.getExtension(path)));
            assertEquals(path, ImageWriter.replaceExtension(path, type));
        }
        assertEquals(ImageType.TIFF, ImageWriter.extensionToType("TIF"));
        assertEquals(ImageType.JPG, ImageWriter.extensionToType(".JPEG"));
    }

    private static Function2D function(double[] real, double[] imaginary) {
        return (Function2D) FunctionFactory.createFunction2D(real.clone(), imaginary.clone(),
            true, "ordinary complex export", 8, 4);
    }
}
