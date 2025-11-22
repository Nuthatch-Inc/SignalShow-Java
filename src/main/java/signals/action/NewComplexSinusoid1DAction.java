/**
 * 
 */
package signals.action;

import java.awt.event.ActionEvent;

import signals.core.Core;
import signals.core.FunctionCreationOptions;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.SineFunctionTerm1D;
import signals.io.ResourceLoader;


/**
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class NewComplexSinusoid1DAction extends NewComplexFunctionAction {
	
	static final String description = "New 1D Complex Exponential";
	
	/**
	 * 
	 */
	public NewComplexSinusoid1DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/sine1D.png")); 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		CosineFunctionTerm1D cosTerm = new CosineFunctionTerm1D( null ); 
		SineFunctionTerm1D sinTerm = new SineFunctionTerm1D( null ); 
		FunctionCreationOptions options = Core.getFunctionCreationOptions();
		
		CosineFunctionTerm1D realTerm = (CosineFunctionTerm1D) cosTerm.getDefaultInstance(); 
		SineFunctionTerm1D imagTerm = (SineFunctionTerm1D) sinTerm.getDefaultInstance(); 
		realTerm.setWidth( options.getDefaultWidth1D() ); 
		imagTerm.setWidth( options.getDefaultWidth1D() );
		
		addReImFunction1D( realTerm, imagTerm, "complex exponential");  
	}

}
