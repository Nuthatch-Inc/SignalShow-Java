/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Rectangle Function
 */
public class RectangleFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public RectangleFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RectangleFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public RectangleFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 	
		
		double bound = getWidth();
		double center = getCenter();
		double amplitude = getAmplitude();
		
		double[] rect = new double[ dimension ];	
									
		for ( int i = 0; i < dimension; i++ )  {
			
			double argument =  Math.abs( indices[i] - center );
			
			if( argument < bound ) { //inside rectangle
				
				rect[i] = amplitude; 
				
			} else if ( argument == bound ) { //on border of rectangle
				
				rect[i] = amplitude / 2.0; 
			}
		}	
		
		return rect;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#isHalfWidthDefined()
	 */
	@Override
	public boolean isHalfWidthDefined() {
		return true;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Rectangle1DLarge.png");
//		model.setSmallIcon("/functionIcons/Rectangle1DSmall.png");
		model.setName("Rectangle");
		
		model.setDocPath("/functiondoc/rect.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "RECT["+formattedParamString(variables[0])+"]"; 
	}
}
