package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class IntegralOp1D extends UnaryOperation {

	public IntegralOp1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IntegralOp1D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public IntegralOp1D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "Integral(" + input.getCompactDescriptor() + ")"; 
	
		double[] realOut = CalculusOperations.integral(real);
		double[] imagOut = CalculusOperations.integral(imag);
		
		return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
	}

	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/IntegralLarge.png");
//		model.setSmallIcon("/operationIcons/IntegralSmall.png");
		model.setName("Integral");
		
		model.setDocPath("/operationdoc/integral.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/IntegralOp.png"; 
	}

	
}
