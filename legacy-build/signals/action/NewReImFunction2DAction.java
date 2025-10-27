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
public class NewReImFunction2DAction extends AbstractAction {
	
	static final String description = "New 2D Function from Real and Imaginary Parts";
	
	/**
	 * 
	 */
	public NewReImFunction2DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/newf1Dri.png"));
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_F, ActionEvent.ALT_MASK ) );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		Function function = FunctionFactory.createZeroFunction2D();
		function.setDescriptor("f" + Core.getFunctionCreationOptions().getNumFunctions());
		Core.addToMainList(function);

	}

}
