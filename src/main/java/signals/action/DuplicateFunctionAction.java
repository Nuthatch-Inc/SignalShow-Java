package signals.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.Showable;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class DuplicateFunctionAction extends AbstractAction {

	static final String description = "Duplicate";
	
	public DuplicateFunctionAction() {
	
		super( "", ResourceLoader.createImageIcon("/guiIcons/MenuIconModify.png"));
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_D, ActionEvent.CTRL_MASK ) );
		
		putValue( SHORT_DESCRIPTION, description );
	}
	
	public void actionPerformed(ActionEvent e) {
		
		Showable function = (Showable) Core.getGUI().getFunctionList().getSelectedItem(); 
		Core.addToMainList(function.clone()); 
			
	}

}
