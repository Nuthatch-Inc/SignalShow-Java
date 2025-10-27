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

public class ThresholdOp extends UnaryOperation implements ParametricOperation {
	
	public ThresholdOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThresholdOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	double minVal, maxVal, threshold; 

	public ThresholdOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		minVal = 0.0;
		maxVal = 1.0; 
		threshold = 0.5; 
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
		
		String name = "threshold("+ input.getCompactDescriptor() + ")"; 
		
		double[] realOut = ArrayMath.threshold(real, threshold, maxVal, minVal); 
		double[] imagOut = ArrayMath.threshold(imag, threshold, maxVal, minVal); 
	
		
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
//		model.setLargeIcon("/operationIcons/ThresholdLarge.png");
//		model.setSmallIcon("/operationIcons/ThresholdSmall.png");
		model.setName("Threshold");
		
		model.setDocPath("/operationdoc/threshold.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ThresholdOp.png"; 
	}

	public int getNumParams() {
		return 3;
	}

	public double getValue(int index) {
			
		if( index == 0 ) return minVal; 
		else if( index == 1 ) return maxVal; 
		return threshold; 
	}

	public void setValue(int index, double value) {
		
		if( index == 0 ) minVal = value; 
		else if( index == 1 ) maxVal = value;
		else threshold = value; 
		
	}

	public String getParamDescriptor() {
		return "Below Threshold: Minimum. \nEqual to or Above Threshold: Maximum";
	}

	public String getParamName( int index ) {
	
		if( index == 0 ) return "Maximum"; 
		else if( index == 1 ) return "Minimum"; 
		return "Threshold";
	}

}
