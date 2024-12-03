/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D WelchWindow Function
 */
public class WelchWindowFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public WelchWindowFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WelchWindowFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public WelchWindowFunctionTerm1D(ParameterBlock paramBlock) {
		
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
				window[i] = amplitude * (1 - arg * arg);
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
//		model.setLargeIcon("/functionIcons/WelchWindow1DLarge.png");
//		model.setSmallIcon("/functionIcons/WelchWindow1DSmall.png");
		model.setName("Welch Window");
		
		model.setDocPath("/functiondoc/welchwindow.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Welch["+formattedParamString(variables[0])+"]"; 
	}
}
