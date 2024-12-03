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
public class HammingWindowFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public HammingWindowFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public HammingWindowFunctionTerm1D(ParameterBlock paramBlock) {
		
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
				window[i] = amplitude * ( 0.54 + 0.46 * Math.cos(  Math.PI * arg ) );
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

		model.setName("Hamming Window");
		
		model.setDocPath("/functiondoc/hammingwindow.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Hamming["+formattedParamString(variables[0])+"]"; 
	}
}
