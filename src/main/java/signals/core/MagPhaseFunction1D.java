package signals.core;

import signals.gui.datagenerator.MagPhaseFunction1DToolBar;
import signals.operation.ArrayMath;

public class MagPhaseFunction1D extends Function1D {

	public MagPhaseFunction1D(FunctionPart1D partA, FunctionPart1D partB,
			String descriptor, int dimension, boolean zeroCentered) {
		super(partA, partB, descriptor, dimension, zeroCentered);
	}


	public MagPhaseFunction1D clone() {
		
		return new MagPhaseFunction1D( partA, partB, 
				Core.getFunctionCreationOptions().getNextFunctionName(), dimension, zeroCentered );
	}

	public void show() {
		
		MagPhaseFunction1DToolBar toolBar = new MagPhaseFunction1DToolBar( this );
		Core.getGUI().setToolBar( toolBar ); 
		toolBar.selectButton(showState); 
	}

	
	@Override
	protected double[] createReal() {
		
		return ArrayMath.real(getMagnitude(), getPhase()); 
	}

	@Override
	protected double[] createImaginary() {
		
		return ArrayMath.imaginary(getMagnitude(), getPhase()); 
	}


	@Override
	protected double[] createMagnitude() {
		
		return partA.create(dimension, zeroCentered); 
	}


	@Override
	protected double[] createPhase() {
	
		return partB.create(dimension, zeroCentered); 
	}
	
	@Override
	public void setPartA(FunctionPart1D partA) {
		super.setPartA(partA);
		real = null; 
		magnitude = null; 
		imaginary = null; 
	}

	@Override
	public void setPartB(FunctionPart1D partB) {
		super.setPartB(partB);
		imaginary = null; 
		real = null; 
		phase = null; 
	}
	
	public String getEquation() {
		
		return "("+partA.getEquation()+")exp[i(" + partB.getEquation() + ")]"; 
	}

}
