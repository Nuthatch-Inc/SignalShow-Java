/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 */
public class GaussianDoubleSlitFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public GaussianDoubleSlitFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int slitwidth_idx = 3; 
	public static int exponent_idx = 4;
	
	/**
	 * @param paramBlock
	 */
	public GaussianDoubleSlitFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#isHalfWidthDefined()
	 */
	@Override
	public boolean isHalfWidthDefined() {
		return false;
	}

	/**
	 * @return the slit width
	 */
	public double getSlitWidth() {
		
		return paramBlock.getDoubleParameter(slitwidth_idx);
	}
	
	/**
	 * @param phase the slit width to set
	 */
	public void setSlitWidth(double slitWidth) {
		
		paramBlock.set( slitWidth, slitwidth_idx );

	}
	
	/**
	 * @return the slit width
	 */
	public int getExponent() {
		
		return paramBlock.getIntParameter(exponent_idx);
	}
	
	/**
	 * @param phase the slit width to set
	 */
	public void setExponent(double exp) {
		
		paramBlock.set( exp, exponent_idx );

	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 	
		
		double width = getWidth();
		double center = getCenter();
		double amplitude = getAmplitude();
		double slitWidth = getSlitWidth();
		int exponent = getExponent();
		
		double[] gaussian = new double[ dimension ];		

		for ( int i = 0; i < dimension; ++i )  { 
			
			double argument1 = Math.abs( (indices[i] - (center+width)) / slitWidth ); 
			double argument2 = Math.abs( (indices[i] - (center-width)) / slitWidth ); 
			gaussian[i] = amplitude * ( Math.exp( -Math.PI * Math.pow(argument1, exponent) )
										+ Math.exp( -Math.PI * Math.pow(argument2, exponent) ) ); 
			
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
		Class[] param_classes = { Double.class, Double.class, Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = { "Amplitude", "Center", "Width", "Slit Width", "Exponent" }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Double( 1 ), new Integer(2) }; 
		model.setParamDefaults( param_defaults );
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel slitWidthSpinnerModel = new SpinnerNumberModel( 1.0, 0.0, 100000.0, 1.0 );
		SpinnerNumberModel exponentSpinnerModel = new SpinnerNumberModel( new Integer(2), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, 
				widthSpinnerModel, slitWidthSpinnerModel, exponentSpinnerModel };
		model.setSpinnerModels(spinnerModels);
	
//		model.setLargeIcon("/functionIcons/DoubleSlit1DLarge.png");
//		model.setSmallIcon("/functionIcons/DoubleSlit1DSmall.png");
		model.setName("Gaussian Double Slit");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "Gaussian Double Slit"; //TODO: name
	}
}
