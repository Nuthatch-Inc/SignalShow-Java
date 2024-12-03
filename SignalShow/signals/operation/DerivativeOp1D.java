package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class DerivativeOp1D extends UnaryOperation {

	public DerivativeOp1D() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DerivativeOp1D(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public DerivativeOp1D(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "d/dx(" + input.getCompactDescriptor() + ")"; 
	
		double[] realOut = CalculusOperations.derivative(real);
		double[] imagOut = CalculusOperations.derivative(imag);
		
		return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
	}
		


	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/DerivativeLarge.png");
//		model.setSmallIcon("/operationIcons/DerivativeSmall.png");
		model.setName("Derivative");
		
		model.setDocPath("/operationdoc/derivative.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/DerivativeOp.png"; 
	}

	
}
