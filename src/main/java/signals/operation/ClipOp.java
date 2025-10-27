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

public class ClipOp extends UnaryOperation implements ParametricOperation {
	
	public ClipOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClipOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	double minVal, maxVal; 

	public ClipOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		minVal = 0.0;
		maxVal = 1.0; 
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
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "clip("+ input.getCompactDescriptor() + ")"; 
		
		double[] realOut = ArrayMath.clip(real, maxVal, minVal); 
		double[] imagOut = ArrayMath.clip(imag, maxVal, minVal); 
	
		
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
//		model.setLargeIcon("/operationIcons/ClipLarge.png");
//		model.setSmallIcon("/operationIcons/ClipSmall.png");
		model.setName("Clip");
		
		model.setDocPath("/operationdoc/clip.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ClipOp.png"; 
	}

	public int getNumParams() {
		return 2;
	}

	public double getValue(int index) {
			
		if( index == 0 ) return minVal; 
		return maxVal; 
	}

	public void setValue(int index, double value) {
		
		if( index == 0 ) minVal = value; 
		else maxVal = value; 
		
	}

	public String getParamDescriptor() {
		return "Clip between maximum and minimum";
	}

	public String getParamName( int index ) {
	
		if( index == 0 ) return "Minimum"; 
		return "Maximum";
	}

}
