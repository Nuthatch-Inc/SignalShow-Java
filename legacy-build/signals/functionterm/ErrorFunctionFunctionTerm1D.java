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
public class ErrorFunctionFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public ErrorFunctionFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public ErrorFunctionFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] errorFunction = new double[ dimension ];		

		int NOver2 = dimension / 2;
		
		double[] polyCoef = {1, .0705230784, .0422820123, .0092705272, 
						.0001520143,  .000276572, .0000430638 }; 
		
		for( int i = 0; i < NOver2; i++ ) {
			
			double x = 4.0 * Math.abs(i - NOver2) / (double)dimension;
			double errF = 1.0 -  1.0 / Math.pow( Polynomial.calcPoly( x, polyCoef, 6 ), 16 );
			errorFunction[i] = -errF;
			
			if( i != 0 )  {
				
				errorFunction[dimension - i] = errF;
			}
		}
		
		return errorFunction;
	
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		
		//parameter classes with exponent
		Class[] param_classes = {};
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = {}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = {}; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/ErrorFunction1DLarge.png");
//		model.setSmallIcon("/functionIcons/ErrorFunction1DSmall.png");
		model.setName("ErrorFunction");
		
		SpinnerNumberModel[] spinnerModels = { };
		model.setSpinnerModels(spinnerModels);
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#hasCenter()
	 */
	@Override
	public boolean hasCenter() {
		return false; 
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#getAmplitude()
	 */
	@Override
	public boolean hasAmplitude() {
		return false;
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#hasWidth()
	 */
	@Override
	public boolean hasWidth() {
		return false;
	}
	
	@Override
	public String getEquation(String[] variables) {
		
		return amplitudeMultiplierString() + "erf["+formattedParamStringNeg(variables[0])+"]"; 
	}
}
