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

		if( format.equals(ImageType.TEXT) ) {

			for( Part part : getSelectedParts() ) {

				returnflag &= ImageWriter.writeText(getSelectedFile(part.toString(), format), function, part);
			}


		} else {

			for( Part part : getSelectedParts() ) {

				returnflag &= ImageWriter.writeImage(getSelectedFile(part.toString(), format), function, part, format );

			}

			
		}
		return returnflag;

	}


	@Override
	public ImageType getDefaultType() {

		return ImageType.TIFF;
	}

}
