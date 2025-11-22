/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Gaussian Function
 */
public class GaussianFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public GaussianFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}


	public static int exponent_idx = 3;
	
	/**
	 * @param paramBlock
	 */
	public GaussianFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}
	
	/**
	 * @return the exponent
	 */
	public int getExponent() {
		
		return paramBlock.getIntParameter(exponent_idx);
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] gaussian = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		int exponent = getExponent();
									
		for ( int i = 0; i < dimension; ++i )  { 
			
			double argument = Math.abs((indices[i] - center) / width); 
			gaussian[i] = amplitude * Math.exp( -Math.PI * Math.pow(argument, exponent) ); 
			
		}	
		
		return gaussian;
		
	}
	
	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
		//parameter classes with exponent
		Class[] param_classes = { Double.class, Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = { "A"+'\u2080', "x"+'\u2080', "b", "n" };
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Integer( 2 ) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/Gaussian1DLarge.png");
//		model.setSmallIcon("/functionIcons/Gaussian1DSmall.png");
		model.setName("Gaussian");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel exponentSpinnerModel = new SpinnerNumberModel( new Integer(2), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, widthSpinnerModel, exponentSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/gaus.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		
		if( getExponent() == 2 ) return amplitudeMultiplierString() + "GAUS["+formattedParamString(variables[0])+"]";
		return amplitudeMultiplierString() + "GAUS["+formattedParamString(variables[0])+"; "+getExponent()+"]"; 
	}
}
