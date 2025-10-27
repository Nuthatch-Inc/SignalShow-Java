/**
 * 
 */
package signals.core;

import signals.gui.datagenerator.ComplexReImFunction1DToolBar;
import signals.gui.datagenerator.ReImFunction1DToolBar;
import signals.operation.ArrayMath;

/**
 * @author Juliet
 *
 */
public class RealImagFunction1D extends Function1D {

	public RealImagFunction1D(FunctionPart1D partA, FunctionPart1D partB,
			String descriptor, int dimension, boolean zeroCentered) {
		super(partA, partB, descriptor, dimension, zeroCentered);
	}

	public RealImagFunction1D clone() {

		return new RealImagFunction1D( partA, partB, 
				Core.getFunctionCreationOptions().getNextFunctionName(), dimension, zeroCentered );

	}

	public void show() {

		ReImFunction1DToolBar toolBar = null; 
		switch( type ) {

		case NOT_COMPLEX: 

			toolBar = new ReImFunction1DToolBar( this ); 
			break; 

		case COMPLEX: 

			toolBar = new ComplexReImFunction1DToolBar( this );
			break; 
		}

		Core.getGUI().setToolBar( toolBar ); 
		toolBar.selectButton(showState); 
	}

	@Override
	protected double[] createReal() {

		return partA.create(dimension, zeroCentered); 

	}


	@Override
	protected double[] createImaginary() {

		return partB.create(dimension, zeroCentered); 

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
	public void setPartA(FunctionPart1D partA) {
		super.setPartA(partA);
		real = null; 
		magnitude = null; 
		phase = null; 
	}

	@Override
	public void setPartB(FunctionPart1D partB) {
		super.setPartB(partB);
		imaginary = null; 
		magnitude = null; 
		phase = null; 
	}

	public String getEquation() {

		return partA.getEquation() + " + i(" + partB.getEquation() + ")"; 
	}

}
