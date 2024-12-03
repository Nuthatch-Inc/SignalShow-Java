package signals.operation;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;

import signals.core.CombineOpsRule;
import signals.core.Core;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.ParameterOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class OffsetOp extends UnaryOperation implements ParametricOperation {
	
	public OffsetOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OffsetOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	double offsetReal, offsetImag; 

	public OffsetOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		offsetReal = 1.0;
		offsetImag = 0.0; 
	}

	/* (non-Javadoc)
	 * @see signals.core.Operation#getOptionsInterface()
	 */
	@Override
	public OperationOptionsPanel getOptionsInterface() {
		
		return new ParameterOptionsPanel( this );
	}
	
	
	@Override
	public Function create(Function input) {
		
		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		
		boolean zeroCentered = input.isZeroCentered();
		
		NumberFormat format = Core.getDisplayOptions().getFormat(); 
		
		String name = "(" + format.format(offsetReal) + "+ i"+ format.format(offsetImag) 
							+ ") + "+ input.getCompactDescriptor(); 
		
		double[] realOut = new double[real.length]; 
		double[] imagOut = new double[real.length]; 
		
		for( int i = 0; i < real.length; ++i ) {
			
			realOut[i] = real[i] + offsetReal;
			imagOut[i] = imag[i] + offsetImag; 
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
//		model.setLargeIcon("/operationIcons/OffsetLarge.png");
//		model.setSmallIcon("/operationIcons/OffsetSmall.png");
		model.setName("Offset");
		
		model.setDocPath("/operationdoc/offset.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/OffsetOp.png"; 
	}

	public void parameterChanged(int index, String newVal) {
		
		if( index == 0 ) {
			offsetReal = Double.parseDouble(newVal); 
		} else {
			offsetImag = Double.parseDouble(newVal); 
		}
	}
	
	public int getNumParams() {
		return 2;
	}

	public double getValue(int index) {
			
		if( index == 0 ) return offsetReal; 
		return offsetImag; 
	}

	public void setValue(int index, double value) {
		
		if( index == 0 ) offsetReal = value; 
		else offsetImag = value; 
		
	}

	public String getParamDescriptor() {
		return "complex offset (a+ib)";
	}

	public String getParamName( int index ) {
	
		if( index == 0 ) return "a"; 
		return "b";
	}

}
