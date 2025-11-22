/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D DeltaPairEven Function
 */
public class DeltaPairEvenFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public DeltaPairEvenFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public DeltaPairEvenFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] deltaPairEven = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		
		int index = (int) (center - indices[0]);				

		//check to make sure that the the larger index is not out of bounds, and add it
		int right = index + (int)width; 
		if ( right <  dimension && right >= 0 ) {
			
			deltaPairEven[right] = amplitude; 
		}
		
		//check to makes sure the smaller index is not out of bounds
		int left  = index - (int)width; 
		if( ( left >= 0 && left < dimension ) ) {
			
			deltaPairEven[left] = amplitude;
		}	
		
		return deltaPairEven;
		
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
//		model.setLargeIcon("/functionIcons/DeltaPairEven1DLarge.png");
//		model.setSmallIcon("/functionIcons/DeltaPairEven1DSmall.png");
		model.setName("Even Delta Pair");
		
		model.setDocPath("/functiondoc/evendelta.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "(\u03B4["+formattedParamStringNeg(variables[0])+"] + \u03B4["+formattedParamString(variables[0])+"])"; 
	}
}
