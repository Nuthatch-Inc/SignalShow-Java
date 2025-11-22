package signals.core;

import java.awt.image.renderable.ParameterBlock;

/**
 * provides a way of obtaining data that varies according 
 * to one independent variable, along with some number of parameters. 
 * @author Juliet
 *
 */
public abstract class FunctionTerm2D extends DataGenerator implements FunctionTerm {

	
	public FunctionTerm2D() {
		super();
	}

	public FunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}


	/**
	 * Adds the data represented by this function at equally spaced integer indices to the output
	 * 
	 * @param x_dimension 
	 * @param y_dimension 
	 * @param output
	 * @return the output array with the sum stored in it
	 */
	public abstract double[] create( int x_dimension, int y_dimension, boolean zeroCentered );
	

} //FunctionTerm1D
