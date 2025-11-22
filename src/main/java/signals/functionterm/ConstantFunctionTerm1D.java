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
public class ConstantFunctionTerm1D extends AnalyticFunctionTerm1D { 
	
	public ConstantFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param paramBlock
	 */
	public ConstantFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
		
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		double[] constant = new double[ dimension ];		
		
		double amplitude = getAmplitude();
								
		for ( int i = 0; i < dimension; ++i )  { 
			constant[i] = amplitude; 
		}	
		
		return constant;
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		
		Class[] param_classes = { Double.class};
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = { "A"+'\u2080'}; 
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/Constant1DLarge.png");
//		model.setSmallIcon("/functionIcons/Constant1DSmall.png");
		model.setName("Constant");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/constant.html");
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#hasCenter()
	 */
	@Override
	public boolean hasCenter() {
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
		return amplitudeMultiplierString() + "1["+variables[0]+"]"; 
	}
}
