package signals.gui.plot;

import signals.core.FunctionTerm1D;
import signals.gui.PlotThumbnailGraphic;

public class FunctionTerm1DThumbnailGraphic extends PlotThumbnailGraphic {
	
	//the data that this thumbnail graphic represents
	protected FunctionTerm1D function; 
	/**
	 * @param function
	 */
	public FunctionTerm1DThumbnailGraphic(FunctionTerm1D function, double[] indices) {
		super( indices );
		this.function = function;
	}
	
	/**
	 * @param function the function to set
	 */
	public void setFunction(FunctionTerm1D function) {
		
		this.function = function;
		setDirty(); 
	}
	
	public void createData() {
		
		data = function.create( indices );
	}

}
