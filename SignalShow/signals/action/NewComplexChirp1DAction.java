/**
 * 
 */
package signals.action;

import java.awt.event.ActionEvent;

import signals.core.Core;
import signals.core.FunctionCreationOptions;
import signals.functionterm.ChirpFunctionTerm1D;
import signals.functionterm.SineChirpFunctionTerm1D;
import signals.io.ResourceLoader;


/**
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class NewComplexChirp1DAction extends NewComplexFunctionAction {

	static final String description = "New 1D Complex Chirp";

	/**
	 * 
	 */
	public NewComplexChirp1DAction() {
		super(description, ResourceLoader.createImageIcon("/guiIcons/chirp1D.png")); 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		ChirpFunctionTerm1D chirpTerm = new ChirpFunctionTerm1D( null ); 
		SineChirpFunctionTerm1D sineChirpTerm = new SineChirpFunctionTerm1D( null ); 
		FunctionCreationOptions options = Core.getFunctionCreationOptions();

		ChirpFunctionTerm1D realTerm = (ChirpFunctionTerm1D) chirpTerm.getDefaultInstance(); 
		SineChirpFunctionTerm1D imagTerm = (SineChirpFunctionTerm1D) sineChirpTerm.getDefaultInstance(); 
		realTerm.setWidth( options.getDefaultWidth1D() ); 
		imagTerm.setWidth( options.getDefaultWidth1D() );

		addReImFunction1D( realTerm, imagTerm, "complex chirp");  
	}

}
