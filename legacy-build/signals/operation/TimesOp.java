package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.BinaryOperation;
import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;

public class TimesOp extends BinaryOperation {

	public TimesOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TimesOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public TimesOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.BINARY_OP_TIER_2);
	}
	
	@Override
	public Function create(Function inputA, Function inputB) {
		
		double[] realA = inputA.getReal(); 
		double[] imagA = inputA.getImaginary(); 
		
		double[] realB = inputB.getReal(); 
		double[] imagB = inputB.getImaginary(); 
		
		boolean zeroCentered = inputA.isZeroCentered();
		
		String name = inputA.getCompactDescriptor() + " x " + inputB.getCompactDescriptor();
		
		double[] real = new double[realA.length]; 
		double[] imag = new double[realA.length]; 
		
		for( int i = 0; i < realA.length; ++i ) {
			
			real[i] = realA[i] * realB[i] - imagA[i] * imagB[i]; 
			imag[i] = realA[i] * imagB[i] + imagA[i] * realB[i]; 
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
//		model.setLargeIcon("/operationIcons/TimesLarge.png");
//		model.setSmallIcon("/operationIcons/TimesSmall.png");
		model.setName("Times Multiply");
		
		model.setDocPath("/operationdoc/times.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/TimesOp.png"; 
	}

}
