/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.ImageIcon;

import signals.core.DataGenerator;
import signals.gui.IconCache;

/**
 * @author Juliet
 *
 */
public class XYFunctionTerm2D extends SeparableFunctionTerm2D {

	public XYFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public XYFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	@Override
	public DataGenerator getInstance(ParameterBlock paramBlock) {
		return new XYFunctionTerm2D( paramBlock );
	}

	@Override
	public double[] create(int x_dimension, int y_dimension, boolean zeroCentered) {
		
		double[] x_data = functionTerm1DA.create( x_dimension, zeroCentered );
		double[] y_data = functionTerm1DB.create( y_dimension, zeroCentered );
		
		double[] output = new double[x_dimension*y_dimension];
		
		int idx = 0; 
		
		for( int j = 1; j <= y_dimension; ++j ) {
			
			for( int i = 0; i < x_dimension; ++i ) {
				
				output[idx++] = x_data[i]*y_data[y_dimension-j];
				
			}
		}
		
		return output; 
	}


	public double[] create(double[] x_indices, double[] y_indices) {
		
		int dimension = x_indices.length;
		
		double[] x_data = functionTerm1DA.create( x_indices );
		double[] y_data = functionTerm1DB.create( y_indices );
		
		double[] output = new double[dimension]; 
		
		for( int i = 0; i < dimension; ++i ) {
			
			output[i] = x_data[i]*y_data[i];
			
		}
		return output;
	}

	@Override
	public String getPartAName() {
		return "Horizontal Part";
	}

	@Override
	public String getPartBName() {
		return "Vertical Part";
	}

	public String getEquation( String[] variables ) {
		
		String[] xVariables = { "x" }; 
		String[] yVariables = { "y" }; 
		
		return( functionTerm1DA.getEquation(xVariables) + 
					functionTerm1DB.getEquation(yVariables) ); 
	}
	
	@Override
	public ImageIcon getPartAIcon() {

		return IconCache.getIcon("/guiIcons/fxLabel.png"); 
	}

	@Override
	public ImageIcon getPartBIcon() {

		return IconCache.getIcon("/guiIcons/fyLabel.png"); 
	}
	
}
