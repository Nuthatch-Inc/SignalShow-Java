package signals.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import signals.core.Core;
import signals.core.Function;
import signals.io.ComplexIO;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class LoadComplexAction extends AbstractAction {

	static final String description = "Load Complex Data";

	public LoadComplexAction() {

		super( description, ResourceLoader.createImageIcon("/guiIcons/open32.png") ); 

	}

	public void actionPerformed(ActionEvent e) {

		File cd = Core.getIOOptions().getCurrentDirectory(); 
		JFileChooser fc = ( cd == null ) ? new JFileChooser(): new JFileChooser( cd );

		int returnVal = fc.showOpenDialog(Core.getFrame());
		if( returnVal == JFileChooser.APPROVE_OPTION ) {

			File selectedFile = fc.getSelectedFile();
			cd = fc.getCurrentDirectory(); 
			if( cd != null ) Core.getIOOptions().setCurrentDirectory( cd );


			Function function = ComplexIO.read(selectedFile); 
			Core.addToMainList(function);
			
		}
	}

}
