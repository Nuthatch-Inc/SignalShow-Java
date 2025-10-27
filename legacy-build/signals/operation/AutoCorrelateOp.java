package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class AutoCorrelateOp extends UnaryOperation {

	public AutoCorrelateOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public AutoCorrelateOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public AutoCorrelateOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function inputA ) {
		
		boolean zeroCentered = inputA.isZeroCentered();
		
		String name = "Autocorrelation {" + inputA.getCompactDescriptor() + "}" ;
		
		double[][] fdA; 
		
		int dimensionX = 0; 
		int dimensionY = 0; 
		
		if( inputA instanceof Function1D ) {
			
			fdA = Transforms.computeFFT1D(inputA.getReal(), inputA.getImaginary(), zeroCentered, false, Transforms.NORMALIZE_NONE);  
			
		} else {
			
			dimensionX = ((Function2D)inputA).getDimensionX(); 
			dimensionY = ((Function2D)inputA).getDimensionY();
			
			fdA = Transforms.computeFFT2D(inputA.getReal(), inputA.getImaginary(), zeroCentered, false, 
					dimensionX, dimensionY, Transforms.NORMALIZE_NONE); 
		}
		
		double[] realA = fdA[0]; 
		double[] imagA = fdA[1]; 
		
		double[] real = new double[realA.length]; 
		double[] imag = new double[realA.length]; 
		
		//multiplication, but take conjugate of array B
		for( int i = 0; i < realA.length; ++i ) {
			
			real[i] = realA[i] * realA[i] + imagA[i] * imagA[i]; 
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

	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/AutocorrelateLarge.png");
//		model.setSmallIcon("/operationIcons/AutocorrelateSmall.png");
		model.setName("AutoCorrelate");
		
		model.setDocPath("/operationdoc/autocorrelate.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/AutocorrelateOp.png"; 
	}

	
}
