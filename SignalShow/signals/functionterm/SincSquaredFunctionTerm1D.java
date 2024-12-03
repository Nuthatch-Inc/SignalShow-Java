/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D SincSquared Function
 */
public class SincSquaredFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public SincSquaredFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SincSquaredFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public SincSquaredFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] sinc = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		
		double piOverHalfWidth = Math.PI / width; 							
		for ( int i = 0; i < dimension; ++i )  { 
			
			double argument = piOverHalfWidth * (indices[i] - center); 
			double sinc1 = ( argument == 0 ? 1 : Math.sin( argument ) / argument );
			sinc[i] = amplitude * sinc1 * sinc1;
		}	
		
		return sinc;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/SincSquared1DLarge.png");
//		model.setSmallIcon("/functionIcons/SincSquared1DSmall.png");
		model.setName("Sinc Squared");
		
		model.setDocPath("/functiondoc/sincsquared.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "SINC\00B2["+formattedParamString(variables[0])+"]"; 
	}
}
