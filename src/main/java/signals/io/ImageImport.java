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
        double[] red;
        if (image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY) {
            // Preserve integer and floating grayscale samples without color conversion or truncation.
            red = image.getRaster().getSamples(0, 0, width, height, 0, (double[]) null);
        } else {
            // Import the red channel, independent of the decoder's band ordering.
            int[] rgb = image.getRGB(0, 0, width, height, null, 0, width);
            red = new double[rgb.length];
            for (int i = 0; i < red.length; i++) red[i] = (rgb[i] >>> 16) & 0xff;
        }
        double[] data = ArrayUtilities.centerAndPad2D(width, height, red);
        int side = ArrayUtilities.getSquareDimension(width, height);
        ParameterBlock block = new DataFunctionTerm2D().getDefaultParamBlock();
        block.addSource(data);
        block.addSource(side);
        block.addSource(side);
        DataFunctionTerm2D term = new DataFunctionTerm2D(block);
        term.setWidth(side);
        term.setHeight(side);
        return term;
    }
}
