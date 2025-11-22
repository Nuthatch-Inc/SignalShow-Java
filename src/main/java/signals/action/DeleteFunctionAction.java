package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class DeleteFunctionAction extends AbstractAction {
	
	static final String description = "Delete";

	public DeleteFunctionAction() {
		
		super( "", ResourceLoader.createImageIcon("/guiIcons/delete.png") ); 
		
		putValue( SHORT_DESCRIPTION, description );
	}
	
	public void actionPerformed(ActionEvent arg0) {
		
		Core.removeSelectedFromMainList();
		System.runFinalization(); 
		System.gc(); 

	}

}
