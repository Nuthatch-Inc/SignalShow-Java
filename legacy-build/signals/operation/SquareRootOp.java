package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class SquareRootOp extends UnaryOperation {

	public SquareRootOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public SquareRootOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public SquareRootOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "\u221A(" + input.getCompactDescriptor() + ")"; 
	
		double[] realOut = ArrayMath.sqrt(real);
		double[] imagOut = ArrayMath.sqrt(imag);
		
		if( input instanceof Function1D ) {
			
			return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
		} 
		
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		return FunctionFactory.createFunction2D( realOut, imagOut, zeroCentered, name, 
				x_dimension, y_dimension ); 
	}
		


	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/SquareRootLarge.png");
//		model.setSmallIcon("/operationIcons/SquareRootSmall.png");
		model.setName("Square Root");
		
		model.setDocPath("/operationdoc/sqrt.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/SquareRootOp.png"; 
	}

	
}
