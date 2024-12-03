package signals.core;

import signals.gui.datagenerator.ComplexReImFunction2DToolBar;
import signals.gui.datagenerator.FourierBasisReImFunction2DToolBar;
import signals.gui.datagenerator.ReImFunction2DToolBar;
import signals.operation.ArrayMath;

public class RealImagFunction2D extends Function2D {

	public RealImagFunction2D(FunctionPart2D partA, FunctionPart2D partB,
			String descriptor, int dimensionX, int dimensionY,
			boolean zeroCentered) {
		super(partA, partB, descriptor, dimensionX, dimensionY, zeroCentered);
	}

	public RealImagFunction2D clone() {
		
		return new RealImagFunction2D( partA, partB, 
				Core.getFunctionCreationOptions().getNextFunctionName(), dimensionX, dimensionY, zeroCentered );
	}

	public void show() {
		
		ReImFunction2DToolBar toolBar = null; 
		switch( type ) {
		
		case NOT_COMPLEX: 
			
			toolBar = new ReImFunction2DToolBar( this, newState ); 
			break; 
			
		case COMPLEX: 
			
			toolBar = new ComplexReImFunction2DToolBar( this );
			break; 
			
		case FOURIER_BASIS: 
			
			toolBar = new FourierBasisReImFunction2DToolBar(this);
			break; 
		
		}
	
		Core.getGUI().setToolBar( toolBar ); 
		toolBar.selectButton(showState);
		newState = false; 
		
	}
	
	
	@Override
	protected double[] createReal() {

		return partA.create(dimensionX, dimensionY, zeroCentered); 

	}


	@Override
	protected double[] createImaginary() {

		return partB.create(dimensionX, dimensionY, zeroCentered); 

	}

	@Override
	protected double[] createMagnitude() {

		return ArrayMath.magnitude(getReal(), getImaginary()); 

	}

	
	@Override
	protected double[] createPhase() {
		
		return ArrayMath.phase(getReal(), getImaginary() ); 

	}

	@Override
	public void setPartA(FunctionPart2D partA) {
		super.setPartA(partA);
		real = null; 
		magnitude = null; 
		phase = null; 
	}

	@Override
	public void setPartB(FunctionPart2D partB) {
		super.setPartB(partB);
		imaginary = null; 
		magnitude = null; 
		phase = null; 
	}

	public String getEquation() {

		return partA.getEquation() + " + i(" + partB.getEquation() + ")"; 
	}
}
