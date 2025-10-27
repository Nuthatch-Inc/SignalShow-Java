package signals.gui;

import javax.swing.JComboBox;

import signals.core.Function2D;
import signals.core.Constants.Part;
import signals.io.ImageWriter;
import signals.io.ImageWriter.ImageType;

@SuppressWarnings("serial")
public class SaveFunction2DDialog extends SaveFunctionDialog {

	Function2D function; 
	JComboBox formatBox; 

	String[] formats; 

	public SaveFunction2DDialog( Function2D function ) {
		super("Save Image", function.getDescriptor());
		this.function = function;

		formats = new String[ImageType.values().length];

		int i = 0; 
		for( ImageType type : ImageType.values() ) { 

			formats[i] = ImageWriter.typeToExtension(type); 
			i++; 
		}

		addPartSelectors();

		formatBox = new JComboBox( formats ); 
		pathPanel.add(formatBox); 

	}


	@Override
	public boolean commitSettings() {

		int formatIndex = formatBox.getSelectedIndex();
		String format = formats[formatIndex]; 
		boolean returnflag = true; 

		// Get selected parts
		Part[] selectedParts = getSelectedParts().toArray(new Part[0]);

		if( format.equals(ImageWriter.typeToExtension(ImageType.TEXT)) ) {

			for( Part part : selectedParts ) {

				returnflag &= ImageWriter.writeText(getSelectedFile(part.toString(), format), function, part);
			}

		} else {
			
			// Fixed BUG-005: Calculate shared min/max for real and imaginary parts
			// to ensure they are normalized to the same scale
			Double sharedMin = null;
			Double sharedMax = null;

			// Check if both real and imaginary parts are being exported
			boolean hasReal = false;
			boolean hasImaginary = false;
			for (Part part : selectedParts) {
				if (part == Part.REAL_PART) hasReal = true;
				if (part == Part.IMAGINARY_PART) hasImaginary = true;
			}
			
			// If exporting both real and imaginary, use shared normalization
			if (hasReal && hasImaginary) {
				double[] minMax = ImageWriter.calculateSharedMinMax(function, 
new Part[] { Part.REAL_PART, Part.IMAGINARY_PART });
				sharedMin = minMax[0];
				sharedMax = minMax[1];
			}

			for( Part part : selectedParts ) {
				
				// Use shared normalization for real and imaginary parts
				if ((part == Part.REAL_PART || part == Part.IMAGINARY_PART) && sharedMin != null && sharedMax != null) {
					returnflag &= ImageWriter.writeImage(getSelectedFile(part.toString(), format), 
						function, part, ImageWriter.extensionToType(format), sharedMin, sharedMax);
				} else {
					returnflag &= ImageWriter.writeImage(getSelectedFile(part.toString(), format), 
						function, part, format);
				}
			}
		}
		return returnflag;

	}


	@Override
	public ImageType getDefaultType() {

		return ImageType.TIFF;
	}

}
