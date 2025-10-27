package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.BinaryOperation;
import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;

public class DivideOp extends BinaryOperation {

	public DivideOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DivideOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public DivideOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.BINARY_OP_TIER_1);
	}
	
	@Override
	public Function create(Function inputA, Function inputB) {
		
		double[] realA = inputA.getReal(); 
		double[] imagA = inputA.getImaginary(); 
		
		double[] realB = inputB.getReal(); 
		double[] imagB = inputB.getImaginary(); 
		
		boolean zeroCentered = inputA.isZeroCentered();
		
		String name = inputA.getCompactDescriptor() + " / " + inputB.getCompactDescriptor();
		
		double[][] output = ArrayMath.divide(realA, imagA, realB, imagB);
		
		double[] real = output[0];
		double[] imag = output[1]; 

		
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
//		model.setLargeIcon("/operationIcons/DivideLarge.png");
//		model.setSmallIcon("/operationIcons/DivideSmall.png");
		model.setName("Divide /");
		
		model.setDocPath("/operationdoc/divide.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/DivideOp.png"; 
	
	}

}
