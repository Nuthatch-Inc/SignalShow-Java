package signals.gui.datagenerator;

import javax.swing.JOptionPane;

import signals.core.Core;
import signals.gui.DialogTemplate;

@SuppressWarnings("serial")
public abstract class NewOperationDialog extends DialogTemplate {

	public NewOperationDialog( String title ) {
		super(Core.getFrame(), title );
	}
	
	public void showErrorDialog() {
		
		JOptionPane.showMessageDialog(null,
			    "The function could not be created. \n" +
			    "Check to see that your syntax is correct.",
			    "Error",
			    JOptionPane.ERROR_MESSAGE);

	}

}
