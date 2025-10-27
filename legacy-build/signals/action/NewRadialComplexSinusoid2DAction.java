/**
 * 
 */
package signals.action;

import java.awt.event.ActionEvent;

import signals.core.Core;
import signals.core.FunctionCreationOptions;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.CosineFunctionTerm1D;
import signals.functionterm.SineFunctionTerm1D;
import signals.io.ResourceLoader;


/**
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class NewRadialComplexSinusoid2DAction extends NewComplexFunctionAction {
	
	static final String description = "New 2D Radial Complex Exponential";
	
	/**
	 * 
	 */
	public NewRadialComplexSinusoid2DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/sinusoid2D.png")); 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
	
		ConstantFunctionTerm1D constantTerm = new ConstantFunctionTerm1D( null ); 
		FunctionCreationOptions options = Core.getFunctionCreationOptions();
		
		ConstantFunctionTerm1D realTermT = (ConstantFunctionTerm1D) constantTerm.getDefaultInstance();
		ConstantFunctionTerm1D imagTermT = (ConstantFunctionTerm1D) constantTerm.getDefaultInstance();
		
		CosineFunctionTerm1D cosTerm = new CosineFunctionTerm1D( null ); 
		SineFunctionTerm1D sinTerm = new SineFunctionTerm1D( null ); 
		CosineFunctionTerm1D realTermR = (CosineFunctionTerm1D) cosTerm.getDefaultInstance(); 
		SineFunctionTerm1D imagTermR = (SineFunctionTerm1D) sinTerm.getDefaultInstance(); 
		realTermR.setWidth( options.getDefaultWidthX2D() ); 
		imagTermR.setWidth( options.getDefaultWidthX2D() );
		
		addPolarReImFunction2D( realTermR, realTermT, imagTermR, imagTermT, "radial complex exponential" );
	}

}
