/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Step Function
 */
public class StepFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public StepFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}


	public StepFunctionTerm1D(DataGenerator datagenerator,
			ParameterBlock paramBlock) {
		super(datagenerator, paramBlock);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param paramBlock
	 */
	public StepFunctionTerm1D(ParameterBlock paramBlock) {

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
		double[] step = new double[ dimension ];	
		
		for( int i = 0; i<dimension; i++ ) {
			
			if( indices[i] > center ) { 

				step[i] = amplitude; 

			} else if( indices[i] == center ) { 

				step[i] = amplitude / 2.0; 

			} 

		}

		return step;

	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
		//parameter classes with exponent
		Class[] param_classes = { Double.class, Double.class };
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = { "A"+'\u2080', "x"+'\u2080' }; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/Step1DLarge.png");
//		model.setSmallIcon("/functionIcons/Step1DSmall.png");
		model.setName("Step");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/step.html");
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
		return amplitudeMultiplierString() + "STEP["+formattedParamString(variables[0])+"]"; 
	}
	
}
