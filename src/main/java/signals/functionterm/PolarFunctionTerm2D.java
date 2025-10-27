/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.ImageIcon;

import signals.core.DataGenerator;
import signals.core.PolarFunctionMath;
import signals.gui.IconCache;
import signals.gui.plot.Indices;

/**
 * @author Juliet
 *
 */
public class PolarFunctionTerm2D extends SeparableFunctionTerm2D {

	public PolarFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public PolarFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}

	@Override
	public double[] create( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		double[] radius = Indices.sliceRadius( x_dimension, y_dimension, zeroCentered ); 		
		double[] r_data = PolarFunctionMath.reflect( functionTerm1DA.create( radius ), x_dimension, y_dimension, zeroCentered );
		double[] angle = Indices.angleIndices(x_dimension, y_dimension, zeroCentered );
		double[] angle_data = functionTerm1DB.create( angle );
				
		int dimension = x_dimension*y_dimension;
		
		double[] output = new double[dimension];

		int idx = 0; 

		for( int i = 0; i < dimension; ++i ) {

			output[idx++] = r_data[i]*angle_data[i];

		}
		
		return output; 
	}

	/** 
	 * calculates result, given r and theta indices 
	 */
	public double[] createPolar(double[] r_indices, double[] theta_indices) {

		double[] r_data = functionTerm1DA.create( r_indices );
		double[] angle_data = functionTerm1DB.create( theta_indices );

		int dimension = r_indices.length; 
		
		double[] output = new double[dimension];

		int idx = 0; 

		for( int i = 0; i < dimension; ++i ) {

			output[idx++] = r_data[i]*angle_data[i];

		}
		
		return output; 

	}

	@Override
	public DataGenerator getInstance(ParameterBlock paramBlock) {
		return new PolarFunctionTerm2D( paramBlock );
	}

	@Override
	public String getPartAName() {
		return "Radial Part";
		
	}

	@Override
	public String getPartBName() {
		return "Angular Part";
	}
	
	public String getEquation( String[] variables ) {
		
		String[] polarVariables = { "r" }; 
		String[] thetaVariables = { "\u03B8" }; 
		
		return( functionTerm1DA.getEquation(polarVariables) + 
					functionTerm1DB.getEquation(thetaVariables) ); 
	}

	@Override
	public ImageIcon getPartAIcon() {

		return IconCache.getIcon("/guiIcons/frLabel.png"); 
	}

	@Override
	public ImageIcon getPartBIcon() {

		return IconCache.getIcon("/guiIcons/fthetaLabel.png"); 
	}

}
