package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class RealOp extends UnaryOperation {

	public RealOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public RealOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public RealOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] realOut = input.getReal(); 
		double[] imagOut = new double[realOut.length]; 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "Re{" + input.getCompactDescriptor() + "}"; 
	
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
//		model.setLargeIcon("/operationIcons/SquaredMagnitudeLarge.png");
//		model.setSmallIcon("/operationIcons/SquaredMagnitudeSmall.png");
		model.setName("Phase");
		
		model.setDocPath("/operationdoc/real.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/RealOp.png"; 
	}

	
}
