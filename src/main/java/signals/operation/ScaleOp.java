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
import signals.gui.operation.OperationOptionsPanel;
import signals.gui.operation.ParameterOptionsPanel;

public class ScaleOp extends UnaryOperation implements ParametricOperation {
	
	public ScaleOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ScaleOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	double scaleFactorReal, scaleFactorImag; 

	public ScaleOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		scaleFactorReal = 1.0;
		scaleFactorImag = 0.0; 
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
		
		String name = "(" + format.format(scaleFactorReal) + "+ i"+ format.format(scaleFactorImag) 
							+ ")* "+ input.getCompactDescriptor(); 
		
		double[] realOut = new double[real.length]; 
		double[] imagOut = new double[real.length]; 
		
		for( int i = 0; i < real.length; ++i ) {
			
			realOut[i] = real[i]*scaleFactorReal - imag[i]*scaleFactorImag;
			imagOut[i] = imag[i]*scaleFactorReal + real[i]*scaleFactorImag; 
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
//		model.setLargeIcon("/operationIcons/ScaleLarge.png");
//		model.setSmallIcon("/operationIcons/ScaleSmall.png");
		model.setName("Scale");
		
		model.setDocPath("/operationdoc/scale.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ScaleOp.png"; 
	}

	public int getNumParams() {
		return 2;
	}

	public double getValue(int index) {
			
		if( index == 0 ) return scaleFactorReal; 
		return scaleFactorImag; 
	}

	public void setValue(int index, double value) {
		
		if( index == 0 ) scaleFactorReal = value; 
		else scaleFactorImag = value; 
		
	}

	public String getParamDescriptor() {
		return "complex scale factor (a+ib)";
	}

	public String getParamName( int index ) {
	
		if( index == 0 ) return "a"; 
		return "b";
	}

}
