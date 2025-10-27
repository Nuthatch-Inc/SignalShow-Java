/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D ParzenWindow Function
 */
public class ExtCosBellWindowFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public ExtCosBellWindowFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public ExtCosBellWindowFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] window = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		
		for ( int i = 0; i < dimension; ++i )  {
			
			if( (indices[i] >= center - width) && (indices[i] <= center + width) ) { 
				
				double arg = ( indices[i] - center ) / width;
				if( ( arg <= -0.4 ) || ( arg >= 0.4 ) ) {
					
					window[i] = amplitude * 0.5 * ( 1 - Math.cos(  Math.PI * ( arg ) ) );
					
				} else {
					
					window[i] = amplitude;
				}
			}
		}
		
		return window;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);

		model.setName("Extended Cosine Bell Window");
		
		model.setDocPath("/functiondoc/extcosbellwindow.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "ExtCosBell["+formattedParamString(variables[0])+"]"; 
	}
}
