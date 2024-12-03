package signals.core;

import signals.gui.datagenerator.MagPhaseFunction2DToolBar;
import signals.operation.ArrayMath;

public class MagPhaseFunction2D extends Function2D {


	public MagPhaseFunction2D(FunctionPart2D partA, FunctionPart2D partB,
			String descriptor, int dimensionX, int dimensionY,
			boolean zeroCentered) {
		super(partA, partB, descriptor, dimensionX, dimensionY, zeroCentered);
	
	}

	public MagPhaseFunction2D clone() {
		
		return new MagPhaseFunction2D( partA, partB, 
				Core.getFunctionCreationOptions().getNextFunctionName(), dimensionX, dimensionY, zeroCentered );
	}

	public void show() {
		
		MagPhaseFunction2DToolBar toolBar = new MagPhaseFunction2DToolBar( this, newState );
		Core.getGUI().setToolBar( toolBar ); 
		toolBar.selectButton(showState); 
		newState = false; 
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
		
		return partA.create(dimensionX, dimensionY, zeroCentered); 
	}


	@Override
	protected double[] createPhase() {
	
		return partB.create(dimensionX, dimensionY, zeroCentered); 
	}
	
	@Override
	public void setPartA(FunctionPart2D partA) {
		super.setPartA(partA);
		real = null; 
		magnitude = null; 
		imaginary = null; 
	}

	@Override
	public void setPartB(FunctionPart2D partB) {
		super.setPartB(partB);
		imaginary = null; 
		real = null; 
		phase = null; 
	}
	
	public String getEquation() {
		
		return "("+partA.getEquation()+")exp[i(" + partB.getEquation() + ")]"; 
	}

}
