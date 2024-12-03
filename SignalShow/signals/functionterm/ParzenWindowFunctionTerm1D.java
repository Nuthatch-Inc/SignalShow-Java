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
public class ParzenWindowFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public ParzenWindowFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public ParzenWindowFunctionTerm1D(ParameterBlock paramBlock) {
		
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
			
				if( ( arg < -0.5 ) || ( arg > 0.5 ) ) {
					
					window[i] = amplitude * 2 * Math.pow( ( 1 - Math.abs( arg )  ) ,  3 ); 
					
				} else {
					
					window[i] = amplitude * (1 - 6 * ( arg * arg ) + 6 * Math.pow( Math.abs(arg),  3 ) );
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
//		model.setLargeIcon("/functionIcons/ParzenWindow1DLarge.png");
//		model.setSmallIcon("/functionIcons/ParzenWindow1DSmall.png");
		model.setName("Parzen Window");
		
		model.setDocPath("/functiondoc/parzenwindow.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Parzen["+formattedParamString(variables[0])+"]"; 
	}
}
