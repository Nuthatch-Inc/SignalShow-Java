package signals.core;

import signals.gui.Function2DThumbnailGraphic;
import signals.operation.PhaseUnwrapper2D;

public abstract class Function2D extends Function {
	
	//two function parts: either real and imaginary or magnitude and phase
	FunctionPart2D partA, partB; 
	
	//dimensions of this function
	int dimensionX, dimensionY; 
	
	public Function2D( FunctionPart2D partA, FunctionPart2D partB, String descriptor,  int dimensionX,
			int dimensionY, boolean zeroCentered ) {
		super(descriptor, zeroCentered);
		this.partA = partA;
		this.partB = partB;
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		thumbnailGraphic = new Function2DThumbnailGraphic(this);
	}

	public FunctionPart2D getPartA() {
		return partA;
	}

	public void setPartA(FunctionPart2D partA) {
		this.partA = partA;
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	public FunctionPart2D getPartB() {
		return partB;
	}

	public void setPartB(FunctionPart2D partB) {
		this.partB = partB;
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	public int getDimensionX() {
		return dimensionX;
	}

	public void setDimensionX(int dimensionX) {
		this.dimensionX = dimensionX;
		real = null; 
		imaginary = null; 
		magnitude = null; 
		phase = null;
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	public int getDimensionY() {
		return dimensionY;
	}

	public void setDimensionY(int dimensionY) {
		this.dimensionY = dimensionY;
		real = null; 
		imaginary = null; 
		magnitude = null; 
		phase = null;
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	@Override
	public void setZeroCentered(boolean zeroCentered) {
		super.setZeroCentered(zeroCentered);
		real = null; 
		imaginary = null; 
		magnitude = null; 
		phase = null;
		thumbnailGraphic.setDirty();
		modifyID(); 
	}

	
	@Override
	public String getIndependentVariables() {
		return "x, y";
	}
	
	@Override
	public double[] getUnwrappedPhase() {
		
		PhaseUnwrapper2D unwrapper = new PhaseUnwrapper2D(getPhase(), getDimensionX(), getDimensionY());
		return unwrapper.unwrap();
	}

	public Function getYProfile(int rise, int y0) {
		
		return ImageProfile.getYProfile(rise, y0, zeroCentered, dimensionX, 
				dimensionY, getReal(), getImaginary(), getCompactDescriptor());
	}

	public Function getXProfile(int run, int x0) {
		
		return ImageProfile.getXProfile(run, x0, zeroCentered, dimensionX, 
				dimensionY, getReal(), getImaginary(), getCompactDescriptor());
	} 

}
