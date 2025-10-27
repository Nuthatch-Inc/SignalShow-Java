/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Sinc Function
 */
public class SincFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public SincFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SincFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public SincFunctionTerm1D(ParameterBlock paramBlock) {
		
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
			sinc[i] = ( argument == 0 ? amplitude : amplitude * Math.sin( argument ) / argument );	
		}	
		
		return sinc;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Sinc1DLarge.png");
//		model.setSmallIcon("/functionIcons/Sinc1DSmall.png");
		model.setName("Sinc");
		
		model.setDocPath("/functiondoc/sinc.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "SINC["+formattedParamString(variables[0])+"]"; 
	}
}
