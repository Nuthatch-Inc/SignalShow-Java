package signals.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.OperatorSystem2D;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class Operate2DAction extends AbstractAction {

	static final String description = "New 2D Mathematical System";

	public Operate2DAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/MenuIconOp.png"));
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.ALT_MASK ) );

	}

	public void actionPerformed(ActionEvent e) {

		OperatorSystem2D system = new OperatorSystem2D(); 
		Core.getFunctionList().addItem(system); 

	}

}
