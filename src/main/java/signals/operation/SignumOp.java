package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class SignumOp extends UnaryOperation {

	public SignumOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SignumOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public SignumOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}

	@Override
	public Function create(Function input) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		String name = "SGN{" + input.getCompactDescriptor() + "}"; 
		
		double[] realOut = new double[real.length]; 
		double[] imagOut = new double[real.length]; 
		
		for( int i = 0; i < real.length; ++i ) {
			
			if( real[i] > 0 ) {
				
				realOut[i] = 1; 
				
			} else if ( real[i] < 0 ) {
				
				realOut[i] = -1; 
			}
			
			if( imag[i] > 0 ) {
				
				imagOut[i] = 1; 
				
			} else if ( imag[i] < 0 ) {
				
				imagOut[i] = -1; 
			}
		}
		
		if( input instanceof Function1D ) {
			
			return FunctionFactory.createFunction1D( realOut, imagOut, zeroCentered, name ); 
			
		} 
		
		int x_dimension = ((Function2D)input).getDimensionX();
		int y_dimension = ((Function2D)input).getDimensionY();

		return FunctionFactory.createFunction2D( realOut, imagOut, zeroCentered, name, 
				x_dimension, y_dimension ); 
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
		
		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/SignumLarge.png");
//		model.setSmallIcon("/operationIcons/SignumSmall.png");
		model.setName("Signum");
		
		model.setDocPath("/operationdoc/sgn.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/SignumOp.png"; 
	}

}
