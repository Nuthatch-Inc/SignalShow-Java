package signals.gui;

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

		ComplexIO.write(getSelectedFile("", ".txt"), function);
		return true; 
	}


	@Override
	public ImageType getDefaultType() {

		return ImageType.TEXT;
	}

}
