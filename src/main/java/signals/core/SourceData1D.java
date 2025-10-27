package signals.core;

import signals.core.Constants.Part;
import signals.gui.plot.Indices;

public class SourceData1D extends SourceData {
	
	public SourceData1D(Function function, Part part) {
		super(function, part);
	
	}

	public SourceData1D(Function function, Part part, String label) {
		super(function, part, label);
	}

	public double[] getIndices1D() {
		
		int dimension = ((Function1D)function).getDimension();
		boolean zeroCentered = function.isZeroCentered();
		return Indices.indices1D( dimension, zeroCentered );
	}

}
