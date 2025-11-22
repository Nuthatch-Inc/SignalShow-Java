package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class AbsoluteValueOp extends UnaryOperation {

	public AbsoluteValueOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public AbsoluteValueOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public AbsoluteValueOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "|" + input.getCompactDescriptor() + "|"; 
	
		double[] realOut = ArrayMath.abs(real);
		double[] imagOut = ArrayMath.abs(imag);
		
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
//		model.setLargeIcon("/operationIcons/AbsoluteValueLarge.png");
//		model.setSmallIcon("/operationIcons/AbsoluteValueSmall.png");
		model.setName("Absolute Value");
		
		model.setDocPath("/operationdoc/absolutevalue.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/AbsoluteValueOp.png"; 
	}

	
}
