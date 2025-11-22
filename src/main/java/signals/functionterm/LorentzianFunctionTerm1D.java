/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Lorentzian Function
 */
public class LorentzianFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public LorentzianFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public LorentzianFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] lorentzian = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
								
		for ( int i = 0; i < dimension; ++i )  { 
			
			double quantity = 2 * Math.PI * (indices[i]-center) / width;
			lorentzian[i] =  amplitude / (1 + ( quantity * quantity ) );
			
		}	
	
		return lorentzian;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Lorentzian1DLarge.png");
//		model.setSmallIcon("/functionIcons/Lorentzian1DSmall.png");
		model.setName("Lorentzian");
		
		String[] param_names = { "A"+'\u2080', "x"+'\u2080', "\u03B1" }; 
		model.setParamNames( param_names );
		
		model.setDocPath("/functiondoc/lor.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "LOR["+formattedParamString(variables[0])+"]"; 
	}
}
