package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class ReverseOp1D extends UnaryOperation {

	public ReverseOp1D() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ReverseOp1D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public ReverseOp1D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "reverse(" + input.getCompactDescriptor() + ")"; 
	
		double[] realOut = ArrayMath.reverse(real);
		double[] imagOut = ArrayMath.reverse(imag);
		
		return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
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
