package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.BinaryOperation;
import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;

public class MinusOp extends BinaryOperation {

	public MinusOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MinusOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public MinusOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.BINARY_OP_TIER_1);
	}
	
	@Override
	public Function create(Function inputA, Function inputB) {
		
		double[] realA = inputA.getReal(); 
		double[] imagA = inputA.getImaginary(); 
		
		double[] realB = inputB.getReal(); 
		double[] imagB = inputB.getImaginary(); 
		
		boolean zeroCentered = inputA.isZeroCentered();
		
		String name = inputA.getCompactDescriptor() + " - " + inputB.getCompactDescriptor();
		
		double[] real = new double[realA.length]; 
		double[] imag = new double[realA.length]; 
		
		for( int i = 0; i < realA.length; ++i ) {
			
			real[i] = realA[i] - realB[i]; 
			imag[i] = imagA[i] - imagB[i]; 
		}
		
		if( inputA instanceof Function1D ) {
			
			return FunctionFactory.createFunction1D( real, imag, zeroCentered, name ); 
			
		} 
		
		int x_dimension = ((Function2D)inputA).getDimensionX();
		int y_dimension = ((Function2D)inputA).getDimensionY();

		return FunctionFactory.createFunction2D( real, imag, zeroCentered, name, 
				x_dimension, y_dimension ); 
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/SubtractLarge.png");
//		model.setSmallIcon("/operationIcons/SubtractSmall.png");
		model.setName("Subtract Minus -");
		
		model.setDocPath("/operationdoc/minus.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/SubtractOp.png"; 
	}

}
