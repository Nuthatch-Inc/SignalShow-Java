package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.demo.Sampling1DSetup;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class SamplingDemo1DAction extends AbstractAction {

	static final String description = "1D Sampling/Aliasing Demo";

	public SamplingDemo1DAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/newSampling.png"));

	}

	public void actionPerformed(ActionEvent e) {
		
		Sampling1DSetup setup = new Sampling1DSetup(); 
		setup.display(); 

	}

}
