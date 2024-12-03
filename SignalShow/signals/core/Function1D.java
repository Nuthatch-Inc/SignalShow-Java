package signals.core;

import signals.gui.Function1DThumbnailGraphic;
import signals.gui.plot.Indices;
import signals.operation.PhaseUnwrapper1D;



/**
 * Represents a 1D complex function in terms of real and imaginary parts or magnitude and phase. 
 * @author Juliet
 */
public abstract class Function1D extends Function { 
	
	//dimension of this function
	int dimension;
	
	//two function parts: either real and imaginary or magnitude and phase
	FunctionPart1D partA, partB;

	public Function1D(FunctionPart1D partA, FunctionPart1D partB, 
			String descriptor, int dimension, boolean zeroCentered ) {
		super(descriptor, zeroCentered);
		this.dimension = dimension;
		this.partA = partA;
		this.partB = partB;
		thumbnailGraphic = new Function1DThumbnailGraphic(this);
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
		real = null; 
		imaginary = null; 
		magnitude = null; 
		phase = null;
		((Function1DThumbnailGraphic)thumbnailGraphic).setIndices(Indices.indices1D(dimension, zeroCentered)); 
		thumbnailGraphic.setDirty();
		modifyID();  
	}
	
	public void setZeroCentered(boolean zeroCentered ) {
		
		this.zeroCentered = zeroCentered;
		real = null; 
		imaginary = null; 
		magnitude = null; 
		phase = null;
		((Function1DThumbnailGraphic)thumbnailGraphic).setIndices(Indices.indices1D(dimension, zeroCentered)); 
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	public FunctionPart1D getPartA() {
		return partA;
	}

	public void setPartA(FunctionPart1D partA) {
		this.partA = partA;
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	public FunctionPart1D getPartB() {
		return partB;
	}

	public void setPartB(FunctionPart1D partB) {
		this.partB = partB;
		thumbnailGraphic.setDirty();
		modifyID(); 
	} 
	
	@Override
	public double[] getUnwrappedPhase() {

		return PhaseUnwrapper1D.unwrapItoh(getPhase(), 0);
	}
	
	@Override
	public String getIndependentVariables() {
		return "x";
	}
	
}
