package signals.gui;

import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Constants.Part;
import signals.io.ImageWriter.ImageType;

@SuppressWarnings("serial")
public class SaveFunction1DDialog extends SaveFunctionDialog {

	Function1D function; 

	public SaveFunction1DDialog( Function1D function ) {
		super("Save 1D Data", function.getDescriptor());
		this.function = function;

		addPartSelectors();

	}

	@Override
	public boolean commitSettings() {

		//open the file
		FileWriter writer = null; 
		try {

			for( Part part : getSelectedParts() ) { 

				writer = new FileWriter(getSelectedFile(part.toString(), ".txt"));

				//write the data to the file
				double[] data = ((Function)function).getPart(part); 
				for( int i = 0; i < data.length; ++i ) {

					writer.write("\n"+data[i]); 
				}

				//close the file
				writer.close();

			} 


		} catch (IOException e) {
			JOptionPane.showMessageDialog(Core.getFrame(),
					"Data was not saved.",
					"File I/O Error",
					JOptionPane.ERROR_MESSAGE);
			return false; 
		} 


		return true;
	}


	@Override
	public ImageType getDefaultType() {

		return ImageType.TEXT;
	}

}
