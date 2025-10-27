package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.core.Function;
import signals.core.OperatorSystem;
import signals.gui.SaveComplexDialog;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class SaveComplexAction extends AbstractAction {

	static final String description = "Save Complex Data";

	public SaveComplexAction() {

		super( description, ResourceLoader.createImageIcon("/guiIcons/save.png") ); 

	}

	public void actionPerformed(ActionEvent e) {

		if( Core.getGUI().getFunctionList().getListSize() > 0 ) {

			Object function = Core.getGUI().getFunctionList().getSelectedItem(); 

			if( function instanceof Function ) {
				
				(new SaveComplexDialog( (Function)function )).display();

			} else if ( function instanceof OperatorSystem ) {

				(new SaveComplexDialog( ((OperatorSystem)function).getFunction() )).display(); 

			} 

		}

	}

}
