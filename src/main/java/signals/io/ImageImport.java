package signals.io;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import signals.functionterm.DataFunctionTerm2D;
import signals.operation.ArrayUtilities;

/** Reads the image samples used by the existing Load Image action. */
public final class ImageImport {
    private ImageImport() {}

    public static DataFunctionTerm2D read(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) throw new IOException("Unsupported image format: " + file.getName());
        int width = image.getWidth();
        int height = image.getHeight();
        int[] red;
        if (image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY) {
            // Preserve grayscale sample values; getRGB would apply a color conversion.
            red = image.getRaster().getSamples(0, 0, width, height, 0, (int[]) null);
        } else {
            // Import the red channel, independent of the decoder's band ordering.
            red = image.getRGB(0, 0, width, height, null, 0, width);
            for (int i = 0; i < red.length; i++) red[i] = (red[i] >>> 16) & 0xff;
        }
        double[] data = ArrayUtilities.centerAndPad2D(width, height, red);
        int side = ArrayUtilities.getSquareDimension(width, height);
        ParameterBlock block = new DataFunctionTerm2D().getDefaultParamBlock();
        block.addSource(data);
        block.addSource(side);
        block.addSource(side);
        return new DataFunctionTerm2D(block);
    }
}
