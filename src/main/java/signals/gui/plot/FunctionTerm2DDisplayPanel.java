package signals.gui.plot;

import java.awt.Dimension;

import signals.core.FunctionTerm2D;

@SuppressWarnings("serial")
public class FunctionTerm2DDisplayPanel extends ImageDisplayPanel {

	FunctionTerm2D currentFunction;

	public FunctionTerm2DDisplayPanel( Dimension displayDimension ) {
		
		super( displayDimension );
	}
	
	/**
	 * @param currentFunction the currentFunction to set
	 */
	public void setFunctionTerm( FunctionTerm2D currentFunction ) {

		this.currentFunction = currentFunction;
		refreshView(); 
	}
	
	public void refreshView() {
		
		if( currentFunction != null ) {
			
			double[] data = currentFunction.create( x_dimension, y_dimension, zeroCentered );
			display( data );
		}
	}

}
