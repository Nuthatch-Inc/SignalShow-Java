/**
 * 
 */
package signals.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.Function;
import signals.core.FunctionFactory;
import signals.io.ResourceLoader;


/**
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class NewReImFunction1DAction extends AbstractAction {
	
	static final String description = "New 1D Function from Real and Imaginary Parts";
	
	/**
	 * 
	 */
	public NewReImFunction1DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/newf1Dri.png"));
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_F, ActionEvent.CTRL_MASK ) );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
	
		Function function = FunctionFactory.createZeroFunction1D();
		function.setDescriptor("f" + Core.getFunctionCreationOptions().getNumFunctions());
		Core.addToMainList(function);

	}

}
