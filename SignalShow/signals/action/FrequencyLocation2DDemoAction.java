package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.demo.FrequencyLocations2DSetup;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class FrequencyLocation2DDemoAction extends AbstractAction {

	static final String description = "2D Frequency Locations";

	public FrequencyLocation2DDemoAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/newFrequencyLocations.png") );
	}

	public void actionPerformed(ActionEvent e) {

		FrequencyLocations2DSetup setup = new FrequencyLocations2DSetup(); 
		setup.display();  
		
	}

}
