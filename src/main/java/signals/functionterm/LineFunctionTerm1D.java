/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Line Function
 */
public class LineFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public LineFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public LineFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] line = new double[ dimension ];		
		
		double width = getWidth();
		double center = getCenter(); //intercept
		double amplitude = getAmplitude();
						
		for ( int i = 0; i < dimension; i++ )  { 			
		
			line[i] = amplitude * ( indices[i]-center ) / (width); 
		}
		
		return line;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Line1DLarge.png");
//		model.setSmallIcon("/functionIcons/Line1DSmall.png");
		model.setName("Line");
		model.setDocPath("/functiondoc/line.html");
	
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "("+ formattedParamString(variables[0]) + ")"; 
	}
}
