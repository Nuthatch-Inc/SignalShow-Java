package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.OperationOptionsPanel;
import signals.gui.operation.ParameterOptionsPanel;

public class NormalizeOp extends UnaryOperation {
	
	public NormalizeOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NormalizeOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	double scaleFactorReal, scaleFactorImag; 

	public NormalizeOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}

	/* (non-Javadoc)
	 * @see signals.core.Operation#getOptionsInterface()
	 */
	@Override
	public OperationOptionsPanel getOptionsInterface() {
		
		return new ParameterOptionsPanel( this );
	}
	
	
	@Override
	public Function create(Function input) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		scaleFactorReal = CalculusOperations.area(real);
		scaleFactorImag = CalculusOperations.area(imag);
		
		scaleFactorReal = (scaleFactorReal == 0) ? 1.0 : (1.0 / scaleFactorReal); 
		scaleFactorImag = (scaleFactorImag == 0) ? 1.0 : (1.0 / scaleFactorImag); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "normalize("+ input.getCompactDescriptor() + ")"; 
		
		double[] realOut = new double[real.length]; 
		double[] imagOut = new double[real.length]; 
		
		for( int i = 0; i < real.length; ++i ) {
			
			realOut[i] = real[i] * scaleFactorReal; 
			imagOut[i] = imag[i] * scaleFactorImag; 
		}
		
		if( input instanceof Function1D ) {
			
			return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
		} 
		
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		return FunctionFactory.createFunction2D( realOut, imagOut, zeroCentered, name, 
				x_dimension, y_dimension ); 
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
		model.setName("Scale");
		
		model.setDocPath("/operationdoc/scale.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ScaleOp.png"; 
	}

	public int getNumParams() {
		return 0;
	}

}
