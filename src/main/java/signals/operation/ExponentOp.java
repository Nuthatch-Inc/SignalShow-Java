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

public class ExponentOp extends UnaryOperation implements ParametricOperation {
	
	public ExponentOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExponentOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	double exponentReal, exponentImag; 

	public ExponentOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		exponentReal = 1.0;
		exponentImag = 0.0; 
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
		
		String name = input.getCompactDescriptor() + "^(" + format.format(exponentReal) + "+ i"+ format.format(exponentImag) 
							+ ")"; 
		
		double[][] output = ArrayMath.complexPower(real, imag, exponentReal, exponentImag );
		double[] realOut = output[0]; 
		double[] imagOut = output[1];
		
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
//		model.setLargeIcon("/operationIcons/RaiseToPowerLarge.png");
//		model.setSmallIcon("/operationIcons/RaiseToPowerSmall.png");
		model.setName("exponent");
		
		model.setDocPath("/operationdoc/exponent.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/RaiseToPowerOp.png"; 
	}

	public void parameterChanged(int index, String newVal) {
		
		if( index == 0 ) {
			exponentReal = Double.parseDouble(newVal); 
		} else {
			exponentImag = Double.parseDouble(newVal); 
		}
	}

	public int getNumParams() {
		return 2;
	}

	public double getValue(int index) {
			
		if( index == 0 ) return exponentReal; 
		return exponentImag; 
	}

	public void setValue(int index, double value) {
		
		if( index == 0 ) exponentReal = value; 
		else exponentImag = value; 
		
	}

	public String getParamDescriptor() {
		return "complex exponent (a+ib)";
	}

	public String getParamName( int index ) {
	
		if( index == 0 ) return "a"; 
		return "b";
	}
}
