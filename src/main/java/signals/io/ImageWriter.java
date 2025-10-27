package signals.io;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JOptionPane;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.Constants.Part;
import signals.gui.plot.ImageDisplayMath;

public class ImageWriter {

	public static enum ImageType {
		TIFF, PNG, JPG, BMP, TEXT, CSV
	};

	public static boolean writeImage(File file, Function2D function, Part part, String extension) {

		return writeImage(file, function, part, extensionToType(extension));
	}

	public static boolean writeImage(File file, Function2D function, Part part, ImageType type) {
		return writeImage(file, function, part, type, null, null);
	}

	/**
	 * Write image with explicit min/max values for normalization.
	 * This allows multiple parts (real, imaginary) to be normalized to the same
	 * scale.
	 * 
	 * @param file     Output file
	 * @param function Function to export
	 * @param part     Which part to export (real, imaginary, magnitude, phase)
	 * @param type     Image type
	 * @param minValue Minimum value for normalization (null for auto)
	 * @param maxValue Maximum value for normalization (null for auto)
	 * @return true if successful
	 */
	public static boolean writeImage(File file, Function2D function, Part part, ImageType type,
			Double minValue, Double maxValue) {

		double[] data = ((Function) function).getPart(part);
		RenderedImage tiledImage = ImageDisplayMath.data2Image(data, function.getDimensionX(), function.getDimensionY());

		ImageDisplayMath disp = new ImageDisplayMath();
		RenderedImage scaled;

		// Fixed BUG-005: Use shared min/max for normalization if provided
		if (minValue != null && maxValue != null) {
			disp.setMinValue(minValue);
			disp.setMaxValue(maxValue);
			scaled = disp.scale(tiledImage);
		} else {
			scaled = disp.autoScale(tiledImage);
		}

		PlanarImage output = (PlanarImage) ImageDisplayMath.convertToDisplayType(scaled);
		BufferedImage bi = output.getAsBufferedImage();

		return writeImage(file, bi, type);
	}

	/**
	 * Calculate shared min/max values across multiple parts of a function.
	 * This ensures real and imaginary parts can be normalized to the same scale.
	 * 
	 * @param function The function to analyze
	 * @param parts    Array of parts to include in min/max calculation
	 * @return double array [minValue, maxValue]
	 */
	public static double[] calculateSharedMinMax(Function2D function, Part[] parts) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (Part part : parts) {
			double[] data = ((Function) function).getPart(part);
			for (double value : data) {
				if (value < min)
					min = value;
				if (value > max)
					max = value;
			}
		}

		return new double[] { min, max };
	}

	public static String typeToExtension(ImageType type) {

		switch (type) {

			case BMP:

				return ".bmp";
			case JPG:

				return ".jpg";

			case PNG:

				return ".png";

			case TIFF:

				return ".tiff";

			case TEXT:

				return ".txt";

			case CSV:

				return ".csv";

		}

		return null;
	}

	public static String replaceExtension(String filename, ImageType type) {

		String extension = Utils.getExtension(filename);

		if (extension == null || extension.trim().length() == 0) {

			filename = filename + typeToExtension(type);

		} else if (type != extensionToType(extension)) {

			String strippedName = Utils.getNameNoExtension(filename);
			filename = strippedName + typeToExtension(type);
		}

		return filename;
	}

	public static ImageType extensionToType(String extension) {

		String ext = extension.toLowerCase();

		if (ext.equals(".tiff") || ext.equals(".tif")) {

			return ImageType.TIFF;

		} else if (ext.equals(".png")) {

			return ImageType.PNG;

		} else if (ext.equals(".jpg") || ext.equals(".jpeg")) {

			return ImageType.JPG;

		} else if (ext.equals(".txt")) {

			return ImageType.TEXT;

		} else if (ext.equals(".csv")) {

			return ImageType.CSV;

		} else if (ext.equals(".bmp")) {

			return ImageType.BMP;
		}

		return null;
	}

	public static boolean writeImage(File file, BufferedImage image, ImageType type) {

		String path = file.getPath();

		try {
			switch (type) {

				case BMP:

					JAI.create("filestore", image, path, "BMP");

					break;

				case JPG:

					ImageIO.write(image, "JPG", new File(path));

					break;

				case PNG:

					ImageIO.write(image, "PNG", new File(path));

					break;

				case TIFF:

					JAI.create("filestore", image, path, "TIFF");

					break;

				case TEXT:
				case CSV:
					// These formats are handled by writeText() and writeCSV() methods
					return false;

			}

		} catch (Exception e) {

			JOptionPane.showMessageDialog(Core.getFrame(),
					"Image was not saved.",
					"File I/O Error",
					JOptionPane.ERROR_MESSAGE);

			return false;

		}

		return true;
	}

	public static boolean writeText(File file, Function2D function, Part part) {

		String path = file.getPath();
		String extension = Utils.getExtension(path);

		if (extension == null || extension.trim().length() == 0 || ImageType.TEXT != extensionToType(extension)) {

			path = path + typeToExtension(ImageType.TEXT);

		}

		// open the file
		FileWriter writer = null;
		try {

			writer = new FileWriter(new File(path));

			// write the dimension to the file
			writer.write("" + function.getDimensionX() + " " + function.getDimensionY());

			// write the data to the file
			double[] data = ((Function) function).getPart(part);

			int i = 0;

			// for each row
			for (int y = 0; y < function.getDimensionY(); ++y) {

				// write a newline
				writer.write('\n');

				// for each col
				for (int x = 0; x < function.getDimensionX(); ++x) {

					writer.write("" + data[i++] + " ");

				}

			}

			// close the file
			writer.close();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(Core.getFrame(),
					"Data was not saved.",
					"File I/O Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * Write function data to CSV (Comma-Separated Values) file.
	 * CSV format is more suitable for spreadsheets and data analysis tools.
	 * 
	 * @param file     Output file
	 * @param function Function to export
	 * @param part     Which part to export (real, imaginary, magnitude, phase)
	 * @return true if successful
	 */
	public static boolean writeCSV(File file, Function2D function, Part part) {

		String path = file.getPath();
		String extension = Utils.getExtension(path);

		if (extension == null || extension.trim().length() == 0 || ImageType.CSV != extensionToType(extension)) {

			path = path + typeToExtension(ImageType.CSV);

		}

		// open the file
		FileWriter writer = null;
		try {

			writer = new FileWriter(new File(path));

			// write CSV header with metadata
			writer.write("# SignalShow 2D Function Export\n");
			writer.write("# Dimensions: " + function.getDimensionX() + " x " + function.getDimensionY() + "\n");
			writer.write("# Part: " + part.toString() + "\n");
			writer.write("# Function: " + function.getDescriptor() + "\n");

			// write column headers (x indices)
			for (int x = 0; x < function.getDimensionX(); ++x) {
				if (x > 0)
					writer.write(",");
				writer.write("" + x);
			}
			writer.write("\n");

			// write the data to the file
			double[] data = ((Function) function).getPart(part);

			int i = 0;

			// for each row (y)
			for (int y = 0; y < function.getDimensionY(); ++y) {

				// for each col (x)
				for (int x = 0; x < function.getDimensionX(); ++x) {

					if (x > 0)
						writer.write(",");
					writer.write("" + data[i++]);

				}

				// write a newline after each row
				writer.write("\n");

			}

			// close the file
			writer.close();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(Core.getFrame(),
					"Data was not saved.",
					"File I/O Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

}
