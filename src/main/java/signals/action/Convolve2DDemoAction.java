package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.demo.Convolve2DSetup;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class Convolve2DDemoAction extends AbstractAction {

	static final String description = "2D Convolution Demo";

	public Convolve2DDemoAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/newConvolve2D.png") );
	}

	public void actionPerformed(ActionEvent e) {

		if( Core.getGUI().getFunction2DList().getListSize() > 0 ) {
			
			Convolve2DSetup setup = new Convolve2DSetup(); 
			setup.display();
			
		}

	}

}
