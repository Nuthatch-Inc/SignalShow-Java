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
public class HanningWindowFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public HanningWindowFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public HanningWindowFunctionTerm1D(ParameterBlock paramBlock) {
		
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
				window[i] = amplitude * 0.5 * ( 1 + Math.cos(  Math.PI * arg ) );
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

		model.setName("Hanning Window");
		
		model.setDocPath("/functiondoc/hanningwindow.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Hanning["+formattedParamString(variables[0])+"]"; 
	}
}
