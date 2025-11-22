package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.demo.Filtering1DSetup;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class FilteringDemo1DAction extends AbstractAction {

	static final String description = "1D Frequency Filtering Demo";

	public FilteringDemo1DAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/newFiltering1D.png"));

	}

	public void actionPerformed(ActionEvent e) {
		
		Filtering1DSetup setup = new Filtering1DSetup(); 
		setup.display(); 

	}

}
