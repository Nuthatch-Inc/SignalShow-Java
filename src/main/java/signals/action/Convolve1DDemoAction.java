package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.demo.Convolve1DSetup;
import signals.demo.ConvolveDemoSetup;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class Convolve1DDemoAction extends AbstractAction {

	static final String description = "1D Convolution Demo";

	public Convolve1DDemoAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/newConvolve1D.png") );
		
	}

	public void actionPerformed(ActionEvent e) {

		if( Core.getGUI().getFunction1DList().getListSize() > 0 ) {
			
			ConvolveDemoSetup setup = new Convolve1DSetup(); 
			setup.display();
			
		}

	}

}
