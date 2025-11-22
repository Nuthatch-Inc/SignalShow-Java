package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.demo.FilteringModel1D;
import signals.demo.FilteringModel2D;

@SuppressWarnings("serial")
public class FilteringDemoAction extends AbstractAction {

	static final String description = "1D Frequency Filtering";

	public FilteringDemoAction() {

		super(description);

	}

	public void actionPerformed(ActionEvent e) {
		
		if( Core.getGUI().getFunctionList().getListSize() > 0 ) {

			//get the selected item from the function 1D list
			Object function = Core.getGUI().getFunctionList().getSelectedItem(); 

			if( function instanceof Function1D ) {
				
				FilteringModel1D model = new FilteringModel1D((Function)function);
				Core.getFunctionList().addItem( model ); 
				
			} else if ( function instanceof Function2D ) {
				
				FilteringModel2D model = new FilteringModel2D((Function)function);
				Core.getFunctionList().addItem( model ); 
			}
			
		
		}

	}

}
