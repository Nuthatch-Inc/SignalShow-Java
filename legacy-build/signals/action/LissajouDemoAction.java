package signals.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.demo.LissajouModel;
import signals.io.ResourceLoader;


@SuppressWarnings("serial")
public class LissajouDemoAction extends AbstractAction {

	static final String description = "Lissajou Figure";
	
	public LissajouDemoAction() {
		
		super( description, ResourceLoader.createImageIcon("/guiIcons/newLissajou.png") ); 
	}
	
	public void actionPerformed(ActionEvent e) {
		
		LissajouModel model = new LissajouModel(); 
		Core.getFunctionList().addItem( model ); 
	}

}
