/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;
import signals.core.Zeros;

/**
 * @author Juliet
 */
public class ZeroFunctionTerm1D extends AnalyticFunctionTerm1D {
	
	/**
	 * @param paramBlock
	 */
	public ZeroFunctionTerm1D(ParameterBlock paramBlock) {
		
		super(paramBlock);
	}

	public ZeroFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {
		
		int dimension = indices.length; 
		return Zeros.zeros(dimension);	
	}
	
	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/functionIcons/Zero1DLarge.png");
//		model.setSmallIcon("/functionIcons/Zero1DSmall.png");
		model.setName("Zero");
		
		//parameter classes with exponent
		Class[] param_classes = {};
		model.setParamClasses( param_classes );
		
		//default paramNames
		String[] param_names = {}; 
		model.setParamNames( param_names );

		SpinnerNumberModel[] spinnerModels = {};
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/zero.html");
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#hasAmplitude()
	 */
	@Override
	public boolean hasAmplitude() {
		return false; 
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
		return "0["+variables[0]+"]";
	}
}
