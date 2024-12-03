package signals.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.OperatorSystem1D;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class Operate1DAction extends AbstractAction {

	static final String description = "New 1D Mathematical System";

	public Operate1DAction() {

		super(description, ResourceLoader.createImageIcon("/guiIcons/MenuIconOp.png"));
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );

	}

	public void actionPerformed(ActionEvent e) {

		OperatorSystem1D system = new OperatorSystem1D(); 
		Core.getFunctionList().addItem(system); 

	}

}
