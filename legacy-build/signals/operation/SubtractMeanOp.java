package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class SubtractMeanOp extends UnaryOperation {

	public SubtractMeanOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public SubtractMeanOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public SubtractMeanOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = input.getCompactDescriptor() + "-mean"; 
	
		double[] realOut = ArrayMath.subtractMean(real);
		double[] imagOut = ArrayMath.subtractMean(imag);
		
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
//		model.setLargeIcon("/operationIcons/SubtractMeanLarge.png");
//		model.setSmallIcon("/operationIcons/SubtractMeanSmall.png");
		model.setName("Subtract Mean");
		
		model.setDocPath("/operationdoc/subtractmean.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/SubtractMeanOp.png"; 
	}

	
}
