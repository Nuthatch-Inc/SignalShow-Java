package signals.gui.datagenerator;

import signals.functionterm.AnalyticFunctionTerm2D;

public interface AnalyticFunctionTerm2DEditor {
	
	public void setIndices( int x_dimension, int y_dimension, boolean zeroCentered );
	
	public void setFunctionTerm( AnalyticFunctionTerm2D term );
}
