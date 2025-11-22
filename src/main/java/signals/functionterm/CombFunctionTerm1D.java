/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;

import signals.core.Core;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Comb Function
 */
public class CombFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public CombFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public CombFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] comb = new double[ dimension ];		
		
		int width = (int) Math.round(getWidth()); 
		double center = (int)Math.round(getCenter());
		double amplitude = getAmplitude();
									
		for ( int i = 0; i < dimension; ++i )  { 
			
			double argument = indices[i] - center; 
			comb[i] = ( ( argument )/width == (int)( ( argument )/width ) ) ? amplitude : 0;
			
		}	
		
		return comb;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Comb1DLarge.png");
//		model.setSmallIcon("/functionIcons/Comb1DSmall.png");
		model.setName("Comb");
		
		model.setDocPath("/functiondoc/comb.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		NumberFormat format = Core.getDisplayOptions().getFormat();
		return amplitudeMultiplierString() + "(1/" + format.format(Math.abs(getWidth())) + ")COMB["+formattedParamString(variables[0])+"]";
	}
}
