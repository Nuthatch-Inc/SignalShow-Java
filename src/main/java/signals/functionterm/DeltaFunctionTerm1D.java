/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Delta Function
 */
public class DeltaFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public DeltaFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public DeltaFunctionTerm1D(ParameterBlock paramBlock) {

		super(paramBlock);

	}


	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {

		double center = getCenter();
		double amplitude = getAmplitude();

		int dimension = indices.length; 
		double[] delta = new double[ dimension ];	
		
		int location = (int) (center-indices[0]);
		if( location < dimension && location >= 0 )
		delta[location] = amplitude;

		return delta;

	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
		Class[] param_classes = { Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = { "A"+'\u2080', "x"+'\u2080' }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/Delta1DLarge.png");
//		model.setSmallIcon("/functionIcons/Delta1DSmall.png");
		model.setName("Delta");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/delta.html");
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
		return amplitudeMultiplierString() + "\u03B4["+formattedParamString(variables[0])+"]"; 
	}
}
