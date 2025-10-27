package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.OperatorSystem1D;
import signals.core.OperatorSystem2D;
import signals.gui.SaveFunction1DDialog;
import signals.gui.SaveFunction2DDialog;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class SaveFunctionAction extends AbstractAction {
	
	static final String description = "Export Function as Data or Image";

	public SaveFunctionAction() {
		
		super( description, ResourceLoader.createImageIcon("/guiIcons/export.png") ); 
	
	}
	
	public void actionPerformed(ActionEvent e) {
		
	if( Core.getGUI().getFunctionList().getListSize() > 0 ) {
			
			Object function = Core.getGUI().getFunctionList().getSelectedItem(); 
			
			if( function instanceof Function1D ) {
				
				(new SaveFunction1DDialog( (Function1D)function )).display();
				
			} else if ( function instanceof Function2D ) {
				
				(new SaveFunction2DDialog( (Function2D)function )).display(); 
				
			} else if ( function instanceof OperatorSystem2D ) {
				
				(new SaveFunction2DDialog( (Function2D)((OperatorSystem2D)function).getFunction() )).display(); 
				
			} else if ( function instanceof OperatorSystem1D ) {
				
				(new SaveFunction1DDialog( (Function1D)((OperatorSystem1D)function).getFunction() )).display(); 
				
			} 
			
		}

	}

}
