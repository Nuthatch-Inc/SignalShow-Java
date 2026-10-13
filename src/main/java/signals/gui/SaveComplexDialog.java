package signals.gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import signals.core.Function;
import signals.io.ComplexIO;
import signals.io.ImageWriter.ImageType;

@SuppressWarnings("serial")
public class SaveComplexDialog extends SaveFunctionDialog {

	Function function; 

	public SaveComplexDialog( Function function ) {
		super("Save Complex Data", function.getDescriptor());
		this.function = function;
	}

	@Override
	public boolean commitSettings() {

		try {
			ComplexIO.write(getSelectedFile("", ".txt"), function);
			return true;
		} catch (IOException e) {
			// Keep this dialog open so the filename can be corrected or cancelled.
			JOptionPane.showMessageDialog(this, "Data was not saved.",
					"File I/O Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}


	@Override
	public ImageType getDefaultType() {

		return ImageType.TEXT;
	}

}
