/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D WelchWindow Function
 */
public class MonomialFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public MonomialFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int exponent_idx = 3;
	
	/**
	 * @param paramBlock
	 */
	public MonomialFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}
	
	/**
	 * @return the exponent
	 */
	public int getExponent() {
		
		return paramBlock.getIntParameter(exponent_idx);
	}
	
	/**
	 * @param exponent the exponent to set
	 */
	public void setExponent(int exponent) {
		
		paramBlock.set( exponent, exponent_idx );

	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] monomial = new double[ dimension ];		
		
		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		int exponent = getExponent();
		
		for ( int i = 0; i < dimension; ++i )  {
				
				double arg = ( indices[i] - center ) / ( width );
				monomial[i] = amplitude * Math.pow(arg, exponent);
		}
		
		return monomial;
		
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
		String[] param_names =  { "A"+'\u2080', "x"+'\u2080', "b", "n" };
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Integer( 2 ) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/Monomial1DLarge.png");
//		model.setSmallIcon("/functionIcons/Monomial1DSmall.png");
		model.setName("Monomial");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel exponentSpinnerModel = new SpinnerNumberModel( new Integer(2), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, widthSpinnerModel, exponentSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/monomial.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "["+formattedParamString(variables[0])+"]^" + getExponent(); 
	}
}
