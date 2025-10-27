package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class SampleOp1D extends UnaryOperation {
	
	int samplingPeriod; 

	public SampleOp1D() {
		super();
		samplingPeriod = 1; 
	}


	public SampleOp1D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		samplingPeriod = 1; 
	}


	public SampleOp1D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		samplingPeriod = 1; 
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "sample(" + input.getCompactDescriptor() + ")"; 
	
		double[] realOut = ArrayMath.sample(real, zeroCentered, samplingPeriod );
		double[] imagOut = ArrayMath.sample(imag, zeroCentered, samplingPeriod );
		
		return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
	}

	public int getSamplingPeriod() {
		return samplingPeriod;
	}


	public void setSamplingPeriod(int samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
	}


	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/ReverseLarge.png");
//		model.setSmallIcon("/operationIcons/ReverseSmall.png");
		model.setName("Reverse");
		
		model.setDocPath("/operationdoc/reverse.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ReverseOp.png"; 
	}

	
}
