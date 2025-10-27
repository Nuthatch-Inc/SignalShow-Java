/**
 * 
 */
package signals.action;

import java.awt.event.ActionEvent;

import signals.core.Core;
import signals.core.FunctionCreationOptions;
import signals.functionterm.ChirpFunctionTerm1D;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.SineChirpFunctionTerm1D;
import signals.io.ResourceLoader;


/**
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class NewComplexChirp2DAction extends NewComplexFunctionAction {
	
	static final String description = "New 2D Complex Chirp";
	
	/**
	 * 
	 */
	public NewComplexChirp2DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/chirp2D.png")); 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		ChirpFunctionTerm1D chirpTerm = new ChirpFunctionTerm1D( null ); 
		SineChirpFunctionTerm1D sineChirpTerm = new SineChirpFunctionTerm1D( null ); 
		ConstantFunctionTerm1D constantTerm = new ConstantFunctionTerm1D( null ); 
		FunctionCreationOptions options = Core.getFunctionCreationOptions();
		
		ConstantFunctionTerm1D constTerm = (ConstantFunctionTerm1D) constantTerm.getDefaultInstance();
		
		ChirpFunctionTerm1D realTermR = (ChirpFunctionTerm1D) chirpTerm.getDefaultInstance(); 
		SineChirpFunctionTerm1D imagTermR = (SineChirpFunctionTerm1D) sineChirpTerm.getDefaultInstance(); 
		realTermR.setWidth( options.getDefaultWidthX2D() ); 
		imagTermR.setWidth( options.getDefaultWidthX2D() );
		
		addPolarReImFunction2D( realTermR, constTerm, imagTermR, constTerm, "complex chirp" ); 
	}

}
