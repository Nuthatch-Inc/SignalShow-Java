package signals.gui;

import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import signals.io.ImageWriter;
import signals.io.ImageWriter.ImageType;

@SuppressWarnings("serial")
public class SaveImageDialog extends SaveDialog {

	BufferedImage image; 

	JComboBox formatBox; 

	String[] formats; 

	String defaultName; 


	public SaveImageDialog(JFrame frame, String title, BufferedImage image, String defaultName) {
		super(frame, title, defaultName );
		this.image = image; 
		this.defaultName = defaultName; 

		formats = new String[ImageType.values().length];

		int i = 0; 
		for( ImageType type : ImageType.values() ) { 

			formats[i] = ImageWriter.typeToExtension(type); 
			i++; 
		}

		formatBox = new JComboBox( formats ); 

		formatBox = new JComboBox( formats ); 

		pathPanel.add(formatBox); 
	}

	@Override
	public boolean commitSettings() {

		int formatIndex = formatBox.getSelectedIndex();
		String format = formats[formatIndex]; 
		ImageWriter.writeImage(getSelectedFile("", format), image, ImageWriter.extensionToType(format)); 

		return true;
	}

	@Override
	public ImageType getDefaultType() {

		return ImageType.PNG;
	}

}
