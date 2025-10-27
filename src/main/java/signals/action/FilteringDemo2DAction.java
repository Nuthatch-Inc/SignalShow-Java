package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.demo.Filtering2DSetup;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class FilteringDemo2DAction extends AbstractAction {

	static final String description = "2D Frequency Filtering Demo";

	public FilteringDemo2DAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/newFiltering2D.png"));

	}

	public void actionPerformed(ActionEvent e) {
		
		Filtering2DSetup setup = new Filtering2DSetup(); 
		setup.display();  
	
	}

}
