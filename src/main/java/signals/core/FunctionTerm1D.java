package signals.core;

import java.awt.image.renderable.ParameterBlock;

/**
 * provides a way of obtaining data that varies according 
 * to one independent variable, along with some number of parameters. 
 * @author Juliet
 *
 */
public abstract class FunctionTerm1D extends DataGenerator implements FunctionTerm {
	
	/**
	 * Constructor that accepts a ParameterBlock
	 */
	public FunctionTerm1D( ParameterBlock paramBlock ) {
		super( paramBlock );
	}
	
	public FunctionTerm1D() {
		super();
	}

	public FunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Adds the data represented by this function at given x indices to the output. 
	 * 
	 * @param indices the indices over which this function is defined
	 * @param output output array to which to add the data (this is modified)
	 * @return the output array with the sum stored in it
	 */
	public abstract double[] create( double[] indices );
	
	/**
	 * Adds the data represented by this function at equally spaced integer indices to the output
	 * 
	 * @param dimension
	 * @param output
	 * @return the output array with the sum stored in it
	 */
	public abstract double[] create( int dimension, boolean zeroCentered );
	
	/**
	 * Sets the scale factor by which the width of the function is stretched
	 * @param scale
	 */
	public abstract void setWidthScale( double scale );

} //FunctionTerm1D
