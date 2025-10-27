package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.BinaryOperation;
import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;

public class ConvolveOp extends BinaryOperation {

	public ConvolveOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ConvolveOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public ConvolveOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.BINARY_OP_TIER_2);
	}
	
	@Override
	public Function create(Function inputA, Function inputB) {
		
		boolean zeroCentered = inputA.isZeroCentered();
		
		String name = inputA.getCompactDescriptor() + " * " + inputB.getCompactDescriptor();
		
		double[][] fdA, fdB; 
		
		int dimensionX = 0; 
		int dimensionY = 0; 
		
		if( inputA instanceof Function1D ) {
			
			fdA = Transforms.computeFFT1D(inputA.getReal(), inputA.getImaginary(), zeroCentered, false, Transforms.NORMALIZE_NONE); 
			fdB = Transforms.computeFFT1D(inputB.getReal(), inputB.getImaginary(), zeroCentered, false, Transforms.NORMALIZE_NONE); 
			
		} else {
			
			dimensionX = ((Function2D)inputA).getDimensionX(); 
			dimensionY = ((Function2D)inputA).getDimensionY();
			
			fdA = Transforms.computeFFT2D(inputA.getReal(), inputA.getImaginary(), zeroCentered, false, 
					dimensionX, dimensionY, Transforms.NORMALIZE_NONE); 
			fdB = Transforms.computeFFT2D(inputB.getReal(), inputB.getImaginary(), zeroCentered, false, 
					dimensionX, dimensionY, Transforms.NORMALIZE_NONE);
		}
		
		double[] realA = fdA[0]; 
		double[] imagA = fdA[1]; 
		
		double[] realB = fdB[0]; 
		double[] imagB = fdB[1]; 
		
		double[] real = new double[realA.length]; 
		double[] imag = new double[realA.length]; 
		
		for( int i = 0; i < realA.length; ++i ) {
			
			real[i] = realA[i] * realB[i] - imagA[i] * imagB[i]; 
			imag[i] = realA[i] * imagB[i] + imagA[i] * realB[i]; 
		}
		
		if( inputA instanceof Function1D ) {
			
			double[][] output = Transforms.computeFFT1D(real, imag, zeroCentered, true, Transforms.NORMALIZE_N); 
			return FunctionFactory.createFunction1D( output[0], output[1], zeroCentered, name ); 
			
		} 

		//2D
		double[][] output = Transforms.computeFFT2D( real, imag, zeroCentered, true, dimensionX, dimensionY, Transforms.NORMALIZE_N );
		return FunctionFactory.createFunction2D( output[0], output[1], zeroCentered, 
				name, dimensionX, dimensionY ); 
		
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/ConvolveLarge.png");
//		model.setSmallIcon("/operationIcons/ConvolveSmall.png");
		model.setName("Convolve");
		
		model.setDocPath("/operationdoc/convolve.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ConvolveOp.png"; 
	}

}
