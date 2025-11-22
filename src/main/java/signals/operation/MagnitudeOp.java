package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class MagnitudeOp extends UnaryOperation {

	public MagnitudeOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public MagnitudeOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public MagnitudeOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "||" + input.getCompactDescriptor() + "||"; 
	
		double[] realOut = ArrayMath.magnitude(real, imag);
		double[] imagOut = new double[real.length]; 
		
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
//		model.setLargeIcon("/operationIcons/MagnitudeLarge.png");
//		model.setSmallIcon("/operationIcons/MagnitudeSmall.png");
		model.setName("Magnitude");
		
		model.setDocPath("/operationdoc/magnitude.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/MagnitudeOp.png"; 
	}

	
}
