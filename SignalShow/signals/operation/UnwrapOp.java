package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class UnwrapOp extends UnaryOperation {

	public UnwrapOp() {
		super();
		// TODO Auto-generated constructor stub
	}


	public UnwrapOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}


	public UnwrapOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}
	
	
	public Function create( Function input ) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "Unwrap{" + input.getCompactDescriptor() + "}";  
		
		if( input instanceof Function1D ) {
			
			int startingIndex = ( zeroCentered ? real.length/2 : 0 );
			double[] realOut = PhaseUnwrapper1D.unwrapItoh(real, startingIndex);
			double[] imagOut = PhaseUnwrapper1D.unwrapItoh(imag, startingIndex);
			return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
		} 
		
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		PhaseUnwrapper2D unwrapper = new PhaseUnwrapper2D( real, x_dimension, y_dimension );
		double[] realOut = unwrapper.unwrap(); 
		
		unwrapper = new PhaseUnwrapper2D( imag, x_dimension, y_dimension );
		double[] imagOut = unwrapper.unwrap(); 
		
		return FunctionFactory.createFunction2D( realOut, imagOut, zeroCentered, name, 
				x_dimension, y_dimension ); 
	}
		


	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/UnwrapLarge.png");
//		model.setSmallIcon("/operationIcons/UnwrapSmall.png");
		model.setName("Unwrap");
		
		model.setDocPath("/operationdoc/unwrap.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/UnwrapOp.png"; 
	}

	
}
